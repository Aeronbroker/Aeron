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

package eu.neclab.iotplatform.ngsiemulator.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import eu.neclab.iotplatform.iotbroker.commons.ContentType;

public class ServerConfiguration {

	public final static String DEFAULT_PORTNUMBERS = "8001";
	public final static Mode DEFAULT_MODE = Mode.RANDOM;
	public final static String DEFAULT_RANGESOFENTITYIDS = "1-100";
	public final static int DEFAULT_NUMBEROFENTITYIDSTOSELECT = 10;
	public final static String DEFAULT_RANGESOFATTRIBUTES = "1-100";
	public final static int DEFAULT_NUMBEROFATTRIBUTESTOSELECT = 10;
	public final static String DEFAULT_EXPOSEDURL = "http://localhost";
	public final static String DEFAULT_IOTDISCOVERYURL = "http://localhost:8065/";
	public final static String DEFAULT_DOREGISTRATION = "true";
	public final static String DEFAULT_QUERYCONTEXTRESPONSEFILE = "queryContextResponse.xml";
	public final static String DEFAULT_NOTIFYCONTEXTREQUESTFILE = "notifyContextRequest.xml";
	public final static String DEFAULT_REGISTERCONTEXTAVAILABILITY = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><registerContextRequest><contextRegistrationList><contextRegistration><entityIdList><entityId isPattern=\"true\"><id>.*</id></entityId></entityIdList><providingApplication>PROVIDINGAPPLICATION_PLACEHOLDER</providingApplication></contextRegistration></contextRegistrationList></registerContextRequest>";
	public final static int DEFAULT_NOTIFICATIONPERIOD = 5;
	public final static ContentType DEFAULT_OUTGOINGCONTENTTYPE = ContentType.XML;
	public final static ContentType DEFAULT_INCOMINGCONTENTTYPE = ContentType.XML;

	private int port;
	private Mode mode;
	private String queryContextResponseFile;
	private String notifyContextRequestFile;
	private ContentType incomingContentType;
	private ContentType outgoingContentType;
	private int notificationPeriod;
	private Set<String> entityNames;
	private Set<String> attributeNames;
	private boolean doRegistration;

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

	public Set<String> getEntityNames() {
		return entityNames;
	}

	public void setEntityNames(Set<String> entityNames) {
		this.entityNames = entityNames;
	}

	public Set<String> getAttributeNames() {
		return attributeNames;
	}

	public void setAttributeNames(Set<String> attributeNames) {
		this.attributeNames = attributeNames;
	}

	public boolean isDoRegistration() {
		return doRegistration;
	}

	public void setDoRegistration(boolean doRegistration) {
		this.doRegistration = doRegistration;
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
	
	public Map<String, Object> toMap1() {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Field field : this.getClass().getDeclaredFields()) {
			try {
				Object object = field.get(this);
				if (object != null) {
					map.put(field.getName(), object);
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
