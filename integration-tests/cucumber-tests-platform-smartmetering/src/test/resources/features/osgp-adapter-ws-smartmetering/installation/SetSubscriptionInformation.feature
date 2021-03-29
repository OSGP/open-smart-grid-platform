@SmartMetering @Platform
Feature: SmartMetering Installation - Set Subscription Information
  As a grid operator
  I want to be able to be able to update the subscription information of a smart meter

  Scenario: Set Subscription Information
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When receiving a smartmetering set subscription information request
      | DeviceIdentification | TEST1024000000001 |
      | IpAddress            | 10.0.0.1          |
      | BtsId                | 20                |
      | CellId               | 1                 |
    Then the set subscription information response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |
      | IpAddress            | 10.0.0.1          |
      | BtsId                | 20                |
      | CellId               | 1                 |