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
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import eu.neclab.iotplatform.iotbroker.commons.GenerateMetadata;
import eu.neclab.iotplatform.iotbroker.commons.XmlFactory;
import eu.neclab.iotplatform.iotbroker.commons.XmlValidator;
import eu.neclab.iotplatform.ngsi.api.datamodel.Code;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityResponse;
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
 */
public class Southbound implements Ngsi10Requester, Ngsi9Interface {

	/** The logger. */
	private static Logger logger = Logger.getLogger(Southbound.class);

	/** The XML Validator, used to validate xml */
	private final XmlValidator validator = new XmlValidator();

	/** The XmlFactory, used for XML parsing. */
	private final XmlFactory xmlFactory = new XmlFactory();

	/** The ngsi10schema file for validation */
	@Value("${schema_ngsi10_operation}")
	private String ngsi10schema;

	/** The ngsi9schema file for validation */
	@Value("${schema_ngsi9_operation}")
	private String ngsi9schema;

	/** The ngsi9url address of NGSI 9 component */
	@Value("${ngsi9Uri}")
	private String ngsi9url;

	/** The ngsi9root path. */
	@Value("${pathPreFix_ngsi9}")
	private String ngsi9rootPath;

/*	*//** The ngsi10root path. *//*
	@Value("${pathPreFix_ngsi10}")
	private String ngsi10rootPath;*/

	/** Port of tomcat server from command-line parameter */
	private final String tomcatPort = System.getProperty("tomcat.init.port");

	/** The Constant CONTENT_TYPE. */
	private static final String CONTENT_TYPE = "application/xml";

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

		// boolean indicating whether the response is syntactically correct.
		// False means valid!
		boolean status = true;

		// initialize response as an empty response.
		QueryContextResponse output = new QueryContextResponse();

		try {

			// convert the URI parameter into a URL
			URL url = new URL(uri.toString());

			// create the client used for the connection
			HttpConnectionClient connection = new HttpConnectionClient();

			logger.debug("Starting Http Thread");

			String respObj = connection.initializeConnection(url, "/queryContext", "POST", request,
					CONTENT_TYPE);

			if ("500".matches(respObj.substring(0, 3))) {

				output = new QueryContextResponse(null, new StatusCode(
						Code.INTERNALERROR_500.getCode(),
						ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
						respObj.substring(5)));
				return output;

			} else {

				// start the connection and retrieve the response
				output = (QueryContextResponse) xmlFactory.convertStringToXml(
						respObj, QueryContextResponse.class);

			}

			if (output.getListContextElementResponse() != null) {

				// validate the syntax of the response
				status = validator.xmlValidation(output, ngsi10schema);

			}

			// i.e. response is valid.
			if (!status) {

				logger.info("QueryContextResponse well Formed!");
				logger.debug("EntityID  "
						+ output.getListContextElementResponse().size());
				logger.debug("Response received!");

				// Add Metadata
				for (int i = 0; i < output.getListContextElementResponse()
						.size(); i++) {

					output.getListContextElementResponse().get(i)
							.getContextElement().getDomainMetadata()
							.add(GenerateMetadata.createSourceIPMetadata(uri));

					output.getListContextElementResponse().get(i)
							.getContextElement().getDomainMetadata()
							.add(GenerateMetadata.createDomainTimestampMetadata());

				}

			} else {

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

		SubscribeContextResponse output = new SubscribeContextResponse();

		logger.debug("REQUEST BEFORE PARSE" + request.getReference());

		try {

			// get address of local host
			InetAddress thisIp = InetAddress.getLocalHost();

			// initialize http connection
			URL url = new URL(uri.toString());
			HttpConnectionClient connection = new HttpConnectionClient();

			request.setReference("http://" + thisIp.getHostAddress() + ":"
					+ tomcatPort + "/ngsi10/notify");

			String respObj = connection.initializeConnection(url,
					"/subscribeContext", "POST", request,
					CONTENT_TYPE);

			if ("500".matches(respObj.substring(0, 3))) {

				output = new SubscribeContextResponse(null, new SubscribeError(
						null, new StatusCode(Code.INTERNALERROR_500.getCode(),
								ReasonPhrase.RECEIVERINTERNALERROR_500
										.toString(), respObj.substring(5))));
				return output;

			} else {

				// start the connection and retrieve the response
				output = (SubscribeContextResponse) xmlFactory
						.convertStringToXml(respObj,
								SubscribeContextResponse.class);

			}

			// validate the response against the XML_SCHEMA
			boolean status = true;
			if (output != null) {
				status = validator.xmlValidation(output, ngsi10schema);
			}

			if (!status) {
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

		boolean status = true;
		UpdateContextSubscriptionResponse output = new UpdateContextSubscriptionResponse();

		try {

			URL url = new URL(uri.toString());
			HttpConnectionClient connection = new HttpConnectionClient();
			logger.debug("Starting Http Thread ");

			String respObj = connection.initializeConnection(url, "/updateContextSubscription", "POST",
					request, CONTENT_TYPE);

			if ("500".matches(respObj.substring(0, 3))) {

				output = new UpdateContextSubscriptionResponse(null,
						new SubscribeError(null, new StatusCode(
								Code.INTERNALERROR_500.getCode(),
								ReasonPhrase.RECEIVERINTERNALERROR_500
										.toString(), respObj.substring(5))));
				return output;

			} else {

				// start the connection and retrieve the response
				output = (UpdateContextSubscriptionResponse) xmlFactory
						.convertStringToXml(respObj,
								UpdateContextSubscriptionResponse.class);

			}

			if (output != null) {

				status = validator.xmlValidation(output, ngsi10schema);
			}

			if (!status) {

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

		UnsubscribeContextResponse output = new UnsubscribeContextResponse();

		try {

			// init connection
			URL url = new URL(uri.toString());
			HttpConnectionClient connection = new HttpConnectionClient();
			logger.debug("Thread Http Start");

			// connect and get response
			String respObj = connection.initializeConnection(url, "/unsubscribeContext", "POST", request,
					CONTENT_TYPE);

			if ("500".matches(respObj.substring(0, 3))) {

				output = new UnsubscribeContextResponse(null, new StatusCode(
						Code.INTERNALERROR_500.getCode(),
						ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
						respObj.substring(5)));
				return output;

			} else {

				// start the connection and retrieve the response
				output = (UnsubscribeContextResponse) xmlFactory
						.convertStringToXml(respObj,
								UnsubscribeContextResponse.class);

			}

			boolean status = true;
			if (output != null) {
				status = validator.xmlValidation(output, ngsi10schema);
			}
			// response valid and not null
			if (!status) {
				return output;

			} else {

				output = new UnsubscribeContextResponse(null,
						new StatusCode(Code.INTERNALERROR_500.getCode(),
								ReasonPhrase.RECEIVERINTERNALERROR_500
										.toString(), null));

				return output;

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

		boolean isInvalid = true;
		UpdateContextResponse output = new UpdateContextResponse();

		try {

			URL url = new URL(uri.toString());
			HttpConnectionClient connection = new HttpConnectionClient();
			logger.debug("Starting Http Thread ");

			String respObj = connection.initializeConnection(url, "/updateContext", "POST", request,
					CONTENT_TYPE);

			if ("500".matches(respObj.substring(0, 3))) {

				output = new UpdateContextResponse(new StatusCode(
						Code.INTERNALERROR_500.getCode(),
						ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
						respObj.substring(5)), null);
				return output;

			} else {

				// convert the response
				output = (UpdateContextResponse) xmlFactory.convertStringToXml(
						respObj, UpdateContextResponse.class);

			}

			if (output != null) {

				isInvalid = validator.xmlValidation(output, ngsi10schema);

			}

			if (!isInvalid) {

				// Add Metadata only when the contextElementResponse is not null
				if (output.getContextElementResponse() != null) {
					for (int i = 0; i < output.getContextElementResponse()
							.size(); i++) {

						try {
							output.getContextElementResponse().get(i)
									.getContextElement().getDomainMetadata()
									.add(GenerateMetadata.createSourceIPMetadata(uri));

							output.getContextElementResponse().get(i)
									.getContextElement().getDomainMetadata()
									.add(GenerateMetadata.createDomainTimestampMetadata());
						} catch (URISyntaxException e) {
							logger.debug("Malformed URI", e);
							break;
						}

					}
				}

			} else {

				output = new UpdateContextResponse();
				//output.setContextElementResponse(null);
				output.setErrorCode(new StatusCode(Code.INTERNALERROR_500
						.getCode(), ReasonPhrase.RECEIVERINTERNALERROR_500
						.toString(), "Response from Pub/Sub not Valid!"));
				return output;

			}

		} catch (MalformedURLException e) {
			logger.debug("Malformed URI", e);

			output = new UpdateContextResponse(new StatusCode(
					Code.INTERNALERROR_500.getCode(),
					ReasonPhrase.RECEIVERINTERNALERROR_500.toString(), "Malformed URI"),
					null);
		}

		return output;

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

		try {

			// init connection
			HttpConnectionClient connection = new HttpConnectionClient();

			URL ngsi9 = new URL(ngsi9url);

			// connect
			String response = connection.initializeConnection(ngsi9, "/"
					+ ngsi9rootPath + "/discoverContextAvailability", "POST",
					request, "application/xml");

			if ("500".equals(response.substring(0, 3))) {

				output = new DiscoverContextAvailabilityResponse(null,
						new StatusCode(Code.INTERNALERROR_500.getCode(),
								ReasonPhrase.RECEIVERINTERNALERROR_500
										.toString(), response.substring(5)));
				return output;

			} else {

				List<String> lstValue = getAssociationDataFromRegistrationMetaData(response);
				output = (DiscoverContextAvailabilityResponse) xmlFactory
						.convertStringToXml(response,
								DiscoverContextAvailabilityResponse.class);
				output = addingAssociatinDataToDiscContextAvailabilityRes(
						output, lstValue);

			}

			boolean status = false;

			status = validator.xmlValidation(output, ngsi9schema);

			if (!status) {

				// do nothing but logging
				logger.info("DiscoverContextAvailabilityResponse well Formed!");
				logger.debug("Response received!");

			} else {

				logger.info("Validation not Passed!");

				output.setContextRegistrationResponse(null);
				output.setErrorCode(new StatusCode(Code.INTERNALERROR_500
						.getCode(), ReasonPhrase.RECEIVERINTERNALERROR_500
						.toString(), null));
			}

		} catch (MalformedURLException e) {
			logger.debug("Malformed URI", e);
			output.setContextRegistrationResponse(null);
			output.setErrorCode(new StatusCode(
					Code.INTERNALERROR_500.getCode(),
					ReasonPhrase.RECEIVERINTERNALERROR_500.toString(), null));

		}

		return output;
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

		return null;

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

		try {

			// init connection
			HttpConnectionClient newTread = new HttpConnectionClient();

			URL ngsi9 = new URL(ngsi9url);

			String response = newTread.initializeConnection(ngsi9, "/"
					+ ngsi9rootPath + "/subscribeContextAvailability", "POST",
					request, CONTENT_TYPE);




			if ("500".equals(response.substring(0, 3))) {

				output = new SubscribeContextAvailabilityResponse(null, null,
						new StatusCode(Code.INTERNALERROR_500.getCode(),
								ReasonPhrase.RECEIVERINTERNALERROR_500
										.toString(), response.substring(5)));
				return output;

			} else {

				output = (SubscribeContextAvailabilityResponse) xmlFactory
						.convertStringToXml(response,
								SubscribeContextAvailabilityResponse.class);

			}

			boolean status = false;
			status = validator.xmlValidation(output, ngsi9schema);

			if (!status) {

				// do nothing but logging
				logger.info("SubscribeContextAvailability well Formed!");
				logger.debug("Response received!");

			} else {

				logger.info("Validation not Passed!");
				SubscribeContextAvailabilityResponse subscribeContextAvailabilityResponse = new SubscribeContextAvailabilityResponse(
						null, null, new StatusCode(
								Code.INTERNALERROR_500.getCode(),
								ReasonPhrase.RECEIVERINTERNALERROR_500
										.toString(), null));
				return subscribeContextAvailabilityResponse;

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

		try {

			URL ngsi9 = new URL(ngsi9url);
			HttpConnectionClient newTread = new HttpConnectionClient();
			logger.debug("Starting http thread");

			String response = newTread.initializeConnection(ngsi9, "/"
					+ ngsi9rootPath + "/unsubscribeContextAvailability",
					"POST", request, CONTENT_TYPE);

			if ("500".equals(response.substring(0, 3))) {

				output = new UnsubscribeContextAvailabilityResponse(null,
						new StatusCode(Code.INTERNALERROR_500.getCode(),
								ReasonPhrase.RECEIVERINTERNALERROR_500
										.toString(), response.substring(5)));
				return output;

			} else {

				output = (UnsubscribeContextAvailabilityResponse) xmlFactory
						.convertStringToXml(response,
								UnsubscribeContextAvailabilityResponse.class);

			}

			boolean status = true;

			if (output != null) {
				status = validator.xmlValidation(output, ngsi9schema);
			}

			if (!status) {

				return output;

			} else {

				output = new UnsubscribeContextAvailabilityResponse(null,
						new StatusCode(Code.INTERNALERROR_500.getCode(),
								ReasonPhrase.RECEIVERINTERNALERROR_500
										.toString(), null));

				return output;

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

		try {

			HttpConnectionClient connection = new HttpConnectionClient();
			logger.debug("Starting http thread");

			// Add Metadata to the Notification
			for (int i = 0; i < request.getContextElementResponseList().size(); i++) {

				request.getContextElementResponseList().get(i)
						.getContextElement().getDomainMetadata()
						.add(GenerateMetadata.createSourceIPMetadata(uri));

				request.getContextElementResponseList().get(i)
						.getContextElement().getDomainMetadata()
						.add(GenerateMetadata.createDomainTimestampMetadata());

			}

			String response = connection.initializeConnection(uri.toURL(), "",
					"POST", request, CONTENT_TYPE);

			if ("500".equals(response.substring(0, 3))) {

				output = new NotifyContextResponse(new StatusCode(
						Code.INTERNALERROR_500.getCode(),
						ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
						response.substring(5)));
				return output;

			} else {

				output = (NotifyContextResponse) xmlFactory.convertStringToXml(
						response, NotifyContextResponse.class);

			}

			boolean status = true;
			if (output != null) {
				status = validator.xmlValidation(output, ngsi10schema);
			}

			if (!status) {

				logger.info("NotifyContextResponse well Formed!");

				logger.info("NotificationResponse from agent:" + output);

				logger.debug("Response received!");

			} else {

				output = new NotifyContextResponse();
				output.setResponseCode(new StatusCode(Code.INTERNALERROR_500
						.getCode(), ReasonPhrase.RECEIVERINTERNALERROR_500
						.toString(), null));
				return output;

			}

		} catch (MalformedURLException e) {
			logger.debug("Malformed URI", e);
			output = new NotifyContextResponse();
			output.setResponseCode(new StatusCode(Code.INTERNALERROR_500
					.getCode(), ReasonPhrase.RECEIVERINTERNALERROR_500
					.toString(), null));
			return output;
		} catch (URISyntaxException e) {
			logger.debug("Malformed URI", e);
			return output;
		}

		return output;

	}

	/**
	 * Calls the NotifyContextAvailability method on the NGSI-9 server.
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

	private List<String> getAssociationDataFromRegistrationMetaData(
			String response) {
		LinkedList<String> lstValue = null;
		lstValue = new LinkedList<String>();
		int counter = 0;
		int length = response.length();
		while (counter <= length) {
			logger.debug("Counter: " + counter + " Length: " + length);
			int s = response.indexOf("<registrationMetaData>");
			int e = response.indexOf("</registrationMetaData>");

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

				logger.info(value);
				value = value.replaceAll("    ", "");

				value = value.replaceAll("\r", "");
				value = value.trim();

				lstValue.add(value);
				logger.debug(value);

			}
			counter = counter + e + 12;

		}
		return lstValue;
	}

	private DiscoverContextAvailabilityResponse addingAssociatinDataToDiscContextAvailabilityRes(
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
}
