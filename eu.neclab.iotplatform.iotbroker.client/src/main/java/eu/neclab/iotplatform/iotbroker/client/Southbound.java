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
package eu.neclab.iotplatform.iotbroker.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import eu.neclab.iotplatform.iotbroker.commons.GenerateMetadata;
import eu.neclab.iotplatform.iotbroker.commons.JsonFactory;
import eu.neclab.iotplatform.iotbroker.commons.JsonValidator;
import eu.neclab.iotplatform.iotbroker.commons.XmlFactory;
import eu.neclab.iotplatform.iotbroker.commons.XmlValidator;
import eu.neclab.iotplatform.ngsi.api.datamodel.Code;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.MetadataTypes;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.ReasonPhrase;
import eu.neclab.iotplatform.ngsi.api.datamodel.RegisterContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.RegisterContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.StatusCode;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeError;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextAvailabilitySubscriptionRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextAvailabilitySubscriptionResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextSubscriptionRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextSubscriptionResponse;
import eu.neclab.iotplatform.ngsi.api.ngsi10.Ngsi10Requester;
import eu.neclab.iotplatform.ngsi.api.ngsi9.Ngsi9Interface;

/**
 * Objects of this class are NGSI request clients to be used by the IoT Broker. <br>
 * By implementing the {@link Ngsi10Requester} interface, objects of this class
 * can initiate NGSI-10 communication with arbitrary NGSI-10 servers. <br>
 * Furthermore, this class implements the NGSI9Interface interface, which means
 * that it can initiate communication with an NGSI-9 server. However, unlike for
 * NGSI-10 communication, the address of the NGSI-9 server is fixed by a
 * configuration parameter.
 * 
 * The supported content types for response bodies are application/xml and
 * application./json. The content type for outgoing message bodies is can be set
 * by modifying the private constant CONTENT_TYPE.
 * 
 */
@Component
public class Southbound implements Ngsi10Requester, Ngsi9Interface {

	/** The logger. */
	private static Logger logger = Logger.getLogger(Southbound.class);

	/** The XmlFactory, used for XML parsing. */
	private final XmlFactory xmlFactory = new XmlFactory();

	/** The JsonFactory, used for JSON parsing. */
	private final JsonFactory jsonFactory = new JsonFactory();

	/** The ngsi10schema file for validation */
	@Value("${schema_ngsi10_operation}")
	private String ngsi10schema;

	/** The ngsi9schema file for validation */
	@Value("${schema_ngsi9_operation}")
	private String ngsi9schema;

	/** The ngsi9url address of NGSI 9 component */
	@Value("${ngsi9Uri}")
	private String ngsi9url;

	/** The remote ngsi9 component to be contacted for registration */
	@Value("${ngsi9RemoteUrl:null}")
	private String ngsi9RemoteUrl;

	/** The ngsi9root path. */
	@Value("${pathPreFix_ngsi9:ngsi9}")
	private String ngsi9rootPath;

	/** The xAuthToken for FI-LAB. */
	@Value("${X-Auth-Token:1234567890}")
	private String xAuthToken;

	/** Port of tomcat server from command-line parameter */
	private final String tomcatPort = System.getProperty("tomcat.init.port");

	/** The Constant CONTENT_TYPE. */
	@Value("${default_content_type:application/xml}")
	private String CONTENT_TYPE;

	/** Adapt UpdateContextRequest to Orion Standard */
	@Value("${adaptUpdatesToOrionStandard:true}")
	private boolean adaptUpdatesToOrionStandard;

	/**
	 * Validate if a message body is syntactically correct. Returns true if body
	 * is correct.
	 * 
	 * @param body
	 *            The message body string
	 * @param contentType
	 *            String representing the content type of the message body.
	 *            Supported content types are "application/xml" and
	 *            "application/json". In case of "application/json" it is only
	 *            checked whether the body is syntactically correct json; it is
	 *            not checked against a json schema language. Unsupported
	 *            content types always result in "incorrect".
	 * @param classType
	 *            The expected type of object represented by the message body
	 * @param schema
	 *            The xml schema the message body is evaluated against.
	 * 
	 */
	private boolean validateMessageBody(String body, String contentType,
			Class<?> classType, String schema) {

		boolean status = false;
		/*
		 * status=false means incorrect syntax
		 */

		if (contentType.equals("application/xml")) {
			// make xml check against xml schema

			XmlValidator validator = new XmlValidator();

			Object obj;
			try {
				obj = classType.newInstance();
			} catch (InstantiationException e) {
				logger.info("InstantiationException", e);
			} catch (IllegalAccessException e) {
				logger.info("InstantiationException", e);
			}
			obj = xmlFactory.convertStringToXml(body, classType);

			status = validator.xmlValidation(obj, schema);

		} else if (contentType.equals("application/json")) {
			// make json syntax check

			JsonValidator validator = new JsonValidator();
			status = validator.isValidJSON(body);

		}

		if (!status) {
			logger.info("Invalid incoming request. Reference schema is: "
					+ schema);
		}

		logger.info("Incoming request Valid:" + status);

		return status;

	}

	private String tryDifferentContentType(Object request, String path,
			String method, HttpConnectionClient connection, URL url) {

		String response = null;

		if (CONTENT_TYPE.equals("application/xml")) {

			response = connection.initializeConnection(url, path, method,
					request, "application/json", xAuthToken);

		} else if (CONTENT_TYPE.equals("application/json")) {

			response = connection.initializeConnection(url, path, method,
					request, "application/xml", xAuthToken);

		}
		return response;

	}

	/**
	 * Calls the QueryContext method on an NGSI-10 server.
	 * 
	 * @param request
	 *            The request message.
	 * @param uri
	 *            The address of the NGSI-10 server.
	 * @return The response message.
	 * 
	 */
	@Override
	public QueryContextResponse queryContext(QueryContextRequest request,
			URI uri) {
		
		String contentType = CONTENT_TYPE;

		// initialize response as an empty response.
		QueryContextResponse output = new QueryContextResponse();

		try {

			// convert the URI parameter into a URL
			URL url = new URL(uri.toString());

			// create the client used for the connection
			HttpConnectionClient connection = new HttpConnectionClient();

			logger.debug("Starting Http Thread");

			// initialize the connection
			String respObj = connection.initializeConnection(url,
					"/queryContext", "POST", request, contentType, xAuthToken);

			if (respObj.equals("415")) {

				logger.info("NGSI-10 agent non supporting "+ contentType +". Trying a different content type");
				if (CONTENT_TYPE.equals("application/xml")) {
					contentType = "application/json";
				} else if (CONTENT_TYPE.equals("application/json")) {
					contentType = "application/xml";
				}

				
				respObj = tryDifferentContentType(request, "/queryContext",
						"POST", connection, url);

				if (respObj.equals("415")) {

					output = new QueryContextResponse(null, new StatusCode(
							Code.INTERNALERROR_500.getCode(),
							ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
							"Content Type is not supported!"));
					return output;

				}

			}

			// check whether connection returned with error, and react
			// accordingly if that
			// is the case
			if (respObj != null && contentType.equals("application/xml")
					&& "500".matches(respObj.substring(0, 3))) {

				output = new QueryContextResponse(null, new StatusCode(
						Code.INTERNALERROR_500.getCode(),
						ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
						respObj.substring(5)));
				return output;

			}

			/*
			 * Otherwise the response did not return an error. Now we
			 * additionally figure out whether the format of the response body
			 * is correct XML or JSON. If yes, we transform it to a
			 * QueryContextResponse object, otherwise we create an error
			 * QueryContextResponse object.
			 */

			if (respObj != null
					&& validateMessageBody(respObj, contentType,
							QueryContextResponse.class, ngsi10schema)) {

				if (contentType.equals("application/xml")) {

					output = (QueryContextResponse) xmlFactory
							.convertStringToXml(respObj,
									QueryContextResponse.class);

				} else {

					output = (QueryContextResponse) jsonFactory
							.convertStringToJsonObject(respObj,
									QueryContextResponse.class);

				}

				logger.info("QueryContextResponse well Formed!");

				if (logger.isDebugEnabled()) {
					logger.debug("EntityID  "
							+ output.getListContextElementResponse().size());
					logger.debug("Response received!");
				}

				// Add Metadata to each ContextElementResponse: Time Stamp and
				// Source URL
				for (int i = 0; i < output.getListContextElementResponse()
						.size(); i++) {

					output.getListContextElementResponse().get(i)
							.getContextElement().getDomainMetadata()
							.add(GenerateMetadata.createSourceIPMetadata(uri));

					output.getListContextElementResponse()
							.get(i)
							.getContextElement()
							.getDomainMetadata()
							.add(GenerateMetadata
									.createDomainTimestampMetadata());

				}

			} else {
				/*
				 * We end up here if the response body is syntactically
				 * incorrect.
				 */

				output = new QueryContextResponse();

				// delete any contextResponsList in this empty response
				output.setContextResponseList(null);

				// set the error code in the response to some error!
				output.setErrorCode(new StatusCode(Code.INTERNALERROR_500
						.getCode(), ReasonPhrase.RECEIVERINTERNALERROR_500
						.toString(), null));

				return output;
			}

		} catch (MalformedURLException e) {

			logger.debug("Malformed URI", e);

			output.setErrorCode(new StatusCode(
					Code.INTERNALERROR_500.getCode(),
					ReasonPhrase.RECEIVERINTERNALERROR_500.toString(), null));

		} catch (URISyntaxException e) {
			logger.debug("Uri Exception", e);
			return output;
		}

		return output;
	}

	/**
	 * Calls the SubscribeContext method on an NGSI-10 server.
	 * 
	 * @param request
	 *            The request message.
	 * @param uri
	 *            The address of the NGSI-10 server.
	 * @return The response message.
	 * 
	 */
	@Override
	public SubscribeContextResponse subscribeContext(
			SubscribeContextRequest request, URI uri) {

		/*
		 * This is implemented analogously to queryContext. See the comments
		 * there for clarification.
		 */

		SubscribeContextResponse output = new SubscribeContextResponse();

		String contentType = CONTENT_TYPE;
		
		logger.debug("REQUEST BEFORE PARSE" + request.getReference());

		try {

			// get address of local host
			InetAddress thisIp = InetAddress.getLocalHost();

			// initialize http connection
			URL url = new URL(uri.toString());
			HttpConnectionClient connection = new HttpConnectionClient();

			request.setReference("http://" + thisIp.getHostAddress() + ":"
					+ tomcatPort + "/ngsi10/notify");

			String resource;
			if (url.toString().matches(".*/")) {
				resource = "subscribeContext";
			} else {
				resource = "/subscribeContext";
			}

			String respObj = connection.initializeConnection(url, resource,
					"POST", request, contentType, xAuthToken);

			if (respObj.equals("415")) {
				
				logger.info("NGSI-10 agent non supporting "+ contentType +". Trying a different content type");
				if (CONTENT_TYPE.equals("application/xml")) {
					contentType = "application/json";
				} else if (CONTENT_TYPE.equals("application/json")) {
					contentType = "application/xml";
				}

				respObj = tryDifferentContentType(request, resource, "POST",
						connection, url);

				if (respObj.equals("415")) {

					output = new SubscribeContextResponse(null,
							new SubscribeError(null, new StatusCode(
									Code.INTERNALERROR_500.getCode(),
									ReasonPhrase.RECEIVERINTERNALERROR_500
											.toString(),
									"Content Type is not supported!")));
					return output;

				}

			}

			if (respObj != null && contentType.equals("application/xml")
					&& "500".matches(respObj.substring(0, 3))) {

				output = new SubscribeContextResponse(null, new SubscribeError(
						null, new StatusCode(Code.INTERNALERROR_500.getCode(),
								ReasonPhrase.RECEIVERINTERNALERROR_500
										.toString(), respObj.substring(5))));
				return output;

			}

			if (respObj != null
					&& validateMessageBody(respObj, contentType,
							SubscribeContextResponse.class, ngsi10schema)) {

				if (contentType.equals("application/xml")) {

					output = (SubscribeContextResponse) xmlFactory
							.convertStringToXml(respObj,
									SubscribeContextResponse.class);

				} else {

					output = (SubscribeContextResponse) jsonFactory
							.convertStringToJsonObject(respObj,
									SubscribeContextResponse.class);

				}

				return output;

			} else {
				// If the response is null or invalid then send an error message
				output = new SubscribeContextResponse(null, new SubscribeError(
						null, new StatusCode(Code.INTERNALERROR_500.getCode(),
								ReasonPhrase.RECEIVERINTERNALERROR_500
										.toString(), null)));
				return output;

			}

		} catch (MalformedURLException e) {
			logger.debug("Malformed URI", e);

			output = new SubscribeContextResponse(null, new SubscribeError(
					null, new StatusCode(Code.INTERNALERROR_500.getCode(),
							ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
							null)));

		} catch (IOException e) {
			logger.debug("I/O Exception", e);

			output = new SubscribeContextResponse(null, new SubscribeError(
					null, new StatusCode(Code.INTERNALERROR_500.getCode(),
							ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
							null)));

		}

		return output;

	}

	/**
	 * Calls the UpdateContextSubscription method on an NGSI-10 server.
	 * 
	 * @param request
	 *            The request message.
	 * @param uri
	 *            The address of the NGSI-10 server.
	 * @return The response message.
	 * 
	 */
	@Override
	public UpdateContextSubscriptionResponse updateContextSubscription(
			UpdateContextSubscriptionRequest request, URI uri) {

		/*
		 * This is implemented analogously to queryContext. See the comments
		 * there for clarification.
		 */

		String contentType = CONTENT_TYPE;
		
		UpdateContextSubscriptionResponse output = new UpdateContextSubscriptionResponse();

		try {

			URL url = new URL(uri.toString());
			HttpConnectionClient connection = new HttpConnectionClient();
			logger.debug("Starting Http Thread ");

			String respObj = connection.initializeConnection(url,
					"/updateContextSubscription", "POST", request,
					contentType, xAuthToken);

			if (respObj.equals("415")) {
				
				logger.info("Not supporting "+ contentType +". Trying a different content type");
				if (CONTENT_TYPE.equals("application/xml")) {
					contentType = "application/json";
				} else if (CONTENT_TYPE.equals("application/json")) {
					contentType = "application/xml";
				}

				respObj = tryDifferentContentType(request,
						"/updateContextSubscription", "POST", connection, url);

				if (respObj.equals("415")) {

					output = new UpdateContextSubscriptionResponse(null,
							new SubscribeError(null, new StatusCode(
									Code.INTERNALERROR_500.getCode(),
									ReasonPhrase.RECEIVERINTERNALERROR_500
											.toString(),
									"Content Type is not supported!")));
					return output;

				}

			}

			if (respObj != null && contentType.equals("application/xml")
					&& "500".matches(respObj.substring(0, 3))) {

				output = new UpdateContextSubscriptionResponse(null,
						new SubscribeError(null, new StatusCode(
								Code.INTERNALERROR_500.getCode(),
								ReasonPhrase.RECEIVERINTERNALERROR_500
										.toString(), respObj.substring(5))));
				return output;

			}
			if (respObj != null
					&& validateMessageBody(respObj, contentType,
							UpdateContextSubscriptionResponse.class,
							ngsi10schema)) {

				if (contentType.equals("application/xml")) {

					output = (UpdateContextSubscriptionResponse) xmlFactory
							.convertStringToXml(respObj,
									UpdateContextSubscriptionResponse.class);

				} else {

					output = (UpdateContextSubscriptionResponse) jsonFactory
							.convertStringToJsonObject(respObj,
									UpdateContextSubscriptionResponse.class);

				}

				return output;

			} else {

				output = new UpdateContextSubscriptionResponse(null,
						new SubscribeError(null, new StatusCode(
								Code.INTERNALERROR_500.getCode(),
								ReasonPhrase.RECEIVERINTERNALERROR_500
										.toString(), null)));
				return output;

			}

		} catch (MalformedURLException e) {
			logger.debug("Malformed URI", e);

			output = new UpdateContextSubscriptionResponse(null,
					new SubscribeError(null, new StatusCode(
							Code.INTERNALERROR_500.getCode(),
							ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
							null)));
		}

		return output;
	}

	/**
	 * Calls the UnsubscribeContext method on an NGSI-10 server.
	 * 
	 * @param request
	 *            The request message.
	 * @param uri
	 *            The address of the NGSI-10 server.
	 * @return The response message.
	 * 
	 */
	@Override
	public UnsubscribeContextResponse unsubscribeContext(
			UnsubscribeContextRequest request, URI uri) {

		/*
		 * This is implemented analogously to queryContext. See the comments
		 * there for clarification.
		 */

		
		String contentType = CONTENT_TYPE;

		UnsubscribeContextResponse output = new UnsubscribeContextResponse();

		try {

			// init connection
			URL url = new URL(uri.toString());
			HttpConnectionClient connection = new HttpConnectionClient();
			logger.debug("Thread Http Start");

			// connect and get response
			String respObj = connection.initializeConnection(url,
					"/unsubscribeContext", "POST", request, contentType,
					xAuthToken);

			if (respObj.equals("415")) {
				
				logger.info("NGSI-10 agent non supporting "+ contentType +". Trying a different content type");
				if (CONTENT_TYPE.equals("application/xml")) {
					contentType = "application/json";
				} else if (CONTENT_TYPE.equals("application/json")) {
					contentType = "application/xml";
				}

				respObj = tryDifferentContentType(request,
						"/unsubscribeContext", "POST", connection, url);

				if (respObj.equals("415")) {

					output = new UnsubscribeContextResponse(null,
							new StatusCode(Code.INTERNALERROR_500.getCode(),
									ReasonPhrase.RECEIVERINTERNALERROR_500
											.toString(),
									"Content Type is not supported!"));
					return output;

				}

			}

			if (respObj != null && contentType.equals("application/xml")
					&& "500".matches(respObj.substring(0, 3))) {

				output = new UnsubscribeContextResponse(null, new StatusCode(
						Code.INTERNALERROR_500.getCode(),
						ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
						respObj.substring(5)));
				return output;

			}
			// response valid and not null
			if (respObj != null
					&& validateMessageBody(respObj, contentType,
							UnsubscribeContextResponse.class, ngsi10schema)) {

				if (validateMessageBody(respObj, contentType,
						UnsubscribeContextResponse.class, ngsi10schema)) {

					if (contentType.equals("application/xml")) {

						output = (UnsubscribeContextResponse) xmlFactory
								.convertStringToXml(respObj,
										UnsubscribeContextResponse.class);

					} else {

						output = (UnsubscribeContextResponse) jsonFactory
								.convertStringToJsonObject(respObj,
										UnsubscribeContextResponse.class);

					}

					return output;

				} else {

					output = new UnsubscribeContextResponse(null,
							new StatusCode(Code.INTERNALERROR_500.getCode(),
									ReasonPhrase.RECEIVERINTERNALERROR_500
											.toString(), null));

					return output;

				}
			}

		} catch (MalformedURLException e) {
			logger.debug("Malformed URI", e);

			output = new UnsubscribeContextResponse(null, new StatusCode(
					Code.INTERNALERROR_500.getCode(),
					ReasonPhrase.RECEIVERINTERNALERROR_500.toString(), null));
		}

		return output;

	}

	/**
	 * Calls the UpdateContext method on an NGSI-10 server.
	 * 
	 * @param request
	 *            The request message.
	 * @param uri
	 *            The address of the NGSI-10 server
	 * @return The response message.
	 * 
	 */
	@Override
	public UpdateContextResponse updateContext(UpdateContextRequest request,
			URI uri) {

		/*
		 * This is implemented analogously to queryContext. See the comments
		 * there for clarification.
		 */

		String contentType = CONTENT_TYPE;
		
		UpdateContextResponse output = new UpdateContextResponse();

		try {

			URL url = new URL(uri.toString());
			HttpConnectionClient connection = new HttpConnectionClient();
			logger.debug("Starting Http Thread ");

			String respObj;
			if (adaptUpdatesToOrionStandard) {
				logger.info("Adapting updateContext to Orion Standards");
				respObj = connection.initializeUpdateContextConnectionToOrion(
						url, "/updateContext", request, xAuthToken);
			} else {

				respObj = connection.initializeConnection(url,
						"/updateContext", "POST", request, contentType,
						xAuthToken);
			}

			if (respObj.equals("415")) {
				
				logger.info("NGSI-10 agent non supporting "+ contentType +". Trying a different content type");
				if (CONTENT_TYPE.equals("application/xml")) {
					contentType = "application/json";
				} else if (CONTENT_TYPE.equals("application/json")) {
					contentType = "application/xml";
				}

				respObj = tryDifferentContentType(request, "/updateContext",
						"POST", connection, url);

				if (respObj.equals("415")) {

					output = new UpdateContextResponse(new StatusCode(
							Code.INTERNALERROR_500.getCode(),
							ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
							"Content Type is not supported!"), null);
					return output;

				}

			}

			if (respObj != null && contentType.equals("application/xml")
					&& "500".matches(respObj.substring(0, 3))) {

				output = new UpdateContextResponse(new StatusCode(
						Code.INTERNALERROR_500.getCode(),
						ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
						respObj.substring(5)), null);
				return output;

			}

			if (respObj != null
					&& validateMessageBody(respObj, contentType,
							UpdateContextResponse.class, ngsi10schema)) {

				if (contentType.equals("application/xml")) {

					output = (UpdateContextResponse) xmlFactory
							.convertStringToXml(respObj,
									UpdateContextResponse.class);

				} else {

					output = (UpdateContextResponse) jsonFactory
							.convertStringToJsonObject(respObj,
									UpdateContextResponse.class);

				}

			}

			return output;

		} catch (MalformedURLException e) {
			logger.debug("Malformed URI", e);

			output = new UpdateContextResponse(new StatusCode(
					Code.INTERNALERROR_500.getCode(),
					ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
					"Malformed URI"), null);
		}

		return output;

	}

	public static void main(String[] args) {

		ContextRegistration c = new ContextRegistration();

		List<ContextMetadata> contextMetadataList = new ArrayList<ContextMetadata>();
		contextMetadataList.add(new ContextMetadata(
				MetadataTypes.NotificationHandler, null, "ciao"));
		c.setListContextMetadata(contextMetadataList);

		System.out.println(c.toJsonString());

	}

	/**
	 * Calls the DiscoverContextAvailability method on the NGSI-9 server.
	 * 
	 * @param request
	 *            The request message.
	 * @return The response message.
	 */
	@Override
	public DiscoverContextAvailabilityResponse discoverContextAvailability(
			DiscoverContextAvailabilityRequest request) {

		// initialze the response as an empty one
		DiscoverContextAvailabilityResponse output = new DiscoverContextAvailabilityResponse();
		
		String contentType = CONTENT_TYPE;

		try {

			// init connection
			HttpConnectionClient connection = new HttpConnectionClient();

			URL ngsi9 = new URL(ngsi9url);

			// connect
			String response = connection.initializeConnection(ngsi9, "/"
					+ ngsi9rootPath + "/discoverContextAvailability", "POST",
					request, contentType, xAuthToken);

			if (response.equals("415")) {
				
				logger.info("NGSI-10 agent non supporting "+ contentType +". Trying a different content type");
				if (CONTENT_TYPE.equals("application/xml")) {
					contentType = "application/json";
				} else if (CONTENT_TYPE.equals("application/json")) {
					contentType = "application/xml";
				}

				response = tryDifferentContentType(request, "/" + ngsi9rootPath
						+ "/discoverContextAvailability", "POST", connection,
						ngsi9);

				if (response.equals("415")) {

					output = new DiscoverContextAvailabilityResponse(null,
							new StatusCode(Code.INTERNALERROR_500.getCode(),
									ReasonPhrase.RECEIVERINTERNALERROR_500
											.toString(),
									"Content Type is not supported!"));
					return output;

				}

			}

			if (response != null && contentType.equals("application/xml")) {

				logger.info("Response being parsed as XML");

				if ("500".equals(response.substring(0, 3))) {

					output = new DiscoverContextAvailabilityResponse(null,
							new StatusCode(Code.INTERNALERROR_500.getCode(),
									ReasonPhrase.RECEIVERINTERNALERROR_500
											.toString(), response.substring(5)));
					return output;

				} else if (response != null
						&& validateMessageBody(response, contentType,
								DiscoverContextAvailabilityResponse.class,
								ngsi9schema)) {

					List<String> lstValue = getAssociationDataFromRegistrationMetaData(response);

					Iterator<String> iter = lstValue.iterator();

					while (iter.hasNext()) {

						String s = iter.next();

						if (logger.isDebugEnabled()) {
							logger.debug("String Association -->" + s);
						}
					}

					output = (DiscoverContextAvailabilityResponse) xmlFactory
							.convertStringToXml(response,
									DiscoverContextAvailabilityResponse.class);

					/*
					 * The xmlFactory, as currently used, is not able to
					 * transform associations automatically. Therefore the
					 * association information is extracted from the response
					 * string and then re-inserted into the transformed object
					 * using the method
					 * addingAssociationDataToDiscContextAvailabilityRes.
					 * 
					 * TODO: this can potentially be done in a more elegant way.
					 */
					if (logger.isDebugEnabled()) {
						logger.debug("Associations: " + lstValue);
					}

					output = addingAssociationDataToDiscContextAvailabilityRes(
							output, lstValue);

				} else {
					output = new DiscoverContextAvailabilityResponse(null,
							new StatusCode(Code.INTERNALERROR_500.getCode(),
									ReasonPhrase.RECEIVERINTERNALERROR_500
											.toString(),
									"XML Response not Valid!"));
					return output;
				}

			} else {

				logger.info("Response being parsed as JSON");

				if (response != null && response.contains("500")) {

					output = new DiscoverContextAvailabilityResponse(null,
							new StatusCode(Code.INTERNALERROR_500.getCode(),
									ReasonPhrase.RECEIVERINTERNALERROR_500
											.toString(), response.substring(5)));
					return output;

				} else if (response != null
						&& validateMessageBody(response,contentType,
								DiscoverContextAvailabilityResponse.class,
								ngsi9schema)) {

					List<String> lstValue = getAssociationDataFromRegistrationMetaData(response);

					Iterator<String> iter = lstValue.iterator();

					while (iter.hasNext()) {

						String s = iter.next();

						if (logger.isDebugEnabled()) {
							logger.debug("String Association -->" + s);
						}
					}

					// Correcting eventual errors of other JSON binding
					response = response.replaceAll("\\\"metadatas\\\"",
							"\\\"contextMetadata\\\"");

					output = (DiscoverContextAvailabilityResponse) jsonFactory
							.convertStringToJsonObject(response,
									DiscoverContextAvailabilityResponse.class);

					if (logger.isDebugEnabled()) {
						logger.debug("Associations: " + lstValue);
					}

					/*
					 * The xmlFactory, as currently used, is not able to
					 * transform associations automatically. Therefore the
					 * association information is extracted from the response
					 * string and then re-inserted into the transformed object
					 * using the method
					 * addingAssociationDataToDiscContextAvailabilityRes.
					 * 
					 * TODO: this can potentially be done in a more elegant way.
					 */

					output = addingAssociationDataToDiscContextAvailabilityRes(
							output, lstValue);

				} else {
					output = new DiscoverContextAvailabilityResponse(null,
							new StatusCode(Code.INTERNALERROR_500.getCode(),
									ReasonPhrase.RECEIVERINTERNALERROR_500
											.toString(),
									"JSON Response not Valid!"));
					return output;
				}

			}

		} catch (MalformedURLException e) {
			logger.debug("Malformed URI", e);
			output.setContextRegistrationResponse(null);
			output.setErrorCode(new StatusCode(
					Code.INTERNALERROR_500.getCode(),
					ReasonPhrase.RECEIVERINTERNALERROR_500.toString(), e
							.getMessage()));

		}

		return output;
	}

	/**
	 * This static method extracts associations from context registrations.
	 */
	private static List<String> getAssociationDataFromRegistrationMetaData(
			String response) {

		LinkedList<String> lstValue = null;
		lstValue = new LinkedList<String>();

		int counter = 0;
		int length = response.length();
		while (counter <= length) {
			logger.debug("Counter: " + counter + " Length: " + length);
			int s = response.indexOf("<registrationMetaData>", counter);
			int e = response.indexOf("</registrationMetaData>", counter);

			if (s == -1) {
				break;
			}
			String regMetaData = response.substring(s, e);
			logger.debug("s: " + s + " e: " + e + " regMetaData: "
					+ regMetaData);
			if (regMetaData.contains("Association")) {
				int vs = regMetaData.indexOf("<value>");
				int ve = regMetaData.indexOf("</value>");
				String value = regMetaData.substring(vs + 7, ve);

				logger.debug("vs: " + vs + " ve: " + ve + " value: " + value);
				value = value.replaceAll("\t", "");
				value = value.replaceAll("\n", "");

				value = value.replaceAll("    ", "");
				value = value.replaceAll("\r", "");
				value = value.trim();

				lstValue.add(value);
				logger.debug("Association added: " + value);

			}
			counter = counter + e + 12;

		}
		return lstValue;
	}

	/**
	 * This method adds to a {@link DiscoverContextAvailabilityResponse} message
	 * body the associations specified as the second function parameter. The
	 * latter associations are inserted into the response as context metadata
	 * values at the places where context metadata with type "association" is
	 * found.
	 * 
	 * The purpose of this method is to reinsert association information where
	 * the xml parser as it is used is not able to generate it automatically.
	 */
	private DiscoverContextAvailabilityResponse addingAssociationDataToDiscContextAvailabilityRes(
			DiscoverContextAvailabilityResponse resp, List<String> lstValue) {

		int count = 0;

		if (!lstValue.isEmpty()) {

			DiscoverContextAvailabilityResponse dcaRes = resp;
			List<ContextRegistrationResponse> lstCRegRes = dcaRes
					.getContextRegistrationResponse();

			for (ContextRegistrationResponse cRegRes : lstCRegRes) {
				List<ContextMetadata> lstCMetaData = cRegRes
						.getContextRegistration().getListContextMetadata();

				for (ContextMetadata cMetaData : lstCMetaData) {
					if ("Association".equals(cMetaData.getType().toString())
							&& count <= lstValue.size()) {
						cMetaData.setValue(lstValue.get(count));
						try {
							cMetaData.setValue(new String(lstValue.get(count)
									.getBytes("US-ASCII")));
						} catch (UnsupportedEncodingException e) {
							logger.debug("Unsupported Encoding Exception", e);
						}
						logger.debug(cMetaData.toString());
						count++;
					}

				}
			}

		}

		return resp;
	}

	/**
	 * Calls the RegisterContext method on the NGSI-9 server. <br>
	 * Note: Unlike specified below, this method is currently not implemented
	 * and returns null.
	 * 
	 * @param request
	 *            The request message.
	 * @return The response message.
	 */
	@Override
	public RegisterContextResponse registerContext(
			RegisterContextRequest request) {

		/*
		 * This is implemented analogously to queryContext. See the comments
		 * there for clarification.
		 */

		RegisterContextResponse output = new RegisterContextResponse();
		
		String contentType = CONTENT_TYPE;

		try {

			// get address of local host
			InetAddress thisIp = InetAddress.getLocalHost();

			if (ngsi9RemoteUrl == null) {
				ngsi9RemoteUrl = ngsi9url;
			}

			// initialize http connection
			URL url = new URL(ngsi9RemoteUrl);
			HttpConnectionClient connection = new HttpConnectionClient();

			for (ContextRegistration contextRegistration : request
					.getContextRegistrationList()) {
				contextRegistration.setProvidingApplication(new URI("http://"
						+ thisIp.getHostAddress() + ":" + tomcatPort
						+ "/ngsi10/"));
			}

			String resource;
			if (url.toString().matches(".*/")) {
				resource = "registerContext";
			} else {
				resource = "/registerContext";
			}

			String respObj = connection.initializeConnection(url, "/"
					+ ngsi9rootPath + "/" + resource, "POST", request,
					contentType, xAuthToken);

			if (respObj.equals("415")) {
				
				logger.info("NGSI-10 agent non supporting "+ contentType +". Trying a different content type");
				if (CONTENT_TYPE.equals("application/xml")) {
					contentType = "application/json";
				} else if (CONTENT_TYPE.equals("application/json")) {
					contentType = "application/xml";
				}

				respObj = tryDifferentContentType(request, resource, "POST",
						connection, url);

				if (respObj.equals("415")) {

					output = new RegisterContextResponse(null, null,
							new StatusCode(Code.INTERNALERROR_500.getCode(),
									ReasonPhrase.RECEIVERINTERNALERROR_500
											.toString(),
									"Content Type is not supported!"));

					return output;

				}

			}

			if (respObj != null && contentType.equals("application/xml")
					&& "500".matches(respObj.substring(0, 3))) {

				output = new RegisterContextResponse(null, null,
						new StatusCode(Code.INTERNALERROR_500.getCode(),
								ReasonPhrase.RECEIVERINTERNALERROR_500
										.toString(),
								ReasonPhrase.RECEIVERINTERNALERROR_500
										.toString()));
				return output;

			}

			if (respObj != null
					&& validateMessageBody(respObj, contentType,
							RegisterContextResponse.class, ngsi9schema)) {

				if (contentType.equals("application/xml")) {

					output = (RegisterContextResponse) xmlFactory
							.convertStringToXml(respObj,
									RegisterContextResponse.class);

				} else {

					output = (RegisterContextResponse) jsonFactory
							.convertStringToJsonObject(respObj,
									RegisterContextResponse.class);

				}

				return output;

			} else {
				// If the response is null or invalid then send an error message
				output = new RegisterContextResponse(null, null,
						new StatusCode(Code.INTERNALERROR_500.getCode(),
								ReasonPhrase.RECEIVERINTERNALERROR_500
										.toString(),
								ReasonPhrase.RECEIVERINTERNALERROR_500
										.toString()));
				return output;

			}

		} catch (MalformedURLException e) {
			if (logger.isDebugEnabled())
				logger.debug("Malformed URI", e);

			output = new RegisterContextResponse(null, null, new StatusCode(
					Code.INTERNALERROR_500.getCode(),
					ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
					ReasonPhrase.RECEIVERINTERNALERROR_500.toString()));

		} catch (IOException e) {
			if (logger.isDebugEnabled())
				logger.debug("I/O Exception", e);

			output = new RegisterContextResponse(null, null, new StatusCode(
					Code.INTERNALERROR_500.getCode(),
					ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
					ReasonPhrase.RECEIVERINTERNALERROR_500.toString()));

		} catch (URISyntaxException e) {
			if (logger.isDebugEnabled())
				logger.debug("URISyntaxException", e);

			return null;
		}

		return output;
	}

	/**
	 * Calls the SubscribeContextAvailability method on the NGSI-9 server.
	 * 
	 * @param request
	 *            The request message.
	 * @return The response message.
	 */
	@Override
	public SubscribeContextAvailabilityResponse subscribeContextAvailability(
			SubscribeContextAvailabilityRequest request) {

		// init response as empty
		SubscribeContextAvailabilityResponse output = null;
		
		String contentType = CONTENT_TYPE;

		try {

			// init connection
			HttpConnectionClient connection = new HttpConnectionClient();

			URL ngsi9 = new URL(ngsi9url);

			String response = connection.initializeConnection(ngsi9, "/"
					+ ngsi9rootPath + "/subscribeContextAvailability", "POST",
					request, contentType, xAuthToken);

			if (response.equals("415")) {
				
				logger.info("IoT Discovery not supporting "+ contentType +". Trying a different content type");
				if (CONTENT_TYPE.equals("application/xml")) {
					contentType = "application/json";
				} else if (CONTENT_TYPE.equals("application/json")) {
					contentType = "application/xml";
				}

				response = tryDifferentContentType(request, "/" + ngsi9rootPath
						+ "/subscribeContextAvailability", "POST", connection,
						ngsi9);

				if (response.equals("415")) {

					output = new SubscribeContextAvailabilityResponse(null,
							null, new StatusCode(
									Code.INTERNALERROR_500.getCode(),
									ReasonPhrase.RECEIVERINTERNALERROR_500
											.toString(),
									"Content Type is not supported!"));

					return output;

				}

			}

			if (response != null && contentType.equals("application/xml")) {

				if ("500".equals(response.substring(0, 3))) {

					output = new SubscribeContextAvailabilityResponse(null,
							null, new StatusCode(
									Code.INTERNALERROR_500.getCode(),
									ReasonPhrase.RECEIVERINTERNALERROR_500
											.toString(), response.substring(5)));
					return output;

				} else if (response != null
						&& validateMessageBody(response, contentType,
								SubscribeContextAvailabilityResponse.class,
								ngsi9schema)) {

					output = (SubscribeContextAvailabilityResponse) xmlFactory
							.convertStringToXml(response,
									SubscribeContextAvailabilityResponse.class);

				} else {

					output = new SubscribeContextAvailabilityResponse(null,
							null, new StatusCode(
									Code.INTERNALERROR_500.getCode(),
									ReasonPhrase.RECEIVERINTERNALERROR_500
											.toString(),
									"XML Response not Valid!"));
					return output;

				}
			} else {

				if (response != null && response.contains("500")) {

					output = new SubscribeContextAvailabilityResponse(null,
							null, new StatusCode(
									Code.INTERNALERROR_500.getCode(),
									ReasonPhrase.RECEIVERINTERNALERROR_500
											.toString(), response.substring(5)));
					return output;

				} else if (response != null
						&& validateMessageBody(response, contentType,
								SubscribeContextAvailabilityResponse.class,
								ngsi9schema)) {

					output = (SubscribeContextAvailabilityResponse) jsonFactory
							.convertStringToJsonObject(response,
									SubscribeContextAvailabilityResponse.class);

				} else {
					output = new SubscribeContextAvailabilityResponse(null,
							null, new StatusCode(
									Code.INTERNALERROR_500.getCode(),
									ReasonPhrase.RECEIVERINTERNALERROR_500
											.toString(),
									"JSON Response not Valid!"));
					return output;
				}

			}

		} catch (MalformedURLException e) {
			logger.debug("Malformed URI", e);

			SubscribeContextAvailabilityResponse subscribeContextAvailabilityResponse = new SubscribeContextAvailabilityResponse(
					null, null, new StatusCode(
							Code.INTERNALERROR_500.getCode(),
							ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
							null));
			return subscribeContextAvailabilityResponse;

		}
		return output;

	}

	/**
	 * Calls the UnsubscribeContextAvailability method on the NGSI-9 server.
	 * 
	 * @param request
	 *            The request message.
	 * @return The response message.
	 */
	@Override
	public UnsubscribeContextAvailabilityResponse unsubscribeContextAvailability(
			UnsubscribeContextAvailabilityRequest request) {

		UnsubscribeContextAvailabilityResponse output = new UnsubscribeContextAvailabilityResponse();
		
		String contentType = CONTENT_TYPE;

		try {

			URL ngsi9 = new URL(ngsi9url);
			HttpConnectionClient connection = new HttpConnectionClient();
			logger.debug("Starting http thread");

			String response = connection.initializeConnection(ngsi9, "/"
					+ ngsi9rootPath + "/unsubscribeContextAvailability",
					"POST", request, contentType, xAuthToken);

			if (response.equals("415")) {
				
				logger.info("IoT Discovery not supporting "+ contentType +". Trying a different content type");
				if (CONTENT_TYPE.equals("application/xml")) {
					contentType = "application/json";
				} else if (CONTENT_TYPE.equals("application/json")) {
					contentType = "application/xml";
				}

				response = tryDifferentContentType(request, "/" + ngsi9rootPath
						+ "/unsubscribeContextAvailability", "POST",
						connection, ngsi9);

				if (response.equals("415")) {

					output = new UnsubscribeContextAvailabilityResponse(null,
							new StatusCode(Code.INTERNALERROR_500.getCode(),
									ReasonPhrase.RECEIVERINTERNALERROR_500
											.toString(),
									"Content Type is not supported!"));

					return output;

				}

			}

			if (response != null && contentType.equals("application/xml")) {

				if ("500".equals(response.substring(0, 3))) {

					output = new UnsubscribeContextAvailabilityResponse(null,
							new StatusCode(Code.INTERNALERROR_500.getCode(),
									ReasonPhrase.RECEIVERINTERNALERROR_500
											.toString(), response.substring(5)));
					return output;

				} else if (response != null
						&& validateMessageBody(response, contentType,
								SubscribeContextAvailabilityResponse.class,
								ngsi9schema)) {

					output = (UnsubscribeContextAvailabilityResponse) xmlFactory
							.convertStringToXml(
									response,
									UnsubscribeContextAvailabilityResponse.class);

				} else {
					output = new UnsubscribeContextAvailabilityResponse(null,
							new StatusCode(Code.INTERNALERROR_500.getCode(),
									ReasonPhrase.RECEIVERINTERNALERROR_500
											.toString(),
									"XML Response not Valid!"));
					return output;

				}
			} else {

				if (response != null && response.contains("500")) {
					output = new UnsubscribeContextAvailabilityResponse(null,
							new StatusCode(Code.INTERNALERROR_500.getCode(),
									ReasonPhrase.RECEIVERINTERNALERROR_500
											.toString(), response.substring(5)));
					return output;
				} else if (response != null
						&& validateMessageBody(response, contentType,
								SubscribeContextAvailabilityResponse.class,
								ngsi9schema)) {

					output = (UnsubscribeContextAvailabilityResponse) jsonFactory
							.convertStringToJsonObject(
									response,
									UnsubscribeContextAvailabilityResponse.class);
				} else {
					output = new UnsubscribeContextAvailabilityResponse(null,
							new StatusCode(Code.INTERNALERROR_500.getCode(),
									ReasonPhrase.RECEIVERINTERNALERROR_500
											.toString(),
									"JSON Response not Valid!"));
					return output;

				}

			}

		} catch (MalformedURLException e) {
			logger.debug("Malformed URI", e);
			output = new UnsubscribeContextAvailabilityResponse(null,
					new StatusCode(Code.INTERNALERROR_500.getCode(),
							ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
							null));

		}

		return output;
	}

	/**
	 * Calls the UpdateContextAvailabilitySubscription method on the NGSI-9
	 * server. <br>
	 * Note: Unlike specified below, this method is currently not implemented
	 * and returns null.
	 * 
	 * @param request
	 *            The request message.
	 * @return The response message.
	 */
	@Override
	public UpdateContextAvailabilitySubscriptionResponse updateContextAvailabilitySubscription(
			UpdateContextAvailabilitySubscriptionRequest request) {

		return null;
	}

	/**
	 * Calls the NotifyContext method on an NGSI-10 server.
	 * 
	 * @param request
	 *            The request message.
	 * @param uri
	 *            The address of the NGSI-10 server.
	 * @return The response message.
	 * 
	 */
	@Override
	public NotifyContextResponse notifyContext(NotifyContextRequest request,
			URI uri) {

		NotifyContextResponse output = new NotifyContextResponse();

		String contentType = CONTENT_TYPE;
		
		try {

			HttpConnectionClient connection = new HttpConnectionClient();

			String response = connection.initializeConnection(uri.toURL(), "",
					"POST", request, contentType, xAuthToken);

			if (response.equals("415")) {
				
				logger.info("Application not supporting "+ contentType +". Trying a different content type");
				if (CONTENT_TYPE.equals("application/xml")) {
					contentType = "application/json";
				} else if (CONTENT_TYPE.equals("application/json")) {
					contentType = "application/xml";
				}

				response = tryDifferentContentType(request, "", "POST",
						connection, uri.toURL());

				if (response.equals("415")) {

					output = new NotifyContextResponse(new StatusCode(
							Code.INTERNALERROR_500.getCode(),
							ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
							"Content Type is not supported!"));

					return output;

				}

			}

			if (response != null && contentType.equals("application/xml")) {

				if ("500".equals(response.substring(0, 3))) {

					output = new NotifyContextResponse(new StatusCode(
							Code.INTERNALERROR_500.getCode(),
							ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
							response.substring(5)));
					return output;

				} else if (response != null
						&& validateMessageBody(response, contentType,
								NotifyContextResponse.class, ngsi10schema)) {

					output = (NotifyContextResponse) xmlFactory
							.convertStringToXml(response,
									NotifyContextResponse.class);

				} else {
					output = new NotifyContextResponse(new StatusCode(
							Code.INTERNALERROR_500.getCode(),
							ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
							"XML Response not Valid!"));
					return output;
				}
			} else {

				if (response != null && response.contains("500")) {
					output = new NotifyContextResponse(new StatusCode(
							Code.INTERNALERROR_500.getCode(),
							ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
							response.substring(5)));
					return output;
				} else if (response != null
						&& validateMessageBody(response, contentType,
								SubscribeContextAvailabilityResponse.class,
								ngsi10schema)) {
					output = (NotifyContextResponse) jsonFactory
							.convertStringToJsonObject(response,
									NotifyContextResponse.class);
				} else {
					output = new NotifyContextResponse(new StatusCode(
							Code.INTERNALERROR_500.getCode(),
							ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
							"JSON Response not Valid!"));
					return output;
				}

			}

		} catch (MalformedURLException e) {
			logger.debug("Malformed URI", e);
			output = new NotifyContextResponse();
			output.setResponseCode(new StatusCode(Code.INTERNALERROR_500
					.getCode(), ReasonPhrase.RECEIVERINTERNALERROR_500
					.toString(), e.getMessage()));
			return output;
		}

		return output;

	}

	/**
	 * Calls the NotifyContextAvailability method on the NGSI-9 server. Note:
	 * Unlike specified below, this method is currently not implemented and
	 * returns null.
	 * 
	 * @param request
	 *            The request message.
	 * @return The response message.
	 */
	@Override
	public NotifyContextAvailabilityResponse notifyContextAvailability(
			NotifyContextAvailabilityRequest request) {
		return null;
	}
}
