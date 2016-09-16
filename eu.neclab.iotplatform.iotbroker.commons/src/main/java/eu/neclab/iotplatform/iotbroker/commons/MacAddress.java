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

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

import org.apache.log4j.Logger;

/**
 * A class giving access to the system MAC address.
 */
public class MacAddress {

	private static Logger logger = Logger.getLogger(MacAddress.class);
	private static String MACADDRESS;

	/**
	 * Creates a new instance of the class. Objects of this class are stateless,
	 * which means that a singleton instance will be sufficient.
	 */
	public MacAddress() {
		super();

	}

	/**
	 * Returns the MAC address of the system.
	 */
	public static String getMACADDRESS() {

		StringBuilder b = new StringBuilder();
		try {

			Enumeration<NetworkInterface> nets = NetworkInterface
					.getNetworkInterfaces();

			for (NetworkInterface netIf : Collections.list(nets)) {
				byte[] mac = netIf.getHardwareAddress();
				if (mac != null) {

					for (int i = 0; i < mac.length; i++) {
						b.append(String.format("%02X%s", mac[i],
								i < mac.length - 1 ? "-" : "").toString());
					}
					MACADDRESS = b.toString();
					logger.debug("MAC address found : " + MACADDRESS);
					break;
				}
			}
			if (MACADDRESS == null || MACADDRESS.equals("")) {
				logger.info("Address doesn't exist or is not " + "accessible.");
			}
		} catch (SocketException e) {
			logger.debug("SocketException", e);
		}

		return MACADDRESS;
	}

}
