# README #

Maven build:
```
mvn clean install
```
The build generates a .war file which can be deployed in Tomcat.
See `context.xml` for the property file paths.

The simulator can also be run by starting org.opensmartgridplatform.simulator.protocol.mqtt.Simulator 

It is also possible to run this module as a Docker instance, see below.

Create Docker image
```
mkdir target/dependency
cd target/dependency
jar -xf ../*.jar
cd ../..
mvn com.google.cloud.tools:jib-maven-plugin:dockerBuild -Dimage=osgp-protocol-simulator-mqtt
```

Push Docker image to registry
```
mvn com.google.cloud.tools:jib-maven-plugin:build -Dimage=osgp-protocol-simulator-mqtt
```

Check the image:
```
docker image inspect osgp-protocol-simulator-mqtt
```

Run it
```
docker run -p 8883:8883 -t osgp-protocol-simulator-mqtt
```

Tail the logs:
```
docker logs -f CONTAINER
```

## Enable device communication monitoring

Enable the device communication monitoring task for osgp-adapter-domain-distributionautomation using this property:

```
communication.monitoring.enabled=true
```

## Add an RtuDevice

To add an RtuDevice call the AddRtuDevice SOAP endpoint from this WSDL

```
http://localhost:8080/osgp-adapter-ws-distributionautomation/distributionautomation/devicemanagement/DistributionAutomationDeviceManagement.wsdl
```

A test request could look like this:

```
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ns="http://www.opensmartgridplatform.org/schemas/distributionautomation/defs/2017/04">
   <soapenv:Header>
      <ns:ApplicationName>SoapUI</ns:ApplicationName>
      <ns:UserName>user</ns:UserName>
      <ns:OrganisationIdentification>test-org</ns:OrganisationIdentification>     
   </soapenv:Header>
   <soapenv:Body>
      <ns:AddRtuDeviceRequest>
         <ns:RtuDevice>
            <ns:DeviceIdentification>TST-01</ns:DeviceIdentification>
            <ns:ProtocolName>MQTT</ns:ProtocolName>
            <ns:ProtocolVersion>3.1.1</ns:ProtocolVersion>
            <ns:NetworkAddress>127.0.0.1</ns:NetworkAddress> 
         </ns:RtuDevice>
         <ns:DeviceModel>
            <ns:Manufacturer>TST</ns:Manufacturer>
            <ns:ModelCode>TSTMOD</ns:ModelCode>
         </ns:DeviceModel>
      </ns:AddRtuDeviceRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

The RtuDevice will be inserted in the osgp-core database and picked up by the communication monitoring task.

## Add MQTT RTU Device in the DB
Alternative way to add RTU device.

```
INSERT INTO public.protocol_info(
            creation_time, modification_time, version, protocol, protocol_version, 
            outgoing_requests_property_prefix, incoming_responses_property_prefix, 
            incoming_requests_property_prefix, outgoing_responses_property_prefix, 
            parallel_requests_allowed)
    VALUES (now(), now(), 0, 'MQTT', '3.1.1', 
    'jms.protocol.mqtt.outgoing.requests', 'jms.protocol.mqtt.incoming.responses',
    'jms.protocol.mqtt.incoming.requests', 'jms.protocol.mqtt.outgoing.responses',TRUE
    );
```
protocol_info.id is auto generated

```    
INSERT INTO public.device(
            creation_time, modification_time, version, device_identification, 
            device_type, is_activated,                 
            protocol_info_id, network_address,  
            in_maintenance, technical_installation_date, 
            device_model, device_lifecycle_status, integration_type)
    VALUES (now(), now(), 0, 'TST-01', 
	    'PSD', true, 
            (SELECT id FROM protocol_info WHERE protocol = 'MQTT'), '127.0.0.1', 
            false, now(), 
            (SELECT id FROM device_model WHERE model_code = 'TSTMOD'), 'IN_USE', 'KAFKA'); 
```

These queries depend on the dummy device scripts. 
It may be needed to delete TST-1 or rename it before inserting the device using the query above.

```
INSERT INTO public.rtu_device(
            id, last_communication_time, domain_info_id)
    VALUES ((SELECT id FROM device WHERE device_identification = 'TST-01'), now(), (SELECT id FROM domain_info WHERE domain = 'DISTRIBUTION_AUTOMATION'));

INSERT INTO public.device_authorization(
            creation_time, modification_time, version, function_group, 
            device, organisation)
    VALUES (now(), now(), 0, 0, (SELECT id FROM device WHERE device_identification = 'TST-01'), 
            (SELECT id FROM organisation WHERE organisation_identification = 'LianderNetManagement'));
```

Note: function_group 0 = OWNER

## Managing the Broker at Runtime
### Start/stop
To start/stop the broker (used e.g. in reconnection-scenario), `GET` these endpoints:

```shell
curl localhost:8080/osgp-protocol-simulator-mqtt/broker/start
curl localhost:8080/osgp-protocol-simulator-mqtt/broker/stop
```

### Client monitoring
You can get a list of connected clients:

```shell
curl localhost:8080/osgp-protocol-simulator-mqtt/broker/clients
```

Using [watch](https://man7.org/linux/man-pages/man1/watch.1.html) and [jq](https://stedolan.github.io/jq/tutorial/) you can monitor the clients:

```shell
watch -n1 "curl -s localhost:8080/osgp-protocol-simulator-mqtt/broker/clients|jq"
```
