package eu.neclab.iotplatform.ngsiemulator.server;

import java.net.BindException;
import java.util.Set;

import eu.neclab.iotplatform.ngsiemulator.utils.RangesUtil;

public class MainIoTApplication {

//	private static int portNumber = Integer.parseInt(System.getProperty("eu.neclab.ioplatform.ngsiemulator.iotapplication.ports", "8201"));
	private static String portNumbers = System.getProperty(
			"eu.neclab.ioplatform.ngsiemulator.iotapplication.ports", "8201");
	
//	private static int portNumber = 8004;

	public static void main(String[] args) {
		
		Set<Integer> portSet = RangesUtil.rangesToSet(portNumbers);

		if (portSet == null) {
			System.out
					.println("Wrong eu.neclab.ioplatform.ngsiemulator.iotapplication.ports property. "
							+ "Allowed only ranges (e.g. 8201-8205) and single ports (e.g. 8201) separated by comma. "
							+ "E.g. -Deu.neclab.ioplatform.ngsiemulator.iotapplication.ports=8201-8205,8221,8225,8230-8240");
			System.exit(0);
		}

		for (int portNumber : portSet) {
			ServerDummy server = new ServerDummy();

			try {

				server.startServer(portNumber,
						"eu.neclab.iotplatform.ngsiemulator.iotapplication");

			} catch (BindException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
			}
		}

//		ServerDummy server = new ServerDummy();
//
//		try {
//
//			server.startServer(portNumber,
//					"eu.neclab.iotplatform.ngsiemulator.iotapplication");
//
//		} catch (BindException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//
//			e.printStackTrace();
//		}
	}

}