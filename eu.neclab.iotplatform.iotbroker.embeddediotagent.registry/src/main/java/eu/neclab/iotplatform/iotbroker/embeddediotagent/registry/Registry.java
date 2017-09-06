package eu.neclab.iotplatform.iotbroker.embeddediotagent.registry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import eu.neclab.iotplatform.iotbroker.commons.interfaces.EmbeddedAgentIndexerInterface;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.EmbeddedAgentRegistryInterface;
import eu.neclab.iotplatform.iotbroker.commons.interfaces.PermanentRegistryInterface;
import eu.neclab.iotplatform.iotbroker.embeddediotagent.registry.comparator.ContextRegistrationAttributeComparator;
import eu.neclab.iotplatform.iotbroker.embeddediotagent.registry.comparator.ContextRegistrationComparator;
import eu.neclab.iotplatform.ngsi.api.datamodel.Circle;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElement;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.MetadataTypes;
import eu.neclab.iotplatform.ngsi.api.datamodel.RegisterContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.RegisterContextResponse;
import eu.neclab.iotplatform.ngsi.api.ngsi9.Ngsi9Interface;

public class Registry implements EmbeddedAgentRegistryInterface {

	/** The logger. */
	private static Logger logger = Logger.getLogger(Registry.class);

	@Value("${agentgeoscope_latitude}")
	private String agentgeoscope_latitude = null;
	@Value("${agentgeoscope_longitude}")
	private String agentgeoscope_longitude = null;
	@Value("${agentgeoscope_radius}")
	private String agentgeoscope_radius = null;

	@Value("${registrations_folder}")
	private String registrations_folder = null;

	@Value("${embeddedAgentId:agent1}")
	private String embeddedAgentId = null;

	private Map<String, LockableRegisterContextRequest> registrationById = new HashMap<String, LockableRegisterContextRequest>();
	private boolean registrationByIdInitialized = false;

	// private final String dirConfig = System.getProperty("dir.config");
	// private final String path = new
	// String("iotbrokerconfig/storageCouchDB/");
	private final String genericRegistrationIdFile = new String(
			"genericRegistrationId.dat");

	private ContextMetadata embeddedAgentIdentifier = null;

	// private final String registrationsFile = new String("registrations.dat");

	private EmbeddedAgentIndexerInterface indexer;

	private Ngsi9Interface ngsi9Client;

	private PermanentRegistryInterface permanentRegistry;

	public PermanentRegistryInterface getPermanentRegistry() {
		return permanentRegistry;
	}

	public void setPermanentRegistry(
			PermanentRegistryInterface permanentRegistry) {
		this.permanentRegistry = permanentRegistry;
	}

	public EmbeddedAgentIndexerInterface getIndexer() {
		return indexer;
	}

	public void setIndexer(EmbeddedAgentIndexerInterface indexer) {
		this.indexer = indexer;
	}

	public Ngsi9Interface getNgsi9Client() {
		return ngsi9Client;
	}

	public void setNgsi9Client(Ngsi9Interface ngsi9Client) {
		this.ngsi9Client = ngsi9Client;
	}

	private LockableRegisterContextRequest getLockableRegisterContextRequestAndLock(
			String id) {

		if (!registrationByIdInitialized) {
			initializeRegistrationById();
		}

		LockableRegisterContextRequest lockableRegisterContextRequest;
		synchronized (registrationById) {
			if (registrationById.containsKey(id)) {
				lockableRegisterContextRequest = registrationById.get(id);
				lockableRegisterContextRequest.lock();
			} else {
				lockableRegisterContextRequest = new LockableRegisterContextRequest(
						getEmbeddedAgentIdentifier());
				registrationById.put(id, lockableRegisterContextRequest);
				lockableRegisterContextRequest.lock();
			}
		}
		return lockableRegisterContextRequest;
	}

	private synchronized void initializeRegistrationById() {

		if (!registrationByIdInitialized) {

			for (Entry<String, RegisterContextRequest> entry : permanentRegistry
					.getAllRegistrations().entrySet()) {

				ContextRegistration contextRegistration = entry.getValue()
						.getContextRegistrationList().iterator().next();

				for (EntityId entityId : contextRegistration.getListEntityId()) {

					LockableRegisterContextRequest lockableRegisterContextRequest = new LockableRegisterContextRequest(
							getEmbeddedAgentIdentifier());
					lockableRegisterContextRequest
							.setContextRegistration(contextRegistration);
					lockableRegisterContextRequest.getRegisterContextRequest()
							.setRegistrationId(
									entry.getValue().getRegistrationId());

					registrationById.put(indexer.generateId(entityId),
							lockableRegisterContextRequest);
				}

			}

			registrationByIdInitialized = true;

		}

	}

	private ContextMetadata getEmbeddedAgentIdentifier() {
		if (embeddedAgentIdentifier == null) {
			embeddedAgentIdentifier = EmbeddedAgentIdentifierFactory
					.getEmbeddedAgentIdentifier(embeddedAgentId);
		}
		return embeddedAgentIdentifier;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.neclab.iotplatform.iotbroker.embeddediotagent.registry.
	 * EmbeddedAgentRegistryInterface#makeGenericRegistration()
	 */
	@Override
	public void makeGenericRegistration() {

		// Try to get the previous registrationId if any
		String registrationId = getGenericRegistrationId();
		if (registrationId != null) {

			logger.info("Trying to refresh generic ResisterContext with id: "
					+ registrationId);
			// Try to refresh the registration
			registrationId = refreshRegistration(registrationId);
		}

		if (registrationId == null) {

			logger.info("Trying to make a generic ResisterContext");

			RegisterContextResponse response = ngsi9Client
					.registerContext(createGenericRegistration());

			if (response.getErrorCode() == null
					|| response.getErrorCode().getCode() == 200) {

				logger.info("Generic ResisterContext done: "
						+ response.getRegistrationId());

				storeRegistrationIdOnFile(response.getRegistrationId());
			} else {
				logger.info("Impossible to make a generic ResisterContext");

			}
		} else {
			storeRegistrationIdOnFile(registrationId);
		}

	}

	private RegisterContextRequest createGenericRegistration() {
		RegisterContextRequest registerContextRequest = new RegisterContextRequest();

		List<ContextRegistration> contextRegistrationList = new ArrayList<ContextRegistration>();
		ContextRegistration contextRegistration = new ContextRegistration();
		contextRegistrationList.add(contextRegistration);

		List<ContextMetadata> contextMetadataList = new ArrayList<ContextMetadata>();
		contextRegistration.setListContextMetadata(contextMetadataList);

		List<EntityId> entityIdList = new ArrayList<EntityId>();
		EntityId entityId = new EntityId(".*", null, true);
		entityIdList.add(entityId);
		contextRegistration.setListEntityId(entityIdList);

		List<ContextRegistrationAttribute> contextRegistrationAttributeList = new ArrayList<ContextRegistrationAttribute>();
		contextRegistration
				.setListContextRegistrationAttribute(contextRegistrationAttributeList);

		if (agentgeoscope_latitude != null && !agentgeoscope_latitude.isEmpty()
				&& agentgeoscope_longitude != null
				&& !agentgeoscope_longitude.isEmpty()
				&& agentgeoscope_radius != null
				&& !agentgeoscope_radius.isEmpty()) {
			logger.info("agentgeoscope_latitude " + agentgeoscope_latitude
					+ " agentgeoscope_longitude " + agentgeoscope_longitude
					+ " agentgeoscope_radius " + agentgeoscope_radius);

			ContextMetadata contextMetadata = new ContextMetadata();
			contextMetadata.setName(MetadataTypes.SimpleGeolocation.getName());
			contextMetadata.setType(MetadataTypes.SimpleGeolocation.getType());
			contextMetadata.setValue(new Circle(Float
					.parseFloat(agentgeoscope_latitude), Float
					.parseFloat(agentgeoscope_longitude), Float
					.parseFloat(agentgeoscope_radius)));
			contextMetadataList.add(contextMetadata);

		}

		registerContextRequest
				.setContextRegistrationList(contextRegistrationList);
		return registerContextRequest;
	}

	private String refreshRegistration(String registrationId) {
		// Create Generic registration
		RegisterContextRequest registerContextRequest = createGenericRegistration();

		registerContextRequest.setRegistrationId(registrationId);

		RegisterContextResponse response = ngsi9Client
				.registerContext(registerContextRequest);

		if (response == null) {
			logger.info("Remote Confman not reachable");
			return null;
		} else if (response.getErrorCode() == null
				|| response.getErrorCode().getCode() == 200) {
			logger.info("ResisterContext refreshed with id: "
					+ response.getRegistrationId());

			return response.getRegistrationId();
		} else {
			logger.info("ResisterContext not found in Confman: "
					+ response.getRegistrationId());

			return null;
		}

	}

	private void storeRegistrationIdOnFile(String registrationId) {
		PrintWriter writer = null;
		try {
			// File file = new File(dirConfig + "/" + path + "/"
			// + genericRegistrationIdFile);
			File file = new File(registrations_folder + "/"
					+ genericRegistrationIdFile);
			if (!file.exists()) {
				file.createNewFile();
			} else {
				file.delete();
				file.createNewFile();
			}
			// writer = new PrintWriter(dirConfig + "/" + path + "/"
			// + genericRegistrationIdFile, "UTF-8");
			writer = new PrintWriter(registrations_folder + "/"
					+ genericRegistrationIdFile, "UTF-8");
			writer.println(registrationId);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}

	}

	private String getGenericRegistrationId() {

		String registrationId = null;

		// File file = new File(dirConfig + "/" + path + "/"
		// + genericRegistrationIdFile);
		File file = new File(registrations_folder + "/"
				+ genericRegistrationIdFile);

		if (!file.exists()) {
			return null;
		} else {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(file));
				String line = null;
				while ((line = reader.readLine()) != null) {
					registrationId = line;
				}
				reader.close();
			} catch (IOException x) {
				System.err.format("IOException: %s%n", x);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		return registrationId;

	}

	public void checkRegistration(List<ContextElement> contextElementList) {

		if (logger.isDebugEnabled()) {
			logger.debug("CheckingRegistration for contextElementList: "
					+ contextElementList);
		}

		if (contextElementList != null && !contextElementList.isEmpty()) {
			if (contextElementList.size() == 1) {
				checkRegistration(contextElementList.get(0));
			} else {
				List<ContextElement> compactedContextElement = compactContextElement(contextElementList);

				for (ContextElement contextElement : compactedContextElement) {
					checkRegistration(contextElement);
				}
			}
		}

	}

	private List<ContextElement> compactContextElement(
			List<ContextElement> contextElementList) {

		Map<EntityId, ContextElement> contextElementById = new HashMap<EntityId, ContextElement>();

		for (ContextElement contextElement : contextElementList) {
			if (!contextElementById.containsKey(contextElement.getEntityId())) {
				contextElementById.put(contextElement.getEntityId(),
						contextElement);
			} else {
				ContextElement compactedContextElement = contextElementById
						.get(contextElement.getEntityId());
				compactedContextElement.getContextAttributeList().addAll(
						contextElement.getContextAttributeList());

				if (contextElement.getAttributeDomainName() != null) {
					compactedContextElement
							.setAttributeDomainName(contextElement
									.getAttributeDomainName());
				}

				if (contextElement.getDomainMetadata() != null
						&& !contextElement.getDomainMetadata().isEmpty()) {

					if (compactedContextElement.getDomainMetadata() != null
							&& !compactedContextElement.getDomainMetadata()
									.isEmpty()) {

						compactedContextElement
								.setDomainMetadata(compactContextMetadata(
										contextElement.getDomainMetadata(),
										compactedContextElement
												.getDomainMetadata()));

					} else {
						compactedContextElement
								.setDomainMetadata(contextElement
										.getDomainMetadata());
					}
				}

			}
		}

		return new ArrayList<ContextElement>(contextElementById.values());

	}

	private List<ContextMetadata> compactContextMetadata(
			List<ContextMetadata> contextMetadataList1,
			List<ContextMetadata> contextMetadataList2) {

		Map<String, ContextMetadata> contextElementByHash = new HashMap<String, ContextMetadata>();

		for (ContextMetadata contextMetadata : contextMetadataList1) {

			if (!contextElementByHash.containsKey(contextMetadata)) {
				contextElementByHash.put(contextMetadata.getName(),
						contextMetadata);
			}
		}

		for (ContextMetadata contextMetadata : contextMetadataList2) {

			if (!contextElementByHash.containsKey(contextMetadata)) {
				contextElementByHash.put(contextMetadata.getName(),
						contextMetadata);
			}
		}

		return new ArrayList<ContextMetadata>(contextElementByHash.values());

	}

	private boolean checkContextElementVsRegistrations(
			ContextElement contextElement, RegisterContextRequest registration) {

		ContextRegistration contextRegistration1 = new ContextRegistration();
		contextRegistration1
				.setListContextMetadata(new ArrayList<ContextMetadata>(
						contextElement.getDomainMetadata()));
		contextRegistration1.getListContextMetadata().add(
				EmbeddedAgentIdentifierFactory
						.getEmbeddedAgentIdentifier(embeddedAgentId));
		contextRegistration1.setListEntityId(new ArrayList<EntityId>());
		contextRegistration1.getListEntityId()
				.add(contextElement.getEntityId());

		ContextRegistrationComparator comparator = new ContextRegistrationComparator();

		ContextRegistration contextRegistration2 = new ContextRegistration();
		contextRegistration2.setListContextMetadata(registration
				.getContextRegistrationList().iterator().next()
				.getListContextMetadata());
		contextRegistration2.setListEntityId(registration
				.getContextRegistrationList().iterator().next()
				.getListEntityId());

		int comparison = comparator.compare(contextRegistration1,
				contextRegistration2);

		if (logger.isDebugEnabled()) {
			logger.debug("CheckingRegistration " + (comparison == 0)
					+ " for ContextElement: " + contextElement
					+ " versus Registration" + registration);
		}

		return comparison == 0;

	}

	public void checkRegistration(ContextElement contextElement) {
		// List<ContextRegistrationAttribute> contextRegistrationAttributeList =
		// generateContextRegistrationAttributeList(contextElement
		// .getContextAttributeList());

		String id = indexer.generateId(contextElement.getEntityId());

		boolean newRegistration = true;

		LockableRegisterContextRequest lockableRegisterContextRequest = getLockableRegisterContextRequestAndLock(id);

		if (logger.isDebugEnabled()) {
			logger.debug("CheckingRegistration for: " + contextElement);
		}

		if (!checkContextElementVsRegistrations(contextElement,
				lockableRegisterContextRequest.getRegisterContextRequest())) {

			if (lockableRegisterContextRequest.getContextRegistration()
					.getListEntityId().isEmpty()) {

				if (logger.isDebugEnabled()) {
					logger.debug("This needs a new registration ContextElement: "
							+ contextElement);
				}

				List<EntityId> entityIdList = new ArrayList<EntityId>();
				EntityId entityId = contextElement.getEntityId();
				entityIdList.add(entityId);

				lockableRegisterContextRequest
						.getContextRegistration()
						.getListContextMetadata()
						.addAll(new ArrayList<ContextMetadata>(contextElement
								.getDomainMetadata()));
				lockableRegisterContextRequest.getContextRegistration()
						.setListEntityId(entityIdList);
				// lockableRegisterContextRequest
				// .getContextRegistration()
				// .setListContextRegistrationAttribute(
				// generateContextRegistrationAttributeList(contextElement
				// .getContextAttributeList()));

			} else {

				newRegistration = false;

				if (contextElement.getDomainMetadata() != null
						&& !contextElement.getDomainMetadata().isEmpty()) {

					if (lockableRegisterContextRequest.getContextRegistration()
							.getListContextMetadata() != null
							&& !lockableRegisterContextRequest
									.getContextRegistration()
									.getListContextMetadata().isEmpty()) {

						lockableRegisterContextRequest
								.getContextRegistration()
								.setListContextMetadata(
										compactContextMetadata(
												lockableRegisterContextRequest
														.getContextRegistration()
														.getListContextMetadata(),
												contextElement
														.getDomainMetadata()));

					} else {
						lockableRegisterContextRequest.getContextRegistration()
								.setListContextMetadata(
										new ArrayList<ContextMetadata>(
												contextElement
														.getDomainMetadata()));
					}
				}
			}

			RegisterContextResponse registerContextResponse = ngsi9Client
					.registerContext(lockableRegisterContextRequest
							.getRegisterContextRequest());

			lockableRegisterContextRequest.getRegisterContextRequest()
					.setRegistrationId(
							registerContextResponse.getRegistrationId());

			if (!newRegistration) {
				permanentRegistry.deleteRegistration(id);
			}
			permanentRegistry.storeRegistration(id,
					lockableRegisterContextRequest.getRegisterContextRequest());

		}

		lockableRegisterContextRequest.unlock();

	}

	// public RegisterContextResponse registerContext(
	// RegisterContextRequest request, URI uri) {
	//
	// /*
	// * This is implemented analogously to queryContext. See the comments
	// * there for clarification.
	// */
	//
	// RegisterContextResponse output = new RegisterContextResponse();
	//
	// try {
	//
	// // get address of local host
	// // InetAddress thisIp = InetAddress.getLocalHost();
	//
	// // initialize http connection
	// URL url = new URL(uri.toString());
	// HttpConnectionClient connection = new HttpConnectionClient();
	//
	// String resource;
	// if (url.toString().matches(".*/")) {
	// resource = "registerContext";
	// } else {
	// resource = "/registerContext";
	// }
	//
	// String respObj = connection.initializeConnection(url, resource,
	// "POST", request, "application/xml", "");
	//
	// if (respObj != null) {
	//
	// output = (RegisterContextResponse) xmlFactory
	// .convertStringToXml(respObj,
	// SubscribeContextResponse.class);
	//
	// return output;
	//
	// }
	//
	// } catch (MalformedURLException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// return output;
	//
	// }

	//
	// private void queueContextRegistration(
	// String id,
	// List<ContextRegistrationAttribute>
	// actualContextRegistrationAttributeList,
	// List<ContextRegistrationAttribute>
	// receivedContextRegistrationAttributeList) {
	// LockableRegisterContextRequest lockableRegisterContextRequest =
	// getLockableRegisterContextRequestAndLock(id);
	//
	//
	//
	// }

	private List<ContextRegistrationAttribute> calculateNewContextRegistrationAttributes(
			String id,
			List<ContextRegistrationAttribute> actualContextRegistrationAttributeList,
			List<ContextRegistrationAttribute> receivedContextRegistrationAttributeList) {

		/*
		 * This list will contain the new ContextRegistrationAttributeList seen
		 */
		List<ContextRegistrationAttribute> newContextRegistrationAttributeList = new ArrayList<ContextRegistrationAttribute>();

		/*
		 * If there is not a previous registration just create an empty array
		 */
		List<ContextRegistrationAttribute> o1List = actualContextRegistrationAttributeList;
		List<ContextRegistrationAttribute> o2List = receivedContextRegistrationAttributeList;

		/*
		 * Sort the lists
		 */
		ContextRegistrationAttributeComparator contextRegistrationAttributeComparator = new ContextRegistrationAttributeComparator();
		Collections.sort(o1List, contextRegistrationAttributeComparator);
		Collections.sort(o2List, contextRegistrationAttributeComparator);

		/*
		 * Create the iterator
		 */
		Iterator<ContextRegistrationAttribute> contextRegistrationIterator1 = o1List
				.iterator();
		Iterator<ContextRegistrationAttribute> contextRegistrationIterator2 = o2List
				.iterator();

		if (!contextRegistrationIterator1.hasNext()) {
			/*
			 * Add all
			 */
			newContextRegistrationAttributeList = contextRegistrationAttributeSet(
					contextRegistrationIterator2,
					contextRegistrationAttributeComparator);
		} else {

			ContextRegistrationAttribute contextRegistrationAttribute1 = contextRegistrationIterator1
					.next();
			ContextRegistrationAttribute contextRegistrationAttribute2 = contextRegistrationIterator2
					.next();

			int comp = contextRegistrationAttributeComparator.compare(
					contextRegistrationAttribute1,
					contextRegistrationAttribute2);

			while (contextRegistrationIterator2.hasNext()) {
				if (comp == 0) {
					if (contextRegistrationIterator1.hasNext()) {
						contextRegistrationAttribute1 = contextRegistrationIterator1
								.next();
					} else {
						newContextRegistrationAttributeList
								.addAll(contextRegistrationAttributeSet(
										contextRegistrationIterator2,
										contextRegistrationAttributeComparator));
						break;
					}
					contextRegistrationAttribute2 = nextDifferentContextRegistrationAttribute(
							contextRegistrationAttribute2,
							contextRegistrationIterator1,
							contextRegistrationAttributeComparator);
					if (contextRegistrationAttribute2 == null) {
						break;
					}
					comp = contextRegistrationAttributeComparator.compare(
							contextRegistrationAttribute1,
							contextRegistrationAttribute2);
				} else if (comp < 0) {
					if (contextRegistrationIterator1.hasNext()) {
						contextRegistrationAttribute1 = contextRegistrationIterator1
								.next();
					} else {
						newContextRegistrationAttributeList
								.addAll(contextRegistrationAttributeSet(
										contextRegistrationIterator2,
										contextRegistrationAttributeComparator));
						break;
					}
					comp = contextRegistrationAttributeComparator.compare(
							contextRegistrationAttribute1,
							contextRegistrationAttribute2);
				} else {
					newContextRegistrationAttributeList
							.add(contextRegistrationAttribute2);

					contextRegistrationAttribute2 = nextDifferentContextRegistrationAttribute(
							contextRegistrationAttribute2,
							contextRegistrationIterator1,
							contextRegistrationAttributeComparator);

					if (contextRegistrationAttribute2 == null) {
						break;
					}
					comp = contextRegistrationAttributeComparator.compare(
							contextRegistrationAttribute1,
							contextRegistrationAttribute2);
				}

			}
		}
		return newContextRegistrationAttributeList;

	}

	// private List<Object> calculateNewTs(
	// String id,
	// List<Object> actualList,
	// List<Object> receivedList) {
	//
	// /*
	// * This list will contain the new ContextRegistrationAttributeList seen
	// */
	// List<Object> newList = new ArrayList<Object>();
	//
	// /*
	// * If there is not a previous registration just create an empty array
	// */
	// List<Object> o1List = actualList;
	// List<Object> o2List = receivedList;
	//
	// /*
	// * Sort the lists
	// */
	// ContextRegistrationAttributeComparator
	// contextRegistrationAttributeComparator = new
	// ContextRegistrationAttributeComparator();
	// Comparator comparator;
	// if (receivedList.get(0) instanceof ContextRegistrationAttribute){
	// comparator = new ContextRegistrationAttributeComparator();
	// } else if (receivedList.get(0) instanceof ContextMetadata){
	// comparator = new ContextMetadataComparator();
	// }
	// Collections.sort(o1List, comparator);
	// Collections.sort(o2List, comparator);
	//
	// /*
	// * Create the iterator
	// */
	// Iterator<Object> iterator1 = o1List
	// .iterator();
	// Iterator<Object> iterator2 = o2List
	// .iterator();
	//
	// if (!iterator1.hasNext()) {
	// /*
	// * Add all
	// */
	// newList = contextRegistrationAttributeSet(
	// iterator2,
	// contextRegistrationAttributeComparator);
	// } else {
	//
	// ContextRegistrationAttribute contextRegistrationAttribute1 = iterator1
	// .next();
	// ContextRegistrationAttribute contextRegistrationAttribute2 = iterator2
	// .next();
	//
	// int comp = contextRegistrationAttributeComparator.compare(
	// contextRegistrationAttribute1,
	// contextRegistrationAttribute2);
	//
	// while (iterator2.hasNext()) {
	// if (comp == 0) {
	// if (iterator1.hasNext()) {
	// contextRegistrationAttribute1 = iterator1
	// .next();
	// } else {
	// newList
	// .addAll(contextRegistrationAttributeSet(
	// iterator2,
	// contextRegistrationAttributeComparator));
	// break;
	// }
	// contextRegistrationAttribute2 =
	// nextDifferentContextRegistrationAttribute(
	// contextRegistrationAttribute2,
	// iterator1,
	// contextRegistrationAttributeComparator);
	// if (contextRegistrationAttribute2 == null) {
	// break;
	// }
	// comp = contextRegistrationAttributeComparator.compare(
	// contextRegistrationAttribute1,
	// contextRegistrationAttribute2);
	// } else if (comp < 0) {
	// if (iterator1.hasNext()) {
	// contextRegistrationAttribute1 = iterator1
	// .next();
	// } else {
	// newList
	// .addAll(contextRegistrationAttributeSet(
	// iterator2,
	// contextRegistrationAttributeComparator));
	// break;
	// }
	// comp = contextRegistrationAttributeComparator.compare(
	// contextRegistrationAttribute1,
	// contextRegistrationAttribute2);
	// } else {
	// newList
	// .add(contextRegistrationAttribute2);
	//
	// contextRegistrationAttribute2 =
	// nextDifferentContextRegistrationAttribute(
	// contextRegistrationAttribute2,
	// iterator1,
	// contextRegistrationAttributeComparator);
	//
	// if (contextRegistrationAttribute2 == null) {
	// break;
	// }
	// comp = contextRegistrationAttributeComparator.compare(
	// contextRegistrationAttribute1,
	// contextRegistrationAttribute2);
	// }
	//
	// }
	// }
	// return newList;
	//
	// }

	private List<ContextRegistrationAttribute> contextRegistrationAttributeSet(
			Iterator<ContextRegistrationAttribute> contextRegistrationAttributeIterator,
			ContextRegistrationAttributeComparator contextRegistrationAttributeComparator) {
		/*
		 * Add all but check duplicates
		 */
		List<ContextRegistrationAttribute> newContextRegistrationAttributeList = new ArrayList<ContextRegistrationAttribute>();

		ContextRegistrationAttribute newContextRegistrationAttribute = contextRegistrationAttributeIterator
				.next();

		newContextRegistrationAttributeList
				.add(newContextRegistrationAttribute);

		while (contextRegistrationAttributeIterator.hasNext()) {
			newContextRegistrationAttribute = nextDifferentContextRegistrationAttribute(
					newContextRegistrationAttribute,
					contextRegistrationAttributeIterator,
					contextRegistrationAttributeComparator);

			if (newContextRegistrationAttribute != null) {
				newContextRegistrationAttributeList
						.add(newContextRegistrationAttribute);
			}
		}

		return newContextRegistrationAttributeList;
	}

	// private List<Object> objectSet(
	// Iterator<Object> iterator,
	// Comparator comparator) {
	// /*
	// * Add all but check duplicates
	// */
	// List<Object> newObjectList = new ArrayList<Object>();
	//
	// Object newObject = iterator
	// .next();
	//
	// newObjectList
	// .add(newObject);
	//
	// while (iterator.hasNext()) {
	// newObject = nextDifferentContextRegistrationAttribute(
	// newObject,
	// iterator,
	// comparator);
	//
	// if (newObject != null) {
	// newObjectList
	// .add(newObject);
	// }
	// }
	//
	// return newObjectList;
	// }

	private ContextRegistrationAttribute nextDifferentContextRegistrationAttribute(
			ContextRegistrationAttribute contextRegistrationAttribute,
			Iterator<ContextRegistrationAttribute> contextRegistrationAttributeIterator,
			ContextRegistrationAttributeComparator contextRegistrationAttributeComparator) {

		while (contextRegistrationAttributeIterator.hasNext()) {

			ContextRegistrationAttribute nextContextRegistrationAttribute = contextRegistrationAttributeIterator
					.next();

			if (contextRegistrationAttributeComparator.compare(
					contextRegistrationAttribute,
					nextContextRegistrationAttribute) != 0) {

				return nextContextRegistrationAttribute;

			}
		}

		return null;
	}

	// private List<ContextRegistrationAttribute>
	// generateContextRegistrationAttributeList(
	// List<ContextAttribute> contextAttributeList) {
	//
	// List<ContextRegistrationAttribute> contextRegistrationAttributeList = new
	// ArrayList<ContextRegistrationAttribute>();
	// for (ContextAttribute contextAttribute : contextAttributeList) {
	// ContextRegistrationAttribute contextRegistrationAttribute = new
	// ContextRegistrationAttribute();
	//
	// contextRegistrationAttribute.setMetaData(contextAttribute
	// .getMetadata());
	// contextRegistrationAttribute.setName(contextAttribute.getName());
	// contextRegistrationAttribute.setType(contextAttribute.getType());
	//
	// contextRegistrationAttributeList.add(contextRegistrationAttribute);
	// }
	//
	// return contextRegistrationAttributeList;
	// }

	@Override
	public List<ContextRegistration> extractOwnContextRegistrations(
			DiscoverContextAvailabilityResponse discoveryResponse) {

		List<ContextRegistration> extractedContextRegistrations = new ArrayList<ContextRegistration>();

		if (discoveryResponse != null
				&& discoveryResponse.getErrorCode().getCode() == 200
				&& discoveryResponse.getContextRegistrationResponse() != null
				&& !discoveryResponse.getContextRegistrationResponse()
						.isEmpty()) {

			Iterator<ContextRegistrationResponse> contextRegistrationResponseIterator = discoveryResponse
					.getContextRegistrationResponse().iterator();

			while (contextRegistrationResponseIterator.hasNext()) {

				ContextRegistrationResponse contextRegistrationResponse = contextRegistrationResponseIterator
						.next();

				for (ContextMetadata contextMetadata : contextRegistrationResponse
						.getContextRegistration().getListContextMetadata()) {

					if (EmbeddedAgentIdentifierFactory.compare(
							getEmbeddedAgentIdentifier(), contextMetadata)) {

						extractedContextRegistrations
								.add(contextRegistrationResponse
										.getContextRegistration());

						contextRegistrationResponseIterator.remove();
						break;

					}
				}
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Extracted Context Registration for agent "
					+ getEmbeddedAgentIdentifier() + ": "
					+ extractedContextRegistrations);
		}

		return extractedContextRegistrations;
	}
}
