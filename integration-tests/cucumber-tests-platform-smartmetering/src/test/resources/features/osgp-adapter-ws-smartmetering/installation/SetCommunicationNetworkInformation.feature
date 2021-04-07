@SmartMetering @Platform
Feature: SmartMetering Installation - Set Communication Network Information
  As a grid operator
  I want to be able to update the communication network information of a smart meter

  Scenario: Set Communication Network Information
    Given a smart meter
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When receiving a smartmetering set communication network information request
      | DeviceIdentification | TEST1024000000001 |
      | IpAddress            |          10.0.0.1 |
      | BtsId                |                20 |
      | CellId               |                 1 |
    Then the set communication network information response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |
      | IpAddress            |          10.0.0.1 |
      | BtsId                |                20 |
      | CellId               |                 1 |