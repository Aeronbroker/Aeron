package eu.neclab.iotplatform.mocks.iotproducer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import eu.neclab.iotplatform.mocks.utils.Connector;
import eu.neclab.iotplatform.mocks.utils.Mode;
import eu.neclab.iotplatform.mocks.utils.UniqueIDGenerator;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeResponse;

@Path("ngsi10")
public class IoTProducer {

	// The logger
	private static Logger logger = Logger.getLogger(IoTProducer.class);

	private static String path = System.getProperty(
			"eu.neclab.ioplatform.mocks.iotproducer.path", "xml/");

	private static Mode mode = Mode.fromString(
			System.getProperty("eu.neclab.ioplatform.mocks.iotproducer.mode"),
			Mode.RANDOM);

	private static int period = Integer
			.parseInt(
					System.getProperty("eu.neclab.ioplatform.mocks.iotproducer.period"),
					1000);

	private static String reference = System.getProperty(
			"eu.neclab.ioplatform.mocks.iotproducer.reference",
			"http://localhost:8060/ngsi10/updateContext");

	// private static String notificationFile = "notification.xml";
//	private static String queryContextResponseFile = "queryContextResponse.xml";
//	private static String notifyContextRequestFile = "notifyContextRequest.xml";
//	private static String subscribeContextResponseFile = "subscribeContextResponse.xml";
	private static String updateContextRequestsFile = "updateContextRequests.xml";


//	public IoTProducer() {
//
//		if (mode == Mode.FROMFILE) {
//
//			ScheduledExecutorService executorService = Executors
//					.newScheduledThreadPool(1);
//			executorService.scheduleWithFixedDelay(new Runnable() {
//				@Override
//				public void run() {
//
//					Connector conn = null;
//					try {
//						conn = new Connector(new URL(reference));
//
//					} catch (MalformedURLException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					conn.start("", "POST", readNotifyFromFile(id.toString()));
//
//				}
//
//			}, 1000, period, TimeUnit.MILLISECONDS);
//		
//		} else if (mode == Mode.RANDOM) {
//			
//			
//
//		}
//
//	}
//
//	@GET
//	@Path("/test")
//	@Produces("application/xml")
//	public String test() {
//
//		return "test";
//
//	}
//
//	@POST
//	@Path("/testPost")
//	@Produces("application/json")
//	public String testPost(String body) {
//		logger.info("Received a testPost:" + body);
//		return "test";
//
//	}
//
//	public static String readNotifyFromFile() {
//
//		NotifyContextRequest response = new NotifyContextRequest();
//		String file = null;
//		try {
//
//			file = path + "/" + updateContextRequestsFile;
//			InputStream is = new FileInputStream(file);
//
//			JAXBContext context;
//			context = JAXBContext.newInstance(NotifyContextRequest.class);
//
//			// Create the marshaller, this is the nifty little thing that
//			// will actually transform the object into XML
//			Unmarshaller unmarshaller = context.createUnmarshaller();
//			response = (NotifyContextRequest) unmarshaller.unmarshal(is);
//
//			response.setOriginator(urlIoTAgent);
//			response.setSubscriptionId(id);
//
//		} catch (JAXBException e) {
//			logger.error("JAXB ERROR!", e);
//		} catch (FileNotFoundException e) {
//			logger.error("FILE NOT FOUND!: " + file);
//		}
//
//		String resp = response.toString();
//
//		return resp;
//	}
//
//	@POST
//	@Path("/subscribeContext")
//	@Produces("application/xml")
//	public SubscribeContextResponse subscriptionResponse(String body) {
//
//		logger.info("Received a NGSI-10 SubscribeContext");
//		if (logger.isDebugEnabled()) {
//			logger.debug("Received a NGSI-10 SubscribeContext: " + body);
//		}
//
//		final String reference;
//		Pattern pattern_reference = Pattern
//				.compile("<reference>(\\S+)</reference>");
//
//		Matcher matcher = pattern_reference
//				.matcher(body.replaceAll("\\s+", ""));
//
//		if (matcher.find()) {
//			reference = matcher.group(1);
//		} else {
//			return null;
//		}
//
//		UniqueIDGenerator idGenerator = new UniqueIDGenerator();
//		final String id = idGenerator.getNextUniqueId();
//		ScheduledExecutorService executorService = Executors
//				.newScheduledThreadPool(1);
//		executorService.scheduleWithFixedDelay(new Runnable() {
//			@Override
//			public void run() {
//
//				Connector conn = null;
//				try {
//					conn = new Connector(new URL(reference));
//
//				} catch (MalformedURLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				conn.start("", "POST", readNotifyFromFile(id.toString()));
//
//			}
//
//		}, 1, 2, TimeUnit.SECONDS);
//
//		SubscribeContextResponse response = new SubscribeContextResponse();
//
//		String file = null;
//		try {
//			file = path + "/" + subscribeContextResponseFile;
//			InputStream is = new FileInputStream(path + "/"
//					+ subscribeContextResponseFile);
//
//			JAXBContext context;
//			context = JAXBContext.newInstance(SubscribeContextResponse.class);
//
//			// Create the marshaller, this is the nifty little thing that
//			// will actually transform the object into XML
//			Unmarshaller unmarshaller = context.createUnmarshaller();
//			response = (SubscribeContextResponse) unmarshaller.unmarshal(is);
//
//			response.setSubscribeResponse(new SubscribeResponse(id.toString(),
//					response.getSubscribeResponse().getDuration(), null));
//
//			if (logger.isDebugEnabled()) {
//				logger.debug("NGSI-10 Subscribe response: " + response);
//			}
//
//		} catch (JAXBException e) {
//			logger.error("JAXB ERROR!", e);
//		} catch (FileNotFoundException e) {
//			logger.error("FILE NOT FOUND!: " + file);
//		}
//
//		return response;
//
//	}

}
