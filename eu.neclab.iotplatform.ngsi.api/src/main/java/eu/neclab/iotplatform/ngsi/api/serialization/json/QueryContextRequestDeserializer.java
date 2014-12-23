package eu.neclab.iotplatform.ngsi.api.serialization.json;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.OperationScope;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.Restriction;

/**
 * Deserializer to obtain QueryContextRequest objects from JSON objects. 
 */
public class QueryContextRequestDeserializer extends JsonDeserializer<QueryContextRequest>{

	@Override
	public QueryContextRequest deserialize(JsonParser jp,
			DeserializationContext ctxt) throws IOException,
			JsonProcessingException {
		QueryContextRequest result = new QueryContextRequest();
		
		JsonToken token = jp.getCurrentToken();
		while(token != null){
			if(token.equals(JsonToken.FIELD_NAME)){
				String fieldName = jp.getCurrentName();
				if(fieldName.equals("entities")){
					result.setEntityIdList(getEntities(jp));
				}else if(fieldName.equals("attributes")){
					result.setAttributeList(getAttributes(jp));
				}else if(fieldName.equals("restriction")){
					result.setRestriction(getRestriction(jp));
				}  
			
			}
			token = jp.nextToken();
		}
		return result;
	}

	private Restriction getRestriction(JsonParser jp) throws JsonParseException, IOException {
		JsonToken token = jp.nextToken();
		Restriction restriction = new Restriction();
		while(!token.equals(JsonToken.END_OBJECT)){
			if(token.equals(JsonToken.FIELD_NAME)){
				String fieldName = jp.getCurrentName();
				if(fieldName.equals("scopes")){
					restriction.setOperationScope(getRestrictionScopes(jp));
				}else if(fieldName.equals("attributeexpression")){
					restriction.setAttributeExpression(getRestrictionAttributeExpression(jp));
				}
			}
			token = jp.nextToken();
		}
		
		
		return restriction;
	}

	private String getRestrictionAttributeExpression(JsonParser jp) throws JsonParseException, IOException {
		jp.nextToken();
		return jp.getText();
	}

	private List<OperationScope> getRestrictionScopes(JsonParser jp) throws JsonParseException, IOException {
		JsonToken token = jp.nextToken();
		if(!token.equals(JsonToken.START_ARRAY)){
			throw new JsonParseException("scopes has to be an array", null);
		}
		ArrayList<OperationScope> result = new ArrayList<OperationScope>();
		token = jp.nextToken();
		while(!token.equals(JsonToken.END_ARRAY)){
			OperationScope scope = new OperationScope();
			String type = null, value = null;
			while(!token.equals(JsonToken.END_OBJECT)){
				if(token.equals(JsonToken.FIELD_NAME)){
					String fieldName = jp.getCurrentName();
					token = jp.nextToken();
					if(fieldName.equals("type")){
						type = jp.getText();
					}else if(fieldName.equals("value")){
						value = jp.getText();
					}
				}
				token = jp.nextToken();
			}
			if(value == null || type == null){
				throw new JsonParseException("Provided scope is missing a required field type or value", null);
			}
			scope.setScopeType(type);
			scope.setScopeValue(value);
		}
		return result;
	}

	private List<String> getAttributes(JsonParser jp) throws JsonParseException, IOException {
		JsonToken token = jp.nextToken();
		if(!token.equals(JsonToken.START_ARRAY)){
			throw new JsonParseException("attributes has to be an array", null);
		}
		token = jp.nextToken();
		ArrayList<String> attributes = new ArrayList<String>();
		while(!token.equals(JsonToken.END_ARRAY)){
			attributes.add(jp.getText());
			token = jp.nextToken();
		}
		return attributes;
	}

	private List<EntityId> getEntities(JsonParser jp) throws JsonParseException, IOException {
		ArrayList<EntityId> entities = new ArrayList<EntityId>();
		JsonToken token = jp.nextToken();
		if(!token.equals(JsonToken.START_ARRAY)){
			throw new JsonParseException("entities has to be an array", null);
		}
		token = jp.nextToken();
		while(!token.equals(JsonToken.END_ARRAY)){
			EntityId entityId = new EntityId();
			String id = null, type = null;
			Boolean isPattern = null;
			while(!token.equals(JsonToken.END_OBJECT)){
				if(token.equals(JsonToken.FIELD_NAME)){
					String fieldName = jp.getCurrentName();
					token = jp.nextToken();
					if(fieldName.equals("id")){
						id = jp.getText();
					}else if(fieldName.equals("type")){
						type = jp.getText();
					}else if(fieldName.equals("isPattern")){
						if(!token.equals(JsonToken.VALUE_TRUE) && !token.equals(JsonToken.VALUE_FALSE)){
							throw new JsonParseException("isPattern has to be a boolean", null);
						}
						isPattern = jp.getBooleanValue();
					} 
					
				}
				token = jp.nextToken();
			}
			if(id == null || type == null || isPattern == null){
				throw new JsonParseException("Provided entity is missing a required field like id, type or isPattern", null);
			}
			entityId.setId(id);
			try {
				entityId.setType(new URI(type));
			} catch (URISyntaxException e) {
				throw new JsonParseException("provided type is not a valid URI", null);
			}
			entityId.setIsPattern(isPattern);
			entities.add(entityId);
			token = jp.nextToken();
		}
		
		return entities;
	}

}
