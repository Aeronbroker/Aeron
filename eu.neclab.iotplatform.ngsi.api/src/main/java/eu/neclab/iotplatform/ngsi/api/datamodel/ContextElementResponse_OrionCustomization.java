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

/**
 * Implements ContextElementResponse as defined in OMA NGSI 9/10 approved
 * version 1.0.
 */
@XmlRootElement(name = "contextElementResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContextElementResponse_OrionCustomization extends
		NgsiStructureAlternative {

	@XmlElement(name = "contextElement", required = true)
	private ContextElement_OrionCustomization contextElement = null;

	@XmlElement(name = "statusCode", required = true)
	private StatusCode statusCode = null;

	public ContextElementResponse_OrionCustomization() {

	}

	public ContextElementResponse_OrionCustomization(
			ContextElement_OrionCustomization contextElement,
			StatusCode statusCode) {
		this.contextElement = contextElement;
		this.statusCode = statusCode;

	}

	public ContextElement_OrionCustomization getContextElement() {
		return contextElement;
	}

	public void setContextElement(
			ContextElement_OrionCustomization contextElement) {
		this.contextElement = contextElement;
	}

	public StatusCode getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(StatusCode statusCode) {
		this.statusCode = statusCode;
	}

	public ContextElementResponse toContextElementResponse() {

		return new ContextElementResponse(contextElement.toContextElement(),
				statusCode);

	}

	@Override
	public boolean sanityCheck() {
		if (contextElement == null || !contextElement.sanityCheck()
				|| statusCode == null || !statusCode.sanityCheck()) {
			return false;
		}
		return true;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (contextElement == null ? 0 : contextElement.hashCode());
		result = prime * result
				+ (statusCode == null ? 0 : statusCode.hashCode());
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
		ContextElementResponse_OrionCustomization other = (ContextElementResponse_OrionCustomization) obj;
		if (contextElement == null) {
			if (other.contextElement != null) {
				return false;
			}
		} else if (!contextElement.equals(other.contextElement)) {
			return false;
		}
		if (statusCode == null) {
			if (other.statusCode != null) {
				return false;
			}
		} else if (!statusCode.equals(other.statusCode)) {
			return false;
		}
		return true;
	}

	@Override
	public NgsiStructure toStandardNgsiStructure() {
		return toContextElementResponse();
	}

}
