/*******************************************************************************
 * Copyright (c) 2015, NEC Europe Ltd.
 * All rights reserved.
 * 
 * Authors:
 *          * Salvatore Longo - salvatore.longo@neclab.eu
 *          * Tobias Jacobs - tobias.jacobs@neclab.eu
 *          * Flavio Cirillo - flavio.cirillo@neclab.eu
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
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Implements ContextElement as defined in OMA NGSI 9/10 approved version 1.0.
 */
@XmlRootElement(name = "contextElement")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContextElement_OrionCustomization extends NgsiStructure {

	@XmlElement(name = "id", required = true)
	private String id = null;

	@XmlAttribute(name = "type", required = false)
	@XmlSchemaType(name = "anyURI")
	private URI type = null;

	@XmlAttribute(name = "isPattern")
	private boolean isPattern = false;

	@XmlElementWrapper(name = "contextAttributeList")
	@XmlElement(name = "contextAttribute")
	@JsonProperty("attributes")
	private List<ContextAttribute_OrionCustomization> contextAttributeList;

	public ContextElement_OrionCustomization() {

	}

	public ContextElement_OrionCustomization(ContextElement contextElement) {

		this.id = contextElement.getEntityId().getId();
		this.type = contextElement.getEntityId().getType();
		this.isPattern = contextElement.getEntityId().getIsPattern();

		if (contextElement.getContextAttributeList() != null
				&& !contextElement.getContextAttributeList().isEmpty()) {

			contextAttributeList = new ArrayList<ContextAttribute_OrionCustomization>();

			for (ContextAttribute contextAttribute : contextElement
					.getContextAttributeList()) {
				contextAttributeList
						.add(new ContextAttribute_OrionCustomization(
								contextAttribute));
			}
		}

	}

	public ContextElement_OrionCustomization(
			EntityId entityId,
			List<ContextAttribute_OrionCustomization> contextAttribute_OrionCustomizationList) {

		this.id = entityId.getId();
		this.type = entityId.getType();
		this.isPattern = entityId.getIsPattern();

		this.contextAttributeList = contextAttribute_OrionCustomizationList;

	}

	public ContextElement_OrionCustomization(
			String id,
			URI type,
			boolean isPattern,
			List<ContextAttribute_OrionCustomization> contextAttribute_OrionCustomizationList) {

		this.id = id;
		this.type = type;
		this.isPattern = isPattern;
		this.contextAttributeList = contextAttribute_OrionCustomizationList;

	}

	@JsonIgnore
	public List<ContextAttribute_OrionCustomization> getContextAttributeList() {
		return contextAttributeList;

	}

	@JsonIgnore
	public void setContextAttributeList(
			List<ContextAttribute_OrionCustomization> attributeDomainName) {
		contextAttributeList = attributeDomainName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public URI getType() {
		return type;
	}

	public void setType(URI type) {
		this.type = type;
	}

	public boolean getIsPattern() {
		return isPattern;
	}

	public void setIsPattern(boolean isPattern) {
		this.isPattern = isPattern;
	}

	public ContextElement toContextElement() {
		List<ContextAttribute> contextAttributeList = null;

		if (this.contextAttributeList != null
				&& !this.contextAttributeList.isEmpty()) {
			contextAttributeList = new ArrayList<ContextAttribute>();

			for (ContextAttribute_OrionCustomization contextAttribute_OrionCustomization : this.contextAttributeList) {
				contextAttributeList.add(contextAttribute_OrionCustomization
						.toContextAttribute());
			}

		}

		return new ContextElement(new EntityId(id, type, isPattern), null,
				contextAttributeList, null);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((contextAttributeList == null) ? 0 : contextAttributeList
						.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isPattern ? 1231 : 1237);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContextElement_OrionCustomization other = (ContextElement_OrionCustomization) obj;
		if (contextAttributeList == null) {
			if (other.contextAttributeList != null)
				return false;
		} else if (!contextAttributeList.equals(other.contextAttributeList))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isPattern != other.isPattern)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

}
