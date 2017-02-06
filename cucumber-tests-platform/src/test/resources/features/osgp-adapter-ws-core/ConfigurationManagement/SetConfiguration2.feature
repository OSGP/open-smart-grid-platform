Feature: CoreConfigurationManagement SetConfiguration
  As a ...
  I want to ...
  In order to ...

  @OslpMockServer
  Scenario Outline: Set configuration of a device
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
    And the device returns a set configuration status over OSLP
      | Status | OK |
    When receiving a set configuration request
      | DeviceIdentification | TEST1024000000001   |
      | LightType            | <LightType>         |
      | DcLights             | <DcLights>          |
      | DcMap                | <DcMap>             |
      | RcType               | <RcType>            |
      | RcMap                | <RcMap>             |
      | PreferredLinkType    | <PreferredLinkType> |
      | MeterType            | <MeterType>         |
      | ShortInterval        | <ShortInterval>     |
      | LongInterval         | <LongInterval>      |
      | IntervalType         | <IntervalType>      |
    Then the set configuration async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set configuration OSLP message is sent to device "TEST1024000000001"
    And the platform buffers a set configuration response message for device "TEST1024000000001"
      | Result | OK |

    Examples: 
      | LightType | DcLights | DcMap | RcType          | RcMap | PreferredLinkType | MeterType | ShortInterval | LongInterval | IntervalType |
      | RELAY     |          |       | TARIFF_REVERSED |   1,1 |                   | AUX       |               |              |              |
      | RELAY     |          |       |                 |       |                   |           |               |              |              |

  #Scenario Outline: Set configuration data with invalid data
    #Given an oslp device
      #| DeviceIdentification | TEST1024000000001 |
    #And the device returns a set configuration status over OSLP
      #| Status | OK |
    #When receiving a set configuration request
      #| DeviceIdentification | TEST1024000000001   |
      #| LightType            | <LightType>         |
      #| DcLights             | <DcLights>          |
      #| DcMap                | <DcMap>             |
      #| RcType               | <RcType>            |
      #| RcMap                | <RcMap>             |
      #| PreferredLinkType    | <PreferredLinkType> |
      #| MeterType            | <MeterType>         |
      #| ShortInterval        | <ShortInterval>     |
      #| LongInterval         | <LongInterval>      |
      #| IntervalType         | <IntervalType>      |
    #Then the set configuration async response contains soap fault
      #| Code           |                                                           401 |
      #| Message        | VALIDATION_ERROR                                              |
      #| Component      | WS_CORE                                                       |
      #| InnerException | com.alliander.osgp.domain.core.exceptions.ValidationException |
      #| InnerMessage   | Validation Exception, violations: <InnerMessage>;             |
#
    #Examples: 
      #| LightType       | DcLights | DcMap   | RcType | RcMap | ShortInterval | PreferredLinkType | MeterType | LongInterval | IntervalType | InnerMessage |
      #| DALI            |        2 |     1,1 |        |       |               |                   |           |              |              |              |
      #| DALI            |        1 | 1,1;2,2 |        |       |               |                   |           |              |              |              |
      #|                 |        1 |         | LIGHT  |   1,1 |               |                   |           |              |              |              |
      #| ONE_TO_TEN_VOLT |        1 |         |        |       |               |                   |           |              |              |              |
      #|                 |          |         |        |       |               |                   |           |           10 |              |              |
