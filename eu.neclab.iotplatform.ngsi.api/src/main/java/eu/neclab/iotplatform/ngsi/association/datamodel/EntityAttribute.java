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

package eu.neclab.iotplatform.ngsi.association.datamodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;

/**
 * Represents an entity/attribute combination.
 * <p>
 * In order to represent an entity association, the attribute strings 
 * are left empty "".
 *
 */
@XmlRootElement(name = "EntityAttribute")
@XmlAccessorType(XmlAccessType.FIELD)
public class EntityAttribute {
	@XmlElement(name = "entityIdString")
	private String entityID;
	@XmlElement(name = "entityAttribute")
	private String entityAttribute;
	@XmlElement(name = "entityId")
	private EntityId entity;

	/**
	 * Initializes an empty instance.
	 */
	public EntityAttribute() {

	}

	/**
	 * Initializes an instance given an entity id and an attribute.
	 * 
	 * @param entity The entity id,
	 * @param entityAttribute The attribute.
	 */
	public EntityAttribute(EntityId entity, String entityAttribute) {
		super();
		entityID = entity.getId();
		this.entityAttribute = entityAttribute;
		this.entity = entity;
	}

	/**
	 * @return The entity id.
	 */
	public EntityId getEntity() {
		return entity;
	}

	/**
	 * Sets the entity id.
	 */
	public void setEntity(EntityId entity) {
		this.entity = entity;
	}

	/**
	 * @return The "id" field of the entity id.
	 */
	public String getEntityID() {
		return entityID;
	}

	/**
	 * Sets the string representing the entity id.
	 */
	public void setEntityID(String entityID) {
		this.entityID = entityID;
	}

	/**
	 * 
	 * @return The attribute.
	 */
	public String getEntityAttribute() {
		return entityAttribute;
	}

	/**
	 * Assigns the attribute.
	 */
	public void setEntityAttribute(String entityAttribute) {
		this.entityAttribute = entityAttribute;
	}

	@Override
	public String toString() {
		return "EntityAttribute [EntityID=" + entityID + ", EntityAttribute="
				+ entityAttribute + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((entityAttribute == null) ? 0 : entityAttribute.hashCode());
		result = prime * result
				+ ((entityID == null) ? 0 : entityID.hashCode());
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
		EntityAttribute other = (EntityAttribute) obj;
		if (entityAttribute == null) {
			if (other.entityAttribute != null) {
				return false;
			}
		} else if (!entityAttribute.equals(other.entityAttribute)) {
			return false;
		}
		if (entityID == null) {
			if (other.entityID != null) {
				return false;
			}
		} else if (!entityID.equals(other.entityID)) {
			return false;
		}
		return true;
	}

}
