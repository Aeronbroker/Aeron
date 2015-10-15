package eu.neclab.iotplatform.ngsi.api.datamodel;

public enum MetadataTypes {
	
	NotificationHandler("NotificationHandler");
	
	private String string;

	private MetadataTypes(String string) {
		 
        this.string = string;
	}
	
	public String toString(){
		return string;
	}
}
