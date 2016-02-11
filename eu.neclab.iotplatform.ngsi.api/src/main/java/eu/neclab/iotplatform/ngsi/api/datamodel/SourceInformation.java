package eu.neclab.iotplatform.ngsi.api.datamodel;

import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

/**
 * This class represents information about how to 
 * obtain NGSI Entity information.
 */
@XmlRootElement(name = "sourceInformation")
@XmlAccessorType(XmlAccessType.FIELD)
public class SourceInformation extends NgsiStructure{

	
	@XmlSchemaType(name = "anyURI")
	private URI sourceType = null;
	
	@XmlElement(required=true)
	Object sourceData;
	
	public URI getSourceType() {
		return sourceType;
	}

	public void setSourceType(URI sourceType) {
		this.sourceType = sourceType;
	}

	public Object getSourceData() {
		return sourceData;
	}

	public void setSourceData(String sourceData) {
		this.sourceData = sourceData;
	}


	
}
