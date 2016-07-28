package eu.neclab.iotplatform.mocks.server;

import java.net.BindException;

public class Test {

	private static int portNumber = 8080;

	public static void main(String[] args) {

		ServerDummy server = new ServerDummy();

		try {

			server.startServer(portNumber, "eu.fiware.neclab.dns");

		} catch (BindException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		}
	}

}
