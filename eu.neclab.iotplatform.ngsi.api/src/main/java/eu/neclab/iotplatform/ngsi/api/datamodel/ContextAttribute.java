/*******************************************************************************
 *   Copyright (c) 2014, NEC Europe Ltd.
 *   All rights reserved.
 *
 *   Authors:
 *           * Salvatore Longo - salvatore.longo@neclab.eu
 *           * Tobias Jacobs - tobias.jacobs@neclab.eu
 *           * Raihan Ul-Islam - raihan.ul-islam@neclab.eu
 *
 *    Redistribution and use in source and binary forms, with or without
 *    modification, are permitted provided that the following conditions are met:
 *   1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   3. All advertising materials mentioning features or use of this software
 *     must display the following acknowledgement:
 *     This product includes software developed by NEC Europe Ltd.
 *   4. Neither the name of the NEC nor the
 *     names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY NEC ''AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL NEC BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package eu.neclab.iotplatform.ngsi.api.datamodel;

import java.net.URI;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

/**
 * Implements context attributes
 * as defined in OMA NGSI 9/10 approved version 1.0.
 */
@XmlRootElement(name = "contextAttribute")
@XmlAccessorType(XmlAccessType.FIELD)
public final class ContextAttribute extends NgsiStructure {

	/** The name of the attribute represented by this ContextAttribute. */
	@XmlElement(required = true)
	private String name = null;

	/** A string representing the type of the value. */
	@XmlSchemaType(name = "anyURI")
	private URI type = null;

	@XmlElement(required = true)
	private String contextValue = null;

	@XmlElementWrapper(name = "metadata")
	@XmlElement(required = false)
	private List<ContextMetadata> contextMetadata;

	/**
	 * Creates a new ContextAttribute object.
	 */
	public ContextAttribute() {

	}

	/**
	 * Creates a new Context attribute object with no meta data.
	 */
	public ContextAttribute(String name, URI type, String value) {
		this.name = name;
		this.type = type;
		contextValue = value;
	}

	/**
	 * Creates a new Context attribute object with meta data.
	 */

	public ContextAttribute(String name, URI type, String value,
			List<ContextMetadata> metaData) {
		this(name, type, value);
		contextMetadata = metaData;
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

	public Object getcontextValue() {
		return contextValue;
	}

	public void setcontextValue(String contextValue) {
		this.contextValue = contextValue;
	}

	public List<ContextMetadata> getMetadata() {
		return contextMetadata;
	}

	public void setMetaData(List<ContextMetadata> metaData) {
		contextMetadata = metaData;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (contextMetadata == null ? 0 : contextMetadata.hashCode());
		result = prime * result
				+ (contextValue == null ? 0 : contextValue.hashCode());
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result + (type == null ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ContextAttribute other = (ContextAttribute) obj;
		if (contextMetadata == null) {
			if (other.contextMetadata != null) {
				return false;
			}
		} else if (!contextMetadata.equals(other.contextMetadata)) {
			return false;
		}
		if (contextValue == null) {
			if (other.contextValue != null) {
				return false;
			}
		} else if (!contextValue.equals(other.contextValue)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		return true;
	}

}