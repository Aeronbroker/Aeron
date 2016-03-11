package eu.neclab.iotplatform.iotbroker.commons.interfaces;

import eu.neclab.iotplatform.iotbroker.commons.SubscriptionWithInfo;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextRequest;

public interface OnTimeIntervalHandlerInterface {

	public abstract void setIotAgentWrapper(
			IoTAgentWrapperInterface iotAgentWrapper);

	public abstract boolean pushSubscription(
			SubscriptionWithInfo subscriptionWithInfo);

	public abstract boolean notifyContext(NotifyContextRequest notification);

}