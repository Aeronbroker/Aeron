package eu.neclab.iotplatform.mocks.utils;

import java.util.List;

import javax.ws.rs.core.HttpHeaders;

import eu.neclab.iotplatform.iotbroker.commons.ContentType;

public class HeaderExtractor {

	public static ContentType getContentType(HttpHeaders headers,
			ContentType defaultContentType) {
		
		List<String> strings = headers.getRequestHeader("Content-Type");
		
		if (strings !=null && !strings.isEmpty()){
			return ContentType.fromString(strings.get(0), defaultContentType);
		}
		
		return defaultContentType;
		
	}
	
	public static ContentType getAccept(HttpHeaders headers,
			ContentType defaultAccept) {
		
		List<String> strings = headers.getRequestHeader("Accept");
		
		if (strings !=null && !strings.isEmpty()){
			return ContentType.fromString(strings.get(0), defaultAccept);
		}
		
		return defaultAccept;
		
	}

}
