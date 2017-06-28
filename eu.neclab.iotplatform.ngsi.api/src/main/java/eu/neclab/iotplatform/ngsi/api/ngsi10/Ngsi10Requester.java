/*******************************************************************************
 * Copyright (c) 2016, NEC Europe Ltd.
 * All rights reserved.
 * 
 * Authors:
 *          * NEC IoT Platform Team - iotplatform@neclab.eu
 *          * Flavio Cirillo - flavio.cirillo@neclab.eu
 *          * Tobias Jacobs - tobias.jacobs@neclab.eu
 *          * Gurkan Solmaz - gurkan.solmaz@neclab.eu
 *          * Salvatore Longo
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
 
import java.net.URI;

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
 * This is an interface for components making NGSI-10 requests, including
 * requests to Context Management, Context Consumer, and Context Provider
 * Components.
 * 
 * Unlike {@link Ngsi10Interface}, here the operations have the target address
 * of the operations as an additional parameter.
 * 
 */
@Resource
public interface Ngsi10Requester {

	/**
	 * Operation for querying context information.
	 * 
	 * @param request The NGSI 10 QueryContextRequest.
	 * @param uri The target address of the operation.
	 * @return
	 * The NGSI 10 QueryContextResponse.
	 */
	QueryContextResponse queryContext(QueryContextRequest request, URI uri);

	/**
	 * Operation for subscribing to context information.
	 * 
	 * @param request The NGSI 10 SubscribeContextRequest.
	 * @param uri The target address of the operation.
	 * @return
	 * The NGSI 10 SubscribeContextResponse.
	 */
	SubscribeContextResponse subscribeContext(SubscribeContextRequest request,
			URI uri);

	/**
	 * Operation for updating context subscriptions.
	 * 
	 * @param request The NGSI 10 UpdateContextSusbcriptionRequest.
	 * @param uri The target address of the operation.
	 * @return
	 * The NGSI 10 UpdateContextSusbcriptionResponse.
	 */
	UpdateContextSubscriptionResponse updateContextSubscription(
			UpdateContextSubscriptionRequest request, URI uri);

	/**
	 * Operation for canceling context subscriptions. 
	 * 
	 * @param request The NGSI 10 UnsubscribeContextRequest.
	 * @param uri The target address of the operation.
	 * @return The NGSI 10 UnsubscribeContextResponse.
	 */
	UnsubscribeContextResponse unsubscribeContext(
			UnsubscribeContextRequest request, URI uri);

	/**
	 * 
	 * @param request The NGSI 10 UpdateContextRequest.
	 * @param uri The target address of the operation.
	 * @return The NGSI 10 UpdateContextResponse.
	 */
	UpdateContextResponse updateContext(UpdateContextRequest request, URI uri);
	
	/**
	 * 
	 * @param request The NGSI 10 UpdateContextRequest.
	 * @param uri The target address of the operation.
	 * @return The NGSI 10 UpdateContextResponse.
	 */
	UpdateContextResponse updateContext(UpdateContextRequest request, URI uri, StandardVersion standardVersion);

	/**
	 * Operation for processing notifications that are sent in reaction
	 * to subscriptions.
	 * 
	 * @param request The NGSI 10 NotifyContextRequest.
	 * @param uri The target address of the operation.
	 * @return The NGSI 10 NotifyContextResponse.
	 */
	NotifyContextResponse notifyContext(NotifyContextRequest request, URI uri);

}
