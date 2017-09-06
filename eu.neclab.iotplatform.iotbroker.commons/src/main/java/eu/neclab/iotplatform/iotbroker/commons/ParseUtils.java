package eu.neclab.iotplatform.iotbroker.commons;

import org.apache.log4j.Logger;

public class ParseUtils {

	private static Logger logger = Logger.getLogger(ParseUtils.class);

	public static Integer parseIntOrDefault(Object string, int defaultInt) {

		Integer integer = null;

		if (string != null && string instanceof String) {
			try {
				integer = Integer.parseInt((String) string);
			} catch (NumberFormatException e) {
				logger.warn("Input format not valid: " + string + ". "
						+ e.getCause());
				integer = defaultInt;
			}
		} else {
			integer = defaultInt;
		}

		return integer;

	}
}
