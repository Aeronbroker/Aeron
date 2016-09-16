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


import static org.junit.Assert.*;

import java.io.File;

import org.apache.log4j.Logger;
import org.easymock.classextension.EasyMock;
import org.junit.*;
import org.springframework.http.ResponseEntity;

import eu.neclab.iotplatform.iotbroker.commons.XmlFactory;
import eu.neclab.iotplatform.iotbroker.restcontroller.RestProviderController;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElementResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextResponse;
import eu.neclab.iotplatform.ngsi.api.ngsi10.Ngsi10Interface;

public class TestFunctions {
	
	/*
	 * The Rest interface to test
	 */
	RestProviderController restContr;
	Ngsi10Interface coreMock;
	
	//initialize logger
	private static Logger logger = Logger
			.getLogger("Unit Tests");
	
	@Before
	public void setUp()
	{

		
		
		//initialize rest interface to test
		restContr = new RestProviderController();
		
		//create mock for IoT Broker Core
		coreMock = EasyMock.createMock(Ngsi10Interface.class);
		
		//assign the mocked core to the rest interface
		restContr.setNgsiCore(coreMock);
	}
	
	@Test
	public void IdBasedGet(){
		
		
		//used for debugging the test
//		File f = new File("src/test/resources/queryContextRequest-IdBased.xml");
//		System.out.println("file found:"+f.exists());
		
		//create requests and responses from files
		QueryContextRequest expectedReq =
				(QueryContextRequest) 
				(new XmlFactory()).convertFileToXML("src/test/resources/queryContextRequest-IdBased.xml", QueryContextRequest.class);
		
		QueryContextResponse resp = 
				(QueryContextResponse)
				(new XmlFactory()).convertFileToXML("src/test/resources/queryContextResponse-IdBased.xml", QueryContextResponse.class);
		
		//define behavior of IoT Broker core mock
		EasyMock.expect(coreMock.queryContext(expectedReq)).andReturn(resp);	
		EasyMock.replay(coreMock);
		
				
		ResponseEntity<ContextElementResponse> receivedResp = restContr.simpleQueryIdGet("room1");
		
		EasyMock.verify(coreMock);
		
		assertEquals(receivedResp.getBody(), resp.getListContextElementResponse().get(0));
		
		logger.info("Successfully tested feature: FIWARE.Feature.IoT.BackendThingsManagement.Query.IdBased");		
	}

}
