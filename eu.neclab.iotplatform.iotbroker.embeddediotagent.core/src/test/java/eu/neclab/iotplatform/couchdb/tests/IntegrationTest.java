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

//package eu.neclab.iotplatform.couchdb.tests;
//
//import java.net.MalformedURLException;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//import eu.neclab.iotplatform.embeddediotagent.http.HttpRequester;
//import eu.neclab.iotplatform.embeddediotagent.util.CouchDBUtil;
//
//public class IntegrationTest {
//
//	private HttpRequester httpRequester = new HttpRequester();
//
//	private CouchDBUtil couchDBUtil = new CouchDBUtil(httpRequester);
//
//	private String couchDB_protocol = "HTTP";
//	private String couchDB_host = "127.0.0.1";
//	private String couchDB_port = "5984";
//	private String couchDB_ip;
//	private String couchdb_name = "bigdatarepositorydisposable";
//	
//	private List<String> updates;
//	
//	private void intializeUpdateList(){
//		updates = new ArrayList<String>();
//		updates.add("{"
//						+"    \"updateAction\": \"UPDATE\","
//						+"    \"contextElements\": [{"
//						+"   \"entityId\": {"
//						+"       \"id\": \"urn:x-iot:smartsantander:1:478\","
//						+"       \"isPattern\": false"
//						+"   },"
//						+"   \"domainMetadata\": ["
//						+"       {"
//						+"           \"name\": \"longitude\","
//						+"           \"type\": \"longitude\","
//						+"           \"value\": \"-3.80485\""
//						+"       },"
//						+"       {"
//						+"           \"name\": \"latitude\","
//						+"           \"type\": \"latitude\","
//						+"           \"value\": \"43.46276\""
//						+"       },"
//						+"       {"
//						+"           \"name\": \"district\","
//						+"           \"type\": \"district\","
//						+"           \"value\": \"1\""
//						+"       },"
//						+"       {"
//						+"           \"name\": \"section\","
//						+"           \"type\": \"section\","
//						+"           \"value\": \"002\""
//						+"       }"
//						+"   ],"
//						+"   \"attributes\": ["
//						+"       {"
//						+"           \"name\": \"battery\","
//						+"           \"type\": \"battery\","
//						+"           \"contextValue\": \"66.00\","
//						+"           \"metadata\": ["
//						+"               {"
//						+"                   \"name\": \"date\","
//						+"                   \"type\": \"date\","
//						+"                   \"value\": \"2015-08-18 16:54:36\""
//						+"               }"
//						+"           ]"
//						+"       }"
//						+"   ]"
//						+"}]"
//						+"}");
//	}
//	
//	
//
//	public String getCouchDB_ip() {
//		if (couchDB_ip == null) {
//			this.setCouchDB_ip();
//		}
//		return couchDB_ip;
//	}
//
//	public void setCouchDB_ip() {
//		couchDB_ip = couchDB_protocol + "://" + couchDB_host + ":"
//				+ couchDB_port + "/";
//	}
//
//	
//	
//	private void resetHSQLDB(){
//		 try {
//		     Class.forName("org.hsqldb.jdbc.JDBCDriver" );
//		 } catch (Exception e) {
//		     System.err.println("ERROR: failed to load HSQLDB JDBC driver.");
//		     e.printStackTrace();
//		     return;
//		 }
//
//		 try {
//			Connection c = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/linkDB", "SA", "");
//			PreparedStatement stmt = null;
//			
//			stmt = c.prepareStatement("DELETE FROM Subscription");
//
//			stmt.execute();
//			
//			stmt = c.prepareStatement("DELETE FROM Pairsav");
//
//			stmt.execute();
//			
//			stmt = c.prepareStatement("DELETE FROM SubscriptionAv");
//
//			stmt.execute();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	private void resetCouchDB() {
//		try {
//			if (couchDBUtil.checkDB(getCouchDB_ip(), couchdb_name, null)) {
//				couchDBUtil.deleteDb(getCouchDB_ip(), couchdb_name, null);
//			}
//
//			couchDBUtil.createDb(getCouchDB_ip(), couchdb_name, null);
//
//			couchDBUtil.checkViews(getCouchDB_ip(), couchdb_name);
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	public static void main(String[] args) {
//		IntegrationTest test = new IntegrationTest();
//		test.storeTest();
//	}
//	
//	public void storeTest() {
//		
////		resetCouchDB();
////		resetHSQLDB();
//				
//	}
//	
//	public void firstSubscribeThenUpdateTest(){
//		
//	}
//	
//	public void firstUpdateThenNotifyTest(){
//		
//	}
//	
//	public void firstUpdateThenNotifyThenUpdateTest(){
//		
//	}
//	
//	
//
//}
