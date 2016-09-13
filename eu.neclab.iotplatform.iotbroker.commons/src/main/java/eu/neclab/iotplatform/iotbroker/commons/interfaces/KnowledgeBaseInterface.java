package eu.neclab.iotplatform.iotbroker.commons.interfaces;

import java.net.URI;
import java.util.Set;

public interface KnowledgeBaseInterface {
	
	Set<URI> getSubTypes(URI type);
	
	Set<URI> getSuperTypes(URI type);
	

}
