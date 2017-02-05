Feature: CoreDeviceInstallation Get Status
  As a ...
  I want to be able to perform DeviceInstallation operations on a device
  In order to ...

  @OslpMockServer
  Scenario Outline: Get status of a device
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
    And the device returns a get status response "OK" over OSLP
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
      | RelayType | PreferredLinkType | ActualLinkType | LightType               | EventNotificationTypes                                                                             | LightValues | Result | ExpectedPreferredLinkType | ExpectedActualLinkType | ExpectedLightType       |
      | LIGHT     | LINK_NOT_SET      | LINK_NOT_SET   | LT_NOT_SET              |                                                                                                    | 1,true,100  | OK     |                           |                        |                         |
      | LIGHT     | CDMA              | GPRS           | DALI                    | DIAG_EVENTS                                                                                        | 1,true,100  | OK     | CDMA                      | GPRS                   | DALI                    |
      | LIGHT     | CDMA              | GPRS           | DALI                    | COMM_EVENTS                                                                                        | 1,true,100  | OK     | CDMA                      | GPRS                   | DALI                    |
      | LIGHT     | CDMA              | CDMA           | RELAY                   | HARDWARE_FAILURE                                                                                   | 1,true,100  | OK     | CDMA                      | CDMA                   | RELAY                   |
      | LIGHT     | CDMA              | CDMA           | RELAY                   | LIGHT_EVENTS,TARIFF_EVENTS                                                                         | 1,true,100  | OK     | CDMA                      | CDMA                   | RELAY                   |
      | LIGHT     | CDMA              | CDMA           | DALI                    | DIAG_EVENTS,HARDWARE_FAILURE,LIGHT_EVENTS,TARIFF_EVENTS,MONITOR_EVENTS,FIRMWARE_EVENTS,COMM_EVENTS | 1,true,100  | OK     | CDMA                      | CDMA                   | DALI                    |
      | LIGHT     | CDMA              | ETHERNET       | ONE_TO_TEN_VOLT         |                                                                                                    | 1,true,100  | OK     | CDMA                      | ETHERNET               | ONE_TO_TEN_VOLT         |
      | LIGHT     | CDMA              | CDMA           | ONE_TO_TEN_VOLT_REVERSE |                                                                                                    | 1,true,100  | OK     | CDMA                      | CDMA                   | ONE_TO_TEN_VOLT_REVERSE |
      | LIGHT     | CDMA              | CDMA           | DALI                    |                                                                                                    | 1,true,100  | OK     | CDMA                      | CDMA                   | DALI                    |
