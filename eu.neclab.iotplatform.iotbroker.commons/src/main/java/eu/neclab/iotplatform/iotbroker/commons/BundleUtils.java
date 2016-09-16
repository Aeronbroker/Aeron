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

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class BundleUtils {
	
	/** The logger. */
	private static Logger logger = Logger.getLogger(BundleUtils.class);

	/**
	 * This method return true if a service is registered to the interfaceObject
	 * within the bundle context of clazz
	 * 
	 * @param clazz
	 *            Object from where get the bundle context
	 * @param interfaceObject
	 *            Interface object to be checked
	 * @return
	 */
	public static boolean isServiceRegistered(Object clazz,
			Object interfaceObject) {

		if (interfaceObject == null) {
			return false;
		}

		// if (interfaceObject instanceof Proxy)



		BundleContext bundleContext = FrameworkUtil.getBundle(clazz.getClass())
				.getBundleContext();

		/* Getting the interfaces implemented by the object */
		Class<?>[] clazzes = interfaceObject.getClass().getInterfaces();

		/* Getting the ServiceReference of the interface, if any */
		ServiceReference service = bundleContext.getServiceReference(clazzes[0]
				.getName());
		
//		 if (interfaceObject instanceof com.sun.Proxy) {
		/* Getting the bundleContext of the clazz */
		if (!interfaceObject.getClass().toString().matches(".*com.sun.proxy.*")) {
			return true;
		}
		
		/* It is returned if there is a service registered to such reference */
		return (service != null);
	}
}
