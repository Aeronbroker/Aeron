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

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement(name = "subscribeContextRequest")
@XmlAccessorType(XmlAccessType.FIELD)

/**
 * Implements SubscribeContextRequest
 * as defined in OMA NGSI 9/10 approved version 1.0.
 */
public class SubscribeContextRequest extends NgsiStructure {

	@XmlElementWrapper(name = "entityIdList")
	@XmlElement(name = "entityId", required = true)
	@JsonProperty("entities")
	private List<EntityId> entityId;
	
	@XmlElementWrapper(name = "attributeList")
	@XmlElement(name = "attribute", nillable = true)
	@JsonProperty("attributes")
	protected List<String> attribute;
	
	@XmlElement(name = "reference", required = true)
	private String reference = null;
	
	@XmlElement(name = "duration")
	private Duration duration = null;
	
	@XmlElement(name = "restriction")
	private Restriction restriction = null;
	
	@XmlElementWrapper(name = "notifyConditions")
	@XmlElement(name = "notifyCondition")
	@JsonProperty("notifyConditions")
	private List<NotifyCondition> notifyCondition;
	
	@XmlElement(name = "throttling")
	private Duration throttling = null;

	public SubscribeContextRequest() {

	}
	@JsonIgnore
	public List<EntityId> getEntityIdList() {
		if (entityId == null) {
			entityId = new ArrayList<EntityId>();
		}
		return entityId;
	}
	@JsonIgnore
	public void setEntityIdList(List<EntityId> entityId) {
		this.entityId = entityId;

	}
	@JsonIgnore
	public List<EntityId> getAllEntity() {

		return new ArrayList<EntityId>(entityId);

	}
	@JsonIgnore
	public List<String> getAttributeList() {
		if (attribute == null) {
			attribute = new ArrayList<String>();
		}
		return attribute;
	}
	@JsonIgnore
	public void setAttributeList(List<String> attributeList) {
		attribute = attributeList;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String ref) {
		reference = ref;

	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;

	}

	public Restriction getRestriction() {
		return restriction;
	}

	public void setRestriction(Restriction restriction) {
		this.restriction = restriction;

	}

	public List<NotifyCondition> getNotifyCondition() {
		if (notifyCondition == null) {
			notifyCondition = new ArrayList<NotifyCondition>();
		}
		return notifyCondition;
	}

	public void setNotifyCondition(List<NotifyCondition> notifyConditions) {
		notifyCondition = notifyConditions;
	}

	public Duration getThrottling() {
		return throttling;
	}

	public void setThrottling(Duration throttling) {
		this.throttling = throttling;
	}

}
