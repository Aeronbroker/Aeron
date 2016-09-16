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

package eu.neclab.iotplatform.iotbroker.core.junittests.tests;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import eu.neclab.iotplatform.iotbroker.commons.XmlFactory;
import eu.neclab.iotplatform.iotbroker.core.IotBrokerCore;
import eu.neclab.iotplatform.iotbroker.core.subscription.AgentWrapper;
import eu.neclab.iotplatform.iotbroker.core.subscription.ConfManWrapper;
import eu.neclab.iotplatform.iotbroker.core.subscription.NorthBoundWrapper;
import eu.neclab.iotplatform.iotbroker.core.subscription.SubscriptionController;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextResponse;
import eu.neclab.iotplatform.ngsi.api.ngsi10.Ngsi10Requester;
import eu.neclab.iotplatform.ngsi.api.ngsi9.Ngsi9Interface;

/**
 * Unit tests. Here a core setup (consisting of IoT Broker Core and Subscription Handler 
 * including the subscription wrapper classes) is tested. 
 */
public class TestFunctionality {

	
	//The components that are tested:
	IotBrokerCore core;
	SubscriptionController subscriptionController;
	AgentWrapper agentWrapper;
	ConfManWrapper confManWrapper;
	NorthBoundWrapper northBoundWrapper;
	
	//the mocks used for testing
	Ngsi9Interface ngsi9InterfaceMock;
	Ngsi10Requester ngsi10RequesterMock;
	//ResultFilterInterface resultFilterMock;	
	
	//initialize logger
	private static Logger logger = Logger.getLogger("Unit Tests");
	
	
	//read input and output messages from files
	DiscoverContextAvailabilityRequest discoverReq_attribExpr = (DiscoverContextAvailabilityRequest) 
			XmlFactory.convertFileToXML("src/test/resources/discoverReq_attribExpr.xml", DiscoverContextAvailabilityRequest.class);
	
	DiscoverContextAvailabilityRequest discoverReq_type = (DiscoverContextAvailabilityRequest) 
			XmlFactory.convertFileToXML("src/test/resources/discoverReq_type.xml", DiscoverContextAvailabilityRequest.class);
	
	DiscoverContextAvailabilityRequest discoverReq_pattern = (DiscoverContextAvailabilityRequest) 
			XmlFactory.convertFileToXML("src/test/resources/discoverReq_pattern.xml", DiscoverContextAvailabilityRequest.class);
	
	DiscoverContextAvailabilityRequest discoverReq_update = (DiscoverContextAvailabilityRequest) 
			XmlFactory.convertFileToXML("src/test/resources/discoverReq_update.xml", DiscoverContextAvailabilityRequest.class);

	DiscoverContextAvailabilityRequest discoverReq_attribDom = (DiscoverContextAvailabilityRequest) 
			XmlFactory.convertFileToXML("src/test/resources/discoverReq_attribDom.xml", DiscoverContextAvailabilityRequest.class);
	
	
	DiscoverContextAvailabilityResponse discoverResp = (DiscoverContextAvailabilityResponse)
			XmlFactory.convertFileToXML("src/test/resources/discoverResp.xml", DiscoverContextAvailabilityResponse.class);
	
	DiscoverContextAvailabilityResponse discoverResp_notFound = (DiscoverContextAvailabilityResponse)
			XmlFactory.convertFileToXML("src/test/resources/discoverResp_notFound.xml", DiscoverContextAvailabilityResponse.class);
	
	DiscoverContextAvailabilityResponse discoverResp_attribDom = (DiscoverContextAvailabilityResponse)
			XmlFactory.convertFileToXML("src/test/resources/discoverResp_attribDom.xml", DiscoverContextAvailabilityResponse.class);
	
	QueryContextRequest queryReq = (QueryContextRequest) 
			XmlFactory.convertFileToXML("src/test/resources/queryReq.xml", QueryContextRequest.class);
	
	QueryContextRequest queryReq_attribExpr = (QueryContextRequest) 
			XmlFactory.convertFileToXML("src/test/resources/queryReq_attribExpr.xml", QueryContextRequest.class);
	
	QueryContextRequest queryReq_type = (QueryContextRequest) 
			XmlFactory.convertFileToXML("src/test/resources/queryReq_type.xml", QueryContextRequest.class);
	
	QueryContextRequest queryReq_pattern = (QueryContextRequest) 
			XmlFactory.convertFileToXML("src/test/resources/queryReq_pattern.xml", QueryContextRequest.class);
	
	QueryContextRequest queryReq_attribDom = (QueryContextRequest) 
			XmlFactory.convertFileToXML("src/test/resources/queryReq_attribDom.xml", QueryContextRequest.class);
	
	QueryContextResponse queryResp = (QueryContextResponse)
			XmlFactory.convertFileToXML("src/test/resources/queryResp.xml", QueryContextResponse.class);
	
	QueryContextResponse queryResp_notFound =  (QueryContextResponse)
			XmlFactory.convertFileToXML("src/test/resources/queryResp_notFound.xml", QueryContextResponse.class);
	
	QueryContextResponse queryResp_attribDom =  (QueryContextResponse)
			XmlFactory.convertFileToXML("src/test/resources/queryResp_attribDom.xml", QueryContextResponse.class);
	
	UpdateContextRequest updateReq =  (UpdateContextRequest)
			XmlFactory.convertFileToXML("src/test/resources/updateReq.xml", UpdateContextRequest.class);
	
	UpdateContextResponse updateResp =  (UpdateContextResponse)
			XmlFactory.convertFileToXML("src/test/resources/updateResp.xml", UpdateContextResponse.class);
	
	@Before
	public void before(){
		
		//Set up a complete IoT Broker Core System, including Subscription Controller
		//with all wrappers.
		core = new IotBrokerCore();
		subscriptionController = new SubscriptionController();
		agentWrapper = new AgentWrapper(subscriptionController);
		confManWrapper = new ConfManWrapper(subscriptionController);
		northBoundWrapper = new NorthBoundWrapper(subscriptionController);		
		
		//connect the components
		core.setSubscriptionController(subscriptionController);
		core.setAgentWrapper(agentWrapper);
		core.setConfManWrapper(confManWrapper);
		core.setNorthBoundWrapper(northBoundWrapper);
		core.enableAssociations(true);
		
		subscriptionController.setAgentWrapper(agentWrapper);
		subscriptionController.setConfManWrapper(confManWrapper);
		subscriptionController.setNorthBoundWrapper(northBoundWrapper);
		
		//initialize the mocks
		ngsi9InterfaceMock = EasyMock.createMock(Ngsi9Interface.class);
		ngsi10RequesterMock = EasyMock.createMock(Ngsi10Requester.class);
		//resultFilterMock= EasyMock.createMock(ResultFilterInterface.class);
		
		//connect the components to the mocks
		core.setNgsi9Impl(ngsi9InterfaceMock);
		core.setNgsi10Requestor(ngsi10RequesterMock);
		//iotBrokerCore.setResultFilter(resultFilterMock);
		
		
		
		//further configuration of core
		ReflectionTestUtils.setField(core, "pubSubUrl", "http://192.168.100.1:70/application");
	}
	
	
	
	
	
	@Test
	public void restrictionTest(){

		logger.info("Now testing FIWARE.Feature.IoT.BackendIoTBroker.Query.Restriction");
		
		//configure mocks
		EasyMock.expect(ngsi9InterfaceMock.discoverContextAvailability(discoverReq_attribExpr))
			.andReturn(discoverResp);
		
		try {
			EasyMock.expect(ngsi10RequesterMock.queryContext(queryReq_attribExpr,new URI("http://192.168.100.1:70/application")))
			.andReturn(queryResp);
		} catch (URISyntaxException e) {			
			e.printStackTrace();
		}
		
		//arm the mocks
		EasyMock.replay(ngsi9InterfaceMock);
		EasyMock.replay(ngsi10RequesterMock);
		
		//execute the test
		QueryContextResponse brokerResp = core.queryContext(queryReq_attribExpr);
		
		
		//verify the communication of core
		EasyMock.verify(ngsi9InterfaceMock);
		EasyMock.verify(ngsi10RequesterMock);
		
		//manually remove the details from the broker response
		brokerResp.getErrorCode().setDetails(null);
		
		assertEquals(brokerResp,queryResp_notFound);
		
		logger.info("Successfully tested FIWARE.Feature.IoT.BackendIoTBroker.Query.Restriction");
	}
	
	@Test
	public void typeTest(){
		
		logger.info("Now testing FIWARE.Feature.IoT.BackendThingsManagement.Query.typeBased");
		
		//configure mocks
		EasyMock.expect(ngsi9InterfaceMock.discoverContextAvailability(discoverReq_type))
			.andReturn(discoverResp);
		
		try {
			EasyMock.expect(ngsi10RequesterMock.queryContext(queryReq,new URI("http://192.168.100.1:70/application")))
			.andReturn(queryResp);
		} catch (URISyntaxException e) {			
			e.printStackTrace();
		}
		
		//arm the mocks
		EasyMock.replay(ngsi9InterfaceMock);
		EasyMock.replay(ngsi10RequesterMock);
		
		//execute the test
		QueryContextResponse brokerResp = core.queryContext(queryReq_type);
		
		
		//verify the communication of core
		EasyMock.verify(ngsi9InterfaceMock);
		EasyMock.verify(ngsi10RequesterMock);
		
		assertEquals(brokerResp,queryResp);
		
		logger.info("Successfully tested FIWARE.Feature.IoT.BackendThingsManagement.Query.typeBased");
	}
	
	
	@Test
	public void patternTest(){
		
		logger.info("Now testing FIWARE.Feature.IoT.BackendIoTBroker.Query.Patterns");
		
		//configure mocks
		EasyMock.expect(ngsi9InterfaceMock.discoverContextAvailability(discoverReq_pattern))
			.andReturn(discoverResp);
			
		try {
			EasyMock.expect(ngsi10RequesterMock.queryContext(queryReq,new URI("http://192.168.100.1:70/application")))
			.andReturn(queryResp);
		} catch (URISyntaxException e) {			
			e.printStackTrace();
		}
		
		//arm the mocks
		EasyMock.replay(ngsi9InterfaceMock);
		EasyMock.replay(ngsi10RequesterMock);
		
		//execute the test
		QueryContextResponse brokerResp = core.queryContext(queryReq_pattern);
		
		
		//verify the communication of core
		EasyMock.verify(ngsi9InterfaceMock);
		EasyMock.verify(ngsi10RequesterMock);
		
		assertEquals(brokerResp,queryResp);
		
		logger.info("Successfully tested FIWARE.Feature.IoT.BackendIoTBroker.Query.Patterns");
	}
	
	
	@Test
	public void updateTest(){
		
		logger.info("Now testing FIWARE.Feature.IoT.BackendIoTBroker.Update.IdBased");
		
		//configure mocks
		EasyMock.expect(ngsi9InterfaceMock.discoverContextAvailability(discoverReq_update))
			.andReturn(discoverResp_notFound);
		
		try {
			EasyMock.expect(ngsi10RequesterMock.updateContext(updateReq,new URI("http://192.168.100.1:70/application")))
			.andReturn(updateResp);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		//arm the mocks
		EasyMock.replay(ngsi9InterfaceMock);
		EasyMock.replay(ngsi10RequesterMock);
		
		
		//execute the test
		UpdateContextResponse brokerResp = core.updateContext(updateReq);
		
		
		//verify the communication of core
		EasyMock.verify(ngsi9InterfaceMock);
		EasyMock.verify(ngsi10RequesterMock);
		
		assertEquals(updateResp,brokerResp);
		
		logger.info("Successfully tested FIWARE.Feature.IoT.BackendIoTBroker.Update.IdBased");
	}	
	
	
	@Test
	public void attributeDomainTest(){
		
		logger.info("Now testing FIWARE.Feature.IoT.BackendThingsManagement.Query.attributeDomain");
		
		//configure mocks
		EasyMock.expect(ngsi9InterfaceMock.discoverContextAvailability(discoverReq_attribDom))
			.andReturn(discoverResp_attribDom);
		
		try {
			EasyMock.expect(ngsi10RequesterMock.queryContext(queryReq_attribDom,new URI("http://192.168.100.1:70/application")))
			.andReturn(queryResp_attribDom);
		} catch (URISyntaxException e) {			
			e.printStackTrace();
		}
		
		//arm the mocks
		EasyMock.replay(ngsi9InterfaceMock);
		EasyMock.replay(ngsi10RequesterMock);
		
		//execute the test
		QueryContextResponse brokerResp = core.queryContext(queryReq_attribDom);
		
		
		//verify the communication of core
		EasyMock.verify(ngsi9InterfaceMock);
		EasyMock.verify(ngsi10RequesterMock);
		
		assertEquals(brokerResp,queryResp_attribDom);
		
		logger.info("Successfully tested FIWARE.Feature.IoT.BackendThingsManagement.Query.attributeDomain");
	}
	
	
	
}
