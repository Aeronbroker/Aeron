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

/*******************************************************************************
 *   Copyright (c) 2015, NEC Europe Ltd.
 *   All rights reserved.
 *
 *   Authors:
 *           * Salvatore Longo - salvatore.longo@neclab.eu
 *           * Tobias Jacobs - tobias.jacobs@neclab.eu
 *           * Flavio Cirillo - flavio.cirillo@neclab.eu
 *           * Raihan Ul-Islam
 *
 *    Redistribution and use in source and binary forms, with or without
 *    modification, are permitted provided that the following conditions are met:
 *   1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   3. All advertising materials mentioning features or use of this software
 *     must display the following acknowledgment:
 *     This product includes software developed by NEC Europe Ltd.
 *   4. Neither the name of NEC nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific 
 *   prior written permission.
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
import javax.xml.bind.annotation.XmlSchemaType;

import org.apache.commons.codec.binary.Hex;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement(name = "notifyContextRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class NotifyContextRequest_OrionCustomization extends
		NgsiStructureAlternative {

	@XmlElement(name = "subscriptionId", required = true)
	private String subscriptionId = null;

	@XmlElement(name = "subscriptionId", required = true)
	@JsonProperty("subscriptionId")
	private String subscriptionIdOrionFormat = null;

	@XmlElement(name = "originator", required = true)
	@XmlSchemaType(name = "anyURI")
	private String originator = null;

	@XmlElementWrapper(name = "contextResponseList")
	@XmlElement(name = "contextElementResponse")
	@JsonProperty("contextResponses")
	private List<ContextElementResponse_OrionCustomization> contextElementResponse;

	public NotifyContextRequest_OrionCustomization() {

	}

	public NotifyContextRequest_OrionCustomization(
			String subcriptionId,
			String originator,
			List<ContextElementResponse_OrionCustomization> contextElementResponseList) {
		this.subscriptionId = subcriptionId;
		this.originator = originator;
		this.contextElementResponse = contextElementResponseList;

	}

	public NotifyContextRequest_OrionCustomization(
			NotifyContextRequest notification) {
		this.subscriptionId = notification.getSubscriptionId();
		this.originator = notification.getOriginator();
		if (notification.getContextElementResponseList() != null
				&& !notification.getContextElementResponseList().isEmpty()) {
			this.contextElementResponse = new ArrayList<ContextElementResponse_OrionCustomization>();
			for (ContextElementResponse contextElementResponse : notification
					.getContextElementResponseList()) {
				this.contextElementResponse
						.add(new ContextElementResponse_OrionCustomization(
								contextElementResponse));
			}
		}
	}
	
	
	@JsonIgnore
	public List<ContextElementResponse_OrionCustomization> getContextElementResponse() {
		return contextElementResponse;
	}

	@JsonIgnore
	public void setContextElementResponse(
			List<ContextElementResponse_OrionCustomization> contextElementResponse) {
		this.contextElementResponse = contextElementResponse;
	}

	@JsonIgnore
	public String getSubscriptionIdOrionFormat() {
		return subscriptionIdOrionFormat;
	}

	@JsonIgnore
	public void setSubscriptionIdOrionFormat(String subscriptionIdOrionFormat) {
		this.subscriptionIdOrionFormat = subscriptionIdOrionFormat;
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
	public String getOriginator() {
		return originator;
	}

	@JsonIgnore
	public void setOriginator(String originator) {
		this.originator = originator;
	}

	public NotifyContextRequest toNotifyContextRequest() {
		List<ContextElementResponse> contextElementResponseList = null;
		if (contextElementResponse != null) {
			contextElementResponseList = new ArrayList<ContextElementResponse>();
			for (ContextElementResponse_OrionCustomization contextElementResponse_OrionCustomization : contextElementResponse) {
				contextElementResponseList
						.add(contextElementResponse_OrionCustomization
								.toContextElementResponse());
			}
		}
		return new NotifyContextRequest(subscriptionId, originator,
				contextElementResponseList);
	}

	@JsonIgnore
	public List<ContextElementResponse_OrionCustomization> getContextElementResponseList() {
		if (contextElementResponse == null) {
			contextElementResponse = new ArrayList<ContextElementResponse_OrionCustomization>();
		}
		return contextElementResponse;
	}

	@JsonIgnore
	public void setContextResponseList(
			List<ContextElementResponse_OrionCustomization> contextResponseList) {
		this.contextElementResponse = contextResponseList;
	}

	@Override
	public NgsiStructure toStandardNgsiStructure() {
		return toNotifyContextRequest();
	}

}
