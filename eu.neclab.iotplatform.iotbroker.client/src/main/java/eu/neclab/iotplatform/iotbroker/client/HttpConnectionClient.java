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

package eu.neclab.iotplatform.iotbroker.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

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

import eu.neclab.iotplatform.iotbroker.commons.XmlFactory;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextRequest;

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
	 * @param url
	 *            The URL of the server to connect to.
	 * @param resource
	 *            The name of resource on the server.
	 * @param method
	 *            The name of the HTTP method. Expected to be "GET", "PUT",
	 *            "POST", or "DELETE".
	 * @param contentType
	 *            the content type
	 * @return Returns the connection.
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static HttpURLConnection createConnection(URL url, String resource,
			String method, String contentType, String xAuthToken) throws IOException {

		// initialize the URL of the connection as the concatenation of
		// parameters url and resource.
		URL connectionURL = new URL(url + resource);

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
		connection.setRequestProperty("Accept",contentType);
		connection.setRequestProperty("X-Auth-Token", xAuthToken);
		connection.setRequestProperty("X-Auth-Token", xAuthToken);


		// set connection timeout
		connection.setConnectTimeout(CONNECTION_TIMEOUT);

		// set the request method to be used by the connection
		connection.setRequestMethod(method);

		// finally return the nicely configured connection
		return connection;

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
	 *            The message body of the request to send. The object is expected
	 *            to be an instance of an NGSI 9 or 10 message body.
	 * @param contentType
	 *            The content type that is announced in the request header. The request object
	 *            in the message body will be sent in XML format for contentType  
	 *            "application/xml" and JSON format otherwise.                
	 * @param xAuthToken
	 * 			  The security token used by this connection in order to connect to
	 * 			  a component secured by the FIWARE security mechanisms.
	 *
	 * @return Either returns the response body returned by the server (as a String) or an
	 * error message.
	 */
	public String initializeConnection(URL url, String resource, String method,
			Object request, String contentType,  String xAuthToken) {

		// initialize variables
		HttpURLConnection connection = null;
		InputStream is = null;
		OutputStream os = null;
		String resp = null;

		try {

			// use the above setConnection method to get a connection from url,
			// resource and the method.
			connection = createConnection(url, resource, method, contentType, xAuthToken);

			if(contentType.equals("application/xml")){				
				//connect using XML message body

				logger.info("URL" + url + resource);

				logger.info("Starting connection with: " + url);

				logger.info("Send the QUERY!");

				// create a context from the request class
				JAXBContext requestContext = JAXBContext.newInstance(request
						.getClass());

				// Create a Marshaller from the context
				Marshaller m = requestContext.createMarshaller();

				// get the OutputStram form the connection
				os = connection.getOutputStream();

				// Ask the marshaller to marshall the request for you
				logger.info("Request Class: " + request.getClass().toString());
				m.marshal(request, os);
				
			}else{
				//connect using JSON message body
				
				// get the OutputStram form the connection
				os = connection.getOutputStream();

				ObjectMapper mapper = new ObjectMapper();
				SerializationConfig config = mapper.getSerializationConfig();
				config.setSerializationInclusion(Inclusion.NON_NULL);
				//			m.setProperty(Marshaller.JAXB_ENCODING, "Unicode");

				try {
					mapper.writeValue(os,request);
					logger.info("----------------->"+mapper.writeValueAsString(request));
				} catch (JsonGenerationException e) {

					logger.debug("JsonGenerationException", e);
				} catch (JsonMappingException e) {

					logger.debug("JsonMappingException", e);
				} catch (IOException e) {

					logger.debug("IOException", e);
				}

			}
			logger.info("Output Stream: " + os.toString());

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

			logger.info("------------->Response = "+resp);

			return resp;

		} catch (ConnectException e) {

			logger.info("Connection Error: Impossible to contact: "+ url);
			logger.debug("ConnectException",e);

			return "500 - Connection Error - the URL: "+ url+" cannot be reached!";

		} catch (JAXBException e) {
			logger.info("XML Parse Error!",e);
			logger.debug("JAXBException",e);
			return "500 - XML Parse Error! Response from: " + url
					+ " is not correct!";
		} catch (IOException e) {

			logger.info("500 - Error I/O with: " + url);
			logger.debug("IOException",e);

			return "500 - Error I/O with: " + url;

		} finally {

			if (connection != null) {

				connection.disconnect();
			}
			logger.info("Connection Closed!");
		}

	}
}
