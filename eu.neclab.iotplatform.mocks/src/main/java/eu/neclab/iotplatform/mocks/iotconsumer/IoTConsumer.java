package eu.neclab.iotplatform.mocks.iotconsumer;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import org.apache.log4j.Logger;

import eu.neclab.iotplatform.mocks.utils.ContentType;
import eu.neclab.iotplatform.mocks.utils.HeaderExtractor;
import eu.neclab.iotplatform.ngsi.api.datamodel.StatusCode;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextResponse;

@Path("ngsi10")
public class IoTConsumer {

	// The logger
	private static Logger logger = Logger.getLogger(IoTConsumer.class);

	private final ContentType defaultOutgoingContentType = ContentType
			.fromString(
					System.getProperty("eu.neclab.iotplaform.mocks.iotconsumer.defaultOutgoingContentType"),
					ContentType.XML);

	public IoTConsumer() {
		System.out.println("Create again");
	}

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
	@Path("/updateContext")
	@Consumes("application/json,application/xml")
	@Produces("application/json,application/xml")
	public String updateContext(String body, @Context HttpHeaders headers) {

		// Get the accept contentType
		ContentType outgoingContentType = HeaderExtractor.getAccept(headers,
				defaultOutgoingContentType);

		// Let tell to everybody what we just got
		logger.info("Received a NGSI-10 Update");
		if (logger.isDebugEnabled()) {
			logger.debug("NGSI-10 Update received: " + body);
		}

		// Create the response
		UpdateContextResponse response = new UpdateContextResponse();
		response.setErrorCode(new StatusCode(200, "OK", null));

		// Respond back according to the accepted content-type
		if (outgoingContentType == ContentType.JSON) {
			return response.toJsonString();
		} else {
			return response.toString();
		}

	}

}
