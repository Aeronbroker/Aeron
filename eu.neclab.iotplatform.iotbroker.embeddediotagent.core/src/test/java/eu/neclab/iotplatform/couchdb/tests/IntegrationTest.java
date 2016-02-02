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
