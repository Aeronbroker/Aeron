package eu.neclab.iotplatform.ngsi.api.datamodel;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

public class QueryContextRequestTest {

	private static Logger logger = Logger
			.getLogger(QueryContextRequestTest.class);

	private QueryContextRequest generateQueryContextRequest1() {

		QueryContextRequest queryContextRequest = new QueryContextRequest();

		queryContextRequest.setEntityIdList(NgsiObjectFactory
				.generateEntityIdListFull());

		queryContextRequest.setAttributeList(NgsiObjectFactory
				.generateAttributeListFull());

		queryContextRequest.setRestriction(NgsiObjectFactory
				.generateRestrictionFull());

		return queryContextRequest;

	}

	private String serializeAndWrap(QueryContextRequest queryContextRequest) {

		String jsonString = queryContextRequest.toJsonString();
		return jsonString = "{\"queryContextRequest\":" + jsonString + "}";

	}

	@Test
	public void toJsonTest() {

		try {

//			String jsonString = serializeAndWrap(generateQueryContextRequest1());
			String jsonString = generateQueryContextRequest1().toJsonString();
			logger.info(jsonString);

			JsonNode jsonNode = JsonLoader.fromString(jsonString);

			JsonNode ngsiSchema = JsonLoader.fromFile(new File(
					"jsonSchema/ngsi-v0.1.jsonschema"));

			JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
			JsonSchema schema = factory.getJsonSchema(ngsiSchema);

			ProcessingReport report;

			report = schema.validate(jsonNode);
			logger.info(report);
			Iterator<ProcessingMessage> msgg = report.iterator();
			while (msgg.hasNext()) {
				ProcessingMessage msg = msgg.next();
				logger.info(msg);

			}
			// System.out.println(report.);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
