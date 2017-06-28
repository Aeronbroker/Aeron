package eu.neclab.iotplatform.ngsi.api.ngsi10;


public enum StandardVersion {
	
	NGSI10_v1_nle("NGSI10_v1_nle"), 
	NGSI10_v1_tid("NGSI10_v1_tid");

	private String string;

	public static StandardVersion fromString(String string) {
		for (StandardVersion version : StandardVersion.values()) {
			if (version.getVersion().toLowerCase().equals(string.toLowerCase())) {
				return version;
			}
		}
		return null;
	}

	private StandardVersion(String version) {

		this.string = version;
	}

	public String getVersion() {
		return string;
	}

}
