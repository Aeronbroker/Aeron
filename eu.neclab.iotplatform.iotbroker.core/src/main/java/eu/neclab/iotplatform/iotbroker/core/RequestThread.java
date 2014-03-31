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

 package eu.neclab.iotplatform.iotbroker.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.neclab.iotplatform.iotbroker.commons.EntityIDMatcher;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.ResultFilterInterface;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElementResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextResponse;
import eu.neclab.iotplatform.ngsi.api.ngsi10.Ngsi10Requester;
import eu.neclab.iotplatform.ngsi.association.datamodel.AssociationDS;

/**
 * This class represents a thread for making NGSI 10 requests. NGSI 10 requests
 * are made by independent threads in order make several requests in parallel.
 */
@Service
public class RequestThread implements Runnable {

	/** The logger. */
	private static Logger logger = Logger.getLogger(RequestThread.class);


	private QueryContextRequest request;
	private URI uri;
	private QueryResponseMerger merger;
	private CountDownLatch count;

	private Ngsi10Requester requestor;

	private List<AssociationDS> additionalRequestList;

	@Autowired
	private ResultFilterInterface resultFilter = null;

	/**
	 * @return A pointer to the result filter this RequestThread instance
	 * uses. Note that the result filter is retrieved via the OSGi framework.
	 */
	public ResultFilterInterface getResultFilter() {
		return resultFilter;
	}

	/**
	 * Instructs the object to get load the result filter from the
	 * given result filter interface.
	 *
	 * @param resultFilter The result filter interface.
	 */
	public void setResultFilter(ResultFilterInterface resultFilter) {
		this.resultFilter = resultFilter;
	}

	/**
	 *
	 * @return The request of this instance.
	 */
	public QueryContextRequest getRequest() {
		return request;
	}


	public RequestThread() {
		super();
		
	}

	/**
	 * Initializes a new RequestThread.
	 *
	 * @param requestor A pointer to the interface making NGSI 10 requests.
	 * @param request The NGSI 10 request to make by this instance.
	 * @param uri The address of the server where the request is to be sent to.
	 * @param merger A pointer to the merger. This is the {@link QueryResponseMerger}
	 * where the response to the request will be inserted.
	 * @param count Pointer to a {@link CountDownLatch} which represents the number of
	 * active request threads. Before the thread terminates it will decrement the latch.
	 * @param additionalRequestList
	 */
	public RequestThread(Ngsi10Requester requestor,
			QueryContextRequest request, URI uri, QueryResponseMerger merger,
			CountDownLatch count, List<AssociationDS> additionalRequestList) {

		this.requestor = requestor;
		this.request = request;
		this.uri = uri;
		this.merger = merger;
		this.count = count;
		this.additionalRequestList = additionalRequestList;

	}

	/**
	 * Runs the thread to make the request. Recall  that for running is as an individual
	 * thread, the start() method has to be called instead.
	 */
	@Override
	public void run() {

		QueryContextResponse response = requestor.queryContext(request, uri);

		if (resultFilter != null) {
			logger.info("-----------++++++++++++++++++++++Begin Filter");
			List<QueryContextRequest> lqcReq = new ArrayList<QueryContextRequest>();
			lqcReq.add(request);
			List<ContextElementResponse> lceRes = response.getListContextElementResponse();
			logger.info("-----------++++++++++++++++++++++ QueryContextRequest:"
							+ lqcReq.toString()
							+ " ContextElementResponse:"
							+ lceRes.toString());

			logger.info(lqcReq.size());
			logger.info(lceRes.size());


			List<QueryContextResponse> lqcRes =resultFilter.filterResult(lceRes, lqcReq);

			if (lqcRes.size() == 1) {
				response = lqcRes.get(0);
			}
			logger.info("-----------++++++++++++++++++++++ After Filter ListContextElementResponse:"
							+ lqcRes.toString()
							+ " ContextElementResponse:"
							+ lqcRes.toString());
			logger.info("-----------++++++++++++++++++++++End Filter");
		}

		/*
		 * Updating EntityID and Attribute based on the transitiveList of
		 * associations begin
		 */
		// Checking if transitiveList contains associations
		if (!additionalRequestList.isEmpty()) {
			// Getting List of ContextElementResponse from QueryContextResponse
			List<ContextElementResponse> lContextElementResponse = response
					.getListContextElementResponse();
			// Creating new List of ContextElementResponse
			List<ContextElementResponse> contextElementRespList = new ArrayList<ContextElementResponse>();
			// For each of the ContextElementResponse
			for (ContextElementResponse contEle : lContextElementResponse) {

				for (AssociationDS aDS : additionalRequestList) {

					// Checking if EntityID of QueryContextResponse matches with
					// any source entity id of transitiveList of associations
					if (EntityIDMatcher.matcher(aDS.getSourceEA().getEntity(),
							contEle.getContextElement().getEntityId())) {

						// Checking if Attribute of source EntityID of
						// transitiveList of associations is not empty or null

						if (!"".equals(aDS.getSourceEA().getEntityAttribute())) {
							// updating the EntityId with the Target EntityId of
							// transitiveList of associations
							contEle.getContextElement().setEntityId(
									aDS.getTargetEA().getEntity());
							boolean ifAttributeDomainNameExists = false;

							if (contEle.getContextElement()
									.getAttributeDomainName() != null && !contEle.getContextElement()
										.getAttributeDomainName().equals("")) {
									contEle.getContextElement()
											.setAttributeDomainName(
													aDS.getTargetEA()
															.getEntityAttribute());
									ifAttributeDomainNameExists = true;
								}


							if (!ifAttributeDomainNameExists) {
								// updating the Attribute of same EntityId with
								// the
								// Attribute of Target EntityId of
								// transitiveList of
								// associations
								List<ContextAttribute> lca = new ArrayList<ContextAttribute>();
								for (ContextAttribute ca : contEle
										.getContextElement()
										.getContextAttributeList()) {
									if (ca.getName().equals(
											aDS.getSourceEA()
													.getEntityAttribute())) {
										ca.setName(aDS.getTargetEA()
												.getEntityAttribute());
										lca.add(ca);

									}
								}
								contEle.getContextElement()
										.setContextAttributeList(null);
								contEle.getContextElement()
										.setContextAttributeList(lca);
							}

						} else if ("".equals(aDS.getSourceEA().getEntityAttribute()
								)) {

							contEle.getContextElement().setEntityId(
									aDS.getTargetEA().getEntity());

							if (!"".equals(aDS.getTargetEA().getEntityAttribute()
									)) {
								List<ContextAttribute> lca = new ArrayList<ContextAttribute>();
								for (ContextAttribute ca : contEle
										.getContextElement()
										.getContextAttributeList()) {
									ca.setName(ca.getName());
									lca.add(ca);
								}
								contEle.getContextElement()
										.setContextAttributeList(null);
								contEle.getContextElement()
										.setContextAttributeList(lca);
							}
						}
						// Adding the ContextElement to new
						// ContextElementResponse
						contextElementRespList.add(contEle);
					}
				}
			}
			QueryContextResponse updatedResponse = new QueryContextResponse();
			updatedResponse.setContextResponseList(contextElementRespList);
			updatedResponse.setErrorCode(response.getErrorCode());
			response = updatedResponse;
		}


		synchronized (merger) {
			merger.put(response);
			count.countDown();
		}

	}

}
