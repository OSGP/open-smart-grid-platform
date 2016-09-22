Feature: Firmware management
  As OSGP 
  I want to manage the firmware of a device
  In order to ...

  # Note: All devices return multiple firmwares. How to solve this?
  @OslpMockServer
  Scenario Outline: Get firmware version
    Given an oslp device
      | DeviceIdentification | D01               |
      | Status               | Active            |
      | Organization         | Test Organization |
      | IsActivated          | True              |
    And the device returns firmware version "<Firmware Version>" over OSLP
    When receiving a get firmware version request
      | DeviceIdentification | D01 |
    Then the get firmware version async response contains
      | DeviceIdentification | D01 |
    And a get firmware version OSLP message is sent to device "D01"
    And the platform buffers a get firmware version response message for device "D01"
      | FirmwareVersion    | <Firmware Version>     |
      | FirmwareModuleType | <Firmware Module Type> |

    Examples: 
      | Firmware Version | Firmware Module Type |
      | 0123             | FUNCTIONAL           |
