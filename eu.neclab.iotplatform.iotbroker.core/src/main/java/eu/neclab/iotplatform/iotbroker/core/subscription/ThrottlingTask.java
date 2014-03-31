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
 *  Represents the recurring task for sending notifications. 
 */
public class ThrottlingTask extends TimerTask {
	private static Logger logger = Logger.getLogger(ThrottlingTask.class);
	private final String subId;
	private final SubscriptionController subContoller;

	/**
	 * Creates a new task instance.
	 * 
	 * @param subId The identifier of this subscription.
	 * @param subscriptionController Pointer to the subscription controller.
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
	 * Sends notifications when necessary.
	 */
	@Override
	public void run() {

		SubscriptionData subscriptionData = subContoller.getSubscriptionStore()
				.get(subId);
		if (subscriptionData.getContextResponseQueue().isEmpty()) {
			return;
		}

		try {
			subscriptionData.getLock().lock();
			logger.info("#############################" + subId);

			// Get the subId of the Agent Subscription
			List<String> subIdAgent = subContoller.getLinkAvSub().getAvailIDs(
					subId);
			logger.info("--------------> SIZE QUERY" + subIdAgent.size());
			logger.info("#############################" + subIdAgent.get(0));

			SubscribeContextRequest request = subContoller.getIncomingSub()
					.getIncomingSubscription(subId);
			// send notification
			NotifyContextRequest notifyReq = null;
			try {
				if (request.getRestriction() != null) {
					QueryContextResponse qcr = new QueryContextResponse();
					qcr.setContextResponseList(subscriptionData
							.getContextResponseQueue());
					Restriction.applyRestriction(request.getRestriction()
							.getAttributeExpression(), qcr);
					notifyReq = new NotifyContextRequest(subId, "http://"
							+ InetAddress.getLocalHost().getHostAddress() + ":"
							+ System.getProperty("tomcat.init.port")
							+ "/ngsi10/notify",
							qcr.getListContextElementResponse());
				}

				notifyReq = new NotifyContextRequest(subId, "http://"
						+ InetAddress.getLocalHost().getHostAddress() + ":"
						+ System.getProperty("tomcat.init.port")
						+ "/ngsi10/notify",
						subscriptionData.getContextResponseQueue());

			} catch (UnknownHostException e) {
				logger.info("Unknown host", e);
			}

			logger.info("-------------------------------------------------------Request from DATABASE:"
					+ request.toString());
			logger.info("-------------------------------------------------------URL of popsubbroker:"
					+ request.getReference().toString());
			logger.info("-------------------------------------------------------notify send to popsubbroker:"
					+ notifyReq);
			subContoller.getNorthBoundWrapper().forwardNotification(notifyReq,
					new URI(request.getReference()));

		} catch (URISyntaxException e) {
			logger.info("Error Uri Sintax", e);
		}

		finally {

			// Clear the List of ContextResponse
			subscriptionData.getContextResponseQueue().clear();
			subscriptionData.getLock().unlock();

		}

	}

}
