package eu.neclab.iotplatform.iotbroker.embeddediotagent.registry;

import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.MetadataTypes;

public class EmbeddedAgentIdentifierFactory {

	public static ContextMetadata getEmbeddedAgentIdentifier(String identifier) {
		return new ContextMetadata(
				MetadataTypes.EmbeddedAgentIdentifier.getName(), null,
				identifier);
	}

	public static boolean compare(ContextMetadata embeddedAgentIdentifier,
			ContextMetadata contextMetadata) {

		if (embeddedAgentIdentifier == null) {
			if (contextMetadata == null) {
				return true;
			} else {
				return false;
			}
		}

		if (contextMetadata == null) {
			return false;
		}

//		System.out.println(MetadataTypes.EmbeddedAgentIdentifier.getName());

		if (contextMetadata.getName().toLowerCase().equals(
				MetadataTypes.EmbeddedAgentIdentifier.getName().toLowerCase())) {

			if (contextMetadata.getValue() instanceof String) {

				if (((String) embeddedAgentIdentifier.getValue())
						.equals((String) contextMetadata.getValue())) {
					return true;

				} else {
					return false;
				}

			} else {
				return false;
			}

		} else {
			return false;
		}
	}

}
