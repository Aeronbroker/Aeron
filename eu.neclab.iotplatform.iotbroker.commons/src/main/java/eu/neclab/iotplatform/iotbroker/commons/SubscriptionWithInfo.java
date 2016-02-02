package eu.neclab.iotplatform.iotbroker.commons;

import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextRequest;

public class SubscriptionWithInfo extends SubscribeContextRequest {

	private String id;

	public SubscriptionWithInfo() {
		super();
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
	
	

}
