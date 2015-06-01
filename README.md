# Protocol Adapter for Open Street Light Protocol

License information: Apache 2.0. The code files on this master branch don't have a license header yet. Soon we will merge development branch into master branch. Then all code files will have a licence header.

### Build Status

[![Build Status](http://54.77.62.182/job/OSGP_Protocol-Adapter-OSLP_master/badge/icon?style=plastic)](http://54.77.62.182/job/OSGP_Protocol-Adapter-OSLP_master)

### Component Description

These components offer an implementation of OSLP. There's the Protocol Adapter that can map a domain message to a OSLP message and send it to a SSLD/PSLD smart device. For development and testing there's the Device Simulator that has similar interface and behaviour as SSLD/PSLD smart devices.

- oslp, Implementation of OSLP
- osgp-adapter-protocol-oslp, Protocol Adapter
- osgp-core-db-api, Database access provider
- web-device-simulator, OSLP device simulator

The components have dependencies.

- shared, Common classes used by the Protocol Adapter and Device Simulator
- osgp-dto, Data Transfer Objects
