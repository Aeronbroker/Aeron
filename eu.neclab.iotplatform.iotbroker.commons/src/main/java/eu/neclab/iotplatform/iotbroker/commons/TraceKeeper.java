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

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import eu.neclab.iotplatform.ngsi.api.datamodel.OperationScope;
import eu.neclab.iotplatform.ngsi.api.datamodel.Restriction;

/**
 * This class is used to keep trace of QueryContextRequest and
 * SubscribeContextRequest in order to avoid loop between chain of IoT platform
 * 
 * @author flavio
 * 
 */
public class TraceKeeper {
	
	private static Logger logger = Logger
			.getLogger(TraceKeeper.class);
	
	/**
	 * @return The URL where agents should send there notifications to. This is
	 *         the address where the NGSI RESTful interface is reachable.
	 */
	public static String getRefURl() {
		String ref = null;
		try {
			ref = "http://" + InetAddress.getLocalHost().getHostAddress() + ":"
					+ System.getProperty("tomcat.init.port") + "/ngsi10";

		} catch (UnknownHostException e) {
			logger.error("Unknown Host", e);
		}
		return ref;
	}
	
	public static Restriction addHopToTrace(Restriction restriction) {

		/*
		 * Create restriction if it does not exist
		 */
		if (restriction == null) {
			restriction = new Restriction();
		}

		/*
		 * Create attributeExpression if it does not exist
		 */
		if (restriction.getAttributeExpression() == null) {
			restriction.setAttributeExpression("");
		}

		/*
		 * Check the OperationScopeList
		 */
		List<OperationScope> operationScopeList;
		if (restriction.getOperationScope() == null) {

			/*
			 * If we are here, create the operationScopeList and add the Trace
			 * OperationScope
			 */

			operationScopeList = new ArrayList<OperationScope>();

			// Create the Trace OperationScope
			OperationScope traceOperationScope = new OperationScope();
			traceOperationScope.setScopeType("Trace");
			traceOperationScope.setScopeValue(getRefURl());

			// Add the OperationScope
			operationScopeList.add(traceOperationScope);

			restriction.setOperationScope(operationScopeList);
		} else {
			/*
			 * If we are it means that the OperationScopeList exist. So we must
			 * check if the Trace is already there.
			 */

			operationScopeList = restriction.getOperationScope();

			OperationScope traceOperationScope = null;

			// Lets find the Trace OperationScope
			Iterator<OperationScope> operationScopeIter = restriction
					.getOperationScope().iterator();
			while (operationScopeIter.hasNext()) {
				OperationScope operationScope = operationScopeIter.next();

				if (operationScope.getScopeType() != null
						&& !operationScope.getScopeType().isEmpty()
						&& operationScope.getScopeType().equals("Trace")) {
					/*
					 * Trace OperationScope found
					 */
					traceOperationScope = operationScope;
					break;
				}
			}

			if (traceOperationScope == null) {
				/*
				 * If we are here it means that the Trace OperationScope was not
				 * found. So it will be created.
				 */
				traceOperationScope = new OperationScope();
				traceOperationScope.setScopeType("Trace");
				traceOperationScope.setScopeValue(getRefURl());
				operationScopeList.add(traceOperationScope);
			} else {
				/*
				 * If we are here it means that the Trace OperationScope was
				 * found. So it will be populated.
				 */
				String trace = (String) traceOperationScope.getScopeValue();
				trace = trace + ";" + getRefURl();
				traceOperationScope.setScopeValue(trace);
			}

		}

		return restriction;

	}

	public static boolean checkRequestorHopVSTrace(Restriction restriction,
			String hopReference) {
		if (restriction == null || restriction.getOperationScope() == null
				|| restriction.getOperationScope().isEmpty()) {
			return false;
		}

		Iterator<OperationScope> operationScopeIter = restriction
				.getOperationScope().iterator();
		while (operationScopeIter.hasNext()) {
			OperationScope operationScope = operationScopeIter.next();

			if (operationScope.getScopeType() != null
					&& !operationScope.getScopeType().isEmpty()
					&& operationScope.getScopeType().equals("Trace")) {

				String trace = (String) operationScope.getScopeValue();

				String[] hops = trace.split(";");
				for (int i = 0; i < hops.length; i++) {
					try {
						if (new URI(hops[i]).equals(new URI(hopReference)) ||
								new URI(hops[i]+"/").equals(new URI(hopReference)) ||
								new URI(hops[i]).equals(new URI(hopReference+"/")) ) {
							return true;
						}
					} catch (URISyntaxException e) {
						logger.debug("Exception: "+e);
					}
				}
				break;
			}
		}

		return false;
	}

}
