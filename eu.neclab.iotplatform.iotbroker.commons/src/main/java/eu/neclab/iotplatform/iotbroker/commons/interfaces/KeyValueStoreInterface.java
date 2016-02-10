package eu.neclab.iotplatform.iotbroker.commons.interfaces;

import java.util.Collection;

import com.google.common.collect.Multimap;

import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElement;

public interface KeyValueStoreInterface {

	void updateValue(String key, ContextElement contextElement);

	void storeValue(String key, ContextElement contextElement);
	
	public Collection<String> getKeys(String startKey, String endKey);
	
	public Multimap<String, String> getIdsByType();
	
	ContextElement getValue(String latestValueDocumentKey);
	
	ContextElement getValues(String startKey, String endKey);

}