package eu.neclab.iotplatform.iotbroker.commons;

public enum ContentType {
	JSON("application/json"), XML("application/xml");

	private String contentType;

	ContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentType() {
		return this.contentType;
	}

	public static ContentType fromString(String contentType) {
		if (contentType != null) {
			for (ContentType t : ContentType.values()) {
				if (contentType.equalsIgnoreCase(t.contentType)) {
					return t;
				}
			}
		}
		return null;
	}

	public static ContentType fromString(String contentType,
			ContentType defaultType) {
		if (contentType != null) {
			for (ContentType t : ContentType.values()) {
				if (contentType.equalsIgnoreCase(t.contentType)) {
					return t;
				}
			}
		}
		return defaultType;
	}
	
	public String toString(){
		return contentType;
	}
}
