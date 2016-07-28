package eu.neclab.iotplatform.mocks.server;

import java.net.BindException;
import java.util.HashSet;
import java.util.Set;

public class MainIoTConsumer {

	// private static int portNumber =
	// Integer.parseInt(System.getProperty("eu.neclab.ioplatform.mocks.iotconsumer.port",
	// "8001"));
	private static String portNumbers = System.getProperty(
			"eu.neclab.ioplatform.mocks.iotconsumer.ports", "8001");

	public static void main(String[] args) {

		if (!portNumbers.matches("[0-9,-]*")) {
			System.out
					.println("Wrong eu.neclab.ioplatform.mocks.iotconsumer.ports property. "
							+ "Allowed only ranges (e.g. 8001-8005) and single ports (e.g. 8001) separated by comma. "
							+ "E.g. -Deu.neclab.ioplatform.mocks.iotconsumer.ports=8001-8005,8021,8025,8030-8040");
			System.exit(0);
		}

		Set<Integer> portSet = new HashSet<Integer>();

		String[] ports = portNumbers.split(",");

		for (int i = 0; i < ports.length; i++) {
			if (ports[i].contains("-")) {
				String[] portsRange = ports[i].split("-");
				Integer lowerBound = Integer.parseInt(portsRange[0]);
				Integer upperBound = Integer.parseInt(portsRange[1]);
				for (int j = lowerBound; j <= upperBound; j++)
					portSet.add(j);
			} else {
				portSet.add(Integer.parseInt(ports[i]));
			}
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