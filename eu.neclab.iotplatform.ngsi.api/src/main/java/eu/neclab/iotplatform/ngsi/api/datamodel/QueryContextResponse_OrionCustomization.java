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

@XmlRootElement(name = "queryContextResponse")
/**
 * Implements QueryContextResponse
 * as defined in OMA NGSI 9/10 approved version 1.0.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryContextResponse_OrionCustomization extends NgsiStructureAlternative {

	@XmlElementWrapper(name = "contextResponseList")
	@XmlElement(name = "contextElementResponse")
	@JsonProperty("contextResponses")
	private List<ContextElementResponse_OrionCustomization> contextElementResponse;

	@XmlElement(name = "errorCode")
	private StatusCode errorCode = null;

	public QueryContextResponse_OrionCustomization() {

	}

	public QueryContextResponse_OrionCustomization(
			List<ContextElementResponse_OrionCustomization> contextResponseList,
			StatusCode errorCode) {
		this();
		contextElementResponse = contextResponseList;
		this.errorCode = errorCode;
	}

	public QueryContextResponse toQueryContextResponse() {

		List<ContextElementResponse> contextElementResponseList = null;

		if (contextElementResponse != null) {
			contextElementResponseList = new ArrayList<ContextElementResponse>();
			for (ContextElementResponse_OrionCustomization contextElementResponse_OrionCustomization : contextElementResponse) {
				contextElementResponseList
						.add(contextElementResponse_OrionCustomization
								.toContextElementResponse());
			}
		}

		return new QueryContextResponse(contextElementResponseList, errorCode);

	}

	public List<ContextElementResponse_OrionCustomization> getListContextElementResponse() {
		if (contextElementResponse == null) {
			contextElementResponse = new ArrayList<ContextElementResponse_OrionCustomization>();
		}
		return contextElementResponse;
	}

	public void setContextResponseList(
			List<ContextElementResponse_OrionCustomization> ContextResponseList) {
		contextElementResponse = ContextResponseList;
	}

	public StatusCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(StatusCode errorCode) {
		this.errorCode = errorCode;
	}
	
	@Override
	public boolean sanityCheck() {
		if (contextElementResponse != null && !contextElementResponse.isEmpty()) {
			for (ContextElementResponse_OrionCustomization contextElementR : contextElementResponse) {
				if (!contextElementR.sanityCheck()) {
					return false;
				}
			}
		}
		if (errorCode != null && !errorCode.sanityCheck()) {
			return false;
		}
		return true;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ (contextElementResponse == null ? 0 : contextElementResponse
						.hashCode());
		result = prime * result
				+ (errorCode == null ? 0 : errorCode.hashCode());
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
		QueryContextResponse_OrionCustomization other = (QueryContextResponse_OrionCustomization) obj;

		/*
		 * The following makes sure that empty response lists are regarded equal
		 * to non-existing response lists.
		 */
		if (contextElementResponse == null || contextElementResponse.isEmpty()) {
			if (other.contextElementResponse != null
					&& !other.contextElementResponse.isEmpty()) {
				return false;
			}
		} else if (!contextElementResponse.equals(other.contextElementResponse)) {
			return false;
		}

		if (errorCode == null) {
			if (other.errorCode != null) {
				return false;
			}
		} else if (!errorCode.equals(other.errorCode)) {
			return false;
		}
		return true;
	}

	@Override
	public NgsiStructure toStandardNgsiStructure() {
		return toQueryContextResponse();
	}

}
