package eu.neclab.iotplatform.iotbroker.commons;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.apache.log4j.Logger;

public class DurationUtils {

	private static Logger logger = Logger.getLogger(DurationUtils.class);

	/**
	 * Returns the current time in {@link Date} format.
	 */
	public static Date currentTime() {
		Calendar cal = Calendar.getInstance();
		return cal.getTime();

	}

	/**
	 * Returns the remaining duration, given an initial duration and the time
	 * elapsed so far.
	 * 
	 * @param oldDuration
	 *            The initial duration.
	 * @param timeElapsed
	 *            The time elapsed so far.
	 * @return The remaining duration.
	 */
	public static Duration newDuration(Duration oldDuration, long timeElapsed) {
		long ss = oldDuration.getTimeInMillis(new GregorianCalendar());

		if (logger.isDebugEnabled()) {
			logger.debug("Old Duration in millisecond:" + ss);
		}

		long remainingDuration = ss > timeElapsed ? ss - timeElapsed : 0;

		if (logger.isDebugEnabled()) {
			logger.debug("timeElapsed in millisecond:" + timeElapsed);
			logger.debug("remainingDuration in millisecond:"
					+ remainingDuration);
		}
		DatatypeFactory df = null;
		try {
			df = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			logger.error("Data Type Wrong!", e);
		}
		Duration duration = df.newDuration(remainingDuration);
		
		if (logger.isDebugEnabled()) {
			logger.debug("remainingDuration in Duration: " + duration);
		}

		return duration;

	}

	/**
	 * Converts a string representing milliseconds into the {@link Duration}
	 * format.
	 * 
	 * @param milliSeconds
	 *            String representing a number of milliseconds.
	 * @return The milliseconds in {@link Duration} format.
	 */
	public static Duration convertToDuration(long milliSeconds) {

		DatatypeFactory df = null;
		try {
			df = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			logger.error("Data Type Wrong!", e);
		}
		Duration duration = df.newDuration(milliSeconds);
		logger.debug("String " + milliSeconds + " to Duration: " + duration);

		return duration;
	}

}
