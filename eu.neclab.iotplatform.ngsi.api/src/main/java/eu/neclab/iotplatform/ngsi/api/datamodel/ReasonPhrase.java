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

/**\
 * Enumeration of error reason phrases.
 *
 */
public enum ReasonPhrase {

	OK_200("OK"), BADREQUEST_400("BAD REQUEST"), FORBIDDEN_403("FORBIDDEN"), CONTEXTELEMENTNOTFOUND_404(
			"CONTEXT ELEMENT NOT FOUND"), SUBSCRIPTIONIDNOTFOUND_470(
			"SUBSCRIPTION ID NOT FOUND"), MISSINGPARAMETER_471(
			"MISSING PARAMETER"), INVALIDPARAMETER_472("INVALID PARAMETER"), ERRORINMETADATA_473(
			"ERROR IN METADATA"), REGULAREXPRESSIONFORENTITYIDNOTALLOWED_480(
			"REGULAR EXPRESSION FOR ENTITYID NOT ALLOWED"), ENTITYTYPEREQUIRED_481(
			"ENTITY TYPE REQUIRED"), ATTRIBUTELISTREQUIRED_482(
			"ATTRIBUTE LIST REQUIRED"), RECEIVERINTERNALERROR_500(
			"RECEIVER INTERNAL ERROR");

	private final String reasonPhrase;

	private ReasonPhrase(String toString) {
		this.reasonPhrase = toString;
	}

	public String toString() {
		return reasonPhrase;
	}

}
