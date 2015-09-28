package eu.neclab.iotplatform.entitycomposer.datamodel;

import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import eu.neclab.iotplatform.iotbroker.commons.XmlFactory;
import eu.neclab.iotplatform.ngsi.api.datamodel.NgsiStructure;
import eu.neclab.iotplatform.ngsi.api.datamodel.SourceInformation;

/**
 *  Specialized SourceInfo class where the source
 *  value is of type EntityAggregationInfo
 */
@XmlRootElement(name = "sourceInformation")
@XmlAccessorType(XmlAccessType.FIELD)
public class AggregationSourceInformation extends NgsiStructure{
	
	public static EntityAggregationInfo getValueAsAggrInfo(SourceInformation si){
		
		String s = si.toString();
		
		AggregationSourceInformation asi =
				(AggregationSourceInformation)
				(new XmlFactory()).convertStringToXml(s, AggregationSourceInformation.class);
		
		return asi.getSourceData();
		
	}

	
	@XmlSchemaType(name = "anyURI")
	private URI sourceType = null;
	
	@XmlElement(required=true)
	EntityAggregationInfo sourceData;
	
	public URI getSourceType() {
		return sourceType;
	}

	public void setSourceType(URI sourceType) {
		this.sourceType = sourceType;
	}

	public EntityAggregationInfo getSourceData() {
		return sourceData;
	}

	public void setSourceData(EntityAggregationInfo sourceData) {
		this.sourceData = sourceData;
	}


	
}
