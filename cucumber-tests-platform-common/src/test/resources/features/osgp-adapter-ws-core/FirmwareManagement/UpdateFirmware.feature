@PublicLighting @Platform @FirmwareManagement
Feature: FirmwareManagement update firmware
  As OSGP 
  I want to manage the firmware of a device
  In order to ...

  Scenario: Update firmware
    Given a device
      | DeviceIdentification | TEST1024000000001 |
    And a firmware
      | DeviceIdentification     | TEST1024000000001 |
      | FirmwareFilename         | OldFirmware       |
      | FirmwarePushToNewDevices | true              |
      | Manufacturer             | Test              |
      | ModelCode                | TestModel         |
      | Description              |                   |
    When receiving an update firmware request
      | FirmwareFilename         | NewFirmware          |
      | FirmwarePushToNewDevices | false                |
      | Manufacturer             | Test                 |
      | ModelCode                | TestModel            |
      | FirmwareDescription      | Firmware is changed! |
    Then the update firmware response contains
      | Result | OK |

  Scenario Outline: Update firmware with an unknown or empty devicemodel
    Given a device
      | DeviceIdentification | TEST1024000000001 |
    And a firmware
      | DeviceIdentification     | TEST1024000000001 |
      | FirmwareFilename         | Firmware          |
      | FirmwarePushToNewDevices | true              |
      | Manufacturer             | Test              |
      | ModelCode                | TestModel         |
      | Description              |                   |
    When receiving an update firmware request
      | ModelCode | unknown |
    Then the update firmware response contains soap fault
      | FaultCode    | SOAP-ENV:Server                                   |
      | FaultString  | UNKNOWN_DEVICEMODEL                               |
      | InnerMessage | DeviceModel with id "unknown" could not be found. |

    Examples: 
      | ModelCode |
      | unknown   |
      |           |

  Scenario: Update the firmware for an unknown firmware
    When receiving an update firmware request
      | FirmwareFilename | Firmware |
    Then the update firmware response contains soap fault
      | FaultCode | SOAP-ENV:Server  |
      | Message   | UNKNOWN_FIRMWARE |
