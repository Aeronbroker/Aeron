package eu.neclab.iotplatform.mocks.server;

import java.net.BindException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.neclab.iotplatform.iotbroker.commons.ContentType;
import eu.neclab.iotplatform.mocks.utils.Mode;
import eu.neclab.iotplatform.mocks.utils.NGSIRequester;
import eu.neclab.iotplatform.mocks.utils.RangesUtil;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.RegisterContextRequest;

public class MainIoTProvider {

	private static Logger logger = Logger.getLogger(MainIoTProvider.class);

	private static String portNumbers = System.getProperty(
			"eu.neclab.ioplatform.mocks.iotprovider.ports", "8001");

	// Mode of the IoT Provider
	private static final Mode defaultMode = Mode.fromString(System
			.getProperty("eu.neclab.ioplatform.mocks.iotprovider.defaultMode"),
			Mode.RANDOM);

	// Ranges of allowed EntityIds
	private static final String rangesOfEntityIds = System
			.getProperty(
					"eu.neclab.ioplatform.mocks.iotprovider.rangesOfEntityIds",
					"1-100");

	// Number of EntityIds to select amongst the EntityIds.
	private static final int numberOfEntityIdsToSelect = Integer
			.parseInt(System
					.getProperty(
							"eu.neclab.ioplatform.mocks.iotprovider.numberOfEntityIdsToSelect",
							"10"));

	// Ranges of allowed Attributes
	private static final String rangesOfAttributes = System.getProperty(
			"eu.neclab.ioplatform.mocks.iotprovider.rangesOfAttributes",
			"1-100");

	// Number of Attributes to select amongst the Attributes.
	private static final int numberOfAttributesToSelect = Integer
			.parseInt(System
					.getProperty(
							"eu.neclab.ioplatform.mocks.iotprovider.numberOfAttributesToSelect",
							"10"));

	// Get the IoT Discovery URL
	private static String exposedURL = System
			.getProperty("eu.neclab.ioplatform.mocks.iotprovider.exposedURL");

	public static void main(String[] args) {

		Set<Integer> portSet = RangesUtil.rangesToSet(portNumbers);

		if (portSet == null) {
			System.out
					.println("Wrong eu.neclab.ioplatform.mocks.iotconsumer.ports property. "
							+ "Allowed only ranges (e.g. 8001-8005) and single ports (e.g. 8001) separated by comma. "
							+ "E.g. -Deu.neclab.ioplatform.mocks.iotconsumer.ports=8001-8005,8021,8025,8030-8040");
			System.exit(0);
		}

		NGSIRequester ngsiRequester = new NGSIRequester();

		for (int portNumber : portSet) {
			ServerDummy server = new ServerDummy();

			try {

				server.startServer(portNumber,
						"eu.neclab.iotplatform.mocks.iotprovider");

			} catch (BindException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
			}

			if (Mode.fromString(System
					.getProperty("eu.neclab.ioplatform.mocks.iotprovider."
							+ portNumber + ".mode"), defaultMode) == Mode.RANDOM) {

				Set<String> entityNames = chooseEntityNames();
				Set<String> attributes = chooseAttributes();

				RegisterContextRequest registration = createRegisterContextRequest(
						entityNames, attributes, portNumber);

				// TODO use the SouthBound class
				ngsiRequester.doRegistration(registration, ContentType.XML);
			} else {

			}

		}

	}

	private static Set<String> chooseEntityNames() {

		// This Set will contain the chosen entityIds Set
		Set<String> entityIdSet = new HashSet<String>();

		Object[] entityIdsAvailable = RangesUtil.rangesToSet(rangesOfEntityIds)
				.toArray();

		if (numberOfEntityIdsToSelect >= entityIdsAvailable.length) {
			System.out
					.println("WARN: numberOfEntityIdsToSelect >= entityIdsAvailable.length");
			for (Object id : entityIdsAvailable) {
				entityIdSet.add("EntityId-" + id);
			}
		} else {
			Random rand = new Random();
			int count = 0;
			while (count < numberOfEntityIdsToSelect) {
				if (entityIdSet.add("EntityId-"
						+ entityIdsAvailable[rand
								.nextInt(entityIdsAvailable.length)])) {
					count++;
				}
			}
		}

		return entityIdSet;

	}

	private static Set<String> chooseAttributes() {

		// This Set will contain the chosen attributes Set
		Set<String> attributeSet = new HashSet<String>();

		Object[] attributesAvailable = RangesUtil.rangesToSet(
				rangesOfAttributes).toArray();

		if (numberOfAttributesToSelect >= attributesAvailable.length) {
			System.out
					.println("WARN: numberOfAttributesToSelect >= attributesAvailable.length");
			for (Object attribute : attributesAvailable) {
				attributeSet.add("Attribute-" + attribute);
			}
		} else {
			Random rand = new Random();
			int count = 0;
			while (count < numberOfAttributesToSelect) {
				if (attributeSet.add("Attribute-"
						+ attributesAvailable[rand
								.nextInt(attributesAvailable.length)])) {
					count++;
				}
			}

		}

		return attributeSet;

	}

	private static RegisterContextRequest createRegisterContextRequest(
			Set<String> entityNames, Set<String> attributes, int port) {
		// Create the entityIdList to put into the registration
		List<EntityId> entityIdList = new ArrayList<EntityId>();
		for (String entityName : entityNames) {
			entityIdList.add(new EntityId(entityName, null, false));
		}

		// Create the contextRegistrationAttribute list to put into the
		// registration
		List<ContextRegistrationAttribute> contextRegistrationAttributes = new ArrayList<ContextRegistrationAttribute>();
		for (String attribute : attributes) {
			contextRegistrationAttributes.add(new ContextRegistrationAttribute(
					attribute, null, false, null));
		}

		// Create the context Registration
		ContextRegistration contextRegistration = new ContextRegistration();
		contextRegistration.setListEntityId(entityIdList);
		contextRegistration
				.setListContextRegistrationAttribute(contextRegistrationAttributes);

		// Form the providing application
		URI providingApplication = null;
		try {
			String ref;
			if (exposedURL != null) {
				ref = exposedURL;
			} else {
				ref = "http://" + InetAddress.getLocalHost().getHostAddress()
						+ ":" + port + "/ngsi10/";
			}

			providingApplication = new URI(ref);

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		contextRegistration.setProvidingApplication(providingApplication);

		// Create the contextRegistration list
		List<ContextRegistration> contextRegistrationList = new ArrayList<ContextRegistration>();
		contextRegistrationList.add(contextRegistration);

		// Create the registration
		RegisterContextRequest registration = new RegisterContextRequest();
		registration.setContextRegistrationList(contextRegistrationList);

		return registration;
	}

	// private static void doRegistration(RegisterContextRequest registration) {
	//
	// // Get the IoT Discovery URL
	// String iotDiscoveryURL = System.getProperty(
	// "eu.neclab.ioplatform.mocks.iotprovider.iotDiscoveryUrl",
	// "http://localhost:8065/");
	//
	// RegisterContextResponse output = new RegisterContextResponse();
	//
	// try {
	//
	// Object response = sendRequest(new URL(iotDiscoveryURL), "/"
	// + "ngsi9" + "/" + "registerContext", registration,
	// RegisterContextResponse.class);
	//
	// // If there was an error then a StatusCode has been returned
	// if (response instanceof StatusCode) {
	// output = new RegisterContextResponse(null, null,
	// (StatusCode) response);
	// }
	//
	// // Cast the response
	// output = (RegisterContextResponse) response;
	//
	// } catch (MalformedURLException e) {
	// logger.warn("Malformed URI", e);
	//
	// output = new RegisterContextResponse(null, null, new StatusCode(
	// Code.INTERNALERROR_500.getCode(),
	// ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
	// ReasonPhrase.RECEIVERINTERNALERROR_500.toString()));
	//
	// } catch (IOException e) {
	// logger.warn("I/O Exception", e);
	//
	// output = new RegisterContextResponse(null, null, new StatusCode(
	// Code.INTERNALERROR_500.getCode(),
	// ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
	// ReasonPhrase.RECEIVERINTERNALERROR_500.toString()));
	//
	// } catch (URISyntaxException e) {
	// logger.warn("URISyntaxException", e);
	// }
	// }
	//
	// /**
	// * Calls the QueryContext method on an NGSI-10 server.
	// *
	// * @return A StatusCode if there was an error, otherwise an object of the
	// * expectedResponseClazz
	// *
	// */
	// private static Object sendRequest(URL url, String resource,
	// NgsiStructure request, Class<?> expectedResponseClazz) {
	//
	// ContentType preferredContentType = getCONTENT_TYPE();
	//
	// Object output;
	//
	// try {
	// String correctedResource;
	// if (url.toString().isEmpty() || url.toString().matches(".*/")) {
	// correctedResource = resource;
	// } else {
	// correctedResource = "/" + resource;
	// }
	//
	// FullHttpResponse response = sendPostTryingAllSupportedContentType(
	// new URL(url + correctedResource), request,
	// preferredContentType, correctedResource);
	//
	// if (response.getStatusLine().getStatusCode() == 415) {
	//
	// logger.warn("Content Type is not supported by the receiver! URL: "
	// + url + correctedResource);
	//
	// // TODO make a better usage of the Status Code
	// output = new StatusCode(
	// Code.INTERNALERROR_500.getCode(),
	// ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
	// "Content Type is not supported by the receiver! (application/xml and application/json tried)");
	// return output;
	//
	// }
	//
	// if (response.getStatusLine().getStatusCode() == 500) {
	//
	// logger.warn("Receiver Internal Error. URL: " + url
	// + correctedResource + ". "
	// + response.getStatusLine().getReasonPhrase());
	//
	// // TODO make a better usage of the Status Code
	// output = new StatusCode(Code.INTERNALERROR_500.getCode(),
	// ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
	// "Final receiver internal error: "
	// + response.getStatusLine().getReasonPhrase());
	// return output;
	//
	// }
	//
	// if (response.getStatusLine().getStatusCode() == 503) {
	//
	// logger.warn("Service Unavailable. URL: " + url
	// + correctedResource + ". "
	// + response.getStatusLine().getReasonPhrase());
	//
	// // TODO make a better usage of the Status Code
	// output = new StatusCode(Code.INTERNALERROR_500.getCode(),
	// ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
	// "Receiver service unavailable: "
	// + response.getStatusLine().getReasonPhrase());
	// return output;
	//
	// }
	//
	// // Check if there is a body
	// if (response.getBody() == null || response.getBody().isEmpty()) {
	//
	// logger.warn("Response from remote server empty");
	//
	// // TODO make a better usage of the Status Code
	// output = new StatusCode(Code.INTERNALERROR_500.getCode(),
	// ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
	// "Receiver response empty");
	//
	// return output;
	//
	// }
	//
	// // Get the ContentType of the response
	// ContentType responseContentType = getContentTypeFromResponse(
	// response, preferredContentType);
	//
	// // Check if the message is valid
	// if (response.getBody() != null
	// && !validateMessageBody(response.getBody(),
	// responseContentType.toString(),
	// expectedResponseClazz, ngsi10schema)) {
	//
	// logger.warn("Response from remote server non a valid NGSI message");
	//
	// // TODO make a better usage of the Status Code
	// output = new StatusCode(Code.INTERNALERROR_500.getCode(),
	// ReasonPhrase.RECEIVERINTERNALERROR_500.toString(),
	// "Receiver response non a valid NGSI message");
	//
	// return output;
	//
	// }
	//
	// // Finally parse it
	// output = parseResponse(response.getBody(), responseContentType,
	// expectedResponseClazz);
	//
	// } catch (MalformedURLException e) {
	// logger.warn("Malformed URI", e);
	//
	// // TODO make a better usage of the Status Code
	// output = new StatusCode(Code.INTERNALERROR_500.getCode(),
	// ReasonPhrase.RECEIVERINTERNALERROR_500.toString(), null);
	//
	// }
	//
	// return output;
	//
	// }
}