/*******************************************************************************
 * Copyright (c) 2016, NEC Europe Ltd.
 * All rights reserved.
 * 
 * Authors:
 *          * NEC IoT Platform Team - iotplatform@neclab.eu
 *          * Flavio Cirillo - flavio.cirillo@neclab.eu
 *          * Tobias Jacobs - tobias.jacobs@neclab.eu
 *          * Gurkan Solmaz - gurkan.solmaz@neclab.eu
 *          * Salvatore Longo
 *          * Raihan Ul-Islam
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions 
 * are met:
 * 1. Redistributions of source code must retain the above copyright 
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above 
 * copyright notice, this list of conditions and the following disclaimer 
 * in the documentation and/or other materials provided with the 
 * distribution.
 * 3. All advertising materials mentioning features or use of this 
 * software must display the following acknowledgment: This 
 * product includes software developed by NEC Europe Ltd.
 * 4. Neither the name of NEC nor the names of its contributors may 
 * be used to endorse or promote products derived from this 
 * software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY NEC ''AS IS'' AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY 
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN 
 * NO EVENT SHALL NEC BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT 
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED 
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH 
 * DAMAGE.
 ******************************************************************************/

package eu.neclab.iotplatform.ngsiemulator.server;

import java.net.BindException;
import java.util.Map.Entry;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import com.sun.jersey.spi.container.servlet.ServletContainer;

import eu.neclab.iotplatform.ngsiemulator.utils.ServerConfiguration;

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
	// .get("eu.neclab.iotplaform.ngsiemulator.iotprovider." + port + ".mode");
	// if (mode != null) {
	//
	// initParameters.put("mode", Mode.fromString(mode, Mode.RANDOM));
	//
	// }
	//
	// String queryContextResponseFile = System.getProperty(
	// "eu.neclab.iotplaform.ngsiemulator.iotprovider." + port
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
