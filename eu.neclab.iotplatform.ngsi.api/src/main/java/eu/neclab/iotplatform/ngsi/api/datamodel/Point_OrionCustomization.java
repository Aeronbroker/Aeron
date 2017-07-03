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

/**
 *  Representation of a point object.
 */
import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "point")
@XmlAccessorType(XmlAccessType.FIELD)
public class Point_OrionCustomization extends NgsiStructure implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "latitude", required = true)
	private float latitude;
	@XmlElement(name = "longitude", required = true)
	private float longitude;

	public Point_OrionCustomization() {
		super();
	}

	public Point_OrionCustomization(float latitude, float longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Point_OrionCustomization(Point point) {
		super();
		if (point != null) {
			this.latitude = point.getLatitude();
			this.longitude = point.getLongitude();
		}
	}

	public Point_OrionCustomization(Segment segment) {
		super();
		Point point = segment.getBarycentre();
		if (point != null) {
			this.latitude = point.getLatitude();
			this.longitude = point.getLongitude();
		}

	}

	public Point_OrionCustomization(Circle circle) {
		super();
		if (circle != null) {
			this.latitude = circle.getCenterLatitude();
			this.longitude = circle.getCenterLongitude();
		}

	}

	public Point_OrionCustomization(Polygon polygon) {
		super();
		Point point = polygon.getBarycentre();
		if (point != null) {
			this.latitude = point.getLatitude();
			this.longitude = point.getLongitude();
		}
	}

	public static Point_OrionCustomization createPoint(Object object) {
		if (object instanceof Point) {
			return new Point_OrionCustomization((Point) object);
		} else if (object instanceof Segment) {
			return new Point_OrionCustomization((Segment) object);
		} else if (object instanceof Circle) {
			return new Point_OrionCustomization((Circle) object);
		} else if (object instanceof Polygon) {
			return new Point_OrionCustomization((Polygon) object);
		}
		return null;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		return latitude + ", " + longitude;
	}

}
