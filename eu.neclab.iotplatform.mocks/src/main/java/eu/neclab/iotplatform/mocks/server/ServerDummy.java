package eu.neclab.iotplatform.mocks.server;

import java.net.BindException;
import java.util.Map.Entry;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import com.sun.jersey.spi.container.servlet.ServletContainer;

import eu.neclab.iotplatform.mocks.utils.ServerConfiguration;

public class ServerDummy {

	private ServletHolder sh;

	// private static int numIoTagent;

	// private static Properties prop = new Properties();

	// public static void loadPropAndSet() {
	//
	// try {
	// prop.load(new FileInputStream(".\\config\\config.properties"));
	// ServerDummy.numIoTagent = Integer.parseInt(prop
	// .getProperty("numIoTagent"));
	// } catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }

	public void startServer(int port, String classBound) throws BindException,
			Exception {
		sh = new ServletHolder(ServletContainer.class);

		sh.setInitParameter(
				"com.sun.jersey.config.property.resourceConfigClass",
				"com.sun.jersey.api.core.PackagesResourceConfig");
		sh.setInitParameter("com.sun.jersey.config.property.packages",
				classBound);

		// custom parameters
		// Map<String, Object> initParameters = getCustomInitParamters(port);
		// sh.setInitParameters(initParameters);

		Server server = new Server(port);

		Context context = new Context(server, "/", Context.SESSIONS);

		context.addServlet(sh, "/*");
		server.start();

	}

	public void startServer(int port, String classBound,
			ServerConfiguration configurations) throws BindException, Exception {
		sh = new ServletHolder(ServletContainer.class);

		sh.setInitParameter(
				"com.sun.jersey.config.property.resourceConfigClass",
				"com.sun.jersey.api.core.PackagesResourceConfig");
		sh.setInitParameter("com.sun.jersey.config.property.packages",
				classBound);

		// custom parameters
		// Map<String, Object> initParameters = getCustomInitParamters(port);
		// sh.setInitParameters(initParameters);
		// sh.setInitParameters(configurations.toMap());
		for (Entry<String, String> entry : configurations.toMap().entrySet()) {
			sh.setInitParameter(entry.getKey(), entry.getValue());
		}

		Server server = new Server(port);

		Context context = new Context(server, "/", Context.SESSIONS);

		context.addServlet(sh, "/*");
		server.start();

	}

	// private Map<String, Object> getCustomInitParamters(int port,
	// Map<String, String> configurations) {
	//
	// Map<String, Object> initParameters = new HashMap<String, Object>();
	//
	// String mode = configurations
	// .get("eu.neclab.iotplaform.mocks.iotprovider." + port + ".mode");
	// if (mode != null) {
	//
	// initParameters.put("mode", Mode.fromString(mode, Mode.RANDOM));
	//
	// }
	//
	// String queryContextResponseFile = System.getProperty(
	// "eu.neclab.iotplaform.mocks.iotprovider." + port
	// + ".queryContextResponseFile",
	// "QueryContextResponse.xml");
	// if (queryContextResponseFile != null) {
	//
	// initParameters.put("queryContextResponseFile",
	// queryContextResponseFile);
	//
	// }
	//
	// return initParameters;
	// }

	public void stopServer() {

		try {
			sh.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// public static void main(String[] args) {
	//
	// ServerDummy server = new ServerDummy();
	// loadPropAndSet();
	//
	// try {
	// // For Testing
	// // server.startServer(8999, "eu.fiware.neclab.test.ngsi.configman");
	// //
	// // for(int i=0;i<numIoTagent;i++){
	// //
	// // server.startServer(Integer.parseInt(digits4(i)),
	// // "eu.fiware.neclab.test.ngsi.iotagent");
	// //
	// // }
	//
	// server.startServer(8999, "eu.fiware.neclab.test.ngsi.configman");
	// server.startServer(8001, "eu.fiware.neclab.test.ngsi.iotagent");
	// // server.startServer(8004, "eu.fiware.neclab.test.ngsi.iotagent");
	//
	// } catch (BindException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	//
	// e.printStackTrace();
	// }
	// }

	public static String digits4(int i) {

		if (i <= 9)
			return "800" + i;
		if (i <= 99)
			return "80" + i;
		if (i <= 900)
			return "8" + i;

		return "0";

	}

}
