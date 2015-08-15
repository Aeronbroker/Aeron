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

import org.codehaus.jackson.annotate.JsonProperty;




/**
 * Implements UpdateContextRequest
 * as defined in OMA NGSI 9/10 approved version 1.0.
 */
@XmlRootElement(name = "updateContextRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class UpdateContextRequest extends NgsiStructure {

	@XmlElementWrapper(name = "contextElementList")
	@XmlElement(name = "contextElement", required = true)
	@JsonProperty("contextElements")
	private List<ContextElement> contextElement;

	@XmlElement(name = "updateAction", required = true)
	private UpdateActionType updateAction = null;

	public UpdateContextRequest() {

	}

	public UpdateContextRequest(List<ContextElement> contextElement,
			UpdateActionType updateAction) {

		this.contextElement = contextElement;
		this.updateAction = updateAction;

	}

	public List<ContextElement> getContextElement() {
		if (contextElement == null) {
			contextElement = new ArrayList<ContextElement>();
		}
		return contextElement;
	}

	public void setContextElement(List<ContextElement> contextElement) {
		this.contextElement = contextElement;
	}

	public UpdateActionType getUpdateAction() {
		return updateAction;
	}

	public void setUpdateAction(UpdateActionType updateAction) {
		this.updateAction = updateAction;
	}
	
	@Override
	public boolean equals(Object obj){
		
		if(obj == null || obj.getClass() != this.getClass())
			return false;
		
		UpdateContextRequest other = (UpdateContextRequest) obj;
		
		if(contextElement == null || contextElement.isEmpty()){
			if(other.contextElement != null && !other.contextElement.isEmpty() )
				return false;
		}
		else if(!contextElement.equals(other.contextElement))
			return false;
		
		return 
				this.updateAction == other.updateAction;
		
	}
		

}
