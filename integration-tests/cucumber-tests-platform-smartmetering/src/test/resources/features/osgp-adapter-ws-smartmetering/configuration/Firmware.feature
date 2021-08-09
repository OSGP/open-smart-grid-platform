@SmartMetering @Platform @SmartMeteringConfiguration @SmartMeteringFirmware
Feature: SmartMetering Configuration - Firmware
  As a grid operator
  I want to be able to perform SmartMeteringFirmware operations on a device
  In order to ...

  Scenario: Get the firmware version from DSMR 4.2.2 device
    Given a dlms device
      | DeviceIdentification      | TEST1024000000001 |
      | DeviceType                | SMART_METER_E     |
      | Protocol                  | DSMR              |
      | ProtocolVersion           | 4.2.2             |
      | Port                      |              1024 |
      | FirmwareModuleVersionComm | V 1.1             |
      | FirmwareModuleVersionMa   | V 1.2             |
      | FirmwareModuleVersionFunc | V 1.3             |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | TEST1024000000001 |
      | Channel                     |                 1 |
    When the get firmware version request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the firmware version result should be returned
      | DeviceIdentification      | TEST1024000000001      |
      | FirmwareModuleVersionComm | Telit 10.00.154        |
      | FirmwareModuleVersionMa   | BL_012 XMX_N42_GprsV09 |
      | FirmwareModuleVersionFunc | M57 4836               |
    And the database should be updated with the device firmware version
      | DeviceIdentification      | TEST1024000000001      |
      | FirmwareModuleVersionComm | Telit 10.00.154        |
      | FirmwareModuleVersionMa   | BL_012 XMX_N42_GprsV09 |
      | FirmwareModuleVersionFunc | M57 4836               |
      | FirmwareIsForSmartMeters  | true                   |

  #  TODO (RvM): to fix test: support SMR 5.1 with invocation counter in the simulator
  @wip @Skip
  Scenario: Get the firmware version from SMR 5.1 device
    Given a dlms device
      | DeviceIdentification      | TEST1027000000001 |
      | DeviceType                | SMART_METER_E     |
      | Protocol                  | SMR               |
      | ProtocolVersion           |               5.1 |
      | Port                      |              1027 |
      | FirmwareModuleVersionComm | V 1.1             |
      | FirmwareModuleVersionMa   | V 1.2             |
      | FirmwareModuleVersionFunc | V 1.3             |
      | FirmwareModuleVersionMbda | V 1.4             |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | TEST1027000000001 |
      | Channel                     |                 1 |
    When the get firmware version request is received
      | DeviceIdentification | TEST1027000000001 |
    Then the firmware version result should be returned
      | DeviceIdentification      | TEST1027000000001      |
      | FirmwareModuleVersionComm | Telit 10.00.154        |
      | FirmwareModuleVersionMa   | BL_012 XMX_N42_GprsV09 |
      | FirmwareModuleVersionFunc | M57 4836               |
      | FirmwareModuleVersionMbda | M00 0000               |
    And the database should be updated with the device firmware version
      | DeviceIdentification      | TEST1027000000001      |
      | FirmwareModuleVersionComm | Telit 10.00.154        |
      | FirmwareModuleVersionMa   | BL_012 XMX_N42_GprsV09 |
      | FirmwareModuleVersionFunc | M57 4836               |
      | FirmwareModuleVersionMbda | M00 0000               |
      | FirmwareIsForSmartMeters  | true                   |

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

  @NightlyBuildOnly
  Scenario: successful update of firmware
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
      | FirmwareFileIdentification  | TEST_FW_FILE_0001                        |
      | FirmwareFile                | 57696520646974206c656573742069732067656b |
      | FirmwareModuleVersionComm   | Telit 10.00.154                          |
      | FirmwareModuleVersionMa     | BL_012 XMX_N42_GprsV09                   |
      | FirmwareModuleVersionFunc   | M57 4836                                 |
      | FirmwareModuleVersionMbus   |                                          |
      | FirmwareModuleVersionSec    |                                          |
      | FirmwareFilename            | KFPP_V060100FF.bin                       |
      | FirmwareFileImageIdentifier | 496d6167654964656e746966696572           |
      | ModelCode                   | MA105                                    |
      | FirmwareIsForSmartMeters    | true                                     |
    When the request for a firmware upgrade is received
      | DeviceIdentification        | TEST1024000000002 |
      | FirmwareFileIdentification  | TEST_FW_FILE_0001 |
    Then the update firmware result should be returned
      | DeviceIdentification        | TEST1024000000002 |
    And the database should not be updated with the new device firmware
      | DeviceIdentification        | TEST1024000000002 |
      | FirmwareModuleVersionComm   |                   |
      | FirmwareModuleVersionMa     |                   |
      | FirmwareModuleVersionFunc   |                   |

  @NightlyBuildOnly
  Scenario: update of firmware, no firmware file found with firmwareFileIdentification
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
      | FirmwareFileIdentification  | TEST_FW_FILE_0001                        |
      | FirmwareFile                | 57696520646974206c656573742069732067656b |
      | FirmwareModuleVersionComm   | Telit 10.00.154                          |
      | FirmwareModuleVersionMa     | BL_012 XMX_N42_GprsV09                   |
      | FirmwareModuleVersionFunc   | M57 4836                                 |
      | FirmwareModuleVersionMbus   |                                          |
      | FirmwareModuleVersionSec    |                                          |
      | FirmwareFilename            | KFPP_V060100FA                           |
      | ModelCode                   | MA105                                    |
      | FirmwareFileImageIdentifier | 496d6167654964656e746966696572           |
      | FirmwareFileExists          | false                                    |
      | FirmwareIsForSmartMeters    | true                                     |
    When the request for a firmware upgrade is received
      | DeviceIdentification        | TEST1024000000002      |
      | FirmwareFileIdentification  | TEST_FW_FILE_000X      |
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

  @NightlyBuildOnly
  Scenario: update of firmware, firmware has no imageIdentifier 
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
      | FirmwareFileIdentification | TEST_FW_FILE_0002                        |
      | FirmwareFile               | 57696520646974206c656573742069732067656b |
      | FirmwareModuleVersionComm  | Telit 10.00.154                          |
      | FirmwareModuleVersionMa    | BL_012 XMX_N42_GprsV09                   |
      | FirmwareModuleVersionFunc  | M57 4836                                 |
      | FirmwareModuleVersionMbus  |                                          |
      | FirmwareModuleVersionSec   |                                          |
      | FirmwareFilename           | KFPP_V060100FA                           |
      | ModelCode                  | MA105                                    |
      | FirmwareFileExists         | false                                    |
      | FirmwareIsForSmartMeters   | true                                     |
    When the request for a firmware upgrade is received
      | DeviceIdentification       | TEST1024000000002      |
      | FirmwareFileIdentification | TEST_FW_FILE_0002      |
    Then retrieving the update firmware response results in an exception
    And a SOAP fault should have been returned
      | Component      | PROTOCOL_DLMS                                                         |
      | Message        | Unexpected exception while handling protocol request/response message |
      | InnerException | org.opensmartgridplatform.shared.exceptionhandling.OsgpException      |
      | InnerMessage   | Firmware Image Identifier is not available.                           |
    And the database should not be updated with the new device firmware
      | DeviceIdentification      | TEST1024000000002      |
      | FirmwareModuleVersionComm | Telit 10.00.154        |
      | FirmwareModuleVersionMa   | BL_012 XMX_N42_GprsV09 |
      | FirmwareModuleVersionFunc | M57 4836               |

  @NightlyBuildOnly
  Scenario: upgrade of firmware, firmware does not support device model
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
      | FirmwareFileIdentification  | TEST_FW_FILE_0001                        |
      | FirmwareFile                | 57696520646974206c656573742069732067656b |
      | FirmwareModuleVersionComm   | Telit 10.00.154                          |
      | FirmwareModuleVersionMa     | BL_012 XMX_N42_GprsV09                   |
      | FirmwareModuleVersionFunc   | M57 4836                                 |
      | FirmwareModuleVersionMbus   |                                          |
      | FirmwareModuleVersionSec    |                                          |
      | FirmwareFilename            | KFPP_V060100FA                           |
      | ModelCode                   | MA10X                                    |
      | FirmwareFileImageIdentifier | 496d6167654964656e746966696572           |
      | FirmwareFileExists          | false                                    |
      | FirmwareIsForSmartMeters    | true                                     |
    When the request for a firmware upgrade is received
      | DeviceIdentification        | TEST1024000000002      |
      | FirmwareFileIdentification  | TEST_FW_FILE_0001      |
    Then retrieving the update firmware response results in an exception
    And a SOAP fault should have been returned
      | Component      | DOMAIN_SMART_METERING                                                 |
      | Message        | FIRMWARE_DOES_NOT_SUPPORT_DEVICE_MODEL                                |
      | InnerException | org.opensmartgridplatform.shared.exceptionhandling.OsgpException      |
      | InnerMessage   | DeviceModel MA105 of smartmeter TEST1024000000002 is not in list of devicemodels supported by firmware file TEST_FW_FILE_0001 : [MA10X] |
    And the database should not be updated with the new device firmware
      | DeviceIdentification        | TEST1024000000002      |
      | FirmwareModuleVersionComm   | Telit 10.00.154        |
      | FirmwareModuleVersionMa     | BL_012 XMX_N42_GprsV09 |
      | FirmwareModuleVersionFunc   | M57 4836               |
