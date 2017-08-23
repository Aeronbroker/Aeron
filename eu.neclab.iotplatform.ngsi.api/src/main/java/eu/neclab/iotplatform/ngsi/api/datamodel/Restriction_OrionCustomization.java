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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Implements Restriction as defined in OMA NGSI 9/10 approved version 1.0.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Restriction_OrionCustomization extends NgsiStructureAlternative {

	@XmlElement(name = "attributeExpression")
	private String attributeExpression = null;

	@XmlElementWrapper(name = "scope")
	@XmlElement(name = "operationScope")
	@JsonProperty("scopes")
	private List<OperationScope_OrionCustomization> operationScope = null;

	public Restriction_OrionCustomization() {

	}

	@JsonIgnore
	public Restriction_OrionCustomization(String attributeExpression,
			List<OperationScope_OrionCustomization> scope) {
		this.attributeExpression = attributeExpression;
		this.operationScope = scope;

	}

	public String getAttributeExpression() {
		return attributeExpression;
	}

	public void setAttributeExpression(String attributeExpression) {
		this.attributeExpression = attributeExpression;
	}

	@JsonIgnore
	public List<OperationScope_OrionCustomization> getOperationScope() {
		if (operationScope == null) {
			operationScope = new ArrayList<OperationScope_OrionCustomization>();
		}
		return operationScope;
	}

	@JsonIgnore
	public void setOperationScope(List<OperationScope_OrionCustomization> scope) {
		operationScope = scope;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ (attributeExpression == null ? 0 : attributeExpression
						.hashCode());
		result = prime * result
				+ (operationScope == null ? 0 : operationScope.hashCode());
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
		Restriction_OrionCustomization other = (Restriction_OrionCustomization) obj;
		if (attributeExpression == null) {
			if (other.attributeExpression != null) {
				return false;
			}
		} else if (!attributeExpression.equals(other.attributeExpression)) {
			return false;
		}
		if (operationScope == null) {
			if (other.operationScope != null) {
				return false;
			}
		} else if (!operationScope.equals(other.operationScope)) {
			return false;
		}
		return true;
	}

	@Override
	public NgsiStructure toStandardNgsiStructure() {
		return toRestriction();
	}

	public Restriction toRestriction() {
		Restriction restriction = new Restriction();
		if (operationScope != null && !operationScope.isEmpty()) {
			restriction.setOperationScope(new ArrayList<OperationScope>());
			for (OperationScope_OrionCustomization scope : operationScope) {
				restriction.getOperationScope().add(scope.toOperationScope());
			}
		}

		if (attributeExpression != null) {
			restriction.setAttributeExpression(attributeExpression);
		} else {
			restriction.setAttributeExpression("");
		}

		return restriction;
	}
}
