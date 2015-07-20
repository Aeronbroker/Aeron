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

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement(name = "queryContextRequest")
@XmlAccessorType(XmlAccessType.FIELD)

/**
 * Implements QueryContextRequest
 * as defined in OMA NGSI 9/10 approved version 1.0.
 */

public class QueryContextRequest extends NgsiStructure {

	@XmlElementWrapper(name = "entityIdList")
	@XmlElement(name = "entityId", required = true)
	@JsonProperty("entities")
	protected List<EntityId> entityId;

	@XmlElementWrapper(name = "attributeList")
	@XmlElement(name = "attribute", required = false)
	@JsonProperty("attributes")
	protected List<String> attribute;

	@XmlElement(name = "restriction")
	protected Restriction restriction = null;

	public QueryContextRequest() {

	}

	public QueryContextRequest(List<EntityId> enityId, List<String> attribute,
			Restriction rest) {
		entityId = enityId;
		this.attribute = attribute;
		restriction = rest;

	}
	@JsonIgnore
	public List<EntityId> getEntityIdList() {
		if (entityId == null) {
			entityId = new ArrayList<EntityId>();
		}
		return entityId;
	}
	@JsonIgnore
	public void setEntityIdList(List<EntityId> entityId) {
		this.entityId = entityId;

	}
	@JsonIgnore
	public List<String> getAttributeList() {
		if (attribute == null) {
			attribute = new ArrayList<String>();
		}
		return attribute;
	}
	@JsonIgnore
	public void setAttributeList(List<String> attributeList) {
		attribute = attributeList;
	}

	public Restriction getRestriction() {
		return restriction;
	}

	public void setRestriction(Restriction restriction) {
		this.restriction = restriction;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (attribute == null ? 0 : attribute.hashCode());
		result = prime * result
				+ (entityId == null ? 0 : entityId.hashCode());
		result = prime * result
				+ (restriction == null ? 0 : restriction.hashCode());
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
		QueryContextRequest other = (QueryContextRequest) obj;
		if (attribute == null) {
			if (other.attribute != null) {
				return false;
			}
		} else if (!attribute.equals(other.attribute)) {
			return false;
		}
		if (entityId == null) {
			if (other.entityId != null) {
				return false;
			}
		} else if (!entityId.equals(other.entityId)) {
			return false;
		}
		if (restriction == null) {
			if (other.restriction != null) {
				return false;
			}
		} else if (!restriction.equals(other.restriction)) {
			return false;
		}
		return true;
	}


}
