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


package eu.neclab.iotplatform.ngsiemulator.utils;

import java.util.Random;
import java.util.UUID;

import org.apache.log4j.Logger;

/**
 * A class for generating Strings that can be used as unique identifiers.
 */
public class UniqueIDGenerator {

	// Logger
	private static Logger logger = Logger.getLogger(UniqueIDGenerator.class);

	private String uniqueId;

	private String macAddress;

	public UniqueIDGenerator() {
		String macAddress = MacAddress.getMACADDRESS();
		if (macAddress == null || macAddress.equals("")) {
			this.macAddress = genRandomMac();
			logger.warn("Not possible to retrieve localhost MAC Adress, "
					+ "a random MAC address has been generated: "
					+ this.macAddress);
		} else {
			this.macAddress = macAddress.replace(":", "-");
		}
	}

	private String genRandomMac() {
		StringBuffer b = new StringBuffer();
		Random rand = new Random();
		for (int i = 0; i < 6; i++) {
			int k = rand.nextInt(100);
			if (k < 10) {
				b.append("0" + k);
			} else {
				b.append(k);
			}
			if (i < 5) {
				b.append("-");
			}
		}
		return b.toString();
	}

	/**
	 * Generates and returns the next unique ID.
	 */
	public String getNextUniqueId() {

		String tempResult = UUID.randomUUID().toString().replaceAll("-", "");
		uniqueId = mix(tempResult, macAddress);

		return uniqueId;
	}

	private static String mix(String a, String b) {
		final int aLength = a.length();
		final int bLength = b.length();
		final int min = Math.min(aLength, bLength);
		final StringBuilder sb = new StringBuilder(aLength + bLength);
		for (int i = 0; i < min; i++) {
			sb.append(a.charAt(i));
			sb.append(b.charAt(i));
		}
		if (aLength > bLength) {
			sb.append(a, bLength, aLength);
		} else if (aLength < bLength) {
			sb.append(b, aLength, bLength);
		}
		return sb.toString();
	}

}
