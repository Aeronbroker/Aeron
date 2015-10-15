package eu.neclab.iotplatform.ngsi.api.datamodel;

public enum ScopeTypes {
	
	ISO8601TimeInterval("ISO8601TimeInterval"),
	SubscriptionOriginator("SubscriptionOriginator"),
	Trace("Trace");
	
	private String string;

	private ScopeTypes(String string) {
		 
        this.string = string;
	}
	
	public String toString(){
		return string;
	}
}
