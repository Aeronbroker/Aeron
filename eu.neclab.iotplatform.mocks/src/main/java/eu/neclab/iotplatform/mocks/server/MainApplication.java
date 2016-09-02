package eu.neclab.iotplatform.mocks.server;

import java.net.BindException;

public class MainApplication {

	private static int portNumber = Integer.parseInt(System.getProperty("eu.neclab.ioplatform.mocks.iotapplication.ports", "8101"));
	
//	private static int portNumber = 8004;

	public static void main(String[] args) {

		ServerDummy server = new ServerDummy();

		try {

			server.startServer(portNumber,
					"eu.neclab.iotplatform.mocks.iotapplication");

		} catch (BindException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		}
	}

}