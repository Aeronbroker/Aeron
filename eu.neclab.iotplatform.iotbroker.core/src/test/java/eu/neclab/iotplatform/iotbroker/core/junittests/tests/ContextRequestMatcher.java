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

package eu.neclab.iotplatform.iotbroker.core.junittests.tests;

import java.util.List;

import org.apache.log4j.Logger;
import org.easymock.IArgumentMatcher;

import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.OperationScope;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextRequest;

/**
 *  Note: class is experimental and currently not used in any unit tests.
 */
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
		expectedDCA = expected;
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
		if(!(actualQCReq.getRestriction()==null)&&!(expectedQCReq.getRestriction()==null))
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


		}else if (!(actualQCReq.getRestriction()==null)&&expectedQCReq.getRestriction()==null){
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
		if (!(actualDCARequest.getRestriction() == null)
				&& !(expectedDCA.getRestriction() == null)) {
			if(expectedDCA.getRestriction().getAttributeExpression()!=null){
				if (!actualDCARequest
						.getRestriction()
						.getAttributeExpression()
						.equals(expectedDCA.getRestriction()
								.getAttributeExpression())) {
					errMessage = errMessage + "Attribute Expression do not match";
					return false;
				}
			}else if(expectedDCA.getRestriction().getAttributeExpression()==null&&actualDCARequest.getRestriction().getAttributeExpression()!=null){
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
		} else if (!(actualDCARequest.getRestriction() == null)
				&& expectedDCA.getRestriction() == null) {
			errMessage = errMessage + "Restriction is missing";
			return false;
		}

		return true;
	}
	private boolean updateContextRequestMatcher(UpdateContextRequest actualQCReq){

		return true;
	}


}
