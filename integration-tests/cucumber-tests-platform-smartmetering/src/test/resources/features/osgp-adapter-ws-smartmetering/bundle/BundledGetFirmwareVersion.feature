# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringBundle @SmartMeteringFirmware
Feature: SmartMetering Bundle - GetFirmwareVersion
  As a grid operator
  I want to retrieve the firmware versions from a meter via a bundle request

  Scenario Outline: Retrieve the firmware version of a <protocol> <version> device in a bundle request
    Given a dlms device
      | DeviceIdentification      | <deviceidentification> |
      | DeviceType                | SMART_METER_E          |
      | Protocol                  | <protocol>             |
      | ProtocolVersion           | <version>              |
      | FirmwareModuleVersionComm | <comm0>                |
      | FirmwareModuleVersionMa   | <ma0>                  |
      | FirmwareModuleVersionFunc | <func0>                |
      | FirmwareModuleVersionMbda | <mbda0>                |
    And a bundle request
      | DeviceIdentification | <deviceidentification> |
    And the bundle request contains a get firmware version action
    When the bundle request is received
    Then the bundle response should contain a get firmware version response
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

  Scenario: Retrieve an updated firmware version of a device in a bundle request, when a device already has a firmware
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a device firmware version
      | DeviceIdentification      | TEST1024000000001      |
      | FirmwareModuleVersionComm | Telit 12.34.567        |
      | FirmwareModuleVersionMa   | xxxxxxxxxxxxxxxxxxxxxx |
      | FirmwareModuleVersionFunc | ME1 1234               |
      | FirmwareIsForSmartMeters  | true                   |
    And a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a get firmware version action
    When the bundle request is received
    Then the bundle response should contain a get firmware version response
      | FirmwareModuleVersionComm | Telit 10.00.154        |
      | FirmwareModuleVersionMa   | BL_012 XMX_N42_GprsV09 |
      | FirmwareModuleVersionFunc | M57 4836               |
    And the database should be updated with the device firmware version
      | DeviceIdentification      | TEST1024000000001      |
      | FirmwareModuleVersionComm | Telit 10.00.154        |
      | FirmwareModuleVersionMa   | BL_012 XMX_N42_GprsV09 |
      | FirmwareModuleVersionFunc | M57 4836               |
      | FirmwareIsForSmartMeters  | true                   |

  Scenario Outline: Retrieve the firmware version of a <protocol> <version> <type> mbus device in a bundle request
    Given a dlms device
      | DeviceIdentification | <e-meter>     |
      | DeviceType           | SMART_METER_E |
      | Protocol             | <protocol>    |
      | ProtocolVersion      |     <version> |
    And a dlms device
      | DeviceIdentification        | <mbus-meter>       |
      | DeviceType                  | SMART_METER_<type> |
      | GatewayDeviceIdentification | <e-meter>          |
      | Channel                     |                  2 |
      | MbusPrimaryAddress          |                  2 |
    And a bundle request
      | DeviceIdentification | <e-meter> |
    And the bundle request contains a get firmware version gas action
      | DeviceIdentification | <mbus-meter> |
    When the bundle request is received
    Then the bundle response should contain a get firmware version gas response
      | SimpleVersionInfo | 19180706 |
    And the database should be updated with the device firmware version
      | DeviceIdentification | <mbus-meter> |
      | SimpleVersionInfo    |     19180706 |

    Examples:
      | e-meter           | mbus-meter        | protocol | version | type |
      | TEST1027000000001 | TESTG102700000001 | SMR      | 5.0.0   | G    |
    @NightlyBuildOnly
    Examples:
      | e-meter           | mbus-meter        | protocol | version | type |
      | TEST1028000000001 | TESTG102800000001 | SMR      | 5.1     | G    |
      | TEST1029000000001 | TESTG102900000001 | SMR      | 5.2     | G    |
      | TEST1030000000001 | TESTG103000000001 | SMR      | 5.5     | G    |
    @Hydrogen @NightlyBuildOnly
    Examples:
      | e-meter           | mbus-meter        | protocol | version | type |
      | TEST1030000000001 | TESTW103000000001 | SMR      | 5.5     | W    |

  Scenario: Retrieve an updated firmware version of a mbus device in a bundle request, when a device already has a firmware
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
      | Channel                     |                 2 |
      | MbusPrimaryAddress          |                 2 |
    And a device firmware version
      | DeviceIdentification | TEST1027000000002 |
      | SimpleVersionInfo    |          12345567 |
    And a bundle request
      | DeviceIdentification | TEST1027000000001 |
    And the bundle request contains a get firmware version gas action
      | DeviceIdentification | TEST1027000000002 |
    When the bundle request is received
    Then the bundle response should contain a get firmware version gas response
      | SimpleVersionInfo | 19180706 |
    And the database should be updated with the device firmware version
      | DeviceIdentification | TEST1027000000002 |
      | SimpleVersionInfo    |          19180706 |

  Scenario Outline: Retrieve the firmware version of a mbus device with a none supporting protocol <protocol> <version> in a bundle request
    Given a dlms device
      | DeviceIdentification | <e-meter>     |
      | DeviceType           | SMART_METER_E |
      | Protocol             | <protocol>    |
      | ProtocolVersion      | <version>     |
    And a dlms device
      | DeviceIdentification        | <g-meter>     |
      | DeviceType                  | SMART_METER_G |
      | GatewayDeviceIdentification | <e-meter>     |
      | Channel                     |             2 |
      | MbusPrimaryAddress          |             2 |
    And a bundle request
      | DeviceIdentification | <e-meter> |
    And the bundle request contains a get firmware version gas action
      | DeviceIdentification | <g-meter> |
    When the bundle request is received
    Then the bundle response should be a FaultResponse with message containing
      | Message | Simple Version Info not supported by protocol |

    Examples:
      | e-meter           | g-meter           | protocol | version |
      | TEST1024000000001 | TESTG102400000001 | DSMR     | 4.2.2   |
    @NightlyBuildOnly
    Examples:
      | e-meter           | g-meter           | protocol | version |
      | TEST1024000000001 | TESTG102400000001 | DSMR     | 2.2     |
