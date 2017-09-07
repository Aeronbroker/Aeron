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

package eu.neclab.iotplatform.ngsiemulator.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.neclab.iotplatform.iotbroker.commons.ContentType;
import eu.neclab.iotplatform.iotbroker.commons.FullHttpRequester;
import eu.neclab.iotplatform.iotbroker.commons.ParseUtils;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.RegisterContextRequest;
import eu.neclab.iotplatform.ngsiemulator.utils.Mode;
import eu.neclab.iotplatform.ngsiemulator.utils.NGSIRequester;
import eu.neclab.iotplatform.ngsiemulator.utils.RangesUtil;
import eu.neclab.iotplatform.ngsiemulator.utils.ServerConfiguration;

public class MainIoTProvider {

	private static Logger logger = Logger.getLogger(MainIoTProvider.class);

	// Port Numbers
	private static String portNumbers;

	// Mode of the IoT Provider
	private static Mode mode;

	// Ranges of allowed EntityIds
	private static String rangesOfEntityIds;

	// Number of EntityIds to select amongst the EntityIds.
	private static int numberOfEntityIdsToSelect;

	// Ranges of allowed Attributes
	private static String rangesOfAttributes;

	// Number of Attributes to select amongst the Attributes.
	private static int numberOfAttributesToSelect;

	// Get the IoT Discovery URL
	private static String exposedURL;

	// ContentTypes
	private static ContentType outgoingContentType;
	private static ContentType incomingContentType;

	private static boolean doRegistration;

	private static String queryContextResponseFile;

	private static String notifyContextRequestFile;

	private static String registerContextAvailabilityFile;

	private static int notificationPeriod;

	// Configurations file
	private static String configurationFile = System
			.getProperty("eu.neclab.ioplatform.ngsiemulator.iotprovider.configurationFile");

	// Configurations map
	private static Map<String, String> configurations;

	private static Properties properties;

	// Get the IoT Discovery URL
	private static String iotDiscoveryURL;

	private static Set<Integer> portSet;

	public static void main(String[] args) {

		if (configurationFile != null) {
			readConfigurations(configurationFile);
		}

		properties = System.getProperties();

		setBasicConfigurations();

		// Set<Integer> portSet = RangesUtil.rangesToSet(portNumbers);
		//
		// if (portSet == null) {
		// System.out
		// .println("Wrong eu.neclab.ioplatform.ngsiemulator.iotconsumer.ports property. "
		// +
		// "Allowed only ranges (e.g. 8001-8005) and single ports (e.g. 8001) separated by comma. "
		// +
		// "E.g. -Deu.neclab.ioplatform.ngsiemulator.iotconsumer.ports=8001-8005,8021,8025,8030-8040"
		// + " or similarly in the configuation file ");
		// System.exit(0);
		// }

		NGSIRequester ngsiRequester = new NGSIRequester();

		for (int portNumber : portSet) {

			ServerConfiguration serverConfiguration = getSpecificServerConfiguration(portNumber);

			ServerDummy server = new ServerDummy();

			try {

				server.startServer(portNumber,
						"eu.neclab.iotplatform.ngsiemulator.iotprovider",
						serverConfiguration);

			} catch (BindException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
			}

			if (serverConfiguration.isDoRegistration()) {
				if (serverConfiguration.getMode() == Mode.RANDOM) {

					Set<String> entityNames = serverConfiguration
							.getEntityNames();
					Set<String> attributes = serverConfiguration
							.getAttributeNames();

					RegisterContextRequest registration = createRegisterContextRequest(
							entityNames, attributes, portNumber);

					// TODO use the SouthBound class
					ngsiRequester.doRegistration(registration, ContentType.XML,
							iotDiscoveryURL);

				} else {
					if (registerContextAvailabilityFile != null) {
						String registration = readRegisterContextAvailabilityFile(registerContextAvailabilityFile);
						if (registration != null) {
							try {
								FullHttpRequester.sendPost(new URL(
										iotDiscoveryURL), registration,
										ContentType.XML.toString());
							} catch (MalformedURLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} else {
						try {
							FullHttpRequester.sendPost(new URL(iotDiscoveryURL
									+ "/ngsi9/registerContext"),
									getDefaultRegistration(portNumber),
									ContentType.XML.toString());
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

		}

	}

	private static String readRegisterContextAvailabilityFile(String file) {

		String response = null;

		try {
			response = new Scanner(new File(file)).useDelimiter("\\Z").next();

			if (logger.isDebugEnabled()) {
				logger.debug("Registration read from file: " + response);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;

	}

	private static void readConfigurations(String file) {

		configurations = new HashMap<String, String>();

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.matches("#.*")) {
					continue;
				}
				if (line.matches(".*=.*")) {
					String[] keyValue = line.split("=");
					configurations.put(keyValue[0],
							keyValue[1].replace("\"", ""));
				} else {
					if (!line.trim().isEmpty()) {
						logger.warn("Wrong property in the configuration file: "
								+ line);
					}
				}
			}
		} catch (FileNotFoundException e) {
			logger.warn("FileNotFoundException: Impossible to read configuration file: "
					+ configurationFile);
		} catch (IOException e) {
			logger.warn("IOException: Impossible to read configuration file: "
					+ configurationFile);
		}

	}

	private static ServerConfiguration getSpecificServerConfiguration(
			int portNumber) {
		/*
		 * Specific configuration for a specific server
		 */
		Mode serverMode;
		String serverQueryContextResponseFile;
		String serverNotifyContextRequestFile;
		int serverNotificationPeriod;
		ContentType serverOutgoingContentType;
		ContentType serverIncomingContentType;
		boolean serverDoRegistration;

		String serverEntityIdsRange;
		String serverAttributesRange;
		int serverNumberOfEntityIdsToSelect;
		int serverNumberOfAttributesToSelect;

		if (configurations != null) {
			serverMode = Mode
					.fromString(
							configurations
									.get("eu.neclab.ioplatform.ngsiemulator.iotprovider."
											+ portNumber + ".mode"),
							Mode.fromString(
									properties
											.getProperty("eu.neclab.ioplatform.ngsiemulator.iotprovider."
													+ portNumber + ".mode"),
									mode));

			serverQueryContextResponseFile = configurations.getOrDefault(
					"eu.neclab.ioplatform.ngsiemulator.iotprovider."
							+ portNumber + ".queryContextResponseFile",
					properties.getProperty(
							"eu.neclab.ioplatform.ngsiemulator.iotprovider."
									+ portNumber + ".queryContextResponseFile",
							queryContextResponseFile));

			serverNotifyContextRequestFile = configurations.getOrDefault(
					"eu.neclab.ioplatform.ngsiemulator.iotprovider"
							+ portNumber + ".notifyContextRequestFile",
					properties.getProperty(
							"eu.neclab.ioplatform.ngsiemulator.iotprovider"
									+ portNumber + ".notifyContextRequestFile",
							notifyContextRequestFile));

			serverNotificationPeriod = ParseUtils
					.parseIntOrDefault(
							configurations
									.get("eu.neclab.ioplatform.ngsiemulator.iotprovider."
											+ portNumber
											+ ".notificationPeriod"),
							ParseUtils.parseIntOrDefault(
									properties
											.getProperty("eu.neclab.ioplatform.ngsiemulator.iotprovider."
													+ portNumber
													+ ".notificationPeriod"),
									notificationPeriod));

			serverIncomingContentType = ContentType
					.fromString(
							configurations
									.get("eu.neclab.ioplatform.ngsiemulator.iotprovider."
											+ portNumber
											+ ".incomingContentType"),
							ContentType.fromString(
									properties
											.getProperty("eu.neclab.ioplatform.ngsiemulator.iotprovider."
													+ portNumber
													+ ".incomingContentType"),
									incomingContentType));

			serverOutgoingContentType = ContentType
					.fromString(
							configurations
									.get("eu.neclab.ioplatform.ngsiemulator.iotprovider."
											+ portNumber
											+ ".outgoingContentType"),
							ContentType.fromString(
									properties
											.getProperty("eu.neclab.ioplatform.ngsiemulator.iotprovider."
													+ portNumber
													+ ".outgoingContentType"),
									outgoingContentType));

			serverEntityIdsRange = configurations.getOrDefault(
					"eu.neclab.ioplatform.ngsiemulator.iotprovider."
							+ portNumber + ".rangesOfEntityIds",
					properties.getProperty(
							"eu.neclab.ioplatform.ngsiemulator.iotprovider."
									+ portNumber + ".rangesOfEntityIds",
							rangesOfEntityIds));

			serverNumberOfEntityIdsToSelect = ParseUtils
					.parseIntOrDefault(
							configurations
									.get("eu.neclab.ioplatform.ngsiemulator.iotprovider."
											+ portNumber
											+ ".numberOfEntityIdsToSelect"),
							ParseUtils.parseIntOrDefault(
									properties
											.getProperty("eu.neclab.ioplatform.ngsiemulator.iotprovider."
													+ portNumber
													+ ".numberOfEntityIdsToSelect"),
									numberOfEntityIdsToSelect));

			serverAttributesRange = configurations.getOrDefault(
					"eu.neclab.ioplatform.ngsiemulator.iotprovider."
							+ portNumber + ".rangesOfAttributes", properties
							.getProperty(
									"eu.neclab.ioplatform.ngsiemulator.iotprovider."
											+ portNumber
											+ ".rangesOfAttributes",
									rangesOfAttributes));

			serverNumberOfAttributesToSelect = ParseUtils
					.parseIntOrDefault(
							configurations
									.get("eu.neclab.ioplatform.ngsiemulator.iotprovider."
											+ portNumber
											+ ".numberOfAttributesToSelect"),
							ParseUtils.parseIntOrDefault(
									properties
											.getProperty("eu.neclab.ioplatform.ngsiemulator.iotprovider."
													+ portNumber
													+ ".numberOfAttributesToSelect"),
									numberOfAttributesToSelect));

			serverDoRegistration = Boolean
					.parseBoolean(configurations
							.getOrDefault(
									"eu.neclab.ioplatform.ngsiemulator.iotprovider."
											+ portNumber + ".doRegistration",
									properties
											.getProperty(
													"eu.neclab.ioplatform.ngsiemulator.iotprovider."
															+ portNumber
															+ ".doRegistration",
													ServerConfiguration.DEFAULT_DOREGISTRATION)));

		} else {
			serverMode = Mode
					.fromString(
							properties
									.getProperty("eu.neclab.ioplatform.ngsiemulator.iotprovider."
											+ portNumber + ".mode"), mode);

			serverQueryContextResponseFile = properties.getProperty(
					"eu.neclab.ioplatform.ngsiemulator.iotprovider."
							+ portNumber + ".queryContextResponseFile",
					queryContextResponseFile);

			serverNotifyContextRequestFile = properties.getProperty(
					"eu.neclab.ioplatform.ngsiemulator.iotprovider"
							+ portNumber + ".notifyContextRequestFile",
					notifyContextRequestFile);

			serverNotificationPeriod = ParseUtils
					.parseIntOrDefault(
							properties
									.getProperty("eu.neclab.ioplatform.ngsiemulator.iotprovider."
											+ portNumber
											+ ".notificationPeriod"),
							notificationPeriod);

			serverIncomingContentType = ContentType
					.fromString(
							properties
									.getProperty("eu.neclab.ioplatform.ngsiemulator.iotprovider."
											+ portNumber
											+ ".incomingContentType"),
							ServerConfiguration.DEFAULT_INCOMINGCONTENTTYPE);

			serverOutgoingContentType = ContentType
					.fromString(
							properties
									.getProperty("eu.neclab.ioplatform.ngsiemulator.iotprovider."
											+ portNumber
											+ ".outgoingContentType"),
							ServerConfiguration.DEFAULT_OUTGOINGCONTENTTYPE);

			serverEntityIdsRange = properties.getProperty(
					"eu.neclab.ioplatform.ngsiemulator.iotprovider."
							+ portNumber + ".rangesOfEntityIds",
					rangesOfEntityIds);

			serverNumberOfEntityIdsToSelect = ParseUtils
					.parseIntOrDefault(
							properties
									.getProperty("eu.neclab.ioplatform.ngsiemulator.iotprovider."
											+ portNumber
											+ ".numberOfEntityIdsToSelect"),
							numberOfEntityIdsToSelect);

			serverAttributesRange = properties.getProperty(
					"eu.neclab.ioplatform.ngsiemulator.iotprovider."
							+ portNumber + ".rangesOfAttributes",
					rangesOfAttributes);

			serverNumberOfAttributesToSelect = ParseUtils
					.parseIntOrDefault(
							properties
									.getProperty("eu.neclab.ioplatform.ngsiemulator.iotprovider."
											+ portNumber
											+ ".numberOfAttributesToSelect"),
							numberOfAttributesToSelect);

			serverDoRegistration = ParseUtils.parseBooleanOrDefault(properties.getProperty(
					"eu.neclab.ioplatform.ngsiemulator.iotprovider."
							+ portNumber + ".doRegistration"),
							doRegistration);
		}

		ServerConfiguration serverConfigurations = new ServerConfiguration();
		serverConfigurations.setPort(portNumber);
		serverConfigurations.setMode(serverMode);
		serverConfigurations
				.setQueryContextResponseFile(serverQueryContextResponseFile);
		serverConfigurations
				.setNotifyContextRequestFile(serverNotifyContextRequestFile);
		serverConfigurations.setNotificationPeriod(serverNotificationPeriod);
		serverConfigurations.setIncomingContentType(serverIncomingContentType);
		serverConfigurations.setOutgoingContentType(serverOutgoingContentType);

		if (serverDoRegistration && serverMode == Mode.RANDOM) {
			Set<String> entityNames = chooseEntityNames(serverEntityIdsRange,
					serverNumberOfEntityIdsToSelect);
			Set<String> attributeNames = chooseAttributes(
					serverAttributesRange, serverNumberOfAttributesToSelect);
			serverConfigurations.setAttributeNames(attributeNames);
			serverConfigurations.setEntityNames(entityNames);
			serverConfigurations.setDoRegistration(serverDoRegistration);
		}

		return serverConfigurations;
	}

	private static void setBasicConfigurations() {

		/*
		 * Port Numbers
		 */
		if (configurations != null) {

			portNumbers = configurations
					.getOrDefault(
							"eu.neclab.ioplatform.ngsiemulator.iotprovider.ports",
							properties
									.getProperty(
											"eu.neclab.ioplatform.ngsiemulator.iotprovider.ports",
											ServerConfiguration.DEFAULT_PORTNUMBERS));
		} else {
			portNumbers = properties.getProperty(
					"eu.neclab.ioplatform.ngsiemulator.iotprovider.ports",
					ServerConfiguration.DEFAULT_PORTNUMBERS);
		}
		portSet = RangesUtil.rangesToSet(portNumbers);

		/*
		 * defaultMode
		 */
		if (configurations != null) {
			mode = Mode
					.fromString(
							configurations
									.get("eu.neclab.ioplatform.ngsiemulator.iotprovider.mode"),
							Mode.fromString(
									properties
											.getProperty("eu.neclab.ioplatform.ngsiemulator.iotprovider.mode"),
									ServerConfiguration.DEFAULT_MODE));
		} else {
			mode = Mode
					.fromString(
							properties
									.getProperty("eu.neclab.ioplatform.ngsiemulator.iotprovider.mode"),
							ServerConfiguration.DEFAULT_MODE);
		}

		/*
		 * IoT Discovery URL
		 */
		if (configurations != null) {
			iotDiscoveryURL = configurations
					.getOrDefault(
							"eu.neclab.ioplatform.ngsiemulator.iotDiscoveryUrl",
							properties
									.getProperty(
											"eu.neclab.ioplatform.ngsiemulator.iotDiscoveryUrl",
											ServerConfiguration.DEFAULT_IOTDISCOVERYURL));
		} else {
			iotDiscoveryURL = properties.getProperty(
					"eu.neclab.ioplatform.ngsiemulator.iotDiscoveryUrl",
					ServerConfiguration.DEFAULT_IOTDISCOVERYURL);
		}

		/*
		 * Ranges of allowed EntityIds
		 */
		if (configurations != null) {
			rangesOfEntityIds = configurations
					.getOrDefault(
							"eu.neclab.ioplatform.ngsiemulator.iotprovider.rangesOfEntityIds",
							properties
									.getProperty(
											"eu.neclab.ioplatform.ngsiemulator.iotprovider.rangesOfEntityIds",
											ServerConfiguration.DEFAULT_RANGESOFENTITYIDS));
		} else {
			rangesOfEntityIds = properties
					.getProperty(
							"eu.neclab.ioplatform.ngsiemulator.iotprovider.rangesOfEntityIds",
							ServerConfiguration.DEFAULT_RANGESOFENTITYIDS);
		}

		/*
		 * Number of EntityIds to select amongst the EntityIds.
		 */
		if (configurations != null) {
			numberOfEntityIdsToSelect = ParseUtils
					.parseIntOrDefault(
							configurations
									.get("eu.neclab.ioplatform.ngsiemulator.iotprovider.numberOfEntityIdsToSelect"),
							ParseUtils.parseIntOrDefault(
									properties
											.getProperty("eu.neclab.ioplatform.ngsiemulator.iotprovider.numberOfEntityIdsToSelect"),
									ServerConfiguration.DEFAULT_NUMBEROFENTITYIDSTOSELECT));
		} else {
			numberOfEntityIdsToSelect = ParseUtils
					.parseIntOrDefault(
							properties
									.getProperty("eu.neclab.ioplatform.ngsiemulator.iotprovider.numberOfEntityIdsToSelect"),
							ServerConfiguration.DEFAULT_NUMBEROFENTITYIDSTOSELECT);
		}

		/*
		 * Ranges of allowed Attributes
		 */
		if (configurations != null) {
			rangesOfAttributes = configurations
					.getOrDefault(
							"eu.neclab.ioplatform.ngsiemulator.iotprovider.rangesOfAttributes",
							properties
									.getProperty(
											"eu.neclab.ioplatform.ngsiemulator.iotprovider.rangesOfAttributes",
											ServerConfiguration.DEFAULT_RANGESOFATTRIBUTES));
		} else {
			rangesOfAttributes = properties
					.getProperty(
							"eu.neclab.ioplatform.ngsiemulator.iotprovider.rangesOfAttributes",
							ServerConfiguration.DEFAULT_RANGESOFATTRIBUTES);
		}

		/*
		 * Number of Attributes to select amongst the Attributes.
		 */
		if (configurations != null) {
			numberOfAttributesToSelect = ParseUtils
					.parseIntOrDefault(
							configurations
									.get("eu.neclab.ioplatform.ngsiemulator.iotprovider.numberOfAttributesToSelect"),
							ParseUtils.parseIntOrDefault(
									properties
											.getProperty("eu.neclab.ioplatform.ngsiemulator.iotprovider.numberOfAttributesToSelect"),
									ServerConfiguration.DEFAULT_NUMBEROFATTRIBUTESTOSELECT));
		} else {
			numberOfAttributesToSelect = ParseUtils
					.parseIntOrDefault(
							properties
									.getProperty("eu.neclab.ioplatform.ngsiemulator.iotprovider.numberOfAttributesToSelect"),
							ServerConfiguration.DEFAULT_NUMBEROFATTRIBUTESTOSELECT);
		}

		/*
		 * ExposedUrl
		 */
		if (configurations != null) {
			exposedURL = configurations
					.getOrDefault(
							"eu.neclab.ioplatform.ngsiemulator.iotprovider.exposedURL",
							properties
									.getProperty(
											"eu.neclab.ioplatform.ngsiemulator.iotprovider.exposedURL",
											ServerConfiguration.DEFAULT_EXPOSEDURL));
		} else {
			exposedURL = properties.getProperty(
					"eu.neclab.ioplatform.ngsiemulator.iotprovider.exposedURL",
					ServerConfiguration.DEFAULT_EXPOSEDURL);
		}

		/*
		 * doRegistration
		 */
		if (configurations != null) {
			doRegistration = Boolean
					.parseBoolean(configurations
							.getOrDefault(
									"eu.neclab.ioplatform.ngsiemulator.iotprovider.doRegistration",
									properties
											.getProperty(
													"eu.neclab.ioplatform.ngsiemulator.iotprovider.doRegistration",
													ServerConfiguration.DEFAULT_DOREGISTRATION)));
		} else {
			doRegistration = Boolean
					.parseBoolean(properties
							.getProperty(
									"eu.neclab.ioplatform.ngsiemulator.iotprovider.doRegistration",
									ServerConfiguration.DEFAULT_DOREGISTRATION));
		}

		/*
		 * notificationPeriod
		 */
		if (configurations != null) {
			notificationPeriod = ParseUtils
					.parseIntOrDefault(
							configurations
									.get("eu.neclab.ioplatform.ngsiemulator.iotprovider.notificationPeriod"),
							ParseUtils.parseIntOrDefault(
									properties
											.getProperty("eu.neclab.ioplatform.ngsiemulator.iotprovider.notificationPeriod"),
									ServerConfiguration.DEFAULT_NOTIFICATIONPERIOD));
		} else {
			notificationPeriod = ParseUtils
					.parseIntOrDefault(
							properties
									.getProperty("eu.neclab.ioplatform.ngsiemulator.iotprovider.notificationPeriod"),
							ServerConfiguration.DEFAULT_NOTIFICATIONPERIOD);
		}

		/*
		 * queryContextResponseFile
		 */
		if (configurations != null) {
			queryContextResponseFile = configurations
					.getOrDefault(
							"eu.neclab.ioplatform.ngsiemulator.iotprovider.queryContextResponseFile",
							properties
									.getProperty(
											"eu.neclab.ioplatform.ngsiemulator.iotprovider.queryContextResponseFile",
											ServerConfiguration.DEFAULT_QUERYCONTEXTRESPONSEFILE));
		} else {
			queryContextResponseFile = properties
					.getProperty(
							"eu.neclab.ioplatform.ngsiemulator.iotprovider.queryContextResponseFile",
							ServerConfiguration.DEFAULT_QUERYCONTEXTRESPONSEFILE);
		}

		/*
		 * notifyContextRequestFile
		 */
		if (configurations != null) {
			notifyContextRequestFile = configurations
					.getOrDefault(
							"eu.neclab.ioplatform.ngsiemulator.iotprovider.notifyContextRequestFile",
							properties
									.getProperty(
											"eu.neclab.ioplatform.ngsiemulator.iotprovider.notifyContextRequestFile",
											ServerConfiguration.DEFAULT_NOTIFYCONTEXTREQUESTFILE));
		} else {
			notifyContextRequestFile = properties
					.getProperty(
							"eu.neclab.ioplatform.ngsiemulator.iotprovider.notifyContextRequestFile",
							ServerConfiguration.DEFAULT_NOTIFYCONTEXTREQUESTFILE);
		}

		/*
		 * registerContextAvailabilityFile
		 */
		if (configurations != null) {
			registerContextAvailabilityFile = configurations
					.getOrDefault(
							"eu.neclab.ioplatform.ngsiemulator.iotprovider.registerContextAvailabilityFile",
							properties
									.getProperty("eu.neclab.ioplatform.ngsiemulator.iotprovider.registerContextAvailabilityFile"));
		} else {
			registerContextAvailabilityFile = properties
					.getProperty("eu.neclab.ioplatform.ngsiemulator.iotprovider.registerContextAvailabilityFile");
		}

		/*
		 * outgoingContentType
		 */
		if (configurations != null) {
			outgoingContentType = ContentType
					.fromString(
							configurations
									.get("eu.neclab.ioplatform.ngsiemulator.iotprovider.outgoingContentType"),
							ContentType.fromString(
									properties
											.getProperty("eu.neclab.ioplatform.ngsiemulator.iotprovider.outgoingContentType"),
									ServerConfiguration.DEFAULT_OUTGOINGCONTENTTYPE));
		} else {
			outgoingContentType = ContentType
					.fromString(
							properties
									.getProperty("eu.neclab.ioplatform.ngsiemulator.iotprovider.outgoingContentType"),
							ServerConfiguration.DEFAULT_OUTGOINGCONTENTTYPE);
		}

		/*
		 * incomingContentType
		 */
		if (configurations != null) {
			incomingContentType = ContentType
					.fromString(
							configurations
									.get("eu.neclab.ioplatform.ngsiemulator.iotprovider.incomingContentType"),
							ContentType.fromString(
									properties
											.getProperty("eu.neclab.ioplatform.ngsiemulator.iotprovider.incomingContentType"),
									ServerConfiguration.DEFAULT_INCOMINGCONTENTTYPE));
		} else {
			incomingContentType = ContentType
					.fromString(
							properties
									.getProperty("eu.neclab.ioplatform.ngsiemulator.iotprovider.incomingContentType"),
							ServerConfiguration.DEFAULT_INCOMINGCONTENTTYPE);
		}

	}

	private static Set<String> chooseEntityNames() {
		return chooseEntityNames(rangesOfEntityIds, numberOfEntityIdsToSelect);
	}

	private static Set<String> chooseEntityNames(String rangesOfEntityIds,
			int numberOfEntityIdsToSelect) {

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
		return chooseAttributes(rangesOfAttributes, numberOfAttributesToSelect);
	}

	private static Set<String> chooseAttributes(String rangesOfAttributes,
			int numberOfAttributesToSelect) {

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
				ref = exposedURL + ":" + port + "/ngsi10/";
				;
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

	private static String getDefaultRegistration(int port) {
		String registration = ServerConfiguration.DEFAULT_REGISTERCONTEXTAVAILABILITY;
		try {
			String ref;
			if (exposedURL != null) {
				ref = exposedURL + ":" + port + "/ngsi10/";
				;
			} else {
				ref = "http://" + InetAddress.getLocalHost().getHostAddress()
						+ ":" + port + "/ngsi10/";
			}

			registration = registration.replace(
					"PROVIDINGAPPLICATION_PLACEHOLDER", ref);

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return registration;
	}
}