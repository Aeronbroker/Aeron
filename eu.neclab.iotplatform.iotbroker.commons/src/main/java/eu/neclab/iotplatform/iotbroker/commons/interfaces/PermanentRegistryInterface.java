package eu.neclab.iotplatform.iotbroker.commons.interfaces;

import java.util.Map;

import eu.neclab.iotplatform.ngsi.api.datamodel.RegisterContextRequest;

public interface PermanentRegistryInterface {

	Map<String, RegisterContextRequest> getAllRegistrations();

	void storeRegistration(String id, RegisterContextRequest registration);

	void deleteRegistration(String id);

}
