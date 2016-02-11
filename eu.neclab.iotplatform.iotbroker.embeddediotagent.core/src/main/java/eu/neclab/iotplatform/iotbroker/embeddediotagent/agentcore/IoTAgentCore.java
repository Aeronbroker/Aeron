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
package eu.neclab.iotplatform.iotbroker.embeddediotagent.agentcore;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;

import eu.neclab.iotplatform.iotbroker.commons.BundleUtils;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.EmbeddedAgentRegistryInterface;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.EmbeddedAgentStorageInterface;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.EmbeddedAgentSubscriptionHandlerInterface;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.IoTAgentInterface;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElement;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextRequest;
import eu.neclab.iotplatform.ngsi.api.ngsi10.Ngsi10Interface;

public class IoTAgentCore implements IoTAgentInterface {

	/** The logger. */
	private static Logger logger = Logger.getLogger(IoTAgentCore.class);

	private EmbeddedAgentStorageInterface iotAgentStorage;

	private EmbeddedAgentSubscriptionHandlerInterface subscriptionHandler;

	private EmbeddedAgentRegistryInterface registrationHandler;

	public EmbeddedAgentStorageInterface getIotAgentStorage() {
		return iotAgentStorage;
	}

	public void setIotAgentStorage(EmbeddedAgentStorageInterface iotAgentStorage) {
		this.iotAgentStorage = iotAgentStorage;
	}

	public EmbeddedAgentSubscriptionHandlerInterface getSubscriptionHandler() {
		return subscriptionHandler;
	}

	public void setSubscriptionHandler(
			EmbeddedAgentSubscriptionHandlerInterface subscriptionHandler) {
		this.subscriptionHandler = subscriptionHandler;
	}

	public EmbeddedAgentRegistryInterface getRegistrationHandler() {
		return registrationHandler;
	}

	public void setRegistrationHandler(
			EmbeddedAgentRegistryInterface registrationHandler) {
		this.registrationHandler = registrationHandler;
	}

	public IoTAgentCore() {

	}

	@PostConstruct
	public void postConstruct() {

		/*
		 * Make a generic registration
		 */
		if (BundleUtils.isServiceRegistered(this, registrationHandler)) {
			registrationHandler.makeGenericRegistration();
		}

	}

	@Override
	public void storeData(List<ContextElement> contextElementList) {

		// List of Task
		List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();

		Iterator<ContextElement> iter = contextElementList.iterator();
		while (iter.hasNext()) {

			ContextElement contextElement = iter.next();

			// Create a list of ContextElement where each ContextElement has
			// exactly one ContextAttribute
			final List<ContextElement> isolatedContextElementList = this
					.isolateAttributes(contextElement);

			final Date localDate = new Date();

			// Iterate over the isolatedContextElement list in order to create
			// the tasks used to store data
			for (final ContextElement isolatedContextElement : isolatedContextElementList) {

				tasks.add(Executors.callable(new Runnable() {

					@Override
					public void run() {

						iotAgentStorage.storeHistoricalData(
								isolatedContextElement, localDate);

						iotAgentStorage.storeLatestData(isolatedContextElement);

					}

				}));

			}

			if (BundleUtils.isServiceRegistered(this, subscriptionHandler)) {
				tasks.add(Executors.callable(new Runnable() {

					@Override
					public void run() {
						subscriptionHandler
								.checkSubscriptions(isolatedContextElementList);
					}

				}));
			}
		}

		// Execute the tasks used to store the data
		ExecutorService taskExecutor = Executors.newCachedThreadPool();
		try {
			taskExecutor.invokeAll(tasks);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

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

	@Override
	public ContextElement getLatestValue(String id, URI type,
			String attributeName) {

		if (type == null) {
			return iotAgentStorage.getLatestValue(id, "", attributeName);

		} else {
			return iotAgentStorage.getLatestValue(id, type.toString(),
					attributeName);

		}
	}


	@Override
	public ContextElement getHistoricalValues(String id, URI type,
			String attributeName, Date startDate, Date endDate) {

		return iotAgentStorage.getHistoricalValues(id, type, attributeName,
				startDate, endDate);

	}


	@Override
	public List<ContextElement> getLatestValues(List<EntityId> entityIdList) {

		return iotAgentStorage.getLatestValues(entityIdList, null);

	}

	@Override
	public List<ContextElement> getLatestValues(List<EntityId> entityIdList,
			List<String> attributeNames) {

		return iotAgentStorage.getLatestValues(entityIdList, attributeNames);

	}

	@Override
	public List<ContextElement> getHistoricalValues(
			List<EntityId> entityIdList, Date startDate, Date endDate) {

		return iotAgentStorage.getHistoricalValues(entityIdList, null,
				startDate, endDate);

	}

	@Override
	public List<ContextElement> getHistoricalValues(
			List<EntityId> entityIdList, List<String> attributeNames,
			final Date startDate, final Date endDate) {

		return iotAgentStorage.getHistoricalValues(entityIdList,
				attributeNames, startDate, endDate);

	}

	@Override
	public void subscribe(String subscriptionId,
			SubscribeContextRequest subscription) {
		if (BundleUtils.isServiceRegistered(this, subscriptionHandler)) {
			subscriptionHandler.subscribe(subscriptionId, subscription);
		}
	}

	@Override
	public void unsubscribe(String subscriptionId) {
		if (BundleUtils.isServiceRegistered(this, subscriptionHandler)) {
			subscriptionHandler.unsubscribe(subscriptionId);
		}
	}

	@Override
	public Ngsi10Interface getNgsi10Callback() {
		if (BundleUtils.isServiceRegistered(this, subscriptionHandler)) {
			return subscriptionHandler.getNgsi10Callback();
		} else {
			return null;
		}
	}

	@Override
	public void setNgsi10Callback(Ngsi10Interface ngsi10Callback) {
		if (BundleUtils.isServiceRegistered(this, subscriptionHandler)) {
			subscriptionHandler.setNgsi10Callback(ngsi10Callback);
		}
	}

}
