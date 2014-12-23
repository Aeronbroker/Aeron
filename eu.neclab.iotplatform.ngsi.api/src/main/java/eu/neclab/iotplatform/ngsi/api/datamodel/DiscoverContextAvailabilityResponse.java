/*******************************************************************************
 *   Copyright (c) 2014, NEC Europe Ltd.
 *   All rights reserved.
 *   
 *   Authors:
 *           * Salvatore Longo - salvatore.longo@neclab.eu
 *           * Tobias Jacobs - tobias.jacobs@neclab.eu
 *           * Raihan Ul-Islam - raihan.ul-islam@neclab.eu
 *  
 *    Redistribution and use in source and binary forms, with or without
 *    modification, are permitted provided that the following conditions are met:
 *   1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   3. All advertising materials mentioning features or use of this software
 *     must display the following acknowledgement:
 *     This product includes software developed by NEC Europe Ltd.
 *   4. Neither the name of the NEC nor the
 *     names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY NEC ''AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL NEC BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED 
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
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
 * Implements DiscoverContextAvailabilityResponse
 * as defined in OMA NGSI 9/10 approved version 1.0.
 */
@XmlRootElement(name = "discoverContextAvailabilityResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class DiscoverContextAvailabilityResponse extends NgsiStructure {

	@XmlElementWrapper(name = "contextRegistrationResponseList")
	@XmlElement(name = "contextRegistrationResponse", required = false)
	@JsonProperty("contextRegistrationResponses")
	private List<ContextRegistrationResponse> contextRegistrationResponse = null;
	@XmlElement(name = "errorCode", required = false)
	private StatusCode errorCode = null;

	public DiscoverContextAvailabilityResponse() {

	}

	public DiscoverContextAvailabilityResponse(
			List<ContextRegistrationResponse> contextRegRespList,
			StatusCode errorCode) {
		contextRegistrationResponse = contextRegRespList;
		this.errorCode = errorCode;
	}

	public List<ContextRegistrationResponse> getContextRegistrationResponse() {
		if (contextRegistrationResponse == null) {
			contextRegistrationResponse = new ArrayList<ContextRegistrationResponse>();
		}
		return contextRegistrationResponse;
	}

	public void setContextRegistrationResponse(
			List<ContextRegistrationResponse> contextRegistrationResponse) {
		this.contextRegistrationResponse = contextRegistrationResponse;
	}

	public StatusCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(StatusCode errorCode) {
		this.errorCode = errorCode;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ (contextRegistrationResponse == null ? 0
						: contextRegistrationResponse.hashCode());
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
		DiscoverContextAvailabilityResponse other = (DiscoverContextAvailabilityResponse) obj;
		if (contextRegistrationResponse == null) {
			if (other.contextRegistrationResponse != null) {
				return false;
			}
		} else if (!contextRegistrationResponse
				.equals(other.contextRegistrationResponse)) {
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


}
