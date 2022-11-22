# OSGP Components

### Component Description

The OSGP components implement SOAP web services, domain logic and message routing to Protocol Adapters.

Web Service Layer

- osgp-adapter-ws-admin, Management functions web service
- ospg-adapter-ws-core, Generic functions web service
- osgp-adapter-ws-public-lighting, Public Lighting functions web service
- osgp-adapter-ws-tariff-switching, Tariff Switching function web service
- osgp-adapter-ws-smart-metering, Smart Metering function web service
- osgp-adapter-ws-microgrids, Microgrids function web service
- osgp-adapter-ws-distributionautomation, Distribution Automation function web service
- osgp-adapter-ws-shared, Common classes used by these components

Domain Layer

- osgp-adapter-domain-admin, Management functions domain logic
- osgp-adapter-domain-core, Generic functions domain logic
- osgp-adapter-domain-distributionautomation, Distribution Automation functions domain logic
- osgp-adapter-domain-microgrids, Microgrids functions domain logic
- osgp-adapter-domain-public-lighting, Public Lighting functions domain logic
- osgp-adapter-domain-tariff-switching, Tariff Switching functions domain logic
- osgp-adapter-domain-smart-metering, Smart Metering functions domain logic
- osgp-adapter-domain-shared, Common classes used by these components
- osgp-domain-core, Generic domain classes
- osgp-domain-logging, Logging domain classes
- osgp-domain-distributionautomation, Distribution Automation domain classes
- osgp-domain-microgrids, Microgrids domain classes
- osgp-domain-smartmetering, smartmetering domain classes e.g.: device config objects 
- osgp-domain-tariff-switching, Tariff Switching domain classes
- osgp-logging, Logging incoming requests and outgoing responses of the
  Web Service Layer and logging calls to and from devices

Message Routing Layer

- osgp-core, Message Routing to Protocol Adapters
- osgp-core-db-api, Database Access Control for Protocol Adapters

The components have dependencies.

- shared, Common classes used by the OSGP Components
- osgp-dto, Data Transfer Objects

## Grid eXchange Fabric information and news

High-level project information and news can be found on the Grid eXchange Fabric website:
* [www.lfenergy.org/projects/gxf/](https://www.lfenergy.org/projects/gxf/)

Grid eXchange Fabric detailed documentation:
* [documentation.gxf.lfenergy.org](https://documentation.gxf.lfenergy.org/)

Grid eXchange Fabric issue tracker:
* [github.com/OSGP/Documentation/issues](https://github.com/OSGP/Documentation/issues)

Built using Java 8.

