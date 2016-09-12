package eu.neclab.iotplatform.ngsiemulator.iotprovider;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
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

import org.apache.log4j.Logger;

import com.sun.jersey.api.core.ResourceConfig;

import eu.neclab.iotplatform.iotbroker.commons.ContentType;
import eu.neclab.iotplatform.iotbroker.commons.FullHttpRequester;
import eu.neclab.iotplatform.iotbroker.commons.FullHttpResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.Code;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElement;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElementResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.NgsiStructure;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextResponse;
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

@Path("ngsi10")
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

	// private final String defaultMode = System.getProperty(
	// "eu.neclab.iotplaform.ngsiemulator.iotprovider.defaultMode", "random");

	// private final String queryContextResponseFile = System
	// .getProperty(
	// "eu.neclab.iotplaform.ngsiemulator.iotprovider.defaultQueryContextResponseFile",
	// "queryContextResponse.xml");

	// private static String urlIoTAgent =
	// "http://127.0.0.1:8004/ngsi10/notify";

	// private static String path = System.getProperty(
	// "eu.neclab.ioplatform.ngsiemulator.iotprovider.path", "xml/");

	// private static String notificationFile = "notification.xml";
	// private static String queryContextResponseFile =
	// "queryContextResponse.xml";
	// private static String notifyContextRequestFile =
	// "notifyContextRequest.xml";
	// private static String subscribeContextResponseFile =
	// "subscribeContextResponse.xml";

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
	@Path("/queryContext")
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
			mode = (Mode) Mode.fromString((String) contextMode,
					ServerConfiguration.DEFAULT_MODE);
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

			response = createQueryContextResponse(queryContextRequest);

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

		// QueryContextResponse response = new QueryContextResponse();
		//
		// String file = null;
		// try {
		// file = path + "/" + queryContextResponseFile;
		//
		// InputStream is = new FileInputStream(file);
		//
		// JAXBContext context;
		// context = JAXBContext.newInstance(QueryContextResponse.class);
		//
		// // Create the marshaller, this is the nifty little thing that
		// // will actually transform the object into XML
		// Unmarshaller unmarshaller = context.createUnmarshaller();
		// response = (QueryContextResponse) unmarshaller.unmarshal(is);
		//
		// if (logger.isDebugEnabled()) {
		// logger.debug("NGSI-10 Query response: " + response);
		// }
		//
		// } catch (JAXBException e) {
		// logger.error("JAXB ERROR!", e);
		// } catch (FileNotFoundException e) {
		// logger.error("FILE NOT FOUND!: " + file);
		// }
		//
		// if (outgoingContentType == ContentType.JSON) {
		// return response.toJsonString();
		// } else {
		// return response.toString();
		// }
	}

	private QueryContextResponse createQueryContextResponse(
			QueryContextRequest queryRequest) {

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

		QueryContextResponse response = new QueryContextResponse();
		response.setContextResponseList(createContextElementResponses(
				queryRequest.getEntityIdList(), queryRequest.getAttributeList()));

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

		// try {
		//
		// // file = notifyContextRequestFile;
		// InputStream is = new FileInputStream(file);
		//
		// JAXBContext context;
		// context = JAXBContext.newInstance(QueryContextResponse.class);
		//
		// // Create the marshaller, this is the nifty little thing that
		// // will actually transform the object into XML
		// Unmarshaller unmarshaller = context.createUnmarshaller();
		// response = (QueryContextResponse) unmarshaller.unmarshal(is);
		//
		// } catch (JAXBException e) {
		// logger.error("JAXB ERROR!", e);
		// } catch (FileNotFoundException e) {
		// logger.error("FILE NOT FOUND!: " + file);
		// }

	}

	@POST
	@Path("/subscribeContext")
	@Consumes("application/json,application/xml")
	@Produces("application/json,application/xml")
	public String subscriptionResponse(@Context HttpHeaders headers,
			@Context ResourceConfig config, String body,
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
			mode = (Mode) Mode.fromString((String) contextMode,
					ServerConfiguration.DEFAULT_MODE);
		} else {
			mode = ServerConfiguration.DEFAULT_MODE;
		}

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
			if (contextPeriod != null && contextPeriod instanceof String) {
				period = (Integer) Integer.parseInt((String) contextPeriod,
						ServerConfiguration.DEFAULT_NOTIFICATIONPERIOD);
			} else {
				period = ServerConfiguration.DEFAULT_NOTIFICATIONPERIOD;
			}

			// Create the timer for the notification thread
			ScheduledExecutorService executorService = Executors
					.newScheduledThreadPool(1);
			executorService.scheduleWithFixedDelay(new Runnable() {

				@Override
				public void run() {

					NotifyContextRequest notification = createNotifyContext(
							subscribeContextRequest, id, originator);

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
	@Path("/unsubscribeContext")
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
