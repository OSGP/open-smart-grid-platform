# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@Common @Platform @FirmwareManagement
Feature: FirmwareManagement change firmware
  As OSGP 
  I want to change the firmware of a device
  In order to ...

  Scenario: Change firmware
    Given a device
      | DeviceIdentification | TEST1024000000001 |
    And a firmware
      | DeviceIdentification     | TEST1024000000001 |
      | FirmwareFilename         | OldFirmware       |
      | FirmwarePushToNewDevices | true              |
      | ManufacturerName         | Test              |
      | ModelCode                | TestModel         |
      | Description              |                   |
    When receiving an change firmware request
      | FirmwareFilename         | NewFirmware          |
      | FirmwarePushToNewDevices | false                |
      | ManufacturerName         | Test                 |
      | ModelCode                | TestModel            |
      | FirmwareDescription      | Firmware is changed! |
    Then the change firmware response contains
      | Result | OK |
    And the entity firmware exists
      | ModelCode                 | TestModel            |
      | FirmwareFilename          | NewFirmware          |
      | FirmwareDescription       | Firmware is changed! |

  Scenario Outline: Change firmware with an unknown or empty devicemodel
    Given a device
      | DeviceIdentification | TEST1024000000001 |
    And a firmware
      | DeviceIdentification     | TEST1024000000001 |
      | FirmwareFilename         | Firmware          |
      | FirmwarePushToNewDevices | true              |
      | ManufacturerName         | Test              |
      | ModelCode                | TestModel         |
      | Description              |                   |
    When receiving an change firmware request
      | ModelCode | unknown |
    Then the change firmware response contains soap fault
      | FaultCode    | SOAP-ENV:Server                                   |
      | FaultString  | UNKNOWN_DEVICEMODEL                               |
      | InnerMessage | DeviceModel with id "unknown" could not be found. |

    Examples: 
      | ModelCode |
      | unknown   |
      |           |

  Scenario: Change the firmware for an unknown firmware
    When receiving an change firmware request
      | FirmwareFilename | Firmware |
    Then the change firmware response contains soap fault
      | FaultCode | SOAP-ENV:Server  |
      | Message   | UNKNOWN_FIRMWARE |
