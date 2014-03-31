/*******************************************************************************
 *   Copyright (c) 2014, NEC Europe Ltd.
 *   All rights reserved.
 *   
 *   Authors:
 *           * Salvatore Longo - salvatore.longo@neclab.eu
 *           * Tobias Jacobs - tobias.jacobs@neclab.eu
 *           * Raihan Ul-Islam - raihan.ul-islam@neclab.eu
 *  
 *    Redistribution and use in source and binary forms, with or without
 *    modification, are permitted provided that the following conditions are met:
 *   1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   3. All advertising materials mentioning features or use of this software
 *     must display the following acknowledgement:
 *     This product includes software developed by NEC Europe Ltd.
 *   4. Neither the name of the NEC nor the
 *     names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY NEC ''AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL NEC BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED 
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package eu.neclab.iotplatform.iotbroker.storage.impl;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import eu.neclab.iotplatform.iotbroker.commons.XmlFactory;
import eu.neclab.iotplatform.iotbroker.storage.OutgoingSubscriptionInterface;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextSubscriptionRequest;

/**
 * Implementation of {@link OutgoingSubscriptionInterface} based on a HyperSqlDb
 * Server.
 */
public class OutgoingSubStorage implements OutgoingSubscriptionInterface {

	private static Logger logger = Logger.getLogger(OutgoingSubStorage.class);
	private final XmlFactory xmlfactory = new XmlFactory();
	private static final String JDBC_DRIVER = "org.hsqldb.jdbc.JDBCDriver";

	@Value("${hsqldb.username}")
	private String username;
	@Value("${hsqldb.password}")
	private String password ;

	private static final String NAME_DB = "linkDB";

	private static final String URICONNECTION = "jdbc:hsqldb:hsql://localhost/";

	@Override
	public void saveOutgoingSubscription(SubscribeContextRequest request,
			String id, URI agentUri, long timestamp) {

		Connection c = null;
		PreparedStatement stmt = null;
		try {

			Class.forName(JDBC_DRIVER);

			c = DriverManager.getConnection(URICONNECTION + NAME_DB, username,
					password);

			stmt = c.prepareStatement("INSERT INTO OUTSUBSCRIPTION VALUES ( ?,?,?,?)");

			stmt.setString(1, id);
			stmt.setString(2, request.toString());
			stmt.setString(3, agentUri.toString());
			stmt.setString(4, Long.toString(timestamp));

			stmt.execute();

		} catch (SQLException e) {
			logger.info("SQL Exception", e);
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
	public void deleteOutgoingSubscription(String id) {
		Connection c = null;
		PreparedStatement stmt = null;
		try {

			Class.forName(JDBC_DRIVER);

			c = DriverManager.getConnection(URICONNECTION + NAME_DB, username,
					password);

			stmt = c.prepareCall("DELETE FROM OUTSUBSCRIPTION WHERE OUTSUBID = ?");

			stmt.setString(1, id);
			stmt.executeUpdate();

		} catch (SQLException e) {
			logger.info("SQL Exception", e);
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
	public SubscribeContextRequest getOutgoingSubscription(String originalSubId) {

		SubscribeContextRequest request = null;
		Connection c = null;
		PreparedStatement stmt = null;
		ResultSet result = null;
		try {

			Class.forName(JDBC_DRIVER);

			c = DriverManager.getConnection(URICONNECTION + NAME_DB, username,
					password);

			stmt = c.prepareStatement("SELECT OUTSUBREQ FROM OUTSUBSCRIPTION WHERE OUTSUBID = ?");

			stmt.setString(1, originalSubId);
			result = stmt.executeQuery();
			SubscribeContextRequest scReq = null;
			while (result.next()) {
				String queryResult = result.getString("OUTSUBREQ");
				scReq = (SubscribeContextRequest) xmlfactory
						.convertStringToXml(queryResult,
								SubscribeContextRequest.class);
			}
			return scReq;
		} catch (SQLException e) {
			logger.info("SQL Exception", e);
		} catch (Exception e) {
			logger.info("ERROR: failed to load HSQLDB JDBC driver.", e);

		} finally {

			try {
				if (result != null) {
					result.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}
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
		return request;

	}

	@Override
	public long getTimestamp(String id) {

		return 0;

	}

	@Override
	public URI getAgentUri(String id) {
		URI agentUri = null;
		Connection c = null;
		PreparedStatement stmt = null;
		ResultSet result = null;
		try {

			Class.forName(JDBC_DRIVER);

			c = DriverManager.getConnection(URICONNECTION + NAME_DB, username,
					password);

			stmt = c.prepareStatement("SELECT AGENTURI FROM OUTSUBSCRIPTION WHERE OUTSUBID = ? ");

			stmt.setString(1, id);

			result = stmt.executeQuery();

			while (result.next()) {
				String queryResult = result.getString("AGENTURI");
				agentUri = URI.create(queryResult);
			}

			result.close();

		} catch (SQLException e) {
			logger.info("SQL Exception", e);
		} catch (Exception e) {
			logger.info("ERROR: failed to load HSQLDB JDBC driver.", e);

		} finally {


			try {
				if (result != null) {
					result.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}
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
		return agentUri;
	}

	@Override
	public String getOutID(String sCReq, URI agentURI) {

		Connection c = null;
		PreparedStatement stmt = null;
		ResultSet result = null;
		String queryResult = null;
		try {

			Class.forName(JDBC_DRIVER);

			c = DriverManager.getConnection(URICONNECTION + NAME_DB, username,
					password);

			stmt = c.prepareStatement("SELECT OUTSUBID FROM OUTSUBSCRIPTION WHERE OUTSUBREQ = ? AND AGENTURI=?");

			stmt.setString(1, sCReq);
			stmt.setString(2, agentURI.toString());
			result = stmt.executeQuery();

			while (result.next()) {
				queryResult = result.getString("OUTSUBID");

			}

			result.close();

		} catch (SQLException e) {
			logger.info("SQL Exception", e);
		} catch (Exception e) {
			logger.info("ERROR: failed to load HSQLDB JDBC driver.", e);

		} finally {


			try {
				if (result != null) {
					result.close();
				}
			} catch (SQLException e) {
				logger.info("SQL Exception", e);
			}
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
		return queryResult;
	}
}
