@Common @Platform @CoreDeviceManagement
Feature: CoreDeviceManagement Find Devices
  As a ...
  I want to be able to perform DeviceManagement operations on a device
  So that ...

  Scenario: Find devices parameterized
    Given a device
      | DeviceIdentification       | TEST1024000000001 |
      | OrganizationIdentification | test-org          |
    When receiving a find devices request
      | PageSize | 25 |
      | Page     |  0 |
    Then the find devices response contains "1" devices
    And the find devices response contains at index "1"
      | DeviceIdentification | TEST1024000000001 |
