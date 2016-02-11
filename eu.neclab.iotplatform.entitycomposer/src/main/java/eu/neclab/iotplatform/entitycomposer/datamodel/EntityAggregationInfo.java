package eu.neclab.iotplatform.entitycomposer.datamodel;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import eu.neclab.iotplatform.ngsi.api.datamodel.AttributeAssociation;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.NgsiStructure;

/**
 * This class represents information about how to compose entities from 
 * other entities by means of aggregation functions like SUM, AVG, MIN, MAX.
 */
@XmlRootElement(name = "entityAggregationInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class EntityAggregationInfo extends NgsiStructure{

	@XmlElement(name = "targetEntity", required = true)
	EntityId targetEntity;
	
	@XmlElementWrapper(name = "sourceEntityList")
	@XmlElement(name = "entityId")
	List<EntityId> sourceEntityList;	
	
	@XmlElementWrapper(name = "attributeAssociationList")
	@XmlElement(name = "attributeAssociation")
	List<AttributeAssociation> attributeAssociationList;
	
	@XmlElement(name = "aggregationType", required = true)
	AggregationType aggregationType;
	
	public List<EntityId> getSourceEntityList() {
		return sourceEntityList;
	}

	public void setSourceEntityList(List<EntityId> sourceEntityList) {
		this.sourceEntityList = sourceEntityList;
	}

	public EntityId getTargetEntity() {
		return targetEntity;
	}

	public void setTargetEntity(EntityId targetEntity) {
		this.targetEntity = targetEntity;
	}

	public List<AttributeAssociation> getAttributeAssociationList() {
		return attributeAssociationList;
	}

	public void setAttributeAssociationList(List<AttributeAssociation> attributeAssociationList) {
		this.attributeAssociationList = attributeAssociationList;
	}

	public AggregationType getAggregationType() {
		return aggregationType;
	}

	public void setAggregationType(AggregationType aggregationType) {
		this.aggregationType = aggregationType;
	}

	public EntityAggregationInfo(List<EntityId> sourceEntityList, EntityId targetEntity,
			List<AttributeAssociation> attributeAssociationList, AggregationType aggregationType) {
		super();
		this.sourceEntityList = sourceEntityList;
		this.targetEntity = targetEntity;
		this.attributeAssociationList = attributeAssociationList;
		this.aggregationType = aggregationType;
	}
	
	

	public EntityAggregationInfo() {
		super();
	}

	public EntityAggregationInfo(Object sourceData) {
		// TODO remove this when a proper cast has been implemented
	}

}
