Feature: Stop Device
  As an ...
  I want to ...
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

  @OslpMockServer
  Scenario Outline: Stop device with incorrect parameters
    Given an oslp device
      | DeviceIdentification       | TEST1024000000001            |
      | OrganizationIdentification | <OrganizationIdentification> |
      | Status                     | unknown                      |
    And the device returns a stop device response "OK" over OSLP
    When receiving a stop device test request
      | DeviceIdentification | TEST1024000000001 |
    Then the stop device response contains soap fault
      | Message | <Message> |

    Examples: 
      | OrganizationIdentification | Message      |
      | ORGANIZATION-01            | UNAUTHORIZED |
      | ORGANIZATION_ID_UNKNOWN    | UNAUTHORIZED |
      | ORGANIZATION_ID_EMPTY      | UNAUTHORIZED |
      | ORGANIZATION_ID_SPACES     | UNAUTHORIZED |
      
      