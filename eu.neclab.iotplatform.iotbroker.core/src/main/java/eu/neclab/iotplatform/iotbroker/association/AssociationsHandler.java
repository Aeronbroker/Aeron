package eu.neclab.iotplatform.iotbroker.association;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.neclab.iotplatform.iotbroker.commons.EntityIDMatcher;
import eu.neclab.iotplatform.iotbroker.commons.XmlFactory;
import eu.neclab.iotplatform.ngsi.api.datamodel.AttributeAssociation;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElement;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElementResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadataAssociation;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.OperationScope;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.Restriction;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.ValueAssociation;
import eu.neclab.iotplatform.ngsi.api.ngsi9.Ngsi9Interface;
import eu.neclab.iotplatform.ngsi.association.datamodel.AssociationDS;
import eu.neclab.iotplatform.ngsi.association.datamodel.EntityAttribute;

public class AssociationsHandler {

	/** The logger. */
	private static Logger logger = Logger.getLogger(AssociationsHandler.class);

	// /** The implementation of the NGSI 9 interface */
	// private Ngsi9Interface ngsi9Impl;

	public AssociationsHandler() {
		super();
		// this.ngsi9Impl = ngsi9Impl;
	}

	public static void insertAssociationScope(
			DiscoverContextAvailabilityRequest request) {

		/*
		 * create associations operation scope for discovery
		 */
		OperationScope operationScope = new OperationScope(
				"IncludeAssociations", "SOURCES");

		/*
		 * Create a new restriction with the same attribute expression and
		 * operation scope as in the request.
		 */
		Restriction restriction;

		if (request.getRestriction() != null) {
			restriction = request.getRestriction();

		} else {

			restriction = new Restriction();
			request.setRestriction(restriction);
			restriction.setAttributeExpression("");

		}

		/*
		 * Add the associations operation scope to the the restriction.
		 */

		ArrayList<OperationScope> lstOperationScopes = null;

		if (restriction.getOperationScope() == null) {
			lstOperationScopes = new ArrayList<OperationScope>();
			lstOperationScopes.add(operationScope);
			restriction.setOperationScope(lstOperationScopes);
		} else {
			restriction.getOperationScope().add(operationScope);
		}

	}

	public static List<AssociationDS> getTransitiveList(
			DiscoverContextAvailabilityResponse discoveryResponse,
			QueryContextRequest queryRequest) {

		if (logger.isDebugEnabled()) {
			logger.debug("Receive discoveryResponse from Config Man:"
					+ discoveryResponse);
		}
		List<AssociationDS> assocList = AssociationsUtil
				.retrieveAssociation(discoveryResponse);

		if (logger.isDebugEnabled()) {
			logger.debug("Association List Size: " + assocList.size());
		}
		List<AssociationDS> additionalRequestList = AssociationsUtil
				.initialLstOfmatchedAssociation(queryRequest, assocList);

		if (logger.isDebugEnabled()) {
			logger.debug("(Step 1) Initial List Of matchedAssociation:"
					+ additionalRequestList);
		}
		List<AssociationDS> transitiveList = AssociationsUtil
				.transitiveAssociationAnalysisFrQuery(assocList,
						additionalRequestList);

		if (logger.isDebugEnabled()) {
			logger.debug("(Step 2 ) Transitive List Of matchedAssociation:"
					+ transitiveList);

			logger.debug("(Step 2 a) Final additionalRequestList List Of matchedAssociation:"
					+ additionalRequestList);
		}

		return transitiveList;

	}

	public static List<ContextRegistrationResponse> getAssociatedContextRegistrations(
			List<AssociationDS> transitiveList) {

		List<ContextRegistrationResponse> associatedContextRegistrations = new ArrayList<ContextRegistrationResponse>();

		for (AssociationDS associationDS : transitiveList) {

			ContextRegistration contextRegistration = new ContextRegistration();

			contextRegistration.setListEntityId(new ArrayList<EntityId>());
			contextRegistration.getListEntityId().add(
					associationDS.getSourceEA().getEntity());

			contextRegistration
					.setListContextRegistrationAttribute(new ArrayList<ContextRegistrationAttribute>());
			contextRegistration.getContextRegistrationAttribute().add(
					new ContextRegistrationAttribute(associationDS
							.getSourceEA().getEntityAttribute(), null, false,
							null));

			contextRegistration.setProvidingApplication(associationDS
					.getProvidingApplication());

			associatedContextRegistrations.add(new ContextRegistrationResponse(
					contextRegistration, null));

		}

		return associatedContextRegistrations;

	}

	public static List<ContextElementResponse> applySourceToTargetTransitivity(
			ContextElementResponse contextElementResponse,
			List<AssociationDS> transitiveList) {

		List<ContextElementResponse> newContextElementResponseList = new ArrayList<ContextElementResponse>();
		ContextElement contextElement = contextElementResponse
				.getContextElement();

		for (AssociationDS aDS : transitiveList) {
			ContextElement newContextElement = new ContextElement();

			if (logger.isDebugEnabled()) {
				logger.debug("Association Source EntityId: "
						+ aDS.getSourceEA().getEntity());
				logger.debug("Checked against Response EntityId: "
						+ contextElement.getEntityId());
			}

			// Checking if EntityID of QueryContextResponse matches with
			// any source entity id of transitiveList of associations
			if (EntityIDMatcher.matcher(aDS.getSourceEA().getEntity(),
					contextElement.getEntityId())) {

				if (logger.isDebugEnabled()) {
					logger.debug("EntityIds Matching!");
				}

				// Checking if Attribute of source EntityID of
				// transitiveList of associations is not empty or null

				if (!"".equals(aDS.getSourceEA().getEntityAttribute())) {
					// updating the EntityId with the Target EntityId of
					// transitiveList of associations
					newContextElement
							.setEntityId(aDS.getTargetEA().getEntity());
					boolean ifAttributeDomainNameExists = false;

					if (contextElement.getAttributeDomainName() != null
							&& !contextElement.getAttributeDomainName().equals(
									"")) {
						newContextElement.setAttributeDomainName(aDS
								.getTargetEA().getEntityAttribute());
						ifAttributeDomainNameExists = true;
					}

					if (!ifAttributeDomainNameExists) {
						// updating the Attribute of same EntityId with
						// the
						// Attribute of Target EntityId of
						// transitiveList of
						// associations
						List<ContextAttribute> lca = new ArrayList<ContextAttribute>();
						for (ContextAttribute ca : contextElement
								.getContextAttributeList()) {
							if (ca.getName().equals(
									aDS.getSourceEA().getEntityAttribute())) {
								ca.setName(aDS.getTargetEA()
										.getEntityAttribute());
								lca.add(ca);

							}
						}
						newContextElement.setContextAttributeList(lca);
					}

				} else if ("".equals(aDS.getSourceEA().getEntityAttribute())) {

					newContextElement
							.setEntityId(aDS.getTargetEA().getEntity());

					if (!"".equals(aDS.getTargetEA().getEntityAttribute())) {
						List<ContextAttribute> lca = new ArrayList<ContextAttribute>();
						for (ContextAttribute ca : contextElement
								.getContextAttributeList()) {
							ca.setName(ca.getName());
							lca.add(ca);
						}
						newContextElement.setContextAttributeList(lca);
					}
				}
				
				newContextElementResponseList.add(new ContextElementResponse(
						newContextElement, contextElementResponse.getStatusCode()));
			}


		}

		return newContextElementResponseList;
	}

	public static UpdateContextRequest applyAssociation(
			UpdateContextRequest request, Ngsi9Interface ngsi9Impl) {

		List<AssociationDS> listAssociationDS = new LinkedList<AssociationDS>();
		final List<ContextElement> lContextElements = request
				.getContextElement();
		final List<ContextElement> listContextElement = new LinkedList<ContextElement>();
		UpdateContextRequest updateContextRequest = null;
		// Going through individual ContextElement
		Iterator<ContextElement> it = lContextElements.iterator();
		while (it.hasNext()) {
			ContextElement contextElement = it.next();

			/*
			 * Retrieving EntityID and Entity Attributes for
			 * DiscoverContextAvailabilityRequest
			 */
			List<EntityId> eidList = new LinkedList<EntityId>();
			eidList.add(contextElement.getEntityId());

			List<ContextAttribute> lContextAttributes = contextElement
					.getContextAttributeList();
			List<String> attributeList = new LinkedList<String>();

			if (lContextAttributes != null && !lContextAttributes.isEmpty()) {

				Iterator<ContextAttribute> itAttributeList = lContextAttributes
						.iterator();
				while (itAttributeList.hasNext()) {
					ContextAttribute ca = itAttributeList.next();
					attributeList.add(ca.getName());
				}
			}
			// Creating Restriction OperationScopes for
			// DiscoverContextAvailabilityRequest
			OperationScope os = new OperationScope("IncludeAssociations",
					"TARGETS");
			List<OperationScope> loperOperationScopes = new LinkedList<OperationScope>();
			loperOperationScopes.add(os);
			Restriction restriction = new Restriction("", loperOperationScopes);

			// Create the NGSI 9 DiscoverContextAvailabilityRequest
			DiscoverContextAvailabilityRequest discoveryRequest = new DiscoverContextAvailabilityRequest(
					eidList, attributeList, restriction);
			// Get the NGSI 9 DiscoverContextAvailabilityResponse
			DiscoverContextAvailabilityResponse discoveryResponse = ngsi9Impl
					.discoverContextAvailability(discoveryRequest);

			/*
			 * Getting Associations information from
			 * DiscoverContextAvailabilityResponse
			 */

			List<ContextRegistrationResponse> lcrr = discoveryResponse
					.getContextRegistrationResponse();
			Iterator<ContextRegistrationResponse> itContextRegistrationResponse = lcrr
					.iterator();
			while (itContextRegistrationResponse.hasNext()) {
				ContextRegistrationResponse crr = itContextRegistrationResponse
						.next();

				List<ContextMetadata> lcmd = crr.getContextRegistration()
						.getListContextMetadata();

				Iterator<ContextMetadata> it1 = lcmd.iterator();
				while (it1.hasNext()) {
					ContextMetadata cmd = it1.next();
					if ("Association".equals(cmd.getType().toString())) {

						if (logger.isDebugEnabled()) {
							logger.debug("++++++++++++++++++++++++++++++++++++++++++++++++++befor value");
						}
						String s = "<value>" + cmd.getValue() + "</value>";
						ContextMetadataAssociation cma = (ContextMetadataAssociation) XmlFactory
								.convertStringToXml(cmd.toString(),
										ContextMetadataAssociation.class);
						ValueAssociation va = (ValueAssociation) XmlFactory
								.convertStringToXml(s, ValueAssociation.class);
						cma.setValue(va);

						if (va.getAttributeAssociation().size() == 0) {

							AssociationDS ads = new AssociationDS(
									new EntityAttribute(va.getSourceEntity(),
											""), new EntityAttribute(
											va.getSourceEntity(), ""), crr
											.getContextRegistration()
											.getProvidingApplication());
							listAssociationDS.add(ads);
						} else {
							List<AttributeAssociation> lAttributeAsociations = va
									.getAttributeAssociation();
							for (AttributeAssociation aa : lAttributeAsociations) {
								AssociationDS ads = new AssociationDS(
										new EntityAttribute(
												va.getSourceEntity(),
												aa.getSourceAttribute()),
										new EntityAttribute(va
												.getTargetEntity(), aa
												.getTargetAttribute()), crr
												.getContextRegistration()
												.getProvidingApplication());
								listAssociationDS.add(ads);
							}
						}
					}

				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("List of Assocaions from ConfigManager:"
						+ listAssociationDS.toString());
			}
			if (!listAssociationDS.isEmpty()) {
				for (ContextAttribute contextAttribute : contextElement
						.getContextAttributeList()) {

					List<EntityAttribute> loutput = AssociationsUtil
							.findAssociation(
									listAssociationDS,
									new EntityAttribute(contextElement
											.getEntityId(), contextAttribute
											.getName()));
					if (logger.isDebugEnabled()) {
						logger.debug("List of effective Associations:"
								+ loutput.toString());
					}
					EntityId currentEntityID = null;

					for (EntityAttribute entityAttribute1 : loutput) {
						List<ContextAttribute> lcaRes = new LinkedList<ContextAttribute>();
						if (currentEntityID != null) {
							if (!currentEntityID.getId().equals(
									entityAttribute1.getEntity().getId())) {
								ContextElement contextElementResponse = new ContextElement(
										entityAttribute1.getEntity(),
										contextElement.getAttributeDomainName(),
										lcaRes, contextElement
												.getDomainMetadata());
								listContextElement.add(contextElementResponse);
								currentEntityID = entityAttribute1.getEntity();
							}
						} else {
							ContextElement contextElementResponse = new ContextElement(
									entityAttribute1.getEntity(),
									contextElement.getAttributeDomainName(),
									lcaRes, contextElement.getDomainMetadata());
							listContextElement.add(contextElementResponse);
							currentEntityID = entityAttribute1.getEntity();
						}
						ContextAttribute contextAttribute1 = new ContextAttribute(
								"".equals(entityAttribute1.getEntityAttribute()) ? contextAttribute
										.getName() : entityAttribute1
										.getEntityAttribute(),
								contextAttribute.getType(), contextAttribute
										.getContextValue().toString(),
								contextAttribute.getMetadata());
						lcaRes.add(contextAttribute1);

					}
				}

			} else {

				listContextElement.add(contextElement);

			}

		}

		updateContextRequest = new UpdateContextRequest(listContextElement,
				request.getUpdateAction());

		return updateContextRequest;
	}
}
