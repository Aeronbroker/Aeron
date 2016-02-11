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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import eu.neclab.iotplatform.iotbroker.storage.AvailabilitySubscriptionInterface;
import eu.neclab.iotplatform.iotbroker.storage.LinkSubscriptionAvailabilityInterface;
import eu.neclab.iotplatform.iotbroker.storage.SubscriptionStorageInterface;
import eu.neclab.iotplatform.ngsi.api.datamodel.Code;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationResponse;
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
 * Class used by the subscription handler to communicate with the NGSI 9 server
 * ("Configuration Management"). Currently this class is only used for the
 * communication in case of subscriptions. Therefore, methods of this class are
 * called for
 * <p>
 * - communicating with configuration management in case new subscriptions from
 * applications arrive, or such subscriptions are updated
 * <p>
 * - handing availability notifications arriving from configuration management
 * 
 * Important to note, this class never writes to the storage but only reads from
 * it.
 * 
 */
public class ConfManWrapper {
	private static Logger logger = Logger.getLogger(ConfManWrapper.class);
	private final SubscriptionController subscriptionController;
	private final AssociationsUtil associationsUtil = new AssociationsUtil();
	private AvailabilitySubscriptionInterface availabilitySub;
	private LinkSubscriptionAvailabilityInterface linkAvSub;
	private SubscriptionStorageInterface subscriptionStorage;
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
	 * @return Pointer to the subscription storage.
	 */
	public SubscriptionStorageInterface getSubscriptionStorage() {
		return subscriptionStorage;
	}

	/**
	 * Assigns the pointer to the subscription storage.
	 */
	public void setSubscriptionStorage(
			SubscriptionStorageInterface subscriptionStorage) {
		this.subscriptionStorage = subscriptionStorage;
	}


	/**
	 * @return Pointer to the storage for links between incoming subscriptions
	 *         and availability subscriptions.
	 */
	public LinkSubscriptionAvailabilityInterface getLinkAvSub() {
		return linkAvSub;
	}

	/**
	 * Assigns the pointer to the storage for links between incoming
	 * subscriptions and availability subscriptions.
	 */
	public void setLinkAvSub(LinkSubscriptionAvailabilityInterface linkAvSub) {
		this.linkAvSub = linkAvSub;
	}

	// /**
	// * @return Pointer to the storage for links between incoming subscriptions
	// * and outgoing subscriptions.
	// */
	// public LinkSubscriptionInterface getLinkSub() {
	// return linkSub;
	// }
	//
	// /**
	// * Assigns the pointer to the storage for links between incoming
	// * subscriptions and outgoing subscriptions.
	// */
	// public void setLinkSub(LinkSubscriptionInterface linkSub) {
	// this.linkSub = linkSub;
	// }

	/**
	 * 
	 * @return Pointer to the Associations Utility.
	 */
	public AssociationsUtil getAssociationsUtil() {
		return associationsUtil;
	}

	/**
	 * Creates an instance of this class parameterized by the Subscription
	 * Controller.
	 */
	public ConfManWrapper(SubscriptionController subscriptionController) {
		super();
		this.subscriptionController = subscriptionController;

	}

	/**
	 * Calls the NGSI 9 UnsubscribeContextAvailability operation on the NGSI 9
	 * Configuration Management component.
	 * 
	 * @param uCAReq
	 *            The NGSI 9 {@link UnsubscribeContextAvailabilityRequest}.
	 * @return The NGSI 9 {@link UnsubscribeContextAvailabilityResponse}.
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

	/*
	 * Note: the method directly below is an alternative implementation that can
	 * replace the currently enabled implementation of
	 * "receiveReqFrmSubscriptionController" to enable subscription in case the
	 * configuration management does not support availability subscriptions. If
	 * the configuration management does support availability subscriptions the
	 * currently enabled implementation is to be preferred.
	 */
	/**
	 * This function initiates the communication with configuration management
	 * in reaction to a new subscription. In particular, this function
	 * <p>
	 * (1) makes a discovery to retrieve the relevant set of data sources and
	 * associations for this subscription
	 * <p>
	 * (2) makes a context availability subscription in order to be informed
	 * about future data source availability
	 * 
	 * Note that although the function is called with a
	 * subscribeContextAvailabilityRequest as the parameter, it still makes a
	 * discovery in addition.
	 * 
	 * @param scaReq
	 *            The NGSI 9 {@link SubscribeContextAvailabilityRequest}.
	 * @return The NGSI 9 {@link SubscribeContextAvailabilityResponse}.
	 * 
	 */
	public SubscribeContextAvailabilityResponse receiveReqFrmSubscriptionControllerAlternative(
			final SubscribeContextAvailabilityRequest scaReq) {

		/*
		 * 
		 * Create the operation scope for the discovery. As this is the reaction
		 * to a subscription, the scope is 'IncludeAssociations' with value
		 * 'SOURCES'. The operation scope is then added to the existing scopes
		 * from the original subscribe availability request.
		 */
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

		/*
		 * Create and make the discovery. This includes asking for SOURCES.
		 */

		DiscoverContextAvailabilityRequest discoveryRequest = new DiscoverContextAvailabilityRequest(
				scaReq.getEntityIdList(), scaReq.getAttributeList(),
				scaReq.getRestriction());
		logger.debug("Sending DiscoverContextAvailabilityRequest to ConfMan:"
				+ discoveryRequest.toString());
		final DiscoverContextAvailabilityResponse discoveryResponse = ngsi9Impl
				.discoverContextAvailability(discoveryRequest);

		/*
		 * If the discovery is unsuccessful, the function is aborted and returns
		 * an error.
		 * 
		 * Else the function is continued.
		 */

		if (discoveryResponse.getErrorCode() != null
				&& discoveryResponse.getErrorCode().getCode() > 499) {
			return new SubscribeContextAvailabilityResponse(null, null,
					new StatusCode(500,
							ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
							null));
		} else {

			// Fill in the EntityId list & ContextRegistrationAttribute List if
			// is returned empty by the ConfMan
			Iterator<ContextRegistrationResponse> iter = discoveryResponse
					.getContextRegistrationResponse().iterator();

			while (iter.hasNext()) {
				ContextRegistrationResponse response = iter.next();
				if (response.getContextRegistration().getListEntityId() == null
						|| response.getContextRegistration().getListEntityId()
								.isEmpty()) {

					response.getContextRegistration().setListEntityId(
							scaReq.getEntityIdList());

				}

				if (response.getContextRegistration()
						.getContextRegistrationAttribute() == null
						|| response.getContextRegistration()
								.getContextRegistrationAttribute().isEmpty()) {

					Iterator<String> iterAttribute = scaReq.getAttributeList()
							.iterator();
					List<ContextRegistrationAttribute> conteRegAttrList = new ArrayList<ContextRegistrationAttribute>();
					while (iterAttribute.hasNext()) {

						ContextRegistrationAttribute attribute = new ContextRegistrationAttribute(
								iterAttribute.next(), null, false, null);

						conteRegAttrList.add(attribute);

					}
					response.getContextRegistration()
							.setListContextRegistrationAttribute(
									conteRegAttrList);

				}

			}

			/*
			 * We now also make an availability subscription to config
			 * management. Also this includes asking for SOURCES.
			 */
			final SubscribeContextAvailabilityResponse scaRes = ngsi9Impl
					.subscribeContextAvailability(scaReq);
			/*
			 * Again, if this is unsuccessful, then the function is aborted and
			 * an error is returned. (this behavior could be changed in a future
			 * release)
			 * 
			 * Otherwise, the function continues.
			 */

			if (scaRes.getErrorCode() != null
					&& scaRes.getErrorCode().getCode() > 499) {
				return new SubscribeContextAvailabilityResponse(null, null,
						new StatusCode(500,
								ReasonPhrase.RECEIVERINTERNALERROR_500
										.toString(), null));
			} else if (scaRes.getErrorCode() == null
					|| scaRes.getErrorCode().getCode() == 200) {

				/*
				 * After also the availability subscription being successful,
				 * the function returns a success message in the
				 * SubscribeContextAvailabilityResponse.
				 * 
				 * What is done in addition is to further process the result
				 * from the discovery, but this is done in a different Thread.
				 */

				new Thread() {
					@Override
					public void run() {

						/*
						 * Here we are in the new Thread that is created for
						 * further processing the discovery response.
						 */

						/*
						 * We extract here the relevant associations from the
						 * discovery response. This proceeds in three steps
						 */

						List<AssociationDS> assocList = associationsUtil
								.retrieveAssociation(discoveryResponse);
						logger.debug("Association List from discovery response:"
								+ assocList);

						/*
						 * Step 1: Extracts from the discovered associations the
						 * ones whose target match with the entities in the
						 * given availability subscription.
						 */

						QueryContextRequest qcReq = new QueryContextRequest(
								scaReq.getEntityIdList(),
								scaReq.getAttributeList(), null);

						List<AssociationDS> additionalRequestList = associationsUtil
								.initialLstOfmatchedAssociation(qcReq,
										assocList);
						logger.debug("(Step 1) Initial List Of matchedAssociation:"
								+ additionalRequestList);

						/*
						 * Step 2: apply transitivity to further enrich the set
						 * of associations; see documentation of the
						 * transitiveAssociationAnalysisFrQuery function for
						 * details.
						 */

						List<AssociationDS> transitiveList = associationsUtil
								.transitiveAssociationAnalysisFrQuery(
										assocList, additionalRequestList);
						logger.debug("(Step 2 ) Transitive List Of matchedAssociation:"
								+ transitiveList);

						/*
						 * Step 3: Create a new discovery response, this time
						 * containing all data sources from the original
						 * discovery response for which one of the found
						 * associations is applicable.
						 */

						DiscoverContextAvailabilityResponse dcaRes = associationsUtil
								.validDiscoverContextAvailabiltyResponse(
										discoveryResponse, transitiveList);
						logger.debug("(Step 3 ) Final valid DiscoverContextAvailabilityResponse List Of matchedAssociation:"
								+ dcaRes);

						/*
						 * Now create from the discovery response an
						 * availability notification containing the same
						 * information. This notification is then passed to the
						 * subscription handler together with the final list of
						 * associations to be handled like a normal availability
						 * notification.
						 */

						NotifyContextAvailabilityRequest ncaReq = new NotifyContextAvailabilityRequest(
								scaRes.getSubscribeId(),
								dcaRes.getContextRegistrationResponse(),
								new StatusCode(200, "Ok", null));

						subscriptionController.receiveReqFrmConfManWrapper(
								ncaReq, transitiveList);
					}
				}.start();
			}
			logger.debug("Sending SubscribeContextAvailabilityResponse to SubscriptionController:"
					+ scaRes.toString());
			return scaRes;

		}

	}

	/**
	 * This function initiates the communication with configuration management
	 * in reaction to a new subscription. In particular, this function
	 * <p>
	 * makes a context availability subscription in order to be informed about
	 * future data source availability
	 * 
	 * @param scaReq
	 *            The NGSI 9 {@link SubscribeContextAvailabilityRequest}.
	 * @return The NGSI 9 {@link SubscribeContextAvailabilityResponse}.
	 * 
	 */
	public SubscribeContextAvailabilityResponse receiveReqFrmSubscriptionController(
			final SubscribeContextAvailabilityRequest scaReq) {

		/*
		 * 
		 * Create the operation scope for the discovery. As this is the reaction
		 * to a subscription, the scope is 'IncludeAssociations' with value
		 * 'SOURCES'. The operation scope is then added to the existing scopes
		 * from the original subscribe availability request.
		 */
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

		/*
		 * We now also make an availability subscription to config management.
		 * Also this includes asking for SOURCES.
		 */
		final SubscribeContextAvailabilityResponse scaRes = ngsi9Impl
				.subscribeContextAvailability(scaReq);
		/*
		 * Again, if this is unsuccessful, then the function is aborted and an
		 * error is returned. (this behavior could be changed in a future
		 * release)
		 * 
		 * Otherwise, the function continues.
		 */

		if (scaRes.getErrorCode() != null
				&& scaRes.getErrorCode().getCode() > 499) {
			return new SubscribeContextAvailabilityResponse(null, null,
					new StatusCode(500,
							ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
							null));
		} else {

			return scaRes;

		}

	}

	/**
	 * Processes the NGSI 9 NotifyContextAvailability operation. This operation
	 * is called when an availability notification arrives from the
	 * configuration manager.
	 * 
	 * What the operation does is to extract the final set of associations
	 * (applying transitiveness and filtering) and then pass the notification
	 * and associations on to the subscription controller.
	 * 
	 * @param notifyContextAvailabilityRequest
	 *            The NGSI 9 {@link NotifyContextAvailabilityRequest}.
	 * @return The NGSI 9 {@link NotifyContextAvailabilityResponse}.
	 */
	public NotifyContextAvailabilityResponse receiveReqFrmConfigManager(
			final NotifyContextAvailabilityRequest notifyContextAvailabilityRequest) {

		/*
		 * First, we retrieve from the storage the identifier of the incoming
		 * NGSI 10 subscription for which the availability subscription was
		 * made. Note that this is a 2-step process, as first the subscription
		 * id needs to be retrieved and then the NGSI10 subscription itself is
		 * retrieved from that id.
		 * 
		 * If this is not found or more than one are found (which is not
		 * expected), then the function is aborted and an error is returned.
		 */
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			logger.info("InterruptedException", e);
		}
		List<String> lsubAvailID = linkAvSub
				.getInIDs(notifyContextAvailabilityRequest.getSubscribeId());
		if (lsubAvailID.size() != 1) {

			logger.info("SUBSCRIPTION NOT FOUND!!");

			return new NotifyContextAvailabilityResponse(new StatusCode(
					Code.SUBSCRIPTIONIDNOTFOUND_470.getCode(),
					ReasonPhrase.SUBSCRIPTIONIDNOTFOUND_470.toString(), null));
		}

		final SubscribeContextRequest scReq = subscriptionStorage
				.getIncomingSubscription(lsubAvailID.get(0));

		/*
		 * If the above information could be retrieved successfully from the
		 * storage, we consider the notification to be successful and return a
		 * positive notification response. The further processing is done in a
		 * new Thread.
		 */

		new Thread() {
			@Override
			public void run() {

				/*
				 * Here we are in the new Thread the processes the availability
				 * notification, after already having successfully retrieved the
				 * original NGSI 10 subscription for which the availability
				 * subscription had been done.
				 */

				/*
				 * We represent the availability notification as a discovery
				 * response, because the association utility is made for the
				 * latter data structure.
				 * 
				 * Then the set of relevant associations is computed by the same
				 * three step that were also applied at the time the discovery
				 * was made.
				 */

				DiscoverContextAvailabilityResponse discoveryResponse = new DiscoverContextAvailabilityResponse();
				discoveryResponse
						.setContextRegistrationResponse(notifyContextAvailabilityRequest
								.getContextRegistrationResponseList());

				/*
				 * ########################### Starting code for associations
				 * ###########################
				 */

				/* extract the associations from the discovery response */
				List<AssociationDS> assocList = associationsUtil
						.retrieveAssociation(discoveryResponse);
				if (logger.isDebugEnabled()) {
					logger.debug("Association List:" + assocList);
				}

				/* represent the NGSI 10 subscription as a query */
				QueryContextRequest qcReq = new QueryContextRequest(
						scReq.getEntityIdList(), scReq.getAttributeList(), null);

				/* Step 1 of association application (find targets) */
				List<AssociationDS> additionalRequestList = associationsUtil
						.initialLstOfmatchedAssociation(qcReq, assocList);
				if (logger.isDebugEnabled()) {
					logger.debug("(Step 1) Initial List Of matchedAssociation:"
							+ additionalRequestList);
				}

				/*
				 * Step 2 (transitivity)
				 */
				List<AssociationDS> transitiveList = associationsUtil
						.transitiveAssociationAnalysisFrQuery(assocList,
								additionalRequestList);
				if (logger.isDebugEnabled()) {
					logger.debug("(Step 2 ) Transitive List Of matchedAssociation:"
							+ transitiveList);
				}

				/*
				 * Step 3 (find sources)
				 */
				DiscoverContextAvailabilityResponse dcaRes = associationsUtil
						.validDiscoverContextAvailabiltyResponse(
								discoveryResponse, transitiveList);
				if (logger.isDebugEnabled()) {
					logger.debug("(Step 3 ) Final valid DiscoverContextAvailabilityResponse List Of matchedAssociation:"
							+ dcaRes);
				}

				/*
				 * ########################### Ending code for associations
				 * ###########################
				 */

				/*
				 * 
				 * Now the resulting discovery response (represented as
				 * notification) and the associations are passed to the
				 * subscription handler.
				 */
				NotifyContextAvailabilityRequest ncaReq = new NotifyContextAvailabilityRequest(
						notifyContextAvailabilityRequest.getSubscribeId(),
						dcaRes.getContextRegistrationResponse(),
						new StatusCode(200, "Ok", null));
				if (logger.isDebugEnabled()) {
					logger.debug("Sending NotifyContextAvailabilityRequest to SubscriptionController:"
							+ ncaReq.toString());
				}

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
	 * @return null
	 */
	public UpdateContextAvailabilitySubscriptionResponse receiveReqFrmSubscriptionController(
			UpdateContextAvailabilitySubscriptionRequest uCAReq) {
		return null;
	}
}
