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
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
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
	 * Issues a new subscription.
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
		String ref = getRefURl();

		idSubRequest = genUniqueID.getNextUniqueId();
		logger.debug("SUBSCRIPTION ID  = " + idSubRequest);
		incomingSub.saveIncomingSubscription(scReq, idSubRequest,
				System.currentTimeMillis());
		SubscribeContextAvailabilityRequest scaReq = new SubscribeContextAvailabilityRequest(
				scReq.getAllEntity(), scReq.getAttributeList(), ref, scReq
				.getDuration().toString(), "", scReq.getRestriction());
		logger.debug("Sending SubscribeContextAvailabilityRequest to ConfManWrapper:"
				+ scaReq.toString());

		UnsubscribeTask task = new UnsubscribeTask(idSubRequest, this);
		logger.debug("Subscription time: "
				+ scReq.getDuration().getTimeInMillis(new GregorianCalendar()));
		logger.debug("Subscription time: " + scReq.getDuration().getSeconds());
		logger.debug("Subscription time: " + scReq.getDuration().getMinutes());
		logger.debug("Subscription time: " + scReq.getDuration().toString());
		SubscriptionData subData = new SubscriptionData();
		subData.setStartTime(associationUtil.currentTime());
		subData.setUnsubscribeTask(task);
		if (scReq.getDuration() != null) {
			timer.schedule(task,scReq.getDuration().getTimeInMillis(new GregorianCalendar()));
		} else {
			timer.schedule(task, new Date(System.currentTimeMillis()),defaultDuration);
			scaReq.setDuration(associationUtil.convertToDuration(Long.toString(defaultDuration)).toString());
		}

		subscriptionStore.put(idSubRequest, subData);
		subscribeContextAvailability = confManWrapper.receiveReqFrmSubscriptionController(scaReq);
		if (subscribeContextAvailability.getErrorcode() != null
				&& subscribeContextAvailability.getErrorcode().getCode() != 200) {
			scRes = new SubscribeContextResponse(null, new SubscribeError(null,
					new StatusCode(500,
							ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
							null)));
			return scRes;
		} else {

			scRes = new SubscribeContextResponse(new SubscribeResponse(
					idSubRequest, scReq.getDuration(), scReq.getThrottling()),
					new SubscribeError(null, new StatusCode(Code.OK_200
							.getCode(), ReasonPhrase.OK_200.toString(), null)));
			logger.debug("Sending SubscribeContextResponse to NorthBoundWrapper:"
					+ scRes.toString());
			availabilitySub.saveAvalabilitySubscription(subscribeContextAvailability,
					subscribeContextAvailability.getSubscribeId());
			linkAvSub.insert(idSubRequest, subscribeContextAvailability.getSubscribeId());

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


	private List<ContextElementResponse> modifyEntityAttributeBasedAssociation(
			List<AssociationDS> laDS, List<ContextElementResponse> lCres) {


		if (!laDS.isEmpty()) {
			List<ContextElementResponse> cEResTmp = new ArrayList<ContextElementResponse>();
			for (ContextElementResponse cEres : lCres) {
				for (AssociationDS aDS : laDS) {
					if (EntityIDMatcher.matcher(cEres.getContextElement()
							.getEntityId(), aDS.getSourceEA().getEntity())) {
						boolean ifAttributeDomainNameExists=false;
						if(cEres.getContextElement().getAttributeDomainName()!= null){
							if(!"".equals(cEres.getContextElement().getAttributeDomainName())){
								ifAttributeDomainNameExists=true;
								if (!"".equals(aDS.getSourceEA().getEntityAttribute())) {

									if(aDS.getSourceEA().getEntityAttribute().equals(cEres.getContextElement().getAttributeDomainName())){
										ContextElement cETmp = new ContextElement(aDS.getTargetEA().getEntity(),aDS.getTargetEA().getEntityAttribute(),	cEres.getContextElement().getContextAttributeList(), cEres.getContextElement().getDomainMetadata());
										ContextElementResponse cEresTmp = new ContextElementResponse(cETmp, cEres.getStatusCode());
										cEResTmp.add(cEresTmp);
									}
								}else{
									ContextElement cETmp = new ContextElement(aDS.getTargetEA().getEntity(),cEres.getContextElement().getAttributeDomainName(),	cEres.getContextElement().getContextAttributeList(), cEres.getContextElement().getDomainMetadata());
									ContextElementResponse cEresTmp = new ContextElementResponse(cETmp, cEres.getStatusCode());
									cEResTmp.add(cEresTmp);
								}
							}
						}
						if(ifAttributeDomainNameExists==false){
							if (!"".equals(aDS.getSourceEA().getEntityAttribute())) {
								for (ContextAttribute ca : cEres.getContextElement().getContextAttributeList()) {
									if (ca.getName().equals(aDS.getSourceEA().getEntityAttribute())) {
										List<ContextAttribute> lCaTmp = new ArrayList<ContextAttribute>();
										ca.setName(aDS.getTargetEA().getEntityAttribute());
										lCaTmp.add(ca);
										ContextElement cETmp = new ContextElement(aDS.getTargetEA().getEntity(),cEres.getContextElement().getAttributeDomainName(),	lCaTmp, cEres.getContextElement().getDomainMetadata());
										ContextElementResponse cEresTmp = new ContextElementResponse(cETmp, cEres.getStatusCode());
										cEResTmp.add(cEresTmp);
									}
								}
							} else {

								ContextElement cETmp = new ContextElement(aDS
										.getTargetEA().getEntity(), cEres
										.getContextElement()
										.getAttributeDomainName(), cEres
										.getContextElement()
										.getContextAttributeList(), cEres
										.getContextElement().getDomainMetadata());
								ContextElementResponse cEresTmp = new ContextElementResponse(
										cETmp, cEres.getStatusCode());
								cEResTmp.add(cEresTmp);
							}
						}


					}
				}
			}
			return cEResTmp;
		}
		return lCres;
	}

	/**
	 * Processes a notification.
	 *
	 * @param ncReq
	 *  The NGSI 10 NotifyContextRequest.
	 * @return
	 *  The NGSI 10 NotifyContextResponse.
	 */
	public NotifyContextResponse receiveReqFrmAgentWrapper(
			NotifyContextRequest ncReq) {


		NotifyContextRequest ncReqtoNorthBoundWrapper = new NotifyContextRequest();
		List<String> loutSubscriptionIDs = linkSub.getInIDs(ncReq.getSubscriptionId());
		if (loutSubscriptionIDs == null || loutSubscriptionIDs.size() != 1) {
			return new NotifyContextResponse(new StatusCode(470,
					ReasonPhrase.SUBSCRIPTIONIDNOTFOUND_470.toString(), null));
		} else {

			logger.debug("ID of subscription from SubscriptionController:"+ loutSubscriptionIDs.get(0));
			List<String> outid = linkAvSub.getAvailIDs(loutSubscriptionIDs.get(0));
			logger.debug("ID of subscription request send to agent:"+ ncReq.getSubscriptionId());
			SubscribeContextRequest subReq=outgoingSub.getOutgoingSubscription(ncReq.getSubscriptionId());
			logger.debug("subscriptin request: "+subReq.toString());

			BundleContext bc = FrameworkUtil.getBundle(ResultFilterInterface.class).getBundleContext();
			ServiceReference ref = bc.getServiceReference(ResultFilterInterface.class.getName());

			if(ref!=null){
				logger.debug("-----------++++++++++++++++++++++Begin Filter for subscription");
				List<QueryContextRequest>lqcReq =new ArrayList<QueryContextRequest>();
				QueryContextRequest request = new QueryContextRequest(subReq.getEntityIdList(),subReq.getAttributeList(),subReq.getRestriction());
				lqcReq.add(request);
				List<ContextElementResponse>lceRes =new ArrayList<ContextElementResponse>();
				lceRes=ncReq.getContextElementResponseList();
				logger.debug("-----------++++++++++++++++++++++ QueryContextRequest:"+lqcReq.toString()+" ContextElementResponse:"+lceRes.toString());
				//
				logger.debug("##########################");
				logger.debug(lqcReq.size());
				logger.debug(lceRes.size());
				logger.debug("##########################");


				List<QueryContextResponse>lqcRes =resultFilter.filterResult(lceRes, lqcReq);

				logger.debug("-----------++++++++++++++++++++++ After Filter ListContextElementResponse:"+lqcRes.toString()+" ContextElementResponse:"+lqcRes.toString());

				if(lqcRes.size()==1) {
					ncReq.setContextResponseList(lqcRes.get(0).getListContextElementResponse());
					logger.debug("NotifyContext request after filtering: "+ncReq);
				}
				logger.debug("-----------++++++++++++++++++++++End Filter");
			}

			List<String> listAssoc = null;
			List<ContextElementResponse> lCERes = null;

			if (outid.size() == 1) {
				logger.debug("ID of subscription from subscriptionContextAvailability request:"+ outid.get(0));
				listAssoc = availabilitySub.getListOfAssociations(outid.get(0));
			} else {
				logger.error("More then one row");
				return null;
			}


			if (listAssoc.size() == 1) {
				logger.debug(listAssoc.get(0));
				lCERes = modifyEntityAttributeBasedAssociation(associationUtil.convertToAssociationDS(listAssoc.get(0)),ncReq.getContextElementResponseList());

			} else {
				logger.error("More then one row");
				return null;
			}


			if (lCERes != null && (!lCERes.isEmpty()) ) {
				QueryContextResponse qCRes = new QueryContextResponse();
				qCRes.setContextResponseList(lCERes);
				QueryResponseMerger qRM = new QueryResponseMerger(null);
				qRM.put(qCRes);
				qCRes = qRM.get();

				logger.debug(ncReqtoNorthBoundWrapper);

				SubscriptionData subscriptionData = subscriptionStore
						.get(loutSubscriptionIDs.get(0));
				List<ContextElementResponse> contextRespList = null;
				if (subscriptionData.getContextResponseQueue() != null) {
					contextRespList = subscriptionData
							.getContextResponseQueue();
				} else {
					contextRespList = new ArrayList<ContextElementResponse>();
				}

				for (int i = 0; i < qCRes.getListContextElementResponse()
						.size(); i++) {
					logger.info("###########################################");
					logger.info(qCRes.getListContextElementResponse().get(i));

					contextRespList.add(qCRes.getListContextElementResponse()
							.get(i));
				}
				subscriptionData.setContextResponseQueue(contextRespList);
				subscriptionStore.put(loutSubscriptionIDs.get(0),subscriptionData);
				logger.debug("Adding to subdata subid:"	+ loutSubscriptionIDs.get(0) + " : " + subscriptionData);
			}

		}

		return new NotifyContextResponse(new StatusCode(200,ReasonPhrase.OK_200.toString(), null));

	}

	/**
	 * Instructs the SubscriptionController to process an Availability Notification and 
	 * additionally passes it a list of associations which determine how the data from the
	 * agents is to be interpreted.
	 *
	 * @param ncaReq
	 *  The NGSI 9 NotifyContextAvailabilityRequest.
	 * @param transitiveList
	 *  List of associations explaining how to interpret the results.
	 * @return
	 *  The NGSI 9 NotifyContextAvailabilityResponse.
	 */
	public NotifyContextAvailabilityResponse receiveReqFrmConfManWrapper(
			NotifyContextAvailabilityRequest ncaReq,
			List<AssociationDS> transitiveList) {


		logger.debug("Received NotifyContextAvailabilityRequest:" + ncaReq);
		String idSubscription = "";

		String s = "";

		
		s = associationUtil.convertAssociationToString(transitiveList);
		
		logger.debug("s:" + s);
		availabilitySub.updateAvalabilitySubscription(ncaReq, s,
				ncaReq.getSubscribeId());
		List<String> tmpID = linkAvSub.getInIDs(ncaReq.getSubscribeId());
		if (tmpID.size() == 1) {
			idSubscription = tmpID.get(0);
		}

		PriorityQueue<Pair<URI, SubscribeContextRequest>> existingSubscribedAgentQ = convertToPair(idSubscription);
		PriorityQueue<Pair<URI, ContextRegistrationResponse>> newProposedSubscribedAgentQ = convertToPair(ncaReq
				.getContextRegistrationResponseList());
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
									npSATmp.getRight(), idSubscription),
									npSATmp.getLeft(), idSubscription);
					exSATmp = existingSubscribedAgentQ.poll();
					logger.debug("POLL exist" + exSATmp.getLeft().toString());
					sendUnsubscribeContextRequest(exSATmp.getRight(),
							exSATmp.getLeft(), idSubscription);
				} else if (compareResult > 0) {
					npSATmp = newProposedSubscribedAgentQ.poll();
					logger.debug("POLL new" + npSATmp.getLeft().toString());

					sendSubscribeContextRequest(
							createSubscribeContextRequestFromCRRes(
									npSATmp.getRight(), idSubscription),
									npSATmp.getLeft(), idSubscription);
				} else if (compareResult < 0) {
					exSATmp = existingSubscribedAgentQ.poll();
					logger.debug("POLL exist" + exSATmp.getLeft().toString());

					sendUnsubscribeContextRequest(exSATmp.getRight(),
							exSATmp.getLeft(), idSubscription);
				}
			}
			while (!existingSubscribedAgentQ.isEmpty()) {
				exSATmp = existingSubscribedAgentQ.poll();
				logger.debug("POLL exist" + exSATmp.getLeft().toString()
						+ " :q " + existingSubscribedAgentQ.size());
				sendUnsubscribeContextRequest(exSATmp.getRight(),
						exSATmp.getLeft(), idSubscription);
			}
			while (!newProposedSubscribedAgentQ.isEmpty()) {
				npSATmp = newProposedSubscribedAgentQ.poll();
				logger.debug("POLL new" + npSATmp.getLeft().toString() + " :q "
						+ newProposedSubscribedAgentQ.size());
				sendSubscribeContextRequest(
						createSubscribeContextRequestFromCRRes(
								npSATmp.getRight(), idSubscription),
								npSATmp.getLeft(), idSubscription);
			}
		} else {
			while (newProposedSubscribedAgentQ.isEmpty()) {
				npSATmp = newProposedSubscribedAgentQ.poll();
				logger.debug("POLL new" + npSATmp.getLeft().toString() + " :q "
						+ newProposedSubscribedAgentQ.size());
				sendSubscribeContextRequest(
						createSubscribeContextRequestFromCRRes(
								npSATmp.getRight(), idSubscription),
								npSATmp.getLeft(), idSubscription);
			}
		}

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

	private PriorityQueue<Pair<URI, SubscribeContextRequest>> convertToPair(
			String originalID) {

		PriorityQueue<Pair<URI, SubscribeContextRequest>> existingSubscribedAgent = new PriorityQueue<Pair<URI, SubscribeContextRequest>>(
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

			existingSubscribedAgent.add(new Pair<URI, SubscribeContextRequest>(
					outgoingSub.getAgentUri(s), outgoingSub
					.getOutgoingSubscription(s)));
		}

		return existingSubscribedAgent;

	}

	private PriorityQueue<Pair<URI, ContextRegistrationResponse>> convertToPair(
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
