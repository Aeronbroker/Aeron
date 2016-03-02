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

	// /**
	// * Modifies a QueryContextResponse instance by applying a restriction to
	// it,
	// * eliminating all parts of the response that do not match the
	// restriction.
	// *
	// * @param attributeExpr
	// * The attribute expression representing the restriction
	// * @param response
	// * The QueryContextResponse where the restriction is to be
	// * applied to.
	// *
	// *
	// */
	// public static void applyRestriction(String attributeExpr,
	// QueryContextResponse response) {
	//
	// // Apply the Restriction
	// XPath xpath = XPathFactory.newInstance().newXPath();
	//
	// try {
	// XPathExpression expr = xpath.compile(attributeExpr);
	//
	// Document doc = xmlFactory.stringToDocument(response.toString());
	// Object result = expr.evaluate(doc, XPathConstants.NODESET);
	//
	// NodeList nodes = (NodeList) result;
	//
	// Iterator<ContextElementResponse> i = response
	// .getListContextElementResponse().iterator();
	//
	// while (i.hasNext()) {
	//
	// ContextElementResponse contextElresp = i.next();
	// boolean doesNotAppear = true;
	//
	// for (int j = 0; j < nodes.getLength(); j++) {
	// if (contextElresp.getContextElement().getEntityId().getId()
	// .equals(nodes.item(j).getTextContent())) {
	//
	// doesNotAppear = false;
	// break;
	// }
	// }
	//
	// if (doesNotAppear) {
	// i.remove(); // to be tested
	// }
	// }
	//
	// } catch (XPathExpressionException e) {
	// logger.debug("XPathExpressionException", e);
	// }
	//
	// }

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

	public static List<ContextElementResponse> applyOnValueRestriction(
			List<ContextElementResponse> contextElementResponseList,
			String onValueAttributeExpr) {
		List<ContextElementResponse> filteredContextElementResponseList = new ArrayList<ContextElementResponse>();

		for (ContextElementResponse contextElementResponse : contextElementResponseList) {

			ContextElementResponse filteredContextElementResponse = Restriction
					.applyOnValueRestriction(contextElementResponse,
							onValueAttributeExpr);

			if (filteredContextElementResponse != null) {
				filteredContextElementResponseList
						.add(filteredContextElementResponse);
			}

		}

		return filteredContextElementResponseList;

	}

	public static ContextElementResponse applyOnValueRestriction(
			ContextElementResponse contextElementResponse,
			String onValueAttributeExpr) {

		/*
		 * Creating the new ContextElementResponse that will contain the
		 * filtered ContextAttribute. The other fields will remain unchanged.
		 */
		ContextElementResponse filteredContextElementResponse = new ContextElementResponse(
				new ContextElement(contextElementResponse.getContextElement()
						.getEntityId(), contextElementResponse
						.getContextElement().getAttributeDomainName(), null,
						contextElementResponse.getContextElement()
								.getDomainMetadata()),
				contextElementResponse.getStatusCode());

		// Filter out context attributes
		List<ContextAttribute> filteredContextAttributeList = Restriction
				.applyOnValueRestriction(onValueAttributeExpr,
						contextElementResponse.getContextElement()
								.getContextAttributeList());

		if (filteredContextAttributeList == null
				|| filteredContextAttributeList.isEmpty()) {
			return null;
		}

		// Set the new context attribute
		filteredContextElementResponse.getContextElement()
				.setContextAttributeList(filteredContextAttributeList);

		return filteredContextElementResponse;

	}

	public static List<ContextAttribute> applyOnValueRestriction(
			String onValueAttributeExpr,
			List<ContextAttribute> contextAttributeList) {

		List<ContextAttribute> filteredContextAttributeList = new ArrayList<ContextAttribute>();

		/*
		 * Here we create a dummy ContextElement meant to contain the
		 * ContextAttributeList. With this approach we need to apply the XPATH
		 * only once for the full list of contextAttribute
		 */
		ContextElement contextElement = new ContextElement();

		contextElement.setContextAttributeList(contextAttributeList);

		// Apply the Restriction
		XPath xpath = XPathFactory.newInstance().newXPath();

		try {

			/*
			 * Here we extract which attributes need to be extract from the
			 * attributeExpression
			 */
			Set<String> attributeToFilterSet = new HashSet<String>();
			Pattern pattern = Pattern
					.compile("name=[',\",\\\"](.*?)[',\",\\\"]");
			Matcher matcher = pattern.matcher(onValueAttributeExpr);
			while (matcher.find()) {
				attributeToFilterSet.add(matcher.group(1));
			}

			/*
			 * Now we apply the XPATH
			 */
			XPathExpression expr = xpath.compile(onValueAttributeExpr);

			Document doc = xmlFactory.stringToDocument(contextElement
					.toString());
			NodeList nodes = (NodeList) expr.evaluate(doc,
					XPathConstants.NODESET);

			Set<String> selectedAttributes = new HashSet<String>();

			for (int j = 0; j < nodes.getLength(); j++) {

				NodeList childNodes = nodes.item(j).getChildNodes();

				String attributeName = null;
				String attributeValue = null;

				for (int i = 0; i < childNodes.getLength(); i++) {
					if ("name".equalsIgnoreCase(childNodes.item(i)
							.getNodeName())) {

						attributeName = childNodes.item(i).getTextContent();

					} else if ("contextValue".equalsIgnoreCase(childNodes.item(
							i).getNodeName())) {

						attributeValue = childNodes.item(i).getTextContent();

					}

					if (attributeName != null && attributeValue != null) {
						selectedAttributes.add(attributeName + ":::::"
								+ attributeValue);
						break;
					}
				}

			}

			for (ContextAttribute contextAttribute : contextElement
					.getContextAttributeList()) {

				/*
				 * If the ContextAttribute is not in the attribute set to be
				 * filtered add it. If it is in the attribute set to be filtered
				 * then check if it is one of the selected ones
				 */
				if (!attributeToFilterSet.contains(contextAttribute.getName())
						|| selectedAttributes.contains(contextAttribute
								.getName()
								+ ":::::"
								+ contextAttribute.getContextValue())) {
					filteredContextAttributeList.add(contextAttribute);
				}

			}

		} catch (XPathExpressionException e) {
			logger.debug("XPathExpressionException", e);
		}

		return filteredContextAttributeList;

	}

	public static void main(String[] args) {
		String mydata = "//contextAttribute[name='noise'][contextValue>68]|//contextAttribute[name=\"temperature\"][contextValue>68]";
		Pattern pattern = Pattern.compile("name=[',\",\\\"](.*?)[',\",\\\"]");
		Matcher matcher = pattern.matcher(mydata);
		while (matcher.find()) {
			System.out.println(matcher.group(1));

		}

	}
}
