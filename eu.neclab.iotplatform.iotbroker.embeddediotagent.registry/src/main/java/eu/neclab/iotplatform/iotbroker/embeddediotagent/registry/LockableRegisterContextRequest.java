package eu.neclab.iotplatform.iotbroker.embeddediotagent.registry;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.RegisterContextRequest;

public class LockableRegisterContextRequest {
	private ReentrantLock lock = new ReentrantLock();

	private RegisterContextRequest registerContextRequest = new RegisterContextRequest();

	public RegisterContextRequest getRegisterContextRequest() {
		return registerContextRequest;
	}

	public void setRegisterContextRequest(
			RegisterContextRequest registerContextRequest) {
		this.registerContextRequest = registerContextRequest;
	}

	LockableRegisterContextRequest(ContextMetadata embeddedAgentIdentifier) {
		super();
		List<ContextRegistration> contextRegistrationList = new ArrayList<ContextRegistration>();
		ContextRegistration contextRegistration = new ContextRegistration();
		contextRegistrationList.add(contextRegistration);

		List<ContextMetadata> contextMetadataList = new ArrayList<ContextMetadata>();
		contextRegistration.setListContextMetadata(contextMetadataList);

		if (embeddedAgentIdentifier != null) {
			contextMetadataList.add(embeddedAgentIdentifier);
		}

		List<EntityId> entityIdList = new ArrayList<EntityId>();
		contextRegistration.setListEntityId(entityIdList);

		List<ContextRegistrationAttribute> contextRegistrationAttributeList = new ArrayList<ContextRegistrationAttribute>();
		contextRegistration
				.setListContextRegistrationAttribute(contextRegistrationAttributeList);

		registerContextRequest
				.setContextRegistrationList(contextRegistrationList);
	}

	public ContextRegistration getContextRegistration() {
		return registerContextRequest.getContextRegistrationList().iterator()
				.next();
	}

	public void setContextRegistration(ContextRegistration contextRegistration) {
		List<ContextRegistration> contextRegistrationList = new ArrayList<ContextRegistration>();
		contextRegistrationList.add(contextRegistration);
		registerContextRequest
				.setContextRegistrationList(contextRegistrationList);
	}

	public void lock() {
		lock.lock();
	}

	public void unlock() {
		lock.unlock();
	}

}
