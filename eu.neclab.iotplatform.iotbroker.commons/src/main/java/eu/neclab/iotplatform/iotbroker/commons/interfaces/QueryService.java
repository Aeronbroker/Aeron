package eu.neclab.iotplatform.iotbroker.commons.interfaces;

import java.util.List;

import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextResponse;

/**
 *  Interface for services that can return NGSI data when called with a
 *  query context request and a list of context registrations.
 */
public interface QueryService {
	
	public QueryContextResponse queryContext(QueryContextRequest req, List<ContextRegistration> regList);	

}
