@PublicLighting @Platform @CoreConfigurationManagement
Feature: CoreConfigurationManagement GetConfiguration
  As a ...
  I want to ...
  In order to ...

  @OslpMockServer
  Scenario Outline: Get configuration of a device
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    And the device returns a get configuration status over "<Protocol>"
      | Status            | OK                  |
      | LightType         | <LightType>         |
      | DcLights          | <DcLights>          |
      | DcMap             | <DcMap>             |
      | RelayConf         | <RelayConf>         |
      | PreferredLinkType | <PreferredLinkType> |
      | MeterType         | <MeterType>         |
      | ShortInterval     | <ShortInterval>     |
      | LongInterval      | <LongInterval>      |
      | IntervalType      | <IntervalType>      |
    When receiving a get configuration request
      | DeviceIdentification | TEST1024000000001 |
    Then the get configuration async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a get configuration "<Protocol>" message is sent to device "TEST1024000000001"
    And the platform buffers a get configuration response message for device "TEST1024000000001"
      | Result            | OK                  |
      | Description       |                     |
      | LightType         | <LightType>         |
      | DcLights          | <DcLights>          |
      | DcMap             | <DcMap>             |
      | RelayConf         | <RelayConf>         |
      | PreferredLinkType | <PreferredLinkType> |
      | MeterType         | <MeterType>         |
      | ShortInterval     | <ShortInterval>     |
      | LongInterval      | <LongInterval>      |
      | IntervalType      | <IntervalType>      |

    Examples: 
      | Protocol    | LightType               | DcLights | DcMap   | RelayConf  | PreferredLinkType | MeterType | ShortInterval | LongInterval | IntervalType |
      | OSLP        | RELAY                   |          |         |            |                   | AUX       |               |              |              |
      | OSLP        | RELAY                   |          |         | 1,1,TARIFF |                   |           |               |              |              |
      | OSLP        | ONE_TO_TEN_VOLT         |          |         |            |                   |           |               |              |              |
      | OSLP        | ONE_TO_TEN_VOLT_REVERSE |          |         |            |                   |           |               |              |              |
      | OSLP        | DALI                    |        2 | 1,2;2,1 |            |                   |           |               |              |              |
      | OSLP        |                         |          |         |            |                   |           |            30 |              |              |
      | OSLP        |                         |          |         |            | GPRS              |           |               |              |              |
      | OSLP        | DALI                    |          |         |            |                   |           |               |              |              |
      | OSLP        |                         |          |         |            |                   |           |               |              |              |
      | OSLP        |                         |          |         |            |                   | P1        |               |              |              |
      | OSLP        |                         |          |         |            |                   |           |               |           10 | DAYS         |
      | OSLP        |                         |          |         |            |                   |           |               |           10 | MONTHS       |
      | OSLP        | RELAY                   |          |         | 1,1,LIGHT  | CDMA              | PULSE     |            15 |           30 | DAYS         |
      | OSLP        | RELAY                   |          |         | 1,1,LIGHT  | ETHERNET          | P1        |            15 |            1 | DAYS         |
      | OSLP ELSTER | RELAY                   |          |         |            |                   | AUX       |               |              |              |
      | OSLP ELSTER | RELAY                   |          |         | 1,1,LIGHT  |                   |           |               |              |              |
      | OSLP ELSTER | ONE_TO_TEN_VOLT         |          |         |            |                   |           |               |              |              |
      | OSLP ELSTER | ONE_TO_TEN_VOLT_REVERSE |          |         |            |                   |           |               |              |              |
      | OSLP ELSTER | DALI                    |        2 | 1,2;2,1 |            |                   |           |               |              |              |
      | OSLP ELSTER |                         |          |         |            |                   |           |            30 |              |              |
      | OSLP ELSTER |                         |          |         |            | GPRS              |           |               |              |              |
      | OSLP ELSTER | DALI                    |          |         |            |                   |           |               |              |              |
      | OSLP ELSTER |                         |          |         |            |                   |           |               |              |              |
      | OSLP ELSTER |                         |          |         |            |                   | P1        |               |              |              |
      | OSLP ELSTER |                         |          |         |            |                   |           |               |           10 | DAYS         |
      | OSLP ELSTER |                         |          |         |            |                   |           |               |           10 | MONTHS       |
      | OSLP ELSTER | RELAY                   |          |         | 1,1,LIGHT  | CDMA              | PULSE     |            15 |           30 | DAYS         |
      | OSLP ELSTER | RELAY                   |          |         | 1,1,LIGHT  | ETHERNET          | P1        |            15 |            1 | DAYS         |

  Scenario: Get configuration data with unknown device
    When receiving a get configuration request
      | DeviceIdentification | TEST1024000000001 |
    Then the get configuration async response contains soap fault
      | Message | UNKNOWN_DEVICE |

  @OslpMockServer
  Scenario Outline: Failed get configuration of a device
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    And the device returns a get configuration status over "<Protocol>"
      | Status            | FAILURE   |
      | LightType         | RELAY     |
      | DcLights          |           |
      | DcMap             |           |
      | RelayConf         | 1,1,LIGHT |
      | PreferredLinkType | ETHERNET  |
      | MeterType         | P1        |
      | ShortInterval     |        15 |
      | LongInterval      |        30 |
      | IntervalType      | DAYS      |
    When receiving a get configuration request
      | DeviceIdentification | TEST1024000000001 |
    Then the get configuration async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a get configuration "<Protocol>" message is sent to device "TEST1024000000001"
    And the platform buffers a get configuration response message for device "TEST1024000000001" contains soap fault
      | Message | Device reports failure |

    Examples: 
      | Protocol    |
      | OSLP        |
      | OSLP ELSTER |

  @OslpMockServer
  Scenario Outline: Rejected get configuration of a device
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    And the device returns a get configuration status over "<Protocol>"
      | Status            | REJECTED  |
      | LightType         | RELAY     |
      | DcLights          |           |
      | DcMap             |           |
      | RelayConf         | 1,1,LIGHT |
      | PreferredLinkType | ETHERNET  |
      | MeterType         | P1        |
      | ShortInterval     |        15 |
      | LongInterval      |        30 |
      | IntervalType      | DAYS      |
    When receiving a get configuration request
      | DeviceIdentification | TEST1024000000001 |
    Then the get configuration async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a get configuration "<Protocol>" message is sent to device "TEST1024000000001"
    And the platform buffers a get configuration response message for device "TEST1024000000001" contains soap fault
      | Message | Device reports rejected |

    Examples: 
      | Protocol    |
      | OSLP        |
      | OSLP ELSTER |
