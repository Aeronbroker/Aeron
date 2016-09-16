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
