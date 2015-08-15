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
 * Implements AppendContextElementRequest
 * as defined in the FI-WARE binding of NGSI 9/10.
 *
 */
@XmlRootElement(name = "appendContextElementRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class AppendContextElementRequest extends NgsiStructure {

	@XmlElement(name = "attributeDomainName", required = false)
	private String attributeDomainName = null;

	@XmlElementWrapper(name = "contextAttributeList")
	@XmlElement(name = "contextAttribute", required = false)
	@JsonProperty("attributes")
	private List<ContextAttribute> contextAttribute;

	@XmlElementWrapper(name = "domainMetadata")
	@XmlElement(name = "contextMetadata", required = false)
	@JsonProperty("domainMetadata")
	private List<ContextMetadata> contextMetadata;

	public AppendContextElementRequest() {

	}

	public AppendContextElementRequest(String attributeDomainName,
			List<ContextAttribute> contextAttributeList,
			List<ContextMetadata> domainMetadata) {
		super();
		this.attributeDomainName = attributeDomainName;
		this.contextAttribute = contextAttributeList;
		this.contextMetadata = domainMetadata;
	}

	public String getAttributeDomainName() {
		return attributeDomainName;
	}

	public void setAttributeDomainName(String attributeDomainName) {
		this.attributeDomainName = attributeDomainName;
	}

	public List<ContextAttribute> getContextAttributeList() {
		if (contextAttribute == null) {
			contextAttribute = new ArrayList<ContextAttribute>();
		}
		return contextAttribute;
	}

	public void setContextAttributeList(
			List<ContextAttribute> contextAttributeList) {
		this.contextAttribute = contextAttributeList;
	}

	@JsonIgnore
	public List<ContextMetadata> getDomainMetadata() {
		if (contextMetadata == null) {
			contextMetadata = new ArrayList<ContextMetadata>();
		}
		return contextMetadata;
	}

	public void setDomainMetadata(List<ContextMetadata> domainMetadata) {
		this.contextMetadata = domainMetadata;
	}

}
