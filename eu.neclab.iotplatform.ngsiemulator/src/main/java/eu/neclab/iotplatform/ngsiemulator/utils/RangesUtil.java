package eu.neclab.iotplatform.ngsiemulator.utils;

import java.util.HashSet;
import java.util.Set;

public class RangesUtil {

	public static Set<Integer> rangesToSet(String ranges) {
		
		if (!ranges.matches("[0-9,-]*")) {
			return null;
		}
		
		Set<Integer> values = new HashSet<Integer>();

		String[] valuesRanges = ranges.split(",");

		for (int i = 0; i < valuesRanges.length; i++) {
			if (valuesRanges[i].contains("-")) {
				String[] portsRange = valuesRanges[i].split("-");
				Integer lowerBound = Integer.parseInt(portsRange[0]);
				Integer upperBound = Integer.parseInt(portsRange[1]);
				for (int j = lowerBound; j <= upperBound; j++)
					values.add(j);
			} else {
				values.add(Integer.parseInt(valuesRanges[i]));
			}
		}
		return values;
	}

}
