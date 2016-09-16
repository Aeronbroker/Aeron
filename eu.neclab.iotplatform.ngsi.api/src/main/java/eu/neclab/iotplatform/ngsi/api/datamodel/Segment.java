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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *  Representation of a rectangle (for geographic scopes).
 */
@XmlRootElement(name = "segment")
@XmlAccessorType(XmlAccessType.FIELD)
public class Segment extends NgsiStructure{

	@XmlElement(name = "NW_Corner", required = true)
	private String NW_Corner;
	
	@XmlElement(name = "SE_Corner", required = true)
	private String SE_Corner;
	
	@XmlElement(name = "Height", required = false)
	private Double height;




	public Segment(String nW_Corner, String sE_Corner, Double height) {
		NW_Corner = nW_Corner;
		SE_Corner = sE_Corner;
		this.height = height;
	}
	public Segment() {

	}
	public String getNW_Corner() {
		return NW_Corner;
	}
	public void setNW_Corner(String nW_Corner) {
		NW_Corner = nW_Corner;
	}
	public String getSE_Corner() {
		return SE_Corner;
	}
	public void setSE_Corner(String sE_Corner) {
		SE_Corner = sE_Corner;
	}
	public Double getHeight() {
		return height;
	}
	public void setHeight(Double height) {
		this.height = height;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (NW_Corner == null ? 0 : NW_Corner.replaceAll("\\s+", "").hashCode());
		result = prime * result
				+ (SE_Corner == null ? 0 : SE_Corner.replaceAll("\\s+", "").hashCode());
		result = prime * result + (height == null ? 0 : height.hashCode());

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
		Segment other = (Segment) obj;
		if (NW_Corner == null) {
			if (other.NW_Corner != null) {
				return false;
			}
		} else if (!NW_Corner.equals(other.NW_Corner)) {
			return false;
		}
		if (SE_Corner == null) {
			if (other.SE_Corner != null) {
				return false;
			}
		} else if (!SE_Corner.equals(other.SE_Corner)) {
			return false;
		}
		if (height == null) {
			if (other.height != null) {
				return false;
			}
		} else if (!height.equals(other.height)) {
			return false;
		}
		return true;
	}
//	@Override
//	public String toString() {
//		return "Segment [NW_Corner=" + NW_Corner + ", SE_Corner=" + SE_Corner
//				+ ", height=" + height + "]";
//	}








}
