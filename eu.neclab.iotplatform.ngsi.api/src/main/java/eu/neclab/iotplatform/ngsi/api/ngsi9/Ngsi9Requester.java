package eu.neclab.iotplatform.ngsi.api.ngsi9;

import java.net.URI;

import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.RegisterContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.RegisterContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextAvailabilitySubscriptionRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextAvailabilitySubscriptionResponse;

public interface Ngsi9Requester {

	/**
	 * Operation for registering availability of context information.
	 * 
	 * @param request
	 *            The NGS9 9 RegisterContextRequest.
	 * @param uri
	 * @return
	 */
	RegisterContextResponse registerContext(RegisterContextRequest request,
			URI uri);

	/**
	 * Operation for retrieving context availability information.
	 * 
	 * @param request
	 *            The NGS9 9 DiscoverContextAvailabilityRequest.
	 * @param uri
	 * @return The NGS9 9 DiscoverContextAvailabilityResponse.
	 */
	DiscoverContextAvailabilityResponse discoverContextAvailability(
			DiscoverContextAvailabilityRequest request, URI uri);

	/**
	 * Operation for subscribing to context availability information.
	 * 
	 * @param request
	 *            The NGS9 9 SubscribeContextAvailabilityRequest.
	 * @param uri
	 * @return The NGS9 9 SubscribeContextAvailabilityResponse.
	 */
	SubscribeContextAvailabilityResponse subscribeContextAvailability(
			SubscribeContextAvailabilityRequest request, URI uri);

	/**
	 * Operation for updating context availability subscriptions.
	 * 
	 * @param request
	 *            The NGS9 9 UpdateContextAvailabilitySubscriptionRequest.
	 * @param uri
	 * @return The NGS9 9 UpdateContextAvailabilitySubscriptionResponse.
	 */
	UpdateContextAvailabilitySubscriptionResponse updateContextAvailabilitySubscription(
			UpdateContextAvailabilitySubscriptionRequest request, URI uri);

	/**
	 * Operation for canceling context availability subscriptions.
	 * 
	 * @param request
	 *            The NGS9 9 UnsubscribeContextAvailabilityRequest.
	 * @param uri
	 * @return The NGS9 9 UnsubscribeContextAvailabilityResponse.
	 */
	UnsubscribeContextAvailabilityResponse unsubscribeContextAvailability(
			UnsubscribeContextAvailabilityRequest request, URI uri);

	/**
	 * Operation for processing context availability notifications.
	 * 
	 * @param request
	 *            The NGS9 9 NotifyContextAvailabilityRequest.
	 * @param uri
	 * @return The NGS9 9 NotifyContextAvailabilityResponse.
	 */
	NotifyContextAvailabilityResponse notifyContextAvailability(
			NotifyContextAvailabilityRequest request, URI uri);

}
