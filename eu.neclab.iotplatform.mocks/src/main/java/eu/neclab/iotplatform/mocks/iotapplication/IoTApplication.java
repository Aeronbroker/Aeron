package eu.neclab.iotplatform.mocks.iotapplication;

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
import eu.neclab.iotplatform.mocks.utils.HeaderExtractor;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.StatusCode;

@Path("ngsi10")
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
					System.getProperty("eu.neclab.iotplaform.mocks.iotprovider.defaultIncomingContentType"),
					ContentType.XML);

	private final ContentType defaultOutgoingContentType = ContentType
			.fromString(
					System.getProperty("eu.neclab.iotplaform.mocks.iotprovider.defaultOutgoingContentType"),
					ContentType.XML);
	
	@GET
	@Path("/test")
	@Produces("application/xml")
	public String test() {

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
	@Path("/notify")
	@Consumes("application/json,application/xml")
	@Produces("application/json,application/xml")
	public String notifyResp(@Context HttpHeaders headers,
			@Context ResourceConfig config, String body) {

		// Get the accepted content type
		final ContentType outgoingContentType = HeaderExtractor.getAccept(
				headers, defaultOutgoingContentType);

		NotifyContextResponse response = new NotifyContextResponse();
		logger.info("Received a NGSI-10 Notification");
		if (logger.isDebugEnabled()) {
			logger.debug("Received a NGSI-10 Notification:" + body);
		}

		response.setResponseCode(new StatusCode(200, "OK", null));

		if (outgoingContentType == ContentType.XML) {
			return response.toString();
		} else {
			return response.toJsonString();
		}

	}

}
