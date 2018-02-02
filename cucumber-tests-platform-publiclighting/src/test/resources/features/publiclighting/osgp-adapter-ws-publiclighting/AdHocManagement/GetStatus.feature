@PublicLighting @Platform @PublicLightingAdhocManagement
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
      | Protocol             | <Protocol>        |
    And the device returns a get status response "OK" over "<Protocol>"
      | PreferredLinkType  | <PreferredLinkType>      |
      | ActualLinkType     | <ActualLinkType>         |
      | LightType          | <LightType>              |
      | EventNotifications | <EventNotificationTypes> |
      | LightValues        | <LightValues>            |
    When receiving a get status request
      | DeviceIdentification | TEST1024000000001 |
    Then the get status async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a get status "<Protocol>" message is sent to device "TEST1024000000001"
    And the platform buffers a get status response message for device "TEST1024000000001"
      | Result            | <Result>                    |
      | PreferredLinkType | <ExpectedPreferredLinkType> |
      | ActualLinkType    | <ExpectedActualLinkType>    |
      | LightType         | <ExpectedLightType>         |

    Examples: 
      | Protocol    | RelayType       | PreferredLinkType | ActualLinkType | LightType               | EventNotificationTypes                                                                             | LightValues                                                       | Result | ExpectedPreferredLinkType | ExpectedActualLinkType | ExpectedLightType       |
      | OSLP        | LIGHT           | LINK_NOT_SET      | LINK_NOT_SET   | LT_NOT_SET              |                                                                                                    | 1,true,100                                                        | OK     |                           |                        |                         |
      | OSLP        | LIGHT           | CDMA              | GPRS           | DALI                    | DIAG_EVENTS                                                                                        | 1,true,100                                                        | OK     | CDMA                      | GPRS                   | DALI                    |
      | OSLP        | LIGHT           | CDMA              | GPRS           | DALI                    | COMM_EVENTS                                                                                        | 1,true,100                                                        | OK     | CDMA                      | GPRS                   | DALI                    |
      | OSLP        | LIGHT           | CDMA              | CDMA           | RELAY                   | HARDWARE_FAILURE                                                                                   | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | RELAY                   |
      | OSLP        | LIGHT           | CDMA              | CDMA           | RELAY                   | LIGHT_EVENTS,TARIFF_EVENTS                                                                         | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | RELAY                   |
      | OSLP        | LIGHT           | CDMA              | CDMA           | DALI                    | DIAG_EVENTS,HARDWARE_FAILURE,LIGHT_EVENTS,TARIFF_EVENTS,MONITOR_EVENTS,FIRMWARE_EVENTS,COMM_EVENTS | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | DALI                    |
      | OSLP        | LIGHT           | CDMA              | ETHERNET       | ONE_TO_TEN_VOLT         |                                                                                                    | 1,true,100                                                        | OK     | CDMA                      | ETHERNET               | ONE_TO_TEN_VOLT         |
      | OSLP        | LIGHT           | CDMA              | CDMA           | ONE_TO_TEN_VOLT_REVERSE |                                                                                                    | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | ONE_TO_TEN_VOLT_REVERSE |
      | OSLP        | LIGHT           | CDMA              | CDMA           | DALI                    |                                                                                                    | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | DALI                    |
      | OSLP        | TARIFF          | LINK_NOT_SET      | LINK_NOT_SET   | LT_NOT_SET              |                                                                                                    | 1,true,100                                                        | OK     |                           |                        |                         |
      | OSLP        | TARIFF          | CDMA              | GPRS           | DALI                    | DIAG_EVENTS                                                                                        | 1,true,100                                                        | OK     | CDMA                      | GPRS                   | DALI                    |
      | OSLP        | TARIFF          | CDMA              | GPRS           | DALI                    | COMM_EVENTS                                                                                        | 1,true,100                                                        | OK     | CDMA                      | GPRS                   | DALI                    |
      | OSLP        | TARIFF          | CDMA              | CDMA           | RELAY                   | HARDWARE_FAILURE                                                                                   | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | RELAY                   |
      | OSLP        | TARIFF          | CDMA              | CDMA           | RELAY                   | LIGHT_EVENTS,TARIFF_EVENTS                                                                         | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | RELAY                   |
      | OSLP        | TARIFF          | CDMA              | CDMA           | DALI                    | DIAG_EVENTS,HARDWARE_FAILURE,LIGHT_EVENTS,TARIFF_EVENTS,MONITOR_EVENTS,FIRMWARE_EVENTS,COMM_EVENTS | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | DALI                    |
      | OSLP        | TARIFF          | CDMA              | ETHERNET       | ONE_TO_TEN_VOLT         |                                                                                                    | 1,true,100                                                        | OK     | CDMA                      | ETHERNET               | ONE_TO_TEN_VOLT         |
      | OSLP        | TARIFF          | CDMA              | CDMA           | ONE_TO_TEN_VOLT_REVERSE |                                                                                                    | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | ONE_TO_TEN_VOLT_REVERSE |
      | OSLP        | TARIFF          | CDMA              | CDMA           | DALI                    |                                                                                                    | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | DALI                    |
      | OSLP        | TARIFF_REVERSED | LINK_NOT_SET      | LINK_NOT_SET   | LT_NOT_SET              |                                                                                                    | 1,true,100                                                        | OK     |                           |                        |                         |
      | OSLP        | TARIFF_REVERSED | CDMA              | GPRS           | DALI                    | DIAG_EVENTS                                                                                        | 1,true,100                                                        | OK     | CDMA                      | GPRS                   | DALI                    |
      | OSLP        | TARIFF_REVERSED | CDMA              | GPRS           | DALI                    | COMM_EVENTS                                                                                        | 1,true,100                                                        | OK     | CDMA                      | GPRS                   | DALI                    |
      | OSLP        | TARIFF_REVERSED | CDMA              | CDMA           | RELAY                   | HARDWARE_FAILURE                                                                                   | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | RELAY                   |
      | OSLP        | TARIFF_REVERSED | CDMA              | CDMA           | RELAY                   | LIGHT_EVENTS,TARIFF_EVENTS                                                                         | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | RELAY                   |
      | OSLP        | TARIFF_REVERSED | CDMA              | CDMA           | DALI                    | DIAG_EVENTS,HARDWARE_FAILURE,LIGHT_EVENTS,TARIFF_EVENTS,MONITOR_EVENTS,FIRMWARE_EVENTS,COMM_EVENTS | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | DALI                    |
      | OSLP        | TARIFF_REVERSED | CDMA              | ETHERNET       | ONE_TO_TEN_VOLT         |                                                                                                    | 1,true,100                                                        | OK     | CDMA                      | ETHERNET               | ONE_TO_TEN_VOLT         |
      | OSLP        | TARIFF_REVERSED | CDMA              | CDMA           | ONE_TO_TEN_VOLT_REVERSE |                                                                                                    | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | ONE_TO_TEN_VOLT_REVERSE |
      | OSLP        | TARIFF_REVERSED | CDMA              | CDMA           | DALI                    |                                                                                                    | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | DALI                    |
      | OSLP        | LIGHT           | LINK_NOT_SET      | LINK_NOT_SET   | LT_NOT_SET              |                                                                                                    | 1,true,100;2,true,100;3,true,100;4,true,100;5,true,100;6,true,100 | OK     |                           |                        |                         |
      | OSLP ELSTER | LIGHT           | LINK_NOT_SET      | LINK_NOT_SET   | LT_NOT_SET              |                                                                                                    | 1,true,100                                                        | OK     |                           |                        |                         |
      | OSLP ELSTER | LIGHT           | CDMA              | GPRS           | DALI                    | DIAG_EVENTS                                                                                        | 1,true,100                                                        | OK     | CDMA                      | GPRS                   | DALI                    |
      | OSLP ELSTER | LIGHT           | CDMA              | GPRS           | DALI                    | COMM_EVENTS                                                                                        | 1,true,100                                                        | OK     | CDMA                      | GPRS                   | DALI                    |
      | OSLP ELSTER | LIGHT           | CDMA              | CDMA           | RELAY                   | HARDWARE_FAILURE                                                                                   | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | RELAY                   |
      | OSLP ELSTER | LIGHT           | CDMA              | CDMA           | RELAY                   | LIGHT_EVENTS,TARIFF_EVENTS                                                                         | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | RELAY                   |
      | OSLP ELSTER | LIGHT           | CDMA              | CDMA           | DALI                    | DIAG_EVENTS,HARDWARE_FAILURE,LIGHT_EVENTS,TARIFF_EVENTS,MONITOR_EVENTS,FIRMWARE_EVENTS,COMM_EVENTS | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | DALI                    |
      | OSLP ELSTER | LIGHT           | CDMA              | ETHERNET       | ONE_TO_TEN_VOLT         |                                                                                                    | 1,true,100                                                        | OK     | CDMA                      | ETHERNET               | ONE_TO_TEN_VOLT         |
      | OSLP ELSTER | LIGHT           | CDMA              | CDMA           | ONE_TO_TEN_VOLT_REVERSE |                                                                                                    | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | ONE_TO_TEN_VOLT_REVERSE |
      | OSLP ELSTER | LIGHT           | CDMA              | CDMA           | DALI                    |                                                                                                    | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | DALI                    |
      | OSLP ELSTER | TARIFF          | LINK_NOT_SET      | LINK_NOT_SET   | LT_NOT_SET              |                                                                                                    | 1,true,100                                                        | OK     |                           |                        |                         |
      | OSLP ELSTER | TARIFF          | CDMA              | GPRS           | DALI                    | DIAG_EVENTS                                                                                        | 1,true,100                                                        | OK     | CDMA                      | GPRS                   | DALI                    |
      | OSLP ELSTER | TARIFF          | CDMA              | GPRS           | DALI                    | COMM_EVENTS                                                                                        | 1,true,100                                                        | OK     | CDMA                      | GPRS                   | DALI                    |
      | OSLP ELSTER | TARIFF          | CDMA              | CDMA           | RELAY                   | HARDWARE_FAILURE                                                                                   | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | RELAY                   |
      | OSLP ELSTER | TARIFF          | CDMA              | CDMA           | RELAY                   | LIGHT_EVENTS,TARIFF_EVENTS                                                                         | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | RELAY                   |
      | OSLP ELSTER | TARIFF          | CDMA              | CDMA           | DALI                    | DIAG_EVENTS,HARDWARE_FAILURE,LIGHT_EVENTS,TARIFF_EVENTS,MONITOR_EVENTS,FIRMWARE_EVENTS,COMM_EVENTS | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | DALI                    |
      | OSLP ELSTER | TARIFF          | CDMA              | ETHERNET       | ONE_TO_TEN_VOLT         |                                                                                                    | 1,true,100                                                        | OK     | CDMA                      | ETHERNET               | ONE_TO_TEN_VOLT         |
      | OSLP ELSTER | TARIFF          | CDMA              | CDMA           | ONE_TO_TEN_VOLT_REVERSE |                                                                                                    | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | ONE_TO_TEN_VOLT_REVERSE |
      | OSLP ELSTER | TARIFF          | CDMA              | CDMA           | DALI                    |                                                                                                    | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | DALI                    |
      | OSLP ELSTER | TARIFF_REVERSED | LINK_NOT_SET      | LINK_NOT_SET   | LT_NOT_SET              |                                                                                                    | 1,true,100                                                        | OK     |                           |                        |                         |
      | OSLP ELSTER | TARIFF_REVERSED | CDMA              | GPRS           | DALI                    | DIAG_EVENTS                                                                                        | 1,true,100                                                        | OK     | CDMA                      | GPRS                   | DALI                    |
      | OSLP ELSTER | TARIFF_REVERSED | CDMA              | GPRS           | DALI                    | COMM_EVENTS                                                                                        | 1,true,100                                                        | OK     | CDMA                      | GPRS                   | DALI                    |
      | OSLP ELSTER | TARIFF_REVERSED | CDMA              | CDMA           | RELAY                   | HARDWARE_FAILURE                                                                                   | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | RELAY                   |
      | OSLP ELSTER | TARIFF_REVERSED | CDMA              | CDMA           | RELAY                   | LIGHT_EVENTS,TARIFF_EVENTS                                                                         | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | RELAY                   |
      | OSLP ELSTER | TARIFF_REVERSED | CDMA              | CDMA           | DALI                    | DIAG_EVENTS,HARDWARE_FAILURE,LIGHT_EVENTS,TARIFF_EVENTS,MONITOR_EVENTS,FIRMWARE_EVENTS,COMM_EVENTS | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | DALI                    |
      | OSLP ELSTER | TARIFF_REVERSED | CDMA              | ETHERNET       | ONE_TO_TEN_VOLT         |                                                                                                    | 1,true,100                                                        | OK     | CDMA                      | ETHERNET               | ONE_TO_TEN_VOLT         |
      | OSLP ELSTER | TARIFF_REVERSED | CDMA              | CDMA           | ONE_TO_TEN_VOLT_REVERSE |                                                                                                    | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | ONE_TO_TEN_VOLT_REVERSE |
      | OSLP ELSTER | TARIFF_REVERSED | CDMA              | CDMA           | DALI                    |                                                                                                    | 1,true,100                                                        | OK     | CDMA                      | CDMA                   | DALI                    |
      | OSLP ELSTER | LIGHT           | LINK_NOT_SET      | LINK_NOT_SET   | LT_NOT_SET              |                                                                                                    | 1,true,100;2,true,100;3,true,100;4,true,100;5,true,100;6,true,100 | OK     |                           |                        |                         |

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
      | DeviceIdentification  | TEST1024000000001 |
      | Activated             | <Activated>       |
      | DeviceLifecycleStatus | <Status>          |
    When receiving a get status request
      | DeviceIdentification | TEST1024000000001 |
    Then the get status response contains soap fault
      | Message | INACTIVE_DEVICE |

    Examples: 
      | Activated | Status           |
      | false     | NEW_IN_INVENTORY |
      | false     | REGISTERED       |
      | false     | DESTROYED        |

  Scenario: Get status for an unregistered device
    Given an ssld device
      | DeviceIdentification | TEST1024000000001 |
      | PublicKeyPresent     | false             |
    When receiving a get status request
      | DeviceIdentification | TEST1024000000001 |
    Then the get status response contains soap fault
      | Message | UNREGISTERED_DEVICE |
