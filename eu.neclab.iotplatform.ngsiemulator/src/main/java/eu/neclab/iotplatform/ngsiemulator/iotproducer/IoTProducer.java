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
