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
package eu.neclab.iotplatform.iotbroker.ext.resultfilter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import eu.neclab.iotplatform.iotbroker.commons.EntityIDMatcher;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.ResultFilterInterface;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElement;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElementResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextResponse;

/**
 * 
 * A class implementing a result filter for Lists of {@link ContextElementResponse}
 * instances. The purpose of this class is to filter out responses that
 * do not match the previous query.
 *
 */
public class ResultFilter implements ResultFilterInterface {
	
	private static Logger logger = Logger.getLogger(ResultFilter.class);
	private List<ContextElementResponse> contextElementResponseList;
	private List<QueryContextRequest> queryContextRequestList;
	private List<QueryContextResponse> queryContextResponseList = new ArrayList<QueryContextResponse>();;
	private long queryContextResponseSize = 0;

	/**
	 * This the main function for filtering a list of 
	 * {@link ContextElementResponse} instances
	 * based a list of {@link QueryContextRequest} instances.
	 * <p>
	 * The function will return the sublist of all 
	 * responses that match at least
	 * one of the requests. 
	 * 
	 * @param contextElementResponseList
	 * 	The list of {@link ContextElementResponse} instances to filter.
	 * @param requestList
	 *  The list of requests according to which the filtering is to
	 *  be done.
	 * 
	 * @return list of filtered QueryContextResponse
	 */
	@Override
	public List<QueryContextResponse> filterResult(
			List<ContextElementResponse> contextElementResponseList,
			List<QueryContextRequest> requestList) {
		queryContextResponseList = new ArrayList<QueryContextResponse>();
		logger.info("Received Request for filtering");
		logger.info("List ContextElementResponse:"
				+ contextElementResponseList.toString());
		logger.info("List QueryContextRequest:"
				+ contextElementResponseList.toString());

		queryContextResponseSize = requestList.size();

		this.contextElementResponseList = contextElementResponseList;
		queryContextRequestList = requestList;
		// Initialize QueryContextResponse
		for (int i = 1; i <= queryContextResponseSize; i++) {
			QueryContextResponse qcr = new QueryContextResponse();
			queryContextResponseList.add(qcr);
		}
		// Filter starting
		for (ContextElementResponse cer : this.contextElementResponseList) {
			int i = 0;
			for (QueryContextRequest qcReq : queryContextRequestList) {
				filterResultIndividual(cer, qcReq,
						queryContextResponseList.get(i++));
			}

		}

		Iterator<QueryContextResponse> it = queryContextResponseList.iterator();
		while (it.hasNext()) {
			QueryContextResponse qcr = it.next();
			Iterator<ContextElementResponse> itCeRes = qcr
					.getListContextElementResponse().iterator();
			while (itCeRes.hasNext()) {
				ContextElementResponse ceRes = itCeRes.next();
				if (ceRes.getContextElement().getContextAttributeList().size() == 0) {
					itCeRes.remove();
				}

			}

		}

		logger.info("QueryContextResponseList after filter"
				+ queryContextResponseList + "end");
		logger.info("ending resultFiltering");
		return queryContextResponseList;
	}

	/**
	 * This the main function for filtering the Individual
	 * ContextElementResponse based on the QueryContextResponse
	 * 
	 * @param contextElementResponse
	 * @param qcRes
	 */
	private void filterResultIndividual(ContextElementResponse contextElementResponse,
			QueryContextRequest qcReq, QueryContextResponse qcRes) {

		Boolean match = false;

		// Matching EntityIDs between contextElementResponse and
		// QueryContextRequest
		// If matche entityIDs found then copy the contextAttributes to
		// QueryContextResponse
		// else return null
		List<EntityId> entityIDList = qcReq.getEntityIdList();
		Iterator<EntityId> it = entityIDList.iterator();
		while (it.hasNext()) {
			EntityId entityID = (EntityId) it.next();

			if (EntityIDMatcher.matcher(contextElementResponse.getContextElement().getEntityId(),
					entityID)) {
				// Checking Enity Type
				if (contextElementResponse.getContextElement().getEntityId().getType() != null) {
					if (entityID.getType() != null) {
						if (contextElementResponse.getContextElement().getEntityId().getType()
								.toString()
								.equals(entityID.getType().toString())) {
							match = true;
							break;
						}
					} else {
						match = true;
						break;
					}
				} else {
					match = true;
					break;
				}
			}
		}
		if (match == false) {
			return;
		}

		if (contextElementResponse.getContextElement().getAttributeDomainName() != null) {
			if (isAttributeDomainNameFound(contextElementResponse.getContextElement()
					.getAttributeDomainName().toString(),
					qcReq.getAttributeList())) {
				// Creating new Context Element Response and assigning context
				// element and statuscode
				ContextElementResponse tmpContextElementResponse = new ContextElementResponse();
				tmpContextElementResponse.setContextElement(contextElementResponse
						.getContextElement());
				tmpContextElementResponse.setStatusCode(contextElementResponse.getStatusCode());
				// Fetching already exisitng Context Element Response and
				// assigning the new Context Element Response
				List<ContextElementResponse> tmpContextElementResponseList = qcRes
						.getListContextElementResponse();
				tmpContextElementResponseList.add(tmpContextElementResponse);
				qcRes.setContextResponseList(tmpContextElementResponseList);

			}
		} else {
			String findName = "";
			ContextElement ce = new ContextElement(contextElementResponse.getContextElement()
					.getEntityId(), null, null, contextElementResponse.getContextElement()
					.getDomainMetadata());

			List<ContextAttribute> lcaFrQueryRequestWithoutAttribure = new ArrayList<ContextAttribute>();

			Iterator<ContextAttribute> it1 = contextElementResponse.getContextElement()
					.getContextAttributeList().iterator();
			while (it1.hasNext()) {
				ContextAttribute ca = (ContextAttribute) it1.next();
				findName = ca.getName();

				Iterator<String> it2 = qcReq.getAttributeList().iterator();
				while (it2.hasNext()) {
					String qcrAttribute = (String) it2.next();
					if (qcrAttribute.equals(findName)) {

						List<ContextAttribute> lca = new ArrayList<ContextAttribute>();
						if (ce.getContextAttributeList() != null) {
							lca = ce.getContextAttributeList();
						}
						lca.add(ca);
						ce.setContextAttributeList(lca);

					}
				}
				if (qcReq.getAttributeList().size() == 0) {

					lcaFrQueryRequestWithoutAttribure.add(ca);
					ce.setContextAttributeList(lcaFrQueryRequestWithoutAttribure);
				}

			}

			// Creating new Context Element Response and assigning newly created
			// context element and old statuscode from cer
			ContextElementResponse tmpContextElementResponse = new ContextElementResponse();
			tmpContextElementResponse.setContextElement(ce);
			tmpContextElementResponse.setStatusCode(contextElementResponse.getStatusCode());
			// Fetching already exisitng Context Element Response and assigning
			// the new Context Element Response
			List<ContextElementResponse> tmpContextElementResponseList = qcRes
					.getListContextElementResponse();
			tmpContextElementResponseList.add(tmpContextElementResponse);
			qcRes.setContextResponseList(tmpContextElementResponseList);

		}

	}

	/**
	 * Checks if list of attribute contains any attributeDomainName
	 * 
	 * @param toBeMatched
	 * @param attributeList
	 * @return Returns true if the any of the Attribute is AttributeDomainName
	 *         otherwise sends false
	 */
	private Boolean isAttributeDomainNameFound(String toBeMatched,
			List<String> attributeList) {
		Iterator<String> it = attributeList.iterator();
		while (it.hasNext()) {
			String Attribute = (String) it.next();
			if (toBeMatched.equals(Attribute)) {
				return true;
			}
		}

		return false;
	}

}
