package eu.neclab.iotplatform.iotbroker.core.junittests.tests;

import static org.junit.Assert.*;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.swing.text.html.parser.Entity;

import org.easymock.EasyMock;
import org.easymock.IExpectationSetters;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import eu.neclab.iotplatform.iotbroker.commons.XmlFactory;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.ResultFilterInterface;
import eu.neclab.iotplatform.iotbroker.core.IotBrokerCore;

import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElementResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextResponse;
import eu.neclab.iotplatform.ngsi.api.ngsi10.Ngsi10Requester;
import eu.neclab.iotplatform.ngsi.api.ngsi9.Ngsi9Interface;

public class TestFunctionality {

	SupportingFunctions util = new SupportingFunctions();
	XmlFactory xmlFactory = new XmlFactory();
	IotBrokerCore iotBrokerCore;
	Ngsi9Interface ngsi9Interface;
	Ngsi10Requester ngsi10Requestor;
	ResultFilterInterface resultFilterInterface;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		ngsi9Interface = EasyMock.createStrictMock(Ngsi9Interface.class);
		ngsi10Requestor = EasyMock.createStrictMock(Ngsi10Requester.class);
		resultFilterInterface= EasyMock.createMock(ResultFilterInterface.class);
		iotBrokerCore = new IotBrokerCore();
		iotBrokerCore.setNgsi9Impl(ngsi9Interface);
		iotBrokerCore.setNgsi10Requestor(ngsi10Requestor);
		ReflectionTestUtils.setField(iotBrokerCore, "pubSubUrl", "http://127.0.0.1");

	}

	@After
	public void tearDown() throws Exception {
	}

	
	/*
	 * Basic query context request junit test 
	 */
	@Test
	public void testQueryContext() {

		QueryContextRequest queryContextRequest = util.prepareQueryContextRequest("/queryContextRequest.xml");
		QueryContextResponse queryContextResponse = util.prepareQueryContextResponse("/queryContextResponse.xml");
		
		
		
		DiscoverContextAvailabilityRequest dca= util.prepareDiscoverContextAvailabilityRequest("/discoverContextAvailabilityRequest.xml");
		EasyMock.expect(
						ngsi9Interface
								.discoverContextAvailability(dca))
						.andReturn(
								util.prepareDiscoverContextAvailabilityResponse("/discoverContextAvailabilityResponse.xml"));
		EasyMock.replay(ngsi9Interface);
		EasyMock.expect(
				ngsi10Requestor.queryContext(
						Matchers.QueryContextRequestMatcher(util.prepareQueryContextRequest("/queryContextRequest.xml")) ,
						(URI) EasyMock.anyObject())).andReturn(
								util.prepareQueryContextResponse("/queryContextResponse.xml"));
		EasyMock.replay(ngsi10Requestor);
		
	
		QueryContextResponse qcRes = iotBrokerCore
				.queryContext(queryContextRequest);
		
		EasyMock.verify(ngsi9Interface);
		EasyMock.verify(ngsi10Requestor);
		
		assertEquals(qcRes.toString(), queryContextResponse.toString());
		
	}
	@Test
	public void testQueryContextChkRestriction() {
		QueryContextRequest queryContextRequest = util.prepareQueryContextRequest("/queryContextRequestWithOutRestriction.xml");
		QueryContextResponse queryContextResponse = util.prepareQueryContextResponse("/queryContextResponseWithOutRestriction.xml");
	
		EasyMock.expect(
				ngsi9Interface
						.discoverContextAvailability((DiscoverContextAvailabilityRequest) EasyMock
								.anyObject()))
				.andReturn(
						util.prepareDiscoverContextAvailabilityResponse("/discoverContextAvailabilityResponse.xml"));
		
		EasyMock.replay(ngsi9Interface);
		EasyMock.expect(
				ngsi10Requestor.queryContext(
						(QueryContextRequest) EasyMock.anyObject(),
						(URI) EasyMock.anyObject())).andReturn(
								util.prepareQueryContextResponse("/queryContextResponse.xml"));
		EasyMock.replay(ngsi10Requestor);
			
	
		QueryContextResponse qcRes = iotBrokerCore
				.queryContext(queryContextRequest);
		
		EasyMock.verify(ngsi9Interface);
		EasyMock.verify(ngsi10Requestor);
		
		assertEquals(qcRes.toString(), queryContextResponse.toString());
	}
	
	@Test
	public void testQueryContextChkOperationScope() {
		QueryContextRequest queryContextRequest = util.prepareQueryContextRequest("/queryContextRequestOpetaionScope.xml");
		QueryContextResponse queryContextResponse = util.prepareQueryContextResponse("/queryContextResponseWithOutRestriction.xml");
	
		DiscoverContextAvailabilityRequest dca= util.prepareDiscoverContextAvailabilityRequest("/discoverContextAvailabilityRequestOperationScope.xml");
		EasyMock.expect(
						ngsi9Interface
								.discoverContextAvailability(Matchers.DiscoverContextAvailabilityMatching(dca)))
						.andReturn(
								util.prepareDiscoverContextAvailabilityResponse("/discoverContextAvailabilityResponse.xml"));
		EasyMock.replay(ngsi9Interface);
		
		EasyMock.expect(
				ngsi10Requestor.queryContext(
						Matchers.QueryContextRequestMatcher(util.prepareQueryContextRequest("/queryContextRequestOpetaionScope.xml")) ,
						(URI) EasyMock.anyObject())).andReturn(
								util.prepareQueryContextResponse("/queryContextResponseWithOutRestriction.xml"));
		EasyMock.replay(ngsi10Requestor);
			
	
		QueryContextResponse qcRes = iotBrokerCore
				.queryContext(queryContextRequest);
		
		EasyMock.verify(ngsi9Interface);
		EasyMock.verify(ngsi10Requestor);
		
		assertEquals(qcRes.toString(), queryContextResponse.toString());
	}
	@Test
	public void testUpdateContext() {
		
		
		UpdateContextRequest updateContextRequest= util.prepareUpdateContextRequest("/updateContextRequest.xml");
		UpdateContextResponse updateContextResponseExpected= util.prepareUpdateContextResponse("/updateContextResponse.xml");
		
		DiscoverContextAvailabilityRequest dca= util.prepareDiscoverContextAvailabilityRequest("/discoverContextAvailabilityRequestUpdate.xml");
		EasyMock.expect(
						ngsi9Interface
								.discoverContextAvailability(Matchers.DiscoverContextAvailabilityMatching(dca)))
						.andReturn(
								util.prepareDiscoverContextAvailabilityResponse("/discoverContextAvailabilityResponseUpdate.xml"));
		EasyMock.replay(ngsi9Interface);
		
		
		EasyMock.expect(
				ngsi10Requestor.updateContext(
						Matchers.UpdateContextRequestMatcher(util.prepareUpdateContextRequest("/updateContextRequest.xml")) ,
						(URI) EasyMock.anyObject())).andReturn(
								util.prepareUpdateContextResponse("/updateContextResponse.xml"));
		EasyMock.replay(ngsi10Requestor);
		
		UpdateContextResponse updateContextResponseActual = iotBrokerCore.updateContext(updateContextRequest);
		System.out.println(updateContextResponseActual.toString());
		EasyMock.verify(ngsi9Interface);
		EasyMock.verify(ngsi10Requestor);
		
		assertEquals( updateContextResponseExpected.toString(),updateContextResponseActual.toString());
	}

}
