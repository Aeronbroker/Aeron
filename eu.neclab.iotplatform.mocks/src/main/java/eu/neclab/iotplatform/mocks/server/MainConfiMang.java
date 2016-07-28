package eu.neclab.iotplatform.mocks.server;

import java.net.BindException;

public class MainConfiMang {

	private static int portNumber = 8002;

	public static void main(String[] args) {

		ServerDummy server = new ServerDummy();

		try {

			server.startServer(portNumber,
					"eu.fiware.neclab.test.ngsi.configman");

		} catch (BindException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		}
	}

}
