Changelog
===========


Release 6.4
---


* New Feature: Store spare notifications: if is arriving at the IoT Broker a notification related to a subscription not initiated by the IoT Broker, the data in the notification is stored historically

* New Feature: Dispatch spare notification to active subscription: if is arriving at the IoT Broker a notification related to a subscription not initiated by the IoT Broker, the data in the notification is checked against active subscription and the interested subscriber notified.

* Historical Agent 
    * Registry: a component that gets as input ContextElement and issues NGSI-9 Registrations in case that the Entity associated to such ContextElement was not yet appeared before or has changed (e.g. Domain Metadata has changed).
    * Optimized connection to CouchDB 
    * Added cache

* Miscellanea  
    * Correct handling of strange character into messages
    * Correct AttributeExpression behaviour
    * Enabled correct restoring of subscription

* Docker:
    * Docker can be configured to use external databases (for permanent storage)

* NGSI-v1.Orion version:
    * Support to QueryContext
    * Support to NotifyContextAvailability
    * Support to SubscribeContext
    * Support notifications to NGSI-v1.Orion interfaces
