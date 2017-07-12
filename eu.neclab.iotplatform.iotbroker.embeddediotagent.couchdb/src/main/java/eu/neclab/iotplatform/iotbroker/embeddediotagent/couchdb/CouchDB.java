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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.neclab.iotplatform.iotbroker.commons.ContentType;
import eu.neclab.iotplatform.iotbroker.commons.FullHttpResponse;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.KeyValueStoreInterface;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.PermanentRegistryInterface;
import eu.neclab.iotplatform.iotbroker.embeddediotagent.storage.commons.StorageUtil;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElement;
import eu.neclab.iotplatform.ngsi.api.datamodel.NgsiStructure;
import eu.neclab.iotplatform.ngsi.api.datamodel.RegisterContextRequest;

public class CouchDB implements KeyValueStoreInterface,
		PermanentRegistryInterface {

	/** The logger. */
	private static Logger logger = Logger.getLogger(CouchDB.class);

	private String keyToCachePrefix = StorageUtil.LATEST_VALUE_PREFIX;

	private Map<String, String> cachedRevisionByKey = new HashMap<String, String>();

	private Map<String, String> permanentRegistryRevById = new HashMap<String, String>();

	private LoadingCache<String, ContextElement> lastValueCache;

	private String couchDB_ip = null;

	private String authentication = null;

	private Boolean databaseExist = false;

	private static String UNDERSCORE_PLACEHOLDER = "-~-_-~-";

	@Value("${couchdb_host:localhost}")
	private String couchDB_HOST;
	@Value("${couchdb_protocol:http}")
	private String couchDB_PROTOCOL;
	@Value("${couchdb_port:5984}")
	private String couchDB_PORT;
	@Value("${couchdb_name:embeddedagentdb}")
	private String couchDB_NAME;
	@Value("${couchdb_username:null}")
	private String couchDB_USERNAME;
	@Value("${couchdb_password:null}")
	private String couchDB_PASSWORD;

	@Value("${registryDB_NAME:embeddedagentregistrydb}")
	private String registryDB_NAME;

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

	public void setUSERNAME(String username) {
		couchDB_USERNAME = username;
	}

	public String getPASSWORD() {
		return couchDB_PASSWORD;
	}

	public void setPASSWORD(String password) {
		couchDB_PASSWORD = password;
	}

	public String getKeyToCachePrefix() {
		return keyToCachePrefix;
	}

	public void setKeyToCachePrefix(String keyToCachePrefix) {
		this.keyToCachePrefix = keyToCachePrefix;
	}

	public String getCouchDB_ip() {
		if (couchDB_ip == null) {
			this.setCouchDB_ip();
		}
		return couchDB_ip;
	}

	public void setCouchDB_ip() {
		couchDB_ip = couchDB_PROTOCOL + "://" + couchDB_HOST + ":"
				+ couchDB_PORT + "/";
		logger.info("CouchDB IP: " + couchDB_ip);
	}

	@PostConstruct
	private void postConstruct() {
		try {

			if (CouchDBUtil.checkDB(getCouchDB_ip(), couchDB_NAME,
					authentication)) {

				if (Boolean.parseBoolean(System.getProperty("agent.reset"))) {
					CouchDBUtil.deleteDb(getCouchDB_ip(), couchDB_NAME,
							authentication);
					CouchDBUtil.createDb(getCouchDB_ip(), couchDB_NAME,
							authentication);
				}

				databaseExist = true;

			} else {

				CouchDBUtil.createDb(getCouchDB_ip(), couchDB_NAME,
						authentication);
				databaseExist = true;
			}

			CouchDBUtil.checkViews(getCouchDB_ip(), couchDB_NAME);

			if (CouchDBUtil.checkDB(getCouchDB_ip(), registryDB_NAME,
					authentication)) {

				if (Boolean.parseBoolean(System.getProperty("agent.reset"))) {
					CouchDBUtil.deleteDb(getCouchDB_ip(), registryDB_NAME,
							authentication);
					CouchDBUtil.createDb(getCouchDB_ip(), registryDB_NAME,
							authentication);
				}

				databaseExist = true;

			} else {

				CouchDBUtil.createDb(getCouchDB_ip(), registryDB_NAME,
						authentication);
				databaseExist = true;
			}

			lastValueCache = CacheBuilder.newBuilder().maximumSize(1_000_000)
					.expireAfterAccess(10, TimeUnit.MINUTES)
					.build(new CacheLoader<String, ContextElement>() {

						@Override
						public ContextElement load(String key) throws Exception {
							return getValuefromCouchDB(key);
						}

						@Override
						public Map<String, ContextElement> loadAll(
								Iterable<? extends String> keys)
								throws Exception {
							return getValuesFromCouchDB(keys);
						}

					});

			this.cacheLastValue();

			if (logger.isDebugEnabled()) {
				logger.debug("Cached revisions: " + cachedRevisionByKey);
			}

			logger.info("All caches initialized. LastValueCache elements: "
					+ lastValueCache.size());

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean updateValue(String key, ContextElement contextElement) {

		this.checkDB();

		String value = contextElement.toJsonString();

		boolean successful = false;

		/*
		 * Get the document revision
		 */
		String revision = cachedRevisionByKey.get(key);

		try {

			String encodedKey = URLEncoder.encode(key, "UTF-8");

			if (revision == null) {

				// Revision is null because not cached before. It means that
				// there is no previous insertion of a document with the same
				// pair (id, attributeName). In this case insert the
				// contextElement into the DB
				FullHttpResponse respFromCouchDB;

				respFromCouchDB = HttpRequester.sendPut(new URL(getCouchDB_ip()
						+ couchDB_NAME + "/" + encodedKey), value,
						"application/json");

				if (respFromCouchDB == null) {

					logger.error("No response from CouchDB!!!");

					successful = false;

				} else if (respFromCouchDB.getStatusLine().getStatusCode() > 299) {

					logger.warn("CouchDB did not create correctly the value. Reason: "
							+ respFromCouchDB.getStatusLine());

					successful = false;

				} else {
					// Parse the revision and store it
					revision = CouchDBUtil
							.parseRevisionFromCouchdbResponse(respFromCouchDB);

					// Update the cache
					cachedRevisionByKey.put(key, revision);

					lastValueCache.put(key, contextElement);

					successful = true;
				}

			} else {
				// Create the MessageBody with the current document revision
				String messageBody = value.replaceFirst("\\{", "{ \"_id\":\""
						+ key + "\", \"_rev\":\"" + revision + "\",");

				// Update the document
				FullHttpResponse respFromCouchDB = HttpRequester.sendPut(
						new URL(getCouchDB_ip() + couchDB_NAME + "/"
								+ encodedKey), messageBody, "application/json");

				if (respFromCouchDB.getStatusLine().getStatusCode() > 299) {

					logger.warn("CouchDB did not update correctly the value. Reason: "
							+ respFromCouchDB.getStatusLine());

					successful = false;

				} else {

					// Parse the revision of the document and update it
					revision = CouchDBUtil
							.parseRevisionFromCouchdbResponse(respFromCouchDB);
					// Put in cache
					cachedRevisionByKey.put(key, revision);

					lastValueCache.put(key, contextElement);

					successful = true;

				}

			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return successful;

	}

	private void checkDB() {
		/*
		 * Checking Database connection issues
		 */
		if (couchDB_ip == null) {
			setCouchDB_ip();
		}

		if (!databaseExist) {

			synchronized (databaseExist) {

				if (!databaseExist) {

					if (couchDB_USERNAME != null
							&& !couchDB_USERNAME.trim().isEmpty()
							&& couchDB_PASSWORD != null
							&& !couchDB_PASSWORD.trim().isEmpty()) {
						UsernamePasswordCredentials creds = new UsernamePasswordCredentials(
								couchDB_USERNAME, couchDB_PASSWORD);
						authentication = BasicScheme.authenticate(creds,
								"US-ASCII", false).toString();
					}
					try {
						if (CouchDBUtil.checkDB(getCouchDB_ip(), couchDB_NAME,
								authentication)) {

							databaseExist = true;

						} else {

							CouchDBUtil.createDb(getCouchDB_ip(), couchDB_NAME,
									authentication);
							databaseExist = true;
						}
					} catch (MalformedURLException e) {
						logger.info("Impossible to access CouchDB", e);
						return;
					}
				}
			}
		}
	}

	@Override
	public boolean storeValue(String key, ContextElement contextElement) {
		return this.storeValue(key, contextElement, false);
	}

	@Override
	public boolean storeValue(String key, ContextElement contextElement,
			boolean cacheAfterStoring) {

		this.checkDB();

		boolean successful = false;

		try {
			FullHttpResponse respFromCouchDB = HttpRequester.sendPut(
					new URL(getCouchDB_ip() + couchDB_NAME + "/"
							+ URLEncoder.encode(key, "UTF-8")),
					contextElement.toJsonString(), "application/json");

			if (respFromCouchDB.getStatusLine().getStatusCode() > 299) {

				logger.warn("CouchDB database: " + couchDB_NAME
						+ " did not store correctly the value with key: " + key
						+ " . Reason: " + respFromCouchDB.getStatusLine());

				successful = false;

			} else {

				successful = true;

				if (cacheAfterStoring) {
					lastValueCache.put(key, contextElement);
				}

			}

		} catch (MalformedURLException e) {
			logger.info("Impossible to store information into CouchDB", e);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return successful;
	}

	private void cacheLastValue() {

		this.checkDB();

		// ÿ is the last character of the UTF-8 character table
		// %C3%BF is the url encoded for ÿ
		String url = null;
		try {
			url = String
					.format("%s%s/_all_docs?startkey=%%22%s%%22&endkey=%%22%s%%C3%%BF%%22&include_docs=true",
							getCouchDB_ip(), couchDB_NAME, keyToCachePrefix,
							keyToCachePrefix);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			FullHttpResponse response = HttpRequester.sendGet(new URL(url));

			if (response == null) {

				logger.error("No response from CouchDB!!!");

			} else if (response.getStatusLine().getStatusCode() == 200
					&& response.getBody() != null
					&& !response.getBody().isEmpty()) {
				JsonElement jelement = new JsonParser().parse(response
						.getBody());
				if (!jelement.isJsonNull()) {

					JsonObject jobject = jelement.getAsJsonObject();

					JsonArray rows = jobject.getAsJsonArray("rows");

					// Parse each row of the response
					for (JsonElement jsonElement : rows) {
						JsonObject row = jsonElement.getAsJsonObject();

						String rev = row.getAsJsonObject("value").get("rev")
								.getAsString();

						String key = row.get("key").getAsString();

//						try {
//							key = URLDecoder.decode(key, "UTF-8");
//						} catch (UnsupportedEncodingException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}

						cachedRevisionByKey.put(key, rev);

						// Parse the ContextElement
						if (row.get("doc") != null) {
							ContextElement contextElement = (ContextElement) NgsiStructure
									.parseStringToJson(row.get("doc")
											.toString(), ContextElement.class);

							lastValueCache.put(key, contextElement);
						} else {
							logger.warn("Inconsistency in CouchDB: "
									+ row.toString());
						}

					}
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public Collection<String> getKeys(String startKey, String endKey) {

		// ÿ is the last character of the UTF-8 character table
		// String url = String.format(
		// "%s%s/_all_docs?startkey=%%22%s%%22&endkey=%%22%sÿ%%22",
		// getCouchDB_ip(), couchDB_NAME, CouchDBUtil.LATEST_VALUE_PREFIX,
		// CouchDBUtil.LATEST_VALUE_PREFIX);

		this.checkDB();

		Collection<String> keys = new ArrayList<String>();

		try {

			String url = String.format(
					"%s%s/_all_docs?startkey=%%22%s%%22&endkey=%%22%s%%22",
					getCouchDB_ip(), couchDB_NAME,
					URLEncoder.encode(startKey, "UTF-8"),
					URLEncoder.encode(endKey, "UTF-8"));

			FullHttpResponse response = HttpRequester.sendGet(new URL(url));

			if (response == null) {

				logger.error("No response from CouchDB!!!");

			} else if (response.getStatusLine().getStatusCode() == 200
					&& response.getBody() != null
					&& !response.getBody().isEmpty()) {

				if (logger.isDebugEnabled()) {
					logger.debug(String.format("GetKeys %s response %s", url,
							response.getBody()));
				}

				JsonElement jelement = new JsonParser().parse(response
						.getBody());
				if (!jelement.isJsonNull()) {

					JsonObject jobject = jelement.getAsJsonObject();

					JsonArray rows = jobject.getAsJsonArray("rows");

					// Parse each row of the response
					for (JsonElement jsonElement : rows) {
						JsonObject row = jsonElement.getAsJsonObject();

						keys.add(row.get("key").getAsString());
					}
				}
			}

			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Keys for %s parsed response %s",
						url, keys));
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return keys;
	}

	public Multimap<String, String> getIdsByType() {
		Multimap<String, String> idsByType = HashMultimap.create();

		this.checkDB();

		String url = String.format("%s%s/%s", getCouchDB_ip(), couchDB_NAME,
				View.ID_BY_TYPE_VIEW.getQueryPath());

		try {
			FullHttpResponse response = HttpRequester.sendGet(new URL(url));

			if (response == null) {

				logger.error("No response from CouchDB for"
						+ View.ID_BY_TYPE_VIEW.getPath() + " view!!!");

			} else if (response.getStatusLine().getStatusCode() == 200
					&& response.getBody() != null
					&& !response.getBody().isEmpty()) {

				if (logger.isDebugEnabled()) {
					logger.debug(String.format("View %s response %s", url,
							response.getBody()));
				}
				JsonElement jelement = new JsonParser().parse(response
						.getBody());
				if (!jelement.isJsonNull()) {

					JsonObject jobject = jelement.getAsJsonObject();

					JsonArray rows = jobject.getAsJsonArray("rows");

					// Parse each row of the response
					for (JsonElement jsonElement : rows) {
						JsonObject row = jsonElement.getAsJsonObject();

						idsByType
								.put(row.get("key").getAsString(),
										row.get("value")
												.getAsString()
												.split(StorageUtil.ID_TO_ATTRIBUTENAME_SEPARATOR)[0]);

					}
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Parsed id by type %s", idsByType));
		}

		return idsByType;
	}

	private ContextElement getValuefromCouchDB(String latestValueDocumentKey) {

		this.checkDB();

		ContextElement contextElement = null;
		try {

			String url = getCouchDB_ip() + couchDB_NAME + "/"
					+ URLEncoder.encode(latestValueDocumentKey, "UTF-8");

			FullHttpResponse httpResponse = HttpRequester.sendGet(new URL(url));

			if (httpResponse == null) {

				logger.error("No response from CouchDB!!!");

			} else {
				if (httpResponse.getStatusLine().getStatusCode() == 200) {

					contextElement = (ContextElement) NgsiStructure
							.parseStringToJson(httpResponse.getBody(),
									ContextElement.class);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return contextElement;
	}

	public ContextElement getValue(String latestValueDocumentKey) {
		try {
			return lastValueCache.get(latestValueDocumentKey);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public ContextElement getValues(String startKey, String endKey) {
		ContextElement historicalContextElement = null;

		this.checkDB();

		// If here, an historical range is wanted
		// EXAMPLE:
		// http://localhost:5984/santander-nz-ccoc/_all_docs?startkey=%22obs_urn:x-iot:smartsantander:1:10006|2015-05-08%2016:00:00%22&endkey=%22obs_urn:x-iot:smartsantander:1:10006|2015-05-08%2017:00:00%22&include_docs=true

		// TODO A check if the key are referring to only one contextElement

		String url = getCouchDB_ip() + "/" + couchDB_NAME + "/_all_docs?"
				+ generateRangeQueryString(startKey, endKey);

		try {
			FullHttpResponse response = HttpRequester.sendGet(new URL(url));
			if (response == null) {

				logger.error("No response from CouchDB!!!");

			} else {

				historicalContextElement = getHistoricalContextElement(response
						.getBody());
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return historicalContextElement;
	}

	public Collection<ContextElement> getValues(List<String> keys) {
		try {
			return lastValueCache.getAll(keys).values();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private Map<String, ContextElement> getValuesFromCouchDB(
			Iterable<? extends String> keys) {

		// curl -d '{"keys":["bar","baz"]}' -X POST
		// http://127.0.0.1:5984/foo/_all_docs?include_docs=true

		StringBuffer body = new StringBuffer();
		body.append("{\"keys\":[");
		boolean first = true;
		for (String key : keys) {
			if (first) {
//				try {
//					body.append("\"" + URLDecoder.decode(key, "UTF-8") + "\"");
//				} catch (UnsupportedEncodingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				body.append("\"" + key + "\"");
				first = false;
			} else {
//				try {
//					body.append(",\"" + URLDecoder.decode(key, "UTF-8") + "\"");
//				} catch (UnsupportedEncodingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				body.append(",\"" + key + "\"");

			}
		}
		body.append("]}");
		
		this.checkDB();

		// TODO A check if the key are referring to only one contextElement

		String url = getCouchDB_ip() + "/" + couchDB_NAME
				+ "/_all_docs?include_docs=true";

		Map<String, ContextElement> contextElements = new HashMap<String, ContextElement>();

		try {
			FullHttpResponse response = HttpRequester.sendPost(new URL(url),
					escapeBody(body.toString()), ContentType.JSON.toString()
							+ "; " + "charset=utf-8");
			if (response == null
					|| response.getStatusLine().getStatusCode() != 200
					|| response.getBody() == null
					|| response.getBody().isEmpty()) {

				logger.error("No response from CouchDB!!!");

			} else {

				contextElements = parseContextElements(response.getBody());
			}

		} catch (MalformedURLException e) {
			logger.warn("Error on getting values from Couchdb, MalformedURLExpection: "
					+ url + " message: " + e.getMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.warn("Error on getting values from Couchdb: ");
			e.printStackTrace();
		}

		return contextElements;
	}

	public List<ContextElement> getAllValues(String startKey, String endKey) {
		List<ContextElement> contextElements = new ArrayList<ContextElement>();

		this.checkDB();

		// If here, an historical range is wanted
		// EXAMPLE:
		// http://localhost:5984/santander-nz-ccoc/_all_docs?startkey=%22obs_urn:x-iot:smartsantander:1:10006|2015-05-08%2016:00:00%22&endkey=%22obs_urn:x-iot:smartsantander:1:10006|2015-05-08%2017:00:00%22&include_docs=true

		// TODO A check if the key are referring to only one contextElement

		String url = getCouchDB_ip() + "/" + couchDB_NAME + "/_all_docs?"
				+ generateRangeQueryString(startKey, endKey);

		try {
			FullHttpResponse response = HttpRequester.sendGet(new URL(url));
			if (response == null) {

				logger.error("No response from CouchDB!!!");

			} else {

				JsonElement jelement = new JsonParser().parse(response
						.getBody());
				if (!jelement.isJsonNull()) {

					JsonObject jobject = jelement.getAsJsonObject();

					JsonArray rows = jobject.getAsJsonArray("rows");

					// Parse each row of the response
					for (JsonElement jsonElement : rows) {
						JsonObject row = jsonElement.getAsJsonObject();

						// Parse the ContextElement
						ContextElement contextElement = (ContextElement) NgsiStructure
								.parseStringToJson(row.get("doc").toString(),
										ContextElement.class);
						contextElements.add(contextElement);

					}
				}
			}

		} catch (MalformedURLException e) {
			logger.warn("Error on getting values from Couchdb, MalformedURLExpection: "
					+ url + " message: " + e.getMessage());
		}

		return contextElements;
	}

	private String generateRangeQueryString(String startKey, String endKey) {

		// "startkey=%22obs_urn:x-iot:smartsantander:1:10006|2015-05-08%2016:00:00%22&endkey=%22obs_urn:x-iot:smartsantander:1:10006|2015-05-08%2017:00:00%22&include_docs=true"

		String queryString = null;
		try {
			queryString = new String(String.format(
					"startkey=%%22%s%%22&endkey=%%22%s%%22&include_docs=true",
					URLEncoder.encode(startKey, "UTF-8"),
					URLEncoder.encode(endKey, "UTF-8")));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return queryString;

	}

	private ContextElement getHistoricalContextElement(String couchDBResultSet) {

		ContextElement historicalContextElement = null;

		JsonElement jelement = new JsonParser().parse(couchDBResultSet);
		if (!jelement.isJsonNull()) {

			JsonObject jobject = jelement.getAsJsonObject();

			JsonArray rows = jobject.getAsJsonArray("rows");

			// Parse each row of the response
			for (JsonElement jsonElement : rows) {
				JsonObject row = jsonElement.getAsJsonObject();

				// Parse the ContextElement
				ContextElement contextElement = (ContextElement) NgsiStructure
						.parseStringToJson(row.get("doc").toString(),
								ContextElement.class);
				if (historicalContextElement == null) {
					historicalContextElement = contextElement;
				} else {
					historicalContextElement.getContextAttributeList().addAll(
							contextElement.getContextAttributeList());
				}

			}
		}

		return historicalContextElement;
	}

	private Map<String, ContextElement> parseContextElements(
			String couchDBResultSet) {

		Map<String, ContextElement> contextElements = new HashMap<String, ContextElement>();

		JsonElement jelement = new JsonParser().parse(couchDBResultSet);
		if (!jelement.isJsonNull()) {

			JsonObject jobject = jelement.getAsJsonObject();

			JsonArray rows = jobject.getAsJsonArray("rows");

			// Parse each row of the response
			for (JsonElement jsonElement : rows) {
				JsonObject row = jsonElement.getAsJsonObject();

				// Parse the ContextElement
				if (row.get("doc") != null) {
					String id = row.get("key").getAsString();

//					try {
//						id = URLDecoder.decode(id, "UTF-8");
//					} catch (UnsupportedEncodingException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}

					ContextElement contextElement = (ContextElement) NgsiStructure
							.parseStringToJson(row.get("doc").toString(),
									ContextElement.class);
					contextElements.put(id, contextElement);
				} else {
					logger.warn("Inconsistency in CouchDB: " + row.toString());
				}

			}
		}

		return contextElements;
	}

	@Override
	public Map<String, RegisterContextRequest> getAllRegistrations() {

		Map<String, RegisterContextRequest> registrations = new HashMap<String, RegisterContextRequest>();

		try {
			FullHttpResponse response = HttpRequester.sendGet(new URL(
					getCouchDB_ip() + registryDB_NAME + "/"
							+ "_all_docs?include_docs=true"));

			if (response == null) {

				logger.error("No response from CouchDB!!!");

			} else if (response.getStatusLine().getStatusCode() == 200
					&& response.getBody() != null
					&& !response.getBody().isEmpty()) {
				JsonElement jelement = new JsonParser().parse(response
						.getBody());
				if (!jelement.isJsonNull()) {

					JsonObject jobject = jelement.getAsJsonObject();

					JsonArray rows = jobject.getAsJsonArray("rows");

					// Parse each row of the response
					for (JsonElement jsonElement : rows) {
						JsonObject row = jsonElement.getAsJsonObject();

						String id = row.get("key").getAsString();
						String rev = row.getAsJsonObject("doc").get("_rev")
								.getAsString();
						String registration = row.getAsJsonObject("doc")
								.get("registration").getAsString();

						if (id.startsWith(UNDERSCORE_PLACEHOLDER)) {
							id = id.replaceFirst(UNDERSCORE_PLACEHOLDER, "_");
						}

						registrations.put(id,
								(RegisterContextRequest) NgsiStructure
										.convertStringToXml(registration,
												RegisterContextRequest.class));

						permanentRegistryRevById.put(id, rev);
					}
				}
			}

		} catch (MalformedURLException e) {
			logger.warn("Impossible to get data from CouchDB", e);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return registrations;
	}

	private String escapeBody(String body) {

		String escapedBody = body.replaceAll("\"", "\\\"");

		return escapedBody;

	}

	@Override
	public void storeRegistration(String id, RegisterContextRequest registration) {

		// boolean successful = false;

		if (id.startsWith("_")) {
			id = id.replaceFirst("_", UNDERSCORE_PLACEHOLDER);
		}

		try {

			String registrationString = registration.toString();
			registrationString = registrationString.replaceAll("\"", "\\\\\"");
			registrationString = registrationString.replace("\n", "");
			registrationString = "{\"registration\":\"" + registrationString
					+ "\"}";

			FullHttpResponse respFromCouchDB = HttpRequester.sendPut(
					new URL(getCouchDB_ip() + registryDB_NAME + "/"
							+ URLEncoder.encode(id, "UTF-8")),
					registrationString, "application/json");

			if (logger.isDebugEnabled()) {
				logger.debug("Response for the insertion in the database "
						+ registryDB_NAME + ". Insertion: key:" + id
						+ " value: " + registrationString
						+ " Response.StatusLine: "
						+ respFromCouchDB.getStatusLine() + " Response.body: "
						+ respFromCouchDB.getBody());
			}

			if (respFromCouchDB.getStatusLine().getStatusCode() > 299) {

				logger.warn("CouchDB database " + registryDB_NAME
						+ " did not store correctly the value with key: " + id
						+ " . Reason: " + respFromCouchDB.getStatusLine());

			} else {
				// Parse the revision and store it
				String revision = CouchDBUtil
						.parseRevisionFromCouchdbResponse(respFromCouchDB);

				permanentRegistryRevById.put(id, revision);
			}

		} catch (MalformedURLException e) {
			logger.warn("Impossible to store information into CouchDB", e);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// return successful;

	}

	@Override
	public void deleteRegistration(String id) {

		if (id.startsWith("_")) {
			id = id.replaceFirst("_", UNDERSCORE_PLACEHOLDER);
		}

		try {
			String rev = permanentRegistryRevById.get(id);

			if (rev == null) {
				rev = permanentRegistryRevById.get(URLEncoder.encode(id,
						"UTF-8"));
			}

			if (rev == null) {
				rev = permanentRegistryRevById.get(URLDecoder.decode(id,
						"UTF-8"));
			}

			FullHttpResponse response = HttpRequester.sendDelete(new URL(
					getCouchDB_ip() + registryDB_NAME + "/" + URLEncoder.encode(id,"UTF-8") + "?rev="
							+ rev));

			if (response == null) {

				logger.error("No response from CouchDB!!!");

			} else if (response.getStatusLine().getStatusCode() == 200) {

				logger.info("Deleted from internal registryDB : " + id);
			} else {

				logger.warn("CouchDB database " + registryDB_NAME
						+ " did not delete correctly the value with key: " + id
						+ " . Reason: " + response.getStatusLine());
			}

		} catch (MalformedURLException e) {
			logger.warn("Impossible to delete data from CouchDB", e);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
