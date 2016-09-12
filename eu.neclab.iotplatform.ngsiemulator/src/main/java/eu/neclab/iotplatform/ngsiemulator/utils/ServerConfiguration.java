package eu.neclab.iotplatform.ngsiemulator.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import eu.neclab.iotplatform.iotbroker.commons.ContentType;

public class ServerConfiguration {

	public final static String DEFAULT_PORTNUMBERS = "8001";
	public final static Mode DEFAULT_MODE = Mode.RANDOM;
	public final static String DEFAULT_RANGESOFENTITYIDS = "1-100";
	public final static String DEFAULT_RANGESOFENTITYIDSTOSELECT = "10";
	public final static String DEFAULT_RANGESOFATTRIBUTES = "1-100";
	public final static String DEFAULT_RANGESOFATTRIBUTESTOSELECT = "10";
	public final static String DEFAULT_EXPOSEDURL = "http://localhost";
	public final static String DEFAULT_DOREGISTRATION = "true";
	public final static String DEFAULT_QUERYCONTEXTRESPONSEFILE = "queryContextResponse.xml";
	public final static String DEFAULT_NOTIFYCONTEXTREQUESTFILE = "notifyContextRequest.xml";
	public final static String DEFAULT_REGISTERCONTEXTAVAILABILITY = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><registerContextRequest><contextRegistrationList><contextRegistration><entityIdList><entityId isPattern=\"true\"><id>.*</id></entityId></entityIdList><providingApplication>PROVIDINGAPPLICATION_PLACEHOLDER</providingApplication></contextRegistration></contextRegistrationList></registerContextRequest>";
	public final static int DEFAULT_NOTIFICATIONPERIOD = 5;
	
	private int port;
	private Mode mode;
	private String queryContextResponseFile;
	private String notifyContextRequestFile;
	private ContentType incomingContentType;
	private ContentType outgoingContentType;
	private int notificationPeriod;


	public int getNotificationPeriod() {
		return notificationPeriod;
	}

	public void setNotificationPeriod(int notificationPeriod) {
		this.notificationPeriod = notificationPeriod;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public String getQueryContextResponseFile() {
		return queryContextResponseFile;
	}

	public void setQueryContextResponseFile(String queryContextResponseFile) {
		this.queryContextResponseFile = queryContextResponseFile;
	}

	public ContentType getIncomingContentType() {
		return incomingContentType;
	}

	public void setIncomingContentType(ContentType incomingContentType) {
		this.incomingContentType = incomingContentType;
	}

	public ContentType getOutgoingContentType() {
		return outgoingContentType;
	}

	public void setOutgoingContentType(ContentType outgoingContentType) {
		this.outgoingContentType = outgoingContentType;
	}

	public String getNotifyContextRequestFile() {
		return notifyContextRequestFile;
	}

	public void setNotifyContextRequestFile(String notifyContextRequestFile) {
		this.notifyContextRequestFile = notifyContextRequestFile;
	}


	public Map<String, String> toMap() {
		Map<String, String> map = new HashMap<String, String>();
		for (Field field : this.getClass().getDeclaredFields()) {
			try {
				Object object = field.get(this);
				if (object != null) {
					map.put(field.getName(), object.toString());
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return map;

	}
}
