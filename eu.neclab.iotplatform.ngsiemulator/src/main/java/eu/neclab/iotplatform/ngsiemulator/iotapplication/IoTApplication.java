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

package eu.neclab.iotplatform.ngsiemulator.iotapplication;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import org.apache.log4j.Logger;

import com.sun.jersey.api.core.ResourceConfig;

import eu.neclab.iotplatform.iotbroker.commons.ContentType;
import eu.neclab.iotplatform.ngsi.api.datamodel.NgsiStructure;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextRequest_OrionCustomization;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.StatusCode;
import eu.neclab.iotplatform.ngsiemulator.utils.HeaderExtractor;

@Path("")
public class IoTApplication {

	// The logger
	private static Logger logger = Logger.getLogger(IoTApplication.class);

	private static String urlIoTAgent = "http://127.0.0.1:8004/ngsi10/notify";

	private static String path = System.getProperty("confman.testing.path",
			"xml/");
	// private static String notificationFile = "notification.xml";
	private static String queryContextResponseFile = "queryContextResponse.xml";
	private static String notifyContextRequestFile = "notifyContextRequest.xml";
	private static String subscribeContextResponseFile = "subscribeContextResponse.xml";

	private final ContentType defaultIncomingContentType = ContentType
			.fromString(
					System.getProperty("eu.neclab.iotplaform.ngsiemulator.iotprovider.defaultIncomingContentType"),
					ContentType.XML);

	private final ContentType defaultOutgoingContentType = ContentType
			.fromString(
					System.getProperty("eu.neclab.iotplaform.ngsiemulator.iotprovider.defaultOutgoingContentType"),
					ContentType.XML);

	@GET
	@Path("/ngsi10/test")
	@Produces("application/xml")
	public String test() {

		return "test";

	}

	@POST
	@Path("/ngsi10/testPost")
	@Produces("application/json")
	public String testPost(@Context HttpHeaders headers,
			@Context ResourceConfig config, String body) {
		logger.info("Received a testPost:" + body);
		return "test";

	}

	@POST
	@Path("/v1/notify")
	@Consumes("application/json")
	@Produces("application/json")
	public String notifyRespOrion(@Context HttpHeaders headers,
			@Context ResourceConfig config, String body) {

		// Get the accepted content type
		final ContentType outgoingContentType = HeaderExtractor.getAccept(
				headers, ContentType.JSON);
		final ContentType incomingContentType = HeaderExtractor.getContentType(
				headers, ContentType.JSON);

		NotifyContextResponse response = new NotifyContextResponse();
		logger.info("Received a NGSI-10 Notification");
		if (logger.isDebugEnabled()) {
			logger.debug("Received a NGSI-10 Notification:" + body);
		}

		if (logger.isDebugEnabled()) {
			NotifyContextRequest_OrionCustomization notification;
			if (incomingContentType == ContentType.JSON) {
				notification = (NotifyContextRequest_OrionCustomization) NgsiStructure
						.parseStringToJson(body,
								NotifyContextRequest_OrionCustomization.class);
				logger.debug("Parsed NGSI-10 Orion Notification:"
						+ notification.toJsonString());
			} else {
				logger.warn("Wrong content type: "
						+ incomingContentType.toString() + " body: " + body);
			}

		}

		response.setResponseCode(new StatusCode(200, "OK", null));

		if (outgoingContentType == ContentType.XML) {
			return response.toString();
		} else {
			return response.toJsonString();
		}
	}

	@POST
	@Path("/ngsi10/notify")
	@Consumes("application/json,application/xml")
	@Produces("application/json,application/xml")
	public String notifyResp(@Context HttpHeaders headers,
			@Context ResourceConfig config, String body) {

		// Get the accepted content type
		final ContentType outgoingContentType = HeaderExtractor.getAccept(
				headers, defaultOutgoingContentType);
		final ContentType incomingContentType = HeaderExtractor.getContentType(
				headers, defaultOutgoingContentType);

		NotifyContextResponse response = new NotifyContextResponse();
		logger.info("Received a NGSI-10 Notification");
		if (logger.isDebugEnabled()) {
			logger.debug("Received a NGSI-10 Notification:" + body);
		}

		if (logger.isDebugEnabled()) {
			NotifyContextRequest notification;
			if (incomingContentType == ContentType.JSON) {
				notification = (NotifyContextRequest) NgsiStructure
						.parseStringToJson(body, NotifyContextRequest.class);
			} else {
				notification = (NotifyContextRequest) NgsiStructure
						.convertStringToXml(body, NotifyContextRequest.class);
			}
			logger.debug("Parsed NGSI-10 Notification:"
					+ notification.toJsonString());

		}

		response.setResponseCode(new StatusCode(200, "OK", null));

		if (outgoingContentType == ContentType.XML) {
			return response.toString();
		} else {
			return response.toJsonString();
		}
	}

	@POST
	@Path("/ngsi9/notify")
	@Consumes("application/json,application/xml")
	@Produces("application/json,application/xml")
	public String notifyAvailablityResp(@Context HttpHeaders headers,
			@Context ResourceConfig config, String body) {

		// Get the accepted content type
		final ContentType outgoingContentType = HeaderExtractor.getAccept(
				headers, defaultOutgoingContentType);
		final ContentType incomingContentType = HeaderExtractor.getContentType(
				headers, defaultOutgoingContentType);

		NotifyContextResponse response = new NotifyContextResponse();
		logger.info("Received a NGSI-9 Notification");
		if (logger.isDebugEnabled()) {
			logger.debug("Received a NGSI-9 Notification:" + body);
		}

		if (logger.isDebugEnabled()) {
			NotifyContextAvailabilityRequest notification;

			if (incomingContentType == ContentType.JSON) {
				notification = (NotifyContextAvailabilityRequest) NgsiStructure
						.parseStringToJson(body,
								NotifyContextAvailabilityRequest.class);
			} else {
				notification = (NotifyContextAvailabilityRequest) NgsiStructure
						.convertStringToXml(body,
								NotifyContextAvailabilityRequest.class);
			}
			logger.debug("Parsed NGSI-9 Notification:"
					+ notification.toJsonString());

		}

		response.setResponseCode(new StatusCode(200, "OK", null));

		if (outgoingContentType == ContentType.XML) {
			return response.toString();
		} else {
			return response.toJsonString();
		}
	}

}
