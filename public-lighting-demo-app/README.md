<!--
SPDX-FileCopyrightText: Contributors to the GXF project

SPDX-License-Identifier: Apache-2.0
-->

## Web Demo App

### Component Description

This repository contains a simple demo app for the Open Smart Grid Platform.
The demo app serves as a SOAP Client for the Platform's Web Services. It demonstrates how to create a Web Request, and send it to the Platform using a signed request over HTTPS.

### Functionality
The demo app currently supports the following web requests:
- UpdateKey request
- FindAllDevices request
- GetStatus request
- SetLight request + Async response

Through it's front end it allows the user to easily add a device, see which devices are registered on the platform and switch/set a light.
See the [Installation Manual](https://documentation.gxf.lfenergy.org/Userguide/Installation/Installationguide.html) for more information.

### Stack
The demo app is using the following frameworks/technologies

- Spring (ws, core, oxm, web, mvc)
- Orika Mapper
- Apache HTTP Client
- JSP

### Installation
You can clone this repository anywhere you want. Remember that it is required to have the Open Smart Grid Platform running on the local environment.
Once cloned, run ```mvn clean install```, and add the app to your Tomcat server.

Also make sure that the following information is added to vhost.conf:

Under ```<VirtualHost>```
```xml
    Redirect permanent /web-demo-app https://localhost/web-demo-app
```

Under ```<IfModule mod_proxy_ajp.c>```
```xml
            ProxyPass /web-demo-app ajp://localhost:8009/web-demo-app
            ProxyPassReverse /web-demo-app /web-demo-app
```

## Grid eXchange Fabric information and news

High-level project information and news can be found on the Grid eXchange Fabric website:
* [www.lfenergy.org/projects/gxf](https://www.lfenergy.org/projects/gxf/)

Grid eXchange Fabric detailed documentation:
* [documentation.gxf.lfenergy.org](https://documentation.gxf.lfenergy.org/)

Grid eXchange Fabric issue tracker:
* [github.com/OSGP/Documentation/issues](https://github.com/OSGP/Documentation/issues)

