Feature: Get Status
  As a ...
  I want to ...
  In order to ...

  @OslpMockServer
  Scenario Outline: Get status of a device
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Status               | Active            |
      | RelayType            | <RelayType>       |
    And the device returns a get status response over OSLP
      | PreferredLinkType  | <PreferredLinkType>      |
      | ActualLinkType     | <ActualLinkType>         |
      | LightType          | <LightType>              |
      | EventNotifications | <EventNotificationTypes> |
      | LightValues        | <LightValues>            |
    When receiving a get status request
      | DeviceIdentification | TEST1024000000001 |
    Then the get status async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a get status OSLP message is sent to device "TEST1024000000001"
    And the platform buffers a get status response message for device "TEST1024000000001"
      | Result            | <Result>                    |
      | PreferredLinkType | <ExpectedPreferredLinkType> |
      | ActualLinkType    | <ExpectedActualLinkType>    |
      | LightType         | <ExpectedLightType>         |

    Examples: 
      | RelayType | PreferredLinkType | ActualLinkType | LightType  | EventNotificationTypes | LightValues | Result | ExpectedPreferredLinkType | ExpectedActualLinkType | ExpectedLightType |
      | LIGHT     | LINK_NOT_SET      | LINK_NOT_SET   | LT_NOT_SET |                        | 1;true;100  | OK     |                           |                        |                   |
