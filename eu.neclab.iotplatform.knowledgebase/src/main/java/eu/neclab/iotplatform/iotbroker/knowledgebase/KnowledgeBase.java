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

package eu.neclab.iotplatform.iotbroker.knowledgebase;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.neclab.iotplatform.iotbroker.commons.FullHttpRequester;
import eu.neclab.iotplatform.iotbroker.commons.FullHttpResponse;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.KnowledgeBaseInterface;

public class KnowledgeBase implements KnowledgeBaseInterface {

	private static final Logger logger = Logger.getLogger(KnowledgeBase.class);

	// String representing the knowledgeBase Address
	private String knowledgeBaseAddress;

	// String representing the knowledgeBase Port.
	private String knowledgeBasePort;

	private URL url;

	public KnowledgeBase() {
		Properties prop = new Properties();
		InputStream input = null;

		try {

			// Read properties from file
			input = new FileInputStream(System.getProperty("dir.config")
					+ "/iotbrokerconfig/knowledgeBase/knowledgeBase.properties");

			// Load the properties file
			prop.load(input);

			// Set the PostgreSQL url location
			knowledgeBaseAddress = prop.getProperty("knowledgebase_address",
					"http://127.0.0.1");

			// Set the database name
			knowledgeBasePort = prop.getProperty("knowledgebase_port", "8080");

			// Generate the complete url
			url = new URL(knowledgeBaseAddress + ":" + knowledgeBasePort);

		} catch (IOException ex) {
			logger.error("Error!! ", ex);
		} finally {
			if (input != null) {
				try {
					// Close input file
					input.close();
				} catch (IOException e) {
					logger.error("Error!! ", e);
				}
			}
		}

	}

	@Override
	public Set<URI> getSubTypes(URI type) {

		// Example:
		// http://localhost:8015/query?request=getAllSubTypes&entityType=Node

		Set<URI> subtypes = null;

		// Lets create the query string
		String queryString = "request=getAllSubTypes&";
		String[] namespaceAndType = type.toString().split("#");
		if (namespaceAndType.length == 1) {
			queryString += "entityType=" + namespaceAndType[0];
		} else {
			queryString += "entityType=" + namespaceAndType[1];
		}

		URL fullUrl;
		try {
			fullUrl = new URL(url + "/query?" + queryString);

			FullHttpResponse fullHttpResponse = FullHttpRequester
					.sendGet(fullUrl);

			if (fullHttpResponse.getStatusLine().getStatusCode() == 200) {

				subtypes = parseTypes(fullHttpResponse.getBody());

			} else {
				logger.info(String
						.format("Problem when contacting the KnowledgeBase server. StatusCode: %s. ReasonPhrase: %s.",
								fullHttpResponse.getStatusLine()
										.getStatusCode(), fullHttpResponse
										.getStatusLine().getReasonPhrase()));
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return subtypes;
	}

	private Set<URI> parseTypes(String body) {

		JsonParser jsonParser = new JsonParser();
		JsonObject jo = (JsonObject) jsonParser.parse(body);

		Set<URI> subtypes = null;

		/*
		 * Parse results
		 */
		JsonArray jsonResults;
		if (jo.get("results") != null
				&& jo.getAsJsonObject("results").get("bindings") != null) {

			if (jo.getAsJsonObject("results").get("bindings").isJsonArray()) {
				jsonResults = jo.getAsJsonObject("results").getAsJsonArray(
						"bindings");
			} else {
				jsonResults = new JsonArray();
				jsonResults.add(jo.getAsJsonObject("results").getAsJsonObject(
						"bindings"));
			}

			if (!jsonResults.isJsonNull()) {
				subtypes = new HashSet<URI>();

				for (JsonElement binding : jsonResults) {

					URI subtype = extractSubtypeFromBinding(binding);
					if (subtype != null) {
						subtypes.add(subtype);
					}

				}
			}

		}

		return subtypes;

	}

	private URI extractSubtypeFromBinding(JsonElement binding) {

		URI subtype = null;

		if (binding.getAsJsonObject().get("type") != null
				&& binding.getAsJsonObject().getAsJsonObject("type")
						.get("value") != null) {
			try {
				subtype = new URI(binding.getAsJsonObject()
						.getAsJsonObject("type").get("value").getAsString());
			} catch (URISyntaxException e) {
				logger.info("Bad uri as type");
			}

		}

		return subtype;

	}

	@Override
	public Set<URI> getSuperTypes(URI type) {

		// Example:
		// http://localhost:8015/query?request=getAllSuperTypes&entityType=BusSensor

		Set<URI> superTypes = null;

		// Lets create the query string
		String queryString = "request=getAllSuperTypes&";
		String[] namespaceAndType = type.toString().split("#");
		if (namespaceAndType.length == 1) {
			queryString += "entityType=" + namespaceAndType[0];
		} else {
			queryString += "entityType=" + namespaceAndType[1];
		}

		URL fullUrl;
		try {
			fullUrl = new URL(url + "/query?" + queryString);

			FullHttpResponse fullHttpResponse = FullHttpRequester
					.sendGet(fullUrl);

			if (fullHttpResponse.getStatusLine().getStatusCode() == 200) {

				superTypes = parseTypes(fullHttpResponse.getBody());

			} else {
				logger.warn(String
						.format("Problem when contacting the KnowledgeBase server. StatusCode: %s. ReasonPhrase: %s.",
								fullHttpResponse.getStatusLine()
										.getStatusCode(), fullHttpResponse
										.getStatusLine().getReasonPhrase()));
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return superTypes;
	}

}
