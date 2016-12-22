Feature: Register Device
  As a ...
  I want to ...
  In order to ...

  # RegisterDevices scenario's
  # Nieuwe classe? Hoe kan een device geregistreerd worden?
  #	Scenario Outline: A Device Performs First Time Registration
  #		Given a not registered device
  #			| DeviceIdentification | <DeviceIdentification> |
  #		And the device returns a register device response over OSLP
  #		When receiving a register device request
  #		Then the register device response contains
  #			| DeviceUid            | <DeviceUid>            |
  #| DeviceIdentification | <DeviceIdentification> |
  #| DeviceType           | <DeviceType>           |
  #| GpsLatitude          | <GpsLatitude>          |
  #| GpsLongitude         | <GpsLongitude>         |
  #| CurrentTime          | <CurrentTime>          |
  #| TimeZone             | <TimeZone>             |
  #
  #Examples:
  #	| DeviceUid  | DeviceIdentification | DeviceType | GpsLatitude | GpsLongitude | CurrentTime | TimeZone |
  #	| 1234567890 | TEST1024000000001    |            |           0 |            0 |             |          |
  #
  #  # Nieuwe classe? Hoe kan een device geregistreerd worden?
  #	Scenario Outline: A Device Performs First Time Registration
  #		Given a not registered device
  #			| DeviceUid            | <DeviceUid>            |
  #| DeviceIdentification | <DeviceIdentification> |
  #| DeviceType           | <DeviceType>           |
  #| GpsLatitude          | <GpsLatitude>          |
  #| GpsLongitude         | <GpsLongitude>         |
  #| NetworkAddress       | <NetworkAddress>       |
  #| CurrentTime          | <CurrentTime>          |
  #| TimeZone             | <TimeZone>             |
  #		And the device returns a register device response over OSLP
  #			| Result | <Result> |
  #		When receiving a register device request
  #		Then the register device response contains
  #			| FaultCode      | SOAP-ENV:Server                                                   |
  #| FaultString    | NETWORK_IN_USE                                                    |
  #| FaultType      | FunctionalFault                                                   |
  #| Code           |                                                               204 |
  #| Message        | NETWORK_IN_USE                                                    |
  #| Component      | WS_CORE                                                           |
  #| InnerException | com.alliander.osgp.domain.core.exceptions.ExistingEntityException |
  #| InnerMessage   | Network address <NetworkAddress> already used by another device.  |
  #
  #Examples:
  #	| DeviceUid  | DeviceIdentification | DeviceType | GpsLatitude | GpsLongitude | NetworkAddress | CurrentTime | TimeZone | Result |
  #	| 1234567890 | TEST1024000000001    |            |           0 |            0 | 0.0.0.0        |             |          | OK     |
