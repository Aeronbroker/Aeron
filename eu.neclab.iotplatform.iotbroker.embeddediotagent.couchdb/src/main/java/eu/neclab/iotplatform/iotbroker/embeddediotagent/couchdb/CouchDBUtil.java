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

package eu.neclab.iotplatform.iotbroker.embeddediotagent.couchdb;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.neclab.iotplatform.iotbroker.commons.FullHttpResponse;

public class CouchDBUtil {

	/** The logger. */
	private static Logger logger = Logger.getLogger(CouchDBUtil.class);

	public CouchDBUtil() {
	}


	/**
	 * Function that checks if the DB exist otherwise it will call a function
	 * that create the DB inside CouchDB
	 * 
	 * @throws MalformedURLException
	 */
	public static boolean checkDB(String couchDB_IP, String couchDB_NAME,
			String authentication) throws MalformedURLException {

		boolean resp = false;

		// String response = Client.sendRequest(new URL(couchDB_IP +
		// "_all_dbs"),
		// "GET", null, "application/json", authentication);

		FullHttpResponse response = HttpRequester.sendGet(new URL(couchDB_IP
				+ "_all_dbs"));

		if (response == null) {
			logger.error("No response from CouchDB!!!!");
			return false;
		}

		logger.info(" Response from CouchDB -----> " + response);

		if (response.getBody().contains(couchDB_NAME)) {

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
	public static boolean createDb(String couchDB_IP, String couchDB_NAME,
			String authentication) throws MalformedURLException {

		FullHttpResponse response;
		try {
			response = HttpRequester.sendPut(
					new URL(couchDB_IP + couchDB_NAME), "", "application/json");

			logger.debug("Response to create_db:" + response);
			if (response != null
					&& response.getStatusLine() != null
					&& (response.getStatusLine().getStatusCode() == 200 || response
							.getStatusLine().getStatusCode() == 201)) {
				return true;

			} else {
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;

		}
		
	}
	
	public static String parseRevisionFromCouchdbResponse(
			FullHttpResponse fullHttpResponse) {

		String responseBody = fullHttpResponse.getBody();

		JsonParser parser = new JsonParser();
		JsonObject o = (JsonObject) parser.parse(responseBody);

		return o.get("rev").getAsString().replaceAll("\"+", "");
	}

	/**
	 * Function that delete the DB inside CouchDB
	 * 
	 * @throws MalformedURLException
	 */
	public static boolean deleteDb(String couchDB_IP, String couchDB_NAME,
			String authentication) throws MalformedURLException {

		FullHttpResponse response;
		try {

			response = HttpRequester.sendDelete(new URL(couchDB_IP
					+ couchDB_NAME));

			logger.debug("Response to deleteDb:" + response);

			if (response != null
					&& response.getStatusLine() != null
					&& (response.getStatusLine().getStatusCode() == 200 || response
							.getStatusLine().getStatusCode() == 204)) {
				return true;

			} else {
				return false;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	public static void checkViews(String couchDB_IP, String couchDB_NAME) {

		for (View view : View.values()) {

			try {
				URL url = new URL(couchDB_IP + couchDB_NAME + "/"
						+ view.getPath());

				// Check if the view is there
				FullHttpResponse response = HttpRequester.sendGet(url);

				if (response == null) {
					logger.error("CouchDB server is not reachable!!!!");
				} else {

					// Store view if not present in the database
					if (response.getStatusLine().getStatusCode() == 404) {
						logger.info("View missing: " + view.getPath());

						String jsView = view.getReadyToStoreView();

						HttpRequester.sendPut(url, jsView, "application/json");
					}
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
