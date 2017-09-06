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
 * Implements ContextRegistrationResponse as defined in OMA NGSI 9/10 approved
 * version 1.0.
 */
@XmlRootElement(name = "contextRegistrationResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContextRegistrationResponse_OrionCustomization extends
		NgsiStructureAlternative {

	@XmlElement(name = "contextRegistration", required = true)
	private ContextRegistration_OrionCustomization contextRegistration = null;
	@XmlElement(name = "errorCode")
	private StatusCode errorCode = null;

	public ContextRegistrationResponse_OrionCustomization() {

	}

	public ContextRegistrationResponse_OrionCustomization(
			ContextRegistration_OrionCustomization contextRegistration,
			StatusCode errorCode) {

		this.contextRegistration = contextRegistration;
		this.errorCode = errorCode;

	}

	public ContextRegistrationResponse_OrionCustomization(
			ContextRegistrationResponse contextRegistrationResponse) {

		this.contextRegistration = new ContextRegistration_OrionCustomization(
				contextRegistrationResponse.getContextRegistration());
		this.errorCode = contextRegistrationResponse.getErrorCode();

	}

	public ContextRegistration_OrionCustomization getContextRegistration() {
		return contextRegistration;
	}

	public void setContextRegistration(
			ContextRegistration_OrionCustomization contextRegistration) {
		this.contextRegistration = contextRegistration;

	}

	public StatusCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(StatusCode errorCode) {
		this.errorCode = errorCode;

	}

	@Override
	public NgsiStructure toStandardNgsiStructure() {
		return toContextRegistrationResponse();
	}

	public ContextRegistrationResponse toContextRegistrationResponse() {
		return new ContextRegistrationResponse(
				contextRegistration.toContextRegistration(), errorCode);
	}
}
