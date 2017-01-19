package eu.neclab.iotplatform.ngsi.api.datamodel;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.Duration;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Implements RegisterProviderRequest
 * as defined in the FI-WARE binding of NGSI 9/10.
 *
 */
@XmlRootElement(name = "registerProviderRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegisterProviderRequest  extends NgsiStructure {
	
	@XmlElementWrapper(name = "metadata")
	@XmlElement(name = "contextMetadata")
	@JsonProperty("metadata")
	private List<ContextMetadata> metadata = null;

	@XmlElement(name = "duration", required = false)
	private Duration duration = null;
	
	@XmlElement(name = "providingApplication",required = true)
	private String providingApplication;

	@XmlElement(name = "registrationId", required = false)
	private String registrationId = null;

	public List<ContextMetadata> getMetadata() {
		return metadata;
	}

	public void setMetadata(List<ContextMetadata> metadata) {
		this.metadata = metadata;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public String getProvidingApplication() {
		return providingApplication;
	}

	public void setProvidingApplication(String providingApplication) {
		this.providingApplication = providingApplication;
	}

	public String getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}
	
	

}
