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
            <ns:Device_Identification>TST-01</ns:Device_Identification>
            <ns:protocolName>MQTT</ns:protocolName>
            <ns:protocolVersion>3</ns:protocolVersion>
            <ns:Network_Address>127.0.0.1</ns:Network_Address> 
         </ns:RtuDevice>
         <ns:DeviceModel>
            <ns:Manufacturer>Test</ns:Manufacturer>
            <ns:ModelCode>Test</ns:ModelCode>
         </ns:DeviceModel>
      </ns:AddRtuDeviceRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

The RtuDevice will be inserted in the osgp-core database and picked up by the communication monitoring task.