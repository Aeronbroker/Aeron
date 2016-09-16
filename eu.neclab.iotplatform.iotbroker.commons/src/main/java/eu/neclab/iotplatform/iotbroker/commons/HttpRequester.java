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

package eu.neclab.iotplatform.iotbroker.commons;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 * Contains a static method to make HTTP requests.
 */
public class HttpRequester {

	/** The logger. */
	private static Logger logger = Logger.getLogger(HttpRequester.class);

	/**
	 * @param url
	 *            The URL to connect to
	 * @param method
	 *            The HTTP method to use for connecting
	 * @param data
	 *            The message body
	 * @param contentType
	 *            Specification of the message body content type
	 */
	public static void sendRequest(URL url, String method, String data,
			String contentType) {

		HttpURLConnection con;
		try {
			con = (HttpURLConnection) url.openConnection();

			// add request header
			con.setRequestMethod(method);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			con.setRequestProperty("Accept", contentType);
			con.setRequestProperty("Content-Type", contentType);

			// Send request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(data);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			logger.info("\nSending " + method + " request to URL : " + url);
			logger.info("Content-Type : " + contentType);
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

			// print result
			logger.info("Response : " + response.toString());

		} catch (IOException e) {
			logger.debug("I/O Exception", e);
		}

	}

	public static int sendGenericRequest(URL url, String method, String data,
			String contentType) {

		HttpURLConnection con = null;
		int responseCode = 0;
		try {
			con = (HttpURLConnection) url.openConnection();

			// add request header
			con.setRequestMethod(method);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			con.setRequestProperty("Accept", contentType);
			con.setRequestProperty("Content-Type", contentType);

			// Send request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(data);
			wr.flush();
			wr.close();

			responseCode = con.getResponseCode();
			logger.info("\nSending " + method + " request to URL : " + url);
			logger.info("Content-Type : " + contentType);
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

			// print result
			logger.info("Response : " + response.toString());

		} catch (IOException e) {
			logger.info("I/O Exception", e);
			responseCode = -1;
		} finally {

			con.disconnect();

		}
		return responseCode;

	}

	public static String sendGenericRequestwithResponse(URL url, String method, String data,
			String contentType) {

		HttpURLConnection con = null;
		String responseMessage= null;
		int responseCode = 0;
		try {
			con = (HttpURLConnection) url.openConnection();

			// add request header
			con.setRequestMethod(method);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			con.setRequestProperty("Accept", contentType);
			con.setRequestProperty("Content-Type", contentType);

			// Send request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(data);
			wr.flush();
			wr.close();

			responseCode = con.getResponseCode();
			logger.info("\nSending " + method + " request to URL : " + url);
			logger.info("Content-Type : " + contentType);
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

			// print result
			logger.info("Response : " + response.toString());
			responseMessage = response.toString();

		} catch (IOException e) {
			logger.debug("I/O Exception", e);
			responseCode = -1;
			responseMessage = e.getMessage();
		} finally {

			con.disconnect();

		}
		return responseCode+"|"+responseMessage;

	}

}
