package eu.neclab.iotplatform.mocks.iotapplication;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;

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
	// @Produces("application/json")
	public NotifyContextResponse notifyResp(String body) {

		NotifyContextResponse response = new NotifyContextResponse();
		logger.info("Received a NGSI-10 Notification");
		if (logger.isDebugEnabled()) {
			logger.debug("Received a NGSI-10 Notification:" + body);
		}

		response.setResponseCode(new StatusCode(200, "OK", null));

		return response;

	}


}
