package eu.neclab.iotplatform.iotbroker.embeddediotagent.couchdb;

import java.util.HashMap;
import java.util.Map;

public class CouchDBBulkStoreResponse {

	private Map<String, String> idAndRevision = new HashMap<String, String>();
	private Map<String, String> errorInsertion = new HashMap<String, String>();

	public Map<String, String> getIdAndRevision() {
		return idAndRevision;
	}

	public void setIdAndRevision(Map<String, String> idAndRevision) {
		this.idAndRevision = idAndRevision;
	}

	public Map<String, String> getErrorInsertion() {
		return errorInsertion;
	}

	public void setErrorInsertion(Map<String, String> errorInsertion) {
		this.errorInsertion = errorInsertion;
	}

}
