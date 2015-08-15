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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Implements ContextElement
 * as defined in OMA NGSI 9/10 approved version 1.0.
 */
@XmlRootElement(name = "contextElement")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContextElement extends NgsiStructure {

	@XmlElement(name = "entityId", required = true)
	private EntityId entityId;
	@XmlElement(name = "attributeDomainName")
	private String attributeDomainName;
	@XmlElementWrapper(name = "contextAttributeList")
	@XmlElement(name = "contextAttribute")
	@JsonProperty("attributes")
	private List<ContextAttribute> contextAttributeList;
	@XmlElementWrapper(name = "domainMetadata")
	@XmlElement(name = "contextMetadata")
	private List<ContextMetadata> domainMetadata;

	public ContextElement() {

	}

	public ContextElement(EntityId entityId, String attributeDomainName,
			List<ContextAttribute> contextAttributeList,
			List<ContextMetadata> domainMetadata) {

		this.entityId = entityId;
		this.attributeDomainName = attributeDomainName;
		this.contextAttributeList = contextAttributeList;
		this.domainMetadata = domainMetadata;

	}

	public EntityId getEntityId() {
		return entityId;
	}

	public void setEntityId(EntityId entityId) {
		this.entityId = entityId;
	}

	public List<ContextAttribute> getContextAttributeList() {
		return contextAttributeList;

	}

	public void setContextAttributeList(
			List<ContextAttribute> attributeDomainName) {
		contextAttributeList = attributeDomainName;
	}

	public String getAttributeDomainName() {
		return attributeDomainName;
	}

	public void setAttributeDomainName(String attributeDomainName) {
		this.attributeDomainName = attributeDomainName;
	}

	public List<ContextMetadata> getDomainMetadata() {
		if (domainMetadata == null) {
			domainMetadata = new ArrayList<ContextMetadata>();
		}
		return domainMetadata;

	}

	public void setDomainMetadata(List<ContextMetadata> domainMetadata) {
		this.domainMetadata = domainMetadata;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ (attributeDomainName == null ? 0 : attributeDomainName
						.hashCode());
		result = prime
				* result
				+ (contextAttributeList == null ? 0 : contextAttributeList
						.hashCode());
		result = prime * result
				+ (domainMetadata == null ? 0 : domainMetadata.hashCode());
		result = prime * result
				+ (entityId == null ? 0 : entityId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ContextElement other = (ContextElement) obj;
		if (attributeDomainName == null) {
			if (other.attributeDomainName != null) {
				return false;
			}
		} else if (!attributeDomainName.equals(other.attributeDomainName)) {
			return false;
		}
		if (contextAttributeList == null) {
			if (other.contextAttributeList != null) {
				return false;
			}
		} else if (!contextAttributeList.equals(other.contextAttributeList)) {
			return false;
		}
		if (domainMetadata == null) {
			if (other.domainMetadata != null) {
				return false;
			}
		} else if (!domainMetadata.equals(other.domainMetadata)) {
			return false;
		}
		if (entityId == null) {
			if (other.entityId != null) {
				return false;
			}
		} else if (!entityId.equals(other.entityId)) {
			return false;
		}
		return true;
	}

}
