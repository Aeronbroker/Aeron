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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import eu.neclab.iotplatform.iotbroker.storage.LinkSubscriptionInterface;

/**
 * Implementation of {@link LinkSubscriptionInterface}
 *  based on a HyperSqlDb Server.
 */
public class LinkSubStorage implements LinkSubscriptionInterface {

	private static Logger logger = Logger.getLogger(LinkSubStorage.class);
	private Connection c = null;

	@Value("${hsqldb.username}")
	private  String username;
	@Value("${hsqldb.password}")
	private  String password;

	private final static String NAME_DB = "linkDB";

	private final static String URICONNECTION = "jdbc:hsqldb:hsql://localhost/";

	public LinkSubStorage() {
		super();

	}

	@Override
	public void insert(String inID, String outID) {
		PreparedStatement stmt = null;
		try {


				Class.forName("org.hsqldb.jdbc.JDBCDriver");


			c = DriverManager.getConnection(URICONNECTION + NAME_DB, username,
					password);

			stmt = c.prepareStatement("INSERT INTO PAIRS VALUES ( ?,?)");
			stmt.setString(1, inID);
			stmt.setString(2, outID);

			stmt.executeUpdate();

		} catch (SQLException e) {
			logger.info("SQL Exception",e);
		} catch (Exception e) {
			logger.info("ERROR: failed to load HSQLDB JDBC driver.",e);


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
	public void delete(String inID, String outID) {
		PreparedStatement stmt = null;
		try {


				Class.forName("org.hsqldb.jdbc.JDBCDriver");


			c = DriverManager.getConnection(URICONNECTION + NAME_DB, username,
					password);

			stmt = c.prepareStatement("DELETE FROM PUBLIC.PAIRS WHERE INID = ? AND OUTID = ?");

			stmt.setString(1, inID);
			stmt.setString(2, outID);

			stmt.executeUpdate();

		} catch (SQLException e) {
			logger.info("SQL Exception",e);
		} catch (Exception e) {
			logger.info("ERROR: failed to load HSQLDB JDBC driver.",e);


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
	public List<String> getInIDs(String outID) {
		PreparedStatement stmt = null;
		ResultSet result = null;
		List<String> listInID = new ArrayList<String>();
		try {


				Class.forName("org.hsqldb.jdbc.JDBCDriver");


			c = DriverManager.getConnection(URICONNECTION + NAME_DB, username,
					password);

			stmt = c.prepareStatement("SELECT INID  FROM  PUBLIC.PAIRS WHERE OUTID = ?");
			stmt.setString(1, outID);

			result = stmt.executeQuery();

			while (result.next()) {

				listInID.add(result.getString(1).replaceAll("\\s", ""));

			}

		} catch (SQLException e) {
			logger.info("SQL Exception",e);
		} catch (Exception e) {
			logger.info("ERROR: failed to load HSQLDB JDBC driver.",e);


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

		return listInID;
	}

	@Override
	public List<String> getOutIDs(String inID) {
		PreparedStatement stmt = null;
		ResultSet result = null;
		List<String> listInID = new ArrayList<String>();
		try {


				Class.forName("org.hsqldb.jdbc.JDBCDriver");


			c = DriverManager.getConnection(URICONNECTION + NAME_DB, username,
					password);

			stmt = c.prepareStatement("SELECT OUTID FROM PAIRS WHERE INID = ?");
			stmt.setString(1, inID);

			result = stmt.executeQuery();

			while (result.next()) {

				listInID.add(result.getString("OUTID").replaceAll("\\s", ""));

			}

		} catch (SQLException e) {
			logger.info("SQL Exception",e);
		} catch (Exception e) {
			logger.info("ERROR: failed to load HSQLDB JDBC driver.",e);


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

		return listInID;
	}

}
