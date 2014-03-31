/*******************************************************************************
 *   Copyright (c) 2014, NEC Europe Ltd.
 *   All rights reserved.
 *   
 *   Authors:
 *           * Salvatore Longo - salvatore.longo@neclab.eu
 *           * Tobias Jacobs - tobias.jacobs@neclab.eu
 *           * Raihan Ul-Islam - raihan.ul-islam@neclab.eu
 *  
 *    Redistribution and use in source and binary forms, with or without
 *    modification, are permitted provided that the following conditions are met:
 *   1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   3. All advertising materials mentioning features or use of this software
 *     must display the following acknowledgement:
 *     This product includes software developed by NEC Europe Ltd.
 *   4. Neither the name of the NEC nor the
 *     names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY NEC ''AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL NEC BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED 
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package eu.neclab.iotplatform.iotbroker.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import eu.neclab.iotplatform.iotbroker.commons.XmlFactory;
import eu.neclab.iotplatform.iotbroker.core.data.Pair;
import eu.neclab.iotplatform.iotbroker.core.subscription.AgentWrapper;
import eu.neclab.iotplatform.iotbroker.core.subscription.AssociationsUtil;
import eu.neclab.iotplatform.iotbroker.core.subscription.ConfManWrapper;
import eu.neclab.iotplatform.iotbroker.core.subscription.NorthBoundWrapper;
import eu.neclab.iotplatform.iotbroker.core.subscription.SubscriptionController;
import eu.neclab.iotplatform.iotbroker.storage.AvailabilitySubscriptionInterface;
import eu.neclab.iotplatform.iotbroker.storage.IncomingSubscriptionInterface;
import eu.neclab.iotplatform.iotbroker.storage.LinkSubscriptionAvailabilityInterface;
import eu.neclab.iotplatform.iotbroker.storage.LinkSubscriptionInterface;
import eu.neclab.iotplatform.iotbroker.storage.OutgoingSubscriptionInterface;
import eu.neclab.iotplatform.ngsi.api.datamodel.AttributeAssociation;
import eu.neclab.iotplatform.ngsi.api.datamodel.Code;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElement;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElementResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadataAssociation;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.OperationScope;
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
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextAvailabilitySubscriptionRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextAvailabilitySubscriptionResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextSubscriptionRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextSubscriptionResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.ValueAssociation;
import eu.neclab.iotplatform.ngsi.api.ngsi10.Ngsi10Interface;
import eu.neclab.iotplatform.ngsi.api.ngsi10.Ngsi10Requester;
import eu.neclab.iotplatform.ngsi.api.ngsi9.Ngsi9Interface;
import eu.neclab.iotplatform.ngsi.association.datamodel.AssociationDS;
import eu.neclab.iotplatform.ngsi.association.datamodel.EntityAttribute;

/**
 * This class represents the functional core of the IoT Broker. Each IoT Broker
 * deployment uses exactly one instance of this class.
 */
public class IotBrokerCore implements Ngsi10Interface, Ngsi9Interface {

	/** The URL of the pub/sub broker */
	@Value("${pub_sub_addr}")
	private String pubSubUrl;

	/** Executor for asynchronous tasks */
	private final ExecutorService taskExecutor = Executors
			.newCachedThreadPool();

	/** The logger. */
	private static Logger logger = Logger.getLogger(IotBrokerCore.class);

	/** Utility for processing NGSI associations */
	private final AssociationsUtil associationUtil = new AssociationsUtil();

	/** The unique xml factory to be used by all core instances. */
	private static final XmlFactory xmlFactory = new XmlFactory();

	/** Interface for making context availability subscriptions. */
	private AvailabilitySubscriptionInterface availabilitySub;

	/** Interface for receiving context subscriptions. */
	private IncomingSubscriptionInterface incomingSub;

	/** Interface for making context subscriptions. */
	private OutgoingSubscriptionInterface outgoingSub;

	/** Interface for assembling availability subscriptions. */
	private LinkSubscriptionAvailabilityInterface linkAvSub;

	/** Interface for assembling availability subscriptions. */
	private LinkSubscriptionInterface linkSub;

	/** Wrapper for the north-bound interface. */
	private NorthBoundWrapper northBoundWrapper;

	/** Wrapper for the Configuration Management GE. */
	private ConfManWrapper confManWrapper;

	/**
	 * Wrapper for IoT agents, that is, components offering data via an NGSI-10
	 * interface.
	 */
	private AgentWrapper agentWrapper;

	/** The subscription controller. */
	private SubscriptionController subscriptionController;

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
	public AgentWrapper getAgentWrapper() {
		return agentWrapper;
	}

	/**
	 * Sets the agent wrapper.
	 *
	 * @param agentWrapper
	 *            the new agent wrapper
	 */
	public void setAgentWrapper(AgentWrapper agentWrapper) {
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
	 * Returns the incoming subscription interface, which is used for receiving
	 * and processing NGSI10 subscriptions.
	 *
	 * @return the incoming sub
	 */
	public IncomingSubscriptionInterface getIncomingSub() {
		return incomingSub;
	}

	/**
	 * Sets the incoming subscription interface, which is used for receiving and
	 * processing NGSI10 subscriptions.
	 *
	 * @param incomingSub
	 *            the new incoming sub
	 */
	public void setIncomingSub(IncomingSubscriptionInterface incomingSub) {
		this.incomingSub = incomingSub;
	}

	/**
	 * Returns the outgoing subscription interface, which is used for making
	 * NGSI10 subscriptions.
	 *
	 * @return the outgoing sub
	 */
	public OutgoingSubscriptionInterface getOutgoingSub() {
		return outgoingSub;
	}

	/**
	 * Sets the outgoing subscription interface, which is used for making NGSI10
	 * subscriptions.
	 *
	 * @param outgoingSub
	 *            the new outgoing sub
	 */
	public void setOutgoingSub(OutgoingSubscriptionInterface outgoingSub) {
		this.outgoingSub = outgoingSub;
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
	 * Returns the link subscription interface, which is used for maintaining
	 * links between ingoing and outgoing subscriptions.
	 *
	 * @return the link subscription interface
	 */
	public LinkSubscriptionInterface getLinkSub() {
		return linkSub;
	}

	/**
	 * Sets the link subscription interface, which is used for maintaining links
	 * between ingoing and outgoing subscriptions.
	 *
	 * @param linkSub
	 *            The link subscription interface.
	 */
	public void setLinkSub(LinkSubscriptionInterface linkSub) {
		this.linkSub = linkSub;
	}

	/** The implementation of the NGSI 9 interface */
	private Ngsi9Interface ngsi9Impl;

	/** Used to make NGSI 10 requests. */
	private Ngsi10Requester ngsi10Requester;

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

	/**
	 *
	 * This operation realizes synchronous retrieval of Context Information via
	 * the QueryContext method defiend by NGSI 10. When this method is called,
	 * the IoT Broker Core tries to retrieve the requested information and
	 * returns whatever could be retrieved.
	 *
	 * @param request
	 *            The QueryContextRequest.
	 * @return The QueryContextResponse.
	 */
	@Override
	public QueryContextResponse queryContext(QueryContextRequest request) {

		OperationScope operationScope = new OperationScope(
				"IncludeAssociations", "SOURCES");
		ArrayList<OperationScope> lstOperationScopes = null;
		Restriction restriction = new Restriction();
		if(request.getRestriction()!=null){
			if(request.getRestriction().getAttributeExpression()!=null){
				restriction.setAttributeExpression(request.getRestriction().getAttributeExpression());
			}
			if(request.getRestriction().getOperationScope()!=null){
				restriction.setOperationScope(request.getRestriction().getOperationScope());
			}

		}


		if (restriction.getOperationScope() == null) {
			lstOperationScopes = new ArrayList<OperationScope>();
		} else {
			lstOperationScopes = new ArrayList<OperationScope>(restriction.getOperationScope());
		}
		lstOperationScopes.add(operationScope);
		restriction.setOperationScope(lstOperationScopes);
		if (request.getRestriction() != null) {
			restriction.setAttributeExpression(request.getRestriction()
					.getAttributeExpression());
		}

		DiscoverContextAvailabilityRequest discoveryRequest = new DiscoverContextAvailabilityRequest(
				request.getEntityIdList(), request.getAttributeList(),
				restriction);
		logger.debug("DiscoverContextAvailabilityRequest:"
				+ discoveryRequest.toString());

		/* Get the NGSI 9 DiscoverContextAvailabilityResponse */
		DiscoverContextAvailabilityResponse discoveryResponse = ngsi9Impl
				.discoverContextAvailability(discoveryRequest);

		if ((discoveryResponse.getErrorCode() == null || discoveryResponse
				.getErrorCode().getCode() == 200)
				&& discoveryResponse.getContextRegistrationResponse() != null) {

			logger.debug("Receive discoveryResponse from Config Man:"
					+ discoveryResponse);
			List<AssociationDS> assocList = associationUtil
					.retrieveAssociation(discoveryResponse);

			List<AssociationDS> additionalRequestList = associationUtil
					.initialLstOfmatchedAssociation(request, assocList);

			logger.debug("(Step 1) Initial List Of matchedAssociation:"
					+ additionalRequestList);

			List<AssociationDS> transitiveList = associationUtil
					.transitiveAssociationAnalysisFrQuery(assocList,
							additionalRequestList);

			logger.debug("(Step 2 ) Transitive List Of matchedAssociation:"
					+ transitiveList);

			logger.debug("(Step 2 a) Final additionalRequestList List Of matchedAssociation:"
					+ additionalRequestList);

			List<Pair<QueryContextRequest, URI>> queryList = createQueryRequestList(
					discoveryResponse, request);

			QueryResponseMerger merger = new QueryResponseMerger(request);

			// List of Task
			List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();

			// Countdown of Task
			CountDownLatch count = new CountDownLatch(queryList.size());

			for (int i = 0; i < queryList.size(); i++) {
				logger.debug("Starting Thread number: " + i);

				logger.debug("info1:"
						+ queryList.get(i).getLeft().getEntityIdList().get(0)
								.getId());
				logger.debug("info2:"
						+ discoveryResponse.getContextRegistrationResponse()
								.get(i).getContextRegistration()
								.getProvidingApplication());

				tasks.add(Executors.callable(new RequestThread(ngsi10Requester,
						queryList.get(i).getLeft(),
						queryList.get(i).getRight(), merger, count,
						transitiveList)));

			}

			try {

				long t0 = System.currentTimeMillis();
				taskExecutor.invokeAll(tasks);
				long t1 = System.currentTimeMillis();
				logger.debug("Finished all tasks in " + (t1 - t0) + " ms");

			} catch (InterruptedException e) {
				logger.debug("Thread Error", e);
			}

			// Call the Merge Method
			final QueryContextResponse threadResponse = merger.get();
			logger.debug("QueryContextResponse after merger:" + threadResponse);
			// the below thread is for storage only
			new Thread() {
				@Override
				public void run() {

					List<ContextElement> contextElementList = new ArrayList<ContextElement>();

					Iterator<ContextElementResponse> iter = threadResponse
							.getListContextElementResponse().iterator();

					while (iter.hasNext()) {

						ContextElementResponse contextElementResp = iter.next();

						contextElementList.add(contextElementResp
								.getContextElement());

					}

				}
			}.start();

			if (request.getRestriction() != null) {

				String xpathExpression = request.getRestriction()
						.getAttributeExpression();
				applyRestriction(xpathExpression, threadResponse);

			}

			if (threadResponse.getListContextElementResponse() == null
					|| threadResponse.getListContextElementResponse().isEmpty()) {

				threadResponse.setErrorCode(new StatusCode(
						Code.CONTEXTELEMENTNOTFOUND_404.getCode(),
						ReasonPhrase.CONTEXTELEMENTNOTFOUND_404.toString(),
						null));

			}

			return threadResponse;

		} else {

			QueryContextResponse response = new QueryContextResponse(null,
					discoveryResponse.getErrorCode());

			return response;

		}

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

			Document doc = xmlFactory.stringToDocument(response.toString());
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
			logger.debug("Xpath Exception", e);
		}

	}

	/**
	 * Creates the query request list.
	 *
	 * @param discoveryResponse
	 *            the discovery response
	 * @param request
	 *            the request
	 * @return the list
	 */
	private List<Pair<QueryContextRequest, URI>> createQueryRequestList(
			DiscoverContextAvailabilityResponse discoveryResponse,
			QueryContextRequest request) {

		List<Pair<QueryContextRequest, URI>> output = new ArrayList<Pair<QueryContextRequest, URI>>();

		for (int i = 0; i < discoveryResponse.getContextRegistrationResponse()
				.size(); i++) {
			List<ContextMetadata> lstcmd = discoveryResponse
					.getContextRegistrationResponse().get(i)
					.getContextRegistration().getListContextMetadata();
			if (lstcmd.size() > 0
					&& "Association".equals(lstcmd.get(0).getType().toString())) {
				continue;
			}

			// (1) get the access URI
			URI uri = discoveryResponse.getContextRegistrationResponse().get(i)
					.getContextRegistration().getProvidingApplication();

			// check if EntityId is != null
			if (discoveryResponse.getContextRegistrationResponse().get(i)
					.getContextRegistration().getListEntityId() != null) {

				// (2) create QueryContextRequest
				QueryContextRequest contextRequest = new QueryContextRequest();

				contextRequest.setEntityIdList(discoveryResponse
						.getContextRegistrationResponse().get(i)
						.getContextRegistration().getListEntityId());

				List<String> attributeNameList = new ArrayList<String>();

				// check if different to null
				if (discoveryResponse.getContextRegistrationResponse().get(i)
						.getContextRegistration()
						.getContextRegistrationAttribute() != null) {

					// run over all attributes
					for (int j = 0; j < discoveryResponse
							.getContextRegistrationResponse().get(i)
							.getContextRegistration()
							.getContextRegistrationAttribute().size(); j++) {

						String attributeName = discoveryResponse
								.getContextRegistrationResponse().get(i)
								.getContextRegistration()
								.getContextRegistrationAttribute().get(j)
								.getName();

						attributeNameList.add(attributeName);

					}

					contextRequest.setAttributeList(attributeNameList);
				}
				// restriction comes from original ngsi10 request !
				contextRequest.setRestriction(request.getRestriction());

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
		logger.debug("Recieve Request: " + request);
		response = northBoundWrapper.receiveReqFrmNorth(request);

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
		List<AssociationDS> listAssociationDS = new LinkedList<AssociationDS>();
		List<ContextElement> lContextElements = request.getContextElement();
		List<ContextElement> lContextElementsRes = new LinkedList<ContextElement>();
		UpdateContextRequest updateContextRequest = null;
		// Going through individual ContextElement
		Iterator<ContextElement> it = lContextElements.iterator();
		while (it.hasNext()) {
			ContextElement ce = it.next();

			/*
			 * Retrieving EntityID and Entity Attributes for
			 * DiscoverContextAvailabilityRequest
			 */
			List<EntityId> eidList = new LinkedList<EntityId>();
			eidList.add(ce.getEntityId());

			List<ContextAttribute> lContextAttributes = ce
					.getContextAttributeList();
			List<String> attributeList = new LinkedList<String>();
			Iterator<ContextAttribute> itAttributeList = lContextAttributes
					.iterator();
			while (itAttributeList.hasNext()) {
				ContextAttribute ca = itAttributeList.next();
				attributeList.add(ca.getName());
			}
			// Creating Restriction OperationScopes for
			// DiscoverContextAvailabilityRequest
			OperationScope os = new OperationScope("IncludeAssociations",
					"TARGETS");
			List<OperationScope> loperOperationScopes = new LinkedList<OperationScope>();
			loperOperationScopes.add(os);
			Restriction restriction = new Restriction(null,
					loperOperationScopes);

			// Create the NGSI 9 DiscoverContextAvailabilityRequest
			DiscoverContextAvailabilityRequest discoveryRequest = new DiscoverContextAvailabilityRequest(
					eidList, attributeList, restriction);
			// Get the NGSI 9 DiscoverContextAvailabilityResponse
			DiscoverContextAvailabilityResponse discoveryResponse = ngsi9Impl
					.discoverContextAvailability(discoveryRequest);

			/*
			 * Getting Associations information from
			 * DiscoverContextAvailabilityResponse
			 */

			List<ContextRegistrationResponse> lcrr = discoveryResponse
					.getContextRegistrationResponse();
			Iterator<ContextRegistrationResponse> itContextRegistrationResponse = lcrr
					.iterator();
			while (itContextRegistrationResponse.hasNext()) {
				ContextRegistrationResponse crr = itContextRegistrationResponse
						.next();

				List<ContextMetadata> lcmd = crr.getContextRegistration()
						.getListContextMetadata();

				Iterator<ContextMetadata> it1 = lcmd.iterator();
				while (it1.hasNext()) {
					ContextMetadata cmd = it1.next();
					if ("Association".equals(cmd.getType().toString())) {

						logger.debug("++++++++++++++++++++++++++++++++++++++++++++++++++befor value");
						String s = "<value>" + cmd.getValue() + "</value>";
						logger.debug("++++++++++++++++++++++++++++++++++++++++++++++++++befor value");
						ContextMetadataAssociation cma = (ContextMetadataAssociation) xmlFactory
								.convertStringToXml(cmd.toString(),
										ContextMetadataAssociation.class);
						XmlFactory xmlFac = new XmlFactory();
						ValueAssociation va = (ValueAssociation) xmlFac
								.convertStringToXml(s, ValueAssociation.class);
						cma.setValue(va);

						if (va.getAttributeAssociation().size() == 0) {

							AssociationDS ads = new AssociationDS(
									new EntityAttribute(va.getSourceEntity(),
											""), new EntityAttribute(
											va.getSourceEntity(), ""));
							listAssociationDS.add(ads);
						} else {
							List<AttributeAssociation> lAttributeAsociations = va
									.getAttributeAssociation();
							for (AttributeAssociation aa : lAttributeAsociations) {
								AssociationDS ads = new AssociationDS(
										new EntityAttribute(
												va.getSourceEntity(),
												aa.getSourceAttribute()),
										new EntityAttribute(va
												.getTargetEntity(), aa
												.getTargetAttribute()));
								listAssociationDS.add(ads);
							}
						}
					}

				}
			}
			logger.debug("List of Assocaions from ConfigManager:"
					+ listAssociationDS.toString());
			if (!listAssociationDS.isEmpty()) {
				for (ContextAttribute ca : ce.getContextAttributeList()) {

					List<EntityAttribute> loutput = associationUtil
							.findA(listAssociationDS,
									new EntityAttribute(ce.getEntityId(), ca
											.getName()));
					logger.debug("List of effective Associations:"
							+ loutput.toString());
					EntityId currentEntityID = null;

					for (EntityAttribute ea1 : loutput) {
						List<ContextAttribute> lcaRes = new LinkedList<ContextAttribute>();
						if (currentEntityID != null) {
							if (!currentEntityID.getId().equals(
									ea1.getEntity().getId())) {
								ContextElement ceRes = new ContextElement(
										ea1.getEntity(),
										ce.getAttributeDomainName(), lcaRes,
										ce.getDomainMetadata());
								lContextElementsRes.add(ceRes);
								currentEntityID = ea1.getEntity();
							}
						} else {
							ContextElement ceRes = new ContextElement(
									ea1.getEntity(),
									ce.getAttributeDomainName(), lcaRes,
									ce.getDomainMetadata());
							lContextElementsRes.add(ceRes);
							currentEntityID = ea1.getEntity();
						}
						ContextAttribute ca1 = new ContextAttribute(
								"".equals(ea1.getEntityAttribute()) ? ca.getName()
										: ea1.getEntityAttribute(),
								ca.getType(), ca.getcontextValue().toString(),
								ca.getMetaData());
						lcaRes.add(ca1);

					}
				}

			} else {

				lContextElementsRes.add(ce);

			}

		}

		updateContextRequest = new UpdateContextRequest(lContextElementsRes,
				request.getUpdateAction());

		logger.info("Started Contact pub/sub broker..");
		try {
			response = ngsi10Requester.updateContext(updateContextRequest,
					new URI(pubSubUrl));
		} catch (URISyntaxException e) {
			logger.debug("Error Uri Sintax", e);
		}

		return response;

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
	public NotifyContextResponse notifyContext(NotifyContextRequest request) {

		NotifyContextResponse notifyContextResponse = agentWrapper
				.receiveFrmAgents(request);
		if (notifyContextResponse == null
				|| notifyContextResponse.getResponseCode() != null && notifyContextResponse
						.getResponseCode().getCode() != Code.OK_200.getCode()) {
			return new NotifyContextResponse(new StatusCode(
					Code.INTERNALERROR_500.getCode(),
					ReasonPhrase.RECEIVERINTERNALERROR_500.toString(), null));
		} else {
			return notifyContextResponse;
		}

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
		NotifyContextAvailabilityResponse nCAResponse = null;
		nCAResponse = confManWrapper.receiveReqFrmConfigManager(request);

		if (nCAResponse == null
				|| nCAResponse.getResponseCode() != null && nCAResponse
						.getResponseCode().getCode() != Code.OK_200.getCode()) {
			return new NotifyContextAvailabilityResponse(new StatusCode(
					Code.INTERNALERROR_500.getCode(),
					ReasonPhrase.RECEIVERINTERNALERROR_500.toString(), null));
		} else {
			return nCAResponse;
		}

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
