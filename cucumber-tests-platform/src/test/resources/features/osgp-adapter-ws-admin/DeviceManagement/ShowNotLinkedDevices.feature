Feature: AdminDeviceManagement Show Not Linked Devices
  As a ...
  I want to ...
  In order to ...

  Scenario: Show Devices Which Are Not Linked To An Organization
    Given a device
      | DeviceIdentification       | TEST1024000000001 |
      | OrganizationIdentification | test-org          |
      | DeviceFunctionGroup        | MANAGEMENT        |
    When receiving a find devices without organization request
    Then the find devices without organization response contains "1" devices
    And the find devices without organization response contains at index "1"
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Show Devices Which Are Not Linked To An Organization While All Devices Are Linked
    Given a device
      | DeviceIdentification       | TEST1024000000001 |
      | OrganizationIdentification | test-org          |
    When receiving a find devices without organization request
    Then the find devices without organization response contains "0" devices
