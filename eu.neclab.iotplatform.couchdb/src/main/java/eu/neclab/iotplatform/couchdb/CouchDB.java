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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.neclab.iotplatform.couchdb.http.HttpRequester;
import eu.neclab.iotplatform.couchdb.util.CouchDBUtil;
import eu.neclab.iotplatform.couchdb.util.View;
import eu.neclab.iotplatform.iotbroker.commons.FullHttpResponse;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.BigDataRepository;
import eu.neclab.iotplatform.ngsi.api.datamodel.Code;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElement;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElementResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.NgsiStructure;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.ReasonPhrase;
import eu.neclab.iotplatform.ngsi.api.datamodel.StatusCode;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextRequest;
import eu.neclab.iotplatform.ngsi.api.ngsi10.Ngsi10Interface;

public class CouchDB implements BigDataRepository {

	/** The logger. */
	private static Logger logger = Logger.getLogger(CouchDB.class);

	// private final CreateDB couchDBtool = new CreateDB();

	private HttpRequester httpRequester = new HttpRequester();

	private CouchDBUtil couchDBUtil = new CouchDBUtil(httpRequester);

	private String authentication = null;

	private boolean databaseExist = false;

	// Ngsi10Interface to call when a notification needs to be issued
	private Ngsi10Interface ngsi10Callback;

	private String couchDB_ip = null;

	private Multimap<String, String> cachedIdsByType = HashMultimap.create();
	private Multimap<String, AttributeNameAndRevision> cachedAttributesAndRevisionById = HashMultimap
			.create();
	private Multimap<String, String> subscriptionIdByLatestDocumentKey = HashMultimap
			.create();

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

	public HttpRequester getHttpRequester() {
		return httpRequester;
	}

	public void setHttpRequester(HttpRequester httpRequester) {
		this.httpRequester = httpRequester;
	}

	@Override
	public Ngsi10Interface getNgsi10Callback() {
		return ngsi10Callback;
	}

	@Override
	public void setNgsi10Callback(Ngsi10Interface ngsi10Callback) {
		this.ngsi10Callback = ngsi10Callback;
	}

	private class AttributeNameAndRevision {
		public String attributeName;
		public String revision;

		public AttributeNameAndRevision(String attributeName, String revision) {
			super();
			this.attributeName = attributeName;
			this.revision = revision;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((attributeName == null) ? 0 : attributeName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AttributeNameAndRevision other = (AttributeNameAndRevision) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (attributeName == null) {
				if (other.attributeName != null)
					return false;
			} else if (!attributeName.equals(other.attributeName))
				return false;
			return true;
		}

		private CouchDB getOuterType() {
			return CouchDB.this;
		}

		@Override
		public String toString() {
			return "AttributeNameAndRevision [attributeName=" + attributeName
					+ ", revision=" + revision + "]";
		}

	}

	public CouchDB() {

	}

	@PostConstruct
	public void postConstruct() {
		try {
			if (couchDBUtil.checkDB(getCouchDB_ip(), couchDB_NAME,
					authentication)) {

				databaseExist = true;

			} else {

				couchDBUtil.createDb(getCouchDB_ip(), couchDB_NAME,
						authentication);
				databaseExist = true;
			}

			couchDBUtil.checkViews(getCouchDB_ip(), couchDB_NAME);

			this.cacheAttributesAndRevisionById();
			this.cacheIdsByType();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
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
				if (couchDBUtil.checkDB(getCouchDB_ip(), couchDB_NAME,
						authentication)) {

					databaseExist = true;

				} else {

					couchDBUtil.createDb(getCouchDB_ip(), couchDB_NAME,
							authentication);
					databaseExist = true;
				}
			} catch (MalformedURLException e) {
				logger.info("Impossible to store information into CouchDB", e);
				return;
			}
		}

		final Multimap<String, ContextElement> contextElementToBeNotified = HashMultimap
				.create();

		// List of Task
		List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();

		Iterator<ContextElement> iter = contextElementList.iterator();
		while (iter.hasNext()) {

			ContextElement contextElement = iter.next();

			// System.out.println(contextElement.toString());

			// Create a list of ContextElement where each ContextElement has
			// exactly one ContextAttribute
			List<ContextElement> isolatedContextElementList = this
					.isolateAttributes(contextElement);

			final Date localDate = new Date();

			// Iterate over the list
			for (final ContextElement isolatedContextElement : isolatedContextElementList) {

				tasks.add(Executors.callable(new Runnable() {

					@Override
					public void run() {
						// Extract the timestamp from the ContextAttribute
						Date timestamp = extractTimestamp(isolatedContextElement
								.getContextAttributeList().iterator().next());

						// If no timestamp is found, take the local one.
						if (timestamp == null) {
							timestamp = localDate;
						}

						// Generate the documentKey for historical data
						String historicalDataDocumentKey = generateKeyForHistoricalData(
								isolatedContextElement.getEntityId().getId(),
								(isolatedContextElement.getEntityId().getType() == null) ? null
										: isolatedContextElement.getEntityId()
												.getType().toString(),
								isolatedContextElement
										.getContextAttributeList().iterator()
										.next().getName(), timestamp);

						// Store the historical data
						// logger.debug("JSON Object to store:" +
						// jsonObj.toString(2));

						// String jsonString = jsonObj.toString();
						String jsonString = isolatedContextElement
								.toJsonString();

						/*
						 * Store the observation as historical document
						 */
						try {
							httpRequester.sendPut(new URL(getCouchDB_ip()
									+ couchDB_NAME + "/"
									+ historicalDataDocumentKey), jsonString,
									"application/json");

						} catch (MalformedURLException e) {
							logger.info(
									"Impossible to store information into CouchDB",
									e);
						} catch (Exception e) {
							e.printStackTrace();
						}

						/*
						 * Update the latest value
						 */
						updateLatestValue(
								isolatedContextElement,
								jsonString,
								Multimaps
										.synchronizedMultimap(contextElementToBeNotified));

					}

				}));

			}

		}

		ExecutorService taskExecutor = Executors.newCachedThreadPool();
		try {
			taskExecutor.invokeAll(tasks);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Multimap<String, ContextElementResponse> notificationMap = createNotificationMap(contextElementToBeNotified);

		for (String subscriptionId : notificationMap.keySet()) {
			notify(subscriptionId, notificationMap.get(subscriptionId));
		}

	}

	private void notify(final String subscriptionId,
			final Collection<ContextElementResponse> contextElementResponseList) {

		new Runnable() {

			@Override
			public void run() {

				ngsi10Callback.notifyContext(new NotifyContextRequest(
						subscriptionId, ngsi10Callback.toString(),
						new ArrayList<ContextElementResponse>(
								contextElementResponseList)));

			}

		}.run();

	}

	private String getAttributeNameFromIsolatedContextElement(
			ContextElement isolatedContextElement) {
		return Iterables.getFirst(
				isolatedContextElement.getContextAttributeList(), null)
				.getName();
	}

	private void updateLatestValue(ContextElement isolatedContextElement,
			String jsonString,
			Multimap<String, ContextElement> contextElementToBeNotified) {

		String documentKey = this
				.generateKeyForLatestValue(
						isolatedContextElement.getEntityId().getId(),
						(isolatedContextElement.getEntityId().getType() == null) ? null
								: isolatedContextElement.getEntityId()
										.getType().toString(),
						getAttributeNameFromIsolatedContextElement(isolatedContextElement));

		/*
		 * Get the document revision
		 */
		AttributeNameAndRevision attributeNameAndRevision = new AttributeNameAndRevision(
				getAttributeNameFromIsolatedContextElement(isolatedContextElement),
				null);

		for (AttributeNameAndRevision a : cachedAttributesAndRevisionById
				.get(isolatedContextElement.getEntityId().getId())) {
			if (a.equals(attributeNameAndRevision)) {
				attributeNameAndRevision = a;
				break;
			}
		}

		try {

			if (attributeNameAndRevision.revision == null) {

				// Revision is null because not cached before. It means that
				// there is no previous insertion of a document with the same
				// pair (id, attributeName). In this case insert the
				// contextElement into the DB
				FullHttpResponse respFromCouchDB = httpRequester.sendPut(
						new URL(getCouchDB_ip() + couchDB_NAME + "/"
								+ documentKey), jsonString, "application/json");

				attributeNameAndRevision.revision = parseRevisionFromCouchdbResponse(respFromCouchDB);

				cachedAttributesAndRevisionById
						.put(generateId(isolatedContextElement.getEntityId()
								.getId(), (isolatedContextElement.getEntityId()
								.getType() == null) ? null
								: isolatedContextElement.getEntityId()
										.getType().toString()),
								attributeNameAndRevision);

			} else {
				// Create the MessageBody with the current document revision
				String messageBody = jsonString.replaceFirst("\\{",
						"{ \"_id\":\"" + documentKey + "\", \"_rev\":\""
								+ attributeNameAndRevision.revision + "\",");

				// Update the document
				FullHttpResponse respFromCouchDB = httpRequester
						.sendPut(new URL(getCouchDB_ip() + couchDB_NAME + "/"
								+ documentKey), messageBody, "application/json");

				attributeNameAndRevision.revision = parseRevisionFromCouchdbResponse(respFromCouchDB);

				for (String subscriptionId : subscriptionIdByLatestDocumentKey
						.get(documentKey)) {
					contextElementToBeNotified.put(subscriptionId,
							isolatedContextElement);
				}

			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method is meant to compact the ContextElements when they can be
	 * compacted (same id) and create the ContextElementResponse from the
	 * resulting list
	 * 
	 * @param contextElementToBeNotified
	 *            A data mapping the subscriptionId and the contextElements
	 * @return
	 */
	private Multimap<String, ContextElementResponse> createNotificationMap(
			Multimap<String, ContextElement> contextElementToBeNotified) {

		Multimap<String, ContextElementResponse> compactedMap = HashMultimap
				.create();

		for (String subscriptionId : contextElementToBeNotified.keySet()) {

			// /*
			// * It will contain a context element for each id (generated by the
			// * function generateId)
			// */
			// Map<String, ContextElement> contextElementById = new
			// HashMap<String, ContextElement>();
			//
			// for (ContextElement contextElement : contextElementToBeNotified
			// .get(subscriptionId)) {
			//
			// String id = generateId(contextElement.getEntityId().getId(),
			// (contextElement.getEntityId().getType() == null ? null
			// : contextElement.getEntityId().getType()
			// .toString()));
			//
			// if (contextElementById.containsKey(id)) {
			// contextElementById.get(id).getContextAttributeList()
			// .addAll(contextElement.getContextAttributeList());
			// } else {
			// ContextElement newContextElement = new ContextElement(
			// contextElement.getEntityId(),
			// contextElement.getAttributeDomainName(),
			// new ArrayList<ContextAttribute>(contextElement
			// .getContextAttributeList()),
			// contextElement.getDomainMetadata());
			//
			// contextElementById.put(id, newContextElement);
			//
			// }
			//
			// }

			Collection<ContextElement> compactedContextElements = compactContextElements(contextElementToBeNotified
					.get(subscriptionId));

			for (ContextElement contextElement : compactedContextElements) {

				compactedMap.put(subscriptionId, new ContextElementResponse(
						contextElement, new StatusCode(Code.OK_200.getCode(),
								ReasonPhrase.OK_200.toString(),
								"New ContextElement")));
			}

		}

		return compactedMap;
	}

	private Collection<ContextElement> compactContextElements(
			Collection<ContextElement> contextElementList) {

		/*
		 * It will contain a context element for each id (generated by the
		 * function generateId)
		 */
		Map<String, ContextElement> contextElementById = new HashMap<String, ContextElement>();

		for (ContextElement contextElement : contextElementList) {

			String id = generateId(
					contextElement.getEntityId().getId(),
					(contextElement.getEntityId().getType() == null ? null
							: contextElement.getEntityId().getType().toString()));

			if (contextElementById.containsKey(id)) {
				contextElementById.get(id).getContextAttributeList()
						.addAll(contextElement.getContextAttributeList());
			} else {
				ContextElement newContextElement = new ContextElement(
						contextElement.getEntityId(),
						contextElement.getAttributeDomainName(),
						new ArrayList<ContextAttribute>(contextElement
								.getContextAttributeList()),
						contextElement.getDomainMetadata());

				contextElementById.put(id, newContextElement);

			}

		}

		return contextElementById.values();

	}

	private String parseRevisionFromCouchdbResponse(
			FullHttpResponse fullHttpResponse) {

		String responseBody = fullHttpResponse.getBody();

		JsonParser parser = new JsonParser();
		JsonObject o = (JsonObject) parser.parse(responseBody);

		//
		// System.out.println("----------------> Response from CouchDB <-------------"+
		// o);

		return o.get("rev").getAsString().replaceAll("\"+", "");
	}

	private String generateKeyForHistoricalData(String entityId, String type,
			String attributeName, Date timestamp) {

		// example: obs-urn:x-iot:smartsantander:1:3301|2015-05-08 16:36:22

		return generateKeyForHistoricalData(generateId(entityId, type),
				attributeName, timestamp);

	}

	/**
	 * IMPORTANT: id needs to be the one generated by generateId(String
	 * entityId, String type)
	 * 
	 * @param id
	 *            The full Id composed by generateId(String entityId, String
	 *            type)
	 * @param attributeName
	 * @param timestamp
	 * @return
	 */
	private String generateKeyForHistoricalData(String id,
			String attributeName, Date timestamp) {

		return CouchDBUtil.HISTORICAL_VALUE_PREFIX
				+ CouchDBUtil.PREFIX_TO_ID_SEPARATOR + id
				+ CouchDBUtil.ID_TO_ATTRIBUTENAME_SEPARATOR + attributeName
				+ CouchDBUtil.DOCUMENT_TO_TIMESTAMP_SEPARATOR
				+ couchDBUtil.formatDate(timestamp);

	}

	private String generateKeyForLatestValue(String entityId, String type,
			String attributeName) {

		// example: entity-urn:x-iot:smartsantander:1:3301

		return generateKeyForLatestValue(generateId(entityId, type),
				attributeName);

	}

	/**
	 * IMPORTANT: id needs to be the one generated by generateId(String
	 * entityId, String type)
	 * 
	 * @param id
	 *            The full Id composed by generateId(String entityId, String
	 *            type)
	 * @param attributeName
	 * @return
	 */
	private String generateKeyForLatestValue(String id, String attributeName) {

		return CouchDBUtil.LATEST_VALUE_PREFIX
				+ CouchDBUtil.PREFIX_TO_ID_SEPARATOR + id
				+ CouchDBUtil.ID_TO_ATTRIBUTENAME_SEPARATOR + attributeName;
	}

	/**
	 * It generated the id of the contextElement by concatenating entityId.id
	 * and entityId.type separate by the separator specified in the CouchDBUtil.
	 * If entityId.type is null or empty, the id will be just the entityId.id
	 * 
	 * @param entityId
	 * @param type
	 * @return the identifier
	 */
	private String generateId(String entityId, String type) {

		if (type == null || type.isEmpty() || type.equals("")) {

			return entityId;

		} else {

			return entityId + CouchDBUtil.ENTITY_TO_TYPE_SEPARATOR + type;
		}

	}

	/**
	 * Return an array of String where the first element is the entityId.id and
	 * the second element is the entity.type if present. If entity.type is not
	 * present the array will be composed by only one String, the entity.id
	 * 
	 * @param id
	 * @return
	 */
	private String[] splitEntityAndType(String id) {
		return id.split(CouchDBUtil.ENTITY_TO_TYPE_SEPARATOR);
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

		if (contextElement.getContextAttributeList().size() < 2) {
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

		if (contextAttribute.getMetadata() != null
				&& !contextAttribute.getMetadata().isEmpty()) {

			for (ContextMetadata contextMetadata : contextAttribute
					.getMetadata()) {

				if (contextMetadata.getName().equalsIgnoreCase("creation_time")) {

					/*
					 * This contextMetadata is set by the leafengine connector
					 */

					// example timestamp "2015.05.29 19:24:28:769 +0000"
					// "yyyy.MM.dd HH:mm:ss:SSS Z"

					SimpleDateFormat parserSDF = new SimpleDateFormat(
							"yyyy.MM.dd HH:mm:ss:SSS Z");
					timestamp = parserSDF.parse(
							(String) contextMetadata.getValue(),
							new ParsePosition(0));
					break;
				} else if (contextMetadata.getName().equalsIgnoreCase("date")) {

					/*
					 * This contextMetadata is set by the leafengine connector
					 */

					// example timestamp "2015-07-24 16:35:09"
					// "yyyy.MM.dd HH:mm:ss:SSS Z"

					SimpleDateFormat parserSDF = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					timestamp = parserSDF.parse(
							(String) contextMetadata.getValue(),
							new ParsePosition(0));
					break;
				}

			}

		}

		return timestamp;
	}

	public static void main(String[] args) {
		// String date = "2015.05.29 19:24:28:769 +0000";
		// SimpleDateFormat parserSDF = new SimpleDateFormat(
		// "yyyy.MM.dd HH:mm:ss:SSS Z");
		// Date timestamp = parserSDF.parse(date, new ParsePosition(0));
		// System.out.println(timestamp);

		//
		// String json = new String(
		// "{\"_id\": \"entity-phidgets:distance1\",\"_rev\": \"3-5b1ac024dbbe58ab82c260bf2c4070de\",\"contextElement\": {\"contextAttributeList\": {\"contextAttribute\": {\"name\": \"distance1\","
		// +
		// "\"contextValue\": false,\"metadata\": {\"contextMetadata\": {\"name\": \"creation_time\",\"value\": {\"content\": \"2015.07.14 09:04:18:740 -0400\",\"xmlns:xs\": \"http://www.w3.org/2001/XMLSchema\","
		// +
		// "\"xmlns:xsi\": \"http://www.w3.org/2001/XMLSchema-instance\",\"xsi:type\": \"xs:string\"}}}}},\"entityId\": {\"id\": \"phidgets\",\"isPattern\": false},\"domainMetadata\": {\"contextMetadata\": ["
		// +
		// "{\"name\": \"unit\",\"value\": {\"content\": \"cm\",\"xmlns:xs\": \"http://www.w3.org/2001/XMLSchema\",\"xmlns:xsi\": \"http://www.w3.org/2001/XMLSchema-instance\",\"xsi:type\": \"xs:string\"}},"
		// +
		// "{\"name\": \"writeable\",\"value\": {\"content\": false,\"xmlns:xs\": \"http://www.w3.org/2001/XMLSchema\",\"xmlns:xsi\": \"http://www.w3.org/2001/XMLSchema-instance\",\"xsi:type\": \"xs:string\"}},"
		// +
		// "{\"name\": \"local\",\"value\": {\"content\": true,\"xmlns:xs\": \"http://www.w3.org/2001/XMLSchema\",\"xmlns:xsi\": \"http://www.w3.org/2001/XMLSchema-instance\",\"xsi:type\": \"xs:string\"}},"
		// +
		// "{\"name\": \"SimpleGeoLocation\",\"value\": {\"NW_Corner\": \"-41.291158,174.778270\",\"xmlns:xsi\": \"http://www.w3.org/2001/XMLSchema-instance\",\"xsi:type\": \"segment\","
		// +
		// "\"SE_Corner\": \"-41.291158,174.778270\"},\"type\": \"SimpleGeoLocation\"}]}}}");

	}

	@Override
	public ContextElement getLatestValue(String id, URI type,
			String attributeName) {

		if (type == null) {
			return this.getLatestValue(id, "", attributeName);

		} else {
			return this.getLatestValue(id, type.toString(), attributeName);

		}
	}

	private ContextElement getLatestValue(String entityId, String type,
			String attributeName) {

		return getLatestValue(generateId(entityId, type), attributeName);

	}

	private ContextElement getLatestValue(String id, String attributeName) {

		String latestValueDocumentKey = this.generateKeyForLatestValue(id,
				attributeName);
		return this.getLatestValue(latestValueDocumentKey);
	}

	private ContextElement getLatestValue(String latestValueDocumentKey) {
		ContextElement contextElement = null;
		try {

			String url = getCouchDB_ip() + couchDB_NAME + "/"
					+ latestValueDocumentKey;

			// System.out.println("Requesting Url:" + url);

			FullHttpResponse httpResponse = httpRequester.sendGet(new URL(url));

			if (httpResponse.getStatusLine().getStatusCode() == 200) {

				contextElement = (ContextElement) NgsiStructure
						.parseStringToJson(httpResponse.getBody(),
								ContextElement.class);

				// System.out.println("PARSED:" + contextElement);

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return contextElement;

	}

	@Override
	public ContextElement getHistoricalValues(String id, URI type,
			String attributeName, Date startDate, Date endDate) {

		if (type == null) {
			return getHistoricalValues(id, "", attributeName, startDate,
					endDate);

		} else {
			return getHistoricalValues(id, type.toString(), attributeName,
					startDate, endDate);

		}

	}

	public ContextElement getHistoricalValues(String id, String type,
			String attributeName, Date startDate, Date endDate) {
		ContextElement historicalContextElement = null;

		// If here, an historical range is wanted
		// EXAMPLE:
		// http://localhost:5984/santander-nz-ccoc/_all_docs?startkey=%22obs_urn:x-iot:smartsantander:1:10006|2015-05-08%2016:00:00%22&endkey=%22obs_urn:x-iot:smartsantander:1:10006|2015-05-08%2017:00:00%22&include_docs=true

		String url = getCouchDB_ip()
				+ "/"
				+ couchDB_NAME
				+ "/_all_docs?"
				+ generateHistoricalQueryString(id, type, attributeName,
						startDate, endDate);

		try {
			FullHttpResponse response = httpRequester.sendGet(new URL(url));

			historicalContextElement = getHistoricalContextElement(response
					.getBody());

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return historicalContextElement;
	}

	private String generateHistoricalQueryString(String id, String type,
			String attributeName, Date startDate, Date endDate) {

		// "startkey=%22obs_urn:x-iot:smartsantander:1:10006|2015-05-08%2016:00:00%22&endkey=%22obs_urn:x-iot:smartsantander:1:10006|2015-05-08%2017:00:00%22&include_docs=true"

		String startKey = generateKeyForHistoricalData(id, type, attributeName,
				startDate);
		String endKey = generateKeyForHistoricalData(id, type, attributeName,
				endDate);

		// Calendar inclusiveEndDate = Calendar.getInstance();
		// inclusiveEndDate.setTime(endDate);
		// inclusiveEndDate.set(Calendar.S, value);

		String queryString = new String(String.format(
				"startkey=%%22%s%%22&endkey=%%22%s%%22&include_docs=true",
				startKey, endKey));

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

	@Override
	public List<ContextElement> getLatestValues(List<EntityId> entityIdList) {

		return this.getLatestValues(entityIdList, null);

	}

	@Override
	public List<ContextElement> getLatestValues(List<EntityId> entityIdList,
			List<String> attributeNames) {

		List<ContextElement> contextElementList = new ArrayList<ContextElement>();

		Set<String> attributeNamesSet = new HashSet<String>();
		attributeNamesSet.addAll(attributeNames);

		Multimap<String, String> idsAndAttributeNames = matchingIdsAndAttributeNames(
				entityIdList, attributeNamesSet);

		// List of Task
		List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();

		for (String id : idsAndAttributeNames.keySet()) {

			/*
			 * Extract the entityId.id and the entityId.type
			 */
			String[] entityAndType = splitEntityAndType(id);
			final String entity;
			final String type;
			if (entityAndType.length == 1) {
				entity = entityAndType[0];
				type = null;
			} else {
				entity = entityAndType[0];
				type = entityAndType[1];
			}

			final ContextElement contextElement = new ContextElement();

			boolean first = true;

			final List<ContextAttribute> contextAttributelist = new ArrayList<ContextAttribute>();
			final List<ContextAttribute> synContextAttributelist = Collections
					.synchronizedList(contextAttributelist);

			for (final String attributeName : idsAndAttributeNames.get(id)) {

				if (first) {

					tasks.add(Executors.callable(new Runnable() {
						public void run() {
							ContextElement contextElementTmp = getLatestValue(
									entity, type, attributeName);
							contextElement
									.setAttributeDomainName(contextElementTmp
											.getAttributeDomainName());
							contextElement
									.setContextAttributeList(contextAttributelist);
							synContextAttributelist.addAll(getLatestValue(
									entity, type, attributeName)
									.getContextAttributeList());
							contextElement.setDomainMetadata(contextElementTmp
									.getDomainMetadata());
							contextElement.setEntityId(contextElementTmp
									.getEntityId());
						}
					}));

					first = false;

				} else {
					tasks.add(Executors.callable(new Runnable() {
						public void run() {
							synContextAttributelist.addAll(getLatestValue(
									entity, type, attributeName)
									.getContextAttributeList());
						}
					}));
				}

			}

			contextElementList.add(contextElement);

		}

		ExecutorService taskExecutor = Executors.newCachedThreadPool();
		try {
			taskExecutor.invokeAll(tasks);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return contextElementList;

	}

	/**
	 * This method will calculate a multimap containing all the entities with
	 * its attributes that are matching the EntityId criteria given as input. It
	 * supports pattern and filtering against EntityId type
	 * 
	 * @param entityId
	 * @return
	 */
	private Multimap<String, String> matchingIdsAndAttributeNames(
			List<EntityId> entityIdList, Set<String> attributeNames) {

		Multimap<String, String> idsAndAttributeNames = HashMultimap.create();

		for (EntityId entityId : entityIdList) {

			/*
			 * Filter first against the type (if present)
			 */
			Collection<String> ids;
			//
			// if (entityId.getType() != null
			// && !entityId.getType().toString().isEmpty()) {

			/*
			 * Type present so filter against the type
			 */

			// if (entityId.getIsPattern()) {

			/*
			 * Filter against the entityId pattern
			 */
			if (entityId.getType() != null
					&& !entityId.getType().toString().isEmpty()) {
				ids = cachedIdsByType.get(entityId.getType().toString());
			} else {
				ids = cachedAttributesAndRevisionById.keySet();
			}

			for (String id : ids) {

				/*
				 * Match against a regular expression
				 */
				String[] entityIdAndType = splitEntityAndType(id);
				if ((entityId.getIsPattern() && entityIdAndType[0]
						.matches(entityId.getId()))
						|| entityIdAndType[0].toLowerCase().equals(
								entityId.getId().toLowerCase())) {

					Collection<AttributeNameAndRevision> attributeNamesAndRevisionCollect = cachedAttributesAndRevisionById
							.get(id);

					for (AttributeNameAndRevision attributeNameAndRevision : attributeNamesAndRevisionCollect) {

						if (attributeNames == null
								|| attributeNames.isEmpty()
								|| attributeNames
										.contains(attributeNameAndRevision.attributeName
												.toLowerCase())) {

							idsAndAttributeNames.put(id,
									attributeNameAndRevision.attributeName);

						}
					}

				}

			}

		}

		return idsAndAttributeNames;
	}

	// private List<String> getAttributeNames(String id) {
	//
	// //
	// http://localhost:5984/bigdatarepositorytmp/_design/attributesById?key="kitchen"
	//
	// List<String> attributeNames = new ArrayList<String>();
	//
	// String url = String.format("%s%s/%s?key=%%22%s%%22", getCouchDB_ip(),
	// couchDB_NAME, View.ATTRIBUTE_BY_ID_VIEW.getQueryPath(), id);
	//
	// try {
	// FullHttpResponse response = HttpRequester.sendGet(new URL(url));
	//
	// if (response.getStatusLine().getStatusCode() == 200
	// && response.getBody() != null
	// && !response.getBody().isEmpty()) {
	//
	// JsonElement jelement = new JsonParser().parse(response
	// .getBody());
	// if (!jelement.isJsonNull()) {
	//
	// JsonObject jobject = jelement.getAsJsonObject();
	//
	// JsonArray rows = jobject.getAsJsonArray("rows");
	//
	// // Parse each row of the response
	// for (JsonElement jsonElement : rows) {
	// JsonObject row = jsonElement.getAsJsonObject();
	//
	// attributeNames.add(row.get("value").getAsString());
	//
	// }
	// }
	// }
	//
	// } catch (MalformedURLException e) {
	// e.printStackTrace();
	// }
	//
	// return attributeNames;
	//
	// }

	@Override
	public List<ContextElement> getHistoricalValues(
			List<EntityId> entityIdList, Date startDate, Date endDate) {

		return this.getHistoricalValues(entityIdList, null, startDate, endDate);

	}

	@Override
	public List<ContextElement> getHistoricalValues(
			List<EntityId> entityIdList, List<String> attributeNames,
			final Date startDate, final Date endDate) {

		List<ContextElement> contextElementList = new ArrayList<ContextElement>();

		final List<ContextElement> synContextElementList = Collections
				.synchronizedList(contextElementList);

		Set<String> attributeNamesSet = new HashSet<String>();
		attributeNamesSet.addAll(attributeNames);

		final Multimap<String, String> idsAndAttributeNames = matchingIdsAndAttributeNames(
				entityIdList, attributeNamesSet);

		// List of Task
		List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();

		/*
		 * For every id create a ContextElement containing all the attributes of
		 * such entityId.id and entityId.type
		 */
		for (final String id : idsAndAttributeNames.keySet()) {

			/*
			 * Extract the entityId.id and the entityId.type
			 */
			String[] entityAndType = splitEntityAndType(id);
			final String entity;
			final String type;
			if (entityAndType.length == 1) {
				entity = entityAndType[0];
				type = null;
			} else {
				entity = entityAndType[0];
				type = entityAndType[1];
			}

			tasks.add(Executors.callable(new Runnable() {

				@Override
				public void run() {
					ContextElement contextElement = null;

					for (String attributeName : idsAndAttributeNames.get(id)) {

						if (contextElement == null) {
							contextElement = getHistoricalValues(entity, type,
									attributeName, startDate, endDate);
						} else {
							contextElement.getContextAttributeList().addAll(
									getHistoricalValues(entity, type,
											attributeName, startDate, endDate)
											.getContextAttributeList());
						}

					}
					if (contextElement != null) {
						synContextElementList.add(contextElement);
					}
				}

			}));

		}

		ExecutorService taskExecutor = Executors.newCachedThreadPool();
		try {
			taskExecutor.invokeAll(tasks);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return contextElementList;
	}

	private void cacheIdsByType() {
		String url = String.format("%s%s/%s", getCouchDB_ip(), couchDB_NAME,
				View.ID_BY_TYPE_VIEW.getQueryPath());

		try {
			FullHttpResponse response = httpRequester.sendGet(new URL(url));

			if (response.getStatusLine().getStatusCode() == 200
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

						cachedIdsByType
								.put(row.get("key").getAsString(),
										row.get("value")
												.getAsString()
												.split(CouchDBUtil.ID_TO_ATTRIBUTENAME_SEPARATOR)[0]);

					}
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private void cacheAttributesAndRevisionById() {

		// ÿ is the last character of the UTF-8 character table
		String url = String.format(
				"%s%s/_all_docs?startkey=%%22%s%%22&endkey=%%22%sÿ%%22",
				getCouchDB_ip(), couchDB_NAME, CouchDBUtil.LATEST_VALUE_PREFIX,
				CouchDBUtil.LATEST_VALUE_PREFIX);

		try {
			FullHttpResponse response = httpRequester.sendGet(new URL(url));

			if (response.getStatusLine().getStatusCode() == 200
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

						String[] entityAndAttributeName = row.get("key")
								.getAsString()
								.split(CouchDBUtil.PREFIX_TO_ID_SEPARATOR)[1]
								.split(CouchDBUtil.ID_TO_ATTRIBUTENAME_SEPARATOR);

						cachedAttributesAndRevisionById.put(
								entityAndAttributeName[0],
								new AttributeNameAndRevision(
										entityAndAttributeName[1], rev));

					}
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean subscribe(final String subscriptionId,
			SubscribeContextRequest subscription) {

		Set<String> attributeNamesSet = new HashSet<String>();
		attributeNamesSet.addAll(subscription.getAttributeList());

		Multimap<String, String> idsAndAttributeNames = matchingIdsAndAttributeNames(
				subscription.getEntityIdList(), attributeNamesSet);

		final List<String> documentKeyToBeNotified = new ArrayList<String>();

		for (Entry<String, String> entry : idsAndAttributeNames.entries()) {

			String latestDocumentKey = generateKeyForLatestValue(
					entry.getKey(), entry.getValue());

			subscriptionIdByLatestDocumentKey.put(latestDocumentKey,
					subscriptionId);

			documentKeyToBeNotified.add(latestDocumentKey);
		}

		new Runnable() {

			@Override
			public void run() {
				issueFirstNotification(subscriptionId, documentKeyToBeNotified);
			}
		}.run();

		return true;
	}

	private void issueFirstNotification(String subscriptionId,
			List<String> documentKeyToBeNotified) {

		// List of Task
		List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();

		List<ContextElement> contextElementsToNotify = new ArrayList<ContextElement>();
		final List<ContextElement> syncContextElementsToNotify = Collections
				.synchronizedList(contextElementsToNotify);

		for (final String documentKey : documentKeyToBeNotified) {
			tasks.add(Executors.callable(new Runnable() {

				@Override
				public void run() {

					syncContextElementsToNotify
							.add(getLatestValue(documentKey));

				}
			}));
		}

		ExecutorService taskExecutor = Executors.newCachedThreadPool();
		try {
			taskExecutor.invokeAll(tasks);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Collection<ContextElement> compactedContextElements = compactContextElements(contextElementsToNotify);

		List<ContextElementResponse> contextElementResponseList = new ArrayList<ContextElementResponse>();
		for (ContextElement contextElement : compactedContextElements) {

			contextElementResponseList.add(new ContextElementResponse(
					contextElement, new StatusCode(Code.OK_200.getCode(),
							ReasonPhrase.OK_200.toString(),
							"New ContextElement")));
		}

		notify(subscriptionId, contextElementResponseList);

	}
}
