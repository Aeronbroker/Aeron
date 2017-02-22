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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import eu.neclab.iotplatform.iotbroker.commons.interfaces.IoTAgentInterface;
import eu.neclab.iotplatform.ngsi.api.datamodel.Code;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElement;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElementResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.OperationScope;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.ReasonPhrase;
import eu.neclab.iotplatform.ngsi.api.datamodel.ScopeTypes;
import eu.neclab.iotplatform.ngsi.api.datamodel.StatusCode;

/**
 * This class represents a thread for making NGSI 10 requests. NGSI 10 requests
 * are made by independent threads in order make several requests in parallel.
 */
@Service
public class EmbeddedIoTAgentRequestThread implements Runnable {

	/** The logger. */
	private static Logger logger = Logger
			.getLogger(EmbeddedIoTAgentRequestThread.class);

	private QueryContextRequest request;
	private QueryResponseMerger merger;
	private IoTAgentInterface embeddedIoTAgent;
	private List<ContextRegistration> embeddedAgentContextRegistration;

	/**
	 * 
	 * @return The request of this instance.
	 */
	public QueryContextRequest getRequest() {
		return request;
	}

	public EmbeddedIoTAgentRequestThread() {
		super();

	}

	/**
	 * Initializes a new RequestThread.
	 * 
	 * @param requestor
	 *            A pointer to the interface making NGSI 10 requests.
	 * @param request
	 *            The NGSI 10 request to make by this instance.
	 * @param uri
	 *            The address of the server where the request is to be sent to.
	 * @param merger
	 *            A pointer to the merger. This is the
	 *            {@link QueryResponseMerger} where the response to the request
	 *            will be inserted.
	 * @param count
	 *            Pointer to a {@link CountDownLatch} which represents the
	 *            number of active request threads. Before the thread terminates
	 *            it will decrement the latch.
	 * @param additionalRequestList
	 */
	public EmbeddedIoTAgentRequestThread(IoTAgentInterface embeddedIoTAgent,
			QueryContextRequest request, QueryResponseMerger merger,
			List<ContextRegistration> embeddedAgentContextRegistration) {

		this.embeddedIoTAgent = embeddedIoTAgent;
		this.request = request;
		this.merger = merger;
		this.embeddedAgentContextRegistration = embeddedAgentContextRegistration;
	}

	/**
	 * Runs the thread to make the request. Recall that for running is as an
	 * individual thread, the start() method has to be called instead.
	 */
	@Override
	public void run() {

		QueryContextResponse response = null;

		/*
		 * Check if historical query
		 */
		/*
		 * <operationScope <scopeType>ISO8601TimeInterval</scopeType>
		 * <scopeValue>yyyy-mm-ddThh:mm:ssZ/yyyy-mm-ddThh:mm:ssZ</scopeValue>
		 * </operationScope>
		 */
		Date startDate = null;
		Date endDate = null;
		if (request.getRestriction() != null
				&& request.getRestriction().getOperationScope() != null
				&& !request.getRestriction().getOperationScope().isEmpty()) {

			for (OperationScope scope : request.getRestriction()
					.getOperationScope()) {
				if (scope.getScopeType().equalsIgnoreCase(
						ScopeTypes.ISO8601TimeInterval.toString())) {
					if (scope.getScopeValue() instanceof String) {
						String scopeValue = (String) (scope.getScopeValue());
						String[] dates = scopeValue.split("/");

						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd'T'HH:mm:ssZ");
						try {
							startDate = sdf.parse(dates[0]);

							endDate = sdf.parse(dates[1]);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();

							String details = "Wrong scopeValue for "
									+ ScopeTypes.ISO8601TimeInterval.toString()
									+ ". Usage example: 2015-06-13T17:07:16+0200/2015-06-13T17:07:18+0200";

							logger.warn(details);
						}

					}
					break;
				}
			}
		}

		List<ContextElementResponse> contextElementResponseList = new ArrayList<ContextElementResponse>();

		List<EntityId> entityIdToRequest;
		List<String> attributeListToRequest;

		if (embeddedAgentContextRegistration == null) {
			entityIdToRequest = request.getEntityIdList();
			attributeListToRequest = request.getAttributeList();
		} else {
			attributeListToRequest = null;
			entityIdToRequest = new ArrayList<EntityId>();
			for (ContextRegistration contextRegistration : embeddedAgentContextRegistration) {
				entityIdToRequest.addAll(contextRegistration.getListEntityId());
			}
		}

		if (response == null || response.getErrorCode() == null) {

			List<ContextElement> contextElementList;

			if (startDate != null && endDate != null) {
				/*
				 * Historical Query
				 */

				contextElementList = embeddedIoTAgent.getHistoricalValues(
						entityIdToRequest, attributeListToRequest, startDate,
						endDate);

			} else {

				/*
				 * Latest Value Query
				 */

				contextElementList = embeddedIoTAgent.getLatestValues(
						entityIdToRequest, attributeListToRequest);

			}

			for (ContextElement contextElement : contextElementList) {
				contextElementResponseList
						.add(createContextElementResponse(contextElement));
			}

			response = new QueryContextResponse(contextElementResponseList,
					null);
		}

		synchronized (merger) {
			if (logger.isDebugEnabled()) {
				logger.debug("Start Merger!");
				logger.debug("Response to put into merger: " + response);
			}
			merger.put(response);
		}

	}

	private ContextElementResponse createContextElementResponse(
			ContextElement contextElement) {
		ContextElementResponse contextElementResponse = new ContextElementResponse(
				contextElement, new StatusCode(Code.OK_200.getCode(),
						ReasonPhrase.OK_200.toString(), "OK"));

		return contextElementResponse;
	}
}
