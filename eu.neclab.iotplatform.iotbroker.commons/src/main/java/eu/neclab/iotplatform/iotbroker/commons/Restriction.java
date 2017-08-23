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

package eu.neclab.iotplatform.iotbroker.commons;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import eu.neclab.iotplatform.ngsi.api.datamodel.ContextAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElement;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElementResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextResponse;

/**
 * This class contains a static method for applying restrictions to
 * QueryContextResponse instances.
 */
public class Restriction {

	private static Logger logger = Logger.getLogger(Restriction.class);
	private static final XmlFactory xmlFactory = new XmlFactory();

	public static List<ContextElementResponse> applyRestriction(
			String attributeExpr,
			List<ContextElementResponse> contextElementResponseList) {

		List<ContextElementResponse> filteredContextElementResponseList = new ArrayList<ContextElementResponse>();

		QueryContextResponse dummyQueryContextResponse = new QueryContextResponse(
				contextElementResponseList, null);

		// Apply the Restriction
		XPath xpath = XPathFactory.newInstance().newXPath();

		try {
			XPathExpression expr = xpath.compile(attributeExpr);

			Document doc = xmlFactory
					.stringToDocument(dummyQueryContextResponse.toString());
			Object result = expr.evaluate(doc, XPathConstants.NODESET);

			NodeList nodes = (NodeList) result;

			for (ContextElementResponse contextElementResponse : contextElementResponseList) {
				boolean appear = false;

				for (int j = 0; j < nodes.getLength(); j++) {
					if (contextElementResponse.getContextElement()
							.getEntityId().getId()
							.equals(nodes.item(j).getTextContent())) {

						appear = true;
						break;
					}
				}

				if (appear) {
					filteredContextElementResponseList
							.add(contextElementResponse);
				}
			}

		} catch (XPathExpressionException e) {
			logger.debug("XPathExpressionException", e);
		}

		return filteredContextElementResponseList;

	}

	/**
	 * It returns a new QueryContextResponse modified the input one by applying
	 * a restriction to it, eliminating all parts of the response that do not
	 * match the restriction.
	 * 
	 * @param attributeExpr
	 *            The attribute expression representing the restriction
	 * @param queryContextResponse
	 *            The input QueryContextResponse (it will be not changed).
	 * @return
	 */
	public static QueryContextResponse applyRestriction(String attributeExpr,
			QueryContextResponse queryContextResponse) {

		QueryContextResponse filteredQueryContextResponse = new QueryContextResponse(
				null, queryContextResponse.getErrorCode());

		List<ContextElementResponse> filteredContextElementResponse = applyRestriction(
				attributeExpr,
				queryContextResponse.getListContextElementResponse());

		filteredQueryContextResponse
				.setContextResponseList(filteredContextElementResponse);

		return filteredQueryContextResponse;

	}

	/**
	 * It returns a new NotifyContextRequest modifying the input one by applying
	 * a restriction to it, eliminating all parts of the response that do not
	 * match the restriction.
	 * 
	 * @param entityIdAttributeExpr
	 *            Restriction on the entityId
	 * @param onValueAttributeExpr
	 *            Restriction on the attributes (given in the ONVALUE
	 *            NotifyCondition Restriction)
	 * @param notifyContextRequest
	 * @return
	 */
	public static NotifyContextRequest applyRestriction(
			String entityIdAttributeExpr, String onValueAttributeExpr,
			NotifyContextRequest notifyContextRequest) {

		NotifyContextRequest filteredNotifyContextRequest = new NotifyContextRequest(
				notifyContextRequest.getSubscriptionId(),
				notifyContextRequest.getOriginator(), null);

		List<ContextElementResponse> filteredContextElementResponseList = new ArrayList<ContextElementResponse>();

		// First filter against the entityIdAttributeExpr
		if (entityIdAttributeExpr != null && !entityIdAttributeExpr.isEmpty()) {
			filteredContextElementResponseList = applyRestriction(
					entityIdAttributeExpr,
					notifyContextRequest.getContextElementResponseList());
		} else {
			filteredContextElementResponseList.addAll(notifyContextRequest
					.getContextElementResponseList());
		}

		// Then filter against the onValueAttributeExpr
		if (onValueAttributeExpr != null && !onValueAttributeExpr.isEmpty()) {
			filteredContextElementResponseList = applyRestriction(
					onValueAttributeExpr, filteredContextElementResponseList);
		}

		// Finally set the filteredContextElementResponse in the
		// notifyContextRequest to be returned
		filteredNotifyContextRequest
				.setContextResponseList(filteredContextElementResponseList);

		return filteredNotifyContextRequest;
	}

}
