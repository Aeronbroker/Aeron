package eu.neclab.iotplatform.iotbroker.commons.interfaces;

import java.util.List;

import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElementResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextRequest;

public interface OnValueHandlerInterface {

	public abstract List<ContextElementResponse> applyOnValue(
			List<ContextElementResponse> contextElementResponseToFilterList,
			SubscribeContextRequest subscribeContextRequestFilter);

}