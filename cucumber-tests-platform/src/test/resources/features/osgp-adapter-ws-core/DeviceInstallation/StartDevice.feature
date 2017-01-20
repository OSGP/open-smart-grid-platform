Feature: CoreDeviceInstallation Device Starting
  As a ...
  I want to be able to perform DeviceInstallation operations on a device
  In order to ...

  @OslpMockServer
  Scenario: Start device
    Given an oslp device
      | DeviceIdentification       | TEST1024000000001 |
      | OrganizationIdentification | test-org          |
    And the device returns a start device response "OK" over OSLP
    When receiving a start device test request
      | DeviceIdentification | TEST1024000000001 |
    Then the start device async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a start device OSLP message is sent to device "TEST1024000000001"
    And the platform buffers a start device response message for device "TEST1024000000001"
      | Result | OK |

  Scenario Outline: Start device with incorrect parameters
    Given an oslp device
      | DeviceIdentification       | TEST1024000000001            |
      | OrganizationIdentification | <OrganizationIdentification> |
      | Status                     | unknown                      |
    When receiving a start device test request
      | DeviceIdentification | TEST1024000000001 |
    Then the start device response contains soap fault
      | Message | UNAUTHORIZED |

    Examples: 
      | OrganizationIdentification |
      | ORGANIZATION-01            |
      | ORGANIZATION_ID_UNKNOWN    |
      | ORGANIZATION_ID_EMPTY      |
      | ORGANIZATION_ID_SPACES     |
