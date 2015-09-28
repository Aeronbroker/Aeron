package entitycomposer;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import eu.neclab.iotplatform.entitycomposer.CompositeEntityQueryService;
import eu.neclab.iotplatform.iotbroker.commons.XmlFactory;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextResponse;
import eu.neclab.iotplatform.ngsi.api.ngsi10.Ngsi10Interface;
import eu.neclab.iotplatform.ngsi.api.ngsi9.Ngsi9Interface;

/**
 * This tests the query service for entity aggregation info. 
 * It mocks the IoT Broker core to simulate that some meaningful
 * result is returned when it is queried by the query service. 
 */
public class EntityAggregationQueryServiceTest {

	/*
	 * The class tested.
	 */
	CompositeEntityQueryService queryService = new CompositeEntityQueryService();
	
	/*
	 * The mock of the broker core
	 */
	Ngsi10Interface coreMock;
	
	/*
	 * event logger
	 */
	private static Logger logger = Logger.getLogger("Unit Tests");
	
	@Before
	public void before(){
		
		//initialize the mock
		coreMock = EasyMock.createMock(Ngsi10Interface.class);
		
		//connect service to mock
		queryService.setIotBrokerCore(coreMock);
		
	}
	
	
	@Test
	public void simpleAVGTest() {
		
		/*
		 * initialize test requests and responses
		 */
		
		DiscoverContextAvailabilityResponse discResp =
				(DiscoverContextAvailabilityResponse)
				(new XmlFactory()).convertFileToXML
				("src/test/resources/aggregationInfoRegistration.xml", 
						DiscoverContextAvailabilityResponse.class);	
		
		List<ContextRegistration> regList = new ArrayList<>();
		regList.add(discResp.getContextRegistrationResponse()
				.get(0).getContextRegistration());

		
		QueryContextRequest targetQueryRequest = 
				(QueryContextRequest)
				(new XmlFactory()).convertFileToXML
				("src/test/resources/targetQueryRequest.xml", 
						QueryContextRequest.class);
		
		QueryContextResponse expectedResp = 
				(QueryContextResponse)
				(new XmlFactory()).convertFileToXML
				("src/test/resources/targetQueryResponse.xml", 
						QueryContextResponse.class);
		
		
		QueryContextRequest sourceQueryRequest = 
				(QueryContextRequest)
				(new XmlFactory()).convertFileToXML
				("src/test/resources/sourceQueryRequest.xml", 
						QueryContextRequest.class);
		
		QueryContextResponse sourceResponse1 = 
				(QueryContextResponse)
				(new XmlFactory()).convertFileToXML
				("src/test/resources/sourceQueryResponse.xml", 
						QueryContextResponse.class);
		
		
		
		//configure core mock
		EasyMock.expect(coreMock.queryContext(sourceQueryRequest)).andReturn(sourceResponse1);
		
		//arm mock
		EasyMock.replay(coreMock);
		
		//execute the test
		QueryContextResponse testResp = queryService.queryContext(targetQueryRequest, regList);
		
		/*
		 * verify results
		 */
		EasyMock.verify(coreMock);
		
		assertEquals(expectedResp,testResp);
		
	}

}
