package eu.neclab.iotplatform.ngsi.api.serialization.json;

import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import eu.neclab.iotplatform.ngsi.api.datamodel.Circle;
import eu.neclab.iotplatform.ngsi.api.datamodel.Polygon;
import eu.neclab.iotplatform.ngsi.api.datamodel.Segment;
import eu.neclab.iotplatform.ngsi.api.datamodel.Vertex;

/**
 *  Deserializer to obtain operation scopes from JSON objects.
 */
public class OperationScopeValueDeserializer extends JsonDeserializer<Object> {

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
				if (name.equals("vertices")) {
					return getPolygon(jp);
				} else if (name.equals("centerLatitude")
						|| name.equals("centerLongitude")
						|| name.equals("radius")) {
					return getCircle(jp);
				} else if (name.equals("height") || name.equals("nw_Corner")
						|| name.equals("se_Corner")) {
					return getSegment(jp);
				} else {
					throw new JsonParseException("unknow datatype for scope",
							null);
				}
			} else {
				throw new JsonParseException("unknow datatype for scope", null);
			}

		} else {

			return null;
		}

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

	/*
	 * "scopeValue" : { "centerLatitude" : 312.0, "centerLongitude" : 564.0,
	 * "radius" : 8.0 }
	 */
	private Object getCircle(JsonParser jp) throws JsonParseException, IOException {
JsonToken token = jp.getCurrentToken();
		
		Circle result = new Circle();
		
		while (!token.equals(JsonToken.END_OBJECT)) {
			token = jp.nextToken();
			if (jp.getCurrentName().equalsIgnoreCase("centerLatitude")) {
				result.setCenterLatitude(jp.getFloatValue());
			} else if (jp.getCurrentName().equalsIgnoreCase("centerLongitude")) {
				result.setCenterLongitude(jp.getFloatValue());
			} else if (jp.getCurrentName().equalsIgnoreCase("radius")) {
				result.setRadius(jp.getFloatValue());
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
	private Object getPolygon(JsonParser jp) throws JsonParseException, IOException {
		JsonToken token = jp.nextToken();
		
		
		if(!token.equals(JsonToken.START_ARRAY)){
			throw new JsonParseException("Vertices has to be an array", null);
		}
		token = jp.nextToken();
		Polygon result = new Polygon();
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		while(!token.equals(JsonToken.END_ARRAY)){
			if(token.equals(JsonToken.START_OBJECT)){
				Vertex vertex = new Vertex();
				while(!token.equals(JsonToken.END_OBJECT)){
					if(token.equals(JsonToken.FIELD_NAME)){
						token = jp.nextToken();
						if(jp.getCurrentName().equalsIgnoreCase("latitude")){
							vertex.setLatitude(jp.getFloatValue());
						}else if(jp.getCurrentName().equalsIgnoreCase("longitude")){
							vertex.setLongitude(jp.getFloatValue());
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
