# README #

Maven build:
```
mvn clean install
```
The build generates a .war file which can be deployed in Tomcat.
See `context.xml` for the property file paths.

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

## Add MQTT RTU Device in the DB

```
INSERT INTO public.protocol_info(
            creation_time, modification_time, version, protocol, protocol_version, 
            outgoing_requests_property_prefix, incoming_responses_property_prefix, 
            incoming_requests_property_prefix, outgoing_responses_property_prefix, 
            parallel_requests_allowed)
    VALUES ('2020-02-24 00:00:00', '2020-02-24 00:00:00', 0, 'MQTT', '3', 
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
            device_model, device_lifecycle_status)
    VALUES ('2020-01-01 12:34', '2020-01-01 12:34', 0, 'TST-1', 
	    'SSLD', true, 
            (SELECT id FROM protocol_info WHERE protocol = 'MQTT'), '127.0.0.1', 
            false, '2020-01-01 12:34', 
            (SELECT id FROM device_model WHERE model_code = 'TSTMOD'), 'IN_USE'); 
```

These queries depend on the dummy device scripts. 
It may be needed to delete TST-1 or rename it before inserting the device using the query above.

```
INSERT INTO public.rtu_device(
            id, last_communication_time)
    VALUES ((SELECT id FROM device WHERE device_identification = 'TST-1'), '2020-01-24 13:33:33.501');

INSERT INTO public.device_authorization(
            creation_time, modification_time, version, function_group, 
            device, organisation)
    VALUES ('2020-01-01 12:34', '2020-01-01 12:34', 0, 0, (SELECT id FROM device WHERE device_identification = 'TST-1'), 
            (SELECT id FROM organisation WHERE organisation_identification = 'LianderNetManagement'));
```

Note: function_group 0 = OWNER 
