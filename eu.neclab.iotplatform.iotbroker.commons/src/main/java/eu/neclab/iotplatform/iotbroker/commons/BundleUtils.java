package eu.neclab.iotplatform.iotbroker.commons;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class BundleUtils {

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
		if (!interfaceObject.getClass().toString().matches("com.sun.proxy.*")) {
			return true;
		}
		
		/* It is returned if there is a service registered to such reference */
		return (service != null);
	}

}
