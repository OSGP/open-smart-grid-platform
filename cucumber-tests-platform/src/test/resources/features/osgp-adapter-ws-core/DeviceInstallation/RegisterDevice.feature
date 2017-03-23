Feature: CoreDeviceInstallation Device Registration
  As a ...
  I want to be able to perform DeviceInstallation operations on a device
  In order to ...

  #@OslpMockServer @Skip
  #Scenario Outline: A not register device Performs First Time Registration
  #When the device sends a register device request to the platform
  #| DeviceIdentification | <DeviceIdentification> |
  #| Protocol             | <Protocol>             |
  #Then the register device response contains
  #| FaultCode      | SOAP-ENV:Server                                                   |
  #| FaultString    | NETWORK_IN_USE                                                    |
  #| FaultType      | FunctionalFault                                                   |
  #| Code           |                                                               204 |
  #| Message        | NETWORK_IN_USE                                                    |
  #| Component      | WS_CORE                                                           |
  #| InnerException | com.alliander.osgp.domain.core.exceptions.ExistingEntityException |
  #| InnerMessage   | Network address <NetworkAddress> already used by another device.  |
  #
  #Examples:
  #| Protocol    | DeviceUid  | DeviceIdentification | DeviceType | GpsLatitude | GpsLongitude | NetworkAddress | CurrentTime | TimeZone | Result |
  #| OSLP        | 1234567890 | TEST1024000000001    |            |           0 |            0 | 0.0.0.0        |             |          | OK     |
  #| OSLP ELSTER | 1234567890 | TEST1024000000001    |            |           0 |            0 | 0.0.0.0        |             |          | OK     |
  
  #@OslpMockServer
  #Scenario Outline: A device which is installed and performs first time registration.
    #Given an ssld oslp device
      #| DeviceIdentification | TEST1024000000001 |
      #| Protocol             | <Protocol>        |
    #When the device sends a register device request to the platform
      #| DeviceIdentification | TEST1024000000001 |
      #| Protocol             | <Protocol>        |
    #Then the register device response contains
      #| Status | OK |
#
    #Examples: 
      #| Protocol    |
      #| OSLP        |
      #| OSLP ELSTER |
#
  #@OslpMockServer
  #Scenario Outline: A device which performs subsequent registration.
    #Given an ssld oslp device
      #| DeviceIdentification | TEST1024000000001 |
      #| Protocol             | <Protocol>        |
    #And the device sends a register device request to the platform
      #| DeviceIdentification | TEST1024000000001 |
    #When the device sends a register device request to the platform
      #| DeviceIdentification | TEST1024000000001 |
      #| DeviceUid            | eHW0eEFzN0R2Okd5  |
      #| IpAddress            | 127.0.0.2         |
      #| DeviceType           | SSLD              |
    #Then the register device response contains
      #| Status | OK |
#
    #Examples: 
      #| Protocol    |
      #| OSLP        |
      #| OSLP ELSTER |
#
  #@OslpMockServer
  #Scenario Outline: Register device that already exists on the platform, without GPS metadata
    #Given an ssld oslp device
      #| DeviceIdentification | TEST1024000000001 |
      #| Protocol             | <Protocol>        |
      #| gpsLatitude          |                   |
      #| gpsLongitude         |                   |
    #And the device sends a register device request to the platform
      #| DeviceIdentification | TEST1024000000001 |
    #When the device sends a register device request to the platform
      #| DeviceIdentification | TEST1024000000001 |
      #| DeviceUid            | eHW0eEFzN0R2Okd5  |
      #| IpAddress            | 127.0.0.2         |
      #| DeviceType           | SSLD              |
    #Then the register device response contains
      #| Status | OK |
#
    #Examples: 
      #| Protocol    |
      #| OSLP        |
      #| OSLP ELSTER |
      
      #@OslpMockServer
      #Scenario Outline: Register device that does not yet exist on the platform
      #When the device sends a register device request to the platform
      #| DeviceIdentification | <DeviceIdentification> |
      #Then the register device response contains
      #| Status | OK |
      #
      #Examples: 
      #| DeviceIdentification    |
      #|TEST1024000000002|
      
      @OslpMockServer
      Scenario Outline: Register device with network address already in use by another device
      Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    And the device sends a register device request to the platform
      | DeviceIdentification | TEST1024000000001 |
      And an ssld oslp device
      | DeviceIdentification | TEST1024000000002 |
      | Protocol             | <Protocol>        |
      When the device sends a register device request to the platform
      | DeviceIdentification | TEST1024000000002 |
      And the device sends a register device request to the platform
      | DeviceIdentification | TEST1024000000001 |
      | DeviceUid            | eHW0eEFzN0R2Okd5  |
      | IpAddress            | 127.0.0.2         |
      | DeviceType           | SSLD              |
      
      
            Examples: 
      | Protocol    |
      |OSLP|
      |OSLP ELSTAR|
