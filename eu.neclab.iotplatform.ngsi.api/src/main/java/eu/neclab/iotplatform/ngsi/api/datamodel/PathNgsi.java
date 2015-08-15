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
package eu.neclab.iotplatform.ngsi.api.datamodel;

/**
 * Enumeration for encoding under which path which resource
 * of the FI-WARE RESTful binding is available.
 *
 */
public enum PathNgsi {

	NGSI10_QUERYCONTEXT("/ngsi10/queryContex"), NGSI10_SUBSCRIBECONTEXT(
			"/ngsi10/subscribeContext"), NGSI10_UPDATECONTEXTSUBSCRIPTION(
			"/ngsi10/updateContextSubscription"), NGSI10_UNSUBSCRIBECONTEXT(
			"/ngsi10/unsubscribeContext"), NGSI10_UPDATECONTEXT(
			"/ngsi10/updateContext"), NGSI10_CONTEXTENTITY(
			"/ngsi10/contextEntities/"), NGSI10_ATTRIBUTES("/attributes"), NGSI10_ATTRIBUTEDOMAIN(
			"/attributeDomains"), NGSI10_CONTEXTENTITYTYPE(
			"/ngsi10/contextEntityTypes"), NGSI10_CONTEXTSUBSCRIPTION(
			"/ngsi10/contextSubscriptions"), NGSI10_NOTIFY("/ngsi10/notify"), NGSI9_NOTIFY(
			"/ngsi9/notify");

	private final String path;

	private PathNgsi(String path) {
		this.path = path;
	}

	public String getNgsiPath() {
		return path;
	}

}
