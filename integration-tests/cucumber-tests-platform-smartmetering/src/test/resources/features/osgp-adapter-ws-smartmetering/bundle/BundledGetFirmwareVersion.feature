@SmartMetering @Platform @SmartMeteringBundle @SmartMeteringFirmware
Feature: SmartMetering Bundle - GetFirmwareVersion
  As a grid operator
  I want to retrieve the firmware versions from a meter via a bundle request

  Scenario: Retrieve the firmware version of a device in a bundle request
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
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

  Scenario: Retrieve the firmware version of a mbus device in a bundle request
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
