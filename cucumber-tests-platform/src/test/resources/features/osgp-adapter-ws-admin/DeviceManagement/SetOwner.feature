Feature: AdminDeviceManagement Set Owner
  As a ...
  I want to be able to perform DeviceManagement operations on a device
  In order to ...

  Scenario: Set Owner
    Given a device
      | DeviceIdentification       | TEST1024000000001 |
      | OrganizationIdentification |                   |
    When receiving a set owner request over OSGP
      | DeviceIdentification       | TEST1024000000001 |
      | OrganizationIdentification | test-org          |
    Then the set owner async response contains
      | DeviceIdentification       | TEST1024000000001 |
      | OrganizationIdentification | test-org          |
    And the owner of device "TEST1024000000001" has been changed
      | OrganizationIdentification | test-org |
