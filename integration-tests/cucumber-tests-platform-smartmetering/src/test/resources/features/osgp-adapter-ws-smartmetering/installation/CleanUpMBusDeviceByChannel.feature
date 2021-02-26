@SmartMetering @Platform @Skip
Feature: SmartMetering Installation - Clean Up M-Bus Device By Channel
  As a grid operator
  I want to be able to clean up an M-Bus device by channel to a smart meter

  Scenario: Clean Up a connected and bound G-meter "TESTG100261510717" to E-meter "TEST1024000000001" on channel 1
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5 | unsigned             | 3        |
      | 6 | double-long-unsigned | 12056731 |
      | 7 | long-unsigned        | 12514    |
      | 8 | unsigned             | 66       |
      | 9 | unsigned             | 3        |
    And a dlms device
      | DeviceIdentification           | TESTG101205673117 |
      | DeviceType                     | SMART_METER_G     |
      | MbusIdentificationNumber       | 12056731          |
      | MbusPrimaryAddress             | 9                 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    | 66                |
      | MbusDeviceTypeIdentification   | 3                 |
    When the Clean Up M-Bus Device By Channel request is received
      | DeviceIdentification | TEST1024000000001 |
      | Channel              | 1                 |
    Then the Clean Up M-Bus Device By Channel response is "OK"
    And the mbus device "TESTG101205673117" is not coupled to the device "TEST1024000000001"
