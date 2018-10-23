@SmartMetering @Platform
Feature: SmartMetering functional exceptions regarding firmware

  Scenario: firmware upgrade with unknown firmware
    Given a manufacturer
      | ManufacturerCode | KAIF  |
      | ManufacturerName | Kaifa |
    And a device model
      | ManufacturerName | Kaifa |
      | ModelCode        | MA105 |
    And a dlms device
      | DeviceIdentification | TEST1024000000002 |
      | DeviceType           | SMART_METER_E     |
      | ManufacturerCode     | KAIF              |
      | DeviceModelCode      | MA105             |
    And a firmware
      | FirmwareModuleVersionComm | x                  |
      | FirmwareModuleVersionMa   | y                  |
      | FirmwareModuleVersionFunc | z                  |
      | FirmwareModuleVersionMbus |                    |
      | FirmwareModuleVersionSec  |                    |
      | FirmwareFilename          | KFPP_V060100FF.bin |
      | ModelCode                 | MA105              |
      | FirmwareIsForSmartMeters  | true               |
    When the request for a firmware upgrade is received
      | DeviceIdentification      | TEST1024000000002      |
      | FirmwareModuleVersionComm | Telit 10.00.154        |
      | FirmwareModuleVersionMa   | BL_012 XMX_N42_GprsV09 |
      | FirmwareModuleVersionFunc | M57 4836               |
    Then retrieving the update firmware response results in an exception
    And a SOAP fault should have been returned
      | Code    |              701 |
      | Message | UNKNOWN_FIRMWARE |

  Scenario: upgrade firmware fails for a device without a device model
    Given a manufacturer
      | ManufacturerCode | KAIF  |
      | ManufacturerName | Kaifa |
    And a dlms device
      | DeviceIdentification | TEST1024000000002 |
      | DeviceType           | SMART_METER_E     |
      | ManufacturerCode     | KAIF              |
    And a firmware
      | FirmwareModuleVersionComm | Telit 10.00.154        |
      | FirmwareModuleVersionMa   | BL_012 XMX_N42_GprsV09 |
      | FirmwareModuleVersionFunc | M57 4836               |
      | FirmwareModuleVersionMbus |                        |
      | FirmwareModuleVersionSec  |                        |
      | FirmwareFilename          | KFPP_V060100FF.bin     |
      | ModelCode                 | MA105                  |
      | FirmwareIsForSmartMeters  | true                   |
    When the request for a firmware upgrade is received
      | DeviceIdentification      | TEST1024000000002      |
      | FirmwareModuleVersionComm | Telit 10.00.154        |
      | FirmwareModuleVersionMa   | BL_012 XMX_N42_GprsV09 |
      | FirmwareModuleVersionFunc | M57 4836               |
    Then retrieving the update firmware response results in an exception
    And a SOAP fault should have been returned
      | Code    |                 601 |
      | Message | UNKNOWN_DEVICEMODEL |
