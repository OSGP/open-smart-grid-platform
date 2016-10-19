# OSGP Components

### Build Status

[![Build Status](http://ci.opensmartgridplatform.org/job/OSGP_Platform_development/badge/icon?style=plastic)](http://ci.opensmartgridplatform.org/job/OSGP_Platform_development)

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

## Open smart grid platform information and news

High-level project information and news can be found on the open smart grid platform website: 
* [www.opensmartgridplatform.org](http://opensmartgridplatform.org/)

Open smart grid platform detailed documentation:
* [documentation.opensmartgridplatform.org/](http://documentation.opensmartgridplatform.org/)

Open smart grid platform issue tracker:
* [Open smart grid platform Jira](https://smartsocietyservices.atlassian.net/projects/OC/issues/)
