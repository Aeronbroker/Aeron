package eu.neclab.iotplatform.ngsiemulator.iotproducer;

import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import eu.neclab.iotplatform.ngsiemulator.utils.Mode;

@Path("ngsi10")
public class IoTProducer {

	// The logger
	private static Logger logger = Logger.getLogger(IoTProducer.class);

	private static String path = System.getProperty(
			"eu.neclab.ioplatform.ngsiemulator.iotproducer.path", "xml/");

	private static Mode mode = Mode.fromString(
			System.getProperty("eu.neclab.ioplatform.ngsiemulator.iotproducer.mode"),
			Mode.RANDOM);

	private static int period = Integer
			.parseInt(
					System.getProperty("eu.neclab.ioplatform.ngsiemulator.iotproducer.period"),
					1000);

	private static String reference = System.getProperty(
			"eu.neclab.ioplatform.ngsiemulator.iotproducer.reference",
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
