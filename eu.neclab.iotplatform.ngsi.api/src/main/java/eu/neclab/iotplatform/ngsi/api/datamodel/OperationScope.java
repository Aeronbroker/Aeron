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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import eu.neclab.iotplatform.ngsi.api.serialization.json.MetadataObjectValueSerializer;
import eu.neclab.iotplatform.ngsi.api.serialization.json.MetadataValueDeserializer;

/**
 * Implements OperationScope
 * as defined in OMA NGSI 9/10 approved version 1.0.
 */
@XmlRootElement(name = "operationScope")
@XmlSeeAlso({Segment.class,Circle.class,Polygon.class,Point.class, PEPCredentials.class})
@XmlAccessorType(XmlAccessType.FIELD)
public class OperationScope extends NgsiStructure {

	@XmlElement(name = "scopeType", required = true)
	private String scopeType = null;

	@XmlElement(name = "scopeValue", required = true)
	@JsonSerialize(using = MetadataObjectValueSerializer.class)
	@JsonDeserialize(using = MetadataValueDeserializer.class)
	private Object scopeValue = null;
	
	public OperationScope() {

	}
	@JsonIgnore
	public OperationScope(String scopeType, Object scopeValue) {
		this.scopeType = scopeType;
		this.scopeValue = scopeValue;
	}
	
	@JsonIgnore
	public OperationScope(ScopeTypes scopeType, Object scopeValue) {
		this.scopeType = scopeType.toString();
		this.scopeValue = scopeValue;
	}

	public String getScopeType() {
		return scopeType;
	}

	public void setScopeType(String scopeType) {
		this.scopeType = scopeType;
	}

	public Object getScopeValue() {
		return scopeValue;
	}

	public void setScopeValue(Object scopeValue) {
		this.scopeValue = scopeValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (scopeType == null ? 0 : scopeType.hashCode());
		result = prime * result
				+ (scopeValue == null ? 0 : scopeValue.hashCode());
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
		OperationScope other = (OperationScope) obj;
		if (scopeType == null) {
			if (other.scopeType != null) {
				return false;
			}
		} else if (!scopeType.equals(other.scopeType)) {
			return false;
		}
		if (scopeValue == null) {
			if (other.scopeValue != null) {
				return false;
			}
		}

		else if (!scopeValue.equals(other.scopeValue)) {
			return false;
		}
		return true;
	}


}
