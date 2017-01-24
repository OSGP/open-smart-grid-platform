Feature: CoreDeviceInstallation Get Status
  As a ...
  I want to be able to perform DeviceInstallation operations on a device
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
    When receiving a device installation get status request
      | DeviceIdentification | TEST1024000000001 |
    Then the device installation get status async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a get status OSLP message is sent to device "TEST1024000000001"
    And the platform buffers a device installation get status response message for device "TEST1024000000001"
      | Result            | <Result>                    |
      | PreferredLinkType | <ExpectedPreferredLinkType> |
      | ActualLinkType    | <ExpectedActualLinkType>    |
      | LightType         | <ExpectedLightType>         |

    Examples: 
      | RelayType | PreferredLinkType | ActualLinkType | LightType  | EventNotificationTypes | LightValues | Result | ExpectedPreferredLinkType | ExpectedActualLinkType | ExpectedLightType |
      | LIGHT     | LINK_NOT_SET      | LINK_NOT_SET   | LT_NOT_SET |                        | 1;true;100  | OK     |                           |                        |                   |
