A Plugin for Composing Entities from other Entities
========================================

From release 4.4, the IoT Broker provides the *entity composer plugin* having the functionality to compose entities from other entities by means of aggregating attribute values and renaming attributes. This plugin is optional. If deployed, it extends the IoT Broker by a [Query Service](../doc/extensionpoint).

Compiling the plugin
--
The plugin is compiled by MAVEN 3 using JDK 7. As it is included by the [IoT Broker builder maven project](../eu.neclab.iotplatform.iotbroker.builder), it can be compiled by following the steps in the general [README](../README.ME).

Activating and deactivating the plugin
--
The plugin is enabled by adding the following line into the `IoTBroker-runner/iotbroker.conf.local`:
```
iotbroker_entitycomposer="enabled"
```
then run
```
cd IoTBroker-runner/
./setup.sh
```
and then restart the IoT Broker.

No additional configuration is necessary.

In order to disable it simply change the line in the `IoTBroker-runner/iotbroker.conf.local`to:
```
iotbroker_entitycomposer="disabled"
```
and then 
```
cd IoTBroker-runner/
./setup.sh
```
and restart the IoT Broker.


Entity Composition Workflow
--

 1. **Registering Composition Rules:**
 Rules for composing entities from other entities need to be registered in the IoT Discovery GE. Each such rule includes a *target entity*, a number of *source entities*, and a description of the function for generating the target entity from the sources. 

 2. **Querying Target Entities:** 
  - When target entities are queried, the IoT Broker core will discover relevant rules and pass them to the entity composer plugin.
  - The plugin then generates queries for the source entities and passes these queries back to the IoT Broker core, who will try to retrieve the source data on behalf of the plugin.
  - From the retrieved source entity data the plugin generates the target entity data and passes it back to the IoT Broker core.
  - The IoT Broker core will then include this data in its response to the original query.

Composition Rule syntax and semantics
--

Entity composition rules are expressed as *RegistrationMetadata* inside Context Registrations. This is best explained by an example.

```
	<contextRegistration>
		<entityIdList>
			<entityId type="Room" isPattern="false">
				<id>ConferenceRoom</id>
			</entityId>
		</entityIdList>
		<contextRegistrationAttributeList>
			<contextRegistrationAttribute>
				<name>temperature</name>
				<isDomain>false</isDomain>						
			</contextRegistrationAttribute>
		</contextRegistrationAttributeList>
		<providingApplication></providingApplication>
		<registrationMetadata>
			<contextMetadata>
				<name>aggregationInfo</name>
				<type>org.fiware.type.metadata.sourceinformation</type>
				<value>
					<sourceType>org.fiware.type.sourceinformation.aggregation</sourceType>
					<sourceData>
						<targetEntity type="Room" isPattern="false">
							<id>ConferenceRoom</id>						
						</targetEntity>
						<sourceEntityList>
							<entityId type="TemperatureSensor" isPattern="false">
								<id>Sensor_A</id>						
							</entityId>
							<entityId type="TemperatureSensor" isPattern="false">
								<id>Sensor_B</id>						
							</entityId>
						</sourceEntityList>
						<attributeAssociationList>
							<attributeAssociation>
								<sourceAttribute>sensorValue</sourceAttribute>
								<targetAttribute>temperature</targetAttribute>
							</attributeAssociation>
						</attributeAssociationList>
						<aggregationType>
							AVG
						</aggregationType>
					</sourceData>
				</value>
			</contextMetadata>
		</registrationMetadata>
	</contextRegistration>
```

(If this xml structure does not look familiar, it might help to first get familiar with the FIWARE NGSI API in general and the *ContextRegistration* datatype in particular.)

The example shows a *contextRegistration* which registers an entity called *ConferenceRoom*. A *providingApplication* for this entity is however not given. Instead, the *registrationMetatdata* contains a *contextMetadata* unit of type *org.fiware.type.metadata.sourceinformation*. Whenever this metadata type is present, the metadata value is expected to contain information about how to retrieve information about the registered entity.

Now let us have a closer look on this particular metadata value:

```
	<value>
		<sourceType>org.fiware.type.sourceinformation.aggregation</sourceType>
		<sourceData>
			<targetEntity type="Room" isPattern="false">
				<id>ConferenceRoom</id>						
			</targetEntity>
			<sourceEntityList>
				<entityId type="TemperatureSensor" isPattern="false">
					<id>Sensor_A</id>						
				</entityId>
				<entityId type="TemperatureSensor" isPattern="false">
					<id>Sensor_B</id>						
				</entityId>
			</sourceEntityList>
			<attributeAssociationList>
				<attributeAssociation>
					<sourceAttribute>sensorValue</sourceAttribute>
					<targetAttribute>temperature</targetAttribute>
				</attributeAssociation>
			</attributeAssociationList>
			<aggregationType>
				AVG
			</aggregationType>
		</sourceData>
	</value>
```

The value of the *sourceInformation* metadata specifies a *sourceType* and a *sourceData*. The source type specifies which kind of entity source is described in the sourceData. The source type that will be interpreted by the entity composer plugin is *org.fiware.type.sourceinformation.aggregation*. Any other source information will be ignored by this particular plugin.

When the source type is *org.fiware.type.sourceinformation.aggregation* (like in this example), the sourceData contains the entity composition rule:

 * The *targetEntity* field specifies the ID of the entity to be composed from other entities. 
 * The *sourceEntityList* contains the IDs of the entities from which the target entity is composed. Importantly, this list can also contain entity ID patterns.
 * The *attributeAssociationList* is an optional component. If it is present, then it contains a list of *attributeAssociations*, that is, mappings between source attributes and target attributes. In this particular example, there is one attribute association which maps the source attribute *sensorValue* to the target attribute *temperature*. This means that the *temperature* attribute of the target entity will be composed from all available *sensorValue* attributes of the source entities.
 * If the *attributeAssociationList* is not present, then each attribute of the source entities will be mapped to an attribute of the target entity having the same attribute name.
 * The *aggregationType* field describes which function is used to compute for each attribute association the target value from the source values. The supported aggregation types are
  * SUM (sum),
  * AVG (average),
  * MAX (maximum),
  * MIN (minimum).

In the example shown above, the entity with ID *ConferenceRoom* is composed from source entities *Sensor_A* and *Sensor_B*. The attribute *temperature* of *ConferenceRoom* will be obtained from the average of the values of the *sensorValue* attribute of the two source entities.

Importantly, the plugin does not expect all information about all source entities to be available. If, in this example, information about *Sensor_A* is inavailable, then the *temperature* of target entity *ConferenceRoom* will be reported as the temperature of *Sensor_B*.

Subscription and update operations
---

The entity composition is currently only available for query operations. It will be implemented for subscriptions and updates in a future IoT Broker release.