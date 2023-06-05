<!--
SPDX-FileCopyrightText: 2023 Contributors to the GXF project

SPDX-License-Identifier: Apache-2.0
-->

# Protocol Adapter MQTT 

To run a demonstration follow the steps below.

Build the following modules:

* osgp-protocol-simulator-mqtt
* osgp-logging
* osgp-adapter-domain-distributionautomation
* osgp-adapter-ws-distributionautomation
* osgp_adapter-domain-core
* osgp-core
* osgp-protocol-adapter-mqtt

e.g. with `mvn clean install`.

Follow the README of `osgp-protocol-simulator-mqtt`. 

The simulator will repeatedly publish MQTT messages as specified in its `mqtt_simulator_spec.json` file.

Deploy the war files in Tomcat and start Tomcat. 

`osgp-adapter-domain-distributionautomation` will detect the RTU device in the database and send
a GET_DATA request to establish communication. 

Make sure `osgp-adapter-domain-distributionautomation.properties` contains `communication.monitoring.enabled=true`

The protocol adapter will connect with the simulator (which runs an MQTT broker) on the IP address specified
in the RTU device, using the default port, see `osgp-adapter-protocol-mqtt.properties`:

```
# MQTT
mqtt.broker.defaultPort=8883
mqtt.broker.defaultTopics=+/measurement,+/congestion
mqtt.broker.defaultQos=AT_LEAST_ONCE 
```

After successfully connecting, the protocol adapter will subscribe to the default topics. A subscription 
is made to each of the topics separated by a comma, using the defauilt QoS value. 
(See `com.hivemq.client.mqtt.datatypes.MqttQos`).

If not yet present, an MqttDevice is saved in the database of the protocol adapter with the values used. 
If the MqttDevice is updated in the database, the updated values will be used for subsequent communication.


