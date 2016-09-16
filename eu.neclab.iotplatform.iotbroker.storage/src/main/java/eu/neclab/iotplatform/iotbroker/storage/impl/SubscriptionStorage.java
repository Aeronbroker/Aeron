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

package eu.neclab.iotplatform.iotbroker.storage.impl;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.xml.datatype.DatatypeFactory;

import org.apache.log4j.Logger;
import org.hsqldb.types.Types;
import org.springframework.beans.factory.annotation.Value;

import eu.neclab.iotplatform.iotbroker.commons.DurationUtils;
import eu.neclab.iotplatform.iotbroker.commons.OutgoingSubscriptionWithInfo;
import eu.neclab.iotplatform.iotbroker.commons.SubscriptionWithInfo;
import eu.neclab.iotplatform.iotbroker.storage.SubscriptionStorageInterface;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElement;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.NgsiStructure;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyCondition;
import eu.neclab.iotplatform.ngsi.api.datamodel.Restriction;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextRequest;

/**
 * Implementation of {@link IncomingSubscriptionInterface} based on a HyperSqlDb
 * Server.
 */
public class SubscriptionStorage implements SubscriptionStorageInterface {

	private static Logger logger = Logger.getLogger(SubscriptionStorage.class);

	@Value("${hsqldb.username}")
	private String username;
	@Value("${hsqldb.password}")
	private String password;

	private @Value("${default_throttling}")
	long defaultThrottling;
	private @Value("${default_duration}")
	long defaultDuration;

	private final String NAME_DB = "linkDB";

	private final String url = System.getProperty("hsqldb.url", "localhost");
	private final String port = System.getProperty("hsqldb.port");
	private final String URICONNECTION = "jdbc:hsqldb:hsql://" + url + ":"
			+ port + "/";

	// public SubscriptionStorage() {
	// if (Boolean.parseBoolean(System.getProperty("iotbroker.reset"))) {
	// deleteAll();
	// }
	// }

	@PostConstruct
	public void postConstruct() {

		if (Boolean.parseBoolean(System.getProperty("iotbroker.reset"))) {
			deleteAll();
		}
	}

	private void insertSubscription(Connection c, String subscriptionId,
			SubscribeContextRequest request, URI agentURI,
			String incomingSubscriptionId, long timestamp) {
		PreparedStatement stmt = null;

		try {
			stmt = c.prepareStatement("INSERT INTO subscription (subscriptionid, reference, duration, throttling, linkToIncomingSubscription, agentUri, timestamp) VALUES(?,?,?,?,?,?,?)");

			stmt.setString(1, subscriptionId);

			stmt.setString(2, request.getReference());

			// TODO when using JAVA 1.8 change getTimeInMillis with a more
			// efficient get()
			if (request.getDuration() == null) {
				stmt.setNull(3, Types.BIGINT);
			} else {
				stmt.setLong(3,
						request.getDuration().getTimeInMillis(new Date()));
			}

			// TODO when using JAVA 1.8 change getTimeInMillis with a more
			// efficient get()
			if (request.getThrottling() == null) {
				stmt.setNull(4, Types.BIGINT);
			} else {
				stmt.setLong(4,
						request.getThrottling().getTimeInMillis(new Date()));
			}

			if (incomingSubscriptionId == null) {
				stmt.setNull(5, Types.VARCHAR);
			} else {
				stmt.setString(5, incomingSubscriptionId);
			}

			if (agentURI == null) {
				stmt.setNull(6, Types.VARCHAR);
			} else {
				stmt.setString(6, agentURI.toString());
			}

			// TODO when using JAVA 1.8 change getTimeInMillis with a more
			// efficient get()
			if (timestamp == 0) {
				stmt.setNull(7, Types.BIGINT);
			} else {
				stmt.setLong(7, timestamp);
			}

			stmt.execute();

		} catch (SQLException e) {
			logger.error(e.toString());
		} finally {

			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}
		}
	}

	private void insertEntityId(Connection c, String subscriptionId,
			EntityId entityId) {
		PreparedStatement stmt = null;

		try {
			stmt = c.prepareStatement("INSERT INTO EntityId (subscriptionid, entityid, entityidpattern, type) VALUES(?,?,?,?)");

			stmt.setString(1, subscriptionId);

			if (entityId.getIsPattern()) {
				stmt.setNull(2, Types.VARCHAR);
				stmt.setString(3, entityId.getId());
			} else {
				stmt.setString(2, entityId.getId());
				stmt.setNull(3, Types.VARCHAR);
			}

			if (entityId.getType() == null) {
				stmt.setNull(4, Types.VARCHAR);
			} else {
				stmt.setString(4, entityId.getType().toString());
			}

			stmt.execute();

		} catch (SQLException e) {
			logger.error(e.toString());
		} finally {

			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}
		}
	}

	private void insertAttributes(Connection c, String subscriptionId,
			String attribute) {

		PreparedStatement stmt = null;

		try {
			stmt = c.prepareStatement("INSERT INTO Attribute (subscriptionid, attribute) VALUES(?,?)");

			stmt.setString(1, subscriptionId);

			stmt.setString(2, attribute);

			stmt.execute();

		} catch (SQLException e) {
			logger.error(e.toString());
		} finally {

			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}
		}

	}

	private void insertNotifyCondition(Connection c, String subscriptionId,
			NotifyCondition notifyCondition) {

		// TODO implements in a SQL style. Now it is just stub
		PreparedStatement stmt = null;

		try {
			stmt = c.prepareStatement("INSERT INTO NotifyCondition (subscriptionid, notifyCondition) VALUES(?,?)");

			stmt.setString(1, subscriptionId);

			stmt.setString(2, notifyCondition.toJsonString());

			stmt.execute();

		} catch (SQLException e) {
			logger.error(e.toString());
		} finally {

			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}
		}
	}

	private void insertRestriction(Connection c, String subscriptionId,
			Restriction restriction) {

		// TODO implements in a SQL style. Now it is just stub

		PreparedStatement stmt = null;

		try {
			stmt = c.prepareStatement("INSERT INTO Restriction (subscriptionid, restriction) VALUES(?,?)");

			stmt.setString(1, subscriptionId);

			stmt.setString(2, restriction.toJsonString());

			stmt.execute();

		} catch (SQLException e) {
			logger.error(e.toString());
		} finally {

			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}
		}
	}

	@Override
	public void saveIncomingSubscription(SubscribeContextRequest request,
			String subscriptionId, long timestamp) {

		this.storeSubscription(request, subscriptionId, timestamp, null, null);

	}

	@Override
	public void saveOutgoingSubscription(SubscribeContextRequest request,
			String outgoingSubscriptionID, String incomingSubscriptionID,
			URI agentUri, long timestamp) {

		this.storeSubscription(request, outgoingSubscriptionID, timestamp,
				incomingSubscriptionID, agentUri);

	}

	private void storeSubscription(SubscribeContextRequest request,
			String subscriptionId, long timestamp,
			String linkToIncomingSubscriptionId, URI agentURI) {
		Connection c = null;

		try {

			Class.forName("org.hsqldb.jdbc.JDBCDriver");

			c = DriverManager.getConnection(URICONNECTION + NAME_DB, username,
					password);

			insertSubscription(c, subscriptionId, request, agentURI,
					linkToIncomingSubscriptionId, timestamp);

			for (EntityId entityId : request.getEntityIdList()) {
				insertEntityId(c, subscriptionId, entityId);
			}

			if (request.getAttributeList() != null
					&& !request.getAttributeList().isEmpty()) {
				for (String attribute : request.getAttributeList()) {
					insertAttributes(c, subscriptionId, attribute);
				}
			}

			if (request.getNotifyCondition() != null
					&& !request.getNotifyCondition().isEmpty()) {
				for (NotifyCondition notifyCondition : request
						.getNotifyCondition()) {
					insertNotifyCondition(c, subscriptionId, notifyCondition);
				}
			}

			if (request.getRestriction() != null) {
				insertRestriction(c, subscriptionId, request.getRestriction());
			}

		} catch (Exception e) {
			logger.info("ERROR: failed to load HSQLDB JDBC driver.", e);

		} finally {

			try {
				if (c != null) {
					c.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}

		}

		// System.out.println("IncomingSubscription from database: "
		// + getIncomingSubscription(subscriptionId).toJsonString());
	}

	private void deleteSubscription(Connection c, String subscriptionId) {
		PreparedStatement stmt = null;

		try {
			stmt = c.prepareStatement("DELETE FROM Subscription WHERE subscriptionId=?");

			stmt.setString(1, subscriptionId);

			stmt.execute();

		} catch (SQLException e) {
			logger.error(e.toString());
		} finally {

			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}
		}
	}

	@Override
	public void deleteIncomingSubscription(String subscriptionId) {
		this.deleteSubscription(subscriptionId);

	}

	@Override
	public void deleteOutgoingSubscription(String id) {
		this.deleteSubscription(id);
	}

	private void deleteSubscription(String subscriptionId) {
		Connection c = null;

		try {

			Class.forName("org.hsqldb.jdbc.JDBCDriver");

			c = DriverManager.getConnection(URICONNECTION + NAME_DB, username,
					password);

			deleteSubscription(c, subscriptionId);

		} catch (Exception e) {
			logger.info("ERROR: failed to load HSQLDB JDBC driver.", e);

		} finally {

			try {
				if (c != null) {
					c.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}

		}
	}

	private void deleteAll() {
		Connection c = null;
		PreparedStatement stmt = null;
		try {

			Class.forName("org.hsqldb.jdbc.JDBCDriver");

			c = DriverManager.getConnection(URICONNECTION + NAME_DB, username,
					password);

			stmt = c.prepareStatement("DELETE FROM Subscription");

			stmt.execute();

		} catch (Exception e) {
			logger.info("ERROR: failed to load HSQLDB JDBC driver.", e);

		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}
			try {
				if (c != null) {
					c.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}

		}
	}

	private SubscribeContextRequest getSubscription(Connection c,
			String subscriptionId) {
		SubscribeContextRequest subscription = new SubscribeContextRequest();
		PreparedStatement stmt = null;
		ResultSet result = null;

		try {

			stmt = c.prepareStatement("SELECT * FROM subscription WHERE subscriptionId = ? ");

			stmt.setString(1, subscriptionId);
			result = stmt.executeQuery();

			while (result.next()) {

				subscription.setReference(result.getString("reference"));

				DatatypeFactory dataTypeFactory = DatatypeFactory.newInstance();

				Long duration = result.getLong("duration");
				if (!result.wasNull()) {
					subscription.setDuration(dataTypeFactory
							.newDuration(duration.longValue()));
				}

				Long throttling = result.getLong("throttling");
				if (!result.wasNull()) {
					subscription.setThrottling(dataTypeFactory
							.newDuration(throttling.longValue()));
				}

			}

		} catch (SQLException e) {
			logger.info("SQLException", e);
		} catch (Exception e) {
			logger.info("ERROR: failed to load HSQLDB JDBC driver.", e);

		} finally {

			try {
				if (result != null) {
					result.close();
				}

				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}

		}

		// If there was anything in the database, return null
		if (subscription.getReference() == null) {
			return null;
		}

		return subscription;
	}

	private List<EntityId> getEntityIdList(Connection c, String subscriptionId) {

		List<EntityId> entityIdList = new ArrayList<EntityId>();
		PreparedStatement stmt = null;
		ResultSet result = null;

		try {

			stmt = c.prepareStatement("SELECT * FROM EntityId WHERE subscriptionId = ? ");

			stmt.setString(1, subscriptionId);
			result = stmt.executeQuery();

			while (result.next()) {

				EntityId entityId = new EntityId();

				String id = result.getString("entityId");
				if (!result.wasNull()) {
					entityId.setIsPattern(false);
				} else {
					entityId.setIsPattern(true);
					id = result.getString("entityIdPattern");
				}
				entityId.setId(id);

				String type = result.getString("type");
				if (!result.wasNull()) {
					entityId.setType(new URI(type));
				}

				entityIdList.add(entityId);

			}

		} catch (SQLException e) {
			logger.info("SQLException", e);
		} catch (Exception e) {
			logger.info("ERROR: failed to load HSQLDB JDBC driver.", e);

		} finally {

			try {
				if (result != null) {
					result.close();
				}

				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}

		}
		return entityIdList;
	}

	private List<String> getAttributeNameList(Connection c,
			String subscriptionId) {

		List<String> attributeNames = new ArrayList<String>();
		PreparedStatement stmt = null;
		ResultSet result = null;

		try {

			stmt = c.prepareStatement("SELECT * FROM Attribute WHERE subscriptionId = ? ");

			stmt.setString(1, subscriptionId);
			result = stmt.executeQuery();

			while (result.next()) {

				attributeNames.add(result.getString("attribute"));

			}

		} catch (SQLException e) {
			logger.info("SQLException", e);
		} catch (Exception e) {
			logger.info("ERROR: failed to load HSQLDB JDBC driver.", e);

		} finally {

			try {
				if (result != null) {
					result.close();
				}

				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}

		}
		return attributeNames;
	}

	private Restriction getRestriction(Connection c, String subscriptionId) {

		Restriction restriction = null;
		PreparedStatement stmt = null;
		ResultSet result = null;

		try {

			stmt = c.prepareStatement("SELECT * FROM Restriction WHERE subscriptionId = ? ");

			stmt.setString(1, subscriptionId);
			result = stmt.executeQuery();

			while (result.next()) {

				String json = result.getString("Restriction");

				if (!result.wasNull()) {
					restriction = (Restriction) NgsiStructure
							.parseStringToJson(json, Restriction.class);
				}

			}

		} catch (SQLException e) {
			logger.info("SQLException", e);
		} catch (Exception e) {
			logger.info("ERROR: failed to load HSQLDB JDBC driver.", e);

		} finally {

			try {
				if (result != null) {
					result.close();
				}

				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}

		}
		return restriction;

	}

	private List<NotifyCondition> getNotifyConditionList(Connection c,
			String subscriptionId) {

		List<NotifyCondition> notifyConditionList = new ArrayList<NotifyCondition>();
		PreparedStatement stmt = null;
		ResultSet result = null;

		try {

			stmt = c.prepareStatement("SELECT * FROM NotifyCondition WHERE subscriptionId = ? ");

			stmt.setString(1, subscriptionId);
			result = stmt.executeQuery();

			while (result.next()) {

				String json = result.getString("NotifyCondition");

				if (!result.wasNull()) {
					notifyConditionList.add((NotifyCondition) NgsiStructure
							.parseStringToJson(json, NotifyCondition.class));
				}

			}

		} catch (SQLException e) {
			logger.info("SQLException", e);
		} catch (Exception e) {
			logger.info("ERROR: failed to load HSQLDB JDBC driver.", e);

		} finally {

			try {
				if (result != null) {
					result.close();
				}

				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}

		}

		if (notifyConditionList.isEmpty()) {
			return null;
		} else {
			return notifyConditionList;
		}

	}

	@Override
	public SubscribeContextRequest getIncomingSubscription(String subscriptionId) {
		return this.getSubscription(subscriptionId);
	}

	@Override
	public SubscribeContextRequest getOutgoingSubscription(String subscriptionId) {
		return this.getSubscription(subscriptionId);
	}

	private SubscribeContextRequest getSubscription(String subscriptionId) {
		Connection c = null;
		SubscribeContextRequest request = null;
		try {

			Class.forName("org.hsqldb.jdbc.JDBCDriver");

			c = DriverManager.getConnection(URICONNECTION + NAME_DB, username,
					password);

			request = getSubscription(c, subscriptionId);
			if (request != null) {
				request.setEntityIdList(getEntityIdList(c, subscriptionId));
				request.setAttributeList(getAttributeNameList(c, subscriptionId));
				request.setRestriction(getRestriction(c, subscriptionId));
				request.setNotifyCondition(getNotifyConditionList(c,
						subscriptionId));
			}

		} catch (SQLException e) {
			logger.info("SQLException", e);
		} catch (Exception e) {
			logger.info("ERROR: failed to load HSQLDB JDBC driver.", e);

		} finally {

			try {
				if (c != null) {
					c.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}

		}
		return request;
	}

	@Override
	public OutgoingSubscriptionWithInfo getOutgoingSubscriptionWithMetadata(
			String subscriptionId) {
		Connection c = null;
		OutgoingSubscriptionWithInfo request = null;
		try {

			Class.forName("org.hsqldb.jdbc.JDBCDriver");

			c = DriverManager.getConnection(URICONNECTION + NAME_DB, username,
					password);

			request = getSubscriptionWithMetadata(c, subscriptionId);
			request.setEntityIdList(getEntityIdList(c, subscriptionId));
			request.setAttributeList(getAttributeNameList(c, subscriptionId));
			request.setRestriction(getRestriction(c, subscriptionId));
			request.setNotifyCondition(getNotifyConditionList(c, subscriptionId));

		} catch (SQLException e) {
			logger.info("SQLException", e);
		} catch (Exception e) {
			logger.info("ERROR: failed to load HSQLDB JDBC driver.", e);

		} finally {

			try {
				if (c != null) {
					c.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}

		}
		return request;
	}

	private OutgoingSubscriptionWithInfo getSubscriptionWithMetadata(
			Connection c, String subscriptionId) {
		OutgoingSubscriptionWithInfo subscription = new OutgoingSubscriptionWithInfo();
		PreparedStatement stmt = null;
		ResultSet result = null;

		try {

			stmt = c.prepareStatement("SELECT * FROM subscription WHERE subscriptionId = ? ");

			stmt.setString(1, subscriptionId);
			result = stmt.executeQuery();

			while (result.next()) {

				subscription.setReference(result.getString("reference"));

				DatatypeFactory dataTypeFactory = DatatypeFactory.newInstance();

				Long duration = result.getLong("duration");
				if (!result.wasNull()) {
					subscription.setDuration(dataTypeFactory
							.newDuration(duration.longValue()));
				}

				Long throttling = result.getLong("throttling");
				if (!result.wasNull()) {
					subscription.setThrottling(dataTypeFactory
							.newDuration(throttling.longValue()));
				}

				String agentUri = result.getString("agentUri");
				if (!result.wasNull()) {
					subscription.setAgentURI(new URI(agentUri));
				}

				String linkedIncomingSubId = result
						.getString("linkToIncomingSubscription");
				if (!result.wasNull()) {
					subscription.setIncomingSubscriptionId(linkedIncomingSubId);
				}

			}

		} catch (SQLException e) {
			logger.info("SQLException", e);
		} catch (Exception e) {
			logger.info("ERROR: failed to load HSQLDB JDBC driver.", e);

		} finally {

			try {
				if (result != null) {
					result.close();
				}

				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}

		}
		return subscription;
	}

	@Override
	public List<SubscriptionWithInfo> getAllIncomingSubscription() {

		List<SubscriptionWithInfo> subscriptionWithInfoList = new ArrayList<SubscriptionWithInfo>();

		Connection c = null;
		PreparedStatement stmt = null;
		ResultSet result = null;

		try {

			Class.forName("org.hsqldb.jdbc.JDBCDriver");

			c = DriverManager.getConnection(URICONNECTION + NAME_DB, username,
					password);

			stmt = c.prepareStatement("SELECT subscriptionid, reference, duration, throttling, timestamp FROM subscription");

			result = stmt.executeQuery();

			while (result.next()) {

				SubscriptionWithInfo subscriptionWithInfo = new SubscriptionWithInfo();

				subscriptionWithInfo.setId(result.getString("subscriptionid"));
				subscriptionWithInfo
						.setReference(result.getString("reference"));

				Long originalDuration = result.getLong("duration");
				if (result.wasNull()) {
					originalDuration = null;
				}
				Long timestamp = result.getLong("timestamp");
				if (result.wasNull()) {
					timestamp = null;
				}

				long duration = computeDuration(originalDuration, timestamp);
				if (duration <= 0) {

					logger.info("Subscription expired found in the HSQLDB. It is going to be deleted. Subscriptionid: "
							+ subscriptionWithInfo.getId());

					this.deleteIncomingSubscription(subscriptionWithInfo
							.getId());
					continue;

				} else {
					subscriptionWithInfo.setDuration(DurationUtils
							.convertToDuration(duration));
				}

				Long throttling = result.getLong("throttling");
				if (result.wasNull()) {
					subscriptionWithInfo.setThrottling(DurationUtils
							.convertToDuration(throttling));
				} else {
					subscriptionWithInfo.setThrottling(DurationUtils
							.convertToDuration(defaultThrottling));
				}
				
				subscriptionWithInfoList.add(subscriptionWithInfo);

			}

		} catch (SQLException e) {
			logger.info("SQLException", e);
		} catch (Exception e) {
			logger.info("ERROR: failed to load HSQLDB JDBC driver.", e);

		} finally {

			try {
				if (result != null) {
					result.close();
				}

				if (stmt != null) {
					stmt.close();
				}

				if (c != null) {
					c.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}

		}
		return subscriptionWithInfoList;

	}

	private long computeDuration(Long originalduration, Long timestamp) {

		if (originalduration == null) {
			originalduration = defaultDuration;
		}

		if (timestamp != null) {

			long now = System.currentTimeMillis();
			return originalduration - (now - timestamp);

		} else {

			return originalduration;

		}

	}

	@Override
	public long getTimestamp(String id) {

		long timestamp = 0;

		Connection c = null;
		PreparedStatement stmt = null;
		ResultSet result = null;

		try {

			Class.forName("org.hsqldb.jdbc.JDBCDriver");

			c = DriverManager.getConnection(URICONNECTION + NAME_DB, username,
					password);

			stmt = c.prepareStatement("SELECT timestamp FROM subscription WHERE subscriptionId = ? ");

			stmt.setString(1, id);
			result = stmt.executeQuery();

			while (result.next()) {

				timestamp = result.getLong("timestamp");
			}

		} catch (SQLException e) {
			logger.info("SQLException", e);
		} catch (Exception e) {
			logger.info("ERROR: failed to load HSQLDB JDBC driver.", e);

		} finally {

			try {
				if (result != null) {
					result.close();
				}

				if (stmt != null) {
					stmt.close();
				}

				if (c != null) {
					c.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}

		}
		return timestamp;

	}

	@Override
	public URI getAgentUri(String outSubscriptionId) {
		Connection c = null;

		URI agentUri = null;
		PreparedStatement stmt = null;
		ResultSet result = null;

		try {

			Class.forName("org.hsqldb.jdbc.JDBCDriver");

			c = DriverManager.getConnection(URICONNECTION + NAME_DB, username,
					password);

			stmt = c.prepareStatement("SELECT agentUri FROM subscription WHERE subscriptionId = ? ");

			stmt.setString(1, outSubscriptionId);
			result = stmt.executeQuery();

			while (result.next()) {

				String uri = result.getString("agentUri");
				if (!result.wasNull()) {
					agentUri = new URI(uri);
				}

			}

		} catch (SQLException e) {
			logger.info("SQLException", e);
		} catch (Exception e) {
			logger.info("ERROR: failed to load HSQLDB JDBC driver.", e);

		} finally {

			try {
				if (result != null) {
					result.close();
				}

				if (stmt != null) {
					stmt.close();
				}

				if (c != null) {
					c.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}

		}
		return agentUri;
	}

	@Override
	public void linkSubscriptions(String inID, String outID) {

		Connection c = null;
		PreparedStatement stmt = null;

		try {

			Class.forName("org.hsqldb.jdbc.JDBCDriver");

			c = DriverManager.getConnection(URICONNECTION + NAME_DB, username,
					password);

			stmt = c.prepareStatement("UPDATE subscription SET linkToIncomingSubscription = ? WHERE subscriptionId = ?");

			stmt.setString(1, inID);
			stmt.setString(2, outID);
			stmt.execute();

		} catch (SQLException e) {
			logger.info("SQLException", e);
		} catch (Exception e) {
			logger.info("ERROR: failed to load HSQLDB JDBC driver.", e);

		} finally {

			try {

				if (stmt != null) {
					stmt.close();
				}

				if (c != null) {
					c.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}

		}
	}

	@Override
	public void unlinkSubscriptions(String inD, String outID) {
		Connection c = null;
		PreparedStatement stmt = null;

		try {

			Class.forName("org.hsqldb.jdbc.JDBCDriver");

			c = DriverManager.getConnection(URICONNECTION + NAME_DB, username,
					password);

			stmt = c.prepareStatement("UPDATE subscription SET linkToIncomingSubscription = ? WHERE subscriptionId = ?");

			stmt.setNull(1, Types.VARCHAR);
			stmt.setString(2, outID);
			stmt.execute();

		} catch (SQLException e) {
			logger.info("SQLException", e);
		} catch (Exception e) {
			logger.info("ERROR: failed to load HSQLDB JDBC driver.", e);

		} finally {

			try {

				if (stmt != null) {
					stmt.close();
				}

				if (c != null) {
					c.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}

		}

	}

	@Override
	public String getInID(String outID) {

		String inID = null;

		Connection c = null;
		PreparedStatement stmt = null;
		ResultSet result = null;

		try {

			Class.forName("org.hsqldb.jdbc.JDBCDriver");

			c = DriverManager.getConnection(URICONNECTION + NAME_DB, username,
					password);

			stmt = c.prepareStatement("SELECT linkToIncomingSubscription FROM Subscription WHERE subscriptionId = ? ");

			stmt.setString(1, outID);
			result = stmt.executeQuery();

			while (result.next()) {

				inID = result.getString("linkToIncomingSubscription");

			}

		} catch (SQLException e) {
			logger.info("SQLException", e);
		} catch (Exception e) {
			logger.info("ERROR: failed to load HSQLDB JDBC driver.", e);

		} finally {

			try {
				if (result != null) {
					result.close();
				}

				if (stmt != null) {
					stmt.close();
				}

				if (c != null) {
					c.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}

		}
		return inID;
	}

	@Override
	public List<String> getOutIDs(String inID) {
		List<String> outIDs = new ArrayList<String>();

		Connection c = null;
		PreparedStatement stmt = null;
		ResultSet result = null;

		try {

			Class.forName("org.hsqldb.jdbc.JDBCDriver");

			c = DriverManager.getConnection(URICONNECTION + NAME_DB, username,
					password);

			stmt = c.prepareStatement("SELECT subscriptionId FROM Subscription WHERE linkToIncomingSubscription = ? ");

			stmt.setString(1, inID);
			result = stmt.executeQuery();

			while (result.next()) {

				outIDs.add(result.getString("subscriptionId"));

			}

		} catch (SQLException e) {
			logger.info("SQLException", e);
		} catch (Exception e) {
			logger.info("ERROR: failed to load HSQLDB JDBC driver.", e);

		} finally {

			try {
				if (result != null) {
					result.close();
				}

				if (stmt != null) {
					stmt.close();
				}

				if (c != null) {
					c.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}

		}
		return outIDs;
	}

	@Override
	public List<SubscriptionWithInfo> checkContextElement(
			ContextElement contextElement) {
		// TODO Auto-generated method stub

		// System.out.println(this.getSubscription(
		// "58906-f14Fb-3042b-88441-4F7F1-628Ee61566f5d08989f")
		// .toJsonString());

		// OLD QUERY
		// SELECT * FROM subscription WHERE subscriptionId IN
		// (
		// SELECT subscriptionId FROM entityId WHERE (entityId = 'cc' OR 'cc'
		// like entityIdPattern) AND (subscriptionId IN
		// (
		// SELECT subscriptionId FROM attribute WHERE attribute = 'noise'
		// )
		// )
		// );

		// NEW QUERY
		// SELECT * FROM subscription WHERE subscriptionId IN
		// (
		// SELECT subscriptionId FROM entityId WHERE (entityId = 'cc' OR 'cc'
		// like entityIdPattern)
		// INTERSECT
		// SELECT subscriptionId FROM attribute WHERE attribute IN ('noise')
		// );

		List<SubscriptionWithInfo> subscriptionWithInfoList = new ArrayList<SubscriptionWithInfo>();

		// String idCondition;
		// if (!contextElement.getEntityId().getIsPattern()) {
		// idCondition = String.format(
		// "entityId = '%s' OR '%s' like entityIdPattern",
		// contextElement.getEntityId().getId(), contextElement
		// .getEntityId().getId());
		// } else {
		// idCondition = String
		// .format("'%s' like entityId OR '%s' like entityIdPattern OR entityIdPattern like '%s'",
		// contextElement.getEntityId().getId(),
		// contextElement.getEntityId().getId(),
		// contextElement.getEntityId().getId());
		// }
		//
		// String attributeNameList = "";
		// for (ContextAttribute contextAttribute : contextElement
		// .getContextAttributeList()) {
		// if (!attributeNameList.isEmpty()) {
		// attributeNameList += ",";
		// }
		// attributeNameList += String.format("'%s'",
		// contextAttribute.getName());
		// }
		// if (!attributeNameList.isEmpty()) {
		// attributeNameList = String.format("(%s)", attributeNameList);
		// }

		Connection c = null;
		PreparedStatement stmt = null;
		ResultSet result = null;

		try {

			Class.forName("org.hsqldb.jdbc.JDBCDriver");

			c = DriverManager.getConnection(URICONNECTION + NAME_DB, username,
					password);
			// System.out.println(idCondition);
			// System.out.println(attributeNameList);

			// // Case that the EntityID is not a pattern
			// if (!contextElement.getEntityId().getIsPattern()) {
			//
			// StringBuilder questionMarkList = new StringBuilder();
			// List<String> attributeNames = new ArrayList<String>();
			// for (ContextAttribute contextAttribute : contextElement
			// .getContextAttributeList()) {
			// if (!questionMarkList.toString().isEmpty()) {
			// questionMarkList.append(",");
			// }
			// // Add a ? for each attributeName in order to be then used
			// // in the prepare statement
			// questionMarkList.append("?");
			// attributeNames.add(contextAttribute.getName());
			// }
			//
			// if ()
			// stmt =
			// c.prepareStatement("SELECT subscriptionId FROM subscription WHERE subscriptionId IN"
			// + "("
			// +
			// "SELECT subscriptionId FROM entityId WHERE (entityId = ? OR ? like entityIdPattern)"
			// + "INTERSECT "
			// + "SELECT subscriptionId FROM attribute WHERE attribute IN (" +
			// questionMarkList.toString() + ")"
			// + ")");
			// stmt.setString(1, contextElement.getEntityId().getId());
			// stmt.setString(2, contextElement.getEntityId().getId());
			//
			//
			//
			//
			// } else {
			//
			// }
			//
			// if (!attributeNameList.isEmpty()) {
			//
			// stmt =
			// c.prepareStatement("SELECT subscriptionId FROM subscription WHERE subscriptionId IN"
			// + "("
			// + "SELECT subscriptionId FROM entityId WHERE (?)"
			// + "INTERSECT "
			// + "SELECT subscriptionId FROM attribute WHERE attribute IN (?)"
			// + ")");
			//
			// } else {
			// stmt =
			// c.prepareStatement("SELECT subscriptionId FROM subscription WHERE subscriptionId IN"
			// + "("
			// + "SELECT subscriptionId FROM entityId WHERE (?)"
			// + ")");
			// stmt.setString(1, idCondition);
			// }

			stmt = this
					.createCheckSubscriptionQueryStatement(c, contextElement);
			logger.info("Check Subscription query:" + stmt.toString());
			result = stmt.executeQuery();

			while (result.next()) {

				SubscriptionWithInfo subscriptionWithInfo = null;

				String id = result.getString(1);

				SubscribeContextRequest request = getSubscription(c, id);
				if (request != null) {
					subscriptionWithInfo = new SubscriptionWithInfo(request);
					subscriptionWithInfo
							.setEntityIdList(getEntityIdList(c, id));
					subscriptionWithInfo.setAttributeList(getAttributeNameList(
							c, id));
					subscriptionWithInfo.setRestriction(getRestriction(c, id));
					subscriptionWithInfo
							.setNotifyCondition(getNotifyConditionList(c, id));
					subscriptionWithInfo.setId(id);

					subscriptionWithInfoList.add(subscriptionWithInfo);
				}

			}

		} catch (SQLException e) {
			logger.info("SQLException", e);
		} catch (Exception e) {
			logger.info("ERROR: failed to load HSQLDB JDBC driver.", e);

		} finally {

			if (result != null) {
				try {
					result.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (c != null) {
				try {
					c.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		return subscriptionWithInfoList;
	}

	private PreparedStatement createCheckSubscriptionQueryStatement(
			Connection c, ContextElement contextElement) {

		PreparedStatement stmt = null;

		// This is a first "simply" version of the query
		//
		// Updates kinds (very minimized version):
		// A = { id, (attribute, ....)}
		// B = { id, type, (attribute, ....)}
		//
		// Subscription kinds (very minimized version):
		// 1 = { id }
		// 2 = { id, type}
		// 3 = { id, type, (attribute, ...)}
		// 4 = { id, (attribute, ...)}
		//
		//
		// Possible matches:
		// A -> 1, 4
		// B -> 1, 2, 3, 4

		/*
		 * First we take care about the attribute name list We will put a
		 * question mark for each contextAttribute and then put the
		 * contextAttributeName in the respective list
		 */
		// Case that the EntityID is not a pattern
		StringBuilder questionMarkList = new StringBuilder();
		List<String> attributeNames = new ArrayList<String>();
		for (ContextAttribute contextAttribute : contextElement
				.getContextAttributeList()) {
			if (!questionMarkList.toString().isEmpty()) {
				questionMarkList.append(",");
			}
			// Add a ? for each attributeName in order to be then used
			// in the prepare statement
			questionMarkList.append("?");
			attributeNames.add(contextAttribute.getName());
		}

		try {

			if (contextElement.getEntityId().getType() == null
					|| contextElement.getEntityId().getType().toString()
							.isEmpty()) {
				/*
				 * Case Update A
				 */

				if (!contextElement.getEntityId().getIsPattern()) {
					/*
					 * EntityId is not a pattern
					 */

					stmt = c.prepareStatement("( SELECT subscriptionId FROM entityId WHERE (entityId = ? OR REGEXP_MATCHES(?, entityIdPattern)) AND (type IS NULL) )"
							+ "INTERSECT "
							+ "("
							+ "( SELECT subscriptionId FROM attribute WHERE attribute IN ("
							+ questionMarkList.toString()
							+ ") )"
							+ "UNION"
							+ "( (SELECT subscriptionId FROM subscription) EXCEPT (SELECT subscriptionid FROM attribute) )"
							+ ")");

					stmt.setString(1, contextElement.getEntityId().getId());
					stmt.setString(2, contextElement.getEntityId().getId());

				} else {
					/*
					 * EntityId is a pattern
					 */

					stmt = c.prepareStatement("( SELECT subscriptionId FROM entityId WHERE (? like entityId OR REGEXP_MATCHES(?, entityIdPattern) OR REGEXP_MATCHES(entityIdPattern,?)) AND (type IS NULL) )"
							+ "INTERSECT "
							+ "("
							+ "( SELECT subscriptionId FROM attribute WHERE attribute IN ("
							+ questionMarkList.toString()
							+ ") )"
							+ "UNION"
							+ "( (SELECT subscriptionId FROM subscription) EXCEPT (SELECT subscriptionid FROM attribute) )"
							+ ")");

					stmt.setString(1, contextElement.getEntityId().getId());
					stmt.setString(2, contextElement.getEntityId().getId());
					stmt.setString(3, contextElement.getEntityId().getId());
				}
			} else {
				/*
				 * Case Update B
				 */

				if (!contextElement.getEntityId().getIsPattern()) {
					/*
					 * EntityId is not a pattern
					 */

					// @formatter:off
					stmt = c.prepareStatement("( SELECT subscriptionId FROM entityId WHERE (entityId = ? OR REGEXP_MATCHES(?, entityIdPattern)) AND ( (type IS NULL) OR (type = ?) ) )"
							+ "INTERSECT "
							+ "("
							+ "( SELECT subscriptionId FROM attribute WHERE attribute IN ("
							+ questionMarkList.toString()
							+ ") )"
							+ "UNION"
							+ "( (SELECT subscriptionId FROM subscription) EXCEPT (SELECT subscriptionid FROM attribute) )"
							+ ")");
					// @formatter:on

					stmt.setString(1, contextElement.getEntityId().getId());
					stmt.setString(2, contextElement.getEntityId().getId());
					stmt.setString(3, contextElement.getEntityId().getType()
							.toString());

				} else {
					/*
					 * EntityId is a pattern
					 */

					// @formatter:off
					stmt = c.prepareStatement("( SELECT subscriptionId FROM entityId WHERE (? like entityId OR REGEXP_MATCHES(?, entityIdPattern) OR REGEXP_MATCHES(entityIdPattern,?)) AND ( (type IS NULL) OR (type = ?) ) )"
							+ "INTERSECT "
							+ "("
							+ "( SELECT subscriptionId FROM attribute WHERE attribute IN ("
							+ questionMarkList.toString()
							+ ") )"
							+ "UNION"
							+ "( (SELECT subscriptionId FROM subscription) EXCEPT (SELECT subscriptionid FROM attribute) )"
							+ ")");
					// @formatter:on

					stmt.setString(1, contextElement.getEntityId().getId());
					stmt.setString(2, contextElement.getEntityId().getId());
					stmt.setString(3, contextElement.getEntityId().getId());
					stmt.setString(4, contextElement.getEntityId().getType()
							.toString());

				}
			}

			int offset = (contextElement.getEntityId().getIsPattern() ? 4 : 3);

			offset = ((contextElement.getEntityId().getType() == null || contextElement
					.getEntityId().getType().toString().isEmpty()) ? offset
					: offset + 1);

			for (String attributeName : attributeNames) {
				stmt.setString(offset, attributeName);
				offset++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO MISSING CASE UPDATE B, BUT PROBABLY IT IS NECESSARY ONLY TO DO A
		// SMALL CHANGED IN THE PREVIOUS ONE AND SO MAYBE MERGE THE CONDITION
		/*
		 * Case Update A
		 */

		return stmt;

	}

	@Override
	public void resetDB() {
		Connection c = null;
		PreparedStatement stmt = null;

		try {

			Class.forName("org.hsqldb.jdbc.JDBCDriver");

			c = DriverManager.getConnection(URICONNECTION + NAME_DB, username,
					password);

			stmt = c.prepareStatement("DELETE FROM Subscription");

			stmt.execute();

		} catch (SQLException e) {
			logger.error(e.toString());
		} catch (ClassNotFoundException e) {
			logger.error(e.toString());
		} finally {

			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}
		}

	}
}
