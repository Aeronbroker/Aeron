package eu.neclab.iotplatform.entitycomposer.datamodel;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.neclab.iotplatform.iotbroker.commons.XmlFactory;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.NgsiStructure;
import eu.neclab.iotplatform.ngsi.api.datamodel.SourceInformation;
import eu.neclab.iotplatform.ngsi.api.serialization.json.MetadataObjectValueSerializer;
import eu.neclab.iotplatform.ngsi.api.serialization.json.MetadataValueDeserializer;

/**
 * This is an alternative de-serialization of contextMetadata xml, 
 * where the value is represented as a EntityAggregationInfo. This is a 
 * workaround for getting the AggregationInfo from registration metadata.
 */
@XmlRootElement(name = "contextMetadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class SourceInfoContextMetadata extends NgsiStructure {
	
	public static SourceInformation getValueAsSourceInfo(ContextMetadata cmd){
		
		String s = cmd.toString();
		
		SourceInfoContextMetadata svcmd =
				(SourceInfoContextMetadata)
				(new XmlFactory()).convertStringToXml(s, SourceInfoContextMetadata.class);
		
		return svcmd.getValue();
				
	}


	@XmlElement(name = "name")
	private String name = null;
	@XmlSchemaType(name = "anyURI")
	private URI type = null;
	
	@XmlElement(name = "value")
	//@XmlAnyElement(lax=true)
	@JsonSerialize(using = MetadataObjectValueSerializer.class)
	@JsonDeserialize(using = MetadataValueDeserializer.class)
	private SourceInformation value;

	public SourceInfoContextMetadata() {

	}

	public SourceInfoContextMetadata(String name, URI type, SourceInformation value) {

		this.name = name;
		this.type = type;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public URI getType() {
		return type;
	}

	public void setType(URI type) {
		this.type = type;
	}

	public SourceInformation getValue() {
		return value;
	}

	public void setValue(SourceInformation value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		SourceInfoContextMetadata other = (SourceInfoContextMetadata) obj;

		return name.equals(other.name) && type.equals(other.type)
				&& value.toString().equals(other.value.toString());

	}

	
	
}
