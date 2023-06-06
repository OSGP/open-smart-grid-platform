<!--
SPDX-FileCopyrightText: Contributors to the GXF project

SPDX-License-Identifier: Apache-2.0
-->

# How to start this application

- this application can be used as stand-alone Spring Boot application:
```
sudo java -jar osgp-protocol-simulator-iec61850-4.29.0-SNAPSHOT.war --spring.config.location=file:osgp-protocol-simulator-iec61850.properties
```

- this application can be deployed in Apache Tomcat
- when deployed in Apache Tomcat, the context.xml file points to application configuration file at `/etc/osp/osgp-protocol-simulator-iec61850.properties`
- when deployed in Apache Tomcat, the context.xml file points to logging configuration file at `/etc/osp/osgp-simulator-protocol-iec61850-logback.xml`