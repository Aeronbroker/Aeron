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
package eu.neclab.iotplatform.iotbroker.core.subscription;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Timer;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import eu.neclab.iotplatform.iotbroker.commons.ComplexMetadataUtil;
import eu.neclab.iotplatform.iotbroker.commons.EntityIDMatcher;
import eu.neclab.iotplatform.iotbroker.commons.GenerateUniqueID;
import eu.neclab.iotplatform.iotbroker.commons.OutgoingSubscriptionWithInfo;
import eu.neclab.iotplatform.iotbroker.commons.Pair;
import eu.neclab.iotplatform.iotbroker.commons.TraceKeeper;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.IoTAgentWrapperInterface;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.ResultFilterInterface;
import eu.neclab.iotplatform.iotbroker.core.IotBrokerCore;
import eu.neclab.iotplatform.iotbroker.core.QueryResponseMerger;
import eu.neclab.iotplatform.iotbroker.storage.AvailabilitySubscriptionInterface;
import eu.neclab.iotplatform.iotbroker.storage.LinkSubscriptionAvailabilityInterface;
import eu.neclab.iotplatform.iotbroker.storage.SubscriptionStorageInterface;
import eu.neclab.iotplatform.ngsi.api.datamodel.Code;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElement;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElementResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.MetadataTypes;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.OperationScope;
import eu.neclab.iotplatform.ngsi.api.datamodel.PEPCredentials;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.ReasonPhrase;
import eu.neclab.iotplatform.ngsi.api.datamodel.Restriction;
import eu.neclab.iotplatform.ngsi.api.datamodel.ScopeTypes;
import eu.neclab.iotplatform.ngsi.api.datamodel.StatusCode;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeError;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextAvailabilitySubscriptionRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextAvailabilitySubscriptionResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextSubscriptionRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextSubscriptionResponse;
import eu.neclab.iotplatform.ngsi.association.datamodel.AssociationDS;

/**
 * The subscription controller is the central component for handling
 * subscriptions in the IoT Broker. Any subscription-related message received by
 * the {@link IotBrokerCore} instance will be directly forwarded to the
 * subscription controller.
 */
public class SubscriptionController {

	private static Logger logger = Logger
			.getLogger(SubscriptionController.class);

	/**
	 * This flag enables the presence information forwarding to NGSI-10
	 * subscriber
	 */
	@Value("${forwardAvailabilityNotifications:false}")
	private boolean forwardAvailabilityNotifications;

	/**
	 * This flag enables the keeping of trace in order to avoid loop in presence
	 * of chain of IoT platform
	 */
	@Value("${traceKeeper_enabled:false}")
	private boolean traceKeeperEnabled;

	/**
	 * This flag enables the forwarding of NGSI-10 notifications to a proxy,
	 * instead of a direct notification to the subscriber
	 */
	@Value("${notificationProxyEnabled:false}")
	private boolean notificationProxyEnabled;

	/**
	 * The proxy that will intercept the notifications instead of a direct
	 * notification to the subscriber.
	 */
	@Value("${notificationProxy:}")
	private String notificationProxy;

	/**
	 * This flag enables the forwarding of NGSI-10 notifications to a proxy,
	 * instead of a direct notification to the subscriber
	 */
	@Value("${PEPCredentialsEnabled:false}")
	private boolean PEPCredentialsEnabled;

	// @Value("${forcePepCredentials:false}")
	// private boolean forcePepCredentials;

	/**
	 * It enable the tracing of the originator IP of the subscription request
	 */
	@Value("${traceOriginatorOfSubscription:false}")
	private boolean traceOriginatorOfSubscription;

	/**
	 * It is meant to create the subscription task even if the ConfMan is not
	 * reachable. Useful for example when there is the bigDataRepository
	 * enabled.
	 */
	@Value("${ignoreIoTDiscoveryFailure:false}")
	private boolean ignoreIoTDiscoveryFailure;

	/**
	 * Pointer to the component from which notifications will be received.
	 */
	private NorthBoundWrapper northBoundWrapper;

	/**
	 * Pointer to the component responsible to communicate with the NGSI 9
	 * server for discovery and availability subscriptions.
	 */
	private ConfManWrapper confManWrapper;

	/**
	 * Pointer to the component responsible to communicate with IoT Agents via
	 * NGSI 10.
	 */
	protected IoTAgentWrapperInterface agentWrapper;

	/*
	 * Used for storage of subscription-related information.
	 */
	private AvailabilitySubscriptionInterface availabilitySub;
	private LinkSubscriptionAvailabilityInterface linkAvSub;
	private SubscriptionStorageInterface subscriptionStorage;

	/**
	 * Used to generate subscription IDs.
	 */
	private final GenerateUniqueID genUniqueID;

	/**
	 * Timer to control the duration of subscriptions.
	 */
	private final Timer timer = new Timer("Subs Duration Timer");

	/**
	 * Used for handling NGSI associations.
	 */
	private final AssociationsUtil associationUtil = new AssociationsUtil();

	private @Value("${default_throttling}")
	long defaultThrottling;
	private @Value("${default_duration}")
	long defaultDuration;

	/**
	 * Used to filter notifications.
	 */
	@Autowired
	private ResultFilterInterface resultFilter = null;

	// private static final String PEP_CREDENTIALS_REQUIRED =
	// "PEPCredentialsRequired";

	private final ContextUniqueIdentifierComparator contextUniqueIdentifierComparator = new ContextUniqueIdentifierComparator();

	/**
	 * A key-value store for storing subscription-related information.
	 */
	private final Map<String, SubscriptionData> subscriptionStore = Collections
			.synchronizedMap(new HashMap<String, SubscriptionData>());

	/**
	 * @return The result filter used for filtering notifications.
	 */
	public ResultFilterInterface getResultFilter() {
		return resultFilter;
	}

	/**
	 * Assigns the result filter used for filtering notifications.
	 */
	public void setResultFilter(ResultFilterInterface resultFilter) {
		this.resultFilter = resultFilter;
	}

	/**
	 * @return Pointer to the component used for receiving notifications.
	 */
	public NorthBoundWrapper getNorthBoundWrapper() {
		return northBoundWrapper;
	}

	/**
	 * Creates a new instance.
	 */
	public SubscriptionController() {
		super();
		genUniqueID = new GenerateUniqueID();
	}

	/**
	 * @return A pointer to the key-value store used for storing subscription-
	 *         related information.
	 */
	public Map<String, SubscriptionData> getSubscriptionStore() {
		return subscriptionStore;
	}

	/**
	 * @return Pointer to the availability subscription storage.
	 */
	public AvailabilitySubscriptionInterface getAvailabilitySub() {
		return availabilitySub;
	}

	/**
	 * @return Sets the pointer to the availability subscription storage.
	 */
	public void setAvailabilitySub(
			AvailabilitySubscriptionInterface availabilitySub) {
		this.availabilitySub = availabilitySub;
	}

	/**
	 * @return Pointer to the storage for links between incoming subscriptions
	 *         and availability subscriptions.
	 */
	public LinkSubscriptionAvailabilityInterface getLinkAvSub() {
		return linkAvSub;
	}

	/**
	 * Sets the pointer to the storage for links between incoming subscriptions
	 * and availability subscriptions.
	 */
	public void setLinkAvSub(LinkSubscriptionAvailabilityInterface linkAvSub) {
		this.linkAvSub = linkAvSub;
	}

	/**
	 * @return Pointer to the subscription storage.
	 */
	public SubscriptionStorageInterface getSubscriptionStorage() {
		return subscriptionStorage;
	}

	/**
	 * Set the pointer to the subscription storage.
	 */
	public void setSubscriptionStorage(
			SubscriptionStorageInterface subscriptionStorage) {
		this.subscriptionStorage = subscriptionStorage;
	}

	/**
	 * Sets the pointer to the component from which notifications will be
	 * received.
	 */
	public void setNorthBoundWrapper(NorthBoundWrapper northBoundWrapper) {
		this.northBoundWrapper = northBoundWrapper;
	}

	/**
	 * Sets the pointer to the component for communication with the NGSI 9
	 * Server.
	 */
	public void setConfManWrapper(ConfManWrapper confManWrapper) {
		this.confManWrapper = confManWrapper;
	}

	/**
	 * Sets the pointer to the component for communication with IoT Agents
	 */
	public void setAgentWrapper(IoTAgentWrapperInterface agentWrapper) {
		this.agentWrapper = agentWrapper;
	}

	/**
	 * @return The default throttling value.
	 */
	public long getDefaultThrottling() {
		return defaultThrottling;
	}

	/**
	 * @return The URL where agents should send there notifications to. This is
	 *         the address where the NGSI RESTful interface is reachable.
	 */
	public String getRefURl() {
		String ref = null;
		try {
			ref = "http://" + InetAddress.getLocalHost().getHostAddress() + ":"
					+ System.getProperty("tomcat.init.port") + "/ngsi10";

		} catch (UnknownHostException e) {
			logger.error("Unknown Host", e);
		}
		return ref;
	}

	/**
	 * @return The URL where agents should send there notifications to. This is
	 *         the address where the NGSI RESTful interface is reachable.
	 */
	public String getNGSI9RefURl() {
		String ref = null;
		try {
			ref = "http://" + InetAddress.getLocalHost().getHostAddress() + ":"
					+ System.getProperty("tomcat.init.port")
					/*
					 * It was decided within a FIWARE discussion, that the ngsi9
					 * reference must be the full path (comprehensive of the
					 * notifyContextAvailability resource)
					 */
					+ "/ngsi9/notifyContextAvailability";

		} catch (UnknownHostException e) {
			logger.error("Unknown Host", e);
		}
		return ref;
	}

	/**
	 * The function is by the northbound wrapper when a new NGSI 10 subscription
	 * arrives.
	 * 
	 * @param scReq
	 *            The NGSI 10 SubscribeContextRequest.
	 * @return The NGSI 10 SusbcribeContextResponse.
	 */
	public SubscribeContextResponse receiveReqFrmNorthBoundWrapper(
			final SubscribeContextRequest scReq) {

		SubscribeContextResponse scRes = null;
		if (logger.isDebugEnabled()) {
			logger.debug("DEFAULT_THROTTLING: " + defaultThrottling);
			logger.debug("DEFAULT_DURATION: " + defaultDuration);
		}

		/*
		 * We retrieve the address where notifications can be sent to the IoT
		 * Broker
		 */

		String ref = getNGSI9RefURl();

		/*
		 * We create a request for retrieving the relevant data sources and
		 * associations for the subscription.
		 */

		SubscribeContextAvailabilityRequest scaReq = new SubscribeContextAvailabilityRequest(
				scReq.getAllEntity(), scReq.getAttributeList(), ref,
				scReq.getDuration(), null, scReq.getRestriction());
		if (logger.isDebugEnabled()) {
			logger.debug("Sending SubscribeContextAvailabilityRequest to ConfManWrapper:"
					+ scaReq.toString());
		}

		/*
		 * Now it is time to instruct the config management wrapper to
		 * communicate with the config manager using the request for retrieving
		 * the relevant data sources and associations that has been created
		 * above.
		 */
		SubscribeContextAvailabilityResponse subscribeContextAvailability = confManWrapper
				.receiveReqFrmSubscriptionController(scaReq);

		/*
		 * The response from the wrapper is analyzed, and if it is not positive
		 * the function is aborted and an error is returned.
		 */
		if (subscribeContextAvailability.getErrorCode() != null
				&& subscribeContextAvailability.getErrorCode().getCode() != 200
				&& !ignoreIoTDiscoveryFailure) {
			scRes = new SubscribeContextResponse(null, new SubscribeError(null,
					new StatusCode(500,
							ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
							null)));
			return scRes;
		} else {

			String idSubRequest = genUniqueID.getNextUniqueId();
			if (logger.isDebugEnabled()) {
				logger.debug("SUBSCRIPTION ID  = " + idSubRequest);
			}

			/*
			 * We also create a task that will automatically unsubscribe again
			 * as soon as the subscription has expired.
			 */

			UnsubscribeTask task = new UnsubscribeTask(idSubRequest, this);
			if (scReq.getDuration() != null && logger.isDebugEnabled()) {
				logger.debug("Subscription time: "
						+ scReq.getDuration().getTimeInMillis(
								new GregorianCalendar()));
			}

			// logger.info(String.format("Notification enable %b, at the proxy: %s",notificationProxyEnabled,
			// notificationProxy));

			// Lets define who should receive the notification when generated.
			// Usually is the one defined by the Reference field in the
			// subscription but it can be overridden if we define a proxy
			String notificationHandler;
			if (notificationProxyEnabled) {
				if (notificationProxy == null || notificationProxy.isEmpty()) {
					notificationHandler = scReq.getReference();
					logger.warn("Notification proxy is not valid: "
							+ notificationProxy);
				}
				notificationHandler = notificationProxy;
			} else {
				notificationHandler = scReq.getReference();
			}

			/*
			 * Now we create a container where the relevant information about
			 * this subscription is packed together. This relevant data consists
			 * of - when the subscription is initiated - a link to the
			 * unsubscribe task
			 */

			SubscriptionData subData = new SubscriptionData(notificationHandler);
			subData.setStartTime(associationUtil.currentTime());
			subData.setUnsubscribeTask(task);

			if (traceOriginatorOfSubscription) {
				String originator = getSubscriptionOriginator(scReq);
				subData.setOriginator(originator);
			}

			/*
			 * The subscription data is now put into the persistent storage,
			 * where the id of the subscription (generated before) is used as
			 * the key.
			 */
			subscriptionStore.put(idSubRequest, subData);

			/*
			 * The unsubscribe task is now submitted to the timer. In case no
			 * duration is given, a default duration is used.
			 */
			if (scReq.getDuration() != null) {
				timer.schedule(
						task,
						scReq.getDuration().getTimeInMillis(
								new GregorianCalendar()));
			} else {
				timer.schedule(task, defaultDuration);
				scaReq.setDuration(associationUtil
						.convertToDuration(defaultDuration));
			}

			/*
			 * Here the incoming subscription is stored in the persistent
			 * storage.
			 */
			subscriptionStorage.saveIncomingSubscription(scReq, idSubRequest,
					System.currentTimeMillis());

			/*
			 * If the answer from the wrapper is positive or we decided it is
			 * decided to ignore problems of the confman (ignoreConfManFailure),
			 * then the subscribe response is also positive. It receives the
			 * subscription id generated before.
			 */

			scRes = new SubscribeContextResponse(new SubscribeResponse(
					idSubRequest, scReq.getDuration(), scReq.getThrottling()),
					new SubscribeError(null, new StatusCode(Code.OK_200
							.getCode(), ReasonPhrase.OK_200.toString(), null)));
			if (logger.isDebugEnabled()) {
				logger.debug("Sending SubscribeContextResponse to NorthBoundWrapper:"
						+ scRes.toString());
			}

			/*
			 * Before the subscribe response is actually returned, the
			 * subscription is also represented in the storage: - the
			 * availability subscription is saved under its id - a link between
			 * the availability subscription and the incoming subscription is
			 * stored
			 */

			if (subscribeContextAvailability.getErrorCode() == null
					|| subscribeContextAvailability.getErrorCode().getCode() == 200) {

				availabilitySub.saveAvalabilitySubscription(
						subscribeContextAvailability,
						subscribeContextAvailability.getSubscribeId());

				linkAvSub.insert(idSubRequest,
						subscribeContextAvailability.getSubscribeId());
			}

			/*
			 * Now finally the successful subscription response is returned.
			 */

			return scRes;
		}

	}

	private String getSubscriptionOriginator(
			SubscribeContextRequest subscription) {

		if (subscription.getRestriction() != null
				&& subscription.getRestriction().getOperationScope() != null
				&& !subscription.getRestriction().getOperationScope().isEmpty()) {

			Iterator<OperationScope> operationScopeIterator = subscription
					.getRestriction().getOperationScope().iterator();
			while (operationScopeIterator.hasNext()) {
				OperationScope operationScope = operationScopeIterator.next();

				if (ScopeTypes.SubscriptionOriginator.toString().toLowerCase()
						.equals(operationScope.getScopeType().toLowerCase())) {
					String originator = operationScope.getScopeValue()
							.toString();
					if (originator.matches("http://.*")) {
						return originator;
					} else {
						return "http://" + originator;
					}
				}
			}
		}

		return null;

	}

	/**
	 * Updates an existing subscription.
	 * 
	 * @param uCSreq
	 *            The NGSI 10 UpdateContextSubscriptionRequest.
	 * @return The NGSI 10 UpdateContextSubscriptionResponse.
	 */
	public UpdateContextSubscriptionResponse receiveReqFrmNorthBoundWrapper(
			UpdateContextSubscriptionRequest uCSreq) {
		UpdateContextSubscriptionResponse uCSres = null;
		SubscribeContextRequest sCReq = subscriptionStorage
				.getIncomingSubscription(uCSreq.getSubscriptionId());
		if (sCReq == null) {
			return new UpdateContextSubscriptionResponse(null,
					new SubscribeError(null, new StatusCode(
							Code.SUBSCRIPTIONIDNOTFOUND_470.getCode(),
							ReasonPhrase.SUBSCRIPTIONIDNOTFOUND_470.toString(),
							null)));

		}
		String subsAvailId = linkAvSub.getAvailIDs(uCSreq.getSubscriptionId())
				.get(0);
		final UpdateContextAvailabilitySubscriptionRequest uCAReq = new UpdateContextAvailabilitySubscriptionRequest(
				sCReq.getEntityIdList(), sCReq.getAttributeList(),
				uCSreq.getDuration(), subsAvailId, uCSreq.getRestriction());
		new Thread() {
			@Override
			public void run() {
				UpdateContextAvailabilitySubscriptionResponse uCASRes = confManWrapper
						.receiveReqFrmSubscriptionController(uCAReq);
				if (uCASRes != null && uCASRes.getErrorCode().getCode() != 200) {
					logger.debug("Error from COnfmanager:"
							+ uCASRes.toString().replaceAll("\\s", ""));
				}
			}
		}.start();

		SubscriptionData subData = subscriptionStore.get(uCSreq
				.getSubscriptionId());
		UnsubscribeTask prvtask = subData.getUnsubscribeTask();
		UnsubscribeTask task = new UnsubscribeTask(uCSreq.getSubscriptionId(),
				this);
		subData.setUnsubscribeTask(task);
		prvtask.cancel();
		if (uCSreq.getDuration() != null) {

			try {
				timer.schedule(
						task,
						uCSreq.getDuration().getTimeInMillis(
								new GregorianCalendar()));
			} catch (Exception e) {
				logger.error("Time Schedule Error", e);
				return new UpdateContextSubscriptionResponse(null,
						new SubscribeError(null, new StatusCode(
								Code.INTERNALERROR_500.getCode(),
								ReasonPhrase.RECEIVERINTERNALERROR_500
										.toString(), null)));
			}
		} else {
			try {
				timer.schedule(task, new Date(System.currentTimeMillis()),
						defaultDuration);
				uCAReq.setDuration(associationUtil
						.convertToDuration(defaultDuration));

			} catch (Exception e) {
				logger.error("Time Schedule Error", e);
				return new UpdateContextSubscriptionResponse(null,
						new SubscribeError(null, new StatusCode(
								Code.INTERNALERROR_500.getCode(),
								ReasonPhrase.RECEIVERINTERNALERROR_500
										.toString(), null)));
			}
		}
		final String idSubscription = uCSreq.getSubscriptionId();
		sendUpdateSubscribeContextRequest(uCSreq, idSubscription);

		uCSres = new UpdateContextSubscriptionResponse(new SubscribeResponse(
				uCSreq.getSubscriptionId(), uCSreq.getDuration(),
				uCSreq.getThrottling()), null);
		return uCSres;
	}

	/**
	 * Cancels the (incoming) subscription having the given identifier.
	 * 
	 * @param subscriptionID
	 *            The identifier of the subscription to cancel.
	 */
	public void unsubscribeOperation(String subscriptionID) {

		List<String> lSubscriptionIDOriginal = linkAvSub
				.getAvailIDs(subscriptionID);

		if (logger.isDebugEnabled()) {
			logger.debug(lSubscriptionIDOriginal);
		}
		if (lSubscriptionIDOriginal.size() == 1) {
			String tmp = lSubscriptionIDOriginal.get(0);
			availabilitySub.deleteAvalabilitySubscription(tmp);
			linkAvSub.delete(subscriptionID, tmp);

			// Deleting the subscription, all the linked outgoing subscription
			// will be deleted as a cascade effect
			subscriptionStorage.deleteIncomingSubscription(subscriptionID);

			SubscriptionData sData = subscriptionStore.get(subscriptionID);
			sData.getUnsubscribeTask().cancel();
			sData.getThrottlingTask().cancel();
			logger.info("Subscription ID: " + subscriptionID + " Canceled");
			subscriptionStore.remove(subscriptionID);
		}

	}

	/**
	 * Cancels an existing subscription.
	 * 
	 * @param uCReq
	 *            The NGSI 10 UnsubscribeContextRequest.
	 * @return The NGSI 10 UnsubscribeContextResponse.
	 */
	public UnsubscribeContextResponse receiveReqFrmNorthBoundWrapper(
			UnsubscribeContextRequest uCReq) {

		List<String> lSubscriptionIDOriginal = linkAvSub.getAvailIDs(uCReq
				.getSubscriptionId());

		if (lSubscriptionIDOriginal.size() != 1) {
			return new UnsubscribeContextResponse(uCReq.getSubscriptionId(),
					new StatusCode(Code.SUBSCRIPTIONIDNOTFOUND_470.getCode(),
							ReasonPhrase.SUBSCRIPTIONIDNOTFOUND_470.toString(),
							null));
		} else {
			final String tmp = lSubscriptionIDOriginal.get(0);
			new Thread() {
				@Override
				public void run() {

					UnsubscribeContextAvailabilityRequest uCAReq = new UnsubscribeContextAvailabilityRequest(
							tmp);
					UnsubscribeContextAvailabilityResponse uCARes = confManWrapper
							.receiveReqFrmSubscriptionController(uCAReq);
					if (uCARes.getStatusCode() != null
							&& uCARes.getStatusCode().getCode() != 200) {
						logger.error("Error in Unscubcriptiopn from Confmanager: "
								+ uCARes);
					}
				}
			}.start();
			availabilitySub.deleteAvalabilitySubscription(tmp);
			linkAvSub.delete(uCReq.getSubscriptionId(), tmp);
			List<String> lSubscriptionIDAgent = subscriptionStorage
					.getOutIDs(uCReq.getSubscriptionId());
			for (String subscriptionAgent : lSubscriptionIDAgent) {
				final String tmp1 = subscriptionAgent;
				final URI agentURi = subscriptionStorage
						.getAgentUri(subscriptionAgent);
				logger.debug("subscriptionAgent: " + subscriptionAgent
						+ " agentURi:" + agentURi.toString());
				new Thread() {
					@Override
					public void run() {
						UnsubscribeContextRequest uCReq = new UnsubscribeContextRequest(
								tmp1);
						UnsubscribeContextResponse uCRes = agentWrapper
								.receiveReqFrmSubscriptionController(uCReq,
										agentURi);
						if (uCRes.getStatusCode() != null
								&& uCRes.getStatusCode().getCode() != 200) {
							logger.error("Error in Unscubcriptiopn from Context Agent: "
									+ uCRes);

						}
					}
				}.start();

				// Deleting the subscription, all the linked outgoing
				// subscription
				// will be deleted as a cascade effect
				subscriptionStorage.deleteIncomingSubscription(uCReq
						.getSubscriptionId());

				SubscriptionData sData = subscriptionStore.get(uCReq
						.getSubscriptionId());
				sData.getUnsubscribeTask().cancel();
				sData.getThrottlingTask().cancel();
				subscriptionStore.remove(tmp);

			}
		}

		return new UnsubscribeContextResponse(uCReq.getSubscriptionId(),
				new StatusCode(Code.OK_200.getCode(),
						ReasonPhrase.OK_200.toString(), null));

	}

	/**
	 * Return a List of ContextElementResponse items that is the result of
	 * applying the given list of associations to the give list of
	 * ContextElementResponse items.
	 * 
	 */
	private List<ContextElementResponse> modifyEntityAttributeBasedAssociation(
			List<AssociationDS> assocList, List<ContextElementResponse> lCres) {

		/*
		 * If the list of associations is empty, the originally given response
		 * list is immediately returned.
		 */

		if (!assocList.isEmpty()) {

			/*
			 * Otherwise we initialize the list of context element responses to
			 * return and then go through all pairs of association and context
			 * element response
			 */

			List<ContextElementResponse> resultingContextElRespList = new ArrayList<ContextElementResponse>();
			for (ContextElementResponse ceResp : lCres) {
				for (AssociationDS aDS : assocList) {

					logger.debug("SubscriptionController: Matching association "
							+ aDS.toString()
							+ "against context element response "
							+ ceResp.toString());

					/*
					 * For each such pair we first look whether the source
					 * entity of the association matches the entity id of the
					 * context element response.
					 * 
					 * If not, then nothing is done
					 */

					if (EntityIDMatcher.matcher(ceResp.getContextElement()
							.getEntityId(), aDS.getSourceEA().getEntity())) {

						/*
						 * If the entities match, then we check whether the
						 * given context element response specifies a non-empty
						 * attribute domain name.
						 */

						boolean attributeDomainNameExists = false;
						if (ceResp.getContextElement().getAttributeDomainName() != null) {
							if (!"".equals(ceResp.getContextElement()
									.getAttributeDomainName())) {
								attributeDomainNameExists = true;

								/*
								 * If the attribute domain name is specified,
								 * then we also check if association is an
								 * attribute association.
								 */

								if (!"".equals(aDS.getSourceEA()
										.getEntityAttribute())) {

									/*
									 * And if it is an attribute association,
									 * then we check if the attribute domain
									 * name of the context element matches with
									 * the source attribute of the association.
									 * 
									 * If it does not match, nothing happens.
									 * 
									 * If it matches, then the association is
									 * applicable and we translate the given
									 * context element response into one where
									 * the entity id is replaced with the target
									 * entity id of the association. This
									 * context element response is then added to
									 * the list of responses to return.
									 */

									if (aDS.getSourceEA()
											.getEntityAttribute()
											.equals(ceResp.getContextElement()
													.getAttributeDomainName())) {
										ContextElement cETmp = new ContextElement(
												aDS.getTargetEA().getEntity(),
												aDS.getTargetEA()
														.getEntityAttribute(),
												ceResp.getContextElement()
														.getContextAttributeList(),
												ceResp.getContextElement()
														.getDomainMetadata());
										ContextElementResponse cEresTmp = new ContextElementResponse(
												cETmp, ceResp.getStatusCode());
										resultingContextElRespList
												.add(cEresTmp);
										logger.debug("SubscriptionController: Successfully applied association, created the context element "
												+ cETmp.toString());
									}
								} else {

									/*
									 * If the association is an entity
									 * association, then it is anyway applicable
									 * to the context element. The same
									 * procedure as above is applied.
									 */

									ContextElement cETmp = new ContextElement(
											aDS.getTargetEA().getEntity(),
											ceResp.getContextElement()
													.getAttributeDomainName(),
											ceResp.getContextElement()
													.getContextAttributeList(),
											ceResp.getContextElement()
													.getDomainMetadata());
									ContextElementResponse cEresTmp = new ContextElementResponse(
											cETmp, ceResp.getStatusCode());

									logger.debug("SubscriptionController: Successfully applied association, created the context element "
											+ cETmp.toString());
									resultingContextElRespList.add(cEresTmp);
								}
							}
						}

						/*
						 * In case there is no attribute domain name specified
						 * in the context element responsem we again check
						 * whether the association is an attribute association.
						 */
						if (attributeDomainNameExists == false) {
							if (!"".equals(aDS.getSourceEA()
									.getEntityAttribute())) {

								/*
								 * If it is an attribute association, we need to
								 * find the attribute values that can be
								 * translated using this association.
								 * 
								 * For this, we run through all attributes
								 * specified in the context element response,
								 * and when we found one that matches with the
								 * source attribute of the association we apply
								 * the association and create a context element
								 * response with the target entity and the
								 * target attribute specified in the
								 * association.
								 */

								for (ContextAttribute ca : ceResp
										.getContextElement()
										.getContextAttributeList()) {
									if (ca.getName().equals(
											aDS.getSourceEA()
													.getEntityAttribute())) {
										List<ContextAttribute> lCaTmp = new ArrayList<ContextAttribute>();
										ca.setName(aDS.getTargetEA()
												.getEntityAttribute());
										lCaTmp.add(ca);
										ContextElement cETmp = new ContextElement(
												aDS.getTargetEA().getEntity(),
												ceResp.getContextElement()
														.getAttributeDomainName(),
												lCaTmp, ceResp
														.getContextElement()
														.getDomainMetadata());
										ContextElementResponse cEresTmp = new ContextElementResponse(
												cETmp, ceResp.getStatusCode());
										resultingContextElRespList
												.add(cEresTmp);
										logger.debug("SubscriptionController: Successfully applied association, created the context element "
												+ cETmp.toString());
									}
								}
							} else {

								/*
								 * The final case is where there is not
								 * attribute domain name specified and the
								 * association is an entity association. In This
								 * case the association is applicable and we can
								 * translate the entity id and keep all
								 * attribute names.
								 */

								ContextElement cETmp = new ContextElement(aDS
										.getTargetEA().getEntity(), ceResp
										.getContextElement()
										.getAttributeDomainName(), ceResp
										.getContextElement()
										.getContextAttributeList(), ceResp
										.getContextElement()
										.getDomainMetadata());
								ContextElementResponse cEresTmp = new ContextElementResponse(
										cETmp, ceResp.getStatusCode());
								resultingContextElRespList.add(cEresTmp);
								logger.debug("SubscriptionController: Successfully applied association, created the context element "
										+ cETmp.toString());
							}
						}

					}
				}
			}
			return resultingContextElRespList;
		}
		return lCres;
	}

	/**
	 * Processes an NGSI 10 notification.
	 * 
	 * @param ncReq
	 *            The NGSI 10 NotifyContextRequest.
	 * @return The NGSI 10 NotifyContextResponse.
	 */
	public NotifyContextResponse receiveReqFrmAgentWrapper(
			NotifyContextRequest ncReq) {

		logger.info("SubscriptionController: Received the following notify context request:"
				+ ncReq.toString()
				+ "/n"
				+ "SubscriptionID is: "
				+ ncReq.getSubscriptionId());

		/*
		 * Initialize the resulting notification request that will be passed to
		 * the northbound wrapper
		 */

		new NotifyContextRequest();

		/*
		 * Retrieves the list of incoming subscription ids that are relevant for
		 * the received notification (note that the notification is the result
		 * of an outgoing subscription).
		 */

		String inID = subscriptionStorage.getInID(ncReq.getSubscriptionId());

		/*
		 * It is expected that exactly one incoming subscription id will be
		 * found. If this is not the case, then the function is aborted and an
		 * error is returned.
		 * 
		 * Otherwise, the function continues.
		 */
		if (inID == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("SubscriptionController: Aborting, because did not find the incoming"
						+ "subscription");
			}
			return new NotifyContextResponse(new StatusCode(470,
					ReasonPhrase.SUBSCRIPTIONIDNOTFOUND_470.toString(), null));
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("SubscriptionController: found incoming subscription ID: "
						+ inID);
			}

			/*
			 * We also retrieve the original incoming subscription message from
			 * the storage.
			 */
			SubscribeContextRequest inSubReq = subscriptionStorage
					.getIncomingSubscription(inID);

			if (inSubReq == null) {
				logger.error("Incoming subscription id found, but no incoming subscription found. Aborting "
						+ "the notification process.");
				return new NotifyContextResponse(new StatusCode(470,
						ReasonPhrase.SUBSCRIPTIONIDNOTFOUND_470.toString(), null));
			}

			logger.debug("SubscriptionController: Identified the original incoming "
					+ "subscription request: " + inSubReq.toString());

			/*
			 * Initialize the list of associations to apply later, and
			 * initialize the list of ContextElementResponses to return then.
			 */
			List<String> listAssoc = null;
			List<ContextElementResponse> lCERes = null;

			/*
			 * Now we retrieve the availability subscription id. This is the
			 * availability subscription that was made on behalf of the
			 * identified incoming subscription.
			 */

			List<String> availId = linkAvSub.getAvailIDs(inID);

			/*
			 * It is again assumed that exactly one availability subscription id
			 * is has been found. If not, then the function aborts and returns
			 * nothing.
			 * 
			 * Otherwise the list of associations that have been stored with
			 * this availability subscription is retrieved. These are the
			 * associations that are potentially applicable for this
			 * notification.
			 */

			if (availId.size() == 1) {
				if (logger.isDebugEnabled()){
					logger.debug("SubscriptionController: found the following availability subscr ID:"
						+ availId.get(0));
				}
				listAssoc = availabilitySub.getListOfAssociations(availId
						.get(0));

			} else if (!ignoreIoTDiscoveryFailure) {
				logger.error("SubscriptionController: found wrong number of availability subscriptions, aborting.");
				return new NotifyContextResponse(new StatusCode(500,
						ReasonPhrase.RECEIVERINTERNALERROR_500.toString(), "found wrong number of availability subscription"));
				
			}

			/*
			 * Now the associations are applied if there are any. Applying the
			 * associations is done by the modifyEntityAttributeBasedAssociation
			 * function. If there are no associations, the
			 * contextelementresponse from the notification are taken as they
			 * are.
			 */

			if (listAssoc != null && !listAssoc.isEmpty()) {
				if (logger.isDebugEnabled())
					logger.debug("SubscriptionController: Applying associations");

				lCERes = modifyEntityAttributeBasedAssociation(
						associationUtil
								.convertToAssociationDS(listAssoc.get(0)),
						ncReq.getContextElementResponseList());

				/*
				 * We merge the results from the associations with the original
				 * context element reponse list
				 */

				lCERes.addAll(ncReq.getContextElementResponseList());
				
				if (logger.isDebugEnabled())
					logger.debug("SubscriptionController: Context Element Responses after applying assoc: "
						+ lCERes.toString());

			} else {
				lCERes = ncReq.getContextElementResponseList();
				
				if (logger.isDebugEnabled())
					logger.debug("SusbcriptionController: Found no associations");
			}

			if (lCERes != null) {
				// this can actually never be null!

				/*
				 * Now, after having applied the associations, it is time to
				 * apply the result filter in order to remove everything from
				 * the notification that has not been queried.
				 */

				/*
				 * If a result filter is available, we apply it to the
				 * notification.
				 */

				// BundleContext bc =
				// FrameworkUtil.getBundle(ResultFilterInterface.class).getBundleContext();
				// ServiceReference ref =
				// bc.getServiceReference(ResultFilterInterface.class.getName());

				if (resultFilter != null) {

					if (logger.isDebugEnabled()){
						logger.debug("SubscriptionController: found resultFilter implementation.");
					}

					/*
					 * As the resultfilter is defined for queries, we have to
					 * convert the subscription request to a query request
					 */

					List<QueryContextRequest> lqcReq_forfilter = new ArrayList<QueryContextRequest>();
					QueryContextRequest tmp_request = new QueryContextRequest(
							inSubReq.getEntityIdList(),
							inSubReq.getAttributeList(),
							inSubReq.getRestriction());
					lqcReq_forfilter.add(tmp_request);

					if (logger.isDebugEnabled())
						logger.debug("SubscriptionController: calling the resultfilter");

					List<QueryContextResponse> lqcRes_fromfilter = resultFilter
							.filterResult(lCERes, lqcReq_forfilter);

					/*
					 * We receive back a query context response from which we
					 * take out the context element responses
					 */
					lCERes = lqcRes_fromfilter.get(0)
							.getListContextElementResponse();
					
					if (logger.isDebugEnabled())
						logger.debug("SubscriptionController: filtered result: "
							+ lCERes.toString());

				} else {
					if (logger.isDebugEnabled())
						logger.debug("SubscriptionController: found no result filter; using the unfiltered result.");
				}

				/*
				 * Now we create a queryContextResponse with our list of context
				 * element responses. We use the QueryResponseMerger to format
				 * the response in a nicer way (eliminating duplicate entities
				 * with the same attributedomain).
				 */

				QueryContextResponse qCRes_forMerger = new QueryContextResponse();
				qCRes_forMerger.setContextResponseList(lCERes);
				QueryResponseMerger qRM = new QueryResponseMerger(null);
				qRM.put(qCRes_forMerger);
				qCRes_forMerger = qRM.get();

				if (logger.isDebugEnabled())
					logger.debug("SubscriptionController: Response list after applying merger:"
						+ qCRes_forMerger.getListContextElementResponse()
								.toString());

				/*
				 * We retrieve the subscriptionData for the incoming
				 * subscription of this notification.
				 */

				SubscriptionData subscriptionData = subscriptionStore.get(inID);

				List<ContextElementResponse> notificationQueue;

				/*
				 * This subscription data contains a queue of unsent
				 * notifications (or otherwise we create it here)
				 */

				if (subscriptionData.getContextResponseQueue() != null) {
					notificationQueue = subscriptionData
							.getContextResponseQueue();
				} else {
					notificationQueue = new ArrayList<ContextElementResponse>();
				}

				/*
				 * The context element responses that we have extracted from the
				 * notification are added now to the notification queue, so that
				 * they will later be sent to the application that has
				 * originally issued the subscription.
				 */

				for (int i = 0; i < qCRes_forMerger
						.getListContextElementResponse().size(); i++) {
					logger.info("###########################################");
					logger.info(qCRes_forMerger.getListContextElementResponse()
							.get(i));

					ContextElementResponse contextElementResponse = qCRes_forMerger
							.getListContextElementResponse().get(i);

					/*
					 * Here we insert PEP credentials if the incoming
					 * Subscription has one in his operation scope list
					 */
					if (PEPCredentialsEnabled) {
						this.addPEPCredentialsToContextElementResponse(
								inSubReq, contextElementResponse);
					}

					/*
					 * If it is enabled the notification proxy, we insert as a
					 * metadata the final NotificationHandler, who should
					 * actually receive the notification
					 */
					if (notificationProxyEnabled) {
						try {
							this.addNotificationHandler(
									inSubReq.getReference(),
									contextElementResponse);
						} catch (URISyntaxException e) {
							logger.info("URISyntaxException", e);
						}
					}

					notificationQueue.add(qCRes_forMerger
							.getListContextElementResponse().get(i));
				}

				/*
				 * After that, the notification queue is put back into the
				 * subscription data, which is then put back into the storage.
				 */
				subscriptionData.setContextResponseQueue(notificationQueue);
				subscriptionStore.put(inID, subscriptionData);
				if (logger.isDebugEnabled()) {
					logger.debug("Adding to subdata subid:" + inID + " : "
							+ subscriptionData);
				}
			}

		}

		/*
		 * Finally the function can return with a positive status code.
		 */
		return new NotifyContextResponse(new StatusCode(200,
				ReasonPhrase.OK_200.toString(), null));

	}

	private void addPEPCredentialsToContextElementResponse(
			SubscribeContextRequest incominbSubscriptionReq,
			ContextElementResponse contextElementResponse) {

		if (incominbSubscriptionReq.getRestriction() != null
				&& incominbSubscriptionReq.getRestriction().getOperationScope() != null
				&& !incominbSubscriptionReq.getRestriction()
						.getOperationScope().isEmpty()) {

			PEPCredentials pepCredentials = null;

			Iterator<OperationScope> operationScopeIter = incominbSubscriptionReq
					.getRestriction().getOperationScope().iterator();

			while (operationScopeIter.hasNext()) {
				OperationScope operationScope = operationScopeIter.next();
				if (operationScope.getScopeType() != null
						&& operationScope.getScopeType().equals(
								"PEPCredentials")) {
					pepCredentials = (PEPCredentials) ComplexMetadataUtil
							.getComplexMetadataValue(
									operationScope.getScopeType(),
									operationScope);
					break;
				}
			}

			if (pepCredentials == null) {
				return;
			}

			List<ContextMetadata> contextMetadataList = contextElementResponse
					.getContextElement().getDomainMetadata();
			try {
				if (contextMetadataList == null) {
					contextMetadataList = new ArrayList<ContextMetadata>();

					contextMetadataList.add(new ContextMetadata(
							"PEPCredentials", new URI("PEPCredentials"),
							pepCredentials));

					contextElementResponse.getContextElement()
							.setDomainMetadata(contextMetadataList);

				} else {

					contextMetadataList.add(new ContextMetadata(
							"PEPCredentials", new URI("PEPCredentials"),
							pepCredentials));
				}
			} catch (URISyntaxException e) {
				logger.info("URISyntaxException", e);
			}

		}

	}

	private void addNotificationHandler(String notificationHandler,
			ContextElementResponse contextElementResponse)
			throws URISyntaxException {

		ContextElement contextElement = contextElementResponse
				.getContextElement();
		if (contextElement.getDomainMetadata() == null) {

			List<ContextMetadata> contextMetadataList = new ArrayList<ContextMetadata>();
			ContextMetadata contexMetadata = new ContextMetadata(
					MetadataTypes.NotificationHandler.toString(),
					new URI("URI"), notificationHandler);
			contextMetadataList.add(contexMetadata);

			contextElement.setDomainMetadata(contextMetadataList);
		} else {
			ContextMetadata contexMetadata = new ContextMetadata(
					MetadataTypes.NotificationHandler.toString(),
					new URI("URI"), notificationHandler);
			contextElement.getDomainMetadata().add(contexMetadata);
		}
	}

	/**
	 * Instructs the SubscriptionController to process an Availability
	 * Notification and additionally passes it a list of associations which
	 * determine how the data from the agents is to be interpreted.
	 * 
	 * @param notifyContextAvailabilityRequest
	 *            The NGSI 9 NotifyContextAvailabilityRequest.
	 * @param transitiveList
	 *            List of associations explaining how to interpret the results.
	 * @return The NGSI 9 NotifyContextAvailabilityResponse.
	 */
	public NotifyContextAvailabilityResponse receiveReqFrmConfManWrapper(
			NotifyContextAvailabilityRequest notifyContextAvailabilityRequest,
			List<AssociationDS> transitiveList) {

		logger.info("Received NotifyContextAvailabilityRequest:"
				+ notifyContextAvailabilityRequest);
		String incomingSubscriptionId = "";

		/*
		 * First, the storage is updated with the new availability notification
		 * and the new association list. (See documentation of availability
		 * subscription storage)
		 */
		String association = associationUtil
				.convertAssociationToString(transitiveList);

		if (logger.isDebugEnabled()) {
			logger.debug("association:" + association);
		}

		availabilitySub.updateAvalabilitySubscription(
				notifyContextAvailabilityRequest, association,
				notifyContextAvailabilityRequest.getSubscribeId());

		/*
		 * Now the id of the incoming subscription corresponding to this
		 * availability notification is retrieved from storage.
		 */
		List<String> inIDs = linkAvSub
				.getInIDs(notifyContextAvailabilityRequest.getSubscribeId());

		logger.info("List Incoming Subscription Id ------------------>" + inIDs);

		if (inIDs.size() != 0) {
			incomingSubscriptionId = inIDs.get(0);
		} else {
			logger.error("No incoming subscriptions matching the availability notification found.");

			// The notification seems to come from an availability subscription
			// not represented in
			// IoT Broker; so the process is aborted and an error message is
			// returned.

			return new NotifyContextAvailabilityResponse(
					new StatusCode(
							400,
							"NOTIFICATION NOT USED",
							"The IoT Broker has no data "
									+ "subscriptions to match against this context availability notification "));

		}

		// Get the Incoming NGSI-10 Subscription data from its ID
		SubscribeContextRequest incomingSubscription = subscriptionStorage
				.getIncomingSubscription(incomingSubscriptionId);

		/*
		 * We retrieve a sorted list (by agent ContextUniqueIdentifier made of
		 * EntityId, Type and ProvidingApplication) of all outgoing
		 * subscriptions made on behalf of this incoming subscription.
		 */

		PriorityQueue<Pair<ContextUniqueIdentifier, OutgoingSubscriptionWithInfo>> existingSubscribedAgentQ = getExistingOutgoingSubs(incomingSubscriptionId);

		/*
		 * Furthermore, we retrieve a sorted (again by ContextUniqueIdentifier)
		 * list that contains all subscriptions that are recommended by this
		 * availability notification.
		 */

		PriorityQueue<Pair<ContextUniqueIdentifier, ContextRegistrationResponse>> newProposedSubscribedAgentQ = convertToPrioQueue(notifyContextAvailabilityRequest
				.getContextRegistrationResponseList());

		/*
		 * Here we pre-process the ContextRegistrationResponseList in order to
		 * find ContextRegistrations that are updated. [Status Code 410 means
		 * that a Registration is not anymore valid]
		 * 
		 * In order to do so, we iterate over the ContextRegistrations and we
		 * find the couples of ContextRegistration with the same
		 * ContextUniqueIdentifier but one of them with StatusCode 410-Gone.
		 * 
		 * What this method returns is the filtered list where the "410-gone"
		 * registrations have been removed.
		 */
		newProposedSubscribedAgentQ = this
				.checkUpdates(newProposedSubscribedAgentQ);

		/*
		 * Here we check for each ContextRegistrationResponse if it has a
		 * ProvidingApplication that is present in the trace of the
		 * IncomingSubscription
		 */
		if (traceKeeperEnabled) {

			Iterator<Pair<ContextUniqueIdentifier, ContextRegistrationResponse>> pairIterator = newProposedSubscribedAgentQ
					.iterator();
			while (pairIterator.hasNext()) {

				Pair<ContextUniqueIdentifier, ContextRegistrationResponse> pair = pairIterator
						.next();

				if (TraceKeeper.checkRequestorHopVSTrace(
						incomingSubscription.getRestriction(), pair.getRight()
								.getContextRegistration()
								.getProvidingApplication().toString())) {
					logger.info(String
							.format("Discarding ContextRegistrationResponse: %s \n"
									+ "In order to avoid loop with the SubscribeContext: %s",
									pair.getRight(), incomingSubscription));
					pairIterator.remove();
				}

			}

		}

		/*
		 * 
		 * What we want to achieve is that - existing subscriptions that are not
		 * needed anymore are cancelled - new subscriptions that are needed are
		 * made
		 * 
		 * How this can be achieved with the prio queues is: (assuming they
		 * always have the lexicogr. largest element on top)
		 * 
		 * - While the existing subscr queue is not empty -- if the recomm.queue
		 * is empty, break -- if the top element of the recom queue is lexicog.
		 * larger than the top element of the exist. queue: ---- make
		 * subscription to top element of recom queue ---- remove first element
		 * of recom queue ---- continue -- if both top elements are equal ----
		 * if necessary, update the subscription to this agent ---- remove top
		 * element of both queues and continue -- if the top element of the
		 * recom queue is lexicog. smaller than the top element of the exist.
		 * queue: ---- cancel subscription corresp. to top element of exist.
		 * queue ---- remove top element of exist. queue ---- continue
		 * 
		 * After the loop, one of the queues is empty and the remaining
		 * subscriptions from the resp. other queue are issued or cancelled.
		 * 
		 * To see that this is correct, observe that 1) an action performed for
		 * each subscription in any of the queues exactly once, because whenever
		 * an action is performed for a queue element, the element is removed
		 * afterwards. 2) Conversely, when elements are removed an action is
		 * always performed. 3) As always the larger element is removed, for all
		 * pairs of equal URIs we perform the appropriate subscription update
		 * action.
		 */

		ContextUniqueIdentifierComparator keyComparator = new ContextUniqueIdentifierComparator();

		Pair<ContextUniqueIdentifier, OutgoingSubscriptionWithInfo> exSATmp = null;
		Pair<ContextUniqueIdentifier, ContextRegistrationResponse> npSATmp = null;
		if (!existingSubscribedAgentQ.isEmpty()) {

			while (!existingSubscribedAgentQ.isEmpty()
					&& !newProposedSubscribedAgentQ.isEmpty()) {
				// Peek the head from both priority queue
				Pair<ContextUniqueIdentifier, OutgoingSubscriptionWithInfo> exSA = existingSubscribedAgentQ
						.peek();
				Pair<ContextUniqueIdentifier, ContextRegistrationResponse> npSA = newProposedSubscribedAgentQ
						.peek();

				// Compare the key
				int compareResult = keyComparator.compare(exSA.getLeft(),
						npSA.getLeft());

				logger.debug("PEEK exist " + exSA.getLeft().toString()
						+ " : PEEK new" + npSA.getLeft().toString() + " : "
						+ compareResult);

				if (compareResult == 0) {

					/*
					 * If we are here the two top elements of the two queue
					 * refers to the same ContextRegistration
					 */

					// Unsubscribe from the previous subscription
					exSATmp = existingSubscribedAgentQ.poll();
					logger.info("POLL exist" + exSATmp.getLeft().toString());
					sendUnsubscribeContextRequest(exSATmp.getRight().getId(),
							exSATmp.getLeft().getProvidingApplication());

					// Subscribe with a new subscription
					npSATmp = newProposedSubscribedAgentQ.poll();
					logger.info("POLL new" + npSATmp.getLeft().toString());
					if (npSA.getRight().getErrorCode() == null
							|| npSA.getRight().getErrorCode().getCode() != 410) {
						sendSubscribeContextRequest(
								createSubscribeContextRequestFromCRRes(
										npSATmp.getRight(),
										incomingSubscriptionId), npSATmp
										.getLeft().getProvidingApplication(),
								incomingSubscriptionId);
					}

					// In case the forwardAvailabilityNotifications feature is
					// on, perform the forwarding
					if (forwardAvailabilityNotifications) {
						if (npSA.getRight().getErrorCode() == null
								|| npSA.getRight().getErrorCode().getCode() != 410) {
							this.notifyAvailabilityToSubscriber(
									Availability.UPDATE, npSA.getRight(),
									incomingSubscriptionId);
						} else {
							this.notifyAvailabilityToSubscriber(
									Availability.GONE, npSA.getRight(),
									incomingSubscriptionId);
						}
					}

				} else if (compareResult > 0) {
					npSATmp = newProposedSubscribedAgentQ.poll();
					logger.info("POLL new" + npSATmp.getLeft().toString());

					if (npSA.getRight().getErrorCode() == null
							|| npSA.getRight().getErrorCode().getCode() != 410) {
						sendSubscribeContextRequest(
								createSubscribeContextRequestFromCRRes(
										npSATmp.getRight(),
										incomingSubscriptionId), npSATmp
										.getLeft().getProvidingApplication(),
								incomingSubscriptionId);

						if (forwardAvailabilityNotifications) {
							this.notifyAvailabilityToSubscriber(
									Availability.NEW, npSA.getRight(),
									incomingSubscriptionId);
						}
					}

				} else if (compareResult < 0) {
					exSATmp = existingSubscribedAgentQ.poll();

					/*
					 * Since this version of IoT Broker is incremental, the
					 * following operations should not be done
					 */

					// logger.info("POLL exist" + exSATmp.getLeft().toString());
					//
					// sendUnsubscribeContextRequest(exSATmp.getRight(), exSATmp
					// .getLeft().getProvidingApplication(),
					// subscriptionId);
					//
					// if (forwardAvailabilityNotifications) {
					// this.notifyAgentAboutAvailability(Availability.GONE,
					// npSA.getRight(), subscriptionId);
					// }
				}
			}

			/*
			 * Since this version of IoT Broker is incremental, the following
			 * operations should not be done
			 */

			// while (!existingSubscribedAgentQ.isEmpty()) {
			// exSATmp = existingSubscribedAgentQ.poll();
			// logger.info("POLL exist" + exSATmp.getLeft().toString()
			// + " :q " + existingSubscribedAgentQ.size());
			// sendUnsubscribeContextRequest(exSATmp.getRight(),
			// exSATmp.getLeft(), subscriptionId);
			//
			// }

			while (!newProposedSubscribedAgentQ.isEmpty()) {
				npSATmp = newProposedSubscribedAgentQ.poll();
				logger.info("POLL new" + npSATmp.getLeft().toString() + " :q "
						+ newProposedSubscribedAgentQ.size());

				logger.info("ContextRegistrationResponse" + npSATmp.getRight());
				logger.info("URI Agent" + npSATmp.getLeft());

				if (npSATmp.getRight().getErrorCode() == null
						|| npSATmp.getRight().getErrorCode().getCode() != 410) {
					sendSubscribeContextRequest(
							createSubscribeContextRequestFromCRRes(
									npSATmp.getRight(), incomingSubscriptionId),
							npSATmp.getLeft().getProvidingApplication(),
							incomingSubscriptionId);

					if (forwardAvailabilityNotifications) {
						this.notifyAvailabilityToSubscriber(Availability.NEW,
								npSATmp.getRight(), incomingSubscriptionId);
					}
				}
			}
		} else {
			while (!newProposedSubscribedAgentQ.isEmpty()) {
				npSATmp = newProposedSubscribedAgentQ.poll();
				logger.info("POLL new" + npSATmp.getLeft().toString() + " :q "
						+ newProposedSubscribedAgentQ.size());

				if (npSATmp.getRight().getErrorCode() == null
						|| npSATmp.getRight().getErrorCode().getCode() != 410) {
					sendSubscribeContextRequest(
							createSubscribeContextRequestFromCRRes(
									npSATmp.getRight(), incomingSubscriptionId),
							npSATmp.getLeft().getProvidingApplication(),
							incomingSubscriptionId);

					if (forwardAvailabilityNotifications) {
						this.notifyAvailabilityToSubscriber(Availability.NEW,
								npSATmp.getRight(), incomingSubscriptionId);
					}
				}
			}
		}

		// TODO check why there is no proper return statement.
		return null;
	}

	private PriorityQueue<Pair<ContextUniqueIdentifier, ContextRegistrationResponse>> checkUpdates(
			PriorityQueue<Pair<ContextUniqueIdentifier, ContextRegistrationResponse>> notifiedPriorityQueue) {

		// logger.info("running the checkUpdates function for \n"+notifiedPriorityQueue.toString()
		// );

		/*
		 * TODO: currently this function removes registrations that are
		 * outdated. But it could be enhanced to in general remove duplicate
		 * registrations.
		 */

		/*
		 * Create new Priority Queue for Pairs of Identifier and Context
		 * Registration response. The queue is sorted by the identifier.
		 */
		PriorityQueue<Pair<ContextUniqueIdentifier, ContextRegistrationResponse>> priorityQueueWithUpdateInfo = new PriorityQueue<Pair<ContextUniqueIdentifier, ContextRegistrationResponse>>(
				2,
				new Comparator<Pair<ContextUniqueIdentifier, ContextRegistrationResponse>>() {
					@Override
					public int compare(
							Pair<ContextUniqueIdentifier, ContextRegistrationResponse> pair1,
							Pair<ContextUniqueIdentifier, ContextRegistrationResponse> pair2) {
						return contextUniqueIdentifierComparator.compare(
								pair1.getLeft(), pair2.getLeft());
					}
				});

		/*
		 * here another instance of the ContextUniqueIdentifierComparator is
		 * created (in addition to the final one called
		 * contextUniqueIdentifierComparator
		 */
		ContextUniqueIdentifierComparator comparator = new ContextUniqueIdentifierComparator();

		/*
		 * Now we run through the input priority queue...
		 */
		Pair<ContextUniqueIdentifier, ContextRegistrationResponse> pairPolled;
		Pair<ContextUniqueIdentifier, ContextRegistrationResponse> pairPeeked;

		while (!notifiedPriorityQueue.isEmpty()) {

			pairPolled = notifiedPriorityQueue.poll();

			/*
			 * If pairPolled was the last element of the queue, then add it to
			 * the output queue
			 */
			if (notifiedPriorityQueue.isEmpty()) {
				priorityQueueWithUpdateInfo.add(pairPolled);
				continue;
			}

			/*
			 * Otherwise look at the current tail of the queue
			 */
			pairPeeked = notifiedPriorityQueue.peek();

			/*
			 * check whether the queue tail and the polled element have the same
			 * identifier.
			 */
			/*
			 * if the identifiers of the polled element and the queue tail are
			 * not the same, then we simply add the polled element to the output
			 * queue.
			 */
			if (comparator.compare(pairPolled.getLeft(), pairPeeked.getLeft()) != 0) {

				priorityQueueWithUpdateInfo.add(pairPolled);
				continue;
			}

			/*
			 * If they are the same, then additionally check if the error code
			 * of the queue tail has error code 410
			 */
			if (pairPeeked.getRight().getErrorCode() != null
					&& pairPeeked.getRight().getErrorCode().getCode() == 410) {

				/*
				 * if the identifiers are the same and the error code of the
				 * queue tail is 410, then add to the output priority queue a
				 * pair consisting of that identifier and a new context
				 * registration response with - the context registration of the
				 * POLLED element - status code 301 "Moved Permanently"
				 * 
				 * Then remove and throw away the queue tail
				 */
				priorityQueueWithUpdateInfo
						.add(new Pair<SubscriptionController.ContextUniqueIdentifier, ContextRegistrationResponse>(
								pairPolled.getLeft(),
								new ContextRegistrationResponse(pairPolled
										.getRight().getContextRegistration(),
										new StatusCode(301,
												"Moved Permanently",
												"Update Entity"))));

				notifiedPriorityQueue.poll();
			} else {

				/*
				 * If the identifiers the same, but the error code of the queue
				 * tail is not 410, then add to the output priority queue a pair
				 * consisting of that identifier and a new context registration
				 * response with - the context registration of the TAIL QUEUE -
				 * status code 301 "Moved Permanently"
				 * 
				 * Then, also here, remove and throw away the queue tail.
				 */

				priorityQueueWithUpdateInfo
						.add(new Pair<SubscriptionController.ContextUniqueIdentifier, ContextRegistrationResponse>(
								pairPeeked.getLeft(),
								new ContextRegistrationResponse(pairPolled
										.getRight().getContextRegistration(),
										new StatusCode(301,
												"Moved Permanently",
												"Update Entity"))));

				notifiedPriorityQueue.poll();
			}

		}

		return priorityQueueWithUpdateInfo;

	}

	private enum Availability {
		NEW, UPDATE, GONE;
	}

	private void notifyAvailabilityToSubscriber(Availability availability,
			ContextRegistrationResponse contextRegistrationResponse,
			String incomingSubscriptionID) {

		StatusCode statusCode = new StatusCode();
		switch (availability) {
		case NEW:
			statusCode.setCode(201);
			statusCode.setReasonPhrase("Created");
			statusCode.setDetails("New Entity Available");
			break;
		case UPDATE:
			statusCode.setCode(301);
			statusCode.setReasonPhrase("Moved Permanently");
			statusCode.setDetails("Update Entity");
			break;
		case GONE:
			statusCode.setCode(410);
			statusCode.setReasonPhrase("Gone");
			statusCode.setDetails("Not Available Anymore");
			break;
		}

		SubscribeContextRequest incomingSubscription = subscriptionStorage
				.getIncomingSubscription(incomingSubscriptionID);

		NotifyContextRequest notifyContextRequest = new NotifyContextRequest();

		notifyContextRequest.setSubscriptionId(incomingSubscriptionID);

		if (traceOriginatorOfSubscription) {
			String originator = getSubscriptionOriginator(incomingSubscription);
			notifyContextRequest.setOriginator(originator);
		}

		/*
		 * Create the ContextElementResponse list
		 */
		List<ContextElementResponse> contextElementResponseList = new ArrayList<ContextElementResponse>();

		Iterator<EntityId> entityIdIter = contextRegistrationResponse
				.getContextRegistration().getListEntityId().iterator();
		while (entityIdIter.hasNext()) {
			EntityId entityId = entityIdIter.next();

			ContextElement contextElement = new ContextElement();

			contextElement.setEntityId(entityId);
			contextElement.setDomainMetadata(contextRegistrationResponse
					.getContextRegistration().getListContextMetadata());

			ContextElementResponse contElementResponse = new ContextElementResponse();
			contElementResponse.setContextElement(contextElement);
			contElementResponse.setStatusCode(statusCode);

			/*
			 * Here we insert PEP credentials if the incoming Subscription has
			 * one in his operation scope list
			 */
			if (PEPCredentialsEnabled) {
				this.addPEPCredentialsToContextElementResponse(
						incomingSubscription, contElementResponse);
			}

			if (notificationProxyEnabled) {
				try {
					this.addNotificationHandler(
							incomingSubscription.getReference(),
							contElementResponse);
				} catch (URISyntaxException e) {
					logger.info("URISyntaxException", e);
				}
			}

			contextElementResponseList.add(contElementResponse);
		}

		notifyContextRequest.setContextResponseList(contextElementResponseList);

		try {
			northBoundWrapper.forwardNotification(notifyContextRequest,
					new URI(subscriptionStore.get(incomingSubscriptionID)
							.getNotificationHandler()));
		} catch (URISyntaxException e) {
			logger.info("URISyntaxException", e);
		}

	}

	// private boolean isPepCredentialsRequired(
	// ContextRegistrationResponse contextRegistrationResponse) {
	//
	// if (forcePepCredentials) {
	// return true;
	// } else {
	//
	// ContextRegistration contextRegistration = contextRegistrationResponse
	// .getContextRegistration();
	//
	// List<ContextMetadata> contextMetadataList = contextRegistration
	// .getListContextMetadata();
	// if (contextMetadataList == null || contextMetadataList.isEmpty()) {
	// return false;
	// } else {
	// for (ContextMetadata contextMetadata : contextMetadataList) {
	// if (PEP_CREDENTIALS_REQUIRED.toLowerCase().equals(
	// contextMetadata.getName().toLowerCase())
	// && contextMetadata.getValue().toString()
	// .toLowerCase().equals("true")) {
	// return true;
	// }
	// }
	// }
	// }
	//
	// return false;
	// }

	private SubscribeContextRequest createSubscribeContextRequestFromCRRes(
			ContextRegistrationResponse contextRegistrationResponse,
			String originalId) {

		SubscribeContextRequest sCReq = subscriptionStorage
				.getIncomingSubscription(originalId);

		if (sCReq.getDuration() == null) {
			try {
				sCReq.setDuration(DatatypeFactory.newInstance().newDuration(
						defaultDuration));
			} catch (DatatypeConfigurationException e) {
				logger.info("DatatypeConfigurationException: ", e);
			}
		}

		logger.info("ContextRegistrationResponse-------->"
				+ contextRegistrationResponse.getContextRegistration()
						.getListEntityId());
		logger.info("Subscription Id-------->" + originalId);

		// TODO filter between original incomingSubscription and
		// ContextRegistrationResponse that is coming the NGSI-9-notification

		/*
		 * In case the ContextElementResponse does not contain an EntityIdList
		 * or it is Empty here it will be populated with a wildcard EntityId
		 */
		if (contextRegistrationResponse.getContextRegistration()
				.getListEntityId() == null
				|| contextRegistrationResponse.getContextRegistration()
						.getListEntityId().isEmpty()) {
			EntityId wildcardEntityId = new EntityId(".*", null, true);
			List<EntityId> entityIdList = new ArrayList<EntityId>();
			entityIdList.add(wildcardEntityId);
			sCReq.setEntityIdList(entityIdList);
		} else {
			sCReq.setEntityIdList(contextRegistrationResponse
					.getContextRegistration().getListEntityId());
		}

		List<String> attribute = new ArrayList<String>();
		for (ContextRegistrationAttribute cRAttributeName : contextRegistrationResponse
				.getContextRegistration().getContextRegistrationAttribute()) {
			attribute.add(cRAttributeName.getName());
		}
		sCReq.setAttributeList(attribute);
		sCReq.setReference(getRefURl());

		/*
		 * Here we add trace information.
		 */
		if (traceKeeperEnabled) {
			Restriction restriction = sCReq.getRestriction();
			restriction = TraceKeeper.addHopToTrace(restriction);
			sCReq.setRestriction(restriction);
		}

		SubscriptionData subdata = subscriptionStore.get(originalId);

		logger.info("Current Duration: " + sCReq.getDuration().toString());
		sCReq.setDuration(associationUtil.newDuration(sCReq.getDuration(),
				associationUtil.currentTime().getTime()
						- subdata.getStartTime().getTime()));
		logger.info("New Duration: " + sCReq.getDuration().toString());
		return sCReq;
	}

	private void sendUnsubscribeContextRequest(String outGoingID,
			final URI agentURi) {

		subscriptionStorage.deleteOutgoingSubscription(outGoingID);
		final UnsubscribeContextRequest uCRequest = new UnsubscribeContextRequest(
				outGoingID);
		new Thread() {
			@Override
			public void run() {
				agentWrapper.receiveReqFrmSubscriptionController(uCRequest,
						agentURi);
			}

		}.start();
	}

	private void sendUpdateSubscribeContextRequest(
			UpdateContextSubscriptionRequest updateRequest, String originalID) {

		// TODO check here if it is correct. I'm afraid that here it sending
		// directly subscription update to IoT Agent without checking against
		// the
		// IoT discovery if the IoT Agent are anymore compliant with the
		// subscription parameter

		// List<String> listOutIDs = linkSub.getOutIDs(originalID);
		List<String> listOutIDs = subscriptionStorage.getOutIDs(originalID);

		for (String id : listOutIDs) {

			final UpdateContextSubscriptionRequest tuCReq = new UpdateContextSubscriptionRequest();
			tuCReq.setDuration(updateRequest.getDuration());
			tuCReq.setNotifyCondition(updateRequest.getNotifyCondition() != null ? updateRequest
					.getNotifyCondition() : null);
			tuCReq.setRestriction(updateRequest.getRestriction());
			tuCReq.setThrottling(updateRequest.getThrottling());
			tuCReq.setSubscriptionId(id);
			final URI agentUri = subscriptionStorage.getAgentUri(id);
			new Thread() {
				@Override
				public void run() {
					logger.debug(agentWrapper
							.receiveReqFrmSubscriptionController(tuCReq,
									agentUri));
				}

			}.start();
		}
	}

	/**
	 * 
	 * @param subscribeRequest
	 * @param agentURi
	 * @param originalID
	 *            The Id of the NGSI-10 subscription arrived at the north bound
	 */
	private void sendSubscribeContextRequest(
			final SubscribeContextRequest subscribeRequest, final URI agentURi,
			final String originalID) {

		// SubscribeContextRequest subscribeRequestToSend = subscribeRequest;
		// if (sendPepCredentials) {
		// subscribeRequestToSend = this
		// .replacePepCredentials(subscribeRequest);
		// }

		new Thread() {
			@Override
			public void run() {
				SubscribeContextResponse scr = agentWrapper
						.receiveReqFrmSubscriptionController(subscribeRequest,
								agentURi);
				if (scr.getSubscribeError() == null
						|| scr.getSubscribeError().getStatusCode() == null
						|| scr.getSubscribeError().getStatusCode().getCode() == 200) {

					subscriptionStorage.saveOutgoingSubscription(
							subscribeRequest, scr.getSubscribeResponse()
									.getSubscriptionId(), originalID, agentURi,
							System.currentTimeMillis());
				}
			}

		}.start();
	}

	/**
	 * Given an id of an incoming subscription, this function retrieves all
	 * existing out- going subscriptions that were made on behalf of this
	 * incoming subscription.
	 * 
	 * The returned outgoing subscriptions are represented in a priority queue
	 * that is sorted by the URI of the agent.
	 */
	private PriorityQueue<Pair<ContextUniqueIdentifier, OutgoingSubscriptionWithInfo>> getExistingOutgoingSubs(
			String incomingSubId) {

		PriorityQueue<Pair<ContextUniqueIdentifier, OutgoingSubscriptionWithInfo>> existingSubscribedAgents = new PriorityQueue<Pair<ContextUniqueIdentifier, OutgoingSubscriptionWithInfo>>(
				2,
				new Comparator<Pair<ContextUniqueIdentifier, OutgoingSubscriptionWithInfo>>() {
					@Override
					public int compare(
							Pair<ContextUniqueIdentifier, OutgoingSubscriptionWithInfo> pair1,
							Pair<ContextUniqueIdentifier, OutgoingSubscriptionWithInfo> pair2) {
						return contextUniqueIdentifierComparator.compare(
								pair1.getLeft(), pair2.getLeft());
					}
				});

		List<String> outgoingIdList = subscriptionStorage
				.getOutIDs(incomingSubId);
		for (String s : outgoingIdList) {

			OutgoingSubscriptionWithInfo subscriptionWithMetadata = subscriptionStorage
					.getOutgoingSubscriptionWithMetadata(s);

			// URI agentUri = outgoingSub.getAgentUri(s);

			existingSubscribedAgents
					.add(new Pair<ContextUniqueIdentifier, OutgoingSubscriptionWithInfo>(
							new ContextUniqueIdentifier(
									subscriptionWithMetadata.getEntityIdList(),
									subscriptionWithMetadata.getAgentURI()),
							subscriptionWithMetadata));
		}

		return existingSubscribedAgents;

	}

	/**
	 * Given an list of context registration responses, this list is represented
	 * as a priority queue sorted by the reference ContextUniqueIdentifier in
	 * the responses.
	 * 
	 */
	private PriorityQueue<Pair<ContextUniqueIdentifier, ContextRegistrationResponse>> convertToPrioQueue(
			List<ContextRegistrationResponse> contextRegistrationResponseList) {

		PriorityQueue<Pair<ContextUniqueIdentifier, ContextRegistrationResponse>> newSubscribedAgent = new PriorityQueue<Pair<ContextUniqueIdentifier, ContextRegistrationResponse>>(
				2,
				new Comparator<Pair<ContextUniqueIdentifier, ContextRegistrationResponse>>() {
					@Override
					public int compare(
							Pair<ContextUniqueIdentifier, ContextRegistrationResponse> pair1,
							Pair<ContextUniqueIdentifier, ContextRegistrationResponse> pair2) {
						return contextUniqueIdentifierComparator.compare(
								pair1.getLeft(), pair2.getLeft());
					}
				});
		for (ContextRegistrationResponse contextRegistrationResponse : contextRegistrationResponseList) {

			newSubscribedAgent
					.add(new Pair<ContextUniqueIdentifier, ContextRegistrationResponse>(
							new ContextUniqueIdentifier(
									contextRegistrationResponse
											.getContextRegistration()),
							contextRegistrationResponse));
		}

		return newSubscribedAgent;

	}

	/*
	 * Contains the data that makes a context registration unique: the entity ID
	 * List and the providing application.
	 */
	private class ContextUniqueIdentifier {
		List<EntityId> entityIdList;
		URI providingApplication;

		ContextUniqueIdentifier(ContextRegistration contextRegistration) {
			entityIdList = contextRegistration.getListEntityId();
			providingApplication = contextRegistration
					.getProvidingApplication();
		}

		ContextUniqueIdentifier(List<EntityId> entityIdList,
				URI providingApplication) {
			this.entityIdList = entityIdList;
			this.providingApplication = providingApplication;
		}

		public List<EntityId> getEntityIdList() {
			return entityIdList;
		}

		public URI getProvidingApplication() {
			return providingApplication;
		}

		@Override
		public String toString() {
			return "ContextUniqueIdentifier [entityIdList=" + entityIdList
					+ ", providingApplication=" + providingApplication + "]";
		}

	}

	/*
	 * Comparator for ContextUniqueIdentifier
	 */
	private class ContextUniqueIdentifierComparator implements
			Comparator<ContextUniqueIdentifier> {
		@Override
		public int compare(ContextUniqueIdentifier o1,
				ContextUniqueIdentifier o2) {
			if (o1 == o2) {
				return 0;
			} else if (o1 == null) {
				return -1;
			} else if (o2 == null) {
				return 1;
			}

			if (o1.getProvidingApplication() != o2.getProvidingApplication()) {
				if (o1.getProvidingApplication() == null) {
					return -1;
				} else if (o2.getProvidingApplication() == null) {
					return 1;
				} else if (!o1
						.getProvidingApplication()
						.toString()
						.equalsIgnoreCase(
								o2.getProvidingApplication().toString())) {
					return o1
							.getProvidingApplication()
							.toString()
							.compareToIgnoreCase(
									o2.getProvidingApplication().toString());
				}
			}

			if (o1.getEntityIdList() != o2.getEntityIdList()) {
				List<EntityId> o1List = new ArrayList<EntityId>(
						o1.getEntityIdList());
				List<EntityId> o2List = new ArrayList<EntityId>(
						o2.getEntityIdList());
				EntityIdComparator entityIdComparator = new EntityIdComparator();
				Collections.sort(o1List, entityIdComparator);
				Collections.sort(o2List, entityIdComparator);
				int comp = new ListComparator<EntityId>(entityIdComparator)
						.compare(o1List, o2List);
				if (comp != 0) {
					return comp;
				}
			}

			return 0;
		}
	}

	private class EntityIdComparator implements Comparator<EntityId> {
		@Override
		public int compare(EntityId o1, EntityId o2) {
			if (o1 == o2) {
				return 0;
			} else if (o1 == null) {
				return -1;
			} else if (o2 == null) {
				return 1;
			}

			if (o1.getIsPattern() != o2.getIsPattern()) {
				if (!o1.getIsPattern()) {
					return -1;
				} else {
					return 1;
				}
			}

			if (o1.getId() != o2.getId()) {
				if (o1.getId() == null) {
					return -1;
				} else if (o2.getId() == null) {
					return 1;
				} else if (!o1.getId().equals(o2.getId())) {
					return o1.getId().compareTo(o2.getId());
				}
			}

			if (o1.getType() != o2.getType()) {
				if (o1.getType() == null) {
					return -1;
				} else if (o2.getType() == null) {
					return 1;
				} else if (!o1.getType().toString()
						.equalsIgnoreCase(o2.getType().toString())) {
					return o1.getType().toString()
							.compareToIgnoreCase(o2.getType().toString());
				}
			}

			return 0;
		}
	}

	private class ListComparator<T> implements Comparator<List<T>> {
		private final Comparator<T> comparator;

		ListComparator(Comparator<T> comparator) {
			this.comparator = comparator;

		}

		/**
		 * Compare two lists. It returns 0 if the two objects are the same list,
		 * or both are null, or both have the same objects. How the elements are
		 * stored in the list matter, hence if you want to check regardless the
		 * order of the elements, submit the lists already sorted.
		 */
		@Override
		public int compare(List<T> o1, List<T> o2) {
			if (o1 != o2) {
				if (o1 == null) {
					return -1;
				} else if (o2 == null) {
					return 1;
				} else {

					if (o1.size() != o2.size()) {
					}
					// Collections.sort(o1,comparator);
					// Collections.sort(o2,comparator);
					Iterator<T> bigIter;
					Iterator<T> smallIter;
					int factor = 1;
					if (o1.size() > o2.size()) {
						bigIter = o1.iterator();
						smallIter = o2.iterator();
						factor = -1;
					} else {
						smallIter = o1.iterator();
						bigIter = o2.iterator();
					}
					while (smallIter.hasNext()) {
						int comp = comparator.compare(smallIter.next(),
								bigIter.next());
						if (comp != 0) {
							return comp * factor;
						}
					}
					if (bigIter.hasNext()) {
						return o1.size() - o2.size();
					}
				}
			}
			return 0;
		}
	}

}
