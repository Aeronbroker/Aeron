package eu.neclab.iotplatform.iotbroker.commons.interfaces;

import java.net.URI;
import java.util.Date;
import java.util.List;

import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElement;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.ngsi10.Ngsi10Interface;

public interface EmbeddedAgentStorageInterface {

	//	@Override
	public abstract Ngsi10Interface getNgsi10Callback();

	//	@Override
	public abstract void setNgsi10Callback(Ngsi10Interface ngsi10Callback);

	public abstract void storeLatestData(ContextElement isolatedContextElement);

	public abstract void storeHistoricalData(
			ContextElement isolatedContextElement, Date defaultDate);

	//	@Override
	public abstract ContextElement getLatestValue(String id, URI type,
			String attributeName);

	public abstract ContextElement getLatestValue(String entityId, String type,
			String attributeName);

	public abstract ContextElement getLatestValue(String id,
			String attributeName);

	public abstract ContextElement getLatestValue(String latestValueDocumentKey);

	//	@Override
	public abstract ContextElement getHistoricalValues(String id, URI type,
			String attributeName, Date startDate, Date endDate);

	//	@Override
	public abstract List<ContextElement> getLatestValues(
			List<EntityId> entityIdList);

	//	@Override
	public abstract List<ContextElement> getLatestValues(
			List<EntityId> entityIdList, List<String> attributeNames);

	//	@Override
	public abstract List<ContextElement> getHistoricalValues(
			List<EntityId> entityIdList, Date startDate, Date endDate);

	//	@Override
	public abstract List<ContextElement> getHistoricalValues(
			List<EntityId> entityIdList, List<String> attributeNames,
			Date startDate, Date endDate);

}