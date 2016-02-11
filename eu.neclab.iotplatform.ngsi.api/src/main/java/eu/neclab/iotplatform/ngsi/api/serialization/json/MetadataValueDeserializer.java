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
package eu.neclab.iotplatform.ngsi.api.serialization.json;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import eu.neclab.iotplatform.ngsi.api.datamodel.Circle;
import eu.neclab.iotplatform.ngsi.api.datamodel.PEPCredentials;
import eu.neclab.iotplatform.ngsi.api.datamodel.Point;
import eu.neclab.iotplatform.ngsi.api.datamodel.Polygon;
import eu.neclab.iotplatform.ngsi.api.datamodel.Segment;
import eu.neclab.iotplatform.ngsi.api.datamodel.Vertex;

/**
 * Deserializer to obtain operation scopes from JSON objects.
 */
public class MetadataValueDeserializer extends JsonDeserializer<Object> {
	
	/** The logger. */
	private static Logger logger = Logger.getLogger(MetadataValueDeserializer.class);
	/*
	 * "scopeValue" : { "vertices" : [ { "latitude" : 12312.0, "longitude" :
	 * 23123.22 }, { "latitude" : 12313.0, "longitude" : 23124.22 }, {
	 * "latitude" : 12314.0, "longitude" : 23125.22 }, { "latitude" : 12315.0,
	 * "longitude" : 23126.22 }, { "latitude" : 12316.0, "longitude" : 23127.22
	 * } ] } }, { "scopeType" : "circle", "scopeValue" : { "centerLatitude" :
	 * 312.0, "centerLongitude" : 564.0, "radius" : 8.0 } }, { "scopeType" :
	 * "segment", "scopeValue" : { "height" : 234234.0, "nw_Corner" :
	 * "312312.2", "se_Corner" : "423423.212" } (non-Javadoc)
	 * 
	 * @see
	 * org.codehaus.jackson.map.JsonDeserializer#deserialize(org.codehaus.jackson
	 * .JsonParser, org.codehaus.jackson.map.DeserializationContext)
	 */

	@Override
	public Object deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		JsonToken token = jp.getCurrentToken();

		if (token.equals(JsonToken.START_OBJECT)) {
			token = jp.nextToken();
			if (token.equals(JsonToken.FIELD_NAME)) {
				String name = jp.getCurrentName();
				try {
					if (name.equals("vertices")) {
						return getPolygon(jp);
					} else if (name.equals("centerLatitude")
							|| name.equals("centerLongitude")
							|| name.equals("radius")) {
						return getCircle(jp);
					} else if (name.equals("height")
							|| name.equals("nw_Corner")
							|| name.equals("se_Corner")) {
						return getSegment(jp);
					} else if (name.equals("username")
							|| name.equals("password")) {
						return getPEPCredentials(jp);
					} else if (name.equals("latitude")
							|| name.equals("longitude")) {
						return getPoint(jp);
					} else {
						return getAsJsonString(jp);
					}
				} catch (JsonProcessingException e) {
					logger.info("Error on parsing metadata");
				}
			} else {
				return getAsJsonString(jp);
			}

		}
		return jp.getText();

	}

	private Object getAsJsonString(JsonParser jp) throws JsonParseException,
			IOException {
		String asString = "";
		JsonToken token = jp.getCurrentToken();

		while (!token.equals(JsonToken.END_OBJECT)) {

			String key = jp.getText();
			token = jp.nextToken();
			String value = jp.getText();
			String.format("\"%s\" : \"%s\"", key, value);

			if (asString.isEmpty()) {
				asString += String.format("\"%s\" : \"%s\"", key, value);
			} else {
				asString += String.format(", \"%s\" : \"%s\"", key, value);
			}
			token = jp.nextToken();
		}
		asString = String.format("{%s}", asString);
		return asString;
	}

	/*
	 * "scopeValue" : { "height" : 234234.0, "nw_Corner" : "312312.2",
	 * "se_Corner" : "423423.212" }
	 */
	private Object getSegment(JsonParser jp) throws JsonParseException,
			IOException {
		JsonToken token = jp.getCurrentToken();

		Segment result = new Segment();

		while (!token.equals(JsonToken.END_OBJECT)) {
			token = jp.nextToken();
			if (jp.getCurrentName().equalsIgnoreCase("height")) {
				result.setHeight(jp.getDoubleValue());
			} else if (jp.getCurrentName().equalsIgnoreCase("nw_Corner")) {
				result.setNW_Corner(jp.getText());
			} else if (jp.getCurrentName().equalsIgnoreCase("se_Corner")) {
				result.setSE_Corner(jp.getText());
			} else {
				throw new JsonParseException("unknown field for segment", null);
			}
			token = jp.nextToken();
		}
		return result;
	}

	private Object getPEPCredentials(JsonParser jp) throws JsonParseException,
			IOException {
		JsonToken token = jp.getCurrentToken();

		PEPCredentials result = new PEPCredentials();

		while (!token.equals(JsonToken.END_OBJECT)) {
			token = jp.nextToken();
			if (jp.getCurrentName().equalsIgnoreCase("username")) {
				result.setUsername(jp.getText());
			} else if (jp.getCurrentName().equalsIgnoreCase("password")) {
				result.setPassword(jp.getText());
			} else {
				throw new JsonParseException(
						"unknown field for pepcredentials", null);
			}
			token = jp.nextToken();
		}
		return result;
	}

	/*
	 * "scopeValue" : { "centerLatitude" : 312.0, "centerLongitude" : 564.0,
	 * "radius" : 8.0 }
	 */
	private Object getCircle(JsonParser jp) throws JsonParseException,
			IOException {
		JsonToken token = jp.getCurrentToken();

		Circle result = new Circle();

		while (!token.equals(JsonToken.END_OBJECT)) {
			token = jp.nextToken();
			if (jp.getCurrentName().equalsIgnoreCase("centerLatitude")) {
				if (token.isNumeric()) {
					result.setCenterLatitude(jp.getFloatValue());
				} else {
					result.setCenterLatitude(Float.parseFloat(jp.getText()));
				}
			} else if (jp.getCurrentName().equalsIgnoreCase("centerLongitude")) {
				if (token.isNumeric()) {
					result.setCenterLongitude(jp.getFloatValue());
				} else {
					result.setCenterLongitude(Float.parseFloat(jp.getText()));
				}
			} else if (jp.getCurrentName().equalsIgnoreCase("radius")) {
				if (token.isNumeric()) {
					result.setRadius(jp.getFloatValue());
				} else {
					result.setRadius(Float.parseFloat(jp.getText()));
				}
			} else {
				throw new JsonParseException("unknown field for circle", null);
			}
			token = jp.nextToken();
		}
		return result;
	}

	/*
	 * "scopeValue" : { "latitude" : 312.0, "longitude" : 564.0}
	 */
	private Object getPoint(JsonParser jp) throws JsonParseException,
			IOException {
		JsonToken token = jp.getCurrentToken();

		Point result = new Point();

		while (!token.equals(JsonToken.END_OBJECT)) {
			token = jp.nextToken();
			if (jp.getCurrentName().equalsIgnoreCase("latitude")) {
				if (token.isNumeric()) {
					result.setLatitude(jp.getFloatValue());
				} else {
					result.setLatitude(Float.parseFloat(jp.getText()));
				}
			} else if (jp.getCurrentName().equalsIgnoreCase("longitude")) {
				if (token.isNumeric()) {
					result.setLongitude(jp.getFloatValue());
				} else {
					result.setLongitude(Float.parseFloat(jp.getText()));
				}
			} else {
				throw new JsonParseException("unknown field for circle", null);
			}
			token = jp.nextToken();
		}
		return result;
	}

	/*
	 * "vertices" : [ { "latitude" : 12312.0, "longitude" : 23123.22 }, {
	 * "latitude" : 12313.0, "longitude" : 23124.22 }, { "latitude" : 12314.0,
	 * "longitude" : 23125.22 }, { "latitude" : 12315.0, "longitude" : 23126.22
	 * }, { "latitude" : 12316.0, "longitude" : 23127.22 } ] }
	 */
	private Object getPolygon(JsonParser jp) throws JsonParseException,
			IOException {
		JsonToken token = jp.nextToken();

		if (!token.equals(JsonToken.START_ARRAY)) {
			throw new JsonParseException("Vertices has to be an array", null);
		}
		token = jp.nextToken();
		Polygon result = new Polygon();
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		while (!token.equals(JsonToken.END_ARRAY)) {
			if (token.equals(JsonToken.START_OBJECT)) {
				Vertex vertex = new Vertex();
				while (!token.equals(JsonToken.END_OBJECT)) {
					if (token.equals(JsonToken.FIELD_NAME)) {
						token = jp.nextToken();
						if (jp.getCurrentName().equalsIgnoreCase("latitude")) {
							if (token.isNumeric()) {
								vertex.setLatitude(jp.getFloatValue());
							} else {
								vertex.setLatitude(Float.parseFloat(jp
										.getText()));
							}
						} else if (jp.getCurrentName().equalsIgnoreCase(
								"longitude")) {
							if (token.isNumeric()) {
								vertex.setLongitude(jp.getFloatValue());
							} else {
								vertex.setLongitude(Float.parseFloat(jp
										.getText()));
							}
						}
					}
					token = jp.nextToken();
				}
				vertices.add(vertex);
			}
			token = jp.nextToken();

		}
		jp.nextToken();
		result.setVertexList(vertices);
		return result;
	}

}
