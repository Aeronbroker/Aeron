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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.codec.binary.Hex;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Implements NotifyContextAvailabilityRequest as defined in OMA NGSI 9/10
 * approved version 1.0.
 */
@XmlRootElement(name = "notifyContextAvailabilityRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class NotifyContextAvailabilityRequest_OrionCustomization extends
		NgsiStructureAlternative {

	@JsonIgnore
	private String subscriptionId = null;

	@XmlElement(name = "subscriptionId", required = true)
	@JsonProperty("subscriptionId")
	private String subscriptionIdOrionFormat = null;

	@XmlElementWrapper(name = "contextRegistrationResponseList")
	@XmlElement(name = "contextRegistrationResponse", required = true)
	@JsonProperty("contextRegistrationResponses")
	private List<ContextRegistrationResponse_OrionCustomization> contextRegistrationResponse = null;

	// @XmlElement(name = "errorCode", required = true)
	// private StatusCode errorCode = null;

	public NotifyContextAvailabilityRequest_OrionCustomization() {

	}

	public NotifyContextAvailabilityRequest_OrionCustomization(
			String subscriptionId,
			List<ContextRegistrationResponse_OrionCustomization> contextRegistrationResponseList) {
		this.subscriptionId = subscriptionId;
		this.contextRegistrationResponse = contextRegistrationResponseList;
		// this.errorCode = errorCode;
	}

	public NotifyContextAvailabilityRequest_OrionCustomization(
			NotifyContextAvailabilityRequest notifyContextAvailabilityRequest) {
		this.subscriptionId = notifyContextAvailabilityRequest
				.getSubscriptionId();
		if (notifyContextAvailabilityRequest
				.getContextRegistrationResponseList() != null
				&& !notifyContextAvailabilityRequest
						.getContextRegistrationResponseList().isEmpty()) {
			this.contextRegistrationResponse = new ArrayList<ContextRegistrationResponse_OrionCustomization>();
			for (ContextRegistrationResponse contextRegistrationResp : notifyContextAvailabilityRequest
					.getContextRegistrationResponseList()) {
				this.contextRegistrationResponse
						.add(new ContextRegistrationResponse_OrionCustomization(
								contextRegistrationResp));
			}
		}
		// this.errorCode = notifyContextAvailabilityRequest.getErrorCode();
	}

	public String getSubscriptionId() {
		if (subscriptionId.length() != 24) {
			if (subscriptionIdOrionFormat == null) {
				try {
					MessageDigest md = MessageDigest.getInstance("MD5");
					byte[] thedigest = md.digest(subscriptionId
							.getBytes("UTF-8"));
					subscriptionIdOrionFormat = new String(Arrays.copyOfRange(
							Hex.encodeHex(thedigest), 0, 24));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return subscriptionIdOrionFormat;
		}
		return subscriptionId;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
		if (subscriptionId.length() != 24) {
			if (subscriptionIdOrionFormat == null) {
				try {
					MessageDigest md = MessageDigest.getInstance("MD5");
					byte[] thedigest = md.digest(subscriptionId
							.getBytes("UTF-8"));
					subscriptionIdOrionFormat = new String(Arrays.copyOfRange(
							Hex.encodeHex(thedigest), 0, 24));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@JsonIgnore
	public List<ContextRegistrationResponse_OrionCustomization> getContextRegistrationResponseList() {
		if (contextRegistrationResponse == null) {
			contextRegistrationResponse = new ArrayList<ContextRegistrationResponse_OrionCustomization>();
		}
		return contextRegistrationResponse;
	}

	@JsonIgnore
	public void setContextRegistrationResponseList(
			List<ContextRegistrationResponse_OrionCustomization> contextRegistrationResponseList) {
		this.contextRegistrationResponse = contextRegistrationResponseList;
	}

	// public StatusCode getErrorCode() {
	// return errorCode;
	// }
	//
	// public void setErrorCode(StatusCode errorCode) {
	// this.errorCode = errorCode;
	// }

	public NotifyContextAvailabilityRequest toNotifyContextAvailabilityRequest() {
		NotifyContextAvailabilityRequest notifyContextAvailabilityRequest = new NotifyContextAvailabilityRequest();
		if (contextRegistrationResponse != null
				&& !contextRegistrationResponse.isEmpty()) {
			notifyContextAvailabilityRequest
					.setContextRegistrationResponseList(new ArrayList<ContextRegistrationResponse>());
			for (ContextRegistrationResponse_OrionCustomization contextRegistrationResponse_orion : contextRegistrationResponse) {
				notifyContextAvailabilityRequest
						.getContextRegistrationResponseList().add(
								contextRegistrationResponse_orion
										.toContextRegistrationResponse());
			}
		}
		notifyContextAvailabilityRequest.setErrorCode(new StatusCode(200,
				ReasonPhrase.OK_200.toString(), ""));
		return notifyContextAvailabilityRequest;

	}

	@Override
	public NgsiStructure toStandardNgsiStructure() {
		return toNotifyContextAvailabilityRequest();
	}

	@JsonIgnore
	public String getSubscriptionIdOrionFormat() {
		return subscriptionIdOrionFormat;
	}

	@JsonIgnore
	public void setSubscriptionIdOrionFormat(String subscriptionIdOrionFormat) {
		this.subscriptionIdOrionFormat = subscriptionIdOrionFormat;
	}

}
