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
package eu.neclab.iotplatform.ngsi.association.datamodel;

import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.neclab.iotplatform.ngsi.api.datamodel.Association;

/**
 * Represents NGSI associations. Associations are ordered (source/target) pairs,
 * where both source and target entity/attribute combinations. The semantic of
 * an association is that any attribute value of source can be interpreted as an
 * attribute value of target.
 * <p>
 * Note that this representation is for internal computations and storage. It
 * differs from the officially defined association representation defined by
 * FI-WARE. The latter is represented in the class {@link Association}.
 * 
 */
@XmlRootElement(name = "AssociationDS")
@XmlAccessorType(XmlAccessType.FIELD)
public class AssociationDS {
	@XmlElement(name = "SourceEA")
	private EntityAttribute sourceEA;
	@XmlElement(name = "TargetEA")
	private EntityAttribute targetEA;

	private URI providingApplication;

	/**
	 * Instantiates an empty association.
	 */
	public AssociationDS() {

	}

//	/**
//	 * Instantiates an association by its source and target.
//	 * 
//	 * @param sourceEA
//	 *            The source.
//	 * @param targetEA
//	 *            The target.
//	 */
//	public AssociationDS(EntityAttribute sourceEA, EntityAttribute targetEA) {
//		super();
//		this.sourceEA = sourceEA;
//		this.targetEA = targetEA;
//	}

	/**
	 * Instantiates an association by its source and target.
	 * 
	 * @param sourceEA
	 *            The source.
	 * @param targetEA
	 *            The target.
	 */
	public AssociationDS(EntityAttribute sourceEA, EntityAttribute targetEA,
			URI providingApplication) {
		super();
		this.sourceEA = sourceEA;
		this.targetEA = targetEA;
		this.providingApplication = providingApplication;
	}

	/**
	 * @return The source of the association.
	 */
	public EntityAttribute getSourceEA() {
		return sourceEA;
	}

	/**
	 * Sets the source of the association.
	 */
	public void setSourceEA(EntityAttribute sourceEA) {
		this.sourceEA = sourceEA;
	}

	/**
	 * @return The target of the association.
	 */
	public EntityAttribute getTargetEA() {
		return targetEA;
	}

	/**
	 * Sets the target of the association.
	 */
	public void setTargetEA(EntityAttribute targetEA) {
		this.targetEA = targetEA;
	}

	public URI getProvidingApplication() {
		return providingApplication;
	}

	public void setProvidingApplication(URI providingApplication) {
		this.providingApplication = providingApplication;
	}



	@Override
	public String toString() {
		return "AssociationDS [sourceEA=" + sourceEA + ", targetEA=" + targetEA
				+ ", providingApplication=" + providingApplication + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (sourceEA == null ? 0 : sourceEA.hashCode());
		result = prime * result + (targetEA == null ? 0 : targetEA.hashCode());
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
		AssociationDS other = (AssociationDS) obj;
		if (sourceEA == null) {
			if (other.sourceEA != null) {
				return false;
			}
		} else if (!sourceEA.equals(other.sourceEA)) {
			return false;
		}
		if (targetEA == null) {
			if (other.targetEA != null) {
				return false;
			}
		} else if (!targetEA.equals(other.targetEA)) {
			return false;
		}
		return true;
	}

}
