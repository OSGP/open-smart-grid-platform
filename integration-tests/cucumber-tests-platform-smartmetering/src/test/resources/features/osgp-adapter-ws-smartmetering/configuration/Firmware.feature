# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringConfiguration @SmartMeteringFirmware
Feature: SmartMetering Configuration - Firmware
  As a grid operator
  I want to be able to perform SmartMeteringFirmware operations on a device
  In order to ...

  @GetFirmwareVersion
  Scenario Outline: Get the firmware version from <protocol> <version> device
    Given a dlms device
      | DeviceIdentification      | <deviceidentification> |
      | DeviceType                | SMART_METER_E          |
      | Protocol                  | <protocol>             |
      | ProtocolVersion           | <version>              |
      | FirmwareModuleVersionComm | <comm0>                |
      | FirmwareModuleVersionMa   | <ma0>                  |
      | FirmwareModuleVersionFunc | <func0>                |
      | FirmwareModuleVersionMbda | <mbda0>                |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001      |
      | DeviceType                  | SMART_METER_G          |
      | GatewayDeviceIdentification | <deviceidentification> |
      | Channel                     |                      1 |
    When the get firmware version request is received
      | DeviceIdentification | <deviceidentification> |
    Then the firmware version result should be returned
      | DeviceIdentification      | <deviceidentification> |
      | FirmwareModuleVersionComm | <comm1>                |
      | FirmwareModuleVersionMa   | <ma1>                  |
      | FirmwareModuleVersionFunc | <func1>                |
      | FirmwareModuleVersionMbda | <mbda1>                |
    And the database should be updated with the device firmware version
      | DeviceIdentification      | <deviceidentification> |
      | FirmwareModuleVersionComm | <comm1>                |
      | FirmwareModuleVersionMa   | <ma1>                  |
      | FirmwareModuleVersionFunc | <func1>                |
      | FirmwareModuleVersionMbda | <mbda1>                |
      | FirmwareIsForSmartMeters  | true                   |
    Examples:
      | deviceidentification | protocol | version | comm0 | ma0   | func0 | mbda0 | comm1           | ma1                    | func1    | mbda1    |
      | TEST1024000000001    | DSMR     | 4.2.2   | V 1.1 | V 1.2 | V 1.3 |       | Telit 10.00.154 | BL_012 XMX_N42_GprsV09 | M57 4836 |          |
      | TEST1027000000001    | SMR      | 5.0.0   | V 1.1 | V 1.2 | V 1.3 | V 1.4 | Telit 10.00.154 | BL_012 XMX_N42_GprsV09 | M57 4836 | M00 0000 |
    @NightlyBuildOnly
    Examples:
      | deviceidentification | protocol | version | comm0 | ma0   | func0 | mbda0 | comm1           | ma1                    | func1    | mbda1    |
      | TEST1024000000001    | DSMR     | 2.2     | V 1.1 | V 1.2 | V 1.3 |       | Telit 10.00.154 | BL_012 XMX_N42_GprsV09 | M57 4836 |          |
      | TEST1028000000001    | SMR      | 5.1     | V 1.1 | V 1.2 | V 1.3 | V 1.4 | Telit 10.00.154 | BL_012 XMX_N42_GprsV09 | M57 4836 | M00 0000 |
      | TEST1029000000001    | SMR      | 5.2     | V 1.1 | V 1.2 | V 1.3 | V 1.4 | Telit 10.00.154 | BL_012 XMX_N42_GprsV09 | M57 4836 | M00 0000 |
      | TEST1030000000001    | SMR      | 5.5     | V 1.1 | V 1.2 | V 1.3 | V 1.4 | Telit 10.00.154 | BL_012 XMX_N42_GprsV09 | M57 4836 | M00 0000 |


  @GetFirmwareVersion @GetFirmwareGas
  Scenario: Get the firmware version from SMR 5.1 gas meter
    Given a dlms device
      | DeviceIdentification | TEST1027000000001 |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | SMR               |
      | ProtocolVersion      |               5.1 |
      | Port                 |              1027 |
    And a dlms device
      | DeviceIdentification        | TEST1027000000002 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | TEST1027000000001 |
      | Channel                     |                 3 |
      | FirmwareModuleVersionSimple |          19180706 |
    When the get firmware version gas request is received
      | DeviceIdentification | TEST1027000000002 |
    Then the firmware version gas result should be returned
      | DeviceIdentification | TEST1027000000002 |
      | SimpleVersionInfo    |          00400011 |
    And the database should be updated with the device firmware version
      | DeviceIdentification | TEST1027000000002 |
      | SimpleVersionInfo    |          00400011 |

  @NightlyBuildOnly @UpdateFirmware
  Scenario: successful update of firmware
    Given a manufacturer
      | ManufacturerCode | KAI  |
      | ManufacturerName | Kaif |
    And a device model
      | ManufacturerName | Kaif  |
      | ModelCode        | MA105 |
    And a dlms device
      | DeviceIdentification | TEST1024000000002 |
      | DeviceType           | SMART_METER_E     |
      | ManufacturerCode     | KAI               |
      | DeviceModelCode      | MA105             |
    And receiving an add or change firmware request
      | FirmwareFileIdentification  | TEST_FW_FILE_0003      |
      | FirmwareFile                | 0000000000230011004000310000001000020801e91effffffff500303000000000000831c9d5aa5b4ffbfd057035a8a7896a4abe7afa36687fbc48944bcee0343eed3a75aab882ec1cf57820adfd4394e262d5fa821c678e71c05c47e1c69c4bfffe1fd |
      | FirmwareFilename            | KFPP_V060100FF.bin     |
      | ManufacturerName            | KAI                    |
      | ModelCode                   | MA105                  |
      | FirmwareModuleVersionComm   | Telit 10.00.154        |
      | FirmwareModuleVersionMa     | BL_012 XMX_N42_GprsV09 |
      | FirmwareModuleVersionFunc   | M57 4836               |
      | FirmwareFileImageIdentifier | FEDCBA0987654321       |
    And the add or change firmware response contains
      | Result | OK |
    When the request for a firmware upgrade is received
      | DeviceIdentification        | TEST1024000000002 |
      | FirmwareFileIdentification  | TEST_FW_FILE_0003 |
    Then the update firmware result should be returned
      | DeviceIdentification        | TEST1024000000002 |
    And the database should not be updated with the new device firmware
      | DeviceIdentification        | TEST1024000000002 |
      | FirmwareModuleVersionComm   |                   |
      | FirmwareModuleVersionMa     |                   |
      | FirmwareModuleVersionFunc   |                   |

  @NightlyBuildOnly @UpdateFirmware
  Scenario: update of firmware, no firmware file found with firmwareFileIdentification
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
    And receiving an add or change firmware request
      | FirmwareFileIdentification  | TEST_FW_FILE_0001      |
      | FirmwareFile                | 0000000000230011004000310000001000020801e91effffffff500303000000000000831c9d5aa5b4ffbfd057035a8a7896a4abe7afa36687fbc48944bcee0343eed3a75aab882ec1cf57820adfd4394e262d5fa821c678e71c05c47e1c69c4bfffe1fd |
      | FirmwareFilename            | KFPP_V060100FF.bin     |
      | ManufacturerName            | KAI                    |
      | ModelCode                   | MA105                  |
      | FirmwareModuleVersionComm   | Telit 10.00.154        |
      | FirmwareModuleVersionMa     | BL_012 XMX_N42_GprsV09 |
      | FirmwareModuleVersionFunc   | M57 4836               |
      | FirmwareFileImageIdentifier | FEDCBA0987654321       |
    And the add or change firmware response contains
      | Result | OK |
    When the request for a firmware upgrade is received
      | DeviceIdentification        | TEST1024000000002 |
      | FirmwareFileIdentification  | TEST_FW_FILE_000X |
    Then retrieving the update firmware response results in an exception
    And a SOAP fault should have been returned
      | Component      | DOMAIN_SMART_METERING                                            |
      | Message        | UNKNOWN_FIRMWARE                                                 |
      | InnerException | org.opensmartgridplatform.shared.exceptionhandling.OsgpException |
      | InnerMessage   | No firmware file found with Identification TEST_FW_FILE_000X     |
    And the database should not be updated with the new device firmware
      | DeviceIdentification        | TEST1024000000002      |
      | FirmwareModuleVersionComm   | Telit 10.00.154        |
      | FirmwareModuleVersionMa     | BL_012 XMX_N42_GprsV09 |
      | FirmwareModuleVersionFunc   | M57 4836               |

  @NightlyBuildOnly @UpdateFirmware
  Scenario: update of firmware, firmware has no imageIdentifier 
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
    And receiving an add or change firmware request
      | FirmwareFileIdentification  | TEST_FW_FILE_0012      |
      | FirmwareFile                | 0000000000230011004000310000001000020801e91effffffff500303000000000000831c9d5aa5b4ffbfd057035a8a7896a4abe7afa36687fbc48944bcee0343eed3a75aab882ec1cf57820adfd4394e262d5fa821c678e71c05c47e1c69c4bfffe1fd |
      | FirmwareFilename            | KFPP_V060100FF.bin     |
      | ManufacturerName            | KAI                    |
      | ModelCode                   | MA105                  |
      | FirmwareModuleVersionComm   | Telit 10.00.154        |
      | FirmwareModuleVersionMa     | BL_012 XMX_N42_GprsV09 |
      | FirmwareModuleVersionFunc   | M57 4836               |
    And the add or change firmware response contains
      | Result | OK |
    When the request for a firmware upgrade is received
      | DeviceIdentification       | TEST1024000000002 |
      | FirmwareFileIdentification | TEST_FW_FILE_0012 |
    Then retrieving the update firmware response results in an exception
    And a SOAP fault should have been returned
      | Component      | PROTOCOL_DLMS                                                                                                 |
      | Message        | Unexpected exception while handling protocol request/response message                                         |
      | InnerException | org.opensmartgridplatform.shared.exceptionhandling.OsgpException                                              |
      | InnerMessage   | Error reading image identifier file (/etc/osp/firmwarefiles/TEST_FW_FILE_0012.imgid) from firmware file store |
    And the database should not be updated with the new device firmware
      | DeviceIdentification      | TEST1024000000002      |
      | FirmwareModuleVersionComm | Telit 10.00.154        |
      | FirmwareModuleVersionMa   | BL_012 XMX_N42_GprsV09 |
      | FirmwareModuleVersionFunc | M57 4836               |

  @NightlyBuildOnly @UpdateFirmware
  Scenario: upgrade of firmware, firmware does not support device model
    Given a manufacturer
      | ManufacturerCode | KAI   |
      | ManufacturerName | Kaifa |
    And a device model
      | ManufacturerName | Kaifa |
      | ModelCode        | MA105 |
    And a device model
      | ManufacturerName | Kaifa |
      | ModelCode        | MA10X |
    And a dlms device
      | DeviceIdentification | TEST1024000000002 |
      | DeviceType           | SMART_METER_E     |
      | ManufacturerCode     | KAI               |
      | DeviceModelCode      | MA105             |
    And receiving an add or change firmware request
      | FirmwareFileIdentification  | TEST_FW_FILE_0001      |
      | FirmwareFile                | 0000000000230011004000310000001000020801e91effffffff500303000000000000831c9d5aa5b4ffbfd057035a8a7896a4abe7afa36687fbc48944bcee0343eed3a75aab882ec1cf57820adfd4394e262d5fa821c678e71c05c47e1c69c4bfffe1fd |
      | FirmwareFilename            | KFPP_V060100FF.bin     |
      | ManufacturerName            | KAI                    |
      | ModelCode                   | MA10X                  |
      | FirmwareModuleVersionComm   | Telit 10.00.154        |
      | FirmwareModuleVersionMa     | BL_012 XMX_N42_GprsV09 |
      | FirmwareModuleVersionFunc   | M57 4836               |
      | FirmwareFileImageIdentifier | FEDCBA0987654321       |
    And the add or change firmware response contains
      | Result | OK |
    When the request for a firmware upgrade is received
      | DeviceIdentification        | TEST1024000000002 |
      | FirmwareFileIdentification  | TEST_FW_FILE_0001 |
    Then retrieving the update firmware response results in an exception
    And a SOAP fault should have been returned
      | Component      | DOMAIN_SMART_METERING                                            |
      | Message        | FIRMWARE_DOES_NOT_SUPPORT_DEVICE_MODEL                           |
      | InnerException | org.opensmartgridplatform.shared.exceptionhandling.OsgpException |
      | InnerMessage   | DeviceModel MA105 of smartmeter TEST1024000000002 is not in list of devicemodels supported by firmware file TEST_FW_FILE_0001 : [MA10X] |
    And the database should not be updated with the new device firmware
      | DeviceIdentification        | TEST1024000000002      |
      | FirmwareModuleVersionComm   | Telit 10.00.154        |
      | FirmwareModuleVersionMa     | BL_012 XMX_N42_GprsV09 |
      | FirmwareModuleVersionFunc   | M57 4836               |

  @NightlyBuildOnly @UpdateFirmware @UpdateFirmwareGas
  Scenario: successful update of firmware on G-meter. Firmware file has valid mbus firmware file header
    Given a manufacturer
      | ManufacturerCode | GMAN          |
      | ManufacturerName | G-meter Man 1 |
    And a device model
      | ManufacturerName | G-meter Man 1   |
      | ModelCode        | G_METER_MODEL_1 |
    And a manufacturer
      | ManufacturerCode | EMAN          |
      | ManufacturerName | E-meter Man 1 |
    And a device model
      | ManufacturerName | E-meter Man 1   |
      | ModelCode        | E_METER_MODEL_1 |
    And a dlms device
      | DeviceIdentification     | ETEST102400000002 |
      | DeviceType               | SMART_METER_E     |
      | ManufacturerCode         | EMAN              |
      | DeviceModelCode          | E_METER_MODEL_1   |
    And a dlms device
      | DeviceIdentification        | GTEST102400000002 |
      | DeviceType                  | SMART_METER_G     |
      | ManufacturerCode            | GMAN              |
      | DeviceModelCode             | G_METER_MODEL_1   |
      | GatewayDeviceIdentification | ETEST102400000002 |
      | MbusIdentificationNumber    | 00000000          |
      | FirmwareUpdateKey           | SECURITY_KEY_2    |
    And a firmware
      | FirmwareFileIdentification  | TEST_FW_FILE_GAS_0002          |
      | FirmwareFilename            | theFirmwareFile.bin            |
      | ModelCode                   | G_METER_MODEL_1                |
      | FirmwareIsForSmartMeters    | true                           |
      | FirmwareFileExists          | false                          |
    And a firmware file and image identifier in a firmware file store and corresponding hash in database
      | FirmwareFileIdentification  | TEST_FW_FILE_GAS_0002          |
      | FirmwareFile                | 534d523500230011004000310000001000020801e91effffffff500303000000000000831c9d5aa5b4ffbfd057035a8a7896a4abe7afa36687fbc48944bcee0343eed3a75aab882ec1cf57820adfd4394e262d5fa821c678e71c05c47e1c69c4bfffe1fd |
      | FirmwareHashType            | SHA256                         |
    When the request for a firmware upgrade is received
      | DeviceIdentification        | GTEST102400000002     |
      | FirmwareFileIdentification  | TEST_FW_FILE_GAS_0002 |
    Then the update firmware result should be returned
      | DeviceIdentification        | GTEST102400000002 |
    And the database should not be updated with the new device firmware
      | DeviceIdentification        | GTEST102400000002 |
