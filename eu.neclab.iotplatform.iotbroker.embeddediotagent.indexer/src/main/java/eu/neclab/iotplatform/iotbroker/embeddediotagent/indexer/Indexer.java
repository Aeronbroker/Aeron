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

package eu.neclab.iotplatform.iotbroker.embeddediotagent.indexer;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

import eu.neclab.iotplatform.iotbroker.commons.interfaces.EmbeddedAgentIndexerInterface;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.KeyValueStoreInterface;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElement;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;

public class Indexer implements EmbeddedAgentIndexerInterface {

	/** The logger. */
	private static Logger logger = Logger.getLogger(Indexer.class);

	public final static String LATEST_VALUE_PREFIX = "entity";
	public final static String HISTORICAL_VALUE_PREFIX = "obs";
	public final static String PREFIX_TO_ID_SEPARATOR = "__";
	public final static String ENTITY_TO_TYPE_SEPARATOR = "~";
	public final static String ID_TO_ATTRIBUTENAME_SEPARATOR = ":::";
	public final static String DOCUMENT_TO_TIMESTAMP_SEPARATOR = "|";

	private Multimap<String, String> cachedIdsByType = HashMultimap.create();

	private Multimap<String, String> cachedAttributeNamesById = HashMultimap
			.create();

	private KeyValueStoreInterface keyValueStore;

	public KeyValueStoreInterface getKeyValueStore() {
		return keyValueStore;
	}

	public void setKeyValueStore(KeyValueStoreInterface keyValueStore) {
		this.keyValueStore = keyValueStore;
	}

	@PostConstruct
	private void postConstruct() {
		this.cacheIdsByType();
		this.cacheAttributeNamesById();
	}

	@Override
	public boolean index(ContextElement isolatedContextElement) {

		if (isolatedContextElement.getContextAttributeList() == null
				|| isolatedContextElement.getContextAttributeList().isEmpty()
				|| isolatedContextElement.getEntityId().getId() == null
				|| isolatedContextElement.getEntityId().getId().isEmpty()) {
			return false;
		}

		String key = generateKeyForLatestValue(isolatedContextElement);

		if (isolatedContextElement.getEntityId().getType() != null) {
			cachedIdsByType.put(isolatedContextElement.getEntityId().getType()
					.toString(), key);
		}
		
		String[] entityAndAttributeName = key.split(PREFIX_TO_ID_SEPARATOR)[1]
				.split(ID_TO_ATTRIBUTENAME_SEPARATOR);

		cachedAttributeNamesById.put(entityAndAttributeName[0],
				entityAndAttributeName[1]);
		
		return true;

	}

	@Override
	public Multimap<String, String> matchingIdsAndAttributeNames(
			List<EntityId> entityIdList, Set<String> attributeNames) {

		Multimap<String, String> idsAndAttributeNames = HashMultimap.create();

		// logger.info("+++++++++++++++++++cachedIdsByType"+cachedIdsByType);
		// logger.info("+++++++++++++++++++cachedAttributeNamesById"+cachedAttributeNamesById);

		for (EntityId entityId : entityIdList) {

			Collection<String> ids;

			/*
			 * Filter first against the type (if present)
			 */
			if (entityId.getType() != null
					&& !entityId.getType().toString().isEmpty()) {
				/*
				 * Type present so filter against the type
				 */
				ids = cachedIdsByType.get(entityId.getType().toString());
			} else {
				ids = cachedAttributeNamesById.keySet();
			}

			/*
			 * Filter against the entityId pattern
			 */
			for (String id : ids) {

				/*
				 * Match against a regular expression
				 */
				String[] entityIdAndType = splitEntityAndType(id);
				if ((entityId.getIsPattern() && entityIdAndType[0]
						.matches(entityId.getId()))
						|| entityIdAndType[0].toLowerCase().equals(
								entityId.getId().toLowerCase())) {

					Collection<String> attributeNamesCollect = cachedAttributeNamesById
							.get(id);

					for (String attributeName : attributeNamesCollect) {

						if (attributeNames == null
								|| attributeNames.isEmpty()
								|| attributeNames.contains(attributeName
										.toLowerCase())) {

							idsAndAttributeNames.put(id, attributeName);

						}
					}

				}

			}

		}

		return idsAndAttributeNames;
	}

	@Override
	public String generateKeyForHistoricalData(String entityId, String type,
			String attributeName, Date timestamp) {

		// example: obs-urn:x-iot:smartsantander:1:3301|2015-05-08 16:36:22

		return generateKeyForHistoricalData(generateId(entityId, type),
				attributeName, timestamp);

	}

	@Override
	public String generateKeyForHistoricalData(String id, String attributeName,
			Date timestamp) {

		return HISTORICAL_VALUE_PREFIX + PREFIX_TO_ID_SEPARATOR + id
				+ ID_TO_ATTRIBUTENAME_SEPARATOR + attributeName
				+ DOCUMENT_TO_TIMESTAMP_SEPARATOR + formatDate(timestamp);

	}

	public String formatDate(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'%20'HH:mm:ss.SSS");

		return dateFormat.format(date);
	}

	@Override
	public String generateKeyForLatestValue(String entityId, String type,
			String attributeName) {

		// example: entity-urn:x-iot:smartsantander:1:3301

		return generateKeyForLatestValue(generateId(entityId, type),
				attributeName);

	}

	@Override
	public String generateKeyForLatestValue(
			ContextElement isolatedContextElement) {

		// example: entity-urn:x-iot:smartsantander:1:3301

		return generateKeyForLatestValue(
				isolatedContextElement.getEntityId().getId(),
				(isolatedContextElement.getEntityId().getType() == null) ? null
						: isolatedContextElement.getEntityId().getType()
								.toString(),
				getAttributeNameFromIsolatedContextElement(isolatedContextElement));

	}

	@Override
	public String generateKeyForLatestValue(String id, String attributeName) {

		return LATEST_VALUE_PREFIX + PREFIX_TO_ID_SEPARATOR + id
				+ ID_TO_ATTRIBUTENAME_SEPARATOR + attributeName;
	}

	@Override
	public String generateId(String entityId, String type) {

		if (type == null || type.isEmpty() || type.equals("")) {

			return entityId;

		} else {

			return entityId + ENTITY_TO_TYPE_SEPARATOR + type;
		}

	}

	@Override
	public String generateId(EntityId entityId) {

		if (entityId.getType() == null
				|| entityId.getType().toString().isEmpty()) {

			return entityId.getId();

		} else {

			return entityId + ENTITY_TO_TYPE_SEPARATOR
					+ entityId.getType().toString();
		}

	}

	@Override
	public String[] splitEntityAndType(String id) {
		return id.split(ENTITY_TO_TYPE_SEPARATOR);
	}

	@Override
	public EntityId getEntityId(String id) {
		EntityId entityId = new EntityId();

		String[] entityAndType = splitEntityAndType(id);
		if (entityAndType.length == 1) {
			entityId.setId(entityAndType[0]);
			entityId.setType(null);
		} else {
			entityId.setId(entityAndType[0]);
			try {
				entityId.setType(new URI(entityAndType[1]));
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return entityId;
	}

	private void cacheIdsByType() {

		cachedIdsByType = keyValueStore.getIdsByType();
	}

	private void cacheAttributeNamesById() {

		// ÿ is the last character of the UTF-8 character table

		for (String key : keyValueStore.getKeys(LATEST_VALUE_PREFIX,
				LATEST_VALUE_PREFIX + "ÿ")) {
			String[] entityAndAttributeName = key.split(PREFIX_TO_ID_SEPARATOR)[1]
					.split(ID_TO_ATTRIBUTENAME_SEPARATOR);

			cachedAttributeNamesById.put(entityAndAttributeName[0],
					entityAndAttributeName[1]);
		}

	}

	private String getAttributeNameFromIsolatedContextElement(
			ContextElement isolatedContextElement) {
		return Iterables.getFirst(
				isolatedContextElement.getContextAttributeList(), null)
				.getName();
	}

}
