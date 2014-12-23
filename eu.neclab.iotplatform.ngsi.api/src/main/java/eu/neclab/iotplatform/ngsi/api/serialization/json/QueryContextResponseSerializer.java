package eu.neclab.iotplatform.ngsi.api.serialization.json;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElementResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextResponse;

/**
 * Serializer to obtain JSON from QueryContextResponse objects. 
 */
public class QueryContextResponseSerializer extends JsonSerializer<QueryContextResponse>{

	@Override
	public void serialize(QueryContextResponse value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.writeStartObject();
		jgen.writeArrayFieldStart("contextResponses");
		for(ContextElementResponse response : value.getListContextElementResponse()){
			provider.defaultSerializeValue(response, jgen);
		}
		jgen.writeEndArray();
		jgen.writeEndObject();
		
	}

}
