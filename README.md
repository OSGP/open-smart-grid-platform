# OSGP Components
---

The OSGP components implement SOAP web services, domain logic and message routing to Protocol Adapters.

Web Service Layer

- osgp-adapter-ws-admin, Management functions web service
- ospg-adapter-ws-core, Generic functions web service
- osgp-adapter-ws-public-lighting, Public Lighting functions web service
- osgp-adapter-ws-tariff-switching, Tariff Switching function web service
- osgp-adapter-ws-shared, Common classes used by these components

Domain Layer

- osgp-adapter-domain-admin, Management functions domain logic
- osgp-adapter-domain-core, Generic functions domain logic
- osgp-adapter-domain-public-lighting, Public Lighting functions domain logic
- osgp-adapter-domain-tariff-switching, Tariff Switching functions domain logic
- osgp-adapter-domain-shared, Common classes used by these components
- osgp-domain-core, Generic domain classes
- osgp-domain-public-lighting, Public Lighting domain classes
- osgp-domain-tariff-switching, Tariff Switching domain classes
- web-service-logging, Logging incoming request and outgoing response of Web Service Layer

Message Routing Layer

- osgp-core, Message Routing to Protocol Adapters
- osgp-core-db-api, Database Access Control for Protocol Adapters

The components have dependencies.

- shared, Common classes used by the OSGP Components
- osgp-dto, Data Transfer Objects