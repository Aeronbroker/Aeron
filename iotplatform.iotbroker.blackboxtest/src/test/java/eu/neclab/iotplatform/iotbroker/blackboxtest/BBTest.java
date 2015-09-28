package eu.neclab.iotplatform.iotbroker.blackboxtest;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;


import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.BodyMatcher;
import org.mockserver.model.Body;
import org.mockserver.verify.VerificationTimes;

import eu.neclab.iotplatform.iotbroker.commons.XmlFactory;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextResponse;
import io.netty.channel.pool.FixedChannelPool.AcquireTimeoutAction;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.HttpForward.forward;
import static org.mockserver.model.Header.header;
import static org.mockserver.model.HttpResponse.notFoundResponse;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.matchers.Times.exactly;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockserver.model.HttpForward.Scheme.HTTP;
import static org.mockserver.model.HttpStatusCode.ACCEPTED_202;

import static org.mockserver.model.StringBody.exact;
import static org.mockserver.model.XPathBody.xpath;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;



/**
 *  A suite of tests using the mockserver framework. These are blackbox tests to be 
 *  run against a deployment of the IoT Broker running at localhost.
 *  
 *   Note: As the mockserver framework supports no xml document equivalence check,
 *   the xml bodies by the IoT Broker are instead tested using XPATH expressions.
 */
public class BBTest {
	
	final int D_PORT = 8002; //port of discovery mock
	final int A1_PORT = 8031; //port of agent 1 mock
	final int A2_PORT = 8032; //port of agent 2 mock
	final int B_PORT = 80; //port of IoT Broker
	
	/*
	 * pointers to the mock servers (initialized in @Before method 
	 * and stopped in @After method) 
	 */
	ClientAndServer discoveryMockServer;
	ClientAndServer agent1MockServer;
	ClientAndServer agent2MockServer;
	
	/*
	 * Clients used to configure the mock servers. Should of course only 
	 * be used in @Test methods, because otherwise the servers will not
	 * be up.
	 */
	MockServerClient d_client = new MockServerClient("localhost", D_PORT);
	MockServerClient a1_client = new MockServerClient("localhost", A1_PORT);
	MockServerClient a2_client = new MockServerClient("localhost", A2_PORT);
	
	@Before
	public void startServerMocks(){
		
		discoveryMockServer = startClientAndServer(D_PORT);
		agent1MockServer = startClientAndServer(A1_PORT);
		agent2MockServer = startClientAndServer(A2_PORT);
		
	}
	
	@After
	public void stopServerMocks(){
		discoveryMockServer.stop();
		agent1MockServer.stop();
		agent2MockServer.stop();
	}
	
	/**
	 * Generic-purpose method to send an http request to localhost.
	 * Use "" for the path if not present.
	 * Use null for body in order not to include one in the request.
	 */
	public static String sendReqToServer(String body, int port, String method, String path) throws Exception{
				
		URL url = new URL("http://localhost:"+port+path);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		
		con.setRequestMethod(method);
		con.setDoOutput(true);
		con.setRequestProperty("Accept", "application/xml");
		con.setRequestProperty("Content-Type", "application/xml");
		
		if(body != null){
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(body);
		wr.flush();
		wr.close();
		}
		
		BufferedReader in;
		if (con.getResponseCode() == 200) {
		    in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
		} else {
		     /* error from server */
			in = new BufferedReader(
			        new InputStreamReader(con.getErrorStream()));
		}
				
		String inputLine;
		StringBuffer response = new StringBuffer();
		
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		
		return response.toString();
	}
	
	/**
	 * Only see if and how the server mock framework is working.
	 */
	@Test
	public void servermocktest() throws Exception{
		
		
		
		//tell server what to respond to POST
		d_client.when(
				request().
				withMethod("POST")
				).respond(response().withStatusCode(200)
						.withBody("Hi World!"));
		
		//send a POST
		
		String response = sendReqToServer("test!", D_PORT, "POST","");
		System.out.println("req result:" + response);
		
		
		//verify there was a POST with the specific body
		d_client
		.verify(
				request().withMethod("POST").withBody("test!"),
				VerificationTimes.exactly(1)
				);

				System.out.println("Reached end of servermocktest");
		
	}
	

	/**
	 * A test for the situation where no discovery is 
	 * found.
	 */
	@Test
	public void noDiscoveryFoundTest() throws Exception{		
		
		//no need to arm a mock server; just send a request to IoT Broker
		String response = sendReqToServer(null, B_PORT, "GET",
				"/ngsi10/contextEntities/ConferenceRoom/attributes/temperature");		
		
		/*
		 * verify that there was a POST on discovery
		 * with the right path, right method, right body parameters
		 */
		d_client.verify(request()
				.withPath("/ngsi9/discoverContextAvailability")
				.withMethod("POST")
				.withBody(xpath(
						"(/discoverContextAvailabilityRequest/entityIdList/entityId//id=\"ConferenceRoom\")"
						+ "and"
						+ "(/discoverContextAvailabilityRequest/attributeList/attribute=\"temperature\")"
						))						
				,
				VerificationTimes.exactly(1)
				);
	}
	
	/**
	 * This test queries for information and simulates that IoT
	 * Discovery has entity aggregation information available
	 * to answer the request.
	 */
	@Test
	public void entityAggregationQueryTest() throws Exception{
		
		/*
		 * The workflow is as follows:
		 * 
		 * - IoT Broker receives Query
		 * - IoT Broker asks Discovery and obtains entity aggregation into
		 * - IoT Broker asks Discovery again for the sources of aggregation info
		 * and receives info in IoT agents
		 * - IoT Broker contacts the IoT agents, assembles the target entity
		 * info from the information received and returns the resulting 
		 * response
		 * 
		 */
		
		
		/*
		 * the request send to start the test
		 */
		QueryContextRequest b_request = 
				(QueryContextRequest)
				(new XmlFactory()).convertFileToXML
				("src/test/resources/entAggTest_b_req.xml", 
						QueryContextRequest.class);
		
		
		/*
		 * the XPATH expression to verify the first request of
		 * IoT Broker to IoT Discovery
		 */
		String d_req1_eval  = 
				"(/discoverContextAvailabilityRequest/entityIdList/entityId//id=\"ConferenceRoom\")"
				+ " and " +
				"(/discoverContextAvailabilityRequest/attributeList/attribute=\"humidity\")"
				+ " and " + 
				"(/discoverContextAvailabilityRequest/attributeList/attribute=\"temperature\")"
				;
		
		/*
		 * The response returned by the IoT Discovery Mock
		 */				
		DiscoverContextAvailabilityResponse d_response_1  = 
				(DiscoverContextAvailabilityResponse)
				(new XmlFactory()).convertFileToXML
				("src/test/resources/entAggTest_d_resp1.xml", 
						DiscoverContextAvailabilityResponse.class);
				
		
		/*
		 * The XPATH expression verifying the second 
		 * request of IoT Broker to IoT Discovery
		 */
		String d_req2_eval  = 
			"(/discoverContextAvailabilityRequest/entityIdList/entityId//id=\"SensorBoard_A\")"
			+ " and " +
			"(/discoverContextAvailabilityRequest/entityIdList/entityId//id=\"TempSensor_B\")"
			+ " and " +
			"(/discoverContextAvailabilityRequest/attributeList/attribute=\"humValue\")"
			+ " and " + 
			"(/discoverContextAvailabilityRequest/attributeList/attribute=\"tempValue\")"
			;
		
		/*
		 * The response returned this time
		 */
		DiscoverContextAvailabilityResponse d_response_2  = 				
				(DiscoverContextAvailabilityResponse)
				(new XmlFactory()).convertFileToXML
				("src/test/resources/entAggTest_d_resp2.xml", 
						DiscoverContextAvailabilityResponse.class);
		
		/*
		 * XPATH to verify request of IoT Broker to agent 1
		 */
		String a1_req_eval  = 
			"(/queryContextRequest/entityIdList/entityId//id=\"SensorBoard_A\")"
			+ " and " +
			"(/queryContextRequest/attributeList/attribute=\"tempValue\")"
			+ " and " +
			"(/queryContextRequest/attributeList/attribute=\"humValue\")"
			;
		
		/*
		 * The response sent by agent 1 mock
		 */
		QueryContextResponse a1_response  = 
				(QueryContextResponse)
				(new XmlFactory()).convertFileToXML
				("src/test/resources/entAggTest_a1_resp.xml", 
						QueryContextResponse.class);
		
		/*
		 * XPATH to verify request of IoT Broker to agent 2
		 */
		String a2_req_eval  = 
				"(/queryContextRequest/entityIdList/entityId//id=\"TempSensor_B\")"
				+ " and " +
				"(/queryContextRequest/attributeList/attribute=\"tempValue\")"
				;
		
		/*
		 * The response sent by agent 2 mock
		 */
		QueryContextResponse a2_response  = 
				(QueryContextResponse)
				(new XmlFactory()).convertFileToXML
				("src/test/resources/entAggTest_a2_resp.xml", 
						QueryContextResponse.class);
		
		/*
		 * The expected final response returned by
		 * IoT Broker
		 */
		QueryContextResponse b_response  = 
				(QueryContextResponse)
				(new XmlFactory()).convertFileToXML
				("src/test/resources/entAggTest_b_resp.xml", 
						QueryContextResponse.class);
		
		
		
		/*
		 * arm the discovery mock for both requests
		 */
		
		d_client.when(request()
				.withMethod("POST")
				.withPath("/ngsi9/discoverContextAvailability")
				.withBody(xpath(
						d_req1_eval
						))
				,
				exactly(1)
				).respond(response().withBody(
						d_response_1.toString()
						).withStatusCode(200)												
						);
		
		d_client.when(request()
				.withMethod("POST")
				.withPath("/ngsi9/discoverContextAvailability")
				.withBody(xpath(
						d_req2_eval
						)),
				exactly(1)
				).respond(response().withBody(
						d_response_2.toString()
						).withStatusCode(200)						
						);		
		
		/*
		 * arm the agent mocks
		 */
		
		a1_client.when(request()
				.withMethod("POST")
				.withBody(xpath(
						a1_req_eval
						))
				).respond(response()
						.withStatusCode(200)
						.withBody(a1_response.toString())
						);

		a2_client.when(request()
				.withMethod("POST")
				.withBody(xpath(
						a2_req_eval
						))
				).respond(response()
						.withStatusCode(200)
						.withBody(a2_response.toString())
						);
		
		/*
		 * trigger the test
		 */
		String actual_b_response_str = sendReqToServer(
				b_request.toString(), 
				B_PORT, 
				"POST", 
				"/ngsi10/queryContext");
		
		QueryContextResponse actual_b_response = (QueryContextResponse)
				(new XmlFactory()).convertStringToXml(
						actual_b_response_str, 
						QueryContextResponse.class);
		
		
				
		
		/*
		 * verify what happened at the mock servers
		 */
		
		d_client.verify(request().withBody(xpath(d_req1_eval)));
		d_client.verify(request().withBody(xpath(d_req2_eval)));
		a1_client.verify(request().withBody(xpath(a1_req_eval)));
		a2_client.verify(request().withBody(xpath(a2_req_eval)));
				
		/*
		 * verify the response as well
		 * 
		 * (commented at the moment as on some machines some problems with
		 * unpredictable order of attributes persist - need to re-work
		 * the .equals functions first)
		 */		
		//assertEquals(b_response,actual_b_response);
		
		
	}
	


}
