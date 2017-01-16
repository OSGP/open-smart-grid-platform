Feature: Start Device
  As a grid operator
  I want to ...
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
      | Message | <Message> |

    Examples: 
      | OrganizationIdentification | Message      |
      | ORGANIZATION-01            | UNAUTHORIZED |
      | ORGANIZATION_ID_UNKNOWN    | UNAUTHORIZED |
      | ORGANIZATION_ID_EMPTY      | UNAUTHORIZED |
      | ORGANIZATION_ID_SPACES     | UNAUTHORIZED |
