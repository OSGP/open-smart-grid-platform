Feature: CoreDeviceInstallation Device Stopping
  As a ...
  I want to be able to perform DeviceInstallation operations on a device
  In order to ...

  @OslpMockServer
  Scenario: Stop Device Successfully
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
    And the device returns a stop device response "OK" over OSLP
    When receiving a stop device test request
      | DeviceIdentification | TEST1024000000001 |
    Then the stop device async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a stop device OSLP message is sent to device "TEST1024000000001"
    And the platform buffers a stop device response message for device "TEST1024000000001"
      | Result | OK |

  Scenario Outline: Stop device with incorrect parameters
    Given an oslp device
      | DeviceIdentification       | TEST1024000000001            |
      | OrganizationIdentification | <OrganizationIdentification> |
      | Status                     | unknown                      |
    When receiving a stop device test request
      | DeviceIdentification | TEST1024000000001 |
    Then the stop device response contains soap fault
      | Message | UNAUTHORIZED |

    Examples: 
      | OrganizationIdentification |
      | ORGANIZATION-01            |
      | ORGANIZATION_ID_UNKNOWN    |
      | ORGANIZATION_ID_EMPTY      |
      | ORGANIZATION_ID_SPACES     |
