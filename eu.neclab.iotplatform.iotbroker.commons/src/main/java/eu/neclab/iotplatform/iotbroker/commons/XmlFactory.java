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

package eu.neclab.iotplatform.iotbroker.commons;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.xml.bind.marshaller.DataWriter;

/**
 * Contains methods to convert XML Strings to Objects and back. Instances of
 * this class are stateless.
 *
 */
public class XmlFactory {

	private static Logger logger = Logger.getLogger(XmlFactory.class);

	/**
	 * ------------------ JAXB OPERATIONs----------------------------------
	 */

	/**
	 * Converts a given Object of a given class
	 * to XML String using JAXB (Marshaller).
	 *
	 * @param source The object to convert.
	 *
	 * @param type The class of the object to convert.
	 *
	 * @return The XML String representing the Object.
	 *
	 */
	public static String convertToXml(Object source, Class<?> type) {
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
			logger.info("JAXBException",e);
		}

		return result;
	}

	/**
	 * Converts a given XML String into an
	 * Object using JAXB (Unmarshaller).
	 *
	 * @param xml The XML String to convert.
	 * @param type The type of the object to return.
	 *
	 * @return The result of the conversion.
	 *
	 */
	public static Object convertStringToXml(String xml, Class<?> type) {

		Object response = null;

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(type);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			StringReader reader = new StringReader(xml);
			response = unmarshaller.unmarshal(reader);

		} catch (JAXBException e) {
			logger.info("JAXBException, caused by XML:"+xml,e);
		}
		return response;

	}
	
	/**
	 * Converts an XML String from a specified file into an
	 * Object using JAXB (Unmarshaller).
	 *
	 * @param path The path to the xml file
	 * @param type The type of the object to return.
	 *
	 * @return The result of the conversion.
	 *
	 */
	public static Object convertFileToXML(String path, Class<?> type){
		
		//read String from File
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(Paths.get(path));
		} catch (IOException e) {			
			throw new RuntimeException("Error Reading from File");
		}
		  String s = new String(encoded);
		  
		  return convertStringToXml(s, type);
		
	}

	/**
	 * This converts an XML String into a Document instance.
	 *
	 * @param xml The XML String to convert.
	 *
	 * @return The resulting Document instance.
	 *
	 */
	public static Document stringToDocument(String xml) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc = null;
		try {
			db = factory.newDocumentBuilder();
			InputSource inStream = new InputSource();
			inStream.setCharacterStream(new StringReader(xml));
			doc = db.parse(inStream);
		} catch (ParserConfigurationException e) {

			logger.info("ParserConfigurationException",e);
		} catch (SAXException e) {

			logger.info("SAXException",e);
		} catch (IOException e) {

			logger.info("IOException",e);
		}

		return doc;
	}

}
