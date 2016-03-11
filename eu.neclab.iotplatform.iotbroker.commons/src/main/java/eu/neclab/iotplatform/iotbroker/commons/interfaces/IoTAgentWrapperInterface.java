package eu.neclab.iotplatform.iotbroker.commons.interfaces;

import java.net.URI;

import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextSubscriptionRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextSubscriptionResponse;

public interface IoTAgentWrapperInterface {

	/* (non-Javadoc)
	 * @see eu.neclab.iotplatform.iotbroker.core.subscription.IoTAgentWrapperInterface#receiveReqFrmSubscriptionController(eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextRequest, java.net.URI)
	 */
	public abstract SubscribeContextResponse receiveReqFrmSubscriptionController(
			SubscribeContextRequest scReq, URI uri);

	/**
	 * Processes NGSI 10 notifications.
	 * 
	 * @param ncReq
	 * The NGSI 10 NotifyContextRequest.
	 * @return
	 * The NGSI 10 NotifyContextResponse.
	 */
	public abstract NotifyContextResponse receiveFrmAgents(
			NotifyContextRequest notifyContextRequest);

	/* (non-Javadoc)
	 * @see eu.neclab.iotplatform.iotbroker.core.subscription.IoTAgentWrapperInterface#receiveReqFrmSubscriptionController(eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextRequest, java.net.URI)
	 */
	public abstract UnsubscribeContextResponse receiveReqFrmSubscriptionController(
			UnsubscribeContextRequest uCReq, URI uri);

	/* (non-Javadoc)
	 * @see eu.neclab.iotplatform.iotbroker.core.subscription.IoTAgentWrapperInterface#receiveReqFrmSubscriptionController(eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextSubscriptionRequest, java.net.URI)
	 */
	public abstract UpdateContextSubscriptionResponse receiveReqFrmSubscriptionController(
			UpdateContextSubscriptionRequest uCReq, URI uri);

}