package entitycomposer;

import org.junit.Test;

import eu.neclab.iotplatform.entitycomposer.datamodel.AggregationSourceInformation;
import eu.neclab.iotplatform.entitycomposer.datamodel.EntityAggregationInfo;
import eu.neclab.iotplatform.entitycomposer.datamodel.SourceInfoContextMetadata;
import eu.neclab.iotplatform.iotbroker.commons.XmlFactory;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.SourceInformation;

public class AggregationInfoSerializationTest {

	@Test
	public void test() {
		

		/*
		 * serialize the discovery response from
		 * the xml file
		 */
		DiscoverContextAvailabilityResponse discoveryResp = (DiscoverContextAvailabilityResponse) 
				(new XmlFactory()).convertFileToXML("src/test/resources/aggregationInfoRegistration.xml", DiscoverContextAvailabilityResponse.class);
		
		System.out.println("Trying to serialize discovery response: \n\n"+ 
		discoveryResp.toString()
				);
		
		
		/*
		 * Try to cast the registration metadata value into source information
		 */
		ContextMetadata md 
		= discoveryResp.getContextRegistrationResponse().get(0).getContextRegistration().getListContextMetadata().get(0);
		
		SourceInformation sourceInfo = SourceInfoContextMetadata.getValueAsSourceInfo(md);
		
		System.out.println("Result of cast: \n\n"+ 
				sourceInfo.toString());
		
		/*
		 * Try to cast the source value
		 */
		
		System.out.println("Now casting source data into entity aggregation info");
		
		EntityAggregationInfo entityAggInfo = 
				AggregationSourceInformation.getValueAsAggrInfo(sourceInfo);
		
		System.out.println("Result:\n\n" + entityAggInfo.toString());
		
		return;
		
	}

}
