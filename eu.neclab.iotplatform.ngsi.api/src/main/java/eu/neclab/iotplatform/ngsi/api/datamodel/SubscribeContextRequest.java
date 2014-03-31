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

@XmlRootElement(name = "subscribeContextRequest")
@XmlAccessorType(XmlAccessType.FIELD)

/**
 * Implements SubscribeContextRequest
 * as defined in OMA NGSI 9/10 approved version 1.0.
 */
public class SubscribeContextRequest extends NgsiStructure {

	@XmlElementWrapper(name = "entityIdList", required = true)
	@XmlElement(name = "entityId", required = true)
	private List<EntityId> entityId;
	@XmlElementWrapper(name = "attributeList", required = true)
	@XmlElement(name = "attribute", nillable = true)
	protected List<String> attribute;
	@XmlElement(name = "reference", required = true)
	private String reference = null;
	@XmlElement(name = "duration")
	private Duration duration = null;
	@XmlElement(name = "restriction")
	private Restriction restriction = null;
	@XmlElementWrapper(name = "notifyConditions")
	@XmlElement(name = "notifyCondition")
	private List<NotifyCondition> notifyCondition;
	@XmlElement(name = "throttling")
	private Duration throttling = null;

	public SubscribeContextRequest() {

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

	public List<EntityId> getAllEntity() {

		return new ArrayList<EntityId>(entityId);

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

	public String getReference() {
		return reference;
	}

	public void setReference(String ref) {
		this.reference = ref;

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
		this.notifyCondition = notifyConditions;
	}

	public Duration getThrottling() {
		return throttling;
	}

	public void setThrottling(Duration throttling) {
		this.throttling = throttling;
	}

}
