package eu.neclab.iotplatform.iotbroker.commons.interfaces;

import java.util.List;

import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElement;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextRequest;
import eu.neclab.iotplatform.ngsi.api.ngsi10.Ngsi10Interface;

public interface EmbeddedAgentSubscriptionHandlerInterface {

	public abstract Ngsi10Interface getNgsi10Callback();

	public abstract void setNgsi10Callback(Ngsi10Interface ngsi10Callback);

	// @Override
	public abstract void subscribe(String subscriptionId,
			SubscribeContextRequest subscription);

	public abstract void unsubscribe(String subscriptionId);

	public abstract void checkSubscriptions(
			List<ContextElement> isolatedContextElementList);

}