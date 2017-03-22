Feature: Core opreations
  As a grid operator
  I want to be able to perform Core operations on a device
  In order to ...

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | TEST1024000000001 |
      | Channel                     |                 1 |

  Scenario: Try to connect to an unknown ip address
    Given a dlms device
      | DeviceIdentification | TEST1024000000002 |
      | DeviceType           | SMART_METER_E     |
      | Port                 |              9999 |
    When the get actual meter reads request is received
      | DeviceIdentification | TEST1024000000002 |
    Then the audit trail contains multiple retry log records
      | DeviceIdentification | TEST1024000000002 |
