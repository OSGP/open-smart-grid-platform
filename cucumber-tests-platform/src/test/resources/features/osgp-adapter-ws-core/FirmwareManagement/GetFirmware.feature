Feature: FirmwareManagement GetFirmware
  As OSGP 
  I want to manage the firmware of a device
  In order to ...

  # Note: All devices return multiple firmwares. How to solve this?
  @OslpMockServer
  Scenario Outline: Get firmware version
    Given an oslp device
      | DeviceIdentification | <DeviceIdentification> |
      | Status               | <Status>               |
      | Organization         | <Organization>         |
      | IsActivated          | <IsActivated>          |
    And the device returns firmware version "<Firmware Version>" over OSLP
    When receiving a get firmware version request
      | DeviceIdentification | <DeviceIdentification> |
    Then the get firmware version async response contains
      | DeviceIdentification | <DeviceIdentification> |
    And a get firmware version OSLP message is sent to device "<DeviceIdentification>"
    And the platform buffers a get firmware version response message for device "<DeviceIdentification>"
      | Result             | OK                     |
      | FirmwareVersion    | <Firmware Version>     |
      | FirmwareModuleType | <Firmware Module Type> |

    Examples: 
      | DeviceIdentification | Status | Organization      | IsActivated | Firmware Version | Firmware Module Type |
      | TEST1024000000001    | active | Test Organization | true        |             0123 | FUNCTIONAL           |
