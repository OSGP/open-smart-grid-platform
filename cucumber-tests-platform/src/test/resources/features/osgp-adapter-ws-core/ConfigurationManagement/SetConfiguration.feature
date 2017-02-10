Feature: CoreConfigurationManagement SetConfiguration
  As a ...
  I want to ...
  In order to ...

  @OslpMockServer
  Scenario Outline: Set configuration of a device
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
    And the device returns a set configuration status "OK" over OSLP
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
      | LightType               | DcLights | DcMap   | RcType          | RcMap | PreferredLinkType | MeterType | ShortInterval | LongInterval | IntervalType |
      | RELAY                   |          |         |                 |       |                   | AUX       |               |              |              |
      | RELAY                   |          |         | TARIFF          |   1,1 |                   |           |               |              |              |
      | RELAY                   |          |         | TARIFF_REVERSED |   1,1 |                   | AUX       |               |              |              |
      | ONE_TO_TEN_VOLT         |          |         |                 |       |                   |           |               |              |              |
      | ONE_TO_TEN_VOLT_REVERSE |          |         |                 |       |                   |           |               |              |              |
      | DALI                    |        2 | 1,2;2,1 |                 |       |                   |           |               |              |              |
      |                         |          |         |                 |       |                   |           |            30 |              |              |
      |                         |          |         |                 |       | GPRS              |           |               |              |              |
      | RELAY                   |          |         |                 |       |                   |           |               |              |              |
      | DALI                    |          |         |                 |       |                   |           |               |              |              |
      | RELAY                   |          |         | LIGHT           |   1,1 |                   |           |               |              |              |
      |                         |          |         |                 |       |                   |           |               |              |              |
      |                         |          |         |                 |       |                   | P1        |               |              |              |
      |                         |          |         |                 |       |                   |           |               |           10 | DAYS         |
      |                         |          |         |                 |       |                   |           |               |           10 | MONTHS       |
      | RELAY                   |          |         | LIGHT           |   1,1 | CDMA              | PULSE     |            15 |           30 | DAYS         |
      | RELAY                   |          |         | LIGHT           |   1,1 | ETHERNET          | P1        |            15 |            1 | DAYS         |
      | DALI                    |        2 |     1,1 |                 |       |                   |           |               |              |              |
      | DALI                    |        1 | 1,1;2,2 |                 |       |                   |           |               |              |              |
      |                         |        1 |         | LIGHT           |   1,1 |                   |           |               |              |              |
      | ONE_TO_TEN_VOLT         |        1 |         |                 |       |                   |           |               |              |              |
      |                         |          |         |                 |       |                   |           |               |           10 |              |

  Scenario: Set configuration of an unknown device
    When receiving a set configuration request
      | DeviceIdentification | TEST1024000000001 |
      | LightType            | RELAY             |
      | DcLights             |                   |
      | DcMap                |                   |
      | RcType               | LIGHT             |
      | RcMap                |               1,1 |
      | PreferredLinkType    | ETHERNET          |
      | MeterType            | P1                |
      | ShortInterval        |                15 |
      | LongInterval         |                 1 |
      | IntervalType         | DAYS              |
    Then the get configuration async response contains soap fault
      | Message | UNKNOWN_DEVICE |

@OslpMockServer
  Scenario Outline: Set configuration data with invalid data which result in validation errors
    Given an ssld oslp device
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
    Then the set configuration async response contains soap fault
      | FaultCode        | <FaultCode>        |
      | FaultString      | <FaultString>      |
      | ValidationErrors | <ValidationErrors> |

    Examples: 
      | LightType | DcLights | DcMap               | RcType          | RcMap                       | ShortInterval | PreferredLinkType | MeterType | LongInterval | IntervalType | FaultCode       | FaultString      | ValidationErrors                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
      | RELAY     |          |                     |                 |                         1,1 |               |                   |           |              |              | SOAP-ENV:Client | Validation error | cvc-complex-type.2.4.b: The content of element 'ns2:RelayMap' is not complete. One of '{"http://www.alliander.com/schemas/osgp/configurationmanagement/2014/10":RelayType}' is expected.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
      | RELAY     |          |                     | TARIFF          |                             |               |                   |           |              |              | SOAP-ENV:Client | Validation error | cvc-minInclusive-valid: Value '0' is not facet-valid with respect to minInclusive '1' for type '#AnonType_IndexRelayMap'.;cvc-type.3.1.3: The value '0' of element 'ns2:Index' is not valid.;cvc-minInclusive-valid: Value '0' is not facet-valid with respect to minInclusive '1' for type '#AnonType_AddressRelayMap'.;cvc-type.3.1.3: The value '0' of element 'ns2:Address' is not valid.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
      | RELAY     |          |                     | TARIFF_REVERSED |                             |               |                   |           |              |              | SOAP-ENV:Client | Validation error | cvc-minInclusive-valid: Value '0' is not facet-valid with respect to minInclusive '1' for type '#AnonType_IndexRelayMap'.;cvc-type.3.1.3: The value '0' of element 'ns2:Index' is not valid.;cvc-minInclusive-valid: Value '0' is not facet-valid with respect to minInclusive '1' for type '#AnonType_AddressRelayMap'.;cvc-type.3.1.3: The value '0' of element 'ns2:Address' is not valid.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
      |           |          |                     |                 |                         1,1 |               |                   |           |              |              | SOAP-ENV:Client | Validation error | cvc-complex-type.2.4.b: The content of element 'ns2:RelayMap' is not complete. One of '{"http://www.alliander.com/schemas/osgp/configurationmanagement/2014/10":RelayType}' is expected.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
      |           |          |                     | TARIFF          |                             |               |                   |           |              |              | SOAP-ENV:Client | Validation error | cvc-minInclusive-valid: Value '0' is not facet-valid with respect to minInclusive '1' for type '#AnonType_IndexRelayMap'.;cvc-type.3.1.3: The value '0' of element 'ns2:Index' is not valid.;cvc-minInclusive-valid: Value '0' is not facet-valid with respect to minInclusive '1' for type '#AnonType_AddressRelayMap'.;cvc-type.3.1.3: The value '0' of element 'ns2:Address' is not valid.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
      |           |          |                     | TARIFF_REVERSED |                             |               |                   |           |              |              | SOAP-ENV:Client | Validation error | cvc-minInclusive-valid: Value '0' is not facet-valid with respect to minInclusive '1' for type '#AnonType_IndexRelayMap'.;cvc-type.3.1.3: The value '0' of element 'ns2:Index' is not valid.;cvc-minInclusive-valid: Value '0' is not facet-valid with respect to minInclusive '1' for type '#AnonType_AddressRelayMap'.;cvc-type.3.1.3: The value '0' of element 'ns2:Address' is not valid.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
      | DALI      |        5 | 1,1;2,2;3,3;4,4;5,5 |                 |                             |               |                   |           |              |              | SOAP-ENV:Client | Validation error | cvc-maxInclusive-valid: Value '5' is not facet-valid with respect to maxInclusive '4' for type '#AnonType_NumberOfLightsDaliConfiguration'.;cvc-type.3.1.3: The value '5' of element 'ns2:NumberOfLights' is not valid.;cvc-complex-type.2.4.d: Invalid content was found starting with element 'ns2:IndexAddressMap'. No child element is expected at this point.;cvc-maxInclusive-valid: Value '5' is not facet-valid with respect to maxInclusive '4' for type '#AnonType_IndexIndexAddressMap'.;cvc-type.3.1.3: The value '5' of element 'ns2:Index' is not valid.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
      | RELAY     |          |                     | LIGHT           | 1,1;2,2;3,3;4,4;5,5;6,6;7,7 |               |                   |           |              |              | SOAP-ENV:Client | Validation error | cvc-maxInclusive-valid: Value '7' is not facet-valid with respect to maxInclusive '6' for type '#AnonType_IndexRelayMap'.;cvc-type.3.1.3: The value '7' of element 'ns2:Index' is not valid.;cvc-maxInclusive-valid: Value '7' is not facet-valid with respect to maxInclusive '6' for type '#AnonType_IndexRelayMap'.;cvc-type.3.1.3: The value '7' of element 'ns2:Index' is not valid.;cvc-maxInclusive-valid: Value '7' is not facet-valid with respect to maxInclusive '6' for type '#AnonType_IndexRelayMap'.;cvc-type.3.1.3: The value '7' of element 'ns2:Index' is not valid.;cvc-maxInclusive-valid: Value '7' is not facet-valid with respect to maxInclusive '6' for type '#AnonType_IndexRelayMap'.;cvc-type.3.1.3: The value '7' of element 'ns2:Index' is not valid.;cvc-maxInclusive-valid: Value '7' is not facet-valid with respect to maxInclusive '6' for type '#AnonType_IndexRelayMap'.;cvc-type.3.1.3: The value '7' of element 'ns2:Index' is not valid.;cvc-maxInclusive-valid: Value '7' is not facet-valid with respect to maxInclusive '6' for type '#AnonType_IndexRelayMap'.;cvc-type.3.1.3: The value '7' of element 'ns2:Index' is not valid.;cvc-complex-type.2.4.d: Invalid content was found starting with element 'ns2:RelayMap'. No child element is expected at this point.;cvc-maxInclusive-valid: Value '7' is not facet-valid with respect to maxInclusive '6' for type '#AnonType_IndexRelayMap'.;cvc-type.3.1.3: The value '7' of element 'ns2:Index' is not valid. |

@OslpMockServer
  Scenario Outline: Set configuration data with invalid data
    Given an ssld oslp device
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
    Then the set configuration async response contains soap fault
      | Code           |                                                           401 |
      | Message        | VALIDATION_ERROR                                              |
      | Component      | WS_CORE                                                       |
      | InnerException | com.alliander.osgp.domain.core.exceptions.ValidationException |
      | InnerMessage   | Validation Exception, violations: <InnerMessage>;             |

    Examples: 
      | LightType       | DcLights | DcMap | RcType          | RcMap | ShortInterval | PreferredLinkType | MeterType | LongInterval | IntervalType | InnerMessage                                                                                                                                                                                                      |
      | RELAY           |        1 |   1,1 |                 |       |               |                   |           |              |              | Light type (e.g. relay, dali) must match configuration type (e.g. relay, dali).                                                                                                                                   |
      | DALI            |          |       | LIGHT           |   1,1 |               |                   |           |              |              | Light type (e.g. relay, dali) must match configuration type (e.g. relay, dali).                                                                                                                                   |
      | ONE_TO_TEN_VOLT |          |       | TARIFF          |   1,1 |               |                   |           |              |              | Light type (e.g. relay, dali) must match configuration type (e.g. relay, dali).                                                                                                                                   |
      | ONE_TO_TEN_VOLT |          |       | TARIFF_REVERSED |   1,1 |               |                   |           |              |              | Light type (e.g. relay, dali) must match configuration type (e.g. relay, dali).                                                                                                                                   |
      |                 |          |       |                 |       |            12 |                   |           |              |              | Permissable values for short term history interval minutes are: 15 - 30 - 60 and 240 minutes.                                                                                                                     |
      |                 |          |       |                 |       |               |                   |           |           31 | DAYS         | LongTermInterval and LongTermIntervalType must both be omitted or both be present. Further the permitted range for LongTermIntervalType.DAYS is from 1 to 30 and for LongTermIntervalType.MONTHS is from 1 to 12. |
      |                 |          |       |                 |       |               |                   |           |           13 | MONTHS       | LongTermInterval and LongTermIntervalType must both be omitted or both be present. Further the permitted range for LongTermIntervalType.DAYS is from 1 to 30 and for LongTermIntervalType.MONTHS is from 1 to 12. |

  @OslpMockServer
  Scenario: Failed set configuration of a device
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
    And the device returns a set configuration status over OSLP
      | Status            | FAILURE  |
      | LightType         | RELAY    |
      | DcLights          |          |
      | DcMap             |          |
      | RcType            | LIGHT    |
      | RcMap             |      1,1 |
      | PreferredLinkType | ETHERNET |
      | MeterType         | P1       |
      | ShortInterval     |       15 |
      | LongInterval      |       30 |
      | IntervalType      | DAYS     |
    When receiving a set configuration request
      | DeviceIdentification | TEST1024000000001 |
    Then the set configuration async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set configuration OSLP message is sent to device "TEST1024000000001"
    And the platform buffers a set configuration response message for device "TEST1024000000001" contains soap fault
      | Message | Device reports failure |

  @OslpMockServer
  Scenario: Rejected set configuration of a device
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
    And the device returns a set configuration status over OSLP
      | Status            | REJECTED |
      | LightType         | RELAY    |
      | DcLights          |          |
      | DcMap             |          |
      | RcType            | LIGHT    |
      | RcMap             |      1,1 |
      | PreferredLinkType | ETHERNET |
      | MeterType         | P1       |
      | ShortInterval     |       15 |
      | LongInterval      |       30 |
      | IntervalType      | DAYS     |
    When receiving a set configuration request
      | DeviceIdentification | TEST1024000000001 |
    Then the set configuration async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set configuration OSLP message is sent to device "TEST1024000000001"
    And the platform buffers a set configuration response message for device "TEST1024000000001" contains soap fault
      | Message | Device reports rejected |
