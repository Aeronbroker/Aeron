package eu.neclab.iotplatform.iotbroker.commons.interfaces;

import eu.neclab.iotplatform.iotbroker.commons.SubscriptionWithInfo;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElement;

public interface OnChangeHandlerInterface {

	public abstract ContextElement applyOnChangeNotifyCondition(
			ContextElement contextElement,
			SubscriptionWithInfo subscriptionWithInfo);

}