/*******************************************************************************
 * Copyright (c) 2016, NEC Europe Ltd.
 * All rights reserved.
 * 
 * Authors:
 *          * NEC IoT Platform Team - iotplatform@neclab.eu
 *          * Flavio Cirillo - flavio.cirillo@neclab.eu
 *          * Tobias Jacobs - tobias.jacobs@neclab.eu
 *          * Gurkan Solmaz - gurkan.solmaz@neclab.eu
 *          * Salvatore Longo
 *          * Raihan Ul-Islam
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions 
 * are met:
 * 1. Redistributions of source code must retain the above copyright 
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above 
 * copyright notice, this list of conditions and the following disclaimer 
 * in the documentation and/or other materials provided with the 
 * distribution.
 * 3. All advertising materials mentioning features or use of this 
 * software must display the following acknowledgment: This 
 * product includes software developed by NEC Europe Ltd.
 * 4. Neither the name of NEC nor the names of its contributors may 
 * be used to endorse or promote products derived from this 
 * software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY NEC ''AS IS'' AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY 
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN 
 * NO EVENT SHALL NEC BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT 
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED 
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH 
 * DAMAGE.
 ******************************************************************************/

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
