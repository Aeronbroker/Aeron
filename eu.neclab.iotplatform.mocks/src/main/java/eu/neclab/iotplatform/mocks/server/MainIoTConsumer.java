package eu.neclab.iotplatform.mocks.server;

import java.net.BindException;
import java.util.Set;

import eu.neclab.iotplatform.mocks.utils.RangesUtil;

public class MainIoTConsumer {

	// private static int portNumber =
	// Integer.parseInt(System.getProperty("eu.neclab.ioplatform.mocks.iotconsumer.port",
	// "8001"));
	private static String portNumbers = System.getProperty(
			"eu.neclab.ioplatform.mocks.iotconsumer.ports", "8001");

	public static void main(String[] args) {
		
		Set<Integer> portSet = RangesUtil.rangesToSet(portNumbers);

		if (portSet == null) {
			System.out
					.println("Wrong eu.neclab.ioplatform.mocks.iotconsumer.ports property. "
							+ "Allowed only ranges (e.g. 8001-8005) and single ports (e.g. 8001) separated by comma. "
							+ "E.g. -Deu.neclab.ioplatform.mocks.iotconsumer.ports=8001-8005,8021,8025,8030-8040");
			System.exit(0);
		}

		for (int portNumber : portSet) {
			ServerDummy server = new ServerDummy();

			try {

				server.startServer(portNumber,
						"eu.neclab.iotplatform.mocks.iotconsumer");

			} catch (BindException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
			}
		}
	}
}