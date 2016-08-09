package eu.neclab.iotplatform.mocks.iotprovider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.sun.jersey.api.core.ResourceConfig;

import eu.neclab.iotplatform.mocks.utils.Connector;
import eu.neclab.iotplatform.mocks.utils.ContentType;
import eu.neclab.iotplatform.mocks.utils.HeaderExtractor;
import eu.neclab.iotplatform.mocks.utils.Mode;
import eu.neclab.iotplatform.mocks.utils.ServerConfiguration;
import eu.neclab.iotplatform.mocks.utils.UniqueIDGenerator;
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
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextResponse;

@Path("ngsi10")
public class IoTProvider {

	// The logger
	private static Logger logger = Logger.getLogger(IoTProvider.class);

	private final ContentType defaultIncomingContentType = ContentType
			.fromString(
					System.getProperty("eu.neclab.iotplaform.mocks.iotprovider.defaultIncomingContentType"),
					ContentType.XML);

	private final ContentType defaultOutgoingContentType = ContentType
			.fromString(
					System.getProperty("eu.neclab.iotplaform.mocks.iotprovider.defaultOutgoingContentType"),
					ContentType.XML);

	// private final String defaultMode = System.getProperty(
	// "eu.neclab.iotplaform.mocks.iotprovider.defaultMode", "random");

	private final String queryContextResponseFile = System
			.getProperty(
					"eu.neclab.iotplaform.mocks.iotprovider.defaultQueryContextResponseFile",
					"queryContextResponse.xml");

	private static String urlIoTAgent = "http://127.0.0.1:8004/ngsi10/notify";

	// private static String path = System.getProperty(
	// "eu.neclab.ioplatform.mocks.iotprovider.path", "xml/");

	// private static String notificationFile = "notification.xml";
	// private static String queryContextResponseFile =
	// "queryContextResponse.xml";
	private static String notifyContextRequestFile = "notifyContextRequest.xml";
	private static String subscribeContextResponseFile = "subscribeContextResponse.xml";

	@GET
	@Path("/test")
	@Produces("application/xml")
	public String test(@Context ResourceConfig config) {

		return "test";

	}

	@POST
	@Path("/testPost")
	@Produces("application/json")
	public String testPost(String body) {
		logger.info("Received a testPost:" + body);
		return "test";

	}

	@POST
	@Path("/queryContext")
	@Consumes("application/json,application/xml")
	@Produces("application/json,application/xml")
	public String queryContext(String body, @Context HttpHeaders headers,
			@Context ResourceConfig config) {

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
				return readQueryContextResponseFromFile(ServerConfiguration.DEFAULT_QUERYCONTEXTRESPONSEFILE);
			} else {
				return readQueryContextResponseFromFile((String) file);
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

		Random rand = new Random();

		List<ContextElementResponse> contextElementResponseList = new ArrayList<ContextElementResponse>();

		StatusCode okCode = new StatusCode(Code.OK_200.getCode(),
				ReasonPhrase.OK_200.toString(), "");

		for (EntityId entityId : queryRequest.getEntityIdList()) {

			List<ContextAttribute> contextAttributeList = new ArrayList<ContextAttribute>();

			for (String attributeName : queryRequest.getAttributeList()) {

				contextAttributeList.add(new ContextAttribute(attributeName,
						null, "" + rand.nextInt()));

			}
			ContextElementResponse contextElementResponse = new ContextElementResponse();

			contextElementResponse.setContextElement(new ContextElement(
					entityId, null, contextAttributeList, null));
			contextElementResponse.setStatusCode(okCode);

			contextElementResponseList.add(contextElementResponse);
		}

		QueryContextResponse response = new QueryContextResponse();
		response.setContextResponseList(contextElementResponseList);

		return response;

	}

	private String readQueryContextResponseFromFile(String file) {

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

	public static String readNotifyFromFile(String id) {

		NotifyContextRequest response = new NotifyContextRequest();
		// String file = null;
		// try {
		//
		// file = path + "/" + notifyContextRequestFile;
		// InputStream is = new FileInputStream(file);
		//
		// JAXBContext context;
		// context = JAXBContext.newInstance(NotifyContextRequest.class);
		//
		// // Create the marshaller, this is the nifty little thing that
		// // will actually transform the object into XML
		// Unmarshaller unmarshaller = context.createUnmarshaller();
		// response = (NotifyContextRequest) unmarshaller.unmarshal(is);
		//
		// response.setOriginator(urlIoTAgent);
		// response.setSubscriptionId(id);
		//
		// } catch (JAXBException e) {
		// logger.error("JAXB ERROR!", e);
		// } catch (FileNotFoundException e) {
		// logger.error("FILE NOT FOUND!: " + file);
		// }
		//
		String resp = response.toString();

		return resp;
	}

	@POST
	@Path("/subscribeContext")
	@Produces("application/xml")
	public SubscribeContextResponse subscriptionResponse(String body) {

		logger.info("Received a NGSI-10 SubscribeContext");
		if (logger.isDebugEnabled()) {
			logger.debug("Received a NGSI-10 SubscribeContext: " + body);
		}

		final String reference;
		Pattern pattern_reference = Pattern
				.compile("<reference>(\\S+)</reference>");

		Matcher matcher = pattern_reference
				.matcher(body.replaceAll("\\s+", ""));

		if (matcher.find()) {
			reference = matcher.group(1);
		} else {
			return null;
		}

		UniqueIDGenerator idGenerator = new UniqueIDGenerator();
		final String id = idGenerator.getNextUniqueId();
		ScheduledExecutorService executorService = Executors
				.newScheduledThreadPool(1);
		executorService.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {

				Connector conn = null;
				try {
					conn = new Connector(new URL(reference));

				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				conn.start("", "POST", readNotifyFromFile(id.toString()));

			}

		}, 1, 2, TimeUnit.SECONDS);

		SubscribeContextResponse response = new SubscribeContextResponse();

		String file = null;
		try {
			file = subscribeContextResponseFile;
			InputStream is = new FileInputStream(subscribeContextResponseFile);

			JAXBContext context;
			context = JAXBContext.newInstance(SubscribeContextResponse.class);

			// Create the marshaller, this is the nifty little thing that
			// will actually transform the object into XML
			Unmarshaller unmarshaller = context.createUnmarshaller();
			response = (SubscribeContextResponse) unmarshaller.unmarshal(is);

			response.setSubscribeResponse(new SubscribeResponse(id.toString(),
					response.getSubscribeResponse().getDuration(), null));

			if (logger.isDebugEnabled()) {
				logger.debug("NGSI-10 Subscribe response: " + response);
			}

		} catch (JAXBException e) {
			logger.error("JAXB ERROR!", e);
		} catch (FileNotFoundException e) {
			logger.error("FILE NOT FOUND!: " + file);
		}

		return response;

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
