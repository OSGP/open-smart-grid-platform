# Protocol Adapter for Open Street Light Protocol

### Component Description

These components offer an implementation of OSLP. There's the Protocol Adapter that can map a domain message to a OSLP message and send it to a SSLD/PSLD smart device. For development and testing there's the Device Simulator that has similar interface and behaviour as SSLD/PSLD smart devices.

- oslp, Implementation of OSLP
- osgp-adapter-protocol-oslp, Protocol Adapter
- osgp-core-db-api, Database access provider
- signing-server, OSLP message signing provider
- web-device-simulator, OSLP device simulator

The components have dependencies.

- shared, Common classes used by the Protocol Adapter and Device Simulator
- osgp-dto, Data Transfer Objects

## Grid eXchange Fabric information and news

High-level project information and news can be found on the Grid eXchange Fabric website:
* [www.lfenergy.org/projects/gxf/](https://www.lfenergy.org/projects/gxf/)

Grid eXchange Fabric detailed documentation:
* [documentation.gxf.lfenergy.org](https://documentation.gxf.lfenergy.org)

Grid eXchange Fabric issue tracker)
* [Grid eXchange Fabric Jira](https://smartsocietyservices.atlassian.net/projects/OC/issues/)
