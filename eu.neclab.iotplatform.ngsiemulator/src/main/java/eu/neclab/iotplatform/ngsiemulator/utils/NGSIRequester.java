package eu.neclab.iotplatform.ngsiemulator.utils;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.Header;
import org.apache.log4j.Logger;

import eu.neclab.iotplatform.iotbroker.commons.ContentType;
import eu.neclab.iotplatform.iotbroker.commons.FullHttpRequester;
import eu.neclab.iotplatform.iotbroker.commons.FullHttpResponse;
import eu.neclab.iotplatform.iotbroker.commons.XmlFactory;
import eu.neclab.iotplatform.ngsi.api.datamodel.Code;
import eu.neclab.iotplatform.ngsi.api.datamodel.NgsiStructure;
import eu.neclab.iotplatform.ngsi.api.datamodel.ReasonPhrase;
import eu.neclab.iotplatform.ngsi.api.datamodel.RegisterContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.RegisterContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.StatusCode;

public class NGSIRequester {

	/** The logger. */
	private static Logger logger = Logger.getLogger(NGSIRequester.class);

	// Get the IoT Discovery URL
	private static String iotDiscoveryURL = System.getProperty(
			"eu.neclab.ioplatform.mocks.iotDiscoveryUrl",
			"http://localhost:8065/");

	public void doRegistration(RegisterContextRequest registration,
			ContentType preferredContentType) {

		RegisterContextResponse output = new RegisterContextResponse();

		try {

			Object response = sendRequest(new URL(iotDiscoveryURL), "/"
					+ "ngsi9" + "/" + "registerContext", registration,
					RegisterContextResponse.class, preferredContentType);

			// If there was an error then a StatusCode has been returned
			if (response instanceof StatusCode) {
				output = new RegisterContextResponse(null, null,
						(StatusCode) response);
			} else {

				// Cast the response
				output = (RegisterContextResponse) response;
			}

		} catch (MalformedURLException e) {
			logger.warn("Malformed URI", e);

			output = new RegisterContextResponse(null, null, new StatusCode(
					Code.INTERNALERROR_500.getCode(),
					ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
					ReasonPhrase.RECEIVERINTERNALERROR_500.toString()));

		}

		logger.info("Registration done: " + output);
	}

	/**
	 * Calls the QueryContext method on an NGSI-10 server.
	 * 
	 * @return A StatusCode if there was an error, otherwise an object of the
	 *         expectedResponseClazz
	 * 
	 */
	private Object sendRequest(URL url, String resource, NgsiStructure request,
			Class<? extends NgsiStructure> expectedResponseClazz, ContentType preferredContentType) {

		Object output;

		try {
			String correctedResource;
			if (url.toString().isEmpty() || url.toString().matches(".*/")) {
				correctedResource = resource;
			} else {
				correctedResource = "/" + resource;
			}

			FullHttpResponse response = sendPostTryingAllSupportedContentType(
					new URL(url + correctedResource), request,
					preferredContentType, correctedResource);

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
			 * Check if there contentType is not supported and switch to the
			 * other IoT Broker supports
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

		} catch (Exception e) {
			logger.warn("Exception", e);
			return fullHttpResponse;
		}

		return fullHttpResponse;

	}

	private Object parseResponse(String body, ContentType contentType,
			Class<? extends NgsiStructure> clazz) {
		if (contentType == ContentType.XML) {

			return XmlFactory.convertStringToXml(body, clazz);

		} else {
			String toParse = body.replaceAll("\\\"metadatas\\\"",
					"\\\"contextMetadata\\\"");

			return NgsiStructure.parseStringToJson(toParse, clazz, true, true);

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

}
