# IEC61850 Simulator

This simulator can be used for testing GXF against devices using the IEC61850 protocol.  
It can be ran as a standalone spring boot application or together with other GXF components in Tomcat.

#### Device Types
Spring profiles are used to configure the device type to be used.  
The simulator currently supports the following device types:
* Light measurement rtu
* Microgrids rtu
* Distribution automation rtu

#### Running as standalone application
When running standalone, for example as a light measurement rtu, the following parameters should be supplied:

`--spring.config.name=simulator-iec61850`
`--spring.profiles.active=light-measurement-rtu,default`  

or when using external configuration:  

`--spring.config.name=simulator-iec61850`  
`--spring.config.additional-location=optional:/etc/osp/`  

in which case the active profiles could be set in `simulator-iec61850.properties` in the additional location.

#### Running in Tomcat
When running in Tomcat parameters could be configured in META-INF/context.xml

`<Parameter name="spring.config.name" value="simulator-iec61850" />`  
`<Parameter name="spring.profiles.active" value="light-measurement-rtu,default" />`  

or when using external configuration:  

`<Parameter name="spring.config.name" value="simulator-iec61850" />`  
`<Parameter name="spring.config.additional-location" value="optional:/etc/osp/" />`  

in which case the active profiles could be set in `simulator-iec61850.properties` in the additional location.


## Grid eXchange Fabric information and news

High-level project information and news can be found on the Grid eXchange Fabric website:
* [www.lfenergy.org/projects/gxf/](https://www.lfenergy.org/projects/gxf/)

Grid eXchange Fabric detailed documentation:
* [documentation.gxf.lfenergy.org](https://documentation.gxf.lfenergy.org/)

Grid eXchange Fabric issue tracker:
* [github.com/OSGP/Documentation/issues](https://github.com/OSGP/Documentation/issues)

