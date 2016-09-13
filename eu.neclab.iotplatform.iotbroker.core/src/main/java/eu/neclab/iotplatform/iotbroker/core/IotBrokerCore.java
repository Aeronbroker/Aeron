/*******************************************************************************
 * Copyright (c) 2015, NEC Europe Ltd.
 * All rights reserved.
 * 
 * Authors:
 *          * Salvatore Longo - salvatore.longo@neclab.eu
 *          * Tobias Jacobs - tobias.jacobs@neclab.eu
 *          * Flavio Cirillo - flavio.cirillo@neclab.eu
 *          * Raihan Ul-Islam
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions 
 * are met:
 * 1. Redistributions of source code must retain the above copyright 
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above 
 * copyright notice, this list of conditions and the following disclaimer 
 * in the documentation and/or other materials provided with the 
 * distribution.
 * 3. All advertising materials mentioning features or use of this 
 * software must display the following acknowledgment: This 
 * product includes software developed by NEC Europe Ltd.
 * 4. Neither the name of NEC nor the names of its contributors may 
 * be used to endorse or promote products derived from this 
 * software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY NEC ''AS IS'' AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY 
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN 
 * NO EVENT SHALL NEC BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT 
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED 
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH 
 * DAMAGE.
 ******************************************************************************/
package eu.neclab.iotplatform.iotbroker.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import eu.neclab.iotplatform.iotbroker.association.AssociationsHandler;
import eu.neclab.iotplatform.iotbroker.commons.BundleUtils;
import eu.neclab.iotplatform.iotbroker.commons.Pair;
import eu.neclab.iotplatform.iotbroker.commons.SubscriptionWithInfo;
import eu.neclab.iotplatform.iotbroker.commons.TraceKeeper;
import eu.neclab.iotplatform.iotbroker.commons.XmlFactory;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.BigDataRepository;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.IoTAgentInterface;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.IoTAgentWrapperInterface;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.NgsiHierarchyInterface;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.OnChangeHandlerInterface;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.OnTimeIntervalHandlerInterface;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.OnValueHandlerInterface;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.QueryService;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.ResultFilterInterface;
import eu.neclab.iotplatform.iotbroker.core.subscription.ConfManWrapper;
import eu.neclab.iotplatform.iotbroker.core.subscription.NorthBoundWrapper;
import eu.neclab.iotplatform.iotbroker.core.subscription.SubscriptionController;
import eu.neclab.iotplatform.iotbroker.storage.AvailabilitySubscriptionInterface;
import eu.neclab.iotplatform.iotbroker.storage.LinkSubscriptionAvailabilityInterface;
import eu.neclab.iotplatform.iotbroker.storage.SubscriptionStorageInterface;
import eu.neclab.iotplatform.ngsi.api.datamodel.Code;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElement;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElementResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.ReasonPhrase;
import eu.neclab.iotplatform.ngsi.api.datamodel.RegisterContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.RegisterContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.Restriction;
import eu.neclab.iotplatform.ngsi.api.datamodel.StatusCode;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateActionType;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextAvailabilitySubscriptionRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextAvailabilitySubscriptionResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextSubscriptionRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextSubscriptionResponse;
import eu.neclab.iotplatform.ngsi.api.ngsi10.Ngsi10Interface;
import eu.neclab.iotplatform.ngsi.api.ngsi10.Ngsi10Requester;
import eu.neclab.iotplatform.ngsi.api.ngsi9.Ngsi9Interface;
import eu.neclab.iotplatform.ngsi.association.datamodel.AssociationDS;

/**
 * This class represents the functional core of the IoT Broker. Each IoT Broker
 * deployment uses exactly one instance of this class.
 */
public class IotBrokerCore implements Ngsi10Interface, Ngsi9Interface {

	/**
	 * List of query services to pass queries to
	 */
	private List<QueryService> queryServiceList;

	public List<QueryService> getQueryServiceList() {
		return queryServiceList;
	}

	public void setQueryServiceList(List<QueryService> queryServiceList) {
		this.queryServiceList = queryServiceList;
	}

	/** The URL of the pub/sub broker */
	@Value("${dumbInBigDataRepo:false}")
	private boolean dumbInBigDataRepo;

	/** The URL of the pub/sub broker */
	@Value("${pub_sub_addr:null}")
	private String pubSubUrl;

	private List<String> pubSubUrlList = null;

	/**
	 * This flag enables the keeping of trace in order to avoid loop in presence
	 * of chain of IoT platform
	 */
	@Value("${traceKeeper_enabled:false}")
	private boolean traceKeeperEnabled;

	@Value("${ignorePubSubFailure:false}")
	private boolean ignorePubSubFailure;

	/**
	 * This flag enables storage of all the QueryResponses and NotifyContext
	 * into the big data repository
	 */
	@Value("${storeQueryResponseAndNotifications:false}")
	private boolean storeQueryResponseAndNotifications;

	// private final String CONFMAN_REG_URL = System.getProperty("confman.ip");

	/** Executor for asynchronous tasks */
	private final ExecutorService taskExecutor = Executors
			.newCachedThreadPool();

	/** The implementation of the NGSI 9 interface */
	private Ngsi9Interface ngsi9Impl;

	/** Used to make NGSI 10 requests. */
	private Ngsi10Requester ngsi10Requester;

	/** The logger. */
	private static Logger logger = Logger.getLogger(IotBrokerCore.class);

	/** Utility for processing NGSI associations */
	// private AssociationsHandler associationsHandler = new
	// AssociationsHandler();

	@Value("${associationsEnabled:false}")
	private boolean associationsEnabled;

	/** Interface for making context availability subscriptions. */
	private AvailabilitySubscriptionInterface availabilitySub;

	/** Interface for assembling availability subscriptions. */
	private LinkSubscriptionAvailabilityInterface linkAvSub;

	/** Interface for storing context subscriptions. */
	private SubscriptionStorageInterface subscriptionStorage;

	/** Wrapper for the north-bound interface. */
	private NorthBoundWrapper northBoundWrapper;

	/** Wrapper for the Configuration Management GE. */
	private ConfManWrapper confManWrapper;

	/**
	 * Wrapper for IoT agents, that is, components offering data via an NGSI-10
	 * interface.
	 */
	private IoTAgentWrapperInterface agentWrapper;

	/** The subscription controller. */
	private SubscriptionController subscriptionController;

	private ResultFilterInterface resultFilter;

	/**
	 * A pointer to the embedded agent.
	 */
	private IoTAgentInterface embeddedIoTAgent;

	/**
	 * A pointer to a Big Data repository. (This functionality is currently
	 * disabled.)
	 */
	private BigDataRepository bigDataRepository;

	/**
	 * ONVALUE notifyCondition handler
	 */
	private OnValueHandlerInterface onValueHandler;

	/**
	 * ONCHANGE notifyCondition handler
	 */
	private OnChangeHandlerInterface onChangeHandler;

	/**
	 * ONTIMEINTERVAL notifyCondition handler
	 */
	private OnTimeIntervalHandlerInterface onTimeIntervalHandler;

	/**
	 * Interface for hierarchies of IoT Broker instances; extra bundle not
	 * included in this release needed.
	 */
	private NgsiHierarchyInterface ngsiHierarchyExtension;

	public OnValueHandlerInterface getOnValueHandler() {
		return onValueHandler;
	}

	public void setOnValueHandler(OnValueHandlerInterface onValueHandler) {
		this.onValueHandler = onValueHandler;
	}

	public OnChangeHandlerInterface getOnChangeHandler() {
		return onChangeHandler;
	}

	public void setOnChangeHandler(OnChangeHandlerInterface onChangeHandler) {
		this.onChangeHandler = onChangeHandler;
	}

	public OnTimeIntervalHandlerInterface getOnTimeIntervalHandler() {
		return onTimeIntervalHandler;
	}

	public void setOnTimeIntervalHandler(
			OnTimeIntervalHandlerInterface onTimeIntervalHandler) {
		this.onTimeIntervalHandler = onTimeIntervalHandler;
	}

	public BigDataRepository getBigDataRepository() {
		return bigDataRepository;
	}

	public void setBigDataRepository(BigDataRepository bigDataRepository) {
		this.bigDataRepository = bigDataRepository;
	}

	public NgsiHierarchyInterface getNgsiHierarchyExtension() {
		return ngsiHierarchyExtension;
	}

	public void setNgsiHierarchyExtension(
			NgsiHierarchyInterface ngsiHierarchyExtension) {
		this.ngsiHierarchyExtension = ngsiHierarchyExtension;
	}

	public IoTAgentInterface getEmbeddedIoTAgent() {
		return embeddedIoTAgent;
	}

	public void setEmbeddedIoTAgent(IoTAgentInterface embeddedIoTAgent) {
		this.embeddedIoTAgent = embeddedIoTAgent;
	}

	/**
	 * @return A pointer to the result filter this RequestThread instance uses.
	 *         Note that the result filter is retrieved via the OSGi framework.
	 */
	public ResultFilterInterface getResultFilter() {
		return resultFilter;
	}

	/**
	 * Instructs the object to get load the result filter from the given result
	 * filter interface.
	 * 
	 * @param resultFilter
	 *            The result filter interface.
	 */
	public void setResultFilter(ResultFilterInterface resultFilter) {
		this.resultFilter = resultFilter;
	}

	/**
	 * Returns the north bound wrapper.
	 * 
	 * @return the north bound wrapper
	 */
	public NorthBoundWrapper getNorthBoundWrapper() {
		return northBoundWrapper;
	}

	/**
	 * Sets the north bound wrapper.
	 * 
	 * @param northBoundWrapper
	 *            the new north bound wrapper
	 */
	public void setNorthBoundWrapper(NorthBoundWrapper northBoundWrapper) {
		this.northBoundWrapper = northBoundWrapper;
	}

	/**
	 * Returns the conf man wrapper.
	 * 
	 * @return the conf man wrapper
	 */
	public ConfManWrapper getConfManWrapper() {
		return confManWrapper;
	}

	/**
	 * Sets the conf man wrapper.
	 * 
	 * @param confManWrapper
	 *            the new conf man wrapper
	 */
	public void setConfManWrapper(ConfManWrapper confManWrapper) {
		this.confManWrapper = confManWrapper;
	}

	/**
	 * Returns the agent wrapper.
	 * 
	 * @return the agent wrapper
	 */
	public IoTAgentWrapperInterface getAgentWrapper() {
		return agentWrapper;
	}

	/**
	 * Sets the agent wrapper.
	 * 
	 * @param agentWrapper
	 *            the new agent wrapper
	 */
	public void setAgentWrapper(IoTAgentWrapperInterface agentWrapper) {
		this.agentWrapper = agentWrapper;
	}

	/**
	 * Returns the subscription controller.
	 * 
	 * @return the subscription controller
	 */
	public SubscriptionController getSubscriptionController() {
		return subscriptionController;
	}

	/**
	 * Sets the subscription controller.
	 * 
	 * @param subscriptionController
	 *            the new subscription controller
	 */
	public void setSubscriptionController(
			SubscriptionController subscriptionController) {
		this.subscriptionController = subscriptionController;
	}

	/**
	 * Returns the availability subscription interface, which is used for making
	 * context availability subscriptions.
	 * 
	 * @return the availability subscription interface
	 */
	public AvailabilitySubscriptionInterface getAvailabilitySub() {
		return availabilitySub;
	}

	/**
	 * Sets the availability subscription interface, which is used for making
	 * context availability subscriptions.
	 * 
	 * @param availabilitySub
	 *            the new availability sub
	 */
	public void setAvailabilitySub(
			AvailabilitySubscriptionInterface availabilitySub) {
		this.availabilitySub = availabilitySub;
	}

	/**
	 * Returns the interface for maintaining links between incoming
	 * subscriptions and outgoing availability subscriptions.
	 * 
	 * @return the link availability subscription interface
	 */
	public LinkSubscriptionAvailabilityInterface getLinkAvSub() {
		return linkAvSub;
	}

	/**
	 * Sets the interface for maintaining links between incoming subscriptions
	 * and outgoing availability subscriptions.
	 * 
	 * @param linkAvSub
	 *            The link availability subscription interface.
	 */
	public void setLinkAvSub(LinkSubscriptionAvailabilityInterface linkAvSub) {
		this.linkAvSub = linkAvSub;
	}

	/**
	 * Returns the subscription interface, which is used for receiving and
	 * processing NGSI10 subscriptions.
	 * 
	 * @return the subscription storage
	 */
	public SubscriptionStorageInterface getSubscriptionStorage() {
		return subscriptionStorage;
	}

	/**
	 * Sets the subscription interface, which is used for receiving and
	 * processing NGSI10 subscriptions.
	 * 
	 * @param subscriptionStorage
	 *            the subscription storage
	 */
	public void setSubscriptionStorage(
			SubscriptionStorageInterface subscriptionStorage) {
		this.subscriptionStorage = subscriptionStorage;
	}

	/**
	 * Returns the implementation of the NGSI 9 interface. This interface is
	 * used by the core for making NGSI-9 discovery operations.
	 * 
	 * @return The NGSI 9 interface.
	 */
	public Ngsi9Interface getNgsi9Impl() {
		return ngsi9Impl;
	}

	/**
	 * Sets the implementation of the NGSI 9 interface. This interface is used
	 * by the core for making NGSI-9 discovery operations.
	 * 
	 * @param ngsi9
	 *            The NGSI 9 interface.
	 */
	public void setNgsi9Impl(Ngsi9Interface ngsi9) {
		ngsi9Impl = ngsi9;
	}

	/**
	 * Returns the ngsi10 requester. This object is used for making NGSI-10
	 * requests to arbitrary URLs.
	 * 
	 * @return the ngsi10 requester.
	 */
	public Ngsi10Requester getNgsi10Requester() {

		return ngsi10Requester;
	}

	/**
	 * Sets the ngsi10 requester. This object is used for making NGSI-10
	 * requests to arbitrary URLs.
	 * 
	 * @param ngsi10Requester
	 *            The new ngsi10 requester.
	 */
	public void setNgsi10Requestor(Ngsi10Requester ngsi10Requester) {

		this.ngsi10Requester = ngsi10Requester;
	}

	/**
	 * Instantiates a new IoT Broker core instance.
	 */
	public IotBrokerCore() {

		System.setProperty("java.awt.headless", "true");

	}

	public boolean isAssociationsEnabled() {
		return associationsEnabled;
	}

	public void enableAssociations(boolean associationsEnabled) {
		this.associationsEnabled = associationsEnabled;
	}

	@PostConstruct
	public void postConstruct() {
		if (pubSubUrl != null && pubSubUrl.contains(",")) {
			pubSubUrlList = getListOfUpdateAdress();
		}
		;
	}

	/**
	 * This method is intended for working with several pub-sub brokers; but as
	 * the feature is not yet enabled the method is currently unused.
	 */
	private List<String> getListOfUpdateAdress() {

		List<String> listUrl = new ArrayList<String>();

		Iterable<String> iterable = Splitter.on(",").omitEmptyStrings()
				.trimResults().split(pubSubUrl);

		Iterator<String> iter = iterable.iterator();

		while (iter.hasNext()) {

			listUrl.add(iter.next());

		}

		return listUrl;

	}

	/**
	 * 
	 * This operation realizes synchronous retrieval of Context Information via
	 * the QueryContext method defined by NGSI 10. When this method is called,
	 * the IoT Broker Core tries to retrieve the requested information and
	 * returns whatever could be retrieved.
	 * 
	 * @param request
	 *            The QueryContextRequest.
	 * @return The QueryContextResponse.
	 */
	@Override
	public QueryContextResponse queryContext(QueryContextRequest request) {

		// /*
		// *
		// ######################################################################
		// * Here starts part of code for the ASSOCIATIONS
		// * ##############################################
		// */
		//
		// /*
		// * create associations operation scope for discovery
		// */
		// OperationScope operationScope = new OperationScope(
		// "IncludeAssociations", "SOURCES");
		//
		// /*
		// * Create a new restriction with the same attribute expression and
		// * operation scope as in the request.
		// */
		// Restriction restriction = new Restriction();
		//
		// if (request.getRestriction() != null) {
		// if (request.getRestriction().getAttributeExpression() != null) {
		// restriction.setAttributeExpression(request.getRestriction()
		// .getAttributeExpression());
		// }
		// if (request.getRestriction().getOperationScope() != null) {
		// restriction.setOperationScope(new ArrayList<OperationScope>(
		// request.getRestriction().getOperationScope()));
		// }
		//
		// } else {
		//
		// restriction.setAttributeExpression("");
		//
		// }
		//
		// /*
		// * Add the associations operation scope to the the restriction.
		// */
		//
		// ArrayList<OperationScope> lstOperationScopes = null;
		//
		// if (restriction.getOperationScope() == null) {
		// lstOperationScopes = new ArrayList<OperationScope>();
		// lstOperationScopes.add(operationScope);
		// restriction.setOperationScope(lstOperationScopes);
		// } else {
		// restriction.getOperationScope().add(operationScope);
		// }
		//
		// /*
		// *
		// ######################################################################
		// * Here finishes part of code for the ASSOCIATIONS
		// * ################################################
		// */

		DiscoverContextAvailabilityRequest discoveryRequest = new DiscoverContextAvailabilityRequest(
				request.getEntityIdList(), request.getAttributeList(),
				request.getRestriction());

		if (associationsEnabled == true) {
			AssociationsHandler.insertAssociationScope(discoveryRequest);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("DiscoverContextAvailabilityRequest:"
					+ discoveryRequest.toString());
		}

		/* Get the NGSI 9 DiscoverContextAvailabilityResponse */
		DiscoverContextAvailabilityResponse discoveryResponse = ngsi9Impl
				.discoverContextAvailability(discoveryRequest);

		/*
		 * The following code lines are used to filter out certain metadata in
		 * case the NGSI hierarchy extension is used.
		 */
		if (BundleUtils.isServiceRegistered(this, ngsiHierarchyExtension)) {
			ngsiHierarchyExtension
					.filterOutSelfSubordination(discoveryResponse);
		}
		/* till here */

		/*
		 * Now as we have done the discovery of data sources, we go and exploit
		 * these data sources for answering the query.
		 */

		/*
		 * We first initialize the merger for merging the query results later
		 */
		QueryResponseMerger merger = new QueryResponseMerger(request);

		/*
		 * Then we initialize a task list that will contain the data retrieval
		 * tasks. The reason we use such a task list is that we want to execute
		 * the data retrieval tasks all in parallel.
		 */
		List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();

		/*
		 * Now check if the discovery response is useful; if yes, then we
		 * populate the query task list with query tasks accordingly.
		 */
		if ((discoveryResponse.getErrorCode() == null || discoveryResponse
				.getErrorCode().getCode() == 200)
				&& discoveryResponse.getContextRegistrationResponse() != null) {

			List<ContextRegistrationResponse> contextRegistrationToQuery = new ArrayList<ContextRegistrationResponse>();
			contextRegistrationToQuery.addAll(discoveryResponse
					.getContextRegistrationResponse());

			// Look for associations
			List<AssociationDS> transitiveList = null;
			if (associationsEnabled == true) {
				transitiveList = AssociationsHandler.getTransitiveList(
						discoveryResponse, request);

				contextRegistrationToQuery.addAll(AssociationsHandler
						.getAssociatedContextRegistrations(transitiveList));
			}

			// Create the query list of IoT Providers
			List<Pair<QueryContextRequest, URI>> queryList = createQueryRequestList(
					contextRegistrationToQuery, request);

			// Query the IoT Provider
			int count = 0;
			for (Pair<QueryContextRequest, URI> pair : queryList) {

				if (logger.isDebugEnabled()) {
					logger.debug(String.format("Query number %d to %s: %s",
							count, pair.getLeft(), pair.getRight()));
					count++;

				}

				tasks.add(Executors.callable(new RequestThread(ngsi10Requester,
						pair.getLeft(), pair.getRight(), merger, transitiveList)));

			}

		}

		/*
		 * Now we query also the Embedded IoT Agent
		 */
		try {
			logger.info("Checking if Historical Agent is present");
			if (BundleUtils.isServiceRegistered(this, embeddedIoTAgent)) {

				logger.info("Historical Agent present: fowarding the query");
				tasks.add(Executors.callable(new EmbeddedIoTAgentRequestThread(
						embeddedIoTAgent, request, merger)));
			}
		} catch (org.springframework.osgi.service.ServiceUnavailableException e) {
			logger.warn("Not possible to query the Big Data Repository: osgi service not registered");
		}

		/*
		 * In addition to the regular tasks of trying to get information from
		 * NGSI10 providers, we create for each 'query service' a task to obtain
		 * information from it.
		 * 
		 * Query services are additional service, obtained from OSGi bundles,
		 * that can make queries for entities. Examples are: retrieving data
		 * from a database, resolving association, etc.
		 */

		// this has only to be executed once...
		if (queryServiceList == null)
			queryServiceList = new ArrayList<QueryService>();

		logger.info(queryServiceList.size() + " query Services found");

		/*
		 * Definition of Thread calling the query services and adding the query
		 * result to the merger. Implementation should be straightforward to
		 * understand.
		 */
		class queryServiceCaller implements Runnable {

			QueryService qS;
			QueryContextRequest qCR;
			List<ContextRegistration> regList;
			QueryResponseMerger merger;

			queryServiceCaller(QueryService qS, QueryContextRequest qCR,
					List<ContextRegistration> regList,
					QueryResponseMerger merger) {
				this.qS = qS;
				this.qCR = qCR;
				this.regList = regList;
				this.merger = merger;
			}

			@Override
			public void run() {
				// query the query service
				QueryContextResponse qCResp = qS.queryContext(qCR, regList);

				// add result to the merger
				synchronized (merger) {
					merger.put(qCResp);
				}
			}

		}

		/*
		 * We extract the list of context registrations from the discovery
		 * response
		 */
		List<ContextRegistration> registrationList = new ArrayList<ContextRegistration>(
				discoveryResponse.getContextRegistrationResponse().size());
		if (discoveryResponse.getContextRegistrationResponse() != null) {
			for (ContextRegistrationResponse cRR : discoveryResponse
					.getContextRegistrationResponse()) {
				registrationList.add(cRR.getContextRegistration());
			}
		}

		/*
		 * Initialize the tasks for calling the query service
		 */
		for (QueryService qS : queryServiceList) {
			tasks.add(Executors.callable(new queryServiceCaller(qS, request,
					registrationList, merger)));
		}

		/*
		 * Now we are finally ready to execute all the data retrieval tasks in
		 * parallel. Yeah! Let's Do it.
		 */

		try {

			long t0 = System.currentTimeMillis();
			taskExecutor.invokeAll(tasks);
			long t1 = System.currentTimeMillis();
			if (logger.isDebugEnabled()) {
				logger.debug("Finished all tasks in " + (t1 - t0) + " ms");
			}

		} catch (InterruptedException e) {
			logger.warn("Thread Error", e);

		}

		/*
		 * And now that all our tasks have nicely at least tried to retrieve
		 * their data and have put their data into the merger, we use the merger
		 * to do what is was designed for: to merge.
		 */

		QueryContextResponse mergerResponse = merger.get();

		if (logger.isDebugEnabled()) {
			logger.debug("Response after merging: " + mergerResponse);
		}

		/*
		 * Now we call also the result filter if it is present. The result
		 * filter will check the merged query result against the original
		 * request to check whether the response matches what actually has been
		 * queried. Everything that is not matching is thrown out of the result.
		 * 
		 * (Note that the try-catch is a workaround of the fact that this is the
		 * only way we see for testing wether the OSGi service is available)
		 */

		if (BundleUtils.isServiceRegistered(this, resultFilter)) {
			if (logger.isDebugEnabled()) {
				logger.debug("-----------++++++++++++++++++++++ Begin Filter");
			}
			List<QueryContextRequest> lqcReq = new ArrayList<QueryContextRequest>();
			lqcReq.add(request);
			List<ContextElementResponse> lceRes = mergerResponse
					.getListContextElementResponse();

			if (logger.isDebugEnabled()) {
				logger.debug("-----------++++++++++++++++++++++ QueryContextRequest:"
						+ lqcReq.toString()
						+ " ContextElementResponse:"
						+ lceRes.toString());

				logger.debug(lqcReq.size());
				logger.debug(lceRes.size());
			}

			List<QueryContextResponse> lqcRes = resultFilter.filterResult(
					lceRes, lqcReq);

			if (lqcRes.size() == 1) {
				mergerResponse = lqcRes.get(0);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("-----------++++++++++++++++++++++ After Filter ListContextElementResponse:"
						+ lqcRes.toString()
						+ " ContextElementResponse:"
						+ lqcRes.toString());
				logger.debug("-----------++++++++++++++++++++++End Filter");
			}
			logger.info("Result filter found and applied.");
		} else {

			logger.warn("Result filter service not registered!!");

		}

		if (logger.isDebugEnabled()) {
			logger.debug("QueryContextResponse after merger and (maybe) result filter:"
					+ mergerResponse);
		}

		/*
		 * The code snippet below is for dumping the data in a Big Data
		 * repository in addition.
		 * 
		 * (And again we a similar workaround as above for the result filter)
		 */

		final QueryContextResponse queryContextRespListAfterMerge = mergerResponse;

		logger.info("Checking if Big Data Repository is present");
		if (storeQueryResponseAndNotifications) {

			logger.info("Trying to access Big Data repository");

			if (BundleUtils.isServiceRegistered(this, embeddedIoTAgent)) {
				new Thread() {
					@Override
					public void run() {

						try {

							List<ContextElement> contextElementList = new ArrayList<ContextElement>();

							Iterator<ContextElementResponse> iter = queryContextRespListAfterMerge
									.getListContextElementResponse().iterator();

							while (iter.hasNext()) {

								ContextElementResponse contextElementResp = iter
										.next();

								contextElementList.add(contextElementResp
										.getContextElement());

							}

							embeddedIoTAgent.storeData(contextElementList);

						} catch (Exception E) {
							logger.info("Error accessing Big data repository; possibly plugin not found.");
						}

					}
				}.start();
			} else {
				logger.warn("Not possible to store in the Big Data Repository: osgi service not registered");
			}

		} else {
			logger.info("No Big Data Repository");
		}

		/**
		 * The code snippet below is for dumping the data in a Big Data
		 * repository in addition.
		 */
		if (BundleUtils.isServiceRegistered(this, bigDataRepository)) {

			new Thread() {
				@Override
				public void run() {

					List<ContextElement> contextElementList = new ArrayList<ContextElement>();

					Iterator<ContextElementResponse> iter = queryContextRespListAfterMerge
							.getListContextElementResponse().iterator();

					while (iter.hasNext()) {

						ContextElementResponse contextElementResp = iter.next();

						contextElementList.add(contextElementResp
								.getContextElement());

					}

					bigDataRepository.storeData(contextElementList);

				}
			}.start();
		}

		/*
		 * Now, having the nicely merged (and maybe even filtered) query result,
		 * it is time to apply the xpath restriction to it.
		 */

		if (request.getRestriction() != null) {

			String xpathExpression = request.getRestriction()
					.getAttributeExpression();
			if (xpathExpression != null && !xpathExpression.isEmpty()) {
				applyRestriction(xpathExpression, mergerResponse);
			}

		}

		/*
		 * Now the final thing to do is to check if at all there is any- thing
		 * contained in the response or if all the work was for nothing. Well,
		 * if all was for nothing we at least add an error message...
		 */

		if (mergerResponse.getListContextElementResponse() == null
				|| mergerResponse.getListContextElementResponse().isEmpty()) {

			/*
			 * The details of the error message are taken from the discovery
			 * response if present.
			 */
			String details = null;
			if (discoveryResponse != null
					&& discoveryResponse.getErrorCode() != null)
				details = "Discovery response: "
						+ discoveryResponse.getErrorCode().getReasonPhrase()
						+ " (details: "
						+ discoveryResponse.getErrorCode().getDetails() + ")";

			// TODO improve the logging here, the details are not really picking
			// the important informations

			if (logger.isDebugEnabled()) {
				logger.debug("No query results to return! Discovery response:"
						+ discoveryResponse.toString());
			}

			mergerResponse
					.setErrorCode(new StatusCode(
							Code.CONTEXTELEMENTNOTFOUND_404.getCode(),
							ReasonPhrase.CONTEXTELEMENTNOTFOUND_404.toString(),
							details));

		}

		return mergerResponse;

	}

	/**
	 * This method is destructive: it changes the input contextElementResponse.
	 * 
	 * @param attributeExpr
	 *            the attribute expr
	 * @param response
	 *            the response
	 */
	private static void applyRestriction(String attributeExpr,
			QueryContextResponse response) {

		// Apply the Restriction
		XPath xpath = XPathFactory.newInstance().newXPath();

		try {
			XPathExpression expr = xpath.compile(attributeExpr);

			Document doc = XmlFactory.stringToDocument(response.toString());
			Object result = expr.evaluate(doc, XPathConstants.NODESET);

			NodeList nodes = (NodeList) result;

			Iterator<ContextElementResponse> i = response
					.getListContextElementResponse().iterator();

			while (i.hasNext()) {

				ContextElementResponse contextElresp = i.next();
				boolean doesNotAppear = true;

				for (int j = 0; j < nodes.getLength(); j++) {
					if (contextElresp.getContextElement().getEntityId().getId()
							.equals(nodes.item(j).getTextContent())) {

						doesNotAppear = false;
						break;
					}
				}

				if (doesNotAppear) {
					i.remove();
				}
			}

		} catch (XPathExpressionException e) {
			logger.error("Xpath Exception", e);
		}

	}

	/**
	 * This function creates a list of which query context request should be
	 * sent to which address, based on a discovery response (containing the
	 * available data sources) and a query context request (defining which data
	 * should be retrieved).
	 * 
	 * @param discoveryResponse
	 *            The discovery response specifying the available data sources
	 * @param request
	 *            The query context request defining which data is to be
	 *            obtained.
	 * @return A list of (QueryContextRequest,URL) pairs specifying where to
	 *         make which query.
	 */
	private List<Pair<QueryContextRequest, URI>> createQueryRequestList(
			List<ContextRegistrationResponse> contextRegistrationResponseList,
			QueryContextRequest request) {

		List<Pair<QueryContextRequest, URI>> output = new ArrayList<Pair<QueryContextRequest, URI>>();

		for (ContextRegistrationResponse contextRegistrationResponse : contextRegistrationResponseList) {

			List<ContextMetadata> contextMetadataList = contextRegistrationResponse
					.getContextRegistration().getListContextMetadata();

			if (contextMetadataList != null
					&& !contextMetadataList.isEmpty()
					&& "Association".equals(contextMetadataList.get(0)
							.getName().toString())) {
				continue;
			}

			// (1) get the access URI
			URI uri = contextRegistrationResponse.getContextRegistration()
					.getProvidingApplication();

			// Check the trace and discard in case of loop detected
			if (traceKeeperEnabled) {
				if (TraceKeeper.checkRequestorHopVSTrace(
						request.getRestriction(), uri.toString())) {
					logger.info(String
							.format("Loop detected, discarding ContextRegistrationResponse: %sBecause of the QueryContext: %s",
									contextRegistrationResponse, request));
					continue;
				}
			}

			// check if EntityId is != null
			if (contextRegistrationResponse.getContextRegistration()
					.getListEntityId() != null) {

				// (2) create QueryContextRequest
				QueryContextRequest contextRequest = new QueryContextRequest();

				contextRequest.setEntityIdList(contextRegistrationResponse
						.getContextRegistration().getListEntityId());

				List<String> attributeNameList = new ArrayList<String>();

				// check if different to null
				if (contextRegistrationResponse.getContextRegistration()
						.getContextRegistrationAttribute() != null) {

					// run over all attributes
					for (int j = 0; j < contextRegistrationResponse
							.getContextRegistration()
							.getContextRegistrationAttribute().size(); j++) {

						String attributeName = contextRegistrationResponse
								.getContextRegistration()
								.getContextRegistrationAttribute().get(j)
								.getName();

						attributeNameList.add(attributeName);

					}

					contextRequest.setAttributeList(attributeNameList);
				}
				// restriction comes from original ngsi10 request !
				contextRequest.setRestriction(request.getRestriction());

				// Keep trace of the queryContext
				if (traceKeeperEnabled) {
					Restriction restriction = contextRequest.getRestriction();
					restriction = TraceKeeper.addHopToTrace(restriction);
					contextRequest.setRestriction(restriction);
				}

				output.add(new Pair<QueryContextRequest, URI>(contextRequest,
						uri));
			} else {

				QueryContextRequest contextRequest = new QueryContextRequest();

				contextRequest.setEntityIdList(request.getEntityIdList());

				List<String> attributeNameList = new ArrayList<String>();

				// check if different to null
				if (request.getAttributeList() != null) {

					// run over all attributes
					for (int j = 0; j < request.getAttributeList().size(); j++) {

						String attributeName = request.getAttributeList()
								.get(j);

						attributeNameList.add(attributeName);

					}

					contextRequest.setAttributeList(attributeNameList);
				}
				// restriction comes from original ngsi1 request !
				contextRequest.setRestriction(request.getRestriction());

				// Keep trace of the queryContext
				if (traceKeeperEnabled) {
					Restriction restriction = contextRequest.getRestriction();
					restriction = TraceKeeper.addHopToTrace(restriction);
					contextRequest.setRestriction(restriction);
				}

				output.add(new Pair<QueryContextRequest, URI>(contextRequest,
						uri));
			}

		}
		return output;
	}

	/**
	 * 
	 * This operation triggers asynchronous retrieval of Context Information by
	 * the SubscribeContext method defined by NGSI 10. When this method is
	 * called, the IoT Broker Core will send notifications about the requested
	 * context data in regular intervals, where the exact conditions of sending
	 * notifications have to be specified in the request.
	 * 
	 * @param request
	 *            The subscription request.
	 * @return The response returned by the IoT Broker in reaction to the
	 *         request. Note that the response does not contain the actually
	 *         requested data, but only management information about the
	 *         subscription.
	 */
	@Override
	public SubscribeContextResponse subscribeContext(
			final SubscribeContextRequest request) {

		SubscribeContextResponse response;

		if (logger.isDebugEnabled()) {
			logger.debug("Receive Request: " + request);
		}

		response = northBoundWrapper.receiveReqFrmNorth(request);

		/*
		 * If the subscription is successful we forward the subscription also to
		 * the HistoricalAgent (if present)
		 */
		if ((response.getSubscribeError() == null || response
				.getSubscribeError().getStatusCode().getCode() == 200)
				&& (response.getSubscribeError() != null
						&& response.getSubscribeResponse().getSubscriptionId() != null && !response
						.getSubscribeResponse().getSubscriptionId().isEmpty())) {

			if (BundleUtils.isServiceRegistered(this, embeddedIoTAgent)) {

				try {
					if (embeddedIoTAgent.getNgsi10Callback() == null) {
						embeddedIoTAgent.setNgsi10Callback(this);
					}

					String subscriptionId = response.getSubscribeResponse()
							.getSubscriptionId();

					// This is necessary in order to associate the internal
					// subscription in the Big Data Repository, with the
					// incoming
					// subscription from northbound
					// subscriptionController.getLinkSub().insert(subscriptionId,
					// subscriptionId);
					subscriptionController.getSubscriptionStorage()
							.linkSubscriptions(subscriptionId, subscriptionId);

					embeddedIoTAgent.subscribe(subscriptionId, request);

				} catch (org.springframework.osgi.service.ServiceUnavailableException e) {

					logger.warn("Not possible to store in the Big Data Repository: osgi service not registered");

				}

			}

			if (BundleUtils.isServiceRegistered(this, onTimeIntervalHandler)) {

				try {

					String subscriptionId = response.getSubscribeResponse()
							.getSubscriptionId();

					onTimeIntervalHandler
							.pushSubscription(new SubscriptionWithInfo(
									subscriptionId, request));

				} catch (org.springframework.osgi.service.ServiceUnavailableException e) {
					logger.warn("Not possible use the onTimeIntervalHandler: osgi service not registered");
				}

			}

		}

		logger.info("Subscription Response:\n" + response.toString());

		return response;

	}

	/**
	 * This operation can be used for canceling existing subscriptions. The
	 * method is defined by NGSI 10.
	 * 
	 * @param request
	 *            The NGSI 10 SubscribeContextRequest.
	 * @return The NGSI 10 SubscribeContextResponse.
	 */
	@Override
	public UnsubscribeContextResponse unsubscribeContext(
			UnsubscribeContextRequest request) {

		UnsubscribeContextResponse response = northBoundWrapper
				.receiveReqFrmNorth(request);

		/*
		 * If the subscription is successful we forward the subscription also to
		 * the HistoricalAgent (if present)
		 */
		if ((response.getStatusCode() == null || response.getStatusCode()
				.getCode() == 200)
				&& (response.getSubscriptionId() != null && !response
						.getSubscriptionId().isEmpty())) {

			try {
				if (BundleUtils.isServiceRegistered(this, embeddedIoTAgent)) {

					String subscriptionId = response.getSubscriptionId();

					embeddedIoTAgent.unsubscribe(subscriptionId);

				}
			} catch (org.springframework.osgi.service.ServiceUnavailableException e) {
				logger.warn("Not possible to store in the Big Data Repository: osgi service not registered");
			}

		}

		return response;

	}

	/**
	 * This operation implements the UpdateContext operation defined by NGSI 10.
	 * Upon retrieval of an UpdateContextRequest, the IoT Broker will forward
	 * the update to a fixed NGSI-10 data consumer, possibly including results
	 * of applying associations to the updated data.
	 * 
	 * @param request
	 *            The NGSI 10 UpdateContextRequest.
	 * @return The NGSI 10 UpdateContextResponse.
	 */
	@Override
	public UpdateContextResponse updateContext(
			final UpdateContextRequest request) {

		UpdateContextResponse response = null;

		/*
		 * Here we apply associations
		 */
		final UpdateContextRequest updateContextRequest;
		if (associationsEnabled == true) {
			updateContextRequest = AssociationsHandler.applyAssociation(
					request, ngsi9Impl);
		} else {
			updateContextRequest = request;
		}

		/**
		 * Dump data in Historical Agent if present.
		 */
		if (request.getUpdateAction() != UpdateActionType.DELETE
				&& BundleUtils.isServiceRegistered(this, embeddedIoTAgent)) {

			new Thread() {

				@Override
				public void run() {
					try {
						embeddedIoTAgent.storeData(updateContextRequest
								.getContextElement());
					} catch (org.springframework.osgi.service.ServiceUnavailableException e) {
						logger.warn("Not possible to store in the Big Data Repository: osgi service not registered");
					}

				}
			}.start();

		}

		/*
		 * Here we notify subscribers whose subscription matches this update.
		 * Since the Embedded Agent may have its own subscription system, here
		 * we check if it is enabled.
		 */

		if (request.getUpdateAction() != UpdateActionType.DELETE) {
			if (!BundleUtils.isServiceRegistered(this, embeddedIoTAgent)
					|| !embeddedIoTAgent.isSubscriptionEnabled()) {

				notifySubscribers(updateContextRequest);

			} else {
				logger.info("EmbeddedAgent has its own Subscription system therefore SmartUpdateHandler will not be applied");
			}
		}

		/**
		 * Dump data in Big Data Repository if present.
		 */
		if (request.getUpdateAction() != UpdateActionType.DELETE
				&& BundleUtils.isServiceRegistered(this, bigDataRepository)) {

			new Thread() {

				@Override
				public void run() {

					bigDataRepository.storeData(updateContextRequest
							.getContextElement());

				}
			}.start();

		}

		if (pubSubUrlList != null) {
			for (String url : pubSubUrlList) {

				if (url != null) {
					logger.info("Started Contact pub/sub broker: " + url);

					try {
						response = ngsi10Requester.updateContext(
								updateContextRequest, new URI(url));
					} catch (URISyntaxException e) {
						logger.info("Impossible to connect to the pub/sub broker: "
								+ url);
						if (logger.isDebugEnabled()) {
							logger.debug("URI Syntax Error", e);
						}
					}
				}
				// TODO here the only the last response is taken into
				// consideration as updateCotnextResponse. It would be
				// necessary to have some rule (for example, ALL,
				// ATLEASTONE, MOST, NOONE fault tolerant)
			}
		} else if (pubSubUrl != null) {
			logger.info("Started Contact pub/sub broker: " + pubSubUrl);

			try {
				response = ngsi10Requester.updateContext(updateContextRequest,
						new URI(pubSubUrl));
			} catch (URISyntaxException e) {
				logger.info("Impossible to connect to the pub/sub broker: "
						+ pubSubUrl);
				if (logger.isDebugEnabled()) {
					logger.debug("URI Syntax Error", e);
				}
			}
		}

		if (response == null) {
			response = new UpdateContextResponse(new StatusCode(
					Code.INTERNALERROR_500.getCode(),
					ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
					"Unspecified error during UpdateContext"), null);
		} else if ((response.getContextElementResponse() == null || response
				.getContextElementResponse().isEmpty())
				&& (response.getErrorCode() == null || (response.getErrorCode()
						.getCode() != Code.OK_200.getCode() && ignorePubSubFailure))) {
			/*
			 * Here if the ContextElementResponse is empty AND the ErrorCode is
			 * null or there was an error
			 */

			response = new UpdateContextResponse(new StatusCode(
					Code.OK_200.getCode(), ReasonPhrase.OK_200.toString(), ""),
					null);
		}

		return response;

	}

	private void notifySubscribers(UpdateContextRequest updateContextRequest) {

		if (BundleUtils.isServiceRegistered(this, subscriptionStorage)) {

			/*
			 * Here we check the subscriptions matching this update
			 */
			Multimap<String, ContextElementResponse> contextElementToNotifyMap = HashMultimap
					.create();

			for (ContextElement contextElement : updateContextRequest
					.getContextElement()) {

				for (SubscriptionWithInfo subscriptionWithInfo : subscriptionStorage
						.checkContextElement(contextElement)) {
					contextElementToNotifyMap.put(subscriptionWithInfo.getId(),
							new ContextElementResponse(contextElement,
									new StatusCode(Code.OK_200.getCode(),
											ReasonPhrase.OK_200.toString(),
											"New ContextElement")));
				}

			}

			ExecutorService taskExecutor = Executors.newCachedThreadPool();
			List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();

			/*
			 * Here we create the Notifications
			 */
			for (String subscriptionId : contextElementToNotifyMap.keySet()) {

				// TODO modify the originator with the TraceOriginator that
				// seems to
				// got lost
				final NotifyContextRequest notifyContextRequest = new NotifyContextRequest(
						subscriptionId, this.toString(),
						new ArrayList<ContextElementResponse>(
								contextElementToNotifyMap.get(subscriptionId)));

				tasks.add(Executors.callable(new Runnable() {

					@Override
					public void run() {
						notifyContext(notifyContextRequest);

					}
				}));

			}

			try {

				taskExecutor.invokeAll(tasks);

			} catch (InterruptedException e) {

				if (logger.isDebugEnabled()) {
					logger.debug("Thread Error", e);
				}
			}
		}

	}

	/**
	 * This operation implements the NotifyContext operation of NGSI 10. Upon
	 * reception of a notifyContext operation the IoT Broker will find out the
	 * relevant original subscriptions from applications and triggers
	 * notification of these applications are notified accordingly.
	 * 
	 * 
	 * @param request
	 *            the NotifyContextRequest
	 * @return the NotifyContextResponse.
	 */
	@Override
	public NotifyContextResponse notifyContext(
			final NotifyContextRequest request) {

		/*
		 * We pass the notification to the IoT Agent wrapper. The response
		 * received from the wrapper is then returned.
		 * 
		 * If the received response is null, or if it has a status code other
		 * than 200 "OK", we return a notify context response with status code
		 * 500 "internal error".
		 */

		SubscribeContextRequest outgoingSubscription = subscriptionStorage
				.getOutgoingSubscription(request.getSubscriptionId());

		NotifyContextResponse notifyContextResponse = null;

		if (outgoingSubscription == null) {
			notifyContextResponse = new NotifyContextResponse(new StatusCode(
					Code.SUBSCRIPTIONIDNOTFOUND_470.getCode(),
					ReasonPhrase.SUBSCRIPTIONIDNOTFOUND_470.toString(), null));
		}

		/**
		 * The code snippet below is for dumping the data in a Historical Agent.
		 */
		if (storeQueryResponseAndNotifications
				&& !this.toString().equals(request.getOriginator())
				&& embeddedIoTAgent != null) {

			if (BundleUtils.isServiceRegistered(this, embeddedIoTAgent)) {
				new Thread() {

					@Override
					public void run() {
						try {
							List<ContextElement> contextElementList = new ArrayList<ContextElement>();

							Iterator<ContextElementResponse> iter = request
									.getContextElementResponseList().iterator();
							while (iter.hasNext()) {

								ContextElementResponse contextElementresp = iter
										.next();
								contextElementList.add(contextElementresp
										.getContextElement());

							}

							embeddedIoTAgent.storeData(contextElementList);
						} catch (org.springframework.osgi.service.ServiceUnavailableException e) {
							logger.warn("Not possible to store in the Big Data Repository: osgi service not registered");
						}
					}
				}.start();
			} else {
				logger.warn("Not possible to store in the Big Data Repository: osgi service not registered");
			}

		}

		/**
		 * The code snippet below is for dumping the data in a Big Data
		 * repository in addition.
		 */
		if (BundleUtils.isServiceRegistered(this, bigDataRepository)) {
			new Thread() {

				@Override
				public void run() {

					List<ContextElement> contextElementList = new ArrayList<ContextElement>();

					Iterator<ContextElementResponse> iter = request
							.getContextElementResponseList().iterator();
					while (iter.hasNext()) {

						ContextElementResponse contextElementresp = iter.next();
						contextElementList.add(contextElementresp
								.getContextElement());

					}

					bigDataRepository.storeData(contextElementList);

				}
			}.start();
		}

		/*
		 * Apply NotifyConditions
		 */
		NotifyContextRequest notificationFilteredByNotifyConditions = applyNotifyConditions(
				request, outgoingSubscription);

		if (notificationFilteredByNotifyConditions != null) {
			notifyContextResponse = agentWrapper
					.receiveFrmAgents(notificationFilteredByNotifyConditions);

			notifyContextResponse = new NotifyContextResponse(new StatusCode(
					200, ReasonPhrase.OK_200.toString(), null));

		}

		if (notifyContextResponse == null
				|| notifyContextResponse.getResponseCode() != null
				&& notifyContextResponse.getResponseCode().getCode() != Code.OK_200
						.getCode()) {
			return new NotifyContextResponse(new StatusCode(
					Code.INTERNALERROR_500.getCode(),
					ReasonPhrase.RECEIVERINTERNALERROR_500.toString(), null));
		} else {
			return notifyContextResponse;
		}

	}

	private NotifyContextRequest applyNotifyConditions(
			NotifyContextRequest request,
			SubscribeContextRequest outgoingSubscription) {

		NotifyContextRequest notificationFilteredByNotifyConditions = null;

		List<ContextElementResponse> filteredContextElementResponse;

		if (!BundleUtils.isServiceRegistered(this, onValueHandler)
				&& !BundleUtils.isServiceRegistered(this, onChangeHandler)
				&& !BundleUtils
						.isServiceRegistered(this, onTimeIntervalHandler)) {

			return request;

		}

		SubscriptionWithInfo subscriptionWithInfo = new SubscriptionWithInfo(
				request.getSubscriptionId(), outgoingSubscription);

		/*
		 * OnValue notifyCondition
		 */
		filteredContextElementResponse = applyOnValueNotifyCondition(
				request.getContextElementResponseList(), subscriptionWithInfo);

		/*
		 * OnChange notifyCondition
		 */
		filteredContextElementResponse = applyOnChangeNotifyCondition(
				filteredContextElementResponse, subscriptionWithInfo);

		// Create the new notification
		notificationFilteredByNotifyConditions = new NotifyContextRequest(
				request.getSubscriptionId(), request.getSubscriptionId(),
				filteredContextElementResponse);

		/*
		 * OnTimeInterval notifyCondition
		 */
		if (putOnTimeIntervalConditionControl(notificationFilteredByNotifyConditions)) {
			return null;
		} else {
			return notificationFilteredByNotifyConditions;
		}

	}

	private List<ContextElementResponse> applyOnValueNotifyCondition(
			List<ContextElementResponse> contexElementResponseListToFilter,
			SubscribeContextRequest subscription) {

		List<ContextElementResponse> filteredContextElementResponse = new ArrayList<ContextElementResponse>();

		if (BundleUtils.isServiceRegistered(this, onValueHandler)) {

			try {

				filteredContextElementResponse = onValueHandler.applyOnValue(
						contexElementResponseListToFilter, subscription);

			} catch (org.springframework.osgi.service.ServiceUnavailableException e) {

				logger.warn("Not possible use the onValueHandler: osgi service not registered");
				filteredContextElementResponse
						.addAll(contexElementResponseListToFilter);

			}
		} else {

			filteredContextElementResponse
					.addAll(contexElementResponseListToFilter);

		}

		return filteredContextElementResponse;

	}

	private List<ContextElementResponse> applyOnChangeNotifyCondition(
			List<ContextElementResponse> contexElementResponseListToFilter,
			SubscriptionWithInfo subscriptionWithInfo) {

		List<ContextElementResponse> filteredContextElementResponse = new ArrayList<ContextElementResponse>();

		if (BundleUtils.isServiceRegistered(this, onChangeHandler)) {

			try {

				/*
				 * Check the onChange notifyCondition
				 */
				for (ContextElementResponse contextElementResponse : contexElementResponseListToFilter) {

					ContextElement contextElementFiltered = onChangeHandler
							.applyOnChangeNotifyCondition(
									contextElementResponse.getContextElement(),
									subscriptionWithInfo);

					if (contextElementFiltered != null) {
						filteredContextElementResponse
								.add(new ContextElementResponse(
										contextElementFiltered,
										contextElementResponse.getStatusCode()));
					}

				}
			} catch (org.springframework.osgi.service.ServiceUnavailableException e) {

				logger.warn("Not possible use the onChangeHandler: osgi service not registered");
				filteredContextElementResponse
						.addAll(contexElementResponseListToFilter);

			}
		} else {

			filteredContextElementResponse
					.addAll(contexElementResponseListToFilter);

		}

		return filteredContextElementResponse;

	}

	/**
	 * 
	 * @param notification
	 * @return true if it is under onTimeInterval notifyCondition control
	 */
	private boolean putOnTimeIntervalConditionControl(
			NotifyContextRequest notification) {

		boolean underOnTimeIntervalCondition = false;

		if (BundleUtils.isServiceRegistered(this, onTimeIntervalHandler)) {

			try {

				/*
				 * Check the onTimeInterval notifyCondition
				 */
				underOnTimeIntervalCondition = onTimeIntervalHandler
						.notifyContext(notification);

			} catch (org.springframework.osgi.service.ServiceUnavailableException e) {
				logger.warn("Not possible use the onTimeIntervalHandler: osgi service not registered");
			}
		}

		return underOnTimeIntervalCondition;

	}

	/**
	 * This operation implements the NotifyContextAvailability operation of NGSI
	 * 9. Upon reception of an availability operations, the IoT Broker will
	 * interact with the new NGSI data sources that have become available in
	 * order to satisfy the application subscriptions it maintains.
	 * 
	 * 
	 * @param request
	 *            the NotifyContextAvailabilityRequest
	 * @return the NotifyContextAvailabilityResponse.
	 */
	@Override
	public NotifyContextAvailabilityResponse notifyContextAvailability(
			final NotifyContextAvailabilityRequest request) {

		logger.info("NotifyContextRequest : " + request);

		NotifyContextAvailabilityResponse notifyContextAvailabilityResponse = null;

		if (request.getContextRegistrationResponseList() != null
				&& !request.getContextRegistrationResponseList().isEmpty()) {
			notifyContextAvailabilityResponse = confManWrapper
					.receiveReqFrmConfigManager(request);

			logger.info("NotifyContextResponse : "
					+ notifyContextAvailabilityResponse);

			if (notifyContextAvailabilityResponse == null
					|| notifyContextAvailabilityResponse.getResponseCode() != null
					&& notifyContextAvailabilityResponse.getResponseCode()
							.getCode() != Code.OK_200.getCode()) {
				return new NotifyContextAvailabilityResponse(
						new StatusCode(Code.INTERNALERROR_500.getCode(),
								ReasonPhrase.RECEIVERINTERNALERROR_500
										.toString(), null));
			} else {
				return notifyContextAvailabilityResponse;
			}
		}
		return new NotifyContextAvailabilityResponse(new StatusCode(
				Code.OK_200.getCode(), ReasonPhrase.OK_200.toString(), null));

	}

	/**
	 * This method is not implemented by the IoT Broker Core and returns null.
	 */
	@Override
	public RegisterContextResponse registerContext(
			RegisterContextRequest request) {
		return null;
	}

	/**
	 * This method is not implemented by the IoT Broker Core and returns null.
	 */
	@Override
	public DiscoverContextAvailabilityResponse discoverContextAvailability(
			DiscoverContextAvailabilityRequest request) {
		return null;
	}

	/**
	 * This method is not implemented by the IoT Broker Core and returns null.
	 */
	@Override
	public SubscribeContextAvailabilityResponse subscribeContextAvailability(
			SubscribeContextAvailabilityRequest request) {
		return null;
	}

	/**
	 * This method is not implemented by the IoT Broker Core and returns null.
	 */
	@Override
	public UpdateContextAvailabilitySubscriptionResponse updateContextAvailabilitySubscription(
			UpdateContextAvailabilitySubscriptionRequest request) {
		return null;
	}

	/**
	 * This method is not implemented by the IoT Broker Core and returns null.
	 */
	@Override
	public UnsubscribeContextAvailabilityResponse unsubscribeContextAvailability(
			UnsubscribeContextAvailabilityRequest request) {
		return null;
	}

	/**
	 * Implements the UpdateContextSubscription method defined by NGSI 10. This
	 * method is used for changing existing subscriptions.
	 * 
	 * @param request
	 *            The NGSI 10 UpdateContextSubscriptionRequest.
	 * @return The NGSI 10 UpdateContextSubscriptionResponse.
	 */
	@Override
	public UpdateContextSubscriptionResponse updateContextSubscription(
			UpdateContextSubscriptionRequest request) {

		UpdateContextSubscriptionResponse response = northBoundWrapper
				.receiveFrmNorth(request);

		return response;

	}

}
