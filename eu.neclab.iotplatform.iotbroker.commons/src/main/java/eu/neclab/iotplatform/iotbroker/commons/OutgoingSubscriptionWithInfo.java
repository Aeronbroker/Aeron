package eu.neclab.iotplatform.iotbroker.commons;

import java.net.URI;

public class OutgoingSubscriptionWithInfo extends SubscriptionWithInfo {

	private URI agentURI;

	private String incomingSubscriptionId;

	public String getIncomingSubscriptionId() {
		return incomingSubscriptionId;
	}

	public void setIncomingSubscriptionId(String incomingSubscriptionId) {
		this.incomingSubscriptionId = incomingSubscriptionId;
	}

	public URI getAgentURI() {
		return agentURI;
	}

	public void setAgentURI(URI agentURI) {
		this.agentURI = agentURI;
	}

	@Override
	public String toString() {
		return "OutgoingSubscriptionWithMetadata [id=" + super.getId()
				+ "agentURI=" + agentURI + ", incomingSubscriptionId="
				+ incomingSubscriptionId + ", subscription=" + toJsonString()
				+ "]";
	}

}
