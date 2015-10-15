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
package eu.neclab.iotplatform.couchdb.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import eu.neclab.iotplatform.couchdb.http.Client;
import eu.neclab.iotplatform.couchdb.http.HttpRequester;
import eu.neclab.iotplatform.iotbroker.commons.FullHttpResponse;

public class CouchDBUtil {

	public final static String LATEST_VALUE_PREFIX = "entity";
	public final static String HISTORICAL_VALUE_PREFIX = "obs";
	public final static String PREFIX_TO_ID_SEPARATOR = "__";
	public final static String ENTITY_TO_TYPE_SEPARATOR = "~";
	public final static String ID_TO_ATTRIBUTENAME_SEPARATOR = ":::";
	public final static String DOCUMENT_TO_TIMESTAMP_SEPARATOR = "|";

	/** The logger. */
	private static Logger logger = Logger.getLogger(CouchDBUtil.class);
	
	private final HttpRequester httpRequester;
	
	public CouchDBUtil(){
		httpRequester = new HttpRequester();
	}
	
	public CouchDBUtil(HttpRequester httpRequester){
		this.httpRequester = httpRequester;
	}

	/**
	 * Function that checks if the DB exist otherwise it will call a function
	 * that create the DB inside CouchDB
	 * 
	 * @throws MalformedURLException
	 */
	public boolean checkDB(String couchDB_IP, String couchDB_NAME,
			String authentication) throws MalformedURLException {

		boolean resp = false;

		String response = Client.sendRequest(new URL(couchDB_IP + "_all_dbs"),
				"GET", null, "application/json", authentication);

		logger.info(" Response from CouchDB -----> " + response);

		if (response.contains(couchDB_NAME)) {

			logger.info("Database " + couchDB_NAME + " already exist!");
			resp = true;
		}

		return resp;

	}

	/**
	 * Function that create the DB inside CouchDB
	 * 
	 * @throws MalformedURLException
	 */
	public String createDb(String couchDB_IP, String couchDB_NAME,
			String authentication) throws MalformedURLException {
		String response = null;

		response = Client.sendRequest(new URL(couchDB_IP + couchDB_NAME),
				"PUT", "", "application/json", authentication);

		logger.debug("Response to create_db:" + response);

		return response;
	}

	public String formatDate(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'%20'HH:mm:ss.SSS");

		return dateFormat.format(date);
	}

	public void checkViews(String couchDB_IP, String couchDB_NAME) {

		for (View view : View.values()) {

			try {
				URL url = new URL(couchDB_IP + couchDB_NAME + "/"
						+ view.getPath());

				// Check if the view is there
				FullHttpResponse response = httpRequester.sendGet(url);

				// Store view if not present in the database
				if (response.getStatusLine().getStatusCode() == 404) {
					logger.info("View missing: " + view.getPath());

					String jsView = view.getReadyToStoreView();

					httpRequester.sendPut(url, jsView, "application/json");
				}

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
