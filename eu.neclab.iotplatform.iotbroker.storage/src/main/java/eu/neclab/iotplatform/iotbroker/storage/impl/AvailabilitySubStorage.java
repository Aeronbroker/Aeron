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
package eu.neclab.iotplatform.iotbroker.storage.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import eu.neclab.iotplatform.iotbroker.storage.AvailabilitySubscriptionInterface;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextAvailabilityResponse;

/**
 * Implementation of {@link AvailabilitySubscriptionInterface}
 *  based on a HyperSqlDb Server.
 */
public class AvailabilitySubStorage implements
		AvailabilitySubscriptionInterface {

	private static Logger logger = Logger
			.getLogger(AvailabilitySubStorage.class);
	@Value("${hsqldb.username}")
	private String username;
	@Value("${hsqldb.password}")
	private String password ;

	private final static String NAME_DB = "linkDB";

	private final String port = System.getProperty("hsqldb.port");
	private final String URICONNECTION = "jdbc:hsqldb:hsql://localhost:" + port
			+ "/";



	@Override
	public void saveAvalabilitySubscription(
			SubscribeContextAvailabilityResponse request, String id) {

		PreparedStatement stmt = null;
		Connection c = null;
		try {


				Class.forName("org.hsqldb.jdbc.JDBCDriver");

			c = DriverManager.getConnection(URICONNECTION + NAME_DB, username,
					password);

			stmt = c.prepareStatement("INSERT INTO SUBSCRIPTIONAV VALUES ( ? , ? , ?, ?  )");

			stmt.setString(1, id);
			stmt.setString(2, request.toJsonString());
			stmt.setString(3, "");
			stmt.setString(4, "");
			stmt.execute();

		} catch (SQLException e) {
			logger.debug("SQLException", e);
		} catch (ClassNotFoundException e) {
			logger.debug("ClassNotFoundException", e);
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


	@Override
	public void updateAvalabilitySubscription(
			NotifyContextAvailabilityRequest ncaReq, String transitiveList,
			String id) {
		PreparedStatement stmt = null;
		Connection c = null;
		try {


				Class.forName("org.hsqldb.jdbc.JDBCDriver");


			c = DriverManager.getConnection(URICONNECTION + NAME_DB, username,
					password);

			stmt = c.prepareStatement("UPDATE SUBSCRIPTIONAV SET NCA=?,ASSOC=? WHERE SUBAVid=? ");

			stmt.setString(1, ncaReq.toJsonString());
			stmt.setString(2, transitiveList);
			stmt.setString(3, id);
			stmt.execute();

		} catch (SQLException e) {
			logger.debug("SQLException", e);
		}  catch (Exception e) {
			logger.debug("ERROR: failed to load HSQLDB JDBC driver.", e);
		}finally {



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

	@Override
	public void deleteAvalabilitySubscription(String id) {

		PreparedStatement stmt = null;
		Connection c = null;
		try {


				Class.forName("org.hsqldb.jdbc.JDBCDriver");


			c = DriverManager.getConnection(URICONNECTION + NAME_DB, username,
					password);

			stmt = c.prepareStatement("DELETE FROM PUBLIC.SUBSCRIPTIONAV WHERE SUBAVid = ? ");

			stmt.setString(1, id);
			stmt.executeUpdate();

		} catch (SQLException e) {
			logger.debug("SQLException", e);
		} catch (Exception e) {
			logger.debug("ERROR: failed to load HSQLDB JDBC driver.", e);
		}finally {



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


	@Override
	public List<String> getListOfAssociations(String id) {

		PreparedStatement stmt = null;
		Connection c = null;
		ResultSet result = null;
		List<String> listAssoc = new ArrayList<String>();
		try {


			Class.forName("org.hsqldb.jdbc.JDBCDriver");


			c = DriverManager.getConnection(URICONNECTION + NAME_DB, username,
					password);

			stmt = c.prepareStatement("SELECT ASSOC FROM PUBLIC.SUBSCRIPTIONAV WHERE SUBAVid = ? ");

			stmt.setString(1, id);
			result = stmt.executeQuery();

			while (result.next()) {
				listAssoc.add(result.getString(1));

			}

			if(listAssoc.get(0).equals("")){

				listAssoc.clear();

			}


		} catch (SQLException e) {
			logger.debug("SQLException", e);
		}  catch (Exception e) {
			logger.debug("ERROR: failed to load HSQLDB JDBC driver.", e);

		}finally {

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
		return listAssoc;
	}


	@Override
	public void resetDB() {
		Connection c = null;
		PreparedStatement stmt = null;
	
		try {
	
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
	
			c = DriverManager.getConnection(URICONNECTION + NAME_DB, username,
					password);
	
			stmt = c.prepareStatement("DELETE FROM SUBSCRIPTIONAV");
	
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
