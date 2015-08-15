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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *  The definition of a circle for using in geographical scopes.
 */
@XmlRootElement(name = "circle")
@XmlAccessorType(XmlAccessType.FIELD)
public class Circle extends NgsiStructure {


	@XmlElement(name = "centerLatitude", required = true)
	private float centerLatitude;
	@XmlElement(name = "centerLongitude", required = true)
	private float centerLongitude;
	@XmlElement(name = "radius", required = true)
	private float radius;

	public Circle() {
		super();
	}

	public Circle(float centerLatitude, float centerLongitude, float radius) {
		super();
		this.centerLatitude = centerLatitude;
		this.centerLongitude = centerLongitude;
		this.radius = radius;
	}

	public float getCenterLatitude() {
		return centerLatitude;
	}

	public void setCenterLatitude(float centerLatitude) {
		this.centerLatitude = centerLatitude;
	}

	public float getCenterLongitude() {
		return centerLongitude;
	}

	public void setCenterLongitude(float centerLongitude) {
		this.centerLongitude = centerLongitude;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

//	@Override
//	public String toString() {
//
//		String result;
//		StringWriter sw = new StringWriter();
//		try {
//			JAXBContext carContext = JAXBContext.newInstance(this.getClass());
//			Marshaller carMarshaller = carContext.createMarshaller();
//			carMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//			carMarshaller.marshal(this, sw);
//			result = sw.toString();
//		} catch (JAXBException e) {
//			throw new RuntimeException(e);
//		}
//
//		return result;
//
//	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(centerLatitude);
		result = prime * result + Float.floatToIntBits(centerLongitude);
		result = prime * result + Float.floatToIntBits(radius);
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
		Circle other = (Circle) obj;
		if (Float.floatToIntBits(centerLatitude) != Float
				.floatToIntBits(other.centerLatitude)) {
			return false;
		}
		if (Float.floatToIntBits(centerLongitude) != Float
				.floatToIntBits(other.centerLongitude)) {
			return false;
		}
		if (Float.floatToIntBits(radius) != Float.floatToIntBits(other.radius)) {
			return false;
		}
		return true;
	}


}
