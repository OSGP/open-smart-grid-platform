Feature: ConfigurationManagement GetConfiguration
  As a ...
  I want to ...
  In order to ...

  @OslpMockServer
  Scenario Outline: Get configuration of a device
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SSLD              |
    And the device returns configuration status over OSLP
      | Status            | OK                  |
      | LightType         | <LightType>         |
      | DcLights          | <DcLights>          |
      | DcMap             | <DcMap>             |
      | RcType            | <RcType>            |
      | RcMap             | <RcMap>             |
      | PreferredLinkType | <PreferredLinkType> |
      | MeterType         | <MeterType>         |
      | ShortInterval     | <ShortInterval>     |
      | LongInterval      | <LongInterval>      |
      | IntervalType      | <IntervalType>      |
    When receiving a get configuration request
      | DeviceIdentification | TEST1024000000001 |
    Then the get configuration async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a get configuration OSLP message is sent to device "TEST1024000000001"
    And the platform buffers a get configuration response message for device "TEST1024000000001"
      | Result            | OK                  |
      | Description       |                     |
      | LightType         | <LightType>         |
      | DcLights          | <DcLights>          |
      | DcMap             | <DcMap>             |
      | RcType            | <RcType>            |
      | RcMap             | <RcMap>             |
      | PreferredLinkType | <PreferredLinkType> |
      | MeterType         | <MeterType>         |
      | ShortInterval     | <ShortInterval>     |
      | LongInterval      | <LongInterval>      |
      | IntervalType      | <IntervalType>      |

    Examples: 
      | LightType               | DcLights | DcMap   | RcType | RcMap | PreferredLinkType | MeterType | ShortInterval | LongInterval | IntervalType |
      | RELAY                   |          |         |        |       |                   | AUX       |               |              |              |
      | RELAY                   |          |         | TARIFF |   1,1 |                   |           |               |              |              |
      | ONE_TO_TEN_VOLT         |          |         |        |       |                   |           |               |              |              |
      | ONE_TO_TEN_VOLT_REVERSE |          |         |        |       |                   |           |               |              |              |
      | DALI                    |        2 | 1,2;2,1 |        |       |                   |           |               |              |              |
      |                         |          |         |        |       |                   |           |            30 |              |              |
      |                         |          |         |        |       | GPRS              |           |               |              |              |
      | DALI                    |          |         |        |       |                   |           |               |              |              |
      | RELAY                   |          |         | LIGHT  |   1,1 |                   |           |               |              |              |
      |                         |          |         |        |       |                   |           |               |              |              |
      |                         |          |         |        |       |                   | P1        |               |              |              |
      |                         |          |         |        |       |                   |           |               |           10 | DAYS         |
      |                         |          |         |        |       |                   |           |               |           10 | MONTHS       |
      | RELAY                   |          |         | LIGHT  |       | CDMA              | PULSE     |            15 |           30 | DAYS         |
      | RELAY                   |          |         | LIGHT  |   1,1 | ETHERNET          | P1        |            15 |            1 | DAYS         |

  #@OslpMockServer
  #Scenario Outline: Fail to get configuration of a device
    #Given an oslp device
      #| DeviceIdentification | TEST1024000000001 |
      #| DeviceType           | SSLD              |
    #And the device returns configuration status over OSLP
      #| Status            | FAILURE             |
      #| LightType         | <LightType>         |
      #| DcLights          | <DcLights>          |
      #| DcMap             | <DcMap>             |
      #| RcType            | <RcType>            |
      #| RcMap             | <RcMap>             |
      #| PreferredLinkType | <PreferredLinkType> |
      #| MeterType         | <MeterType>         |
      #| ShortInterval     | <ShortInterval>     |
      #| LongInterval      | <LongInterval>      |
      #| IntervalType      | <IntervalType>      |
    #When receiving a get configuration request
      #| DeviceIdentification | TEST1024000000001 |
    #Then the get configuration async response contains
      #| DeviceIdentification | TEST1024000000001 |
    #And a get configuration OSLP message is sent to device "TEST1024000000001"
    #And the platform buffers a get configuration response message for device "TEST1024000000001"
      #| Result      | FAILURE |
#
  #@OslpMockServer
  #Scenario Outline: Fail to get configuration of a device
    #Given an oslp device
      #| DeviceIdentification | TEST1024000000001 |
      #| DeviceType           | SSLD              |
    #And the device returns configuration status over OSLP
      #| Status            | REJECTED            |
      #| LightType         | <LightType>         |
      #| DcLights          |                     |
      #| DcMap             |                     |
      #| RcType            |                     |
      #| RcMap             |                     |
      #| PreferredLinkType | <PreferredLinkType> |
      #| MeterType         | <MeterType>         |
      #| ShortInterval     | <ShortInterval>     |
      #| LongInterval      | <LongInterval>      |
      #| IntervalType      | <IntervalType>      |
    #When receiving a get configuration request
      #| DeviceIdentification | TEST1024000000001 |
    #Then the get configuration async response contains
      #| DeviceIdentification | TEST1024000000001 |
    #And a get configuration OSLP message is sent to device "TEST1024000000001"
    #And the platform buffers a get configuration response message for device "TEST1024000000001"
      #| Result      | REJECTED |
