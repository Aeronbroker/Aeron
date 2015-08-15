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

import java.net.URI;
import java.sql.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;

import org.apache.log4j.Logger;

import eu.neclab.iotplatform.ngsi.api.datamodel.Code;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElementResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.ReasonPhrase;
import eu.neclab.iotplatform.ngsi.api.datamodel.StatusCode;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeError;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextSubscriptionRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextSubscriptionResponse;
import eu.neclab.iotplatform.ngsi.api.ngsi10.Ngsi10Requester;

/**
 *  The North Bound Wrapper the component used by the IoT Broker to
 *  take care of incoming NGSI 10 operations related to subscriptions.
 *  These operations are SubscribeContext, UpdateContextSubscription,
 *  UnsubscribeContext, and NotifyContext.
 *
 *  In order to work properly, instances of this class need a pointer
 *  to a SubscriptionController instance and a NGSI10Requester instance.
 *
 */
public class NorthBoundWrapper {

	private static Logger logger = Logger.getLogger(NorthBoundWrapper.class);
	private final SubscriptionController subscriptionController;
	private final Timer timer = new Timer();
	private Ngsi10Requester ngsi10Requestor;

	private final AssociationsUtil associationUtil = new AssociationsUtil();

	/**
	 * Returns the NGSI 10 Requester, which is used for sending NGSI 10
	 * messages to URLs.
	 *
	 * @return The NGSI 10 Requester.
	 */
	public Ngsi10Requester getNgsi10Requestor() {
		return ngsi10Requestor;
	}

	/**
	 * Sets the NGSI 10 Requester, which is used for sending NGSI 10
	 * messages to URLs.
	 *
	 * @param ngsi10Requestor The NGSI 10 Requester.
	 */
	public void setNgsi10Requestor(Ngsi10Requester ngsi10Requestor) {
		this.ngsi10Requestor = ngsi10Requestor;
	}

	/**
	 *  Creates a new instance.
	 */
	public NorthBoundWrapper(SubscriptionController subscriptionController) {

		this.subscriptionController = subscriptionController;

	}

	/**
	 * Processes NGSI NotifyContext operations by sending the notification
	 * to the respective applications.
	 *
	 * @param notifyContextRequest
	 * @param popSubURI
	 * @return
	 */
	public NotifyContextResponse forwardNotification(
			NotifyContextRequest notifyContextRequest, URI popSubURI) {
		return ngsi10Requestor.notifyContext(notifyContextRequest, popSubURI);
	}

	public SubscribeContextResponse receiveReqFrmSubscribeController(
			SubscribeContextRequest scReq) {

		return null;
	}


	/**
	 *
	 * This method processes an incoming subscription from an application.
	 * It announces the subscription to the subscription controller and it
	 * sets up the process of sending notifications.
	 *
	 * @param subscribeContextRequest The NGSI 10 SubscribeContextRequest \
	 * received from an application.
	 * @return The NGSI 10 SubscribeContextResponse.
	 */
	public SubscribeContextResponse receiveReqFrmNorth(
			SubscribeContextRequest subscribeContextRequest) {

		/*Forward the subscribe request to the subscription controller
		 * and receive a response*/
		SubscribeContextResponse sCRes = subscriptionController
				.receiveReqFrmNorthBoundWrapper(subscribeContextRequest);


		if (sCRes != null && sCRes.getSubscribeError() != null && sCRes.getSubscribeError().getStatusCode().getCode() == 200) {


			/*
			 * Set up the notification process for this
			 * subscription.
			 */

			/*
			 * Get the subscription data from the subscription
			 * controller.
			 * */
			SubscriptionData sData = subscriptionController
					.getSubscriptionStore().get(
							sCRes.getSubscribeResponse().getSubscriptionId());

			// create the notification queue for the subscription
			List<ContextElementResponse> contextResponseQueue = new ArrayList<ContextElementResponse>();
			//create the notification task for the subscription

			ThrottlingTask taskThrottling = new ThrottlingTask(sCRes
					.getSubscribeResponse().getSubscriptionId(),
					subscriptionController);
			logger.debug("Created Throttling task for"+sCRes.getSubscribeResponse().getSubscriptionId());
			// store both in the subscription data
			sData.setThrottlingTask(taskThrottling);
			sData.setContextResponseQueue(contextResponseQueue);

			/*
			 * now finally deploy the notification task
			 * */
			if (subscribeContextRequest.getThrottling() != null) {

				timer.scheduleAtFixedRate(taskThrottling,
						new Date(System.currentTimeMillis()),
						subscribeContextRequest.getThrottling()
								.getTimeInMillis(new GregorianCalendar()));
			} else {

				timer.scheduleAtFixedRate(taskThrottling,
						new Date(System.currentTimeMillis()),
						subscriptionController.getDefaultThrottling());

			}

			/* store the subscription data in the subscription
			 * controller's subscription store.
			 */

			subscriptionController.getSubscriptionStore().put(
					sCRes.getSubscribeResponse().getSubscriptionId(), sData);
		}
		return sCRes;

	}

	/**
	 * Processes the NGSI 10 UpdateContextSubscription operation.
	 */
	public UpdateContextSubscriptionResponse receiveFrmNorth(
			UpdateContextSubscriptionRequest uCSreq) {
		UpdateContextSubscriptionResponse uCSres = subscriptionController
				.receiveReqFrmNorthBoundWrapper(uCSreq);
		if (uCSres.getSubscribeError() == null) {

			SubscriptionData sData = subscriptionController
					.getSubscriptionStore().get(uCSreq.getSubscriptionId());
			// Setting Throttling

			ThrottlingTask taskThrottling = new ThrottlingTask(
					uCSreq.getSubscriptionId(), subscriptionController);

			// storeTimerTask and subId
			ThrottlingTask prevTaskThrottling = sData.getThrottlingTask();
			prevTaskThrottling.cancel();
			sData.setThrottlingTask(taskThrottling);
			if (uCSres.getSubscribeResponse().getThrottling() != null) {
				try {
					timer.scheduleAtFixedRate(
							taskThrottling,
							new Date(System.currentTimeMillis()),
							uCSreq.getThrottling().getTimeInMillis(
									new GregorianCalendar()));
				} catch (Exception e) {
					logger.error("Timer Task Error",e);
					return new UpdateContextSubscriptionResponse(null,
							new SubscribeError(null, new StatusCode(
									Code.INTERNALERROR_500.getCode(),
									ReasonPhrase.RECEIVERINTERNALERROR_500
											.toString(), null)));
				}
			} else {
				try {
					timer.scheduleAtFixedRate(taskThrottling,
							new Date(System.currentTimeMillis()),
							subscriptionController.getDefaultThrottling());
					uCSres.getSubscribeResponse().setThrottling(
							associationUtil.convertToDuration(subscriptionController
											.getDefaultThrottling()));
				} catch (Exception e) {
					logger.error("Timer Task Error",e);
					return new UpdateContextSubscriptionResponse(null,
							new SubscribeError(null, new StatusCode(
									Code.INTERNALERROR_500.getCode(),
									ReasonPhrase.RECEIVERINTERNALERROR_500
											.toString(), null)));
				}
			}

			// add SubscriptionData to the Hashmap
			subscriptionController.getSubscriptionStore().put(
					uCSreq.getSubscriptionId(), sData);
		}

		return uCSres;
	}

	/**
	 * Processes the NGSI 10 UnsubscribeContext operation.
	 */
	public UnsubscribeContextResponse receiveReqFrmNorth(
			UnsubscribeContextRequest unsubscribeContextRequest) {
		return subscriptionController
				.receiveReqFrmNorthBoundWrapper(unsubscribeContextRequest);

	}
}
