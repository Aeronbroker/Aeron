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
