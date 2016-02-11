package eu.neclab.iotplatform.ngsi.api.datamodel;

import java.net.URI;
import java.net.URISyntaxException;

public enum MetadataTypes {
	
	NotificationHandler("NotificationHandler"),
	SimpleGeolocation("SimpleGeoLocation");

	
	private String string;

	private MetadataTypes(String string) {
		 
        this.string = string;
	}
	
	public String getName(){
		return string;
	}
	
	public URI getType(){
		try {
			return new URI(string);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
