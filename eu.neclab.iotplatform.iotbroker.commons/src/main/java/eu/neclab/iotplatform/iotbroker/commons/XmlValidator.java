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

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.util.JAXBSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * A class for validating objects against XML schemas. Instances of this
 * class are state-less.
 *
 */
public class XmlValidator {

	private static Logger logger = Logger.getLogger(XmlValidator.class);

	private int errorCount = 0;
	private boolean error = false;

	/**
	 * 
	 * @param request
	 *            The request to be validated.
	 * @param XML_schema
	 *            Path to the xml schema file against which the request is to be
	 *            validated.
	 * @return Returns true if there is an error in the request.
	 */
	public boolean xmlValidation(Object request, String xmlSchema) {

		try {

			// create JAXB context from the class of the request
			JAXBContext jc = JAXBContext.newInstance(request.getClass());

			// create JAXB source from the newly created context and the
			// request.
			JAXBSource source = new JAXBSource(jc, request);

			// create schema factory
			SchemaFactory sf = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			// creates a Java representation of the schema file
			Schema schema = sf.newSchema(new File(xmlSchema));

			// creates a validator from the schema
			Validator validator = schema.newValidator();

			// assign a new instance of MyErrorHandler to the validator.
			validator.setErrorHandler(new MyErrorHandler());

			// now finally validate.
			validator.validate(source);

			if (errorCount > 0) {
				error = true;
				errorCount = 0;

				return error;
			} else {
				error = false;
			}
		} catch (Exception e) {
			// catching all validation exceptions
			logger.info("Exception: ",e);
			error = true;
		} finally {
			errorCount = 0;
		}

		return error;

	}

	private class MyErrorHandler implements ErrorHandler {

		@Override
		public void warning(SAXParseException exception) throws SAXException {
			logger.info("\nWARNING",exception);
			
		}

		@Override
		public void error(SAXParseException exception) throws SAXException {
			logger.info("\nERROR",exception);
			errorCount++;
	
		}

		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			logger.info("\nFATAL ERROR",exception);
			errorCount++;
			
		}

	}

}
