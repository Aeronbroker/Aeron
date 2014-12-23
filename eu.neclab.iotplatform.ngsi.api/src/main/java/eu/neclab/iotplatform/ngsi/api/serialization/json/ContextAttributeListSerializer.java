package eu.neclab.iotplatform.ngsi.api.serialization.json;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import eu.neclab.iotplatform.ngsi.api.datamodel.ContextAttribute;

/**
 *  Serializer to convert attribute lists to JSON format.
 */
public class ContextAttributeListSerializer extends JsonSerializer<List<ContextAttribute>>{

	@Override
	public void serialize(List<ContextAttribute> value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		
		jgen.writeArrayFieldStart("attributes");
		for(ContextAttribute attribute: value){
			provider.defaultSerializeValue(attribute, jgen);
		}
		jgen.writeEndArray();
		
	}

}
