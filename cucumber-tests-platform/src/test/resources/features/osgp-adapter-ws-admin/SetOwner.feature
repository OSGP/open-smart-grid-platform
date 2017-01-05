Feature: Device management
  As a grid operator
  I want to be able to perform DeviceManagement operations on a device
  In order to ...

  Scenario Outline: Set Owner
    Given a device
      | DeviceIdentification       | <DeviceIdentification>          |
      | OrganizationIdentification | <OldOrganizationIdentification> |
    When receiving a set owner request over OSGP
      | DeviceIdentification       | <DeviceIdentification>          |
      | OrganizationIdentification | <NewOrganizationIdentification> |
    Then the set owner async response contains
      | DeviceIdentification       | <DeviceIdentification>          |
      | OrganizationIdentification | <NewOrganizationIdentification> |
    And the owner of device "<DeviceIdentification>" has been changed
      | OrganizationIdentification | <NewOrganizationIdentification> |

    Examples: 
      | DeviceIdentification | OldOrganizationIdentification | NewOrganizationIdentification |
      | TEST1024000000001    |                               | test-org                      |
