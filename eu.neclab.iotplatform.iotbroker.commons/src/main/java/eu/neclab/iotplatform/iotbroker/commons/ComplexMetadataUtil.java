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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.OperationScope;
import eu.neclab.iotplatform.ngsi.api.datamodel.PEPCredentials;

/**
 * @author Flavio Cirillo (flavio.cirillo@neclab.eu)
 * 
 */
public class ComplexMetadataUtil {

	// The logger
	private static Logger logger = Logger.getLogger(ComplexMetadataUtil.class);

	private static XmlFactory xmlFactory = new XmlFactory();

	final static Pattern pattern_contextMetadataValue = Pattern
			.compile("<value>(\\S+)</value>");

	final static Pattern pattern_operationScopeValue = Pattern
			.compile("<scopeValue>(\\S+)</scopeValue>");



	private static Object getPEPCredentialsFromObject(Object value) {

		if (value instanceof eu.neclab.iotplatform.ngsi.api.datamodel.PEPCredentials) {
			return (PEPCredentials) value;
		}

		return null;
	}

	private static Object getPEPCredentialsFromString(
			String pepCredentialsString) {

		if (pepCredentialsString.toLowerCase().contains("pepcredentials")) {

			// Parse the XML and create the Segment object
			PEPCredentials pepCredentials = (PEPCredentials) xmlFactory
					.convertStringToXml(pepCredentialsString,
							PEPCredentials.class);

			return pepCredentials;

		}

		return null;
	}



	public static Object getComplexMetadataValue(String metadataType,
			Object object) {
		Object complexMetadataValue = null;

		if (object instanceof ContextMetadata) {

			if (metadataType.equals("PEPCredentials")) {
				complexMetadataValue = getPEPCredentialsFromObject(((ContextMetadata) object)
						.getValue());
			}

		} else if (object instanceof OperationScope) {

			if (metadataType.equals("PEPCredentials")) {
				complexMetadataValue = getPEPCredentialsFromObject(((OperationScope) object)
						.getScopeValue());
			}
		} else {
			return null;
		}

		if (complexMetadataValue == null) {

			// Check if it is ContextMetadata or an OperationScope
			Matcher matcher;
			if (object instanceof ContextMetadata) {
				// Create the matcher
				matcher = pattern_contextMetadataValue
						.matcher(((ContextMetadata) object).toString()
								.replaceAll("\\s+", ""));

			} else if (object instanceof OperationScope) {
				// Create the matcher
				matcher = pattern_operationScopeValue
						.matcher(((OperationScope) object).toString()
								.replaceAll("\\s+", ""));

			} else {
				return null;
			}

			String metadataInformation;

			// Extract the geoInformation
			if (matcher.find()) {
				metadataInformation = matcher.group(1);
			} else {
				return null;
			}

			logger.info("Simple GeoLocation received:" + metadataInformation);

			if (metadataType.equals("PEPCredentials")) {
				complexMetadataValue = getPEPCredentialsFromString(metadataInformation);
			}
		}

		return complexMetadataValue;

	}
}
