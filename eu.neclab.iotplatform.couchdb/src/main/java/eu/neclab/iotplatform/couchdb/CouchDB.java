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
package eu.neclab.iotplatform.couchdb;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Value;

import eu.neclab.iotplatform.couchdb.http.Client;
import eu.neclab.iotplatform.couchdb.http.HttpRequester;
import eu.neclab.iotplatform.iotbroker.commons.FullHttpResponse;
import eu.neclab.iotplatform.iotbroker.commons.GenerateUniqueID;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.BigDataRepository;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElement;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElementResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;

public class CouchDB implements BigDataRepository {

	/** The logger. */
	private static Logger logger = Logger.getLogger(CouchDB.class);

	@Value("${couchdb_host:localhost}")
	private String couchDB_HOST;
	@Value("${couchdb_protocol:http}")
	private String couchDB_PROTOCOL;
	@Value("${couchdb_port:5984}")
	private String couchDB_PORT;
	@Value("${couchdb_name:iotbrokerdb}")
	private String couchDB_NAME;
	@Value("${couchdb_username:null}")
	private String couchDB_USERNAME;
	@Value("${couchdb_password:null}")
	private String couchDB_PASSWORD;

	public String getCouchDB_HOST() {
		return couchDB_HOST;
	}

	public void setCouchDB_HOST(String couchDB_HOST) {
		this.couchDB_HOST = couchDB_HOST;
	}

	public String getCouchDB_PROTOCOL() {
		return couchDB_PROTOCOL;
	}

	public void setCouchDB_PROTOCOL(String couchDB_PROTOCOL) {
		this.couchDB_PROTOCOL = couchDB_PROTOCOL;
	}

	public String getCouchDB_PORT() {
		return couchDB_PORT;
	}

	public void setCouchDB_PORT(String couchDB_PORT) {
		this.couchDB_PORT = couchDB_PORT;
	}

	public String getCouchDB_NAME() {
		return couchDB_NAME;
	}

	public void setCouchDB_NAME(String couchDB_NAME) {
		this.couchDB_NAME = couchDB_NAME;
	}

	public String getUSERNAME() {
		return couchDB_USERNAME;
	}

	public void setUSERNAME(String uSERNAME) {
		couchDB_USERNAME = uSERNAME;
	}

	public String getPASSWORD() {
		return couchDB_PASSWORD;
	}

	public void setPASSWORD(String pASSWORD) {
		couchDB_PASSWORD = pASSWORD;
	}

	private final CreateDB couchDBtool = new CreateDB();

	private String authentication = null;

	boolean databaseExist = false;

	private String couchDB_ip = null;

	public CouchDB() {

	}

	public String getCouchDB_ip() {
		return couchDB_ip;
	}

	public void setCouchDB_ip() {
		couchDB_ip = couchDB_PROTOCOL + "://" + couchDB_HOST + ":"
				+ couchDB_PORT + "/";
		logger.info("CouchDB IP: " + couchDB_ip);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void storeData(List<ContextElement> contextElementList) {

		if (couchDB_ip == null) {
			setCouchDB_ip();
		}

		logger.info("Send update to the CouchDB storage...");

		if (couchDB_USERNAME != null && !couchDB_USERNAME.trim().isEmpty()
				&& couchDB_PASSWORD != null
				&& !couchDB_PASSWORD.trim().isEmpty()) {
			UsernamePasswordCredentials creds = new UsernamePasswordCredentials(
					couchDB_USERNAME, couchDB_PASSWORD);
			authentication = BasicScheme.authenticate(creds, "US-ASCII", false)
					.toString();
		}

		if (!databaseExist) {
			try {
				if (couchDBtool.checkDB(getCouchDB_ip(), couchDB_NAME,
						authentication)) {

					databaseExist = true;

				} else {

					couchDBtool.createDb(getCouchDB_ip(), couchDB_NAME,
							authentication);
					databaseExist = true;
				}
			} catch (MalformedURLException e) {
				logger.info("Impossible to store information into CouchDB", e);
				return;
			}
		}

		Iterator<ContextElement> iter = contextElementList.iterator();
		while (iter.hasNext()) {

			ContextElement contextElement = iter.next();

			// Create a list of ContextElement where each ContextElement has
			// only one ContextAttribute
			List<ContextElement> isolatedContextElementList = this
					.isolateAttributes(contextElement);

			// Iterate over the list
			for (ContextElement isolatedContextElement : isolatedContextElementList) {

				// Extract the timestamp from the ContextAttribute
				Date timestamp = extractTimestamp(isolatedContextElement
						.getContextAttributeList().iterator().next());

				// If no timestamp is found, take the actual one.
				if (timestamp == null) {
					timestamp = new Date();
				}

				// Generate the documentKey for historical data
				String documentKey = this.generateKeyForHistoricalData(
						contextElement.getEntityId(), contextElement
								.getContextAttributeList().iterator().next()
								.getName(), timestamp);

				JSONObject xmlJSONObj = XML.toJSONObject(contextElement
						.toString());

				// Store the historical data
				logger.debug("JSON Object to store:" + xmlJSONObj.toString(2));
				try {
					Client.sendRequest(new URL(getCouchDB_ip() + couchDB_NAME
							+ "/" + documentKey), "PUT", xmlJSONObj.toString(),
							"application/json", authentication);
				} catch (MalformedURLException e) {
					logger.info("Impossible to store information into CouchDB",
							e);
				}

				// TODO update latest
				// update latest

			}

		}

	}

	private String generateKeyForHistoricalData(EntityId entityId,
			String attributeName, Date timestamp) {

		// example: obs_urn:x-iot:smartsantander:1:3301|2015-05-08 16:36:22

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd%20HH:mm:ss");

		String key = new String(String.format("obs_%s:%s|%s", entityId.getId(),
				attributeName, dateFormat.format(timestamp)));

		return key;

	}

	/**
	 * This method create a list of ContextElement, one for each
	 * ContextAttribute in the original ContextElement. The new
	 * ContextAttributes will have duplicated DomainMetadata and EntityID. This
	 * is necessary in order to store historical data and make historical query
	 * of a specified attribute.
	 * 
	 * @param contextElement
	 * @return
	 */
	private List<ContextElement> isolateAttributes(ContextElement contextElement) {

		List<ContextElement> contextElementList = new ArrayList<ContextElement>();

		if (contextElement.getContextAttributeList().size() > 1) {
			contextElementList.add(contextElement);
		} else {

			for (ContextAttribute contextAttribute : contextElement
					.getContextAttributeList()) {
				List<ContextAttribute> contextAttributeList = new ArrayList<ContextAttribute>();
				contextAttributeList.add(contextAttribute);
				contextElementList.add(new ContextElement(contextElement
						.getEntityId(),
						contextElement.getAttributeDomainName(),
						contextAttributeList, contextElement
								.getDomainMetadata()));
			}
		}

		return contextElementList;

	}

	private Date extractTimestamp(ContextAttribute contextAttribute) {

		Date timestamp = null;

		for (ContextMetadata contextMetadata : contextAttribute.getMetadata()) {

			if (contextMetadata.getName().equalsIgnoreCase("creation_time")) {

				/*
				 * This contextMetadata is set by the leafengine connector
				 */

				// example timestamp "2015.05.29 19:24:28:769 +0000"
				// "yyyy.MM.dd HH:mm:ss:SSS Z"

				SimpleDateFormat parserSDF = new SimpleDateFormat(
						"yyyy.MM.dd HH:mm:ss:SSS Z");
				timestamp = parserSDF.parse(
						(String) contextMetadata.getValue(), new ParsePosition(
								0));
				break;
			}

		}

		return timestamp;
	}

	public static void main(String[] args) {
		String date = "2015.05.29 19:24:28:769 +0000";
		SimpleDateFormat parserSDF = new SimpleDateFormat(
				"yyyy.MM.dd HH:mm:ss:SSS Z");
		Date timestamp = parserSDF.parse(date, new ParsePosition(0));
		System.out.println(timestamp);
	}

	@Override
	public List<ContextElementResponse> getEntityLatestValues(EntityId entityId) {

		// TODO Auto-generated method stub

		FullHttpResponse dbResponse = queryDB(entityId);

		return null;
	}

	/**
	 * It returns result of a view in couchDB
	 * 
	 * @param queryName
	 * @param documentType
	 * @return
	 */
	private FullHttpResponse queryDB(EntityId entityId) {
		FullHttpResponse response = null;
		try {
			response = HttpRequester.sendGet(new URL(couchDB_HOST + "/"
					+ couchDB_NAME + "entity-" + entityId.getId()));
		} catch (MalformedURLException e) {
			logger.error("Error: ", e);
		} catch (Exception e) {
			logger.error("Error: ", e);
		}

		return response;

	}

}
