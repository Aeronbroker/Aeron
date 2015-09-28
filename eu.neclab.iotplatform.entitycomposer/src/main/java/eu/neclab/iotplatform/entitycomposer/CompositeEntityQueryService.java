package eu.neclab.iotplatform.entitycomposer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import eu.neclab.iotplatform.entitycomposer.datamodel.AggregationSourceInformation;
import eu.neclab.iotplatform.entitycomposer.datamodel.EntityAggregationInfo;
import eu.neclab.iotplatform.entitycomposer.datamodel.SourceInfoContextMetadata;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.QueryService;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElementResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.SourceInformation;
import eu.neclab.iotplatform.ngsi.api.ngsi10.Ngsi10Interface;

/**
 *  This class is responsible for composing entities from other entities
 *  by means of aggregation functions.
 *  
 *  After resolving the associations it goes back to the IoT Broker core
 *  to retrieve information on the source entities it needs to compose
 *  the target entity.
 */
public class CompositeEntityQueryService implements QueryService{
	
	private static final String SOURCE_INFORMATION_TYPENAME = "org.fiware.type.metadata.sourceinformation";
	private static final String AGGREGATION_TYPENAME = "org.fiware.type.sourceinformation.aggregation";

	//pointer to IoT Broker core; expected to be autowired
	Ngsi10Interface iotBrokerCore;

	public Ngsi10Interface getIotBrokerCore() {
		return iotBrokerCore;
	}

	public void setIotBrokerCore(Ngsi10Interface iotBrokerCore) {
		this.iotBrokerCore = iotBrokerCore;
	}

	Logger logger = Logger.getLogger(this.getClass());
	
	/** Executor for asynchronous tasks */
	private final ExecutorService taskExecutor = Executors
			.newCachedThreadPool();

	@Override
	public QueryContextResponse queryContext(QueryContextRequest req, List<ContextRegistration> regList) {
		
		logger.info("started composite entity query service");
		
		/*
		 * Pull the relevant assembly information out of the registration metadata. 
		 */
		if(regList==null) return null; //no registrations ->  nothing to assemble
		
		List<EntityAggregationInfo> entityAggrInfoList = new ArrayList<EntityAggregationInfo>(0);
		
		for(ContextRegistration reg:regList){
			if(reg.getListContextMetadata() == null) continue; //no metadata --> nothing to assemble
			for(ContextMetadata metadata:reg.getListContextMetadata()){
				if(metadata.getType().toString().equals(SOURCE_INFORMATION_TYPENAME))
				{
					/*
					 * metadata is on source information; now we need to figure out whether
					 * the source information type is org.fiware.org.sourceinformation.aggregation
					 */
					
					SourceInformation sI;
					try{
					sI = SourceInfoContextMetadata.getValueAsSourceInfo(metadata);
					} catch(Exception e){
						logger.info("problem interpreting metadata "
								+ "as source information:\n" + metadata.toString());
						logger.info("Message:"+e.getMessage());
						continue; 
					}
					
					if(!sI.getSourceType().toString().equals(AGGREGATION_TYPENAME))
						continue; //wrong source information type
					
					/*
					 * Yeah, now we found in a piece of metadata source information
					 * of type aggregation info. We add the aggregation info
					 * to our list.
					 */
					
					EntityAggregationInfo entAggInfo;
					try{
					entAggInfo = AggregationSourceInformation.getValueAsAggrInfo(sI);
					} catch (Exception e){
						logger.info("problem interpreting source information "
								+ "as aggregation info:\n" + sI.toString());
						logger.info("Message:"+e.getMessage());
						continue; 
					}
					
					entityAggrInfoList.add(entAggInfo);			
				}							
				
			} //end of iteration over metadata
						
		} //end of iteration over registrations
		
		if(entityAggrInfoList.isEmpty()) return null; //again, nothing there to aggregate means nothing to do.
		
		/*
		 * Now that we found some entity aggregation information, we create the 
		 * aggregator objects that do the actual aggregation.
		 */		
		List<EntityAggregator> entAggList = new ArrayList<EntityAggregator>(entityAggrInfoList.size());		
		for(EntityAggregationInfo entAggInfo:entityAggrInfoList)
			try {
				entAggList.add(new EntityAggregator(entAggInfo,req.getEntityIdList(),req.getAttributeList()));
			} catch (NotRequestedException e1) {
				//Aggregation information seems not relevant for what was queried; 
				//do nothing.
			}
		
		/*
		 * And now, having the objects that do the aggregation, we need to retrieve 
		 * the source entity information. 
		 */
		
		/*
		 * Here we collect the tasks that will submit queries
		 * to the IoT Broker core
		 */
		List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
		
		/*
		 * And here we collect all the responses AFTER the aggregation process.
		 */
		final List<ContextElementResponse> responseList = new ArrayList<ContextElementResponse>();
		
		/*
		 * We define a class for runnable objects that will
		 * - query the IoT Broker core on behalf of the aggregator object
		 * - submit the result to the aggregator object
		 * - fetch the aggregated information from the object and put it
		 *   into the response list. 
		 */		
		class aggregationThread implements Runnable{
			
			EntityAggregator entAgg;

			public aggregationThread(EntityAggregator entAgg)
			{
				this.entAgg = entAgg;
			}
			
			@Override
			public void run() {

				/*
				 * Get from the entityAggregator the entities and 
				 * attributes that need to be queried
				 */			
				List<EntityId> queryEntityList = entAgg.getSourceEntities();
				List<String> queryAttribList = entAgg.getSourceAttributes();
				
				/*
				 * Formulate a query context request to submit back to the
				 * IoT Broker Core
				 */
				QueryContextRequest queryReq = new QueryContextRequest(queryEntityList, queryAttribList, null);
				
				/*
				 * submit the request to the core & get response
				 */
				logger.info("submitting query for aggregation source to IoT Broker core");
				
				QueryContextResponse queryResp = 				
				iotBrokerCore.queryContext(queryReq);
				
				logger.info("response received from IoT Broker core:\n"+queryResp.toString());
				
				if( queryResp == null ||
						queryResp.getListContextElementResponse() == null)
					return; //no useful response --> nothing to aggregate
				
				logger.info("putting the response into the aggregator");
				/*
				 * give the response to the aggregator
				 */				
				entAgg.putData(queryResp.getListContextElementResponse());
				
				/*
				 * get the aggregated data form aggregator
				 */
				ContextElementResponse aggResp = entAgg.getAggregate();
				
				logger.info("getting aggregated response:\n"+aggResp.toString());
				
				if(aggResp == null) return; //nothing aggregated --> no more to do
				
				/*
				 * add the aggregated data to the response list (which is
				 * shared among all threads)
				 */
				synchronized(responseList){
					responseList.add(aggResp);					
				}
				
			}
			
		}
		
		/*
		 * We create such a thread for each aggregator
		 */
		for(EntityAggregator entAgg:entAggList){
			tasks.add(Executors.callable(new aggregationThread(entAgg)));						
		}
		
		/*
		 * And then we run them all.
		 */
		try{
		taskExecutor.invokeAll(tasks);
		}
		catch(InterruptedException e)
		{
			logger.info("Task Execution error");
			logger.error(e);
			return null;
		}			

		logger.info("final response list of aggregator:\n"+responseList.toString() );
		
		
		/*
		 * After the threads have populated the response list, we return it.
		 */				
		return new QueryContextResponse(responseList,null);

	}
}
