package eu.neclab.iotplatform.mocks.server;

import java.net.BindException;

public class MainIoTAgent {

	private static int portNumber = Integer.parseInt(System.getProperty("agent.port", "8001"));

	// private static int portNumber1 = 8030;

	public static void main(String[] args) {

		ServerDummy server = new ServerDummy();

		try {
			
			server.startServer(portNumber,
					"eu.neclab.iotplatform.confman.testing.iotagent");
			// server.startServer(portNumber1,
			// "eu.fiware.neclab.test.ngsi.iotagent");

		} catch (BindException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		}
	}

}
