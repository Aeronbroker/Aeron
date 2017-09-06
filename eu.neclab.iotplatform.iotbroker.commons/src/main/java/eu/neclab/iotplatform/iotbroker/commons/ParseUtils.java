package eu.neclab.iotplatform.iotbroker.commons;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

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
		} else if (string instanceof Integer) {
			integer = (Integer) string;
		} else {
			integer = defaultInt;
		}

		return integer;

	}

	public static Boolean parseBooleanOrDefault(Object string,
			boolean defaultBoolean) {

		Boolean bool = null;

		if (string != null && string instanceof String) {
			try {
				bool = Boolean.parseBoolean((String) string);
			} catch (Exception e) {
				logger.warn("Input format not valid: " + string + ". "
						+ e.getCause());
				bool = defaultBoolean;
			}
		} else if (string instanceof Integer) {
			bool = (Boolean) string;
		} else {
			bool = defaultBoolean;
		}

		return bool;

	}

	public static Set<String> parseSetFromString(Object string) {
		Set<String> set = new HashSet<String>();
		if (string != null && string instanceof String) {
			StringTokenizer st = new StringTokenizer(((String) (string))
					.replace("[", "").replace("]", ""), ",");
			while (st.hasMoreTokens())
				set.add(st.nextToken());
		}
		return set;
	}
}
