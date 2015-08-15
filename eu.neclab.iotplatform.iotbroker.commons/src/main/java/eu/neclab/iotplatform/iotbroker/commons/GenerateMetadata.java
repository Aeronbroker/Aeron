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
package eu.neclab.iotplatform.iotbroker.commons;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;

public class GenerateMetadata {

	/**
	 * Creates the domain timestamp metadata.
	 *
	 * @return the context metadata
	 * @throws URISyntaxException
	 *             the uRI syntax exception
	 */
	public static ContextMetadata createDomainTimestampMetadata()
			throws URISyntaxException {

		// Define Timestamp Metadata
		ContextMetadata timestampMetadata = new ContextMetadata();
		Date date = new Date();
		DateFormat mISO8601Local = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String formattedDate = mISO8601Local.format(date);

		timestampMetadata.setName("TimeStamp");
		timestampMetadata.setType(new URI("ISO8601"));
		timestampMetadata.setValue(formattedDate);

		return timestampMetadata;

	}

	/**
	 * Creates the source ip metadata.
	 *
	 * @param url
	 *            the url
	 * @return the context metadata
	 * @throws URISyntaxException
	 *             the uRI syntax exception
	 */
	public static ContextMetadata createSourceIPMetadata(URI url)
			throws URISyntaxException {
		// Define SourceIP Metadata
		ContextMetadata sourceIP = new ContextMetadata();

		sourceIP.setName("SourceIP");

		sourceIP.setType(new URI(
				"http://www.w3.org/TR/webarch/#URI-registration"));

		sourceIP.setValue(url.toString());
		return sourceIP;
	}

}
