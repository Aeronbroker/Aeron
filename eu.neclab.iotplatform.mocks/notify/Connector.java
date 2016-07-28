package eu.fiware.neclab.test.ngsi.notify;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Connector {

	private URL url = null;

	private final int connection_TIMEOUT = 3000;

	private HttpURLConnection connection = null;
	private OutputStreamWriter wr = null;
	private BufferedReader rd = null;
	private StringBuilder sb = null;
	private String line = null;

	public Connector() {

	}

	public Connector(URL url) {

		this.url = url;

	}

	public HttpURLConnection setConnection(String resource, String method,
			String request) throws IOException {

		URL urlConnection = new URL(url + resource);

		if (method.equals("GET")) {

			connection = (HttpURLConnection) urlConnection.openConnection();
			connection.setRequestMethod(method);
			connection.setDoOutput(true);
			connection.setReadTimeout(10000);
			connection.setConnectTimeout(connection_TIMEOUT);
			return connection;

		}

		System.out.println(url);

		System.out.println(urlConnection.toString());

		connection = (HttpURLConnection) urlConnection.openConnection();
		connection.setRequestMethod(method);
		connection.setRequestProperty("Content-Type", "application/xml");
		connection.setRequestProperty("Content-Length", ""
				+ request.getBytes().length);
		connection.setRequestProperty("Content-Language", "en-US");
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		// connection.setAllowUserInteraction(false);
		// connection.setInstanceFollowRedirects(false);

		connection.setConnectTimeout(connection_TIMEOUT);
		// connection.setRequestProperty("Accept", "text/xml");

		return connection;

	}

	public String start(String resource, String method, String request) {

		try {

			// Open Connection
			connection = setConnection(resource, method, request);
			System.out.println("Resource" + resource);

			// Set TIMEOUT

			connection.connect();

			if (method.equals("POST") || method.equals("PUT")) {

				// Create the OutputStram
				wr = new OutputStreamWriter(connection.getOutputStream());
				// send Message
				wr.write(request);

				// logger.info("Output Stream: " + wr.toString());
				wr.flush();
				// Close OutrputStream
				wr.close();
			}

			// check the connection HTTP responseCODE :TODO = is not needed???
			// if (connection.getResponseCode() != 200) {
			// System.out.println("Failed : HTTP error code : "
			// + connection.getResponseCode());
			// // TODO modify this if and add try catch
			// return "-1";
			// }

			// Create the BufferReader
			rd = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			sb = new StringBuilder();

			// Print the Output in a String
			while ((line = rd.readLine()) != null) {
				sb.append(line + '\n');
			}
			rd.close();
			// System.out.println(sb.toString());
			return sb.toString();

		} catch (ConnectException e) {

			e.printStackTrace();

			return "-1";

		} catch (IOException e) {

			e.printStackTrace();

			return "-1";

		} finally {

			connection.disconnect();
			rd = null;
			sb = null;
			wr = null;
			connection = null;

		}

	}

}
