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

import org.apache.log4j.Logger;

import eu.neclab.iotplatform.iotbroker.commons.interfaces.IoTAgentWrapperInterface;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.StatusCode;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextSubscriptionRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextSubscriptionResponse;
import eu.neclab.iotplatform.ngsi.api.ngsi10.Ngsi10Requester;

/**
 * Class for communicating with IoT Agents on behalf of the subscription 
 * controller.
 *
 */
public class AgentWrapper implements IoTAgentWrapperInterface {
	private static Logger logger = Logger.getLogger(AgentWrapper.class);
	private final SubscriptionController subscriptionController;
	public Ngsi10Requester ngsi10Requestor;

	/**
	 * Creates a new instance of the class. Only a singleton instance is used
	 * per IoT Broker deployment.
	 * @param subscriptionController Pointer to the subscription controller.
	 */
	public AgentWrapper(SubscriptionController subscriptionController) {
		super();
		this.subscriptionController = subscriptionController;

	}

	/**
	 * @return A pointer to the component for making NGSI 10 requests.
	 */
	public Ngsi10Requester getNgsi10Requestor() {
		return ngsi10Requestor;
	}

	/**
	 * Sets the pointer to the component for making NGSI 10 requests.
	 */
	public void setNgsi10Requestor(Ngsi10Requester ngsi10Requestor) {
		this.ngsi10Requestor = ngsi10Requestor;
	}

	/* (non-Javadoc)
	 * @see eu.neclab.iotplatform.iotbroker.core.subscription.IoTAgentWrapperInterface#receiveReqFrmSubscriptionController(eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextRequest, java.net.URI)
	 */
	/* (non-Javadoc)
	 * @see eu.neclab.iotplatform.iotbroker.core.subscription.IoTAgentWrapperInterface#receiveReqFrmSubscriptionController(eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextRequest, java.net.URI)
	 */
	@Override
	public SubscribeContextResponse receiveReqFrmSubscriptionController(
			SubscribeContextRequest scReq, URI uri) {
		logger.debug("Sending Request:" + scReq.toString() + " : "
				+ uri.toString());
		SubscribeContextResponse scRes = ngsi10Requestor.subscribeContext(
				scReq, uri);
		logger.info("###################Receive Request:" + scRes.toString());
		return scRes;
	}

	/* (non-Javadoc)
	 * @see eu.neclab.iotplatform.iotbroker.core.subscription.IoTAgentWrapperInterface#receiveFrmAgents(eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextRequest)
	 */
	@Override
	public NotifyContextResponse receiveFrmAgents(NotifyContextRequest notifyContextRequest) {
		
		/*
		 * We pass the notification directly to the subscription controller.
		 * The response received from it is then returned. In case the response
		 * is null or it has a status code other than 200 "OK", a response
		 * with status code 500 "internal error" is returned. 
		 */
		
		NotifyContextResponse notifyContextResponse = subscriptionController
				.receiveReqFrmAgentWrapper(notifyContextRequest);
		if ((notifyContextResponse == null)
				|| (notifyContextResponse.getResponseCode() != null && notifyContextResponse.getResponseCode()
				.getCode() != 200)) {
			return new NotifyContextResponse(new StatusCode(500,
					"Receiver internal error", null));
		} else {
			return notifyContextResponse;
		}
	}

	/* (non-Javadoc)
	 * @see eu.neclab.iotplatform.iotbroker.core.subscription.IoTAgentWrapperInterface#receiveReqFrmSubscriptionController(eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextRequest, java.net.URI)
	 */
	/* (non-Javadoc)
	 * @see eu.neclab.iotplatform.iotbroker.core.subscription.IoTAgentWrapperInterface#receiveReqFrmSubscriptionController(eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextRequest, java.net.URI)
	 */
	@Override
	public UnsubscribeContextResponse receiveReqFrmSubscriptionController(
			UnsubscribeContextRequest uCReq, URI uri) {
		logger.debug("Sending Request:" + uCReq.toString() + " : "
				+ uri.toString());
		UnsubscribeContextResponse uCRes = ngsi10Requestor.unsubscribeContext(
				uCReq, uri);
		logger.debug("Receive Request:" + uCRes.toString());
		return uCRes;
	}
	
	/* (non-Javadoc)
	 * @see eu.neclab.iotplatform.iotbroker.core.subscription.IoTAgentWrapperInterface#receiveReqFrmSubscriptionController(eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextSubscriptionRequest, java.net.URI)
	 */
	/* (non-Javadoc)
	 * @see eu.neclab.iotplatform.iotbroker.core.subscription.IoTAgentWrapperInterface#receiveReqFrmSubscriptionController(eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextSubscriptionRequest, java.net.URI)
	 */
	@Override
	public UpdateContextSubscriptionResponse receiveReqFrmSubscriptionController(
			UpdateContextSubscriptionRequest uCReq, URI uri) {
		UpdateContextSubscriptionResponse a =new UpdateContextSubscriptionResponse();
		a.setSubscribeResponse(new SubscribeResponse(uCReq.getSubscriptionId(), uCReq.getDuration(), uCReq.getThrottling()));
		logger.debug(a);
		logger.debug("Sending Request:" + uCReq.toString() + " : "
				+ uri.toString());
		UpdateContextSubscriptionResponse uCRes = ngsi10Requestor.updateContextSubscription(
				uCReq, uri);
		logger.debug("Receive Request:" + uCRes.toString());
		return uCRes;
	}

}
