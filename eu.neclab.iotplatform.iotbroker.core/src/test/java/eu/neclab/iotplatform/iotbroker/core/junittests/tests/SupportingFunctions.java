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
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.sun.xml.bind.marshaller.DataWriter;

import eu.neclab.iotplatform.iotbroker.commons.JaxbCharacterEscapeHandler;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextResponse;

/**
 *  Note: class is experimental and currently not used in any unit tests.
 */
public class SupportingFunctions {
	private BufferedReader br;
	/** The logger. */
	private static Logger logger = Logger.getLogger(SupportingFunctions.class);
	public String readFromFile(String path){
		FileInputStream fstream = null;

		try {
			String url = getClass().getResource(path).getFile();
			fstream = new FileInputStream(url);
			DataInputStream in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			String strLine="";
			String tmp="";
			//Read File Line By Line
			while ((tmp = br.readLine()) != null) {

				strLine=strLine+tmp;

			}
			fstream.close();
			return strLine;
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());

		} catch (IOException e) {

			logger.error(e.getMessage());
		}

		return "";

	}
	public Object convertStringToXml(String xml, Class type) {

		Object response = null;

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(type);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			StringReader reader = new StringReader(xml);
			response = unmarshaller.unmarshal(reader);

		} catch (JAXBException e) {
			logger.error("JAXBException:"+e.getMessage());
		}
		return response;

	}
	public String convertToXml(Object source, Class type) {
		String result = null;
		DataWriter dataWriter;
		try {
			JAXBContext carContext = JAXBContext.newInstance(type);
			Marshaller carMarshaller = carContext.createMarshaller();
			carMarshaller.setProperty(Marshaller.JAXB_ENCODING, "Unicode");
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			dataWriter = new DataWriter(printWriter, "UTF-8",
					new JaxbCharacterEscapeHandler());
			// Perform Marshalling operation
			carMarshaller.marshal(source, dataWriter);
			result = stringWriter.toString();
		} catch (JAXBException e) {

			logger.error("JAXBException:"+e.getMessage());
		}


		return result;
	}
	public QueryContextRequest prepareQueryContextRequest(String path) {

		QueryContextRequest queryContextRequest = (QueryContextRequest)
				convertStringToXml(readFromFile(path),
						QueryContextRequest.class);
		return queryContextRequest;
	}

	public QueryContextResponse prepareQueryContextResponse(String path) {

		QueryContextResponse queryContextResponse = (QueryContextResponse)
				convertStringToXml(readFromFile(path),
						QueryContextResponse.class);
		return queryContextResponse;
	}

	public List<QueryContextResponse> prepareQueryContextResponseList(
			String path) {

		convertStringToXml(readFromFile(path),
				QueryContextResponse.class);
		List<QueryContextResponse> lstQueryContextResponse = new LinkedList<QueryContextResponse>();
		return lstQueryContextResponse;
	}

	public DiscoverContextAvailabilityRequest prepareDiscoverContextAvailabilityRequest(
			String path) {
		DiscoverContextAvailabilityRequest discoverContextAvailabilityRequest = (DiscoverContextAvailabilityRequest)convertStringToXml(readFromFile(path),
				DiscoverContextAvailabilityRequest.class);
		return discoverContextAvailabilityRequest;
	}

	public DiscoverContextAvailabilityResponse prepareDiscoverContextAvailabilityResponse(
			String path) {
		String tmp=readFromFile(path);
		DiscoverContextAvailabilityResponse discoverContextAvailabilityResponse = (DiscoverContextAvailabilityResponse)convertStringToXml(tmp,DiscoverContextAvailabilityResponse.class);

		LinkedList<String> lstValue = getAssociationDataFromRegistrationMetaData(tmp);
		discoverContextAvailabilityResponse = (DiscoverContextAvailabilityResponse)convertStringToXml(tmp,	DiscoverContextAvailabilityResponse.class);
		discoverContextAvailabilityResponse = addingAssociatinDataToDiscContextAvailabilityRes(discoverContextAvailabilityResponse, lstValue);
		return discoverContextAvailabilityResponse;
	}
	public UpdateContextResponse prepareUpdateContextResponse(
			String path) {
		UpdateContextResponse updateContextResponse = (UpdateContextResponse)convertStringToXml(readFromFile(path),
				UpdateContextResponse.class);


		return updateContextResponse;
	}
	public UpdateContextRequest prepareUpdateContextRequest(
			String path) {
		UpdateContextRequest updateContextResquest = (UpdateContextRequest)convertStringToXml(readFromFile(path),
				UpdateContextRequest.class);


		return updateContextResquest;
	}
	public Object chkIfNull(Object o1){
		if(o1.equals(null)){
			return null;
		}else{
			return o1;
		}

	}
	private LinkedList<String> getAssociationDataFromRegistrationMetaData(
			String response) {
		LinkedList<String> lstValue = null;
		lstValue = new LinkedList<String>();
		int counter = 0;
		int length = response.length();
		while (counter <= length) {
			logger.debug("Counter: " + counter + " Length: " + length);
			int s = response.indexOf("<registrationMetaData>");
			int e = response.indexOf("</registrationMetaData>");

			if (s == -1) {
				break;
			}
			String regMetaData = response.substring(s, e);
			logger.debug("s: " + s + " e: " + e + " regMetaData: "
					+ regMetaData);
			if (regMetaData.contains("Association")) {
				int vs = regMetaData.indexOf("<value>");
				int ve = regMetaData.indexOf("</value>");
				String value = regMetaData.substring(vs + 7, ve);

				logger.debug("vs: " + vs + " ve: " + ve + " value: " + value);
				value = value.replaceAll("\t", "");

				value = value.replaceAll("\n", "");

				logger.info(value);
				value = value.replaceAll("    ", "");

				value = value.replaceAll("\r", "");
				value = value.trim();

				lstValue.add(value);
				logger.debug(value);

			}
			counter = counter + e + 12;

		}
		return lstValue;
	}

	private DiscoverContextAvailabilityResponse addingAssociatinDataToDiscContextAvailabilityRes(
			DiscoverContextAvailabilityResponse resp, List<String> lstValue) {

		int count = 0;

		if (lstValue.size() > 0) {

			DiscoverContextAvailabilityResponse dcaRes = resp;
			List<ContextRegistrationResponse> lstCRegRes = dcaRes
					.getContextRegistrationResponse();
			for (ContextRegistrationResponse cRegRes : lstCRegRes) {
				List<ContextMetadata> lstCMetaData = cRegRes
						.getContextRegistration().getListContextMetadata();

				for (ContextMetadata cMetaData : lstCMetaData) {
					if (cMetaData.getType().toString().equals("Association")) {
						if (count <= lstValue.size()) {
							cMetaData.setValue(lstValue.get(count));
							try {
								cMetaData.setValue(new String(lstValue.get(
										count).getBytes("US-ASCII")));
							} catch (UnsupportedEncodingException e) {
								logger.debug("Unsupported Encoding Exception",
										e);
							}
							logger.debug(cMetaData.toString());
							count++;
						}
					}
				}
			}

		}

		return resp;
	}
}
