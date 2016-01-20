# OSGP Components

License information: Apache 2.0. The code files on this master branch don't have a license header yet. Soon we will merge development branch into master branch. Then all code files will have a licence header.

### Build Status

[![Build Status](http://54.77.62.182/job/OSGP_Platform_development/badge/icon?style=plastic)](http://54.77.62.182/job/OSGP_Platform_development)

### Component Description

The OSGP components implement SOAP web services, domain logic and message routing to Protocol Adapters.

Web Service Layer

- osgp-adapter-ws-admin, Management functions web service
- ospg-adapter-ws-core, Generic functions web service
- osgp-adapter-ws-public-lighting, Public Lighting functions web service
- osgp-adapter-ws-tariff-switching, Tariff Switching function web service
- osgp-adapter-ws-smart-metering, Smart Metering function web service
- osgp-adapter-ws-shared, Common classes used by these components

Domain Layer

- osgp-adapter-domain-admin, Management functions domain logic
- osgp-adapter-domain-core, Generic functions domain logic
- osgp-adapter-domain-public-lighting, Public Lighting functions domain logic
- osgp-adapter-domain-tariff-switching, Tariff Switching functions domain logic
- osgp-adapter-domain-smart-metering, Smart Metering functions domain logic
- osgp-adapter-domain-shared, Common classes used by these components
- osgp-domain-core, Generic domain classes
- osgp-domain-public-lighting, Public Lighting domain classes
- osgp-domain-tariff-switching, Tariff Switching domain classes
- osgp-logging, Logging incoming requests and outgoing responses of the
  Web Service Layer and logging calls to and from devices

Message Routing Layer

- osgp-core, Message Routing to Protocol Adapters
- osgp-core-db-api, Database Access Control for Protocol Adapters

The components have dependencies.

- shared, Common classes used by the OSGP Components
- osgp-dto, Data Transfer Objects
