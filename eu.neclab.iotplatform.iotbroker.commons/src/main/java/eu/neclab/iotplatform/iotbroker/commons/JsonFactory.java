/*******************************************************************************
 *   Copyright (c) 2014, NEC Europe Ltd.
 *   All rights reserved.
 *
 *   Authors:
 *           * Salvatore Longo - salvatore.longo@neclab.eu
 *           * Tobias Jacobs - tobias.jacobs@neclab.eu
 *           * Raihan Ul-Islam - raihan.ul-islam@neclab.eu
 *
 *    Redistribution and use in source and binary forms, with or without
 *    modification, are permitted provided that the following conditions are met:
 *   1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   3. All advertising materials mentioning features or use of this software
 *     must display the following acknowledgement:
 *     This product includes software developed by NEC Europe Ltd.
 *   4. Neither the name of the NEC nor the
 *     names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY NEC ''AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL NEC BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

package eu.neclab.iotplatform.iotbroker.commons;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * Tools to convert objects to JSON and back. The method specifications
 * are self-explanatory.
 */
public class JsonFactory {

	private static Logger logger = Logger.getLogger(JsonFactory.class);

	public String convertJsonToString(Object source, Class<?> type) {

		String response = null;

		ObjectMapper mapper = new ObjectMapper();
		SerializationConfig config = mapper.getSerializationConfig();
		config.setSerializationInclusion(Inclusion.NON_NULL);
		try {
			response = mapper.writeValueAsString(source);
			logger.info("Json Body"+ response);
		} catch (JsonGenerationException e) {
			logger.info("JsonGenerationException", e);
		} catch (JsonMappingException e) {
			logger.info("JsonMappingException", e);
		} catch (IOException e) {
			logger.info("IOException", e);
		}



		return response;

	}

	public Object convertStringToJsonObject(String xml, Class<?> type) {

		Object response = null;
		ObjectMapper mapper = new ObjectMapper();
		SerializationConfig config = mapper.getSerializationConfig();
		config.setSerializationInclusion(Inclusion.NON_NULL);
		try {
			response = mapper.readValue(xml, type);
		} catch (JsonGenerationException e) {
			logger.info("JsonGenerationException", e);
		} catch (JsonMappingException e) {
			logger.info("JsonMappingException", e);
		} catch (IOException e) {
			logger.info("IOException", e);
		}

		return response;

	}

}
