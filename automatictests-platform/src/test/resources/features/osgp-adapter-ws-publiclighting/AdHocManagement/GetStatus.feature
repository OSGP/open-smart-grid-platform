Feature: PublicLightingAdhocManagement GetStatus
  As a platform 
  I want to asynchronously handle set light requests
  In order to ... 

  @OslpMockServer
  Scenario Outline: Get Status from a device
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Status               | Active            |
      | RelayType            | <RelayType>       |
    And the device returns a get status response "OK" over OSLP
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
      | RelayType       | PreferredLinkType | ActualLinkType | LightType               | EventNotificationTypes                                                                             | LightValues                                                       | Result | ExpectedPreferredLinkType | ExpectedActualLinkType | ExpectedLightType       |
      | LIGHT           | LINK_NOT_SET      | LINK_NOT_SET   | LT_NOT_SET              |                                                                                                    | 1,true,100                                                        | OK     |                           |                        |                         |
      | LIGHT           | CDMA              | GPRS           | DALI                    | DIAG_EVENTS                                                                                        | 1,true,100                                                        | OK     | CDMA                      | GPRS                   | DALI                    |
      | LIGHT           | CDMA              | GPRS           | DALI                    | COMM_EVENTS                                                                                        | 1,true,100                                                        | OK     | CDMA                      | GPRS                   | DALI                    |
      | LIGHT           | CDMA              | CDMA           | RELAY                   | HARDWARE_FAILURE                                                                                   | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | RELAY                   |
      | LIGHT           | CDMA              | CDMA           | RELAY                   | LIGHT_EVENTS,TARIFF_EVENTS                                                                         | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | RELAY                   |
      | LIGHT           | CDMA              | CDMA           | DALI                    | DIAG_EVENTS,HARDWARE_FAILURE,LIGHT_EVENTS,TARIFF_EVENTS,MONITOR_EVENTS,FIRMWARE_EVENTS,COMM_EVENTS | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | DALI                    |
      | LIGHT           | CDMA              | ETHERNET       | ONE_TO_TEN_VOLT         |                                                                                                    | 1,true,100                                                        | OK     | CDMA                      | ETHERNET               | ONE_TO_TEN_VOLT         |
      | LIGHT           | CDMA              | CDMA           | ONE_TO_TEN_VOLT_REVERSE |                                                                                                    | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | ONE_TO_TEN_VOLT_REVERSE |
      | LIGHT           | CDMA              | CDMA           | DALI                    |                                                                                                    | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | DALI                    |
      | TARIFF          | LINK_NOT_SET      | LINK_NOT_SET   | LT_NOT_SET              |                                                                                                    | 1,true,100                                                        | OK     |                           |                        |                         |
      | TARIFF          | CDMA              | GPRS           | DALI                    | DIAG_EVENTS                                                                                        | 1,true,100                                                        | OK     | CDMA                      | GPRS                   | DALI                    |
      | TARIFF          | CDMA              | GPRS           | DALI                    | COMM_EVENTS                                                                                        | 1,true,100                                                        | OK     | CDMA                      | GPRS                   | DALI                    |
      | TARIFF          | CDMA              | CDMA           | RELAY                   | HARDWARE_FAILURE                                                                                   | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | RELAY                   |
      | TARIFF          | CDMA              | CDMA           | RELAY                   | LIGHT_EVENTS,TARIFF_EVENTS                                                                         | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | RELAY                   |
      | TARIFF          | CDMA              | CDMA           | DALI                    | DIAG_EVENTS,HARDWARE_FAILURE,LIGHT_EVENTS,TARIFF_EVENTS,MONITOR_EVENTS,FIRMWARE_EVENTS,COMM_EVENTS | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | DALI                    |
      | TARIFF          | CDMA              | ETHERNET       | ONE_TO_TEN_VOLT         |                                                                                                    | 1,true,100                                                        | OK     | CDMA                      | ETHERNET               | ONE_TO_TEN_VOLT         |
      | TARIFF          | CDMA              | CDMA           | ONE_TO_TEN_VOLT_REVERSE |                                                                                                    | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | ONE_TO_TEN_VOLT_REVERSE |
      | TARIFF          | CDMA              | CDMA           | DALI                    |                                                                                                    | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | DALI                    |
      | TARIFF_REVERSED | LINK_NOT_SET      | LINK_NOT_SET   | LT_NOT_SET              |                                                                                                    | 1,true,100                                                        | OK     |                           |                        |                         |
      | TARIFF_REVERSED | CDMA              | GPRS           | DALI                    | DIAG_EVENTS                                                                                        | 1,true,100                                                        | OK     | CDMA                      | GPRS                   | DALI                    |
      | TARIFF_REVERSED | CDMA              | GPRS           | DALI                    | COMM_EVENTS                                                                                        | 1,true,100                                                        | OK     | CDMA                      | GPRS                   | DALI                    |
      | TARIFF_REVERSED | CDMA              | CDMA           | RELAY                   | HARDWARE_FAILURE                                                                                   | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | RELAY                   |
      | TARIFF_REVERSED | CDMA              | CDMA           | RELAY                   | LIGHT_EVENTS,TARIFF_EVENTS                                                                         | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | RELAY                   |
      | TARIFF_REVERSED | CDMA              | CDMA           | DALI                    | DIAG_EVENTS,HARDWARE_FAILURE,LIGHT_EVENTS,TARIFF_EVENTS,MONITOR_EVENTS,FIRMWARE_EVENTS,COMM_EVENTS | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | DALI                    |
      | TARIFF_REVERSED | CDMA              | ETHERNET       | ONE_TO_TEN_VOLT         |                                                                                                    | 1,true,100                                                        | OK     | CDMA                      | ETHERNET               | ONE_TO_TEN_VOLT         |
      | TARIFF_REVERSED | CDMA              | CDMA           | ONE_TO_TEN_VOLT_REVERSE |                                                                                                    | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | ONE_TO_TEN_VOLT_REVERSE |
      | TARIFF_REVERSED | CDMA              | CDMA           | DALI                    |                                                                                                    | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | DALI                    |
      | LIGHT           | LINK_NOT_SET      | LINK_NOT_SET   | LT_NOT_SET              |                                                                                                    | 1,true,100;2,true,100;3,true,100;4,true,100;5,true,100;6,true,100 | OK     |                           |                        |                         |

  Scenario: Get status as an unknown organization
    When receiving a get status request by an unknown organization
      | DeviceIdentification | TEST1024000000001 |
    Then the get status response contains soap fault
      | Message | UNKNOWN_ORGANISATION |

  Scenario: Get status for an unknown device
    When receiving a get status request
      | DeviceIdentification | TEST1024000000001 |
    Then the get status response contains soap fault
      | Message | UNKNOWN_DEVICE |

  Scenario Outline: Get status for an inactive device
    Given an ssld device
      | DeviceIdentification | TEST1024000000001 |
      | IsActivated          | <IsActivated>     |
      | Active               | <IsActive>        |
    When receiving a get status request
      | DeviceIdentification | TEST1024000000001 |
    Then the get status response contains soap fault
      | Message | INACTIVE_DEVICE |

    Examples: 
      | IsActivated | IsActive |
      | false       | true     |
      | true        | false    |

  Scenario: Get status for an unregistered device
    Given an ssld device
      | DeviceIdentification | TEST1024000000001 |
      | PublicKeyPresent     | false             |
    When receiving a get status request
      | DeviceIdentification | TEST1024000000001 |
    Then the get status response contains soap fault
      | Message | UNREGISTERED_DEVICE |
