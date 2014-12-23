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
package eu.neclab.iotplatform.iotbroker.commons;

import java.util.Iterator;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElementResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextResponse;

/**
 *  This class contains a static method for applying restrictions
 *  to QueryContextResponse instances.
 */
public class Restriction {


	private static Logger logger = Logger.getLogger(Restriction.class);
	private static final XmlFactory xmlFactory = new XmlFactory();
	
	/**
	 * Modifies a QueryContextResponse instance by applying a restriction
	 * to it, eliminating all parts of the response that do not match the
	 * restriction.
	 * 
	 * @param attributeExpr The attribute expression representing the restriction
	 * @param response The QueryContextResponse where the restriction is to be
	 * applied to.
	 * 
	 * 
	 */
	public static void applyRestriction(String attributeExpr,
			QueryContextResponse response) {

		// Apply the Restriction
		XPath xpath = XPathFactory.newInstance().newXPath();

		try {
			XPathExpression expr = xpath.compile(attributeExpr);

			Document doc = xmlFactory.stringToDocument(response.toString());
			Object result = expr.evaluate(doc, XPathConstants.NODESET);

			NodeList nodes = (NodeList) result;


			Iterator<ContextElementResponse> i = response
					.getListContextElementResponse().iterator();

			while (i.hasNext()) {

				ContextElementResponse contextElresp = i.next();
				boolean doesNotAppear = true;

				for (int j = 0; j < nodes.getLength(); j++) {
					if (contextElresp.getContextElement().getEntityId().getId()
							.equals(nodes.item(j).getTextContent())) {

						doesNotAppear = false;
						break;
					}
				}

				if (doesNotAppear) {
					i.remove(); // to be tested
				}
			}

		} catch (XPathExpressionException e) {
			logger.debug("XPathExpressionException",e);
		}

	}
}
