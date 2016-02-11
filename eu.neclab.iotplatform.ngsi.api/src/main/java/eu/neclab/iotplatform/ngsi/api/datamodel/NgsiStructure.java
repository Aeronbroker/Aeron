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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * Common super-type for NGSI data structure implementations.
 */
public abstract class NgsiStructure {

	/** The logger. */
	private static Logger logger = Logger.getLogger(NgsiStructure.class);

	@Override
	public String toString() {

		String result;
		StringWriter sw = new StringWriter();
		try {
			JAXBContext carContext = JAXBContext.newInstance(this.getClass());
			Marshaller carMarshaller = carContext.createMarshaller();
			carMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			carMarshaller.marshal(this, sw);
			result = sw.toString();
//			String result1 = result;

			result = result.replaceAll("<value.*xsi:type=\"xs:string\".*?>",
					"<value>");

			result = result.replaceAll(
					"<scopeValue.*xsi:type=\"xs:string\".*?>", "<scopeValue>");
			
			
			result = replaceObjectValues("<value[^>]*xsi:type=.*?>(.+?)</value>?", ".*xsi:type=\"([^\"]*)\"", "value", result);
			result = replaceObjectValues("<scopeValue[^>]*xsi:type=.*?>(.+?)</scopeValue>", ".*xsi:type=\"([^\"]*)\"", "scopeValue", result);


			result = result.replaceAll("&lt;", "<");
			result = result.replaceAll("&gt;", ">");
			
			result = formatXmlString(result);



		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}

		return result;

	}

	
	private String replaceObjectValues(String valuePatternRegex, String valueTypePatternRegex, String wrappingValueTag, String strings){
		String string = new String(strings.replace("\n", ""));

		Pattern pattern = Pattern.compile(valuePatternRegex);

		Pattern valueTypePattern = Pattern.compile(valueTypePatternRegex);

		Matcher matcher = pattern.matcher(string);
		String value = null;
		String newString = string;
		while (matcher.find()) {
			value = matcher.group(0);

			Matcher valueTypeMatcher = valueTypePattern.matcher(value);
			if (valueTypeMatcher.find()) {

				String newValue = "<"+wrappingValueTag+"><" + valueTypeMatcher.group(1) + ">"
						+ matcher.group(1) + "</" + valueTypeMatcher.group(1)
						+ "></"+wrappingValueTag+">";				
				newString = newString.replace(value,newValue);

			}
		}
		return newString;
	}
	
	private String formatXmlString(String string){
		String newString = string.replaceAll("> *<","><");
		
		Source xmlInput = new StreamSource(new StringReader(newString));
        StringWriter stringWriter = new StringWriter();
        StreamResult xmlOutput = new StreamResult(stringWriter);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", 2);
        Transformer transformer;
        String formatted = null;
		try {
			transformer = transformerFactory.newTransformer();
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.transform(xmlInput, xmlOutput);
	        formatted = xmlOutput.getWriter().toString();
//	        System.out.println(formatted);
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return (formatted != null) ? formatted : string;
	}
	

	public String toJsonString() {

		ObjectMapper mapper = new ObjectMapper();
		SerializationConfig config1 = mapper.getSerializationConfig();
		config1.setSerializationInclusion(Inclusion.NON_NULL);

		String jsonString = "";

		try {
			// logger.info("----------------->"
			// + mapper.writeValueAsString(this));

			jsonString = mapper.writeValueAsString(this);
		} catch (JsonGenerationException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (JsonMappingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		return jsonString;

	}

	public static Object convertStringToXml(String xml, Class<?> type) {

		if (type.getSuperclass() != NgsiStructure.class) {
			throw new RuntimeException("Cannot convert String to "
					+ type.getName());
		}

		Object response = null;

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(type);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			StringReader reader = new StringReader(xml);
			response = unmarshaller.unmarshal(reader);

		} catch (JAXBException e) {
			logger.info("JAXBException", e);
		}

		return response;

	}

	public static Object parseStringToJson(String json, Class<?> clazz) {
		ObjectMapper mapper = new ObjectMapper();
		Object object = null;

		mapper.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		try {
			object = mapper.readValue(json, clazz);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
}
