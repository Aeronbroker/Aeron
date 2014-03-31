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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.neclab.iotplatform.iotbroker.storage.AvailabilitySubscriptionInterface;
import eu.neclab.iotplatform.iotbroker.storage.IncomingSubscriptionInterface;
import eu.neclab.iotplatform.iotbroker.storage.LinkSubscriptionAvailabilityInterface;
import eu.neclab.iotplatform.iotbroker.storage.LinkSubscriptionInterface;
import eu.neclab.iotplatform.iotbroker.storage.OutgoingSubscriptionInterface;
import eu.neclab.iotplatform.ngsi.api.datamodel.Code;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.OperationScope;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.ReasonPhrase;
import eu.neclab.iotplatform.ngsi.api.datamodel.Restriction;
import eu.neclab.iotplatform.ngsi.api.datamodel.StatusCode;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextAvailabilitySubscriptionRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextAvailabilitySubscriptionResponse;
import eu.neclab.iotplatform.ngsi.api.ngsi9.Ngsi9Interface;
import eu.neclab.iotplatform.ngsi.association.datamodel.AssociationDS;

/**
 * Class used by the subscription handler to communicate with the 
 * NGSI 9 server ("Configuration Management").
 *
 */
public class ConfManWrapper {
	private static Logger logger = Logger.getLogger(ConfManWrapper.class);
	private SubscriptionController subscriptionController;
	private final AssociationsUtil associationsUtil = new AssociationsUtil();
	private AvailabilitySubscriptionInterface availabilitySub;
	private IncomingSubscriptionInterface incomingSub;
	private OutgoingSubscriptionInterface outgoingSub;
	private LinkSubscriptionAvailabilityInterface linkAvSub;
	private LinkSubscriptionInterface linkSub;
	private Ngsi9Interface ngsi9Impl;

	/**
	 * @return Pointer to the component for making NGSI 9 requests.
	 */
	public Ngsi9Interface getNgsi9Impl() {
		return ngsi9Impl;
	}

	/**
	 * Sets the pointer to the component for making NGSI 9 requests.
	 */
	public void setNgsi9Impl(Ngsi9Interface ngsi9Impl) {
		this.ngsi9Impl = ngsi9Impl;
	}

	/**
	 * @return Pointer to the availability subscription storage.
	 */
	public AvailabilitySubscriptionInterface getAvailabilitySub() {
		return availabilitySub;
	}

	/**
	 * Assigns the pointer to the availability subscription storage.
	 */
	public void setAvailabilitySub(
			AvailabilitySubscriptionInterface availabilitySub) {
		this.availabilitySub = availabilitySub;
	}

	/**
	 * @return Pointer to the incoming subscription storage.
	 */
	public IncomingSubscriptionInterface getIncomingSub() {
		return incomingSub;
	}

	/**
	 * Assigns the pointer to the incoming subscription storage.
	 */
	public void setIncomingSub(IncomingSubscriptionInterface incomingSub) {
		this.incomingSub = incomingSub;
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
	 * @return Pointer to the storage for links between
	 * incoming subscriptions
	 * and
	 * availability subscriptions.
	 */
	public LinkSubscriptionAvailabilityInterface getLinkAvSub() {
		return linkAvSub;
	}

	/**
	 * Assigns the pointer to the storage for links between
	 * incoming subscriptions
	 * and
	 * availability subscriptions.
	 */
	public void setLinkAvSub(LinkSubscriptionAvailabilityInterface linkAvSub) {
		this.linkAvSub = linkAvSub;
	}

	/**
	 * @return Pointer to the storage for links between
	 * incoming subscriptions
	 * and
	 * outgoing subscriptions.
	 */
	public LinkSubscriptionInterface getLinkSub() {
		return linkSub;
	}
	/**
	 * Assigns the pointer to the storage for links between
	 * incoming subscriptions
	 * and
	 * outgoing subscriptions.
	 */
	public void setLinkSub(LinkSubscriptionInterface linkSub) {
		this.linkSub = linkSub;
	}

	/**
	 * 
	 * @return Pointer to the Associations Utility.
	 */
	public AssociationsUtil getAssociationsUtil() {
		return associationsUtil;
	}

	/**
	 * Creates an instance of this class parameterized by the
	 * Subscription Controller.
	 */
	public ConfManWrapper(SubscriptionController subscriptionController) {
		super();
		this.subscriptionController = subscriptionController;

	}

	/**
	 * Calls the NGSI 9 UnsubscribeContextAvailability operation on the 
	 * NGSI 9 Configuration Management component.
	 * 
	 * @param uCAReq
	 * The NGSI 9 {@link UnsubscribeContextAvailabilityRequest}.
	 * @return
	 * The NGSI 9 {@link UnsubscribeContextAvailabilityResponse}.
	 * 
	 */
	public UnsubscribeContextAvailabilityResponse receiveReqFrmSubscriptionController(
			UnsubscribeContextAvailabilityRequest uCAReq) {
		logger.debug("Sending UnsubscribeContextAvailabilityRequest to ConfMan:"
				+ uCAReq.toString());
		UnsubscribeContextAvailabilityResponse uCARes = ngsi9Impl
				.unsubscribeContextAvailability(uCAReq);
		logger.debug("Received UnsubscribeContextAvailabilityResponse from ConfMan:"
				+ uCARes.toString());
		return uCARes;

	}

	/**
	 * Executes the NGSI 10 SubscribeContextAvailability operation on the 
	 * NGSI 9 Configuration Management component.
	 * 
	 * @param scaReq
	 * The NGSI 9 {@link SubscribeContextAvailabilityRequest}.
	 * @return
	 * The NGSI 9 {@link SubscribeContextAvailabilityResponse}.
	 * 
	 */
	public SubscribeContextAvailabilityResponse receiveReqFrmSubscriptionController(
			final SubscribeContextAvailabilityRequest scaReq) {
		OperationScope oScope = new OperationScope("IncludeAssociations",
				"SOURCES");
		List<OperationScope> lOperationScope = new ArrayList<OperationScope>();
		lOperationScope.add(oScope);
		if (scaReq.getRestriction() != null) {
			if (scaReq.getRestriction().getOperationScope().isEmpty()) {
				scaReq.getRestriction().setOperationScope(lOperationScope);
			} else if (scaReq.getRestriction().getOperationScope().isEmpty()) {
				scaReq.getRestriction().getOperationScope().add(oScope);
			}
		} else {
			Restriction restriction = new Restriction("", lOperationScope);
			scaReq.setRestriction(restriction);
		}

		DiscoverContextAvailabilityRequest discoveryRequest = new DiscoverContextAvailabilityRequest(
				scaReq.getEntityIdList(), scaReq.getAttributeList(),
				scaReq.getRestriction());
		logger.debug("Sending DiscoverContextAvailabilityRequest to ConfMan:"
				+ discoveryRequest.toString());
		final DiscoverContextAvailabilityResponse discoveryResponse = ngsi9Impl
				.discoverContextAvailability(discoveryRequest);

		if (discoveryResponse.getErrorCode() != null
				&& discoveryResponse.getErrorCode().getCode() != 200) {
			return new SubscribeContextAvailabilityResponse(null, null,
					new StatusCode(500,
							ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
							null));
		} else {
			final SubscribeContextAvailabilityResponse scaRes = ngsi9Impl
					.subscribeContextAvailability(scaReq);
			if (scaRes.getErrorcode() != null) {
				return new SubscribeContextAvailabilityResponse(null, null,
						new StatusCode(500,
								ReasonPhrase.RECEIVERINTERNALERROR_500
								.toString(), null));
			} else {
				// analysis the associations and get the transistive relations
				new Thread() {
					@Override
					public void run() {
						List<AssociationDS> assocList = associationsUtil
								.retrieveAssociation(discoveryResponse);
						logger.debug("Association List:" + assocList);
						
						QueryContextRequest qcReq = new QueryContextRequest(
								scaReq.getEntityIdList(),
								scaReq.getAttributeList(), null);
						List<AssociationDS> additionalRequestList = associationsUtil
								.initialLstOfmatchedAssociation(qcReq,
										assocList);
						logger.debug("(Step 1) Initial List Of matchedAssociation:"
								+ additionalRequestList);
						List<AssociationDS> transitiveList = associationsUtil
								.transitiveAssociationAnalysisFrQuery(
										assocList, additionalRequestList);
						logger.debug("(Step 2 ) Transitive List Of matchedAssociation:"
								+ transitiveList);
						DiscoverContextAvailabilityResponse dcaRes = associationsUtil
								.validDiscoverContextAvailabiltyResponse(
										discoveryResponse, transitiveList);
						logger.debug("(Step 3 ) Final valid DiscoverContextAvailabilityResponse List Of matchedAssociation:"
								+ dcaRes);

						NotifyContextAvailabilityRequest ncaReq = new NotifyContextAvailabilityRequest(
								scaRes.getSubscribeId(),
								dcaRes.getContextRegistrationResponse(),
								new StatusCode(200, "Ok", null));

						subscriptionController.receiveReqFrmConfManWrapper(
								ncaReq, transitiveList);
					}
				}.start();
				logger.debug("Sending SubscribeContextAvailabilityResponse to SubscriptionController:"
						+ scaRes.toString());
				return scaRes;
			}
		}

	}

	/**
	 * Processes the NGSI 9 NotifyContextAvailability operation.
	 * 
	 * @param notifyContextAvailabilityRequest
	 * The NGSI 9 {@link NotifyContextAvailabilityRequest}.
	 * @return
	 * The NGSI 9 {@link NotifyContextAvailabilityResponse}.
	 */
	public NotifyContextAvailabilityResponse receiveReqFrmConfigManager(
			final NotifyContextAvailabilityRequest notifyContextAvailabilityRequest) {

		List<String> lsubAvailID = linkAvSub
				.getInIDs(notifyContextAvailabilityRequest.getSubscribeId());
		if (lsubAvailID.size() != 1) {
			return new NotifyContextAvailabilityResponse(new StatusCode(
					Code.SUBSCRIPTIONIDNOTFOUND_470.getCode(),
					ReasonPhrase.SUBSCRIPTIONIDNOTFOUND_470.toString(), null));
		}
		
		//retrieve subscription request where the notification relates to
		final SubscribeContextRequest scReq = incomingSub
				.getIncomingSubscription(lsubAvailID.get(0));
		new Thread() {
			@Override
			public void run() {
				
				
				//represent notification as discovery response
				DiscoverContextAvailabilityResponse discoveryResponse = new DiscoverContextAvailabilityResponse();		
				discoveryResponse
				.setContextRegistrationResponse(notifyContextAvailabilityRequest
						.getContextRegistrationResponseList());
				
				
				//extract the associations from the discovery response
				List<AssociationDS> assocList = associationsUtil
						.retrieveAssociation(discoveryResponse);
				logger.debug("Association List:" + assocList);
				
				//create a query context request for what is requested in the 
				//subscription this notification relates to.
				QueryContextRequest qcReq = new QueryContextRequest(
						scReq.getEntityIdList(), scReq.getAttributeList(), null);
				
				//determine the list of associations that have items from the query 
				//request in their target.				
				List<AssociationDS>  additionalRequestList = associationsUtil
						.initialLstOfmatchedAssociation(qcReq, assocList);
				logger.debug("(Step 1) Initial List Of matchedAssociation:"
						+ additionalRequestList);
				
				
				//now use the transitivity of associations in order to determine 
				//any additional association usable to satisfy the context query
				List<AssociationDS> transitiveList = associationsUtil
						.transitiveAssociationAnalysisFrQuery(assocList,
								additionalRequestList);								
				logger.debug("(Step 2 ) Transitive List Of matchedAssociation:"
						+ transitiveList);
				
				//find the sources relevant for any of the associations.
				DiscoverContextAvailabilityResponse dcaRes = associationsUtil
						.validDiscoverContextAvailabiltyResponse(
								discoveryResponse, transitiveList);
				logger.debug("(Step 3 ) Final valid DiscoverContextAvailabilityResponse List Of matchedAssociation:"
						+ dcaRes);

				//create a notification which now contains all sources to query (including
				//the results of processing the associations)
				NotifyContextAvailabilityRequest ncaReq = new NotifyContextAvailabilityRequest(
						notifyContextAvailabilityRequest.getSubscribeId(),
						dcaRes.getContextRegistrationResponse(),
						new StatusCode(200, "Ok", null));
				logger.debug("Sending NotifyContextAvailabilityRequest to SubscriptionController:"
						+ ncaReq.toString());
				
				
				subscriptionController.receiveReqFrmConfManWrapper(ncaReq,
						transitiveList);
			}
		}.start();

		return new NotifyContextAvailabilityResponse(new StatusCode(
				Code.OK_200.getCode(), ReasonPhrase.OK_200.toString(), null));

	}

	/**
	 * This method is currently not implemented and returns null.
	 * 
	 * @return
	 * null
	 */
	public UpdateContextAvailabilitySubscriptionResponse receiveReqFrmSubscriptionController(
			UpdateContextAvailabilitySubscriptionRequest uCAReq) {
		return null;
	}
}
