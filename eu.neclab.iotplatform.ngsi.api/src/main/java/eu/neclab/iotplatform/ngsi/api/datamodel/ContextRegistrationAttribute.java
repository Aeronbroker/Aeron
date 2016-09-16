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

package eu.neclab.iotplatform.ngsi.api.datamodel;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Implements ContextRegistrationAttribute
 * as defined in OMA NGSI 9/10 approved version 1.0.
 */
@XmlRootElement(name = "contextRegistrationAttribute")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContextRegistrationAttribute extends NgsiStructure {

	@XmlElement(name = "name", required = true)
	private String name = null;

	@XmlElement(name = "type")
	private URI type = null;

	@XmlElement(name = "isDomain")
	private boolean isDomain = false;

	@XmlElementWrapper(name = "metaData")
	@XmlElement(name = "contextMetadata")
	private List<ContextMetadata> contextMetadata = null;

	public ContextRegistrationAttribute() {

	}
	
	public ContextRegistrationAttribute(ContextRegistrationAttribute contextRegistrationAttribute) {
		this.name = contextRegistrationAttribute.name;
		this.type = contextRegistrationAttribute.type;
		this.isDomain = contextRegistrationAttribute.isDomain;
		this.contextMetadata = contextRegistrationAttribute.contextMetadata;

	}

	public ContextRegistrationAttribute(String name, URI type,
			boolean isDomain, List<ContextMetadata> metaData) {
		this.name = name;
		this.type = type;
		this.isDomain = isDomain;
		this.contextMetadata = metaData;

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;

	}

	public URI getType() {
		return type;
	}

	public void setType(URI type) {
		this.type = type;

	}

	public boolean getIsDomain() {
		return isDomain;
	}

	public void setIsDomain(boolean isDomain) {
		this.isDomain = isDomain;

	}

	public List<ContextMetadata> getMetaData() {
		if (contextMetadata == null) {
			contextMetadata = new ArrayList<ContextMetadata>();
		}
		return contextMetadata;
	}

	public void setMetaData(List<ContextMetadata> metaData) {

		this.contextMetadata = metaData;

	}

}
