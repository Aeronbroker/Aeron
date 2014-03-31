package eu.neclab.iotplatform.iotbroker.core.junittests.tests;

import java.util.List;

import org.apache.log4j.Logger;
import org.easymock.IArgumentMatcher;

import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.OperationScope;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextRequest;

public class ContextRequestMatcher implements IArgumentMatcher {
	/** The logger. */
	private static Logger logger = Logger.getLogger(ContextRequestMatcher.class);
	DiscoverContextAvailabilityRequest expectedDCA;
	QueryContextRequest expectedQCReq;
	UpdateContextRequest expectedUCReq;
	String errMessage = "";
	SupportingFunctions sf = new SupportingFunctions();

	public ContextRequestMatcher(DiscoverContextAvailabilityRequest expected) {
		super();
		this.expectedDCA = expected;
	}

	public ContextRequestMatcher(QueryContextRequest expectedQCReq) {
		super();
		this.expectedQCReq = expectedQCReq;
	}
	public ContextRequestMatcher(UpdateContextRequest expectedUCReq) {
		super();
		this.expectedUCReq = expectedUCReq;
	}
	

	@Override
	public boolean matches(Object actual) {

		if (actual instanceof DiscoverContextAvailabilityRequest) {
			return discoverContextAvailabilityRequestMatcher((DiscoverContextAvailabilityRequest) actual);
		}
		if (actual instanceof QueryContextRequest) {
			return queryContextRequestMatcher((QueryContextRequest) actual);
		}
		if (actual instanceof UpdateContextRequest) {
			return updateContextRequestMatcher((UpdateContextRequest) actual);
		}
		return true;
	}

	@Override
	public void appendTo(StringBuffer buffer) {
	
		buffer.append(errMessage);

	}

	private boolean queryContextRequestMatcher(QueryContextRequest actualQCReq){
		if(!actualQCReq.getEntityIdList().equals(expectedQCReq.getEntityIdList())){
			errMessage=errMessage+"List of EntityId do not match";
			return false;
		}
		if(!actualQCReq.getAttributeList().equals(expectedQCReq.getAttributeList())){
			errMessage=errMessage+"List of Attribute do not match";
			return false;
		}
		if((!(actualQCReq.getRestriction()==null))&&(!(expectedQCReq.getRestriction()==null)))
		{
			if(!actualQCReq.getRestriction().getAttributeExpression().equals(expectedQCReq.getRestriction().getAttributeExpression())){
				errMessage=errMessage+"Attribute Expression do not match";
				return false;
			}
			List<OperationScope> lstActualOperationScope=actualQCReq.getRestriction().getOperationScope();
			
			List<OperationScope> lstExpectedOperationScope=expectedQCReq.getRestriction().getOperationScope();
			
			for(OperationScope actualOperationScope:lstActualOperationScope){
					if(lstExpectedOperationScope.contains(actualOperationScope)){
						for(OperationScope expectedOperationScope:lstExpectedOperationScope)
						{
							if(actualOperationScope.equals(expectedOperationScope))
							{
							
								if(!actualOperationScope.getScopeType().equals(expectedOperationScope.getScopeType()))
								{
									errMessage=errMessage+"ScopeType do not match";
									return false;
								}
								
								String actualScopeValue =(String)actualOperationScope.getScopeValue();
								String expectedScopeValue =(String)expectedOperationScope.getScopeValue();
								
								if(!expectedScopeValue.equals(actualScopeValue)){
									errMessage=errMessage+"ScopeValue do not match";
									return false;
								}
							}
						}			
					}else{
						errMessage=errMessage+"OperationScope do not match";
						return false;
			
					}
			}
			
			
		}else if ((!(actualQCReq.getRestriction()==null))&&((expectedQCReq.getRestriction()==null))){
			errMessage=errMessage+"Restriction is missing";
			return false;
		}
		return true;
	}

	private boolean discoverContextAvailabilityRequestMatcher(
			DiscoverContextAvailabilityRequest actualDCARequest) {

		if (!actualDCARequest.getEntityIdList().equals(
				expectedDCA.getEntityIdList())) {
			errMessage = errMessage + "List of EntityId do not match";
			return false;
		}
		if (!actualDCARequest.getAttributeList().equals(
				expectedDCA.getAttributeList())) {
			errMessage = errMessage + "List of Attribute do not match";
			return false;
		}
		if ((!(actualDCARequest.getRestriction() == null))
				&& (!(expectedDCA.getRestriction() == null))) {
			if(expectedDCA.getRestriction().getAttributeExpression()!=null){
				if (!actualDCARequest
						.getRestriction()
						.getAttributeExpression()
						.equals(expectedDCA.getRestriction()
								.getAttributeExpression())) {
					errMessage = errMessage + "Attribute Expression do not match";
					return false;
				}
			}else if((expectedDCA.getRestriction().getAttributeExpression()==null)&&((actualDCARequest.getRestriction().getAttributeExpression()!=null))){
				errMessage = errMessage + "Attribute Expression do not match";
				return false;
			}
			
			List<OperationScope> lstActualOperationScope = actualDCARequest
					.getRestriction().getOperationScope();

			List<OperationScope> lstExpectedOperationScope = expectedDCA
					.getRestriction().getOperationScope();

			for (OperationScope actualOperationScope : lstActualOperationScope) {

				for (OperationScope expectedOperationScope : lstExpectedOperationScope) {
					if (actualOperationScope.equals(expectedOperationScope)) {

						if (!actualOperationScope.getScopeType().equals(
								expectedOperationScope.getScopeType())) {
							errMessage = errMessage + "ScopeType do not match";
							return false;
						}

						String actualScopeValue = (String) actualOperationScope
								.getScopeValue();
						String expectedScopeValue = (String) expectedOperationScope
								.getScopeValue();

						if (!expectedScopeValue.equals(actualScopeValue)) {
							errMessage = errMessage + "ScopeValue do not match";
							return false;
						}
					}

				}

			}
		} else if ((!(actualDCARequest.getRestriction() == null))
				&& ((expectedDCA.getRestriction() == null))) {
			errMessage = errMessage + "Restriction is missing";
			return false;
		}

		return true;
	}
	private boolean updateContextRequestMatcher(UpdateContextRequest actualQCReq){
		
		return true;
	}


}
