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

package eu.neclab.iotplatform.iotbroker.embeddediotagent.storage;

import java.net.URI;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

import eu.neclab.iotplatform.iotbroker.commons.Pair;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.EmbeddedAgentIndexerInterface;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.EmbeddedAgentStorageInterface;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.KeyValueStoreInterface;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElement;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.ngsi10.Ngsi10Interface;

public class IoTAgentStorage implements EmbeddedAgentStorageInterface {

	/** The logger. */
	private static Logger logger = Logger.getLogger(IoTAgentStorage.class);

	// Ngsi10Interface to call when a notification needs to be issued
	private Ngsi10Interface ngsi10Callback;

	private ExecutorService taskExecutor = new ThreadPoolExecutor(0, 1500, 60L,
			TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

	private EmbeddedAgentIndexerInterface indexer;

	private KeyValueStoreInterface keyValueStore;

	@Override
	public Ngsi10Interface getNgsi10Callback() {
		return ngsi10Callback;
	}

	@Override
	public void setNgsi10Callback(Ngsi10Interface ngsi10Callback) {
		this.ngsi10Callback = ngsi10Callback;
	}

	public EmbeddedAgentIndexerInterface getIndexer() {
		return indexer;
	}

	public void setIndexer(EmbeddedAgentIndexerInterface indexer) {
		this.indexer = indexer;
	}

	public KeyValueStoreInterface getKeyValueStore() {
		return keyValueStore;
	}

	public void setKeyValueStore(KeyValueStoreInterface keyValueStore) {
		this.keyValueStore = keyValueStore;
	}

	@Override
	public boolean storeLatestData(ContextElement isolatedContextElement) {
		String documentKey = indexer
				.generateKeyForLatestValue(
						isolatedContextElement.getEntityId().getId(),
						(isolatedContextElement.getEntityId().getType() == null) ? null
								: isolatedContextElement.getEntityId()
										.getType().toString(),
						getAttributeNameFromIsolatedContextElement(isolatedContextElement));

		boolean successfullyStored = keyValueStore.updateValue(documentKey,
				isolatedContextElement);

		if (successfullyStored) {
			indexer.index(isolatedContextElement);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format(
						"ContextElement %s \n\tsuccessfully stored",
						isolatedContextElement));
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug(String.format(
						"ContextElement %s \n\tnot successfully stored",
						isolatedContextElement));
			}
		}

		return successfullyStored;

	}

	@Override
	public void storeHistoricalData(ContextElement isolatedContextElement,
			Date defaultDate) {

		Date timestamp = extractTimestamp(isolatedContextElement
				.getContextAttributeList().iterator().next());

		// If no timestamp is found, take the local one.
		if (timestamp == null) {
			timestamp = defaultDate;
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("No date found %s",
						isolatedContextElement.toJsonString()));
			}
		}

		final String historicalDataDocumentKey = indexer
				.generateKeyForHistoricalData(isolatedContextElement
						.getEntityId().getId(), (isolatedContextElement
						.getEntityId().getType() == null) ? null
						: isolatedContextElement.getEntityId().getType()
								.toString(), isolatedContextElement
						.getContextAttributeList().iterator().next().getName(),
						timestamp);

		// Store the observation as historical document
		keyValueStore.storeValue(historicalDataDocumentKey,
				isolatedContextElement);

	}

	private String getAttributeNameFromIsolatedContextElement(
			ContextElement isolatedContextElement) {
		return Iterables.getFirst(
				isolatedContextElement.getContextAttributeList(), null)
				.getName();
	}

	private Date extractTimestamp(ContextAttribute contextAttribute) {

		Date timestamp = null;

		// TODO encapsulate this things in a Timestamp class, or create a
		// ContextMetadataFactory

		if (contextAttribute.getMetadata() != null
				&& !contextAttribute.getMetadata().isEmpty()) {

			for (ContextMetadata contextMetadata : contextAttribute
					.getMetadata()) {

				if (contextMetadata.getName() != null
						&& (contextMetadata.getName().equalsIgnoreCase(
								"creation_time") || contextMetadata.getName()
								.equalsIgnoreCase("endtime"))) {

					/*
					 * This contextMetadata is set by the leafengine connector
					 */

					// example timestamp "2015.05.29 19:24:28:769 +0000"
					// "yyyy.MM.dd HH:mm:ss:SSS Z"

					SimpleDateFormat parserSDF = new SimpleDateFormat(
							"yyyy.MM.dd HH:mm:ss:SSS Z");
					try {
						timestamp = parserSDF.parse(
								(String) contextMetadata.getValue(),
								new ParsePosition(0));
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				} else if (contextMetadata.getName() != null
						&& contextMetadata.getName().equalsIgnoreCase("date")) {

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

	@Override
	public ContextElement getLatestValue(String id, URI type,
			String attributeName) {

		if (type == null) {
			return this.getLatestValue(id, "", attributeName);

		} else {
			return this.getLatestValue(id, type.toString(), attributeName);

		}
	}

	@Override
	public ContextElement getLatestValue(String entityId, String type,
			String attributeName) {

		return getLatestValue(indexer.generateId(entityId, type), attributeName);

	}

	@Override
	public ContextElement getLatestValue(String id, String attributeName) {

		String latestValueDocumentKey = indexer.generateKeyForLatestValue(id,
				attributeName);
		return this.getLatestValue(latestValueDocumentKey);
	}

	@Override
	public ContextElement getLatestValue(String latestValueDocumentKey) {

		return keyValueStore.getValue(latestValueDocumentKey);

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

	private ContextElement getHistoricalValues(String id, String type,
			String attributeName, Date startDate, Date endDate) {
		ContextElement historicalContextElement = null;

		// If here, an historical range is wanted
		// EXAMPLE:
		// http://localhost:5984/santander-nz-ccoc/_all_docs?startkey=%22obs_urn:x-iot:smartsantander:1:10006|2015-05-08%2016:00:00%22&endkey=%22obs_urn:x-iot:smartsantander:1:10006|2015-05-08%2017:00:00%22&include_docs=true

		String startKey = indexer.generateKeyForHistoricalData(id, type,
				attributeName, startDate);
		String endKey = indexer.generateKeyForHistoricalData(id, type,
				attributeName, endDate);
		historicalContextElement = keyValueStore.getValues(startKey, endKey);

		return historicalContextElement;
	}

	@Override
	public List<ContextElement> getLatestValues(List<EntityId> entityIdList) {

		return this.getLatestValues(entityIdList, null);

	}

	@Override
	public List<ContextElement> getLatestValues(List<EntityId> entityIdList,
			List<String> attributeNames) {

		if (logger.isDebugEnabled()) {
			logger.debug(String.format(
					"Requested Last Values for entities %s and attribute %s",
					entityIdList, attributeNames));
		}

		Collection<ContextElement> contextElementList = new ArrayList<ContextElement>();

		Set<String> attributeNamesSet;
		if (attributeNames != null && !attributeNames.isEmpty()) {
			attributeNamesSet = new HashSet<String>();
			attributeNamesSet.addAll(attributeNames);
		} else {
			attributeNamesSet = null;
		}

		Multimap<String, String> idsAndAttributeNames = indexer
				.matchingIdsAndAttributeNames(entityIdList, attributeNamesSet);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("idsAndAttributeNames found %s",
					idsAndAttributeNames));
		}

		// List of Task
		// List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();

		List<String> documentKeys = new ArrayList<String>();

		// for (String id : idsAndAttributeNames.keySet()) {
		//
		// /*
		// * Extract the entityId.id and the entityId.type
		// */
		// String[] entityAndType = indexer.splitEntityAndType(id);
		// final String entity;
		// final String type;
		// if (entityAndType.length == 1) {
		// entity = entityAndType[0];
		// type = null;
		// } else {
		// entity = entityAndType[0];
		// type = entityAndType[1];
		// }
		//
		// final ContextElement contextElement = new ContextElement();
		//
		// boolean first = true;
		//
		// final List<ContextAttribute> contextAttributelist = new
		// ArrayList<ContextAttribute>();
		// final List<ContextAttribute> synContextAttributelist = Collections
		// .synchronizedList(contextAttributelist);
		//
		// for (final String attributeName : idsAndAttributeNames.get(id)) {
		//
		// if (first) {
		//
		// tasks.add(Executors.callable(new Runnable() {
		// public void run() {
		// ContextElement contextElementTmp = getLatestValue(
		// entity, type, attributeName);
		// contextElement
		// .setAttributeDomainName(contextElementTmp
		// .getAttributeDomainName());
		// contextElement
		// .setContextAttributeList(contextAttributelist);
		// synContextAttributelist.addAll(contextElementTmp
		// .getContextAttributeList());
		// contextElement.setDomainMetadata(contextElementTmp
		// .getDomainMetadata());
		// contextElement.setEntityId(contextElementTmp
		// .getEntityId());
		// }
		// }));
		//
		// first = false;
		//
		// } else {
		// tasks.add(Executors.callable(new Runnable() {
		// public void run() {
		// synContextAttributelist.addAll(getLatestValue(
		// entity, type, attributeName)
		// .getContextAttributeList());
		// }
		// }));
		// }
		//
		// }
		//
		// contextElementList.add(contextElement);
		//
		// }
		//
		// try {
		// taskExecutor.invokeAll(tasks);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }

		for (String id : idsAndAttributeNames.keySet()) {

			/*
			 * Extract the entityId.id and the entityId.type
			 */
			String[] entityAndType = indexer.splitEntityAndType(id);
			final String entity;
			final String type;
			if (entityAndType.length == 1) {
				entity = entityAndType[0];
				type = null;
			} else {
				entity = entityAndType[0];
				type = entityAndType[1];
			}

			for (final String attributeName : idsAndAttributeNames.get(id)) {

				documentKeys.add(indexer.generateKeyForLatestValue(entity,
						type, attributeName));

			}

		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format(
					"Requesting the ids from the Key-Value Storage: %s",
					documentKeys));
		}

		if (!documentKeys.isEmpty()) {

			contextElementList = keyValueStore.getValues(documentKeys);
		} else {
			contextElementList = new ArrayList<ContextElement>();
		}

		Map<String, ContextElement> compactedContextElementsMap = new HashMap<String, ContextElement>();

		if (contextElementList.size() > 2) {
			for (ContextElement contextElement : contextElementList) {

				String entityKey = contextElement.getEntityId().getId()
						+ contextElement.getEntityId().getType();

				if (compactedContextElementsMap.containsKey(entityKey)) {

					compactedContextElementsMap.get(entityKey)
							.getContextAttributeList()
							.addAll(contextElement.getContextAttributeList());

				} else {
					compactedContextElementsMap.put(entityKey, contextElement);
				}

			}
			return new ArrayList<ContextElement>(
					compactedContextElementsMap.values());
		} else {
			return new ArrayList<ContextElement>(contextElementList);
		}

		// try {
		// taskExecutor.invokeAll(tasks);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }

	}

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

		final Multimap<String, String> idsAndAttributeNames = indexer
				.matchingIdsAndAttributeNames(entityIdList, attributeNamesSet);

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
			String[] entityAndType = indexer.splitEntityAndType(id);
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

		try {
			taskExecutor.invokeAll(tasks);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return contextElementList;
	}

	@Override
	public List<ContextElement> getAllLatestValues() {
		Pair<String, String> startAndEndKey = indexer
				.generateStartAndEndKeyForLatestValues();
		return keyValueStore.getAllValues(startAndEndKey.getLeft(),
				startAndEndKey.getRight());

	}
}
