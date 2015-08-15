/*******************************************************************************
 *   Copyright (c) 2015, NEC Europe Ltd.
 *   All rights reserved.
 *
 *   Authors:
 *           * Salvatore Longo - salvatore.longo@neclab.eu
 *           * Tobias Jacobs - tobias.jacobs@neclab.eu
 *           * Flavio Cirillo - flavio.cirillo@neclab.eu
 *
 *    Redistribution and use in source and binary forms, with or without
 *    modification, are permitted provided that the following conditions are met:
 *   1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   3. All advertising materials mentioning features or use of this software
 *     must display the following acknowledgment:
 *     This product includes software developed by NEC Europe Ltd.
 *   4. Neither the name of NEC nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific 
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY NEC ''AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL NEC BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

package eu.neclab.iotplatform.couchdb.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.log4j.Logger;

import eu.neclab.iotplatform.iotbroker.commons.FullHttpResponse;

public class HttpRequester {

	/** The logger. */
	private static Logger logger = Logger.getLogger(HttpRequester.class);

	public static FullHttpResponse sendPost(URL url, String data,
			String contentType) throws Exception {
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		// add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		if (contentType != null && !contentType.equals("")) {
			con.setRequestProperty("Accept", contentType);
			con.setRequestProperty("Content-Type", contentType);
		}

		logger.info("\nSending 'POST' request to URL : " + url + "\n"
				+ "Put parameters : " + data + "\n");

		if (data != null && !data.equals("")) {
			// Send put request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(data);
			wr.flush();
			wr.close();
		} else {
			con.setDoOutput(false);
		}
		int responseCode = con.getResponseCode();

		FullHttpResponse httpResponse = new FullHttpResponse(
				HttpVersion.HTTP_1_0, con.getResponseCode(),
				con.getResponseMessage());

		if (responseCode > 399 && responseCode < 500) {

			con.disconnect();
			return httpResponse;

		} else {

			// Read response
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			StringBuffer response = new StringBuffer();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
				response.append("\n");
			}
			in.close();

			httpResponse.setBody(response.toString());

			// logger.info("Response Code : " + responseCode);
			logger.info("\nResponse Code : " + responseCode + "\n"
					+ "Response : " + response.toString());

			con.disconnect();
			return httpResponse;
		}
	}

	public static FullHttpResponse sendGet(URL url) {

		FullHttpResponse httpResponse = null;

		HttpURLConnection connection = null;
		// BufferedReader rd = null;
		// StringBuilder sb = new StringBuilder();
		// String line = null;

		try {
			// set up out communications stuff
			connection = null;

			// Set up the initial connection
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setReadTimeout(10000);

			connection.connect();

			int responseCode = connection.getResponseCode();

			httpResponse = new FullHttpResponse(HttpVersion.HTTP_1_0,
					connection.getResponseCode(),
					connection.getResponseMessage());

			if (responseCode > 399 && responseCode < 500) {

				connection.disconnect();
				return httpResponse;

			} else {

				// Read response
				BufferedReader in = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				StringBuffer response = new StringBuffer();
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
					response.append("\n");
				}
				in.close();

				httpResponse.setBody(response.toString());

				// logger.info("Response Code : " + responseCode);
				logger.info("\nResponse Code : " + responseCode + "\n"
						+ "Response : " + response.toString());

				connection.disconnect();
			}

			// // read the result from the server
			// rd = new BufferedReader(new InputStreamReader(
			// connection.getInputStream()));
			//
			// while ((line = rd.readLine()) != null) {
			// sb.append(line + '\n');
			// }

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return new FullHttpResponse(HttpVersion.HTTP_1_0,
					HttpStatus.SC_INTERNAL_SERVER_ERROR, "");
		} catch (ProtocolException e) {
			e.printStackTrace();
			return new FullHttpResponse(HttpVersion.HTTP_1_0,
					HttpStatus.SC_INTERNAL_SERVER_ERROR, "");
		} catch (IOException e) {
			e.printStackTrace();
			return new FullHttpResponse(HttpVersion.HTTP_1_0,
					HttpStatus.SC_INTERNAL_SERVER_ERROR, "");
		} finally {
			// close the connection, set all objects to null
			connection.disconnect();
		}

		return httpResponse;

	}

	public static FullHttpResponse sendPut(URL url, String data,
			String contentType) throws Exception {

		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		// add request header
		con.setRequestMethod("PUT");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		if (contentType != null && !contentType.equals("")) {
			con.setRequestProperty("Accept", contentType);
			con.setRequestProperty("Content-Type", contentType);
		}

		logger.info("\nSending 'PUT' request to URL : " + url + "\n"
				+ "Put parameters : " + data + "\n");

		if (data != null && !data.equals("")) {
			// Send put request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(data);
			wr.flush();
			wr.close();
		} else {
			con.setDoOutput(false);
		}
		int responseCode = con.getResponseCode();

		FullHttpResponse httpResponse = new FullHttpResponse(
				HttpVersion.HTTP_1_0, con.getResponseCode(),
				con.getResponseMessage());

		if (responseCode > 399 && responseCode < 500) {

			con.disconnect();
			return httpResponse;

		} else {

			// Read response
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			StringBuffer response = new StringBuffer();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
				response.append("\n");
			}
			in.close();

			httpResponse.setBody(response.toString());

			// logger.info("Response Code : " + responseCode);
			logger.info("\nResponse Code : " + responseCode + "\n"
					+ "Response : " + response.toString());

			con.disconnect();
			return httpResponse;
		}

	}

	public static FullHttpResponse sendDelete(URL url) throws Exception {

		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		// add request header
		con.setRequestMethod("DELETE");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		logger.info("\nSending 'DELETE' request to URL : " + url);

		int responseCode = con.getResponseCode();
		logger.info("\nResponse Code : " + responseCode + "\n");
		
		FullHttpResponse httpResponse = new FullHttpResponse(
				HttpVersion.HTTP_1_0, con.getResponseCode(),
				con.getResponseMessage());
		
		con.disconnect();
		
		return httpResponse;

	}

	public static void sendRequest(URL url, String method, String data,
			String contentType) throws Exception {

		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		// add request header
		con.setRequestMethod(method);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("Accept", contentType);
		con.setRequestProperty("Content-Type", contentType);

		// Send put request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(data);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		logger.info("\nSending 'PUT' request to URL : " + url);
		logger.info("Post parameters : " + data);
		logger.info("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		con.disconnect();
		// print result
		logger.info("Response : " + response.toString());

	}

}
