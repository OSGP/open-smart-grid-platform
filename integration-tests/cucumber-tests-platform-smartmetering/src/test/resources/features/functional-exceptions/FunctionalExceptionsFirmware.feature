# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @NightlyBuildOnly
Feature: SmartMetering functional exceptions regarding firmware

  Scenario: firmware upgrade with unknown firmware
    Given a manufacturer
      | ManufacturerCode | KAI   |
      | ManufacturerName | Kaifa |
    And a device model
      | ManufacturerName | Kaifa |
      | ModelCode        | MA105 |
    And a dlms device
      | DeviceIdentification | TEST1024000000002 |
      | DeviceType           | SMART_METER_E     |
      | ManufacturerCode     | KAI               |
      | DeviceModelCode      | MA105             |
    And a firmware
      | FirmwareFileIdentification  | TEST_FW_FILE_000X                        |
      | FirmwareFile                | 57696520646974206c656573742069732067656b |
      | FirmwareModuleVersionComm   | x                                        |
      | FirmwareModuleVersionMa     | y                                        |
      | FirmwareModuleVersionFunc   | z                                        |
      | FirmwareModuleVersionMbus   |                                          |
      | FirmwareModuleVersionSec    |                                          |
      | FirmwareFilename            | KFPP_V060100FF.bin                       |
      | ModelCode                   | MA105                                    |
      | FirmwareFileImageIdentifier | 496d6167654964656e746966696572           |
      | FirmwareIsForSmartMeters    | true                                     |
    When the request for a firmware upgrade is received
      | DeviceIdentification        | TEST1024000000002  |
      | FirmwareFileIdentification  | TEST_FW_FILE_0001  |
    Then retrieving the update firmware response results in an exception
    And a SOAP fault should have been returned
      | Code    |              701 |
      | Message | UNKNOWN_FIRMWARE |

  Scenario: upgrade firmware fails for a device without a device model
    Given a manufacturer
      | ManufacturerCode | KAI   |
      | ManufacturerName | Kaifa |
    And a dlms device
      | DeviceIdentification | TEST1024000000002 |
      | DeviceType           | SMART_METER_E     |
      | ManufacturerCode     | KAI               |
    And a firmware
      | FirmwareFileIdentification  | TEST_FW_FILE_0001                        |
      | FirmwareFile                | 57696520646974206c656573742069732067656b |
      | FirmwareModuleVersionComm   | Telit 10.00.154                          |
      | FirmwareModuleVersionMa     | BL_012 XMX_N42_GprsV09                   |
      | FirmwareModuleVersionFunc   | M57 4836                                 |
      | FirmwareModuleVersionMbus   |                                          |
      | FirmwareModuleVersionSec    |                                          |
      | FirmwareFilename            | KFPP_V060100FF.bin                       |
      | ModelCode                   | MA105                                    |
      | FirmwareFileImageIdentifier | 496d6167654964656e746966696572           |
      | FirmwareIsForSmartMeters    | true                                     |
    When the request for a firmware upgrade is received
      | DeviceIdentification        | TEST1024000000002     |
      | FirmwareFileIdentification  | TEST_FW_FILE_0001     |
    Then retrieving the update firmware response results in an exception
    And a SOAP fault should have been returned
      | Code    |                 601 |
      | Message | UNKNOWN_DEVICEMODEL |
