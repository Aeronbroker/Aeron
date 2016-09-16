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

package eu.neclab.iotplatform.iotbroker.commons;

import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextRequest;

public class SubscriptionWithInfo extends SubscribeContextRequest {

	private String id;

	public SubscriptionWithInfo() {
		super();
	}
	
	public SubscriptionWithInfo(String id, SubscribeContextRequest subscribeContextRequest) {
		super();
		this.id = id;
		this.setAttributeList(subscribeContextRequest.getAttributeList());
		this.setDuration(subscribeContextRequest.getDuration());
		this.setEntityIdList(subscribeContextRequest.getEntityIdList());
		this.setNotifyCondition(subscribeContextRequest.getNotifyCondition());
		this.setReference(subscribeContextRequest.getReference());
		this.setRestriction(subscribeContextRequest.getRestriction());
		this.setThrottling(subscribeContextRequest.getThrottling());
	}

	public SubscriptionWithInfo(SubscribeContextRequest subscribeContextRequest) {
		super();
		this.setAttributeList(subscribeContextRequest.getAttributeList());
		this.setDuration(subscribeContextRequest.getDuration());
		this.setEntityIdList(subscribeContextRequest.getEntityIdList());
		this.setNotifyCondition(subscribeContextRequest.getNotifyCondition());
		this.setReference(subscribeContextRequest.getReference());
		this.setRestriction(subscribeContextRequest.getRestriction());
		this.setThrottling(subscribeContextRequest.getThrottling());
	}

	public SubscriptionWithInfo(String id) {
		super();
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "SubscriptionWithInfo [id=" + id + ", subscription="
				+ toJsonString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubscriptionWithInfo other = (SubscriptionWithInfo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	

}
