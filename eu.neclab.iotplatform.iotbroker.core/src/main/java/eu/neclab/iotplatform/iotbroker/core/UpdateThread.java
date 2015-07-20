/*******************************************************************************
 * Copyright (c) 2015, NEC Europe Ltd.
 * All rights reserved.
 * 
 * Authors:
 *          * Salvatore Longo - salvatore.longo@neclab.eu
 *          * Tobias Jacobs - tobias.jacobs@neclab.eu
 *          * Flavio Cirillo - flavio.cirillo@neclab.eu
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
package eu.neclab.iotplatform.iotbroker.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextResponse;
import eu.neclab.iotplatform.ngsi.api.ngsi10.Ngsi10Requester;

/**
 *  Represents a Thread whose purpose is to send an NGSI 10 UpdateContext
 *  message to an NGSI 10 server.
 *
 */
public class UpdateThread implements Callable<UpdateContextResponse> {

	private static Logger logger = Logger.getLogger(UpdateThread.class);
	private Ngsi10Requester ngsi10Requester;
	private UpdateContextRequest updateRequest;
	private String pubSub;

	/**
	 * Instantiates a shallow object of that class without the
	 * necessary information to run included.
	 */
	public UpdateThread() {
		super();
	}

	/**
	 * Instantiates an object of this class with all necessary
	 * information to execute the Thread.
	 *
	 * @param updateRequest
	 *  The request message of the NGSI 10 UpdateContext operation.
	 * @param pub_sub The address of the NGSI 10 Server to send the message to.
	 */
	public UpdateThread(UpdateContextRequest updateRequest, String pub_sub) {
		super();
		this.updateRequest = updateRequest;
		pubSub = pub_sub;
	}



	public Ngsi10Requester getNgsi10Requester() {
		return ngsi10Requester;
	}

	public void setNgsi10Requester(Ngsi10Requester ngsi10Requester) {
		this.ngsi10Requester = ngsi10Requester;
	}

	/**
	 * Starts the Thread.
	 */
	@Override
	public UpdateContextResponse call() {


		UpdateContextResponse resp = null;

		try {

			resp = ngsi10Requester
					.updateContext(updateRequest, new URI(pubSub));

			logger.info("RESPONSE from pub/sub:" + resp.toString());

		} catch (URISyntaxException e) {
			logger.debug("URI Syntax Error",e);
		}

		return resp;
	}

}
