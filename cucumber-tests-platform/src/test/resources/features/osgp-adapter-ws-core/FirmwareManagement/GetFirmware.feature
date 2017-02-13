Feature: FirmwareManagement get firmware
  As OSGP 
  I want to manage the firmware of a device
  In order to ...

  # Note: All devices return multiple firmwares. How to solve this?
  @OslpMockServer
  Scenario Outline: Get firmware version
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Status               | Active            |
      | Organization         | TestOrganization  |
      | IsActivated          | true              |
    And the device returns firmware version "<Firmware Version>" over OSLP
    When receiving a get firmware version request
      | DeviceIdentification | TEST1024000000001 |
    Then the get firmware version async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a get firmware version OSLP message is sent to device "TEST1024000000001"
    And the platform buffers a get firmware version response message for device "TEST1024000000001"
      | Result             | OK                 |
      | FirmwareVersion    | <Firmware Version> |
      | FirmwareModuleType | FUNCTIONAL         |

    Examples: 
      | Firmware Version |
      | R01              |
      | R02              |
      |                  |
      |             0123 |

  Scenario: Get the firmware version for an unknown device
    When receiving a get firmware version request
      | DeviceIdentification | TEST1024000000001 |
    Then the get firmware version response contains soap fault
      | FaultCode | SOAP-ENV:Server |
      | Message   | UNKNOWN_DEVICE  |

  Scenario: Get the firmware version for an unregistered device
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | PublicKeyPresent     | false             |
    When receiving a get firmware version request
      | DeviceIdentification | TEST1024000000001 |
    Then the get firmware version response contains soap fault
      | FaultCode | SOAP-ENV:Server     |
      | Message   | UNREGISTERED_DEVICE |
