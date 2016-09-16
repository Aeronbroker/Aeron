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

package eu.neclab.iotplatform.iotbroker.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElement;
import eu.neclab.iotplatform.ngsi.api.datamodel.NgsiStructure;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextRequest_OrionCustomization;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextResponse_OrionCustomization;

/**
 * Represents a generic client for NGSI via HTTP.
 */
public class HttpConnectionClient {

	/* The logger. */
	private static Logger logger = Logger.getLogger(HttpConnectionClient.class);

	/* The Constant connectionTimeout. */
	private final static int CONNECTION_TIMEOUT = 3000;

	/*
	 * Creates an Http Connection from a url, a resource pathname and an HTTP
	 * method.
	 * 
	 * @param url The URL of the server to connect to.
	 * 
	 * @param resource The name of resource on the server.
	 * 
	 * @param method The name of the HTTP method. Expected to be "GET", "PUT",
	 * "POST", or "DELETE".
	 * 
	 * @param contentType the content type
	 * 
	 * @return Returns the connection.
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private HttpURLConnection createConnection(URL url, String resource,
			String method, String contentType, String xAuthToken)
			throws IOException {

		// initialize the URL of the connection as the concatenation of
		// parameters url and resource.
		URL connectionURL = composeUrl(url.toString(), resource);

		logger.debug("Connecting to: " + connectionURL.toString());

		// initialize and configure the connection
		HttpURLConnection connection = (HttpURLConnection) connectionURL
				.openConnection();

		// enable both input and output for this connection
		connection.setDoInput(true);
		connection.setDoOutput(true);

		// configure other things
		connection.setInstanceFollowRedirects(false);
		connection.setRequestProperty("Content-Type", contentType);
		connection.setRequestProperty("Accept", contentType);
		connection.setRequestProperty("X-Auth-Token", xAuthToken);

		// set connection timeout
		connection.setConnectTimeout(CONNECTION_TIMEOUT);

		// set the request method to be used by the connection
		connection.setRequestMethod(method);

		// finally return the nicely configured connection
		return connection;

	}

	private URL composeUrl(String url, String resource) {

		try {
			if (url.endsWith("/")) {
				if (resource.startsWith("/")) {
					return new URL(url + resource.substring(1));
				} else {
					return new URL(url + resource);
				}
			} else {
				if (resource.startsWith("/")) {
					return new URL(url + resource);
				} else {
					return new URL(url + "/" + resource);
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Makes an HTTP connection to a server.
	 * 
	 * @param url
	 *            The URL of the server to connect to.
	 * @param resource
	 *            The identifier of the resource on the server to address.
	 * @param method
	 *            The name of the HTTP method. Expected to be "GET", "PUT",
	 *            "POST", or "DELETE".
	 * @param request
	 *            The message body of the request to send. The object is
	 *            expected to be an instance of an NGSI 9 or 10 message body.
	 * @param contentType
	 *            The content type that is announced in the request header. The
	 *            request object in the message body will be sent in XML format
	 *            for contentType "application/xml" and JSON format otherwise.
	 * @param xAuthToken
	 *            The security token used by this connection in order to connect
	 *            to a component secured by the FIWARE security mechanisms.
	 * 
	 * @return Either returns the response body returned by the server (as a
	 *         String) or an error message.
	 */
	public String initializeConnection(URL url, String resource, String method,
			Object request, String contentType, String xAuthToken) {

		// initialize variables
		HttpURLConnection connection = null;
		InputStream is = null;
		OutputStream os = null;
		String resp = null;

		try {

			// use the above setConnection method to get a connection from url,
			// resource and the method.
			connection = createConnection(url, resource, method, contentType,
					xAuthToken);

			// get the OutputStram form the connection
			os = connection.getOutputStream();

			if (contentType.equals("application/xml")) {
				// connect using XML message body

				// logger.info("URL" + url + resource);

				logger.info("Starting connection with: " + url + resource);

				// logger.info("Send the QUERY!");

				if (request instanceof NgsiStructure) {
					String string = ((NgsiStructure) request).toString();
					os.write(string.getBytes(Charset.forName("UTF-8")));
				} else {

					// create a context from the request class
					JAXBContext requestContext = JAXBContext
							.newInstance(request.getClass());

					// Create a Marshaller from the context
					Marshaller m = requestContext.createMarshaller();

					// Ask the marshaller to marshall the request for you
					// logger.info("Request Class: "
					// + request.getClass().toString());
					m.marshal(request, os);
				}

			} else {
				// connect using JSON message body

				logger.info("Starting connection with: " + url + resource);

				// get the OutputStram form the connection
				os = connection.getOutputStream();

				if (request instanceof NgsiStructure) {
					String string = ((NgsiStructure) request).toJsonString();
					os.write(string.getBytes(Charset.forName("UTF-8")));
				} else {

					ObjectMapper mapper = new ObjectMapper();
					SerializationConfig config = mapper
							.getSerializationConfig();
					config.setSerializationInclusion(Inclusion.NON_NULL);
					// m.setProperty(Marshaller.JAXB_ENCODING, "Unicode");

					try {
						logger.info("----------------->"
								+ mapper.writeValueAsString(request));
						mapper.writeValue(os, request);
					} catch (JsonGenerationException e) {
						if (logger.isDebugEnabled()) {
							logger.debug("JsonGenerationException", e);
						}
					} catch (JsonMappingException e) {
						if (logger.isDebugEnabled()) {
							logger.debug("JsonMappingException", e);
						}
					} catch (IOException e) {
						if (logger.isDebugEnabled()) {
							logger.debug("IOException", e);
						}
					}
				}

			}
			logger.info("Output Stream to " + url + resource + " : "
					+ os.toString());

			// send Message
			os.flush();
			// close connection again
			os.close();

			// now it is time to receive the response
			// get input stream from the connection
			is = connection.getInputStream();

			StringWriter writer = new StringWriter();
			IOUtils.copy(is, writer, "UTF-8");
			resp = writer.toString();

			is.close();

			logger.info("------------->Response = " + resp);

			if (connection.getResponseCode() == 415) {

				logger.info("Connection Error: Format not supported by " + url);

				return "415";

			}

			return resp;

		} catch (ConnectException e) {

			logger.info("IOException: Impossible to establish a connection with "
					+ url + ":" + e.getMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("ConnectException", e);
			}

			return "500 - Connection Error - the URL: " + url
					+ " create an internal error!";

		} catch (JAXBException e) {
			logger.info("XML Parse Error!", e);
			logger.debug("JAXBException", e);
			return "500 - XML Parse Error! Response from: " + url
					+ " is not correct!";
		} catch (IOException e) {

			try {
				if (connection != null && connection.getResponseCode() == 415) {

					logger.info("Connection Error: Format not supported by "
							+ url);

					return "415";

				}
			} catch (IOException e1) {
				if (logger.isDebugEnabled()) {
					logger.debug("IOException", e);
				}
			}

			logger.info("500 - Error I/O with: " + url);
			logger.info("IOException: Impossible to establish a connection with "
					+ url + ": " + e.getMessage());

			return "500 - Error I/O with: " + url;

		} finally {

			if (connection != null) {

				connection.disconnect();
			}
			logger.info("Connection Closed!");
		}

	}

	public String initializeUpdateContextConnectionToOrion(URL url,
			String resource, UpdateContextRequest request, String xAuthToken) {

		// initialize variables
		HttpURLConnection connection = null;
		InputStream is = null;
		OutputStream os = null;
		String resp = null;

		// Orion is accepting a non-NGSI standard updateContext e.g.
		/*
		 * { "contextElements": [ { "type": "Room", "isPattern": "false", "id":
		 * "Room1", "attributes": [ { "name": "temperature", "type": "float",
		 * "value": "23" }, { "name": "pressure", "type": "integer", "value":
		 * "720" } ] } ], "updateAction": "APPEND" }
		 */

		// List<ContextElement> contextElementsForOrion = new
		// ArrayList<ContextElement>();
		// for (ContextElement contextElement : request.getContextElement()) {
		//
		// // List<ContextAttribute> contextAttributesForOrion = new
		// // ArrayList<ContextAttribute>();
		// // for (ContextAttribute contextAttribute : contextElement
		// // .getContextAttributeList()) {
		// //
		// // ContextAttribute contextAttributeForOrion = new ContextAttribute(
		// // contextAttribute.getName(), contextAttribute.getType(),
		// // contextAttribute.getContextValue());
		// // contextAttributesForOrion.add(contextAttributeForOrion);
		// //
		// // }
		//
		// ContextElement contextElementForOrion = new ContextElement(
		// contextElement.getEntityId(), null,
		// contextElement.getContextAttributeList(), null);
		// contextElementsForOrion.add(contextElementForOrion);
		// }
		// UpdateContextRequest orionUpdateContextRequest = new
		// UpdateContextRequest(
		// contextElementsForOrion, request.getUpdateAction());

		UpdateContextRequest_OrionCustomization orionUpdateContextRequest = new UpdateContextRequest_OrionCustomization(
				request);
		logger.info("Translating UpdateContextRequest :"
				+ request.toJsonString() + " to the Orion Customization:"
				+ orionUpdateContextRequest.toJsonString());

		try {

			// use the above setConnection method to get a connection from url,
			// resource and the method.
			connection = createConnection(url, resource, "POST",
					"application/json", xAuthToken);

			// get the OutputStram form the connection
			os = connection.getOutputStream();

			// connect using JSON message body

			// get the OutputStram form the connection
			os = connection.getOutputStream();

			String string = ((NgsiStructure) orionUpdateContextRequest)
					.toJsonString();

			// string = string.replaceAll("contextValue", "value");

			// // The first is for deleting if it is followed by a comma (other
			// // fields next to it)
			// string = string.replaceAll(".attributeDomainName.:\\[.*?\\],",
			// "");
			// // The second is for deleting if it is followed by the bracket
			// (end
			// // of the structure)
			// string = string.replaceAll(".attributeDomainName.:\\[.*?\\]}",
			// "}");
			//
			// // The first is for deleting if it is followed by a comma (other
			// // fields next to it)
			// string = string.replaceAll(".domainMetadata.:\\[.*?\\],", "");
			// // The second is for deleting if it is followed by the bracket
			// (end
			// // of the structure)
			// string = string.replaceAll(".domainMetadata.:\\[.*?\\]}", "}");
			//
			// // The first is for deleting if it is followed by a comma (other
			// // fields next to it)
			// string = string.replaceAll("\"metadata\"", "\"metadatas\"");
			//
			// string = this.levelUpEntityId(string);

			os.write(string.getBytes(Charset.forName("UTF-8")));

			logger.info("Output Stream to " + url + resource + " : "
					+ os.toString());

			// send Message
			os.flush();
			// close connection again
			os.close();

			// now it is time to receive the response
			// get input stream from the connection
			is = connection.getInputStream();

			StringWriter writer = new StringWriter();
			IOUtils.copy(is, writer, "UTF-8");
			resp = writer.toString();

			is.close();

			logger.info("Response from " + url + resource + " : " + resp);

			if (connection.getResponseCode() == 415) {

				logger.info("Connection Error: Format not supported by " + url);

				return "415";

			}

			UpdateContextResponse_OrionCustomization updateContextResponse_OrionCustomization = (UpdateContextResponse_OrionCustomization) NgsiStructure
					.parseStringToJson(resp,
							UpdateContextResponse_OrionCustomization.class);

			return updateContextResponse_OrionCustomization
					.toUpdateContextResponse().toJsonString();

		} catch (ConnectException e) {

			logger.info("IOException: Impossible to establish a connection with "
					+ url + ":" + e.getMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("ConnectException", e);
			}

			return "500 - Connection Error - the URL: " + url
					+ " cannot be reached!";

		} catch (IOException e) {

			try {
				if (connection != null && connection.getResponseCode() == 415) {

					logger.info("Connection Error: Format not supported by "
							+ url);

					return "415";

				}
			} catch (IOException e1) {
				if (logger.isDebugEnabled()) {
					logger.debug("IOException", e);
				}
			}

			logger.info("500 - Error I/O with: " + url);
			logger.info("IOException: Impossible to establish a connection with "
					+ url + ":" + e.getMessage());

			return "500 - Error I/O with: " + url;

		} finally {

			if (connection != null) {

				connection.disconnect();
			}
			logger.info("Connection Closed!");
		}

	}

	// public static void main(String[] args) {
	// // String string =
	// "{ \"updateAction\": \"UPDATE\", \"contextElements\": [{ \"entityId\": { \"id\": \"test\", \"isPattern\": false }, \"attributes\": [{ \"name\": \"temp\", \"type\": \"temp\", \"contextValue\": \"43\" }] }], \"contextElements\": [{ \"entityId\": { \"id\": \"test\", \"isPattern\": false }, \"attributes\": [{ \"name\": \"temp\", \"type\": \"temp\", \"contextValue\": \"43\" }] }] }";
	// String string =
	// "{\"updateAction\":\"UPDATE\",\"contextElements\":[{\"id\":\"urn:x-iot:smartsantander:1:478\",\"isPattern\":false,\"domainMetadata\":[],\"attributes\":[{\"name\":\"temperature\",\"type\":\"temperature\",\"contextValue\":\"63.51\"}]}]}";
	//
	// string = string.replaceAll("contextValue", "value");
	// System.out.println(string);
	//
	//
	// // The first is for deleting if it is followed by a comma (other fields
	// next to it)
	// string = string.replaceAll(".attributeDomainName.:\\[.*?\\],","");
	// // The second is for deleting if it is followed by the bracket (end of
	// the structure)
	// string = string.replaceAll(".attributeDomainName.:\\[.*?\\]}","}");
	//
	// // The first is for deleting if it is followed by a comma (other fields
	// next to it)
	// string = string.replaceAll(".domainMetadata.:\\[.*?\\],","");
	// // The second is for deleting if it is followed by the bracket (end of
	// the structure)
	// string = string.replaceAll(".domainMetadata.:\\[.*?\\]}","}");
	//
	// System.out.println(string);
	//
	// // System.out.println(HttpConnectionClient.levelUpEntityId(string));
	//
	//
	// }

	private String levelUpEntityId(String requestAsJsonString) {

		String entityIdRegex = "\\\"entityId\\\":(.+?)\\}";

		Pattern pattern = Pattern.compile(entityIdRegex);

		String entityRegex = "\\{(.+?)\\}";

		Pattern entityPattern = Pattern.compile(entityRegex);

		Matcher matcher = pattern.matcher(requestAsJsonString);
		String entityId = null;
		String entity = requestAsJsonString;
		String newRequest = requestAsJsonString;
		while (matcher.find()) {
			entityId = matcher.group(0);

			Matcher entityMatcher = entityPattern.matcher(entityId);
			if (entityMatcher.find()) {
				entity = entityMatcher.group(1);
			}

			newRequest = newRequest.replace(entityId, entity);

		}

		return newRequest;
	}
}
