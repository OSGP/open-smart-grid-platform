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

  Scenario: Find light measurement devices
    Given the light measurement devices
    When receiving a find devices request
      | DeviceIdentification     | LMD-0*               |
      | HasTechnicalInstallation | true                 |
      | PageSize                 |                    5 |
      | Page                     |                    0 |
      | SortDir                  | asc                  |
      | SortedBy                 | deviceIdentification |
    Then the find devices response contains "4" devices
    And the find devices response contains at index "1"
      | DeviceIdentification | LMD-01 |
    And the find devices response contains at index "2"
      | DeviceIdentification | LMD-02 |
    And the find devices response contains at index "3"
      | DeviceIdentification | LMD-03 |
    And the find devices response contains at index "4"
      | DeviceIdentification | LMD-04 |

  Scenario: Find light measurement device and check its details
    Given the light measurement devices
    When receiving a find devices request
      | DeviceIdentification     | LMD-01               |
      | HasTechnicalInstallation | true                 |
      | PageSize                 |                    5 |
      | Page                     |                    0 |
      | SortDir                  | asc                  |
      | SortedBy                 | deviceIdentification |
    Then the find devices response contains "1" devices
    And the find devices response contains at index "1"
      | DeviceIdentification  | LMD-01               |
      | DeviceType            | LMD                  |
      | Code                  | N-01                 |
      | Color                 | #c9eec9              |
      | DigitalInput          |                    1 |
      | LastCommunicationTime | 2017-08-01T13:00:00Z |
