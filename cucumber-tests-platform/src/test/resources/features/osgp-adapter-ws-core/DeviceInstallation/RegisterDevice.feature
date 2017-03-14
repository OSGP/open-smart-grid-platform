Feature: CoreDeviceInstallation Device Registration
  As a ...
  I want to be able to perform DeviceInstallation operations on a device
  In order to ...

  @OslpMockServer @Skip
  Scenario Outline: A Device Performs First Time Registration
    Given an ssld oslp device
      | DeviceIdentification | <DeviceIdentification> |
      | Protocol             | <Protocol>             |
    When the device sends a register device request to the platform
      | DeviceIdentification | <DeviceIdentification> |
      | Protocol             | <Protocol>             |
    Then the device receives a register device response
      | Status         | <Status>         |
      | CurrentTime    | <CurrentTime>    |
      | RandomDevice   | <RandomDevice>   |
      | RandomPlatform | <RandomPlatform> |
      | TimeOffset     | <TimeOffset>     |
      | Latitude       | <Latitude>       |
      | Longitude      | <Longitude>      |

    Examples: 
      | Protocol    | DeviceUid  | DeviceIdentification | DeviceType | GpsLatitude | GpsLongitude | CurrentTime | TimeZone |
      | OSLP        | 1234567890 | TEST1024000000001    |            |           0 |            0 |             |          |
      | OSLP ELSTER | 1234567890 | TEST1024000000001    |            |           0 |            0 |             |          |

	@OslpMockServer @Skip
  Scenario Outline: A not registered device Performs First Time Registration
    When the device sends a register device request to the platform
      | DeviceIdentification | <DeviceIdentification> |
      | Protocol             | <Protocol>             |
    Then the device receives a register device response
      | FaultCode      | SOAP-ENV:Server                                                   |
      | FaultString    | NETWORK_IN_USE                                                    |
      | FaultType      | FunctionalFault                                                   |
      | Code           |                                                               204 |
      | Message        | NETWORK_IN_USE                                                    |
      | Component      | WS_CORE                                                           |
      | InnerException | com.alliander.osgp.domain.core.exceptions.ExistingEntityException |
      | InnerMessage   | Network address <NetworkAddress> already used by another device.  |

    Examples: 
      | Protocol    | DeviceUid  | DeviceIdentification | DeviceType | GpsLatitude | GpsLongitude | NetworkAddress | CurrentTime | TimeZone | Result |
      | OSLP        | 1234567890 | TEST1024000000001    |            |           0 |            0 | 0.0.0.0        |             |          | OK     |
      | OSLP ELSTER | 1234567890 | TEST1024000000001    |            |           0 |            0 | 0.0.0.0        |             |          | OK     |
