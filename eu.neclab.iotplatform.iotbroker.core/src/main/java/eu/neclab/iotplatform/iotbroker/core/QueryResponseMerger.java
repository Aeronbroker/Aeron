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

package eu.neclab.iotplatform.iotbroker.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElementResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextResponse;

/**
 * Objects of this class have the purpose to merge multiple
 * {@link QueryContextResponse} objects into a single QueryContextResponse. <br>
 * By the put method, further QueryContextResponse objects are inserted. By
 * calling the get method, a QueryContextResponse merging all objects inserted
 * so far is retrieved.
 */
public class QueryResponseMerger {
	// Primary Key is EntityId and SecondaryKey is AttributeDomain
	// if no AttributeDomain present we assume empyString is used as Secondary
	// Key
	private final Map<Integer, Map<String, ContextElementResponse>> storeTable = Collections
			.synchronizedMap(new HashMap<Integer, Map<String, ContextElementResponse>>());

	final QueryContextRequest request;

	/**
	 * Creates a new QueryResponseMerger instance.
	 * 
	 * @param request
	 *            This field is supposed to contain the original
	 *            {@link QueryContextRequest} to which the responses represented
	 *            by this instance are corresponding. However, the parameter is
	 *            not used in the current implementation, so it can as well be
	 *            set to <code>null</code>.
	 */
	public QueryResponseMerger(QueryContextRequest request) {
		this.request = request;
	}

	private int generateId(EntityId entityId) {

		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((entityId.getId() == null) ? 0 : entityId.getId().hashCode());
		result = prime
				* result
				+ ((entityId.getType() == null) ? 0 : entityId.getType()
						.hashCode());
		return result;
	}

	/**
	 * Adds a new {@link QueryContextResponse} to the merger.
	 * 
	 * @param response
	 *            The QueryContextResponse to add.
	 */
	public void put(QueryContextResponse response) {

		if (response.getListContextElementResponse() == null) {
			return;
		}

		for (ContextElementResponse contextElementResponse : response
				.getListContextElementResponse()) {

			// String id = response.getListContextElementResponse().get(i)
			// .getContextElement().getEntityId().getId();

			Integer id = generateId(contextElementResponse.getContextElement()
					.getEntityId());

			String attributeDomain = contextElementResponse.getContextElement()
					.getAttributeDomainName();

			if (attributeDomain == null) {

				attributeDomain = "";

			}

			Map<String, ContextElementResponse> existingMap = storeTable
					.get(id);

			if (existingMap != null) {

				ContextElementResponse contextElemResp = existingMap
						.get(attributeDomain);

				if (contextElemResp != null) {

					contextElemResp
							.getContextElement()
							.getContextAttributeList()
							.addAll(contextElementResponse.getContextElement()
									.getContextAttributeList());

				} else {

					existingMap.put(attributeDomain, contextElementResponse);

				}

			} else {

				existingMap = new HashMap<String, ContextElementResponse>();
				existingMap.put(attributeDomain, contextElementResponse);
				storeTable.put(id, existingMap);

			}

		}

	}

	/**
	 * @return The merged QueryContextResponse.
	 */
	public QueryContextResponse get() {
		List<ContextElementResponse> contextElementRespList = new ArrayList<ContextElementResponse>();
		Iterator<Map<String, ContextElementResponse>> iter = storeTable
				.values().iterator();
		Iterator<ContextElementResponse> iterContext;
		while (iter.hasNext()) {

			Map<String, ContextElementResponse> currentMap = iter.next();
			iterContext = currentMap.values().iterator();
			while (iterContext.hasNext()) {

				ContextElementResponse contEle = iterContext.next();
				// Check if the attributeDomain is empty string and substitute
				// it with null
				if (contEle.getContextElement().getAttributeDomainName() != null
						&& "".equals(contEle.getContextElement()
								.getAttributeDomainName())) {
					contEle.getContextElement().setAttributeDomainName(null);
					contextElementRespList.add(contEle);
				}

				contextElementRespList.add(contEle);

			}

		}

		QueryContextResponse response = new QueryContextResponse(
				contextElementRespList, null);

		return response;
	}

}
