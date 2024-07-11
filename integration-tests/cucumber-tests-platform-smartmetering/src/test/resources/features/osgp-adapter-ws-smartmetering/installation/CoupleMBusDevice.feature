# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringInstallation @MBusDevice
Feature: SmartMetering Installation - Couple M-Bus Device
  As a grid operator
  I want to be able to couple an M-Bus device to a smart meter

  Scenario Outline: Couple G-meter "TESTG101205673117" to a <protocol> <version> E-meter on first channel
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
    And a dlms device
      | DeviceIdentification           | TESTG101205673117       |
      | DeviceType                     | SMART_METER_G           |
      | DeviceLifecycleStatus          | <DeviceLifeCycleStatus> |
      | MbusIdentificationNumber       |                12056731 |
      | MbusManufacturerIdentification | LGB                     |
      | MbusVersion                    |                      66 |
      | MbusDeviceTypeIdentification   |                       3 |
    And device simulation of "<deviceIdentification>" with M-Bus client version <mbusversion> values for channel 1
      | MbusPrimaryAddress             |        9 |
      | MbusIdentificationNumber       | 12056731 |
      | MbusManufacturerIdentification | LGB      |
      | MbusVersion                    |       66 |
      | MbusDeviceTypeIdentification   |        3 |
    When the Couple G-meter "TESTG101205673117" request is received for E-meter "<deviceIdentification>"
    Then the Couple response has the following values
      | MbusDeviceIdentification | TESTG101205673117 |
      | Channel                  |                 1 |
      | PrimaryAddress           |                 9 |
    And the M-Bus device "TESTG101205673117" is coupled to device "<deviceIdentification>" on M-Bus channel "1" with PrimaryAddress "9"

    Examples:
      | DeviceLifeCycleStatus      | deviceIdentification | protocol | version | mbusversion |
      | REGISTERED                 | TEST1024000000001    | DSMR     | 4.2.2   |           0 |
    @NightlyBuildOnly
    Examples:
      | DeviceLifeCycleStatus      | deviceIdentification | protocol | version | mbusversion |
      | NEW_IN_INVENTORY           | TEST1024000000001    | DSMR     | 4.2.2   |           0 |
      | READY_FOR_USE              | TEST1024000000001    | DSMR     | 4.2.2   |           0 |
      | REGISTERED_BUILD_IN_FAILED | TEST1024000000001    | DSMR     | 4.2.2   |           0 |
      | REGISTERED_INSTALL_FAILED  | TEST1024000000001    | DSMR     | 4.2.2   |           0 |
      | REGISTERED_UPDATE_FAILED   | TEST1024000000001    | DSMR     | 4.2.2   |           0 |
      | RETURNED_TO_INVENTORY      | TEST1024000000001    | DSMR     | 4.2.2   |           0 |
      | UNDER_TEST                 | TEST1024000000001    | DSMR     | 4.2.2   |           0 |
      | DESTROYED                  | TEST1024000000001    | DSMR     | 4.2.2   |           0 |
      | REGISTERED                 | TEST1024000000001    | DSMR     | 2.2     |           0 |
      | REGISTERED                 | TEST1031000000001    | SMR      | 4.3     |           0 |
      | REGISTERED                 | TEST1027000000001    | SMR      | 5.0.0   |           1 |
      | REGISTERED                 | TEST1028000000001    | SMR      | 5.1     |           1 |
      | REGISTERED                 | TEST1029000000001    | SMR      | 5.2     |           1 |
      | REGISTERED                 | TEST1030000000001    | SMR      | 5.5     |           1 |

  @NightlyBuildOnly
  Scenario: Couple G-meter "TESTG101205673117" with missing attributes to E-meter "TEST1024000000001" on first channel
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification           | TESTG101205673117 |
      | DeviceType                     | SMART_METER_G     |
      | DeviceLifecycleStatus          | READY_FOR_USE     |
      | MbusIdentificationNumber       |          12056731 |
      | MbusManufacturerIdentification | ITG               |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 1
      | MbusPrimaryAddress             |        9 |
      | MbusIdentificationNumber       | 12056731 |
      | MbusManufacturerIdentification | ITG      |
      | MbusVersion                    |       66 |
      | MbusDeviceTypeIdentification   |        3 |
    When the Couple G-meter "TESTG101205673117" request is received for E-meter "TEST1024000000001"
    Then the Couple response has the following values
      | MbusDeviceIdentification | TESTG101205673117 |
      | Channel                  |                 1 |
      | PrimaryAddress           |                 9 |
    And the M-Bus device "TESTG101205673117" is coupled to device "TEST1024000000001" on M-Bus channel "1" with PrimaryAddress "9"

  @NightlyBuildOnly
  Scenario: Couple G-meter "TESTG101205673117" to E-meter "TEST1024000000001" on second channel
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification           | TESTG101205673117 |
      | DeviceType                     | SMART_METER_G     |
      | DeviceLifecycleStatus          | READY_FOR_USE     |
      | MbusIdentificationNumber       |          12056731 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 1
      | MbusPrimaryAddress             | 0 |
      | MbusIdentificationNumber       | 0 |
      | MbusManufacturerIdentification | 0 |
      | MbusVersion                    | 0 |
      | MbusDeviceTypeIdentification   | 0 |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 2
      | MbusPrimaryAddress             |        9 |
      | MbusIdentificationNumber       | 12056731 |
      | MbusManufacturerIdentification | LGB      |
      | MbusVersion                    |       66 |
      | MbusDeviceTypeIdentification   |        3 |
    When the Couple G-meter "TESTG101205673117" request is received for E-meter "TEST1024000000001"
    Then the Couple response has the following values
      | MbusDeviceIdentification | TESTG101205673117 |
      | Channel                  |                 2 |
      | PrimaryAddress           |                 9 |
    And the M-Bus device "TESTG101205673117" is coupled to device "TEST1024000000001" on M-Bus channel "2" with PrimaryAddress "9"

  @NightlyBuildOnly
  Scenario: Couple G-meter "TESTG101205673117" to E-meter "TEST1024000000002" while G-meter is already coupled.
    Given a dlms device
      | DeviceIdentification | TEST1024000000002 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification           | TESTG101205673117 |
      | DeviceType                     | SMART_METER_G     |
      | DeviceLifecycleStatus          | READY_FOR_USE     |
      | GatewayDeviceIdentification    | TEST1024000000001 |
      | Channel                        |                 1 |
      | MbusIdentificationNumber       |          12056731 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
      | MbusPrimaryAddress             |                 3 |
    When the Couple G-meter "TESTG101205673117" request is received for E-meter "TEST1024000000002"
    Then retrieving the Couple response results in an exception
    And a SOAP fault should have been returned
      | Code    |                               216 |
      | Message | GIVEN_MBUS_DEVICE_ALREADY_COUPLED |
    And the M-Bus device "TESTG101205673117" is coupled to device "TEST1024000000001" on M-Bus channel "1" with PrimaryAddress "3"

  @NightlyBuildOnly
  Scenario: Couple G-meter "TESTG101205673117" to E-meter "TEST1024000000001" on second channel with already coupled channel 1
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification           | TESTG102400000001 |
      | DeviceType                     | SMART_METER_G     |
      | DeviceLifecycleStatus          | READY_FOR_USE     |
      | GatewayDeviceIdentification    | TEST1024000000001 |
      | Channel                        |                 1 |
      | MbusIdentificationNumber       |          12056700 |
      | MbusManufacturerIdentification | NVT               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
    And a dlms device
      | DeviceIdentification           | TESTG101205673117 |
      | DeviceType                     | SMART_METER_G     |
      | DeviceLifecycleStatus          | READY_FOR_USE     |
      | MbusIdentificationNumber       |          12056731 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 1
      | MbusPrimaryAddress             | 0 |
      | MbusIdentificationNumber       | 0 |
      | MbusManufacturerIdentification | 0 |
      | MbusVersion                    | 0 |
      | MbusDeviceTypeIdentification   | 0 |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 2
      | MbusPrimaryAddress             |        9 |
      | MbusIdentificationNumber       | 12056731 |
      | MbusManufacturerIdentification | LGB      |
      | MbusVersion                    |       66 |
      | MbusDeviceTypeIdentification   |        3 |
    When the Couple G-meter "TESTG101205673117" request is received for E-meter "TEST1024000000001"
    Then the Couple response has the following values
      | MbusDeviceIdentification | TESTG101205673117 |
      | Channel                  |                 2 |
      | PrimaryAddress           |                 9 |
    And the M-Bus device "TESTG101205673117" is coupled to device "TEST1024000000001" on M-Bus channel "2" with PrimaryAddress "9"

  @NightlyBuildOnly
  Scenario: Couple G-meter "TESTG101205673117" to E-meter "TEST1024000000001" which is already coupled on channel 1
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification           | TESTG101205673117 |
      | DeviceType                     | SMART_METER_G     |
      | DeviceLifecycleStatus          | READY_FOR_USE     |
      | GatewayDeviceIdentification    | TEST1024000000001 |
      | Channel                        |                 1 |
      | MbusIdentificationNumber       |          12056731 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
      | MbusPrimaryAddress             |                 3 |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 1
      | MbusPrimaryAddress             |        9 |
      | MbusIdentificationNumber       | 12056731 |
      | MbusManufacturerIdentification | LGB      |
      | MbusVersion                    |       66 |
      | MbusDeviceTypeIdentification   |        3 |
    When the Couple G-meter "TESTG101205673117" request is received for E-meter "TEST1024000000001"
    Then retrieving the Couple response results in an exception
    And a SOAP fault should have been returned
      | Code    |                               216 |
      | Message | GIVEN_MBUS_DEVICE_ALREADY_COUPLED |
    And the M-Bus device "TESTG101205673117" is coupled to device "TEST1024000000001" on M-Bus channel "1" with PrimaryAddress "3"

  @NightlyBuildOnly
  Scenario: Couple another G-meter to an E-meter
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | DeviceLifecycleStatus       | READY_FOR_USE     |
      | GatewayDeviceIdentification | TEST1024000000001 |
      | Channel                     |                 1 |
      | MbusPrimaryAddress          |                 3 |
    And a dlms device
      | DeviceIdentification           | TESTG101205673117 |
      | DeviceType                     | SMART_METER_G     |
      | DeviceLifecycleStatus          | READY_FOR_USE     |
      | MbusIdentificationNumber       |          12056731 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 2
      | MbusPrimaryAddress             |        9 |
      | MbusIdentificationNumber       | 12056731 |
      | MbusManufacturerIdentification | LGB      |
      | MbusVersion                    |       66 |
      | MbusDeviceTypeIdentification   |        3 |
    When the Couple G-meter "TESTG101205673117" request is received for E-meter "TEST1024000000001"
    Then the Couple response has the following values
      | MbusDeviceIdentification | TESTG101205673117 |
      | Channel                  |                 2 |
      | PrimaryAddress           |                 9 |
    And the M-Bus device "TESTG102400000001" is coupled to device "TEST1024000000001" on M-Bus channel "1" with PrimaryAddress "3"
    And the M-Bus device "TESTG101205673117" is coupled to device "TEST1024000000001" on M-Bus channel "2" with PrimaryAddress "9"

  @NightlyBuildOnly
  Scenario: Couple unknown G-meter to an E-meter
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When the Couple G-meter "TESTG10240unknown" request is received for E-meter "TEST1024000000001"
    Then retrieving the Couple response results in an exception
    And a SOAP fault should have been returned
      | Code    |            201 |
      | Message | UNKNOWN_DEVICE |

  @NightlyBuildOnly
  Scenario: Couple G-meter to an unknown E-meter
    Given a dlms device
      | DeviceIdentification | TESTG101205673117 |
      | DeviceType           | SMART_METER_G     |
    When the Couple G-meter "TESTG101205673117" to E-meter "TEST102400unknown" request is received for an unknown gateway
    Then a SOAP fault should have been returned
      | Code    |            201 |
      | Message | UNKNOWN_DEVICE |

  @NightlyBuildOnly
  Scenario: Couple unbound G-meter "TESTG101205673101" to E-meter "TEST1024000000001" on a channel 1
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 1
      | MbusPrimaryAddress             | 0 |
      | MbusIdentificationNumber       | 0 |
      | MbusManufacturerIdentification | 0 |
      | MbusVersion                    | 0 |
      | MbusDeviceTypeIdentification   | 0 |
    And a dlms device
      | DeviceIdentification           | TESTG101205673117 |
      | DeviceType                     | SMART_METER_G     |
      | DeviceLifecycleStatus          | READY_FOR_USE     |
      | MbusPrimaryAddress             |                 3 |
      | MbusIdentificationNumber       |          12056731 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
    When the Couple G-meter "TESTG101205673117" request is received for E-meter "TEST1024000000001"
    Then the Couple response has the following values
      | MbusDeviceIdentification | TESTG101205673117 |
      | Channel                  |                 1 |
      | PrimaryAddress           |                 3 |
    And the M-Bus device "TESTG101205673117" is coupled to device "TEST1024000000001" on M-Bus channel "1" with PrimaryAddress "3"
    And the values for the M-Bus client for channel 1 on device simulator "TEST1024000000001" are
      | MbusPrimaryAddress             |        3 |
      | MbusIdentificationNumber       | 12056731 |
      | MbusManufacturerIdentification | LGB      |
      | MbusVersion                    |       66 |
      | MbusDeviceTypeIdentification   |        3 |

  @NightlyBuildOnly
  Scenario: Couple unbound G-meter "TESTG101205673117" without a primary address to E-meter "TEST1024000000001" on a channel 1
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 1
      | MbusPrimaryAddress             | 0 |
      | MbusIdentificationNumber       | 0 |
      | MbusManufacturerIdentification | 0 |
      | MbusVersion                    | 0 |
      | MbusDeviceTypeIdentification   | 0 |
    And a dlms device
      | DeviceIdentification           | TESTG101205673117 |
      | DeviceType                     | SMART_METER_G     |
      | DeviceLifecycleStatus          | READY_FOR_USE     |
      | MbusIdentificationNumber       |          12056731 |
      | MbusManufacturerIdentification | ITG               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
    When the Couple G-meter "TESTG101205673117" request is received for E-meter "TEST1024000000001"
    Then the Couple response has the following values
      | MbusDeviceIdentification | TESTG101205673117 |
      | Channel                  |                 1 |
      | PrimaryAddress           |                 0 |
    And the M-Bus device "TESTG101205673117" is coupled to device "TEST1024000000001" on M-Bus channel "1"
    And the values for the M-Bus client for channel 1 on device simulator "TEST1024000000001" are
      | MbusIdentificationNumber       | 12056731 |
      | MbusManufacturerIdentification | ITG      |
      | MbusVersion                    |       66 |
      | MbusDeviceTypeIdentification   |        3 |

  @NightlyBuildOnly
  Scenario: Couple unbound G-meter "TESTG101205673117" to E-meter "TEST1024000000001" on a channel 2
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 1
      | MbusPrimaryAddress             |      241 |
      | MbusIdentificationNumber       | 12056726 |
      | MbusManufacturerIdentification | LGB      |
      | MbusVersion                    |       66 |
      | MbusDeviceTypeIdentification   |        3 |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 2
      | MbusPrimaryAddress             | 0 |
      | MbusIdentificationNumber       | 0 |
      | MbusManufacturerIdentification | 0 |
      | MbusVersion                    | 0 |
      | MbusDeviceTypeIdentification   | 0 |
    And a dlms device
      | DeviceIdentification           | TESTG101205673117 |
      | DeviceType                     | SMART_METER_G     |
      | DeviceLifecycleStatus          | READY_FOR_USE     |
      | MbusPrimaryAddress             |                 3 |
      | MbusIdentificationNumber       |          12056731 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
    When the Couple G-meter "TESTG101205673117" request is received for E-meter "TEST1024000000001"
    Then the Couple response has the following values
      | MbusDeviceIdentification | TESTG101205673117 |
      | Channel                  |                 2 |
      | PrimaryAddress           |                 3 |
    And the M-Bus device "TESTG101205673117" is coupled to device "TEST1024000000001" on M-Bus channel "2" with PrimaryAddress "3"
    And the values for the M-Bus client for channel 1 on device simulator "TEST1024000000001" are
      | MbusPrimaryAddress             |      241 |
      | MbusIdentificationNumber       | 12056726 |
      | MbusManufacturerIdentification | LGB      |
      | MbusVersion                    |       66 |
      | MbusDeviceTypeIdentification   |        3 |
    And the values for the M-Bus client for channel 2 on device simulator "TEST1024000000001" are
      | MbusPrimaryAddress             |        3 |
      | MbusIdentificationNumber       | 12056731 |
      | MbusManufacturerIdentification | LGB      |
      | MbusVersion                    |       66 |
      | MbusDeviceTypeIdentification   |        3 |

  @NightlyBuildOnly
  Scenario: Couple G-meter to an E-meter when all MBus channels are occupied
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification           | TESTG101205673117 |
      | DeviceType                     | SMART_METER_G     |
      | DeviceLifecycleStatus          | READY_FOR_USE     |
      | MbusIdentificationNumber       |          12056731 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
    And all mbus channels are occupied for E-meter "TEST1024000000001"
    When the Couple G-meter "TESTG101205673117" request is received for E-meter "TEST1024000000001"
    Then retrieving the Couple response results in an exception
    And a SOAP fault should have been returned
      | Code    |                        217 |
      | Message | ALL_MBUS_CHANNELS_OCCUPIED |
    And the mbus device "TESTG101205673117" is not coupled to the device "TEST1024000000001"

  @NightlyBuildOnly
  Scenario: Couple G-meter to an E-meter that is already coupled with other G-meter on channel 2
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 2
      | MbusPrimaryAddress             |      241 |
      | MbusIdentificationNumber       | 12056726 |
      | MbusManufacturerIdentification | LGB      |
      | MbusVersion                    |       66 |
      | MbusDeviceTypeIdentification   |        3 |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | DeviceLifecycleStatus       | READY_FOR_USE     |
      | GatewayDeviceIdentification | TEST1024000000001 |
      | Channel                     |                 2 |
      | MbusPrimaryAddress          |                 3 |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 1
      | MbusPrimaryAddress             | 0 |
      | MbusIdentificationNumber       | 0 |
      | MbusManufacturerIdentification | 0 |
      | MbusVersion                    | 0 |
      | MbusDeviceTypeIdentification   | 0 |
    And a dlms device
      | DeviceIdentification           | TESTG101205673117 |
      | DeviceType                     | SMART_METER_G     |
      | DeviceLifecycleStatus          | READY_FOR_USE     |
      | MbusPrimaryAddress             |                 3 |
      | MbusIdentificationNumber       |          12056731 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
    When the Couple G-meter "TESTG101205673117" request is received for E-meter "TEST1024000000001"
    Then the Couple response has the following values
      | MbusDeviceIdentification | TESTG101205673117 |
      | Channel                  |                 1 |
      | PrimaryAddress           |                 3 |
    And the M-Bus device "TESTG101205673117" is coupled to device "TEST1024000000001" on M-Bus channel "1" with PrimaryAddress "3"
    And the values for the M-Bus client for channel 2 on device simulator "TEST1024000000001" are
      | MbusPrimaryAddress             |      241 |
      | MbusIdentificationNumber       | 12056726 |
      | MbusManufacturerIdentification | LGB      |
      | MbusVersion                    |       66 |
      | MbusDeviceTypeIdentification   |        3 |
    And the values for the M-Bus client for channel 1 on device simulator "TEST1024000000001" are
      | MbusPrimaryAddress             |        3 |
      | MbusIdentificationNumber       | 12056731 |
      | MbusManufacturerIdentification | LGB      |
      | MbusVersion                    |       66 |
      | MbusDeviceTypeIdentification   |        3 |

  @NightlyBuildOnly
  Scenario: Couple G-meter to an E-meter when G-meter is IN_USE
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification           | TESTG101205673117 |
      | DeviceType                     | SMART_METER_G     |
      | DeviceLifecycleStatus          | IN_USE            |
      | MbusIdentificationNumber       |          12056731 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
    And all mbus channels are occupied for E-meter "TEST1024000000001"
    When the Couple G-meter "TESTG101205673117" request is received for E-meter "TEST1024000000001"
    Then retrieving the Couple response results in an exception
    And a SOAP fault should have been returned
      | Code    |                                     222 |
      | Message | MBUS_DEVICE_NOT_MOVED_TO_ANOTHER_EMETER |
    And the mbus device "TESTG101205673117" is not coupled to the device "TEST1024000000001"

  Scenario: Couple G-meter to an E-meter, without anything connected
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 1
      | MbusPrimaryAddress             | 0 |
      | MbusIdentificationNumber       | 0 |
      | MbusManufacturerIdentification | 0 |
      | MbusVersion                    | 0 |
      | MbusDeviceTypeIdentification   | 0 |
    And a dlms device
      | DeviceIdentification           | TESTG101205673117 |
      | DeviceType                     | SMART_METER_G     |
      | DeviceLifecycleStatus          | READY_FOR_USE     |
      | MbusPrimaryAddress             |                 3 |
      | MbusIdentificationNumber       |          12056731 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
    When the Couple G-meter "TESTG101205673117" request is received for E-meter "TEST1024000000001"
    Then the Couple response has the following values
      | MbusDeviceIdentification | TESTG101205673117 |
      | Channel                  |                 1 |
      | PrimaryAddress           |                 3 |
    And the M-Bus device "TESTG101205673117" is coupled to device "TEST1024000000001" on M-Bus channel "1" with PrimaryAddress "3"
    And the values for the M-Bus client for channel 1 on device simulator "TEST1024000000001" are
      | MbusPrimaryAddress             |        3 |
      | MbusIdentificationNumber       | 12056731 |
      | MbusManufacturerIdentification | LGB      |
      | MbusVersion                    |       66 |
      | MbusDeviceTypeIdentification   |        3 |

  Scenario Outline: Couple G-meter to an E-meter, with meter already connected
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification | TEST1024000000002 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 1
      | MbusPrimaryAddress             | <MbusPrimaryAddress>             |
      | MbusIdentificationNumber       | <MbusIdentificationNumber>       |
      | MbusManufacturerIdentification | <MbusManufacturerIdentification> |
      | MbusVersion                    | <MbusVersion>                    |
      | MbusDeviceTypeIdentification   | <MbusDeviceTypeIdentification>   |
    And a dlms device
      | DeviceIdentification           | TESTG101205673117             |
      | DeviceType                     | SMART_METER_G                 |
      | DeviceLifecycleStatus          | READY_FOR_USE                 |
      | MbusPrimaryAddress             |                             3 |
      | MbusIdentificationNumber       |                      12056731 |
      | MbusManufacturerIdentification | LGB                           |
      | MbusVersion                    |                            66 |
      | MbusDeviceTypeIdentification   |                             3 |
      | GatewayDeviceIdentification    | <GatewayDeviceIdentification> |
      | Channel                        |                             1 |
    When the Couple G-meter "TESTG101205673117" request is received for E-meter "TEST1024000000001"
    Then retrieving the Couple response results in an exception
    And a SOAP fault should have been returned
      | Code    |                               216 |
      | Message | GIVEN_MBUS_DEVICE_ALREADY_COUPLED |
    And the M-Bus device "TESTG101205673117" is coupled to device "<GatewayDeviceIdentification>" on M-Bus channel "1" with PrimaryAddress "3"

    Examples:
      | GatewayDeviceIdentification | MbusPrimaryAddress | MbusIdentificationNumber | MbusManufacturerIdentification | MbusVersion | MbusDeviceTypeIdentification |
      | TEST1024000000001           |                  3 |                 12056731 |                            LGB |          66 |                            3 |
      | TEST1024000000002           |                  0 |                        0 |                              0 |           0 |                            0 |

  Scenario Outline: Couple G-meter to an E-meter with force, with meter already connected
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification | TEST1024000000002 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 1
      | MbusPrimaryAddress             | <MbusPrimaryAddress>             |
      | MbusIdentificationNumber       | <MbusIdentificationNumber>       |
      | MbusManufacturerIdentification | <MbusManufacturerIdentification> |
      | MbusVersion                    | <MbusVersion>                    |
      | MbusDeviceTypeIdentification   | <MbusDeviceTypeIdentification>   |
    And a dlms device
      | DeviceIdentification           | TESTG101205673117             |
      | DeviceType                     | SMART_METER_G                 |
      | DeviceLifecycleStatus          | READY_FOR_USE                 |
      | MbusPrimaryAddress             |                             3 |
      | MbusIdentificationNumber       |                      12056731 |
      | MbusManufacturerIdentification | LGB                           |
      | MbusVersion                    |                            66 |
      | MbusDeviceTypeIdentification   |                             3 |
      | GatewayDeviceIdentification    | <GatewayDeviceIdentification> |
      | Channel                        |                             1 |
    When the Couple G-meter "TESTG101205673117" request is received for E-meter "TEST1024000000001" with force
    Then the Couple response has the following values
      | MbusDeviceIdentification | TESTG101205673117 |
      | Channel                  |                 1 |
      | PrimaryAddress           |                 3 |
    And the M-Bus device "TESTG101205673117" is coupled to device "TEST1024000000001" on M-Bus channel "1" with PrimaryAddress "3"
    And the values for the M-Bus client for channel 1 on device simulator "TEST1024000000001" are
      | MbusPrimaryAddress             |        3 |
      | MbusIdentificationNumber       | 12056731 |
      | MbusManufacturerIdentification | LGB      |
      | MbusVersion                    |       66 |
      | MbusDeviceTypeIdentification   |        3 |

    Examples:
      | GatewayDeviceIdentification | MbusPrimaryAddress | MbusIdentificationNumber | MbusManufacturerIdentification | MbusVersion | MbusDeviceTypeIdentification |
      | TEST1024000000001           |                  3 |                 12056731 |                            LGB |          66 |                            3 |
      | TEST1024000000002           |                  0 |                        0 |                              0 |           0 |                            0 |
