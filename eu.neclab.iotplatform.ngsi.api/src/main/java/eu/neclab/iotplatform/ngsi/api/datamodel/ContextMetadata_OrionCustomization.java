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

package eu.neclab.iotplatform.ngsi.api.datamodel;

import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

/**
 * Implements ContextMetadata as defined in OMA NGSI 9/10 approved version 1.0.
 */
@XmlRootElement(name = "contextMetadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContextMetadata_OrionCustomization extends
		NgsiStructureAlternative {

	@XmlElement(name = "name")
	private String name = null;

	@XmlSchemaType(name = "anyURI")
	private URI type = null;

	@XmlElement(name = "value")
	private String value = null;

	public ContextMetadata_OrionCustomization() {

	}

	public ContextMetadata_OrionCustomization(String name, URI type,
			String value) {

		this.name = name;
		this.type = type;
		this.value = value;
	}

	public ContextMetadata_OrionCustomization(ContextMetadata contextMetadata) {
		
		this.name = contextMetadata.getName();
		this.type = contextMetadata.getType();
		if (contextMetadata.getValue() instanceof String){
			this.value = (String) contextMetadata.getValue();
		} else {
			String string = contextMetadata.getValue().toString();
			string = string.replace("\n", "").replace("\r", "");
			string = string.replaceAll("<\\?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"\\?>", "");
			string = string.replaceAll("</.*?>", "|");
			string = string.replaceAll("<", "");
			string = string.replaceAll(">", "|");
			this.value = string;
		}

	}

	public ContextMetadata_OrionCustomization(MetadataTypes metadataType,
			URI type, String value) {

		this.name = metadataType.toString();
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
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

		ContextMetadata_OrionCustomization other = (ContextMetadata_OrionCustomization) obj;

		return name.equals(other.name) && type.equals(other.type)
				&& value.toString().equals(other.value.toString());

	}

	@Override
	public int hashCode() {
		return this.toJsonString().hashCode();
	}

	public ContextMetadata toContextMetadata() {
		return new ContextMetadata(name, type, value);
	}

	@Override
	public NgsiStructure toStandardNgsiStructure() {
		return toContextMetadata();
	}

}
