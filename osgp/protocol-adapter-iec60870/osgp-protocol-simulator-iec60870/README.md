# IEC60870-5-104 Simulator

This simulator can be used for testing GXF against devices using the IEC60870-5-104 protocol.

## Device types

Spring profiles are used to configure the device type to be used.  
The simulator currently supports two types of devices:
* Default controlled station
* Light measurement device

## ASDU generation

For manual testing the `job.asdu.generator.enabled` property can be set to `true` to enable ASDU generation.  
The frequency of ASDU generation can be configured using the `job.asdu.generator.cron` property.

For light measurement events the ASDU generator will randomly pick one of the available information object addresses and flip its value.


## Grid eXchange Fabric information and news

High-level project information and news can be found on the Grid eXchange Fabric website:
* [www.lfenergy.org/projects/gxf/](https://www.lfenergy.org/projects/gxf/)

Grid eXchange Fabric detailed documentation:
* [documentation.gxf.lfenergy.org](https://documentation.gxf.lfenergy.org/)

Grid eXchange Fabric issue tracker:
* [github.com/OSGP/Documentation/issues](https://github.com/OSGP/Documentation/issues)

