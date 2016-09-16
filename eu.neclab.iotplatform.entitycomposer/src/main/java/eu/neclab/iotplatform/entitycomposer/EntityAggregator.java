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

package eu.neclab.iotplatform.entitycomposer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.neclab.iotplatform.entitycomposer.datamodel.AggregationType;
import eu.neclab.iotplatform.entitycomposer.datamodel.EntityAggregationInfo;
import eu.neclab.iotplatform.iotbroker.commons.EntityIDMatcher;
import eu.neclab.iotplatform.ngsi.api.datamodel.AttributeAssociation;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElement;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElementResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;

/**
 * Objects of this class apply aggregation functions.
 */
public class EntityAggregator {

	Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * This is the data structure for maintaining the known source
	 * attribute values. The outer map is indexed by the attribute name, 
	 * while the inner hash maps are indexed by the entity IDs.
	 */
	Map<String, Map<EntityId,Double>> sourceValues;
	
	/**
	 * The entities useful for this aggregator as sources.
	 */
	List<EntityId> sourceEntityList;
	
	/**
	 * This map contains the relevant attribute associations
	 * for this aggregator, where each attribute association
	 * is stored with the source as the key and the target
	 * as the value
	 */
	Map<String,String> attributeAssociationMap;
	
	
	/**
	 * The Id of the target entity
	 */
	EntityId targetEntityId;
	
	/**
	 * The aggregation function used.
	 */
	AggregationType aggType;
	
	/**
	 *  Create an entity aggregator object from aggregation info and a list of 
	 *  requested entities and attributes.
	 */
	public EntityAggregator(EntityAggregationInfo entAggInfo, List<EntityId> entityIdList, List<String> attributeList) throws NotRequestedException {
		
		logger.info("costructing entity aggregator for \n"+entAggInfo.toString());
		
		this.targetEntityId = entAggInfo.getTargetEntity();
		
		/*
		 * Run through given entity Id list to see whether the target entity is
		 * requested
		 */		
		{
			boolean isRequested = false;
			for(EntityId eId:entityIdList)
				if (EntityIDMatcher.matcher(targetEntityId,eId))
					isRequested = true;
			
			
			if(!isRequested) {
				logger.info("Target entityId not requested");	
				throw new NotRequestedException("EntityId not requested");
			}
			
		}
		
		
		
		this.sourceValues = new HashMap<String, Map<EntityId,Double>>();
		
		/*
		 * initialize the source entities and aggregation type like given
		 * in the aggregation info.
		 */
		this.sourceEntityList = new ArrayList<EntityId>(entAggInfo.getSourceEntityList());		
		this.aggType = entAggInfo.getAggregationType();
		
		/*
		 * For determining the attributes for which to apply this aggregator, 
		 * we compute the intersection between the requested attributes and the 
		 * ones mentioned as targets in the aggregation info.
		 * 
		 * But first we need to address the special cases where attribute lists
		 * are not given.
		 */
		
		/*
		 * If neither the request nor the association list  
		 * has attributes specified ...
		 */
		if(
				(attributeList == null || attributeList.isEmpty())
				&&
				(
				entAggInfo.getAttributeAssociationList() == null ||
				entAggInfo.getAttributeAssociationList().isEmpty()
				)
				)
		{
			/*
			 * ...then attribute lists stays empty, meaning that
			 * all attributes are relevant.
			 */
		}
		
		/*
		 * If the request specifies attributes but the aggregation
		 * info does not.... 
		 */
		else if(entAggInfo.getAttributeAssociationList() == null ||
				entAggInfo.getAttributeAssociationList().isEmpty()
				)
		{
			/*
			 * ... we make attribute associations for the needed attributes.
			 */
			
			this.attributeAssociationMap = new HashMap<String,String>();
			for(String attr:attributeList){
				attributeAssociationMap.put(attr,attr);				
			}
		}
		
		/*
		 * If the aggregation info specifies attributes but the request
		 * does not ...
		 */
		else if(
				attributeList == null || attributeList.isEmpty()
				)
		{
			/*
			 * We make exactly the attribute associations described in
			 * the aggregation info
			 */
			this.attributeAssociationMap = new HashMap<String,String>();
			for(AttributeAssociation assoc:entAggInfo.getAttributeAssociationList()){
				attributeAssociationMap.put(assoc.getSourceAttribute(), assoc.getTargetAttribute());
			}
		}
		
		else
		{
			
			/*
			 * We compute the intersection as described above:
			 * first create the set of requested attributes for 
			 * fast lookup
			 */
			Set<String> reqAttribSet = new HashSet<String>(attributeList);
			
			this.attributeAssociationMap = new HashMap<String,String>();
			
			/*
			 * Now run through the attribute associations and pick the 
			 * relevant ones (= the ones whose target is a requested attribute).
			 */
			for(AttributeAssociation assoc:entAggInfo.getAttributeAssociationList()){
				if(reqAttribSet.contains(assoc.getTargetAttribute()))
						attributeAssociationMap.put(assoc.getSourceAttribute(), 
								assoc.getTargetAttribute());
			}
			
		}
		
		
	}

	/**
	 * Returns the list of entities that can be used as sources for 
	 * this aggregation.
	 */
	public List<EntityId> getSourceEntities() {
		
		return sourceEntityList;
	}
	
	/**
	 * Returns the list of attributes that can be used as sources for 
	 * this aggregation
	 */
	public List<String> getSourceAttributes() {

		/*
		 * no attributes specified
		 */
		if(attributeAssociationMap == null) return null;
		
		/*
		 * attributes are specified
		 */
		return new ArrayList<String>(attributeAssociationMap.keySet());
		
	}


	/**
	 * Submit source data to the aggregator.
	 */
	public void putData(List<ContextElementResponse> ContextElementResponseList) {
		
		/*
		 * We check for each attribute value if it is numerical, and if yes, 
		 * we put it into the value map.
		 * 
		 * Note: for efficiency we do not check whether the attribute value
		 * is relevant for the aggregation here, because we would have to 
		 * check it against each of the source entity IDs (which are potentially
		 * patterns, so no hashmap tricks are applicable)
		 * 
		 */
		
		logger.info("starting aggregator put method");
		
		if(ContextElementResponseList == null) return;
		
		for(ContextElementResponse cer:ContextElementResponseList){
			if(cer.getContextElement() == null)
				continue;
			if(cer.getContextElement().getContextAttributeList() == null)
				continue;
			
			for(ContextAttribute attrib:cer.getContextElement().getContextAttributeList())
			{
				
				logger.info("now processing attribute:/n"+attrib.toString());
				
				Object attrVal = attrib.getContextValue();
				
				
				
				/*
				 * we try to cast the attribute value into a Double
				 */
				double attrValNum = Double.NaN;
				try{
				attrValNum = Double.valueOf(attrVal.toString());
				}
				catch(ClassCastException E)
				{
					logger.info("Value of attribute "+ attrib.getName()+
							" of entity "+cer.getContextElement().getEntityId().getId()+
							"not numerical.");
					
					continue;
				}
				
				
				
				/*
				 * Now as we have the attribute value as a numerical value,
				 * we can add it to the map.
				 */
				
				/*
				 * check if map for this attribute has been initialized 
				 */
				Map<EntityId,Double> entityValueMap = sourceValues.get(attrib.getName());
				if(entityValueMap == null)
				{
					entityValueMap = new HashMap<EntityId,Double>();
					sourceValues.put(attrib.getName(), entityValueMap);
				}

				/*
				 * add it to the map
				 */
				entityValueMap.put(cer.getContextElement().getEntityId(), new Double(attrValNum));
				
				logger.info("successfully put value into map:"+attrValNum);
			}
			
		}
		
		
	}

	/**
	 * Returns the aggregated entity information. 
	 * 
	 */
	public ContextElementResponse getAggregate() {
		
		logger.info("started aggregator get method");
		
		/*
		 * Create the ContextElementResponse skeleton. 
		 */		
		ContextElementResponse cer = new ContextElementResponse();
		
		/*
		 * create the contextElement skeleton
		 */
		ContextElement ce = new ContextElement();		
		ce.setEntityId(targetEntityId);
		
		/*
		 * put the contextelement skeletton into the contextelementresponse
		 * skeleton
		 */
		cer.setContextElement(ce);
		
		/*
		 * create the attributeList
		 */
		List<ContextAttribute> attributeList = new ArrayList<ContextAttribute>();
		
		/*
		 * put it into the context element skeleton
		 */
		ce.setContextAttributeList(attributeList);
		
		logger.info("aggregator's value map:\n"+sourceValues.toString());
		logger.info("attribute Associations:\n"+attributeAssociationMap.toString());
		
		/*
		 * Now we populate the attribute list
		 */
		for(String srcAttrName:sourceValues.keySet()){
			
			logger.info("now processing source attribute: "+srcAttrName);
			
			Map<EntityId,Double> valueMap = sourceValues.get(srcAttrName);			
			double aggregatedValue = aggregate(valueMap.values()); 
			
			ContextAttribute ca = new ContextAttribute(
					attributeAssociationMap.get(srcAttrName), null, (new Double(aggregatedValue)).toString());
			
			logger.info("created context attribute:\n"+ca.toString());
			
			attributeList.add(ca);
		}
		
		return cer;
	}

	/**
	 * Aggregates doubles according to the aggregation function specified
	 * for this object. The behavior in corner cases is as follows:
	 * 
	 * ALL functions:
	 * - non-finite input numbers (NaN or +-infinity) will be ignored.
	 * 
	 * AVG: 
	 * - returns NaN if there are no input values
	 * MAX, MIN:
	 * - return NaN if there are no input values
	 * SUM:
	 * - return 0.0 if there are no input values
	 */
	private double aggregate(Collection<Double> values) {

		logger.info("starting aggregation of:\n"+values.toString());
		logger.info("aggregation type: "+this.aggType);
		
		double aggregated = Double.NaN;
		
		switch(this.aggType){
		
		case AVG:
		{
			double sum = 0.0;
			int cnt = 0;
			
			for(Double db:values)
			{
				double d = db.doubleValue();
				if(!isFinite(d))
					continue;
				
				cnt++;
				sum+=d;
			}
			if(cnt>0)
				aggregated = sum/(double)cnt;
		}	
			break;
		case MAX:
		{
			double max = Double.NEGATIVE_INFINITY;
			
			for(Double db:values)
			{
				double d = db.doubleValue();
				
				if(!isFinite(d)) continue;
				
				if(d>max) max = d;
			}
			if(max!=Double.NEGATIVE_INFINITY)
				aggregated = max;
				
		}
			break;
		case MIN:
		{
			double min = Double.POSITIVE_INFINITY;
			
			for(Double db:values)
			{
				double d = db.doubleValue();
				
				if(!isFinite(d)) continue;
				
				if(d<min) min = d;
			}
			if(min!=Double.POSITIVE_INFINITY)
				aggregated = min;			
		}
			break;
		case SUM:
		{
			double sum = 0.0;
			
			for(Double db:values){
				
				double d = db.doubleValue();
				
				if(!isFinite(d))
					continue;
				
				sum+=d;
				
			}
			aggregated = sum;						
		}
			break;
		
		}
		
		logger.info("aggregated value: "+aggregated);
		
		return aggregated;
		
	}

	private boolean isFinite(double d) {
		//a double is finite if it is neither infinite nor NaN
		return !( Double.isInfinite(d) || Double.isNaN(d) );
	}



}
