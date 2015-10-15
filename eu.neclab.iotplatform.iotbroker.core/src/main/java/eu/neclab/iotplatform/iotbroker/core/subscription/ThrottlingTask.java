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
import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import eu.neclab.iotplatform.iotbroker.commons.Restriction;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextRequest;

/**
 * Represents the recurring task for sending notifications in reaction to
 * subscriptions.
 * 
 * Usually this task is created by the NorthBoundWrapper when it receive a
 * Subscripiton request from the North (i.e. from an Application)
 */
public class ThrottlingTask extends TimerTask {
	private static Logger logger = Logger.getLogger(ThrottlingTask.class);
	private final String subId;
	private final SubscriptionController subContoller;

	/**
	 * Creates a new task instance.
	 * 
	 * @param subId
	 *            The identifier of this subscription.
	 * @param subscriptionController
	 *            Pointer to the subscription controller.
	 */
	public ThrottlingTask(String subId,
			SubscriptionController subscriptionController) {
		this.subId = subId;
		subContoller = subscriptionController;
	}

	/**
	 * @return The identifier of the subscription.
	 */
	public String getSubId() {
		return subId;
	}

	/**
	 * Flushes the notification queue for a subscription.
	 */
	@Override
	public void run() {

		/*
		 * Retrieve the subscription data from the subscription store
		 */

		logger.debug("SubscriptionId from TrottlingTask ---------------->"
				+ subId);

		SubscriptionData subscriptionData = subContoller.getSubscriptionStore()
				.get(subId);

		logger.debug("Trotthling task ID ---------------->"
				+ subscriptionData.getThrottlingTask().getSubId());

		if (subscriptionData.getContextResponseQueue().isEmpty()) {
			logger.debug("Terminating notification task as there are no notifications to send.");
			return;
		}

		try {

			/*
			 * Lock this subscription data so that no one can add notifications
			 * to it while sending the notifications.
			 */

			subscriptionData.getLock().lock();
			logger.debug("subscription ID to notify: " + subId);

			/*
			 * Get the incoming subscription
			 */
			SubscribeContextRequest request = subContoller.getIncomingSub()
					.getIncomingSubscription(subId);

			logger.debug("Processing Notication for this subscription:"
					+ request.toString());

			NotifyContextRequest notifyReq = null;

			logger.debug("Notification queue: "
					+ subscriptionData.getContextResponseQueue().toString());

			/*
			 * Create a query context response from the context element
			 * responses in the notification queue
			 */
			QueryContextResponse qcr = new QueryContextResponse();
			qcr.setContextResponseList(subscriptionData
					.getContextResponseQueue());

			/*
			 * Apply the restriction if there is any
			 */
			if (request.getRestriction() != null
					&& request.getRestriction().getAttributeExpression() != null
					&& !request.getRestriction().getAttributeExpression()
							.isEmpty()) {
				Restriction.applyRestriction(request.getRestriction()
						.getAttributeExpression(), qcr);
			}

			logger.info("Notification queue after applying restriction: "
					+ qcr.getListContextElementResponse());

			/*
			 * Create the notification request
			 */
			String originator = subscriptionData.getOriginator();
			notifyReq = new NotifyContextRequest(subId, originator,
					qcr.getListContextElementResponse());
			// notifyReq = new NotifyContextRequest(subId,
			// "http://" + InetAddress.getLocalHost().getHostAddress()
			// + ":" + System.getProperty("tomcat.init.port")
			// + "/ngsi10/notify",
			// qcr.getListContextElementResponse());

			logger.info("Now sending this notification:" + notifyReq);

			subContoller.getNorthBoundWrapper().forwardNotification(notifyReq,
					new URI(subscriptionData.getNotificationHandler()));

			// } catch (UnknownHostException e) {
			// logger.info("Unknown host, aborting notification", e);
		} catch (URISyntaxException e) {
			logger.info("Error in URI Syntax", e);
		} finally {
			// Clear the notification list and unlock
			subscriptionData.getContextResponseQueue().clear();
			subscriptionData.getLock().unlock();

		}

	}

}
