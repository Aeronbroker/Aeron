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
import javax.xml.datatype.Duration;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Implements SubscribeContextAvailabilityRequest
 * as defined in OMA NGSI 9/10 approved version 1.0.
 */
@XmlRootElement(name = "subscribeContextAvailabilityRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscribeContextAvailabilityRequest extends NgsiStructure {

	@XmlElementWrapper(name = "entityIdList")
	@XmlElement(name = "entityId", required = true)
	@JsonProperty("entities")
	private List<EntityId> entityId = null;

	@XmlElementWrapper(name = "attributeList")
	@XmlElement(name = "attribute", required = false)
	@JsonProperty("attributes")
	private List<String> attribute = null;

	@XmlElement(name = "reference", required = true)
	private String reference = null;

	@XmlElement(name = "duration", required = false)
	private Duration duration = null;

	@XmlElement(name = "subscriptionId", required = false)
	private String subscriptionId = null;

	@XmlElement(name = "restriction", required = false)
	private Restriction restriction = null;

	public SubscribeContextAvailabilityRequest(List<EntityId> entityId,
			List<String> attributeList, String reference, Duration duration,
			String subscriptionId, Restriction restriction) {

		this.entityId = entityId;
		this.attribute = attributeList;
		this.reference = reference;
		this.duration = duration;
		this.subscriptionId = subscriptionId;
		this.restriction = restriction;
	}

	public SubscribeContextAvailabilityRequest() {

	}

	public List<EntityId> getEntityIdList() {
		if (entityId == null) {
			entityId = new ArrayList<EntityId>();
		}
		return entityId;
	}

	public void setEntityIdList(List<EntityId> entityId) {
		this.entityId = entityId;
	}

	public List<String> getAttributeList() {
		if (attribute == null) {
			attribute = new ArrayList<String>();
		}
		return attribute;
	}

	public void setAttributeList(List<String> attributeList) {
		attribute = attributeList;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public String getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public Restriction getRestriction() {
		return restriction;
	}

	public void setRestriction(Restriction restriction) {
		this.restriction = restriction;
	}

}
