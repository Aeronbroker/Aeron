package eu.neclab.iotplatform.mocks.server;

import java.net.BindException;

public class MainDNS {

	private static int portNumber = 8080;

	public static void main(String[] args) {

		ServerDummy server = new ServerDummy();

		try {

			server.startServer(portNumber, "eu.fiware.neclab.test.ngsi.dns");

		} catch (BindException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		}
	}

}
