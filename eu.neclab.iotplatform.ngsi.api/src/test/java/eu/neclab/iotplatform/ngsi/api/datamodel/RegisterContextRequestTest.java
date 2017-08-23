package eu.neclab.iotplatform.ngsi.api.datamodel;

public class RegisterContextRequestTest {

	// private RegisterContextRequest generateQueryContextRequest1() {
	//
	// RegisterContextRequest registerContextRequest = new
	// RegisterContextRequest();
	//
	// registerContextRequest.setContextRegistrationList(contextRegistrationList);
	//
	// return queryContextRequest;
	//
	// }

	private String serializeAndWrap(
			RegisterContextRequest registerContextRequest) {

		String jsonString = registerContextRequest.toJsonString();
		return jsonString = "{\"registerContextRequest\":" + jsonString + "}";

	}

	private RegisterContextRequest generateRegisterContextRequest() {

		RegisterContextRequest registerContextRequest = new RegisterContextRequest();

		registerContextRequest.setContextRegistrationList(NgsiObjectFactory
				.generateContextRegistrationListOneValue());
		
		
		return registerContextRequest;

	}

}
