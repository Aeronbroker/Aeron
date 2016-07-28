package eu.neclab.iotplatform.mocks.server;

import java.net.BindException;

public class MainApplication {

	private static int portNumber = Integer.parseInt(System.getProperty("application.port", "8001"));
	
//	private static int portNumber = 8004;

	public static void main(String[] args) {

		ServerDummy server = new ServerDummy();

		try {

			server.startServer(portNumber,
					"eu.neclab.iotplatform.confman.testing.iotagent");

		} catch (BindException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		}
	}

}