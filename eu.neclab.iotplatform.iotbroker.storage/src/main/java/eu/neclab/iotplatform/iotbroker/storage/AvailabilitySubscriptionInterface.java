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

package eu.neclab.iotplatform.iotbroker.storage;

import java.util.List;

import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextAvailabilityResponse;

/**
 *
 * Interface to a storage maintaining availability subscriptions. The storage works
 * like a database, where the subscription id is the key, and the three values are the
 * <p> 
 * (1) the availability subscription response containing the subscription id 
 * <p>
 * (2) the 
 * availability notification stating which are the current relevant data sources 
 * <p>
 * (3) the
 * current list of associations relevant for the subscription. The associations are assumed
 * to be already pre-processed, so that e.g. instead of A-->B and B-->C the association A-->C 
 * appears when only a data source for A is available.
 *
 *
 */
public interface AvailabilitySubscriptionInterface {


	/**
	 * Store an availability subscription response under an identifier.
	 */
	void saveAvalabilitySubscription(
			SubscribeContextAvailabilityResponse response, String id);



	/**
	 * Modify an availability subscription request stored
	 * under an identifier.
	 *
	 * @param transitiveList A list of associations represented by a String.
	 *
	 *
	 */
	void updateAvalabilitySubscription(
			NotifyContextAvailabilityRequest ncaReq, String transitiveList,
			String id);

	/**
	 * Remove the availability subscription request stored
	 * under an identifier.
	 */
	void deleteAvalabilitySubscription(String id);


	/**
	 * Retrieves Associations stored under the given id.
	 */
	List<String> getListOfAssociations(String id);
	
	void resetDB();

}
