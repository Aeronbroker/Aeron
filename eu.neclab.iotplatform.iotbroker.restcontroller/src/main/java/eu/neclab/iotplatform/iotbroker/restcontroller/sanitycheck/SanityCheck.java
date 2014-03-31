/*******************************************************************************
 *   Copyright (c) 2014, NEC Europe Ltd.
 *   All rights reserved.
 *   
 *   Authors:
 *           * Salvatore Longo - salvatore.longo@neclab.eu
 *           * Tobias Jacobs - tobias.jacobs@neclab.eu
 *           * Raihan Ul-Islam - raihan.ul-islam@neclab.eu
 *  
 *    Redistribution and use in source and binary forms, with or without
 *    modification, are permitted provided that the following conditions are met:
 *   1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   3. All advertising materials mentioning features or use of this software
 *     must display the following acknowledgement:
 *     This product includes software developed by NEC Europe Ltd.
 *   4. Neither the name of the NEC nor the
 *     names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
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
package eu.neclab.iotplatform.iotbroker.restcontroller.sanitycheck;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The class for executing the sanity check procedure for the
 * IoT Broker. When the sanity check procedure is called, it
 * simply returns a name, a type, and a version string.
 */
@XmlRootElement(name = "sanityCheck")
public class SanityCheck {

	/** The name. */
	private String name;

	/** The type. */
	private String type;

	/** The version. */
	private String version;

	/**
	 * Instantiates a new instance.
	 */
	public SanityCheck() {

	}

	/**
	 * Instantiates a new parameterized instance.
	 * 
	 * @param name
	 *            The name string.
	 * @param type
	 *            The type string.
	 * @param value
	 *            The value string.
	 */
	public SanityCheck(String name, String type, String value) {
		super();
		this.name = name;
		this.type = type;
		version = value;
	}

	/**
	 * Returns the name string.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name string.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the type string.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type string.
	 * 
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Returns the version string.
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Sets the version string.
	 * 
	 */
	public void setVersion(String value) {
		version = value;
	}

}
