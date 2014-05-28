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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	//private List<ContextElementResponse> contextElementResponseList;
	//private List<QueryContextRequest> queryContextRequestList;
	//private List<QueryContextResponse> queryContextResponseList = new ArrayList<QueryContextResponse>();;
	//private long numberofRequests = 0;

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
	 * @return list of filtered QueryContextResponse. The ith element of that list
	 * will contain all context element responses that match the ith element of
	 * the request list that has been submitted in the function parameter.
	 */
	@Override
	public List<QueryContextResponse> filterResult(
			List<ContextElementResponse> contextElementResponseList,
			List<QueryContextRequest> requestList) {

		/*
		 * We first initialize the list of query context responses to return
		 *
		 */
		ArrayList<QueryContextResponse> filteredResultList = new ArrayList<QueryContextResponse>();


		logger.info("Resultfilter: start");
		logger.info("Resultfilter: response list to filter:"
				+ contextElementResponseList.toString());
		logger.info("Resultfilter: request list based on which the filtering is done:"
				+ requestList.toString());



		//numberofRequests = requestList.size();
		//TODO remove commented code from this class after testing.
		//this.contextElementResponseList = contextElementResponseList;
		//this.queryContextRequestList = requestList;

		/*
		 * Now we populate this list with empty query context responses. The number of
		 * empty responses we add corresponds to the number of requests given in the
		 * function parameter.
		 *
		 */

		for (int i = 1; i <= requestList.size(); i++) {
			QueryContextResponse qcr = new QueryContextResponse();
			filteredResultList.add(qcr);
		}

		/*
		 *  Now we run over each pair of context element response cer and request qcReq.
		 *  For each such pair we run the function filterResultIndividual, which
		 *  puts into the corresponding response in the return list all information
		 *  from the cer that is relevant for query qcReq.
		 *
		 */
		for (ContextElementResponse cer : contextElementResponseList) {
			int i = 0;
			for (QueryContextRequest qcReq : requestList) {
				filterResultIndividual(cer, qcReq,
						filteredResultList.get(i++));
			}

		}

		/*
		 * Then we run over the list of query context responses just populated. This
		 * time we remove from each response all context element responses
		 * for which no single attribute value is given.
		 *
		 * This is now already done by the individual filtering method and therefore
		 * can be removed.
		 *
		 */

//		Iterator<QueryContextResponse> it = filteredResultList.iterator();
//		while (it.hasNext()) {
//			QueryContextResponse qcr = it.next();
//			Iterator<ContextElementResponse> itCeRes = qcr
//					.getListContextElementResponse().iterator();
//			while (itCeRes.hasNext()) {
//				ContextElementResponse ceRes = itCeRes.next();
//				if (ceRes.getContextElement().getContextAttributeList().size() == 0) {
//					itCeRes.remove();
//				}
//
//			}
//
//		}

		logger.info("QueryContextResponseList after filter"
				+ filteredResultList + "end");
		logger.info("ending resultFiltering");
		return filteredResultList;
	}

	/**
	 * This the main function for filtering responses against queries. Given a context element
	 * response and a query, it extracts from the response all information relevant for the
	 * query. This information is then added to the query context response qcResp given as the third
	 * parameter.
	 *
	 *
	 * @param conElResp The {@link ContextElementResponse} to filter.
	 * @param qcReq The {@link QueryContextResponse} according to which the filtering is to be done.
	 * @param qcResp The {@link QueryContextRequest} to which the filtered response is added.
	 *
	 */
	private void filterResultIndividual(ContextElementResponse conElResp,
			QueryContextRequest qcReq, QueryContextResponse qcResp) {

		logger.debug("Evaluating context element response " + conElResp.toString() );
		logger.debug("Query to evaluate against: " + qcReq.toString());

		/*
		 * First some checks to avoid null pointer exceptions.
		 */
		if(
			qcReq == null || qcReq.getEntityIdList()==null ||
			conElResp==null || conElResp.getContextElement() == null ||
			qcResp == null
				) {
			return;
		}


		/*
		 * Here is how the method works in principle:
		 * We are given a context element response and a query context request and we
		 * want to extract from the context element response the information relevant
		 * for the query context request.
		 *
		 * The first step is to find out whether the entity specified in the context
		 * element response matches with an entity in the query. If no such match is
		 * found, then in the context element response is not relevant for the query
		 * context request.
		 *
		 * If an entity match is found, then we figure out which attributes values of
		 * the context element response have been asked for in the query. Here we also have
		 * to take the attribute domain of the context element response into account.
		 *
		 */



		/*
		 *  For executing the step of finding of whether the entity id given in the
		 *  context element response has been asked for in the query, we run through
		 *  all entity ids specified in the query context request.
		 */


		Boolean entityMatchFound = false;
		for (EntityId entityID:  qcReq.getEntityIdList()) {

			/*
			 * For each such entity id we check whether it matches with the entity
			 * id provided in the given context element response. If it does not
			 * match, we go immediately to the next entity id in the query.
			 *
			 * Note that the matcher method already takes the entity type into
			 * account.
			 *
			 */
			if (EntityIDMatcher.matcher(conElResp.getContextElement().getEntityId(),
					entityID)) {
				entityMatchFound=true;
				break;
				}

			}

		/*
		 * If no matching entity has been found in the query, the function returns without
		 * doing anything. Otherwise, it continues, knowing that a matching entity in the
		 * query has been found.
		 */
		if (entityMatchFound == false)
		{
			logger.debug("No match for entity ID found in the query");
			return;
		}

		logger.debug("Match for entity ID found in the query");

		/*
		 *
		 * It is now time to figure out which attribute values of the context
		 * element response have been asked by the query.
		 *
		 * There are two different cases where all attributes of the context
		 * element response are taken:
		 * 1) there is no attribute list specified in the query
		 * 2) the context element response specifies an attribute domain name,
		 * and this domain name appears in the attribute list in the query.
		 *
		 */

		boolean takeAllAttributes = false;

		/*
		 * For checking if all attributes can  be taken, we first look whether
		 * there is no attribute list or an empty one in the query
		 */
		if(qcReq.getAttributeList() == null || qcReq.getAttributeList().isEmpty()) {
			takeAllAttributes = true;
		}
		/*
		 * If not, we check whether the context element response specifies an attribute
		 * domain name, and if it does, we look for it in the attribute list of the
		 * response.
		 */
		else if(conElResp.getContextElement().getAttributeDomainName() != null &&
				! "".equals(conElResp.getContextElement().getAttributeDomainName())
				){

			for(String attr:qcReq.getAttributeList()){
				if( attr.equals(conElResp.getContextElement().getAttributeDomainName()))
				{
					takeAllAttributes = true;
					break;
				}
			}

		}


		/*
		 * We now initialize the attribute list for the filtered response. If all attributes
		 * are taken, the list is copied from the given context element response. Otherwise
		 * we initialize it empty and fill it afterwards manually.
		 */

		List<ContextAttribute> filteredAttrList;
		if(takeAllAttributes) {
			filteredAttrList = conElResp.getContextElement().getContextAttributeList();
		} else{
			filteredAttrList = new ArrayList<ContextAttribute>();

			/*
			 * For filling the list, we have to look for each attribute from the given
			 * context element response whether it appears in the attribute list
			 * in the query.
			 */

			Set<String> queryAttrSet = new HashSet<String>(qcReq.getAttributeList());

			for(ContextAttribute conAtt:conElResp.getContextElement().getContextAttributeList()){
				if( queryAttrSet.contains(conAtt.getName()) ){
					filteredAttrList.add(conAtt);
				}
			}
		}

		/*
		 * Now that the necessary attributes are in the list, we create a context element
		 * response around it and put it into the query.
		 *
		 * But only if the attribute list is not empty.
		 */
		if(filteredAttrList.isEmpty())
		{
			logger.debug("No matching attribute values found");
			return;
		}

		/*
		 * Create the contextElement.
		 *
		 * The filtered context element receives the attribute domain name from the original
		 * context element in case the previous check whether all attribute values can be
		 * copied had positive result.
		 */
		ContextElement filteredCE = new ContextElement();
		if(takeAllAttributes) {
			filteredCE.setAttributeDomainName(conElResp.getContextElement().getAttributeDomainName());
		}
		filteredCE.setContextAttributeList(filteredAttrList);
		filteredCE.setDomainMetadata(conElResp.getContextElement().getDomainMetadata());
		filteredCE.setEntityId(conElResp.getContextElement().getEntityId());

		/*
		 * Create context element response around the context element
		 */
		ContextElementResponse filteredCER = new ContextElementResponse();
		filteredCER.setStatusCode(conElResp.getStatusCode());
		filteredCER.setContextElement(filteredCE);

		/*
		 * Finally put the filtered context element response into the query response.
		 */
		if(qcResp.getListContextElementResponse() == null) {
			qcResp.setContextResponseList(new ArrayList<ContextElementResponse>());
		}


		qcResp.getListContextElementResponse().add(filteredCER);

		logger.debug("Filtered context element response: " + filteredCER.toString());

		return;

		/*
		 * Everything below is an old implementation of the method
		 * and can be removed.
		 *
		 */

//
//		if (conElResp.getContextElement().getAttributeDomainName() != null) {
//
//			/*
//			 * If the response specifies an attribute domain name, then we
//			 * look if the query context request specifies an attribute having
//			 * exactly that name.
//			 *
//			 * If we do not find it, we do nothing.
//			 *
//			 * bug?it seems that it is assumed that the attribute list is present in the
//			 * query. If it is not present, probably a null pointer exception will occur.
//			 * Proposed solution: isAttributeDomainNameFound should also return true if
//			 * the attribute list is null.
//			 */
//
//			if (isAttributeDomainNameFound(conElResp.getContextElement()
//					.getAttributeDomainName().toString(),
//					qcReq.getAttributeList())) {
//
//				/*
//				 * If the attribute domain name is found in the attributes of the query,
//				 * it means that all the attribute values of the context element response match
//				 * with the request.
//				 *
//				 */
//
//				/*
//				 * We create a new context element response as a copy of the given
//				 * context element response.
//				 *
//				 * The created response is added to the query context response given
//				 * as function parameter.
//				 *
//				 */
//
//				// Creating new Context Element Response and assigning context
//				// element and statuscode
//				ContextElementResponse tmpContextElementResponse = new ContextElementResponse();
//				tmpContextElementResponse.setContextElement(conElResp
//						.getContextElement());
//				tmpContextElementResponse.setStatusCode(conElResp.getStatusCode());
//
//
//				List<ContextElementResponse> tmpContextElementResponseList = qcResp
//						.getListContextElementResponse();
//				tmpContextElementResponseList.add(tmpContextElementResponse);
//				//qcResp.setContextResponseList(tmpContextElementResponseList);
//				//this is unnecessary
//
//			}
//		} else {
//
//			/*
//			 * In case no domain name is specified in the context element response,
//			 * we need to decide attribute value by attribute value whether they match with
//			 * the given query.
//			 */
//
//			String findName = "";
//
//			/*
//			 * We initialize a context element ce for the response.
//			 *
//			 */
//
//			ContextElement ce = new ContextElement(conElResp.getContextElement()
//					.getEntityId(), null, null, conElResp.getContextElement()
//					.getDomainMetadata());
//
//			List<ContextAttribute> lcaFrQueryRequestWithoutAttribute = new ArrayList<ContextAttribute>();
//
//			/*
//			 * Then we run over all attributes in the original context element
//			 * response.
//			 *
//			 * bug? verify that we do not get a null pointer exception here.
//			 */
//
//			Iterator<ContextAttribute> it1 = conElResp.getContextElement()
//					.getContextAttributeList().iterator();
//			while (it1.hasNext()) {
//				ContextAttribute ca = (ContextAttribute) it1.next();
//				findName = ca.getName();
//
//				/*
//				 * For each such attribute in the original context element response, we
//				 * iterate through
//				 * all attributes in the attribute list of the query.
//				 *
//				 * bug? here again we still need to cover the case that attribute list is not
//				 * present in the query.
//				 */
//
//				Iterator<String> it2 = qcReq.getAttributeList().iterator();
//				while (it2.hasNext()) {
//					String qcrAttribute = (String) it2.next();
//
//					if (qcrAttribute.equals(findName)) {
//
//						/*
//						 * If an attribute in the query matches the attribute in
//						 * the response, this attribute value is added to the new
//						 * context element response.
//						 *
//						 */
//
//						/*
//						 * bug? rather initialize the attribute list of ce at the
//						 * beginning and then just add to it.
//						 */
//						List<ContextAttribute> lca = new ArrayList<ContextAttribute>();
//						if (ce.getContextAttributeList() != null) {
//							lca = ce.getContextAttributeList();
//						}
//						lca.add(ca);
//						ce.setContextAttributeList(lca);
//
//					}
//				}
//
//				/*
//				 * If the attribute list in the query context
//				 * request is empty, then the above loop
//				 * did not do anything.
//				 *
//				 * Also then we add the current attribute value in the
//				 * original context element response, because the
//				 * query did not specify any attribute.
//				 *
//				 * bug? what if the attribute list in the query is null!!
//				 */
//
//				if (qcReq.getAttributeList().size() == 0) {
//
//					lcaFrQueryRequestWithoutAttribute.add(ca);
//					ce.setContextAttributeList(lcaFrQueryRequestWithoutAttribute);
//				}
//
//			}
//
//			/*
//			 *
//			 * After having populated the new context element with the relevant attributes,
//			 * we create a context element response wrapper around it.
//			 * This context element response is then added to the query response given in the
//			 * function parameter.
//			 *
//			 */
//
//
//			ContextElementResponse tmpContextElementResponse = new ContextElementResponse();
//			tmpContextElementResponse.setContextElement(ce);
//			tmpContextElementResponse.setStatusCode(conElResp.getStatusCode());
//
//			List<ContextElementResponse> tmpContextElementResponseList = qcResp
//					.getListContextElementResponse();
//			tmpContextElementResponseList.add(tmpContextElementResponse);
//			//qcResp.setContextResponseList(tmpContextElementResponseList);
//			//unnecessary
//
//		}

	}

	/*
	 * Obsolete function
	 *
	 * Checks if list of attribute contains a given attributeDomainName
	 *
	 * @param toBeMatched
	 * @param attributeList
	 * @return Returns true if the any of the Attribute is AttributeDomainName
	 *         otherwise sends false
	 */
//	private Boolean isAttributeDomainNameFound(String toBeMatched,
//			List<String> attributeList) {
//		Iterator<String> it = attributeList.iterator();
//		while (it.hasNext()) {
//			String Attribute = (String) it.next();
//			if (toBeMatched.equals(Attribute)) {
//				return true;
//			}
//		}
//
//		return false;
//	}
//
}
