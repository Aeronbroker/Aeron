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

package eu.neclab.iotplatform.ngsiemulator.iotprovider;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import com.sun.jersey.api.core.ResourceConfig;

import eu.neclab.iotplatform.iotbroker.commons.ContentType;
import eu.neclab.iotplatform.iotbroker.commons.FullHttpRequester;
import eu.neclab.iotplatform.iotbroker.commons.FullHttpResponse;
import eu.neclab.iotplatform.iotbroker.commons.ParseUtils;
import eu.neclab.iotplatform.ngsi.api.datamodel.Code;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElement;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElementResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.NgsiStructure;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextRequest_OrionCustomization;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextResponse_OrionCustomization;
import eu.neclab.iotplatform.ngsi.api.datamodel.ReasonPhrase;
import eu.neclab.iotplatform.ngsi.api.datamodel.StatusCode;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextResponse;
import eu.neclab.iotplatform.ngsiemulator.utils.HeaderExtractor;
import eu.neclab.iotplatform.ngsiemulator.utils.Mode;
import eu.neclab.iotplatform.ngsiemulator.utils.ServerConfiguration;
import eu.neclab.iotplatform.ngsiemulator.utils.UniqueIDGenerator;

@Path("")
public class IoTProvider {

	// The logger
	private static Logger logger = Logger.getLogger(IoTProvider.class);

	private final ContentType defaultIncomingContentType = ContentType
			.fromString(
					System.getProperty("eu.neclab.iotplaform.ngsiemulator.iotprovider.defaultIncomingContentType"),
					ContentType.XML);

	private final ContentType defaultOutgoingContentType = ContentType
			.fromString(
					System.getProperty("eu.neclab.iotplaform.ngsiemulator.iotprovider.defaultOutgoingContentType"),
					ContentType.XML);

	@POST
	@Path("/{s:.*}")
	@Produces("application/json")
	public String wildcardPost(String body, @Context ResourceConfig config, @Context UriInfo ui, @Context HttpServletRequest req) {
		logger.info("Received a post at "+ ui.getPath()+" with body:" + body);
		return "test";

	}
	
	@GET
	@Path("/test")
	@Produces("application/xml")
	public String test(@Context ResourceConfig config) {

		String string = new String(
				"{\"contextElement\":{\"entityId\":{\"id\":\"thermo1\",\"type\":\"thermometer\",\"isPattern\":false},\"attributeDomainName\":null,\"domainMetadata\":[],\"attributes\":[{\"name\":\"humidity\",\"type\":\"float\",\"contextValue\":\"30.00\",\"metadata\":[{\"name\":\"unit\",\"type\":\"string\",\"value\":\"%\"}]},{\"name\":\"temperature\",\"type\":\"float\",\"contextValue\":\"20.00\",\"metadata\":[{\"name\":\"unit\",\"type\":\"string\",\"value\":\"celsius\"}]},{\"name\":\"humidity\",\"type\":\"float\",\"contextValue\":\"30.00\",\"metadata\":[{\"name\":\"unit\",\"type\":\"string\",\"value\":\"%\"}]},{\"name\":\"temperature\",\"type\":\"float\",\"contextValue\":\"20.00\",\"metadata\":[{\"name\":\"unit\",\"type\":\"string\",\"value\":\"celsius\"}]}]},\"statusCode\":{\"code\":200,\"reasonPhrase\":\"OK\",\"details\":null}}");

		return string;

	}

	@POST
	@Path("/testPost")
	@Produces("application/json")
	public String testPost(String body) {
		logger.info("Received a testPost:" + body);
		return "test";

	}

	public static String getClientIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_FORWARDED");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_VIA");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("REMOTE_ADDR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip == null) {
			ip = "unknown";
		}
		return ip;
	}

	@POST
	@Path("/v1/queryContext")
	@Consumes("application/json,application/xml")
	@Produces("application/json,application/xml")
	public String queryContext_Orion(@Context HttpHeaders headers,
			@Context ResourceConfig config, String body,
			@Context HttpServletRequest req) {

		Mode mode;
		Object contextMode = config.getProperty("mode");
		if (contextMode != null && contextMode instanceof String) {
			mode = (Mode) Mode.fromString((String) contextMode,
					ServerConfiguration.DEFAULT_MODE);
		} else {
			mode = ServerConfiguration.DEFAULT_MODE;
		}

		QueryContextResponse_OrionCustomization response;

		if (mode == Mode.RANDOM) {
			QueryContextRequest_OrionCustomization query = (QueryContextRequest_OrionCustomization) NgsiStructure
					.parseStringToJson(body,
							QueryContextRequest_OrionCustomization.class);

			logger.info("Received a NGSI-10 Query");
			if (logger.isDebugEnabled()) {
				logger.debug("NGSI-10 Query received: " + body);
			}

			response = new QueryContextResponse_OrionCustomization(
					createQueryContextResponse(query.toQueryContextRequest()));

			return response.toJsonString();

		} else {
			Object file = config.getProperty("queryContextResponseFile");
			if (file == null) {
				return readFile(ServerConfiguration.DEFAULT_QUERYCONTEXTRESPONSEFILE);
			} else {
				return readFile((String) file);
			}
		}

	}

	@POST
	@Path("/ngsi10/queryContext")
	@Consumes("application/json,application/xml")
	@Produces("application/json,application/xml")
	public String queryContext(String body, @Context HttpHeaders headers,
			@Context ResourceConfig config) {

		// // System.out.println("here I get something?"+req.getReader());
		// if ("POST".equalsIgnoreCase(req.getMethod())) {
		// try {
		// System.out.println(CharStreams.toString(req.getReader()));
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }

		// Get the incoming content-type
		ContentType incomingContentType = HeaderExtractor.getContentType(
				headers, defaultIncomingContentType);

		// Get the accepted content type
		ContentType outgoingContentType = HeaderExtractor.getAccept(headers,
				defaultOutgoingContentType);

		Mode mode;
		Object contextMode = config.getProperty("mode");
		if (contextMode != null && contextMode instanceof String) {
			if (contextMode instanceof String) {
				mode = (Mode) Mode.fromString((String) contextMode,
						ServerConfiguration.DEFAULT_MODE);
			} else if (contextMode instanceof Mode) {
				mode = (Mode) contextMode;
			} else {
				mode = ServerConfiguration.DEFAULT_MODE;
			}
		} else {
			mode = ServerConfiguration.DEFAULT_MODE;
		}

		QueryContextResponse response;
		if (mode == Mode.RANDOM) {

			// Parse the request
			QueryContextRequest queryContextRequest;
			if (incomingContentType == ContentType.JSON) {
				queryContextRequest = (QueryContextRequest) NgsiStructure
						.parseStringToJson(body, QueryContextRequest.class);
			} else {
				queryContextRequest = (QueryContextRequest) NgsiStructure
						.convertStringToXml(body, QueryContextRequest.class);
			}

			logger.info("Received a NGSI-10 Query");
			if (logger.isDebugEnabled()) {
				logger.debug("NGSI-10 Query received: " + body);
			}

			if (ParseUtils.parseBooleanOrDefault(
					config.getProperty("doRegistration"), false)) {

				Set<String> entityIds = ParseUtils.parseSetFromString(config
						.getProperty("entityNames"));

				Set<String> attributeNames = ParseUtils
						.parseSetFromString(config
								.getProperty("attributeNames"));

				if (!entityIds.isEmpty() && !attributeNames.isEmpty()) {

					response = createQueryContextResponse(queryContextRequest,
							entityIds, attributeNames);

				} else {
					response = createQueryContextResponse(queryContextRequest);
				}
			} else {
				response = createQueryContextResponse(queryContextRequest);
			}

			if (outgoingContentType == ContentType.XML) {
				return response.toString();
			} else {
				return response.toJsonString();
			}

		} else {
			Object file = config.getProperty("queryContextResponseFile");
			if (file == null) {
				return readFile(ServerConfiguration.DEFAULT_QUERYCONTEXTRESPONSEFILE);
			} else {
				return readFile((String) file);
			}
		}

	}

	private QueryContextResponse createQueryContextResponse(
			QueryContextRequest queryRequest) {

		QueryContextResponse response = new QueryContextResponse();
		response.setContextResponseList(createContextElementResponses(
				queryRequest.getEntityIdList(), queryRequest.getAttributeList()));

		return response;

	}

	private QueryContextResponse createQueryContextResponse(
			QueryContextRequest queryRequest, Set<String> entityIds,
			Set<String> attributeNames) {

		QueryContextResponse response = new QueryContextResponse();
		response.setContextResponseList(createContextElementResponses(
				queryRequest.getEntityIdList(),
				queryRequest.getAttributeList(), entityIds, attributeNames));

		return response;

	}

	private NotifyContextRequest createNotifyContext(
			SubscribeContextRequest subscribeContext, String subscriptionID,
			String originator) {

		// Random rand = new Random();

		// List<ContextElementResponse> contextElementResponseList = new
		// ArrayList<ContextElementResponse>();
		//
		// StatusCode okCode = new StatusCode(Code.OK_200.getCode(),
		// ReasonPhrase.OK_200.toString(), "");
		//
		// for (EntityId entityId : queryRequest.getEntityIdList()) {
		//
		// List<ContextAttribute> contextAttributeList = new
		// ArrayList<ContextAttribute>();
		//
		// for (String attributeName : queryRequest.getAttributeList()) {
		//
		// contextAttributeList.add(new ContextAttribute(attributeName,
		// null, "" + rand.nextInt()));
		//
		// }
		// ContextElementResponse contextElementResponse = new
		// ContextElementResponse();
		//
		// contextElementResponse.setContextElement(new ContextElement(
		// entityId, null, contextAttributeList, null));
		// contextElementResponse.setStatusCode(okCode);
		//
		// contextElementResponseList.add(contextElementResponse);
		// }

		NotifyContextRequest notification = new NotifyContextRequest();
		notification.setContextResponseList(createContextElementResponses(
				subscribeContext.getEntityIdList(),
				subscribeContext.getAttributeList()));
		notification.setSubscriptionId(subscriptionID);
		notification.setOriginator(originator);

		return notification;

	}

	private NotifyContextRequest createNotifyContext(
			SubscribeContextRequest subscribeContext, String subscriptionID,
			String originator, Set<String> allowedEntityIds,
			Set<String> allowedAttributeNames) {

		NotifyContextRequest notification = new NotifyContextRequest();
		notification.setContextResponseList(createContextElementResponses(
				subscribeContext.getEntityIdList(),
				subscribeContext.getAttributeList(), allowedEntityIds,
				allowedAttributeNames));
		notification.setSubscriptionId(subscriptionID);
		notification.setOriginator(originator);

		return notification;

	}

	private List<ContextElementResponse> createContextElementResponses(
			List<EntityId> entityIdList, List<String> attributeList) {

		Random rand = new Random();

		List<ContextElementResponse> contextElementResponseList = new ArrayList<ContextElementResponse>();

		StatusCode okCode = new StatusCode(Code.OK_200.getCode(),
				ReasonPhrase.OK_200.toString(), "");

		for (EntityId entityId : entityIdList) {

			List<ContextAttribute> contextAttributeList = new ArrayList<ContextAttribute>();

			for (String attributeName : attributeList) {

				contextAttributeList.add(new ContextAttribute(attributeName,
						null, "" + rand.nextInt()));

			}
			ContextElementResponse contextElementResponse = new ContextElementResponse();

			contextElementResponse.setContextElement(new ContextElement(
					entityId, null, contextAttributeList, null));
			contextElementResponse.setStatusCode(okCode);

			contextElementResponseList.add(contextElementResponse);
		}

		return contextElementResponseList;

	}

	private List<ContextElementResponse> createContextElementResponses(
			List<EntityId> entityIdList, List<String> attributeList,
			Set<String> allowedEntityIds, Set<String> allowedAttributeNames) {

		Random rand = new Random();

		List<ContextElementResponse> contextElementResponseList = new ArrayList<ContextElementResponse>();

		StatusCode okCode = new StatusCode(Code.OK_200.getCode(),
				ReasonPhrase.OK_200.toString(), "");

		Set<String> entityIdsToUpdate = new HashSet<String>();

		for (EntityId entityId : entityIdList) {

			if (entityId.getIsPattern()) {
				if (".*".equals(entityId.getId())) {
					entityIdsToUpdate.addAll(allowedEntityIds);
				} else {
					for (String id : allowedEntityIds) {
						if (id.matches(entityId.getId())) {
							entityIdsToUpdate.add(id);
						}
					}
				}
			} else {
				if (allowedEntityIds.contains(entityId.getId())) {
					entityIdsToUpdate.add(entityId.getId());
				}
			}

		}

		for (String id : entityIdsToUpdate) {

			List<ContextAttribute> contextAttributeList = new ArrayList<ContextAttribute>();

			if (attributeList == null || attributeList.isEmpty()) {
				for (String attributeName : allowedAttributeNames) {

					contextAttributeList.add(new ContextAttribute(
							attributeName, null, "" + rand.nextInt()));

				}
			} else {
				for (String attributeName : attributeList) {

					if (allowedAttributeNames.contains(attributeName)) {

						contextAttributeList.add(new ContextAttribute(
								attributeName, null, "" + rand.nextInt()));
					}

				}
			}

			if (contextAttributeList.isEmpty()) {
				continue;
			}
			ContextElementResponse contextElementResponse = new ContextElementResponse();

			contextElementResponse.setContextElement(new ContextElement(
					new EntityId(id, null, false), null, contextAttributeList,
					null));
			contextElementResponse.setStatusCode(okCode);

			contextElementResponseList.add(contextElementResponse);
		}

		return contextElementResponseList;

	}

	private String readFile(String file) {

		String response = null;

		try {
			response = new Scanner(new File(file)).useDelimiter("\\Z").next();

			if (logger.isDebugEnabled()) {
				logger.debug("Response read from file: " + response);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;

	}

	@POST
	@Path("/v1/subscribeContext")
	@Consumes("application/json,application/xml")
	@Produces("application/json,application/xml")
	public String subscriptionResponseOrion(@Context HttpHeaders headers,
			final @Context ResourceConfig config, String body,
			@Context HttpServletRequest req) {
		return subscriptionResponse(headers, config, body, req);
	}

	@POST
	@Path("/ngsi10/subscribeContext")
	@Consumes("application/json,application/xml")
	@Produces("application/json,application/xml")
	public String subscriptionResponse(@Context HttpHeaders headers,
			final @Context ResourceConfig config, String body,
			@Context HttpServletRequest req) {

		final String originator = getClientIpAddr(req);

		// Get the incoming content-type
		ContentType incomingContentType = HeaderExtractor.getContentType(
				headers, defaultIncomingContentType);

		// Get the accepted content type
		final ContentType outgoingContentType = HeaderExtractor.getAccept(
				headers, defaultOutgoingContentType);

		// Get the mode of working
		Mode mode;
		Object contextMode = config.getProperty("mode");
		if (contextMode != null && contextMode instanceof String) {
			if (contextMode instanceof String) {
				mode = (Mode) Mode.fromString((String) contextMode,
						ServerConfiguration.DEFAULT_MODE);
			} else if (contextMode instanceof Mode) {
				mode = (Mode) contextMode;
			} else {
				mode = ServerConfiguration.DEFAULT_MODE;
			}
		} else {
			mode = ServerConfiguration.DEFAULT_MODE;
		}

		logger.info("Received a NGSI-10 SubscribeContext: " + body);

		SubscribeContextResponse response;
		if (mode == Mode.RANDOM) {

			// Parse the request
			final SubscribeContextRequest subscribeContextRequest;
			if (incomingContentType == ContentType.JSON) {
				subscribeContextRequest = (SubscribeContextRequest) NgsiStructure
						.parseStringToJson(body, SubscribeContextRequest.class);
			} else {
				subscribeContextRequest = (SubscribeContextRequest) NgsiStructure
						.convertStringToXml(body, SubscribeContextRequest.class);
			}

			logger.info("Received a NGSI-10 SubscribeContext");
			if (logger.isDebugEnabled()) {
				logger.debug("Received a NGSI-10 SubscribeContext: " + body);
			}

			// Create the subscription id
			UniqueIDGenerator idGenerator = new UniqueIDGenerator();
			final String id = idGenerator.getNextUniqueId();

			// Get the period of notification from configurations
			Integer period;
			Object contextPeriod = config.getProperty("notificationPeriod");
			period = ParseUtils.parseIntOrDefault(contextPeriod,
					ServerConfiguration.DEFAULT_NOTIFICATIONPERIOD);

			final Set<String> entityIds = ParseUtils.parseSetFromString(config
					.getProperty("entityNames"));

			final Set<String> attributeNames = ParseUtils
					.parseSetFromString(config.getProperty("attributeNames"));

			// Create the timer for the notification thread
			ScheduledExecutorService executorService = Executors
					.newScheduledThreadPool(1);
			executorService.scheduleWithFixedDelay(new Runnable() {

				@Override
				public void run() {

					NotifyContextRequest notification;

					if (ParseUtils.parseBooleanOrDefault(
							config.getProperty("doRegistration"), false)) {

						if (!entityIds.isEmpty() && !attributeNames.isEmpty()) {

							notification = createNotifyContext(
									subscribeContextRequest, id, originator,
									entityIds, attributeNames);

						} else {
							notification = createNotifyContext(
									subscribeContextRequest, id, originator);
						}
					} else {
						notification = createNotifyContext(
								subscribeContextRequest, id, originator);
					}

					// NotifyContextRequest notification = createNotifyContext(
					// subscribeContextRequest, id, originator);

					ContentType contentType;
					String data;
					if (outgoingContentType == ContentType.JSON) {
						contentType = ContentType.JSON;
						data = notification.toJsonString();
					} else {
						contentType = ContentType.XML;
						data = notification.toString();
					}

					FullHttpResponse response = null;
					try {
						response = FullHttpRequester.sendPost(new URL(
								subscribeContextRequest.getReference()), data,
								contentType.toString());
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					logger.info("Response to notification: " + response);

				}

			}, 1, period, TimeUnit.SECONDS);

			response = new SubscribeContextResponse();
			response.setSubscribeResponse(new SubscribeResponse(id,
					subscribeContextRequest.getDuration(),
					subscribeContextRequest.getThrottling()));

			if (outgoingContentType == ContentType.XML) {
				return response.toString();
			} else {
				return response.toJsonString();
			}

		} else {
			Object file = config.getProperty("notifyContextRequestFile");
			if (file == null) {
				return readFile(ServerConfiguration.DEFAULT_NOTIFYCONTEXTREQUESTFILE);
			} else {
				return readFile((String) file);
			}
		}

	}

	@POST
	@Path("/ngsi10/unsubscribeContext")
	@Produces("application/xml")
	public UnsubscribeContextResponse unsubscribeContext(String body) {

		UnsubscribeContextResponse response = new UnsubscribeContextResponse();

		final String subId;
		Pattern pattern_subId = Pattern
				.compile("<subscriptionId>(\\S+)</subscriptionId>");

		logger.info("Received a NGSI-10 UnsubscribeContext for id: ");
		if (logger.isDebugEnabled()) {
			logger.debug("Received a NGSI-10 UnsubscribeContext: " + body);
		}

		Matcher matcher = pattern_subId.matcher(body.replaceAll("\\s+", ""));

		if (matcher.find()) {
			subId = matcher.group(1);
		} else {
			return null;
		}

		response.setSubscriptionId(subId);
		response.setStatusCode(new StatusCode(200, "OK", null));

		if (logger.isDebugEnabled()) {
			logger.debug("NGSI-10 UnsubscribeContext response: " + response);
		}

		return response;

	}

}
