@SmartMetering @Platform
Feature: SmartMetering Bundle - De Couple M-Bus Device By Channel
  As a grid operator
  I want to be able to de couple an M-Bus device by channel to a smart meter

  Scenario: DeCouple Mbus Device By Channel on a administratively decoupled E-meter
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a decouple mbus device by channel action
      | Channel | 1 |
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5 | unsigned             |        9 |
      | 6 | double-long-unsigned | 12056731 |
      | 7 | long-unsigned        |    12514 |
      | 8 | unsigned             |       66 |
      | 9 | unsigned             |        3 |
    When the bundle request is received
    Then the decouple mbus device by channel bundle response is "OK" without Mbus Device
    And the values for classid 72 obiscode "0-1:24.1.0" on device simulator "TEST1024000000001" are
      | 5 | unsigned             | 0 |
      | 6 | double-long-unsigned | 0 |
      | 7 | long-unsigned        | 0 |
      | 8 | unsigned             | 0 |
      | 9 | unsigned             | 0 |

  Scenario: DeCouple Mbus Device By Channel on a administratively decoupled E-meter with empty channel
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a decouple mbus device by channel action
      | Channel | 1 |
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5 | unsigned             | 0 |
      | 6 | double-long-unsigned | 0 |
      | 7 | long-unsigned        | 0 |
      | 8 | unsigned             | 0 |
      | 9 | unsigned             | 0 |
    When the bundle request is received
    Then the decouple mbus device by channel bundle response is "OK" without Mbus Device
    And the values for classid 72 obiscode "0-1:24.1.0" on device simulator "TEST1024000000001" are
      | 5 | unsigned             | 0 |
      | 6 | double-long-unsigned | 0 |
      | 7 | long-unsigned        | 0 |
      | 8 | unsigned             | 0 |
      | 9 | unsigned             | 0 |
