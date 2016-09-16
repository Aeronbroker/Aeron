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
import javax.xml.datatype.Duration;

/**
 * Implements UpdateContextAvailabilitySubscriptionRequest
 * as defined in OMA NGSI 9/10 approved version 1.0.
 */
@XmlRootElement(name = "updateContextAvailabilitySubscriptionRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class UpdateContextAvailabilitySubscriptionRequest extends NgsiStructure {

	@XmlElementWrapper(name = "entityIdList")
	@XmlElement(name = "entityId", required = true)
	private List<EntityId> entityId = null;

	@XmlElementWrapper(name = "attributeList")
	@XmlElement(name = "attribute", required = false)
	private List<String> attribute = null;

	@XmlElement(name = "duration", required = false)
	private Duration duration = null;

	@XmlElement(name = "subscriptionId", required = true)
	private String subscriptionId = null;

	@XmlElement(name = "restriction", required = false)
	private Restriction restriction = null;

	public UpdateContextAvailabilitySubscriptionRequest() {

	}

	public UpdateContextAvailabilitySubscriptionRequest(
			List<EntityId> entityId, List<String> attributeList,
			Duration duration, String subscriptionId, Restriction restriction) {

		this.entityId = entityId;
		this.attribute = attributeList;
		this.duration = duration;
		this.subscriptionId = subscriptionId;
		this.restriction = restriction;
	}

	public List<EntityId> getEntityIdList() {
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
		this.attribute = attributeList;
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
