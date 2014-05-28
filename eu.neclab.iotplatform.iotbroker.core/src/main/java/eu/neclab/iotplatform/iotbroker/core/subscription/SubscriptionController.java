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
package eu.neclab.iotplatform.iotbroker.core.subscription;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Timer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import eu.neclab.iotplatform.iotbroker.commons.EntityIDMatcher;
import eu.neclab.iotplatform.iotbroker.commons.GenerateUniqueID;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.ResultFilterInterface;
import eu.neclab.iotplatform.iotbroker.core.IotBrokerCore;
import eu.neclab.iotplatform.iotbroker.core.QueryResponseMerger;
import eu.neclab.iotplatform.iotbroker.core.data.Pair;
import eu.neclab.iotplatform.iotbroker.storage.AvailabilitySubscriptionInterface;
import eu.neclab.iotplatform.iotbroker.storage.IncomingSubscriptionInterface;
import eu.neclab.iotplatform.iotbroker.storage.LinkSubscriptionAvailabilityInterface;
import eu.neclab.iotplatform.iotbroker.storage.LinkSubscriptionInterface;
import eu.neclab.iotplatform.iotbroker.storage.OutgoingSubscriptionInterface;
import eu.neclab.iotplatform.ngsi.api.datamodel.Code;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElement;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElementResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.ReasonPhrase;
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
 * The subscription controller is the central component for handling subscriptions
 * in the IoT Broker. Any subscription-related message
 * received by the {@link IotBrokerCore} instance will be directly forwarded
 * to the subscription controller.
 */
public class SubscriptionController {

	private static Logger logger = Logger
			.getLogger(SubscriptionController.class);

	/**
	 *  Pointer to the component from which notifications will be received.
	 */
	private NorthBoundWrapper northBoundWrapper;

	/**
	 * Pointer to the component responsible to communicate with the
	 * NGSI 9 server for discovery and availability subscriptions.
	 */
	private ConfManWrapper confManWrapper;

	/**
	 * Pointer to the component responsible to communicate with
	 * IoT Agents via NGSI 10.
	 */
	protected AgentWrapper agentWrapper;


	private SubscribeContextAvailabilityResponse subscribeContextAvailability;
	private String idSubRequest = null;

	/*
	 * Used for storage of subscription-related information.
	 */
	private AvailabilitySubscriptionInterface availabilitySub;
	private IncomingSubscriptionInterface incomingSub;
	private OutgoingSubscriptionInterface outgoingSub;
	private LinkSubscriptionAvailabilityInterface linkAvSub;
	private LinkSubscriptionInterface linkSub;

	/**
	 * Used to generate subscription IDs.
	 */
	private final GenerateUniqueID genUniqueID;

	/**
	 *  Timer to control the duration of subscriptions.
	 */
	private final Timer timer = new Timer("Subs Duration Timer");

	/**
	 * Used for handling NGSI associations.
	 */
	private final AssociationsUtil associationUtil = new AssociationsUtil();

	private @Value("${default_throttling}")	long defaultThrottling;
	private @Value("${default_duration}") long defaultDuration;

	/**
	 * Used to filter notifications.
	 */
	@Autowired
	private ResultFilterInterface resultFilter = null;


	/**
	 * @return The result filter used for filtering notifications.
	 */
	public ResultFilterInterface getResultFilter(){
		return resultFilter;
	}

	/**
	 * Assigns the result filter used for filtering notifications.
	 */
	public void setResultFilter(ResultFilterInterface resultFilter){
		this.resultFilter=resultFilter;
	}


	/**
	 * @return Pointer to the component used for receiving notifications.
	 */
	public NorthBoundWrapper getNorthBoundWrapper() {
		return northBoundWrapper;
	}

	/**
	 * A key-value store for storing subscription-related information.
	 */
	private final Map<String, SubscriptionData> subscriptionStore = Collections
			.synchronizedMap(new HashMap<String, SubscriptionData>());


	/**
	 * Creates a new instance.
	 */
	public SubscriptionController() {
		super();
		genUniqueID = new GenerateUniqueID();
	}

	/**
	 * @return A pointer to the key-value store used for storing subscription-
	 * related information.
	 */
	public Map<String, SubscriptionData> getSubscriptionStore() {
		return subscriptionStore;
	}

	/**
	 * @return Pointer to the incoming subscription storage.
	 */
	public IncomingSubscriptionInterface getIncomingSub() {
		return incomingSub;
	}

	/**
	 * Sets the pointer to the incoming subscription storage.
	 */
	public void setIncomingSub(IncomingSubscriptionInterface incomingSub) {
		this.incomingSub = incomingSub;
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
	 * @return Pointer to the outgoing subscription storage.
	 */
	public OutgoingSubscriptionInterface getOutgoingSub() {
		return outgoingSub;
	}

	/**
	 * Sets the pointer to the outgoing subscription storage.
	 */
	public void setOutgoingSub(OutgoingSubscriptionInterface outgoingSub) {
		this.outgoingSub = outgoingSub;
	}

	/**
	 * @return Pointer to the storage for links between incoming
	 * subscriptions
	 * and availability subscriptions.
	 */
	public LinkSubscriptionAvailabilityInterface getLinkAvSub() {
		return linkAvSub;
	}

	/**
	 * Sets the pointer to the storage for links between incoming
	 * subscriptions
	 * and availability subscriptions.
	 */
	public void setLinkAvSub(LinkSubscriptionAvailabilityInterface linkAvSub) {
		this.linkAvSub = linkAvSub;
	}

	/**
	 * @return Pointer to the storage for links between incoming
	 * subscriptions
	 * and outgoing subscriptions.
	 */
	public LinkSubscriptionInterface getLinkSub() {
		return linkSub;
	}

	/**
	 * Sets the pointer to the storage for links between incoming
	 * subscriptions
	 * and outgoing subscriptions.
	 */
	public void setLinkSub(LinkSubscriptionInterface linkSub) {
		this.linkSub = linkSub;
	}

	/**
	 * Sets the pointer to the component from which notifications
	 * will be received.
	 */
	public void setNorthBoundWrapper(NorthBoundWrapper northBoundWrapper) {
		this.northBoundWrapper = northBoundWrapper;
	}

	/**
	 * Sets the pointer to the component for communication with
	 * the NGSI 9 Server.
	 */
	public void setConfManWrapper(ConfManWrapper confManWrapper) {
		this.confManWrapper = confManWrapper;
	}

	/**
	 * Sets the pointer to the component for communication with
	 * IoT Agents
	 */
	public void setAgentWrapper(AgentWrapper agentWrapper) {
		this.agentWrapper = agentWrapper;
	}

	/**
	 * @return The default throttling value.
	 */
	public long getDefaultThrottling() {
		return defaultThrottling;
	}

	/**
	 * @return The URL where agents should send there notifications to. This
	 * is the address where the NGSI RESTful interface is reachable.
	 */
	public String getRefURl() {
		String ref = null;
		try {
			ref = "http://" + InetAddress.getLocalHost().getHostAddress() + ":"
					+ System.getProperty("tomcat.init.port") + "/ngsi10";

		} catch (UnknownHostException e) {
			logger.error("Unknown Host",e);
		}
		return ref;
	}

	/**
	 * The function is by the northbound wrapper 
	 *  when a new NGSI 10 subscription arrives.
	 *
	 * @param scReq
	 *  The NGSI 10 SubscribeContextRequest.
	 * @return
	 *  The NGSI 10 SusbcribeContextResponse.
	 */
	public SubscribeContextResponse receiveReqFrmNorthBoundWrapper(
			final SubscribeContextRequest scReq) {

		SubscribeContextResponse scRes = null;
		logger.debug("DEFAULT_THROTTLING: " + defaultThrottling);
		logger.debug("DEFAULT_DURATION: " + defaultDuration);
		
		/*
		 * We retrieve the address where notifications can be sent to the 
		 * IoT Broker and also generate a subscription id to return in the
		 * subscribe response.
		 */
		
		String ref = getRefURl();

		
		idSubRequest = genUniqueID.getNextUniqueId();
		logger.debug("SUBSCRIPTION ID  = " + idSubRequest);
		
		
		/*
		 * Here the incoming subscription is already stored in the 
		 * persistent storage.
		 * 
		 * TODO this is probably wrong, as it should only be stored
		 * if the subscription is successful.
		 */
		incomingSub.saveIncomingSubscription(scReq, idSubRequest,
				System.currentTimeMillis());

		/*
		 * After that, we create a request for retrieving the relevant 
		 * data sources and associations for the subscription.
		 */
		
		SubscribeContextAvailabilityRequest scaReq = new SubscribeContextAvailabilityRequest(
				scReq.getAllEntity(), scReq.getAttributeList(), ref, scReq
				.getDuration().toString(), null, scReq.getRestriction());
		logger.debug("Sending SubscribeContextAvailabilityRequest to ConfManWrapper:"
				+ scaReq.toString());

		/*
		 * We also create a task that will automatically unsubscribe again as soon
		 * as the subscription has expired. 
		 */
		
		UnsubscribeTask task = new UnsubscribeTask(idSubRequest, this);
		logger.debug("Subscription time: "
				+ scReq.getDuration().getTimeInMillis(new GregorianCalendar()));
		logger.debug("Subscription time: " + scReq.getDuration().getSeconds());
		logger.debug("Subscription time: " + scReq.getDuration().getMinutes());
		logger.debug("Subscription time: " + scReq.getDuration().toString());
		
		/*
		 * Now we create a container where the relevant information about this subscription
		 * is packed together. This relevant data consists of
		 * - when the subscription is initiated
		 * - a link to the unsubscribe task 
		 */
		
		SubscriptionData subData = new SubscriptionData();
		subData.setStartTime(associationUtil.currentTime());
		subData.setUnsubscribeTask(task);
		
		
		/*
		 * The unsubscribe task is now submitted to the timer. 
		 * In case no duration is given, a default
		 * duration is used.
		 */
		if (scReq.getDuration() != null) {
			timer.schedule(task,scReq.getDuration().getTimeInMillis(new GregorianCalendar()));
		} else {
			timer.schedule(task, new Date(System.currentTimeMillis()),defaultDuration);
			scaReq.setDuration(associationUtil.convertToDuration(Long.toString(defaultDuration)).toString());
		}

		/*
		 * The subscription data is now put into the persistent storage, where the 
		 * id of the subscription (generated before) is used as the key.
		 */
		subscriptionStore.put(idSubRequest, subData);
		
		/*
		 * Now it is time to instruct the config management wrapper to communicate
		 * with the config manager using the request for retrieving the relevant 
		 * data sources and associations that has been created above.
		 */
		subscribeContextAvailability = confManWrapper.receiveReqFrmSubscriptionController(scaReq);
		
		/*
		 * The response from the wrapper is analyzed, and if it is not positive the 
		 * function is aborted and an error is returned.
		 */
		if (subscribeContextAvailability.getErrorcode() != null
				&& subscribeContextAvailability.getErrorcode().getCode() != 200) {
			scRes = new SubscribeContextResponse(null, new SubscribeError(null,
					new StatusCode(500,
							ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
							null)));
			return scRes;
		} else {
			
			/*
			 * If the answer from the wrapper is positive, then
			 * the subscribe response is also positive. It receives the
			 * subscription id generated before. 
			 */
			
			scRes = new SubscribeContextResponse(new SubscribeResponse(
					idSubRequest, scReq.getDuration(), scReq.getThrottling()),
					new SubscribeError(null, new StatusCode(Code.OK_200
							.getCode(), ReasonPhrase.OK_200.toString(), null)));
			logger.debug("Sending SubscribeContextResponse to NorthBoundWrapper:"
					+ scRes.toString());
			
			 /* 
			 * Before the subscribe response is actually returned, the subscription
			 * is also represented in the storage:
			 * - the availability subscription is saved under its id
			 * - a link between the availability subscription and the incoming subscription
			 * is stored
			 */
			
			availabilitySub.saveAvalabilitySubscription(subscribeContextAvailability,
					subscribeContextAvailability.getSubscribeId());
			
			linkAvSub.insert(idSubRequest, subscribeContextAvailability.getSubscribeId());

			/*
			 * No finally the successful subscription response is returned.
			 */
			
			return scRes;
		}

	}

	/**
	 * Updates an existing subscription.
	 *
	 * @param uCSreq
	 *  The NGSI 10 UpdateContextSubscriptionRequest.
	 * @return
	 *  The NGSI 10 UpdateContextSubscriptionResponse.
	 */
	public UpdateContextSubscriptionResponse receiveReqFrmNorthBoundWrapper(
			UpdateContextSubscriptionRequest uCSreq) {
		UpdateContextSubscriptionResponse uCSres = null;
		SubscribeContextRequest sCReq = incomingSub
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
				sCReq.getEntityIdList(), sCReq.getAttributeList(), uCSreq
				.getDuration().toString(), subsAvailId,
				uCSreq.getRestriction());
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
				logger.error("Time Schedule Error" ,e);
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
				uCAReq.setDuration(associationUtil.convertToDuration(
						Long.toString(defaultDuration)).toString());

			} catch (Exception e) {
				logger.error("Time Schedule Error" ,e);
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
	 * @param subscriptionID The identifier of the subscription to cancel.
	 */
	public void unsubscribeOperation(String subscriptionID) {


		List<String> lSubscriptionIDOriginal = linkAvSub.getAvailIDs(subscriptionID);

		logger.debug(lSubscriptionIDOriginal);
		if (lSubscriptionIDOriginal.size() == 1) {
			String tmp = lSubscriptionIDOriginal.get(0);
			availabilitySub.deleteAvalabilitySubscription(tmp);
			linkAvSub.delete(subscriptionID, tmp);
			incomingSub.deleteIncomingSubscription(subscriptionID);
			List<String> lSubscriptionIDAgent = linkSub.getOutIDs(subscriptionID);
			for (String subscriptionAgent : lSubscriptionIDAgent) {
				linkSub.delete(subscriptionID, subscriptionAgent);
				outgoingSub.deleteOutgoingSubscription(subscriptionAgent);
			}
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
	 *  The NGSI 10 UnsubscribeContextRequest.
	 * @return
	 *  The NGSI 10 UnsubscribeContextResponse.
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
			incomingSub.deleteIncomingSubscription(uCReq.getSubscriptionId());
			List<String> lSubscriptionIDAgent = linkSub.getOutIDs(uCReq
					.getSubscriptionId());
			for (String subscriptionAgent : lSubscriptionIDAgent) {
				final String tmp1 = subscriptionAgent;
				final URI agentURi = outgoingSub
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
				linkSub.delete(uCReq.getSubscriptionId(), subscriptionAgent);
				outgoingSub.deleteOutgoingSubscription(subscriptionAgent);
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
	 * Return a List of ContextElementResponse items that is the result of applying
	 * the given list of associations to the give list of ContextElementResponse items. 
	 * 
	 */
	private List<ContextElementResponse> modifyEntityAttributeBasedAssociation(
			List<AssociationDS> assocList, List<ContextElementResponse> lCres) {

		
		/*
		 * If the list of associations is empty, the originally given response list is
		 * immediately returned.
		 * 
		 */

		if (!assocList.isEmpty()) {
			
			/*
			 * Otherwise we initialize the list of context element responses
			 * to return and then go through all pairs of 
			 * association and context element response 
			 */
			
			List<ContextElementResponse> resultingContextElRespList = new ArrayList<ContextElementResponse>();
			for (ContextElementResponse ceResp : lCres) {
				for (AssociationDS aDS : assocList) {
					
					logger.debug("SubscriptionController: Matching association " + 
					aDS.toString()+
					"against context element response " +
					ceResp.toString());
					
					/*
					 * For each such pair we first look whether the source entity of the
					 * association matches the entity id of the context element response.
					 * 
					 * If not, then nothing is done
					 * 
					 */
					
					if (EntityIDMatcher.matcher(ceResp.getContextElement()
							.getEntityId(), aDS.getSourceEA().getEntity())) {
						
						
						/*
						 * If the entities match, then we check whether the 
						 * given context element response specifies a non-empty
						 * attribute domain name.
						 * 
						 */
						
						boolean attributeDomainNameExists=false;
						if(ceResp.getContextElement().getAttributeDomainName()!= null){
							if(!"".equals(ceResp.getContextElement().getAttributeDomainName())){
								attributeDomainNameExists=true;
								
								/*
								 * If the attribute domain name is specified, then we also check
								 * if association is an attribute association. 
								 */
								
								if (!"".equals(aDS.getSourceEA().getEntityAttribute())) {
									
									/*
									 * And if it is an attribute association, then we check if the
									 * attribute domain name of the context element matches with the
									 * source attribute of the association.
									 * 
									 * If it does not match, nothing happens.
									 * 
									 * If it matches, then the association is applicable and we translate
									 * the given context element response into one where the entity id
									 * is replaced with the target entity id of the association. This context 
									 * element response is then added to the list of responses to return. 
									 * 
									 */

									if(aDS.getSourceEA().getEntityAttribute().equals(ceResp.getContextElement().getAttributeDomainName())){
										ContextElement cETmp = new ContextElement(aDS.getTargetEA().getEntity(),aDS.getTargetEA().getEntityAttribute(),	ceResp.getContextElement().getContextAttributeList(), ceResp.getContextElement().getDomainMetadata());
										ContextElementResponse cEresTmp = new ContextElementResponse(cETmp, ceResp.getStatusCode());
										resultingContextElRespList.add(cEresTmp);
										logger.debug("SubscriptionController: Successfully applied association, created the context element " + cETmp.toString() );
									}
								}else{
									
									/*
									 * If the association is an entity association, then it is anyway
									 * applicable to the context element. The same procedure as above is applied. 
									 * 
									 */
									
									ContextElement cETmp = new ContextElement(aDS.getTargetEA().getEntity(),ceResp.getContextElement().getAttributeDomainName(),	ceResp.getContextElement().getContextAttributeList(), ceResp.getContextElement().getDomainMetadata());
									ContextElementResponse cEresTmp = new ContextElementResponse(cETmp, ceResp.getStatusCode());
									
									logger.debug("SubscriptionController: Successfully applied association, created the context element " + cETmp.toString() );
									resultingContextElRespList.add(cEresTmp);
								}
							}
						}
						
						/*
						 * In case there is no attribute domain name specified in the context element responsem
						 * we again check whether the association is an attribute association. 
						 * 
						 */						
						if(attributeDomainNameExists==false){
							if (!"".equals(aDS.getSourceEA().getEntityAttribute())) {
								
								/*
								 * If it is an attribute association, we need to find the attribute values
								 * that can be translated using this association.
								 * 
								 * For this, we run through all attributes specified in the 
								 * context element response, and when we found one that matches with
								 * the source attribute of the association we apply the
								 * association and create a context element response with the target
								 * entity and the target attribute specified in the association.
								 */
								
								for (ContextAttribute ca : ceResp.getContextElement().getContextAttributeList()) {
									if (ca.getName().equals(aDS.getSourceEA().getEntityAttribute())) {
										List<ContextAttribute> lCaTmp = new ArrayList<ContextAttribute>();
										ca.setName(aDS.getTargetEA().getEntityAttribute());
										lCaTmp.add(ca);
										ContextElement cETmp = new ContextElement(aDS.getTargetEA().getEntity(),ceResp.getContextElement().getAttributeDomainName(),	lCaTmp, ceResp.getContextElement().getDomainMetadata());
										ContextElementResponse cEresTmp = new ContextElementResponse(cETmp, ceResp.getStatusCode());
										resultingContextElRespList.add(cEresTmp);
										logger.debug("SubscriptionController: Successfully applied association, created the context element " + cETmp.toString() );
									}
								}
							} else {
								
								/*
								 * The final case is where there is not attribute domain name
								 * specified and the association is an entity association. In 
								 * This case the association is applicable and we can translate 
								 * the entity id and keep all attribute names.
								 */

								ContextElement cETmp = new ContextElement(aDS
										.getTargetEA().getEntity(), ceResp
										.getContextElement()
										.getAttributeDomainName(), ceResp
										.getContextElement()
										.getContextAttributeList(), ceResp
										.getContextElement().getDomainMetadata());
								ContextElementResponse cEresTmp = new ContextElementResponse(
										cETmp, ceResp.getStatusCode());
								resultingContextElRespList.add(cEresTmp);
								logger.debug("SubscriptionController: Successfully applied association, created the context element " + cETmp.toString() );
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
	 *  The NGSI 10 NotifyContextRequest.
	 * @return
	 *  The NGSI 10 NotifyContextResponse.
	 */
	public NotifyContextResponse receiveReqFrmAgentWrapper(
			NotifyContextRequest ncReq) {
		
		logger.debug("SubscriptionController: Received the following notify context request:" 
		+ ncReq.toString()+"/n"+
		"SubscriptionID is: "+ ncReq.getSubscriptionId()
				);		

		/*
		 *  Initialize the resulting notification request that will be passed to the
		 *  northbound wrapper
		 */

		NotifyContextRequest ncReqtoNorthBoundWrapper = new NotifyContextRequest();
		
		/*
		 * Retrieves the list of incoming subscription ids that are relevant for 
		 * the received notification (note that the notification is the result of 
		 * an outgoing subscription).
		 * 
		 */
		
		List<String> inIDs = linkSub.getInIDs(ncReq.getSubscriptionId());
		
		
		/*
		 * It is expected that exactly one incoming subscription id will be found.
		 * If this is not the case, then the function is aborted and an error is
		 * returned.
		 * 
		 * Otherwise, the function continues.
		 * 
		 */
		if (inIDs == null || inIDs.size() != 1) {
			logger.debug("SubscriptionController: Aborting, because did not find exactly one incoming" +
					"subscription, but found "+inIDs.toString());
			return new NotifyContextResponse(new StatusCode(470,
					ReasonPhrase.SUBSCRIPTIONIDNOTFOUND_470.toString(), null));
		} else {
			logger.debug("SubscriptionController: found incoming subscription ID: "+ inIDs.get(0));
			

			/* 
			 * We also retrieve the original incoming subscription message from the storage.
			 * 
			 */
			SubscribeContextRequest inSubReq=incomingSub.getIncomingSubscription(inIDs.get(0));
			
			if(inSubReq == null)
			{
				logger.error("Incoming subscription id found, but no incoming subscription found. Aborting " +
						"the notification process.");
				return null;
			}
					
			logger.debug("SubscriptionController: Identified the original incoming " +
					"subscription request: "+inSubReq.toString());


			
			
			/*
			 * Initialize the list of associations to apply later, and initialize
			 * the list of ContextElementResponses to return then.
			 * 
			 */
			List<String> listAssoc = null;
			List<ContextElementResponse> lCERes = null;
			
			/*
			 * Now we retrieve the availability subscription id. This is the availability 
			 * subscription that was made on behalf of the identified incoming subscription.
			 */
			
			List<String> availId = linkAvSub.getAvailIDs(inIDs.get(0));

			/*
			 * It is again assumed that exactly one availability subscription id is has been found.
			 * If not, then the function aborts and returns nothing. 
			 * 
			 * Otherwise the list of associations that have been stored with this availability subscription
			 * is retrieved. These are the associations that are potentially applicable
			 * for this notification.
			 */
			
			if (availId.size() == 1) {
				logger.debug("SubscriptionController: found the following availability subscr ID:" 
						+ availId.get(0));				
				listAssoc = availabilitySub.getListOfAssociations(availId.get(0));
				logger.debug("SusbcriptionController: The associations are:" + listAssoc.toString());
			} else {
				logger.error("SubscriptionController: found wrong number of availability subscriptions, aborting.");
				return null;
			}

			
			/*
			 *  Now the associations are applied if there are any. 
			 *  Applying the associations is done by the modifyEntityAttributeBasedAssociation 
			 *  function.
			 *  If there are no associations, the contextelementresponse from the notification
			 *  are taken as they are.
			 *  
			 */
			
			if (listAssoc != null && !listAssoc.isEmpty()) {
				logger.debug("SubscriptionController: Applying associations");
				
				lCERes =modifyEntityAttributeBasedAssociation(associationUtil.convertToAssociationDS(listAssoc.get(0)),ncReq.getContextElementResponseList());
				
				/*
				 * We merge the results from the associations with the original 
				 * context element reponse list 
				 */
				
				lCERes.addAll(ncReq.getContextElementResponseList());
				
				logger.debug("SubscriptionController: Context Element Responses after applying assoc: " + lCERes.toString());

			}else{
				lCERes = ncReq.getContextElementResponseList();
				logger.debug("SusbcriptionController: Found no associations");
			}

			
			
			if (lCERes != null ) {
				//this can actually never be null!
				
				
				
				/*
				 * Now, after having applied the associations, it is time to apply 
				 * the result filter in order to remove everything from the notification that has
				 * not been queried.
				 */
				
				/*
				 * If a result filter is available, we apply it to the 
				 * notification.
				 * 
				 */
				
//				BundleContext bc = FrameworkUtil.getBundle(ResultFilterInterface.class).getBundleContext();
//				ServiceReference ref = bc.getServiceReference(ResultFilterInterface.class.getName());

				if(resultFilter != null){
					
					logger.debug("SubscriptionController: found resultFilter implementation.");
					
					/*
					 * As the resultfilter is defined for queries, we have to 
					 * convert the subscription request to a query request
					 */				
					
					List<QueryContextRequest>lqcReq_forfilter =new ArrayList<QueryContextRequest>();
					QueryContextRequest tmp_request = new QueryContextRequest(inSubReq.getEntityIdList(),inSubReq.getAttributeList(),inSubReq.getRestriction());
					lqcReq_forfilter.add(tmp_request);
					
					
					logger.debug("SubscriptionController: calling the resultfilter");				

					List<QueryContextResponse> lqcRes_fromfilter =resultFilter.filterResult(lCERes, lqcReq_forfilter);
					
					/*
					 * We receive back a query context response from which we 
					 * take out the context element responses
					 */
					lCERes = lqcRes_fromfilter.get(0).getListContextElementResponse();										
					logger.debug("SubscriptionController: filtered result: " + lCERes.toString());


				}
				else{
					logger.debug("SubscriptionController: found no result filter; using the unfiltered result.");
				}

				
				/*
				 * Now we create a queryContextResponse with our
				 * list of context element responses. We use the QueryResponseMerger
				 * to format the response in a nicer way (eliminating duplicate entities 
				 * with the same attributedomain).
				 * 
				 */
				
				QueryContextResponse qCRes_forMerger = new QueryContextResponse();
				qCRes_forMerger.setContextResponseList(lCERes);				
				QueryResponseMerger qRM = new QueryResponseMerger(null);
				qRM.put(qCRes_forMerger);
				qCRes_forMerger = qRM.get();
				
				logger.debug("SubscriptionController: Response list after applying merger:" + qCRes_forMerger.getListContextElementResponse().toString());

				
				/*
				 * We retrieve the subscriptionData for the incoming subscription
				 * of this notification.
				 */

				SubscriptionData subscriptionData = subscriptionStore
						.get(inIDs.get(0));
								
				List<ContextElementResponse> notificationQueue;
				
				/*
				 * This subscription data contains a queue of unsent notifications (or 
				 * otherwise we create it here) 
				 * 
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
				 * they will later be sent to the application that has originally
				 * issued the subscription.
				 */
				
				for (int i = 0; i < qCRes_forMerger.getListContextElementResponse()
						.size(); i++) {
					logger.info("###########################################");
					logger.info(qCRes_forMerger.getListContextElementResponse().get(i));

					notificationQueue.add(qCRes_forMerger.getListContextElementResponse()
							.get(i));
				}
				
				/*
				 * After that, the notification queue is put back into the
				 * subscription data, which is then put back into the storage.
				 */
				subscriptionData.setContextResponseQueue(notificationQueue);
				subscriptionStore.put(inIDs.get(0),subscriptionData);
				logger.debug("Adding to subdata subid:"	+ inIDs.get(0) + " : " + subscriptionData);
			}

		}

		/*
		 * finally the function can return with a positive status code.
		 */		
		return new NotifyContextResponse(new StatusCode(200,ReasonPhrase.OK_200.toString(), null));

	}

	/**
	 * Instructs the SubscriptionController to process an Availability Notification and
	 * additionally passes it a list of associations which determine how the data from the
	 * agents is to be interpreted.
	 *
	 * @param notifyContextAvailabilityRequest
	 *  The NGSI 9 NotifyContextAvailabilityRequest.
	 * @param transitiveList
	 *  List of associations explaining how to interpret the results.
	 * @return
	 *  The NGSI 9 NotifyContextAvailabilityResponse.
	 */
	public NotifyContextAvailabilityResponse receiveReqFrmConfManWrapper(
			NotifyContextAvailabilityRequest notifyContextAvailabilityRequest,
			List<AssociationDS> transitiveList) {

		

		logger.debug("Received NotifyContextAvailabilityRequest:" + notifyContextAvailabilityRequest);
		String subscriptionId = "";
		
		/*
		 *  First, the storage is updated with the new availability notification and the new association list. 
		 */
		String association = associationUtil.convertAssociationToString(transitiveList);

		logger.debug("association:" + association);
				
		
		availabilitySub.updateAvalabilitySubscription(notifyContextAvailabilityRequest, association,
				notifyContextAvailabilityRequest.getSubscribeId());
		
		/*
		 *  Now the id of the incoming subscription corresponding to this availability 
		 *  notification is retrieved from storage.
		 */		
		List<String> inIDs = linkAvSub.getInIDs(notifyContextAvailabilityRequest.getSubscribeId());
		if (inIDs.size() == 1) {
			subscriptionId = inIDs.get(0);
		}

		/*
		 * We retrieve a sorted list (by agent URI) of all outgoing subscriptions made on behalf
		 * of this incoming subscription. Furthermore, we retrieve a sorted (again by URI) list that 
		 * contains all subscriptions that are recommended by this availability notification. 
		 * 
		 */
		
		PriorityQueue<Pair<URI, SubscribeContextRequest>> existingSubscribedAgentQ = getExistingOutgoingSubs(subscriptionId);
		PriorityQueue<Pair<URI, ContextRegistrationResponse>> newProposedSubscribedAgentQ = convertToPrioQueue(notifyContextAvailabilityRequest
				.getContextRegistrationResponseList());
		
		/*
		 * Here I write what !should! be done; the below code seems not to do that properly.
		 * 
		 * What we want to achieve is that
		 * - existing subscriptions that are not needed anymore are cancelled
		 * - new subscriptions that are needed are made
		 * 
		 * How this can be achieved with the prio queues is:
		 * (assuming they always have the lexicogr. largest element on top)
		 * 
		 * - While the existing subscr queue is not empty
		 * -- if the recomm.queue is empty, break
		 * -- if the top element of the recom queue is lexicog. larger than the top 
		 *    element of the exist. queue:
		 * ---- make subscription to top element of recom queue
		 * ---- remove first element of recom queue
		 * ---- continue
		 * -- if both top elements are equal
		 * ---- if necessary, update the subscription to this agent
		 * ---- remove top element of both queues and continue
		 * -- if the top element of the recom queue is lexicog. smaller than the top 
		 *    element of the exist. queue: 
		 * ---- cancel subscription corresp. to top element of exist. queue
		 * ---- remove top element of exist. queue
		 * ---- continue
		 * 
		 * After the loop, one of the queues is empty and the remaining subscriptions
		 * from the resp. other queue are issued or cancelled.
		 * 
		 * To see that this is correct, observe that
		 * 1) an action performed for each subscription in any of the queues exactly once, because
		 * whenever an action is performed for a queue element, the element is removed afterwards. 
		 * 2) Conversely, when elements are removed an action is always performed.
		 * 3) As always the larger element is removed, for all pairs of equal URIs we perform the
		 * appropriate subscription update action.
		 * 
		 * 
		 */
		
		Pair<URI, SubscribeContextRequest> exSATmp = null;
		Pair<URI, ContextRegistrationResponse> npSATmp = null;
		if (existingSubscribedAgentQ.isEmpty()) {			

			while (existingSubscribedAgentQ.isEmpty()
					&& newProposedSubscribedAgentQ.isEmpty()) {
				Pair<URI, SubscribeContextRequest> exSA = existingSubscribedAgentQ
						.peek();
				Pair<URI, ContextRegistrationResponse> npSA = newProposedSubscribedAgentQ
						.peek();

				int compareResult = exSA.getLeft().toString()
						.compareToIgnoreCase(npSA.getLeft().toString());
				logger.debug("PEEK exist " + exSA.getLeft().toString()
						+ " : PEEK new" + npSA.getLeft().toString() + " : "
						+ compareResult);
				if (compareResult == 0) {

					npSATmp = newProposedSubscribedAgentQ.poll();
					logger.debug("POLL new" + npSATmp.getLeft().toString());
					sendSubscribeContextRequest(
							createSubscribeContextRequestFromCRRes(
									npSATmp.getRight(), subscriptionId),
									npSATmp.getLeft(), subscriptionId);
					exSATmp = existingSubscribedAgentQ.poll();
					logger.debug("POLL exist" + exSATmp.getLeft().toString());
					sendUnsubscribeContextRequest(exSATmp.getRight(),
							exSATmp.getLeft(), subscriptionId);
				} else if (compareResult > 0) {
					npSATmp = newProposedSubscribedAgentQ.poll();
					logger.debug("POLL new" + npSATmp.getLeft().toString());

					sendSubscribeContextRequest(
							createSubscribeContextRequestFromCRRes(
									npSATmp.getRight(), subscriptionId),
									npSATmp.getLeft(), subscriptionId);
				} else if (compareResult < 0) {
					exSATmp = existingSubscribedAgentQ.poll();
					logger.debug("POLL exist" + exSATmp.getLeft().toString());

					sendUnsubscribeContextRequest(exSATmp.getRight(),
							exSATmp.getLeft(), subscriptionId);
				}
			}
			while (!existingSubscribedAgentQ.isEmpty()) {
				exSATmp = existingSubscribedAgentQ.poll();
				logger.debug("POLL exist" + exSATmp.getLeft().toString()
						+ " :q " + existingSubscribedAgentQ.size());
				sendUnsubscribeContextRequest(exSATmp.getRight(),
						exSATmp.getLeft(), subscriptionId);
			}
			while (!newProposedSubscribedAgentQ.isEmpty()) {
				npSATmp = newProposedSubscribedAgentQ.poll();
				logger.debug("POLL new" + npSATmp.getLeft().toString() + " :q "
						+ newProposedSubscribedAgentQ.size());
				sendSubscribeContextRequest(
						createSubscribeContextRequestFromCRRes(
								npSATmp.getRight(), subscriptionId),
								npSATmp.getLeft(), subscriptionId);
			}
		} else {
			while (newProposedSubscribedAgentQ.isEmpty()) {
				npSATmp = newProposedSubscribedAgentQ.poll();
				logger.debug("POLL new" + npSATmp.getLeft().toString() + " :q "
						+ newProposedSubscribedAgentQ.size());
				sendSubscribeContextRequest(
						createSubscribeContextRequestFromCRRes(
								npSATmp.getRight(), subscriptionId),
								npSATmp.getLeft(), subscriptionId);
			}
		}

		//TODO check why there is no proper return statement.
		return null;
	}

	private SubscribeContextRequest createSubscribeContextRequestFromCRRes(
			ContextRegistrationResponse contextRegistrationResponse, String originalId) {

		SubscribeContextRequest sCReq = incomingSub
				.getIncomingSubscription(originalId);
		sCReq.setEntityIdList(contextRegistrationResponse.getContextRegistration().getListEntityId());
		List<String> attribute = new ArrayList<String>();
		for (ContextRegistrationAttribute cRAttributeName : contextRegistrationResponse
				.getContextRegistration().getContextRegistrationAttribute()) {
			attribute.add(cRAttributeName.getName());
		}
		sCReq.setAttributeList(attribute);
		sCReq.setReference(getRefURl());
		SubscriptionData subdata = subscriptionStore.get(originalId);

		logger.debug("Current Duration: " + sCReq.getDuration().toString());
		sCReq.setDuration(associationUtil.newDuration(sCReq.getDuration(),
				associationUtil.currentTime().getTime()
				- subdata.getStartTime().getTime()));
		logger.debug("New Duration: " + sCReq.getDuration().toString());
		return sCReq;
	}

	private void sendUnsubscribeContextRequest(SubscribeContextRequest sCReq,
			final URI agentURi, String originalId) {

		String outGoingID = outgoingSub.getOutID(sCReq.toString(), agentURi);
		outgoingSub.deleteOutgoingSubscription(outGoingID);
		linkSub.delete(originalId, outGoingID);
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


		List<String> listOutIDs = linkSub.getOutIDs(originalID);

		for (String id : listOutIDs) {

			final UpdateContextSubscriptionRequest tuCReq = new UpdateContextSubscriptionRequest();
			tuCReq.setDuration(updateRequest.getDuration());
			tuCReq.setNotifyCondition(updateRequest.getNotifyCondition() != null ? updateRequest
					.getNotifyCondition() : null);
			tuCReq.setRestriction(updateRequest.getRestriction());
			tuCReq.setThrottling(updateRequest.getThrottling());
			tuCReq.setSubscriptionId(id);
			final URI agentUri = outgoingSub.getAgentUri(id);
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

	private void sendSubscribeContextRequest(
			final SubscribeContextRequest subscribeRequest, final URI agentURi,
			final String originalID) {


		new Thread() {
			@Override
			public void run() {
				SubscribeContextResponse scr = agentWrapper
						.receiveReqFrmSubscriptionController(subscribeRequest, agentURi);
				if (scr.getSubscribeError().getStatusCode() != null) {
					outgoingSub.saveOutgoingSubscription(subscribeRequest, scr
							.getSubscribeResponse().getSubscriptionId(),
							agentURi, System.currentTimeMillis());
					linkSub.insert(originalID, scr.getSubscribeResponse()
							.getSubscriptionId());
				}
			}

		}.start();
	}

	/**
	 * Given an id of an incoming subscription, this function retrieves all existing out-
	 * going subscriptions that were made on behalf of this incoming subscription.
	 * 
	 * The returned outgoing subscriptions are represented in a priority queue that is 
	 * sorted by the URI of the agent.
	 */
	private PriorityQueue<Pair<URI, SubscribeContextRequest>> getExistingOutgoingSubs(
			String originalID) {

		PriorityQueue<Pair<URI, SubscribeContextRequest>> existingSubscribedAgents = new PriorityQueue<Pair<URI, SubscribeContextRequest>>(
				2, new Comparator<Pair<URI, SubscribeContextRequest>>() {
					@Override
					public int compare(
							Pair<URI, SubscribeContextRequest> pair1,
							Pair<URI, SubscribeContextRequest> pair2) {
						return pair1.getLeft().compareTo(pair2.getLeft());
					}
				});
		
		List<String> tmpOutGoingID = linkSub.getOutIDs(originalID);
		for (String s : tmpOutGoingID) {

			existingSubscribedAgents.add(new Pair<URI, SubscribeContextRequest>(
					outgoingSub.getAgentUri(s), outgoingSub
					.getOutgoingSubscription(s)));
		}

		return existingSubscribedAgents;

	}

	/**
	 * Given an list of context registration responses, this list is represented as a
	 * priority queue sorted by the reference URI in the responses.
	 * 
	 */
	private PriorityQueue<Pair<URI, ContextRegistrationResponse>> convertToPrioQueue(
			List<ContextRegistrationResponse> lCRRes) {

		PriorityQueue<Pair<URI, ContextRegistrationResponse>> newSubscribedAgent = new PriorityQueue<Pair<URI, ContextRegistrationResponse>>(
				2, new Comparator<Pair<URI, ContextRegistrationResponse>>() {
					@Override
					public int compare(
							Pair<URI, ContextRegistrationResponse> pair1,
							Pair<URI, ContextRegistrationResponse> pair2) {
						return pair1.getLeft().compareTo(pair2.getLeft());
					}
				});
		for (ContextRegistrationResponse cRRes : lCRRes) {
			newSubscribedAgent.add(new Pair<URI, ContextRegistrationResponse>(
					cRRes.getContextRegistration().getProvidingApplication(),
					cRRes));
		}

		return newSubscribedAgent;

	}

}
