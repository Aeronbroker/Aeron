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
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import eu.neclab.iotplatform.iotbroker.commons.ContentType;
import eu.neclab.iotplatform.iotbroker.commons.FullHttpRequester;
import eu.neclab.iotplatform.iotbroker.commons.FullHttpResponse;
import eu.neclab.iotplatform.iotbroker.commons.GenerateMetadata;
import eu.neclab.iotplatform.iotbroker.commons.JsonValidator;
import eu.neclab.iotplatform.iotbroker.commons.XmlValidator;
import eu.neclab.iotplatform.ngsi.api.datamodel.Code;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElementResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.NgsiStructure;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextRequest_OrionCustomization;
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
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateActionType;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextAvailabilitySubscriptionRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextAvailabilitySubscriptionResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextRequest_OrionCustomization;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextResponse_OrionCustomization;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextSubscriptionRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextSubscriptionResponse;
import eu.neclab.iotplatform.ngsi.api.ngsi10.Ngsi10Requester;
import eu.neclab.iotplatform.ngsi.api.ngsi10.StandardVersion;
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

	@Value("${exposedAddress:}")
	private String exposedAddress;

	private String ngsi10Reference = null;
	private String ngsi9Reference = null;

	private static String REGEX_FOR_ORION_API = ".*\\/v1\\/.*";

	// /** Port of tomcat server from command-line parameter */
	// private final String tomcatPort = System.getProperty("tomcat.init.port");

	/** The Constant CONTENT_TYPE. */
	@Value("${default_content_type:application/xml}")
	private String defaultContentType;
	private ContentType CONTENT_TYPE = null;

	private ContentType getCONTENT_TYPE() {
		if (CONTENT_TYPE == null) {
			CONTENT_TYPE = ContentType.fromString(defaultContentType);
		}

		return CONTENT_TYPE;
	}

	/** Adapt UpdateContextRequest to Orion Standard */
	@Value("${adaptUpdatesToOrionStandard:false}")
	private boolean adaptUpdatesToOrionStandard;

	/**
	 * If true it inject the timestamp in all the ContextElement retrieved from
	 * an IoT Provider
	 */
	@Value("${timestampContextElement:false}")
	private boolean timestampContextElement;

	/**
	 * If true it inject the URI source in all the ContextElement retrieved from
	 * an IoT Provider
	 */
	@Value("${trackContextSource:false}")
	private boolean trackContextSource;

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
			Class<? extends NgsiStructure> classType, String schema) {

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
			obj = NgsiStructure.convertStringToXml(body, classType);

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

		logger.info("Incoming request Valid: " + status);

		return status;

	}

	/**
	 * @return The URL where agents should send there notifications to. This is
	 *         the address where the NGSI RESTful interface is reachable.
	 */
	public String getNgsi10RefURl() {

		if (ngsi10Reference == null) {
			String address = getLocalAddress();

			ngsi10Reference = "http://" + address + ":"
					+ System.getProperty("tomcat.init.port") + "/ngsi10";

		}

		return ngsi10Reference;
	}

	private String getLocalAddress() {

		String address = null;

		if (exposedAddress != null && !exposedAddress.isEmpty()) {

			address = exposedAddress;

		} else {
			Enumeration<NetworkInterface> n;
			try {
				n = NetworkInterface.getNetworkInterfaces();
				while (n.hasMoreElements()) {
					NetworkInterface e = n.nextElement();
					if (e.getName().matches("docker.*")
							|| e.getName().equals("lo")) {
						continue;
					}
					Enumeration<InetAddress> a = e.getInetAddresses();
					while (a.hasMoreElements()) {
						InetAddress addr = a.nextElement();
						String add = addr.getHostAddress();
						if (add.matches("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$"))
							address = add;
					}
					if (address != null) {
						break;
					}
				}
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (address == null) {
				try {
					address = InetAddress.getLocalHost().getHostAddress()
							+ "/ngsi10";
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return address;

	}

	private Object parseResponse(String body, ContentType contentType,
			Class<? extends NgsiStructure> clazz) {
		if (contentType == ContentType.XML) {

			return NgsiStructure.convertStringToXml(body, clazz);

		} else {

			return NgsiStructure.parseStringToJson(body, clazz, true, true);

		}
	}

	private ContentType getContentTypeFromResponse(FullHttpResponse response,
			ContentType defaultContentType) {
		Header[] headers = response.getHeaders("Content-Type");
		if (headers.length != 0) {
			String responseContentType = headers[0].getValue();
			if (responseContentType != null) {
				return ContentType.fromString(responseContentType,
						defaultContentType);
			}
		}
		return defaultContentType;
	}

	private FullHttpResponse sendPostTryingAllSupportedContentType(URL url,
			NgsiStructure request, ContentType preferredContentType,
			String xAuthToken) {

		ContentType requestContentType = preferredContentType;
		FullHttpResponse fullHttpResponse = null;

		try {

			String data;
			if (requestContentType == ContentType.XML) {
				data = request.toString();
			} else {
				data = request.toJsonString();
			}

			fullHttpResponse = FullHttpRequester.sendPost(url, data,
					requestContentType.toString(), xAuthToken);

			/*
			 * Check if the contentType is not supported and switch to the other
			 * IoT Broker supports
			 */
			if (fullHttpResponse.getStatusLine().getStatusCode() == 415) {

				logger.info("Contacted HTTP server non supporting "
						+ requestContentType
						+ ". Trying a different content type");
				if (requestContentType == ContentType.XML) {
					requestContentType = ContentType.JSON;
				} else {
					requestContentType = ContentType.XML;
				}

				if (requestContentType == ContentType.XML) {
					data = request.toString();
				} else {
					data = request.toJsonString();
				}

				fullHttpResponse = FullHttpRequester.sendPost(url, data,
						requestContentType.toString(), xAuthToken);

			}

		} catch (java.net.NoRouteToHostException noRoutToHostEx) {
			logger.warn("Impossible to contact: " + url);
		} catch (IOException e) {
			logger.warn("Impossible to contact " + e.getMessage());
			return fullHttpResponse;

		} catch (Exception e) {

			logger.warn("Exception", e);
			return fullHttpResponse;
		}

		return fullHttpResponse;

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

		// ContentType preferredContentType = CONTENT_TYPE;

		// initialize response as an empty response.
		QueryContextResponse output = new QueryContextResponse();

		try {

			Object response = sendRequest(new URL(uri.toString()),
					"queryContext", request, QueryContextResponse.class,
					ngsi10schema);

			// If there was an error then a StatusCode has been returned
			if (response instanceof StatusCode) {
				output = new QueryContextResponse(null, (StatusCode) response);
				return output;
			}

			// Cast the response
			output = (QueryContextResponse) response;

			// Add Metadata to each ContextElementResponse: Time Stamp and
			// Source URL
			if (trackContextSource || timestampContextElement) {
				annotatedContextElement(output.getListContextElementResponse(),
						uri);
			}

		} catch (MalformedURLException e) {

			logger.warn("Malformed URI", e);

			output.setErrorCode(new StatusCode(
					Code.INTERNALERROR_500.getCode(),
					ReasonPhrase.RECEIVERINTERNALERROR_500.toString(), null));

		} catch (Exception e) {
			logger.warn("Exception", e);
			return output;
		}

		return output;
	}

	private void annotatedContextElement(
			List<ContextElementResponse> contextElementResponseList, URI uri) {

		if (contextElementResponseList == null
				|| contextElementResponseList.isEmpty()) {
			return;
		}

		for (ContextElementResponse contextElementResponse : contextElementResponseList) {

			if (contextElementResponse == null
					|| contextElementResponse.getContextElement() == null
					|| contextElementResponse.getContextElement()
							.getContextAttributeList() == null
					|| contextElementResponse.getContextElement()
							.getContextAttributeList().isEmpty()) {
				continue;
			}

			for (ContextAttribute contextAttribute : contextElementResponse
					.getContextElement().getContextAttributeList()) {

				List<ContextMetadata> contextMetadataList = contextAttribute
						.getMetadata();

				if (contextMetadataList == null) {
					contextMetadataList = new ArrayList<ContextMetadata>();
					contextAttribute.setMetadata(contextMetadataList);
				}

				try {
					if (trackContextSource) {

						contextMetadataList.add(GenerateMetadata
								.createSourceIPMetadata(uri));

					}

					if (timestampContextElement) {
						contextMetadataList.add(GenerateMetadata
								.createTimestampMetadata());
					}
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

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

		// ContentType preferredContentType = CONTENT_TYPE;

		try {

			// // get address of local host
			// InetAddress thisIp = InetAddress.getLocalHost();
			//
			// // HttpConnectionClient connection = new HttpConnectionClient();
			//
			// request.setReference("http://" + thisIp.getHostAddress() + ":"
			// + tomcatPort + "/ngsi10/notify");

			request.setReference(getNgsi10RefURl() + "/notify");

			Object response = sendRequest(new URL(uri.toString()),
					"subscribeContext", request,
					SubscribeContextResponse.class, ngsi10schema);

			// If there was an error then a StatusCode has been returned
			if (response instanceof StatusCode) {
				output = new SubscribeContextResponse(null, new SubscribeError(
						null, (StatusCode) response));
				return output;
			}

			// Cast the response
			output = (SubscribeContextResponse) response;

		} catch (MalformedURLException e) {
			logger.warn("Malformed URI", e);

			output = new SubscribeContextResponse(null, new SubscribeError(
					null, new StatusCode(Code.INTERNALERROR_500.getCode(),
							ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
							null)));

		} catch (IOException e) {
			logger.warn("I/O Exception", e);

			output = new SubscribeContextResponse(null, new SubscribeError(
					null, new StatusCode(Code.INTERNALERROR_500.getCode(),
							ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
							null)));

		}

		return output;

	}

	public static void main(String[] args) {
		DiscoverContextAvailabilityRequest disc = (DiscoverContextAvailabilityRequest) NgsiStructure
				.convertStringToXml(
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?><discoverContextAvailabilityRequest>   <entityIdList>            <entityId type='Room'>                 <id>ConferenceRoom</id>      </entityId>                      </entityIdList>       <attributeList>            <attribute>temperature</attribute>                       </attributeList>       <restriction>       <attributeExpression></attributeExpression>      <scope>            <operationScope>                <scopeType>SimpleGeoLocation</scopeType>                <scopeValue><segment>                        <NW_Corner>7.5 , 2.0</NW_Corner>                        <SE_Corner>5.5 , 11.0</SE_Corner>                     </segment></scopeValue>            </operationScope>        </scope>   </restriction></discoverContextAvailabilityRequest>",
						DiscoverContextAvailabilityRequest.class);

		System.out.println(disc.toJsonString());

	}

	/**
	 * 
	 * 
	 * @return A StatusCode if there was an error, otherwise an object of the
	 *         expectedResponseClazz
	 * 
	 */
	private Object sendRequest(URL url, String resource, NgsiStructure request,
			Class<? extends NgsiStructure> expectedResponseClazz,
			String schemaLocation) {

		ContentType preferredContentType = getCONTENT_TYPE();

		return sendRequest(url, resource, request, expectedResponseClazz,
				schemaLocation, preferredContentType);

	}

	/**
	 * 
	 * 
	 * @return A StatusCode if there was an error, otherwise an object of the
	 *         expectedResponseClazz
	 * 
	 */
	private Object sendRequest(URL url, String resource, NgsiStructure request,
			Class<? extends NgsiStructure> expectedResponseClazz,
			String schemaLocation, ContentType preferredContentType) {

		Object output;

		try {
			String correctedResource;
			if (url.toString().isEmpty() || url.toString().matches(".*/") || resource.isEmpty() || resource == null) {
				correctedResource = resource;
			} else {
				correctedResource = "/" + resource;
			}

			FullHttpResponse response = sendPostTryingAllSupportedContentType(
					new URL(url + correctedResource), request,
					preferredContentType, xAuthToken);

			if (response == null) {
				logger.warn("Impossible to get response from: " + url
						+ correctedResource);

				// TODO make a better usage of the Status Code
				output = new StatusCode(Code.INTERNALERROR_500.getCode(),
						ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
						"Impossible to contact " + url + correctedResource);
				return output;
			}

			if (response.getStatusLine().getStatusCode() == 415) {

				logger.warn("Content Type is not supported by the receiver! URL: "
						+ url + correctedResource);

				// TODO make a better usage of the Status Code
				output = new StatusCode(
						Code.INTERNALERROR_500.getCode(),
						ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
						"Content Type is not supported by the receiver! (application/xml and application/json tried)");
				return output;

			}

			if (response.getStatusLine().getStatusCode() == 500) {

				logger.warn("Receiver Internal Error. URL: " + url
						+ correctedResource + ". "
						+ response.getStatusLine().getReasonPhrase());

				// TODO make a better usage of the Status Code
				output = new StatusCode(Code.INTERNALERROR_500.getCode(),
						ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
						"Final receiver internal error: "
								+ response.getStatusLine().getReasonPhrase());
				return output;

			}

			if (response.getStatusLine().getStatusCode() == 503) {

				logger.warn("Service Unavailable. URL: " + url
						+ correctedResource + ". "
						+ response.getStatusLine().getReasonPhrase());

				// TODO make a better usage of the Status Code
				output = new StatusCode(Code.INTERNALERROR_500.getCode(),
						ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
						"Receiver service unavailable: "
								+ response.getStatusLine().getReasonPhrase());
				return output;

			}

			// Check if there is a body
			if (response.getBody() == null || response.getBody().isEmpty()) {

				logger.warn("Response from remote server empty");

				// TODO make a better usage of the Status Code
				output = new StatusCode(Code.INTERNALERROR_500.getCode(),
						ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
						"Receiver response empty");

				return output;

			}

			// Get the ContentType of the response
			ContentType responseContentType = getContentTypeFromResponse(
					response, preferredContentType);

			// Check if the message is valid
			if (response.getBody() != null
					&& !validateMessageBody(response.getBody(),
							responseContentType.toString(),
							expectedResponseClazz, schemaLocation)) {

				logger.warn("Response from remote server non a valid NGSI message");

				// TODO make a better usage of the Status Code
				output = new StatusCode(Code.INTERNALERROR_500.getCode(),
						ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
						"Receiver response non a valid NGSI message");

				return output;

			}

			// Finally parse it
			output = parseResponse(response.getBody(), responseContentType,
					expectedResponseClazz);

		} catch (MalformedURLException e) {
			logger.warn("Malformed URI", e);

			// TODO make a better usage of the Status Code
			output = new StatusCode(Code.INTERNALERROR_500.getCode(),
					ReasonPhrase.RECEIVERINTERNALERROR_500.toString(), null);

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

		UpdateContextSubscriptionResponse output = new UpdateContextSubscriptionResponse();

		try {

			Object response = sendRequest(new URL(uri.toString()),
					"updateContextSubscription", request,
					UpdateContextSubscriptionResponse.class, ngsi10schema);

			// If there was an error then a StatusCode has been returned
			if (response instanceof StatusCode) {
				output = new UpdateContextSubscriptionResponse(null,
						new SubscribeError(null, (StatusCode) response));
				return output;
			}

			// Cast the response
			output = (UpdateContextSubscriptionResponse) response;

		} catch (MalformedURLException e) {
			logger.warn("Malformed URI", e);

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

			Object response = sendRequest(new URL(uri.toString()),
					"updateContextSubscription", request,
					UnsubscribeContextResponse.class, ngsi10schema);

			// If there was an error then a StatusCode has been returned
			if (response instanceof StatusCode) {
				output = new UnsubscribeContextResponse(null,
						(StatusCode) response);
				return output;
			}

			// Cast the response
			output = (UnsubscribeContextResponse) response;

		} catch (MalformedURLException e) {
			logger.warn("Malformed URI", e);

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

		UpdateContextResponse output = new UpdateContextResponse();

		// adaptUpdatesToOrionStandard
		// I would suggest to have a completely different updateContext for
		// Orion Context and then call it specifically. Maybe add a list of
		// Orion Broker consumer in the settings (so having two pub_sub_addr).

		try {

			Object response = sendRequest(new URL(uri.toString()),
					"updateContext", request, UpdateContextResponse.class,
					ngsi10schema);

			// If there was an error then a StatusCode has been returned
			if (response instanceof StatusCode) {
				output = new UpdateContextResponse((StatusCode) response, null);
				return output;
			}

			// Cast the response
			output = (UpdateContextResponse) response;

		} catch (MalformedURLException e) {
			logger.warn("Malformed URI", e);

			output = new UpdateContextResponse(new StatusCode(
					Code.INTERNALERROR_500.getCode(),
					ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
					"Malformed URI"), null);
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
	public UpdateContextResponse_OrionCustomization updateContext_ngsiv1_tid(
			UpdateContextRequest request, URI uri) {

		UpdateContextResponse_OrionCustomization updateResponse = new UpdateContextResponse_OrionCustomization();

		UpdateContextRequest_OrionCustomization request_tid = new UpdateContextRequest_OrionCustomization(
				request);

		// adaptUpdatesToOrionStandard
		// I would suggest to have a completely different updateContext for
		// Orion Context and then call it specifically. Maybe add a list of
		// Orion Broker consumer in the settings (so having two pub_sub_addr).

		try {

			Object response = sendRequest(new URL(uri.toString()),
					"updateContext", request_tid,
					UpdateContextResponse_OrionCustomization.class,
					ngsi10schema, CONTENT_TYPE.JSON);

			// If there was an error then a StatusCode has been returned
			if (response instanceof StatusCode) {
				updateResponse = new UpdateContextResponse_OrionCustomization(
						(StatusCode) response, null);
				return updateResponse;
			}

			// Cast the response
			updateResponse = (UpdateContextResponse_OrionCustomization) response;

		} catch (MalformedURLException e) {
			logger.warn("Malformed URI", e);

			updateResponse = new UpdateContextResponse_OrionCustomization(
					new StatusCode(Code.INTERNALERROR_500.getCode(),
							ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
							"Malformed URI"), null);
		}

		return updateResponse;

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
	public UpdateContextResponse_OrionCustomization updateContext_orion(
			UpdateContextRequest_OrionCustomization request_tid, URI uri) {

		UpdateContextResponse_OrionCustomization updateResponse = new UpdateContextResponse_OrionCustomization();

		// adaptUpdatesToOrionStandard
		// I would suggest to have a completely different updateContext for
		// Orion Context and then call it specifically. Maybe add a list of
		// Orion Broker consumer in the settings (so having two pub_sub_addr).

		try {

			Object response = sendRequest(new URL(uri.toString()),
					"updateContext", request_tid,
					UpdateContextResponse_OrionCustomization.class,
					ngsi10schema, CONTENT_TYPE.JSON);

			// If there was an error then a StatusCode has been returned
			if (response instanceof StatusCode) {
				updateResponse = new UpdateContextResponse_OrionCustomization(
						(StatusCode) response, null);
				return updateResponse;
			}

			// Cast the response
			updateResponse = (UpdateContextResponse_OrionCustomization) response;

		} catch (MalformedURLException e) {
			logger.warn("Malformed URI", e);

			updateResponse = new UpdateContextResponse_OrionCustomization(
					new StatusCode(Code.INTERNALERROR_500.getCode(),
							ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
							"Malformed URI"), null);
		}

		return updateResponse;

	}

	@Override
	public UpdateContextResponse updateContext(UpdateContextRequest request,
			URI uri, StandardVersion standardVersion) {

		if (standardVersion == StandardVersion.NGSI10_v1_nle) {
			return updateContext(request, uri);
		} else {
			return updateContext_ngsiv1_tid(request, uri)
					.toUpdateContextResponse();
		}
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

		// ContentType preferredContentType = CONTENT_TYPE;

		try {

			Object response = sendRequest(new URL(ngsi9url), "/"
					+ ngsi9rootPath + "/" + "discoverContextAvailability",
					request, DiscoverContextAvailabilityResponse.class,
					ngsi9schema);

			// If there was an error then a StatusCode has been returned
			if (response instanceof StatusCode) {
				output = new DiscoverContextAvailabilityResponse(null,
						(StatusCode) response);
				return output;
			}

			// Cast the response
			output = (DiscoverContextAvailabilityResponse) response;

		} catch (MalformedURLException e) {
			logger.warn("Malformed URI", e);
			output.setContextRegistrationResponse(null);
			output.setErrorCode(new StatusCode(
					Code.INTERNALERROR_500.getCode(),
					ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
					ReasonPhrase.RECEIVERINTERNALERROR_500.toString()));

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

		RegisterContextResponse output = new RegisterContextResponse();

		try {

			// get address of local host
			InetAddress thisIp = InetAddress.getLocalHost();

			if (ngsi9RemoteUrl == null) {
				ngsi9RemoteUrl = ngsi9url;
			}
			for (ContextRegistration contextRegistration : request
					.getContextRegistrationList()) {
				// contextRegistration.setProvidingApplication(new URI("http://"
				// + thisIp.getHostAddress() + ":" + tomcatPort
				// + "/ngsi10/"));
				contextRegistration.setProvidingApplication(new URI(
						getNgsi10RefURl()));
			}

			Object response = sendRequest(new URL(ngsi9RemoteUrl), "/"
					+ ngsi9rootPath + "/" + "registerContext", request,
					RegisterContextResponse.class, ngsi9schema);

			// If there was an error then a StatusCode has been returned
			if (response instanceof StatusCode) {
				output = new RegisterContextResponse(null, null,
						(StatusCode) response);
				return output;
			}

			// Cast the response
			output = (RegisterContextResponse) response;

			if (logger.isDebugEnabled()) {
				logger.debug("Response for the registration. Request: "
						+ request + " Response: " + response);
			}

		} catch (MalformedURLException e) {
			logger.warn("Malformed URI", e);

			output = new RegisterContextResponse(null, null, new StatusCode(
					Code.INTERNALERROR_500.getCode(),
					ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
					ReasonPhrase.RECEIVERINTERNALERROR_500.toString()));

		} catch (IOException e) {
			logger.warn("I/O Exception", e);

			output = new RegisterContextResponse(null, null, new StatusCode(
					Code.INTERNALERROR_500.getCode(),
					ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
					ReasonPhrase.RECEIVERINTERNALERROR_500.toString()));

		} catch (URISyntaxException e) {
			logger.warn("URISyntaxException", e);

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

		// ContentType preferredContentType = CONTENT_TYPE;

		try {

			Object response = sendRequest(new URL(ngsi9url), "/"
					+ ngsi9rootPath + "/subscribeContextAvailability", request,
					SubscribeContextAvailabilityResponse.class, ngsi9schema);

			// If there was an error then a StatusCode has been returned
			if (response instanceof StatusCode) {
				output = new SubscribeContextAvailabilityResponse(null, null,
						(StatusCode) response);
				return output;
			}

			// Cast the response
			output = (SubscribeContextAvailabilityResponse) response;

		} catch (MalformedURLException e) {
			logger.warn("Malformed URI", e);

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

		// ContentType preferredContentType = CONTENT_TYPE;

		try {

			Object response = sendRequest(new URL(ngsi9url), ngsi9rootPath
					+ "/unsubscribeContextAvailability", request,
					UnsubscribeContextAvailabilityResponse.class, ngsi9schema);

			// If there was an error then a StatusCode has been returned
			if (response instanceof StatusCode) {
				output = new UnsubscribeContextAvailabilityResponse(null,
						(StatusCode) response);
				return output;
			}

			// Cast the response
			output = (UnsubscribeContextAvailabilityResponse) response;

		} catch (MalformedURLException e) {
			logger.warn("Malformed URI", e);
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

		// ContentType preferredContentType = CONTENT_TYPE;

		try {

			Object response;
			if (uri.toString().matches(REGEX_FOR_ORION_API)) {
				response = sendRequest(new URL(uri.toString()), "",
						new NotifyContextRequest_OrionCustomization(request),
						NotifyContextResponse.class, ngsi9schema,
						ContentType.JSON);
			} else {
				response = sendRequest(new URL(uri.toString()), "", request,
						NotifyContextResponse.class, ngsi9schema);
			}

			// If there was an error then a StatusCode has been returned
			if (response instanceof StatusCode) {
				output = new NotifyContextResponse((StatusCode) response);
				return output;
			}

			// Cast the response
			output = (NotifyContextResponse) response;

		} catch (MalformedURLException e) {
			logger.warn("Malformed URI", e);
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
