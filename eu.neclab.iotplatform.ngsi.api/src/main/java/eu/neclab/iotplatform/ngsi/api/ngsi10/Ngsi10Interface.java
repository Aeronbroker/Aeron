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
package eu.neclab.iotplatform.ngsi.api.ngsi10;

import javax.annotation.Resource;

import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextSubscriptionRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextSubscriptionResponse;

/**
 * Represents the OMA NGSI 10 interface, as defined in the
 * OMA NGSI standard version 1.0.
 */
@Resource
public interface Ngsi10Interface {

	/**
	 * Operation for querying context information.
	 *
	 * @param request The NGSI 10 QueryContextRequest.
	 * @return
	 * The NGSI 10 QueryContextResponse.
	 */
	QueryContextResponse queryContext(QueryContextRequest request);

	/**
	 * Operation for subscribing to context information.
	 *
	 * @param request The NGSI 10 SubscribeContextRequest.
	 * @return
	 * The NGSI 10 SubscribeContextResponse.
	 */
	SubscribeContextResponse subscribeContext(SubscribeContextRequest request);

	/**
	 * Operation for updating context subscriptions.
	 *
	 * @param request The NGSI 10 UpdateContextSusbcriptionRequest.
	 * @return
	 * The NGSI 10 UpdateContextSusbcriptionResponse.
	 */
	UpdateContextSubscriptionResponse updateContextSubscription(
			UpdateContextSubscriptionRequest request);

	/**
	 * Operation for canceling context subscriptions.
	 *
	 * @param request The NGSI 10 UnsubscribeContextRequest.
	 * @return The NGSI 10 UnsubscribeContextResponse.
	 */
	UnsubscribeContextResponse unsubscribeContext(
			UnsubscribeContextRequest request);

	/**
	 *
	 * @param request The NGSI 10 UpdateContextRequest.
	 * @return The NGSI 10 UpdateContextResponse.
	 */
	UpdateContextResponse updateContext(UpdateContextRequest request);

	/**
	 * Operation for processing notifications that are sent in reaction
	 * to subscriptions.
	 *
	 * @param request The NGSI 10 NotifyContextRequest.
	 * @return The NGSI 10 NotifyContextResponse.
	 */
	NotifyContextResponse notifyContext(NotifyContextRequest request);

}
