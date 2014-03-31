package eu.neclab.iotplatform.iotbroker.core.junittests.tests;

import org.easymock.EasyMock;

import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextRequest;

public class Matchers {
	public static DiscoverContextAvailabilityRequest DiscoverContextAvailabilityMatching(DiscoverContextAvailabilityRequest expected){
		EasyMock.reportMatcher(new ContextRequestMatcher(expected));
		return null;
	}
	public static QueryContextRequest QueryContextRequestMatcher(QueryContextRequest expected){
		EasyMock.reportMatcher(new ContextRequestMatcher(expected));
		return null;
	}
	public static UpdateContextRequest UpdateContextRequestMatcher(UpdateContextRequest expected){
		EasyMock.reportMatcher(new ContextRequestMatcher(expected));
		return null;
	}
}
