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
package eu.neclab.iotplatform.iotbroker.storage;

import java.net.URI;
import java.util.List;

import eu.neclab.iotplatform.iotbroker.commons.OutgoingSubscriptionWithInfo;
import eu.neclab.iotplatform.iotbroker.commons.SubscriptionWithInfo;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElement;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextRequest;

/**
 * Interface to a storage maintaining incoming subscriptions.
 */
public interface SubscriptionStorageInterface {

	/**
	 * Save an subscription and a time stamp under an id.
	 */
	void saveIncomingSubscription(SubscribeContextRequest request, String id,
			long timestamp);

	/**
	 * Save an subscription, a URI, and a time stamp under an id.
	 */
	void saveOutgoingSubscription(SubscribeContextRequest request,
			String outgoingSubscriptionID, String incomingSubscriptionID,
			URI agentUri, long timestamp);

	/**
	 * Remove the subscription stored under an identifier. All the linked
	 * outgoing subscription and the link itself, will be delete as cascade
	 */
	void deleteIncomingSubscription(String id);

	/**
	 * Remove the subscription stored under an identifier.
	 */
	void deleteOutgoingSubscription(String id);

	/**
	 * Retrieve the subscription stored under an identifier.
	 */
	SubscribeContextRequest getIncomingSubscription(String id);

	/**
	 * Retrieve all the stored incoming subscriptions
	 */
	List<SubscriptionWithInfo> getAllIncomingSubscription();

	/**
	 * Retrieve the subscription stored under an identifier.
	 */
	SubscribeContextRequest getOutgoingSubscription(String id);

	/**
	 * Retrieve the outgoing subscription stored under an identifier together
	 * with agentUri and the link incoming subscription.
	 */
	OutgoingSubscriptionWithInfo getOutgoingSubscriptionWithMetadata(String id);

	/**
	 * Retrieve the URI that has been stored under an id.
	 */
	URI getAgentUri(String id);

	/**
	 * Retrieve the time stamp that has been stored under an id.
	 */
	long getTimestamp(String id);

	/**
	 * Insert a link between an incoming subscription and an outgoing
	 * subscription.
	 */
	void linkSubscriptions(String inID, String outID);

	/**
	 * Remove a link between an incoming subscription and an outgoing
	 * subscription.
	 */
	void unlinkSubscriptions(String inD, String outID);

	/**
	 * Retrieve the incoming subscription identifier linked to a given outgoing
	 * subscription.
	 */
	String getInID(String outID);

	/**
	 * Retrieve all outgoing subscription identifiers linked to a given incoming
	 * subscription.
	 */
	List<String> getOutIDs(String inID);

	/**
	 * Retrieve all the subscriptions that would be interested on such
	 * contextEntity
	 * 
	 * @return
	 */
	List<SubscriptionWithInfo> checkContextElement(ContextElement contextElement);

	void resetDB();

}
