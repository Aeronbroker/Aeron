package eu.neclab.iotplatform.ngsi.api.serialization.json;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.w3c.dom.Node;

/**
 *  Serializer to convert metadata values to JSON format.
 */
public class MetadataObjectValueSerializer extends JsonSerializer<Object>{

	@Override
	public void serialize(Object value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		
		if(value instanceof Node){
			Node node = (Node) value;
			String textValue = node.getFirstChild().getTextContent();
			jgen.writeString(textValue);
//			jgen.writeStringField("value", textValue);
		}else{
			provider.defaultSerializeValue(value, jgen);
		}
		
	}

}
