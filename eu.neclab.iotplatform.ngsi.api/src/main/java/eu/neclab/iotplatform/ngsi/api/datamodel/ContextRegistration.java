/*******************************************************************************
 * Copyright (c) 2015, NEC Europe Ltd.
 * All rights reserved.
 * 
 * Authors:
 *          * Salvatore Longo - salvatore.longo@neclab.eu
 *          * Tobias Jacobs - tobias.jacobs@neclab.eu
 *          * Flavio Cirillo - flavio.cirillo@neclab.eu
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
package eu.neclab.iotplatform.ngsi.api.datamodel;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Implements ContextRegistration
 * as defined in OMA NGSI 9/10 approved version 1.0.
 */
@XmlRootElement(name = "contextRegistration")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContextRegistration extends NgsiStructure {

	@XmlElementWrapper(name = "entityIdList")
	@XmlElement(name = "entityId")
	@JsonProperty("entities")
	private List<EntityId> entityId = null;

	@XmlElementWrapper(name = "contextRegistrationAttributeList")
	@XmlElement(name = "contextRegistrationAttribute")
	@JsonProperty("attributes")
	private List<ContextRegistrationAttribute> contextRegistrationAttribute = null;

	@XmlElementWrapper(name = "registrationMetadata")
	@XmlElement(name = "contextMetadata")
	private List<ContextMetadata> contextMetadata = null;

	@XmlElement(name = "providingApplication",required = true)
	//@XmlSchemaType(name = "anyURI")
	private String providingApplication;

	public ContextRegistration() {

	}

	public ContextRegistration(List<EntityId> entityId,
			List<ContextRegistrationAttribute> contextRegistrationAttribute,
			List<ContextMetadata> contextMetadata, URI provideApplication) {
		this.entityId = entityId;
		this.contextRegistrationAttribute = contextRegistrationAttribute;
		this.contextMetadata = contextMetadata;
		providingApplication = provideApplication.toString();

	}

	@JsonIgnore
	public List<EntityId> getListEntityId() {

		return entityId;
	}

	@JsonIgnore
	public void setListEntityId(List<EntityId> entityId) {
		this.entityId = entityId;

	}

	@JsonIgnore
	public List<ContextRegistrationAttribute> getContextRegistrationAttribute() {
		if (contextRegistrationAttribute == null) {
			contextRegistrationAttribute = new ArrayList<ContextRegistrationAttribute>();
		}
		return contextRegistrationAttribute;
	}

	@JsonIgnore
	public void setListContextRegistrationAttribute(
			List<ContextRegistrationAttribute> contextRegistrationAttribute) {
		this.contextRegistrationAttribute = contextRegistrationAttribute;

	}

	public List<ContextMetadata> getListContextMetadata() {
		if (contextMetadata == null) {
			contextMetadata = new ArrayList<ContextMetadata>();
		}
		return contextMetadata;
	}

	public void setListContextMetadata(List<ContextMetadata> contextMetadata) {
		this.contextMetadata = contextMetadata;

	}

	public URI getProvidingApplication() {

		if (providingApplication == null){
			return null;
		}

		URI uri = null;

		try {
			uri = new URI(providingApplication);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uri;

	}

	public void setProvidingApplication(URI providingApplication) {
		this.providingApplication = providingApplication.toString();

	}
}
