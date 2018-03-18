@PublicLighting @Platform @CoreConfigurationManagement
Feature: CoreConfigurationManagement SetConfiguration
  As a ...
  I want to ...
  In order to ...

  @OslpMockServer
  Scenario Outline: Set configuration of a device
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    And the device returns a set configuration status "OK" over "<Protocol>"
    When receiving a set configuration request
      | DeviceIdentification | TEST1024000000001   |
      | LightType            | <LightType>         |
      | DcLights             | <DcLights>          |
      | DcMap                | <DcMap>             |
      | RelayConf            | <RelayConf>         |
      | PreferredLinkType    | <PreferredLinkType> |
      | MeterType            | <MeterType>         |
      | ShortInterval        | <ShortInterval>     |
      | LongInterval         | <LongInterval>      |
      | IntervalType         | <IntervalType>      |
      | OsgpIpAddress        | <OsgpIpAddress>     |
      | OsgpPort             | <OsgpPort>          |
    Then the set configuration async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set configuration "<Protocol>" message is sent to device "TEST1024000000001"
      | LightType         | <LightType>         |
      | DcLights          | <DcLights>          |
      | DcMap             | <DcMap>             |
      | RelayConf         | <RelayConf>         |
      | PreferredLinkType | <PreferredLinkType> |
      | MeterType         | <MeterType>         |
      | ShortInterval     | <ShortInterval>     |
      | LongInterval      | <LongInterval>      |
      | IntervalType      | <IntervalType>      |
      | OsgpIpAddress     | <OsgpIpAddress>     |
      | OsgpPort          | <OsgpPort>          |
    And the platform buffers a set configuration response message for device "TEST1024000000001"
      | Result | OK |

    Examples: 
      | Protocol    | LightType               | DcLights | DcMap   | RelayConf  | PreferredLinkType | MeterType | ShortInterval | LongInterval | IntervalType | OsgpIpAddress | OsgpPort |
      | OSLP ELSTER | RELAY                   |          |         |            |                   | AUX       |               |              |              | 10.20.30.40   |    12122 |
      | OSLP ELSTER | RELAY                   |          |         | 1,1,TARIFF |                   |           |               |              |              | 10.20.30.40   |    12122 |
      | OSLP ELSTER | RELAY                   |          |         | 1,1,TARIFF |                   | AUX       |               |              |              | 10.20.30.40   |    12122 |
      | OSLP ELSTER | ONE_TO_TEN_VOLT         |          |         |            |                   |           |               |              |              | 10.20.30.40   |    12122 |
      | OSLP ELSTER | ONE_TO_TEN_VOLT_REVERSE |          |         |            |                   |           |               |              |              | 10.20.30.40   |    12122 |
      | OSLP ELSTER | DALI                    |        2 | 1,2;2,1 |            |                   |           |               |              |              | 10.20.30.40   |    12123 |
      | OSLP ELSTER |                         |          |         |            |                   |           |            30 |              |              | 10.20.30.40   |    12123 |
      | OSLP ELSTER |                         |          |         |            | GPRS              |           |               |              |              | 10.20.30.40   |    12123 |
      | OSLP ELSTER | RELAY                   |          |         |            |                   |           |               |              |              | 10.20.30.40   |    12123 |
      | OSLP ELSTER | DALI                    |          |         |            |                   |           |               |              |              | 10.20.30.40   |    12123 |
      | OSLP ELSTER | RELAY                   |          |         | 1,1,LIGHT  |                   |           |               |              |              | 10.20.30.50   |    12122 |
      | OSLP ELSTER |                         |          |         |            |                   |           |               |              |              | 10.20.30.50   |    12122 |
      | OSLP ELSTER |                         |          |         |            |                   | P1        |               |              |              | 10.20.30.50   |    12122 |
      | OSLP ELSTER |                         |          |         |            |                   |           |               |           10 | DAYS         | 10.20.30.50   |    12122 |
      | OSLP ELSTER |                         |          |         |            |                   |           |               |           10 | MONTHS       | 10.20.30.50   |    12122 |
      | OSLP ELSTER | RELAY                   |          |         | 1,1,LIGHT  | CDMA              | PULSE     |            15 |           30 | DAYS         | 10.20.30.50   |    12123 |
      | OSLP ELSTER | RELAY                   |          |         | 1,1,LIGHT  | ETHERNET          | P1        |            15 |            1 | DAYS         | 10.20.30.50   |    12123 |
      | OSLP ELSTER | DALI                    |        1 |     1,1 |            |                   |           |               |              |              | 10.20.30.50   |    12123 |
      | OSLP ELSTER | DALI                    |        2 | 1,1;2,2 |            |                   |           |               |              |              | 10.20.30.50   |    12123 |
      | OSLP ELSTER |                         |          |         |            |                   |           |               |           10 |              | 10.20.30.50   |    12123 |

  Scenario: Set configuration of an unknown device
    When receiving a set configuration request
      | DeviceIdentification | TEST1024000000001 |
      | LightType            | RELAY             |
      | DcLights             |                   |
      | DcMap                |                   |
      | RelayConf            | 1,1,LIGHT         |
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
      | Protocol             | <Protocol>        |
    And the device returns a set configuration status over "<Protocol>"
      | Status | OK |
    When receiving a set configuration request
      | DeviceIdentification | TEST1024000000001 |
      | LightType            | <LightType>       |
      | DcLights             | <DcLights>        |
      | DcMap                | <DcMap>           |
      | RelayConf            | <RelayConf>       |
      | OsgpIpAddress        | <OsgpIpAddress>   |
      | OsgpPort             | <OsgpPort>        |
    Then the set configuration async response contains soap fault
      | FaultCode        | <FaultCode>        |
      | FaultString      | <FaultString>      |
      | ValidationErrors | <ValidationErrors> |

    Examples: 
      | Protocol    | LightType | DcLights | DcMap               | RcType          | RelayConf                                                             | OsgpIpAddress | OsgpPort | FaultCode       | FaultString      | ValidationErrors                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
      | OSLP ELSTER | RELAY     |          |                     |                 |                                                                   1,1 |               |          | SOAP-ENV:Client | Validation error | cvc-complex-type.2.4.b: The content of element 'ns2:RelayMap' is not complete. One of '{"http://www.alliander.com/schemas/osgp/configurationmanagement/2014/10":RelayType}' is expected.                                                                                                                                                                                                                                                                                                                                                                               |
      | OSLP ELSTER | RELAY     |          |                     | TARIFF          | ,,TARIFF                                                              |               |          | SOAP-ENV:Client | Validation error | cvc-minInclusive-valid: Value '0' is not facet-valid with respect to minInclusive '1' for type '#AnonType_IndexRelayMap'.;cvc-type.3.1.3: The value '0' of element 'ns2:Index' is not valid.;cvc-minInclusive-valid: Value '0' is not facet-valid with respect to minInclusive '1' for type '#AnonType_AddressRelayMap'.;cvc-type.3.1.3: The value '0' of element 'ns2:Address' is not valid.                                                                                                                                                                          |
      | OSLP ELSTER | RELAY     |          |                     | TARIFF_REVERSED | ,,TARIFF_REVERSED                                                     |               |          | SOAP-ENV:Client | Validation error | cvc-minInclusive-valid: Value '0' is not facet-valid with respect to minInclusive '1' for type '#AnonType_IndexRelayMap'.;cvc-type.3.1.3: The value '0' of element 'ns2:Index' is not valid.;cvc-minInclusive-valid: Value '0' is not facet-valid with respect to minInclusive '1' for type '#AnonType_AddressRelayMap'.;cvc-type.3.1.3: The value '0' of element 'ns2:Address' is not valid.                                                                                                                                                                          |
      | OSLP ELSTER |           |          |                     |                 |                                                                   1,1 |               |          | SOAP-ENV:Client | Validation error | cvc-complex-type.2.4.b: The content of element 'ns2:RelayMap' is not complete. One of '{"http://www.alliander.com/schemas/osgp/configurationmanagement/2014/10":RelayType}' is expected.                                                                                                                                                                                                                                                                                                                                                                               |
      | OSLP ELSTER |           |          |                     | TARIFF          | ,,TARIFF                                                              |               |          | SOAP-ENV:Client | Validation error | cvc-minInclusive-valid: Value '0' is not facet-valid with respect to minInclusive '1' for type '#AnonType_IndexRelayMap'.;cvc-type.3.1.3: The value '0' of element 'ns2:Index' is not valid.;cvc-minInclusive-valid: Value '0' is not facet-valid with respect to minInclusive '1' for type '#AnonType_AddressRelayMap'.;cvc-type.3.1.3: The value '0' of element 'ns2:Address' is not valid.                                                                                                                                                                          |
      | OSLP ELSTER |           |          |                     | TARIFF_REVERSED | ,,TARIFF_REVERSED                                                     |               |          | SOAP-ENV:Client | Validation error | cvc-minInclusive-valid: Value '0' is not facet-valid with respect to minInclusive '1' for type '#AnonType_IndexRelayMap'.;cvc-type.3.1.3: The value '0' of element 'ns2:Index' is not valid.;cvc-minInclusive-valid: Value '0' is not facet-valid with respect to minInclusive '1' for type '#AnonType_AddressRelayMap'.;cvc-type.3.1.3: The value '0' of element 'ns2:Address' is not valid.                                                                                                                                                                          |
      | OSLP ELSTER | DALI      |        5 | 1,1;2,2;3,3;4,4;5,5 |                 |                                                                       |               |          | SOAP-ENV:Client | Validation error | cvc-maxInclusive-valid: Value '5' is not facet-valid with respect to maxInclusive '4' for type '#AnonType_NumberOfLightsDaliConfiguration'.;cvc-type.3.1.3: The value '5' of element 'ns2:NumberOfLights' is not valid.;cvc-complex-type.2.4.d: Invalid content was found starting with element 'ns2:IndexAddressMap'. No child element is expected at this point.;cvc-maxInclusive-valid: Value '5' is not facet-valid with respect to maxInclusive '4' for type '#AnonType_IndexIndexAddressMap'.;cvc-type.3.1.3: The value '5' of element 'ns2:Index' is not valid. |
      | OSLP ELSTER | RELAY     |          |                     | LIGHT           | 1,1,LIGHT;2,2,LIGHT;3,3,LIGHT;4,4,LIGHT;5,5,LIGHT;6,6,LIGHT;7,7,LIGHT |               |          | SOAP-ENV:Client | Validation error | cvc-complex-type.2.4.d: Invalid content was found starting with element 'ns2:RelayMap'. No child element is expected at this point.;cvc-maxInclusive-valid: Value '7' is not facet-valid with respect to maxInclusive '6' for type '#AnonType_IndexRelayMap'.;cvc-type.3.1.3: The value '7' of element 'ns2:Index' is not valid.                                                                                                                                                                                                                                       |
      | OSLP ELSTER |           |          |                     |                 |                                                                       | 256.20.30.40  |          | SOAP-ENV:Client | Validation error | cvc-pattern-valid: Value '256.20.30.40' is not facet-valid with respect to pattern '((1?[0-9]?[0-9]\|2[0-4][0-9]\|25[0-5]).){3}(1?[0-9]?[0-9]\|2[0-4][0-9]\|25[0-5])' for type 'IPType'.;cvc-type.3.1.3: The value '256.20.30.40' of element 'ns1:OsgpIpAddress' is not valid.                                                                                                                                                                                                                                                                                         |
      | OSLP ELSTER |           |          |                     |                 |                                                                       | 10.20.30.     |          | SOAP-ENV:Client | Validation error | cvc-pattern-valid: Value '10.20.30.' is not facet-valid with respect to pattern '((1?[0-9]?[0-9]\|2[0-4][0-9]\|25[0-5]).){3}(1?[0-9]?[0-9]\|2[0-4][0-9]\|25[0-5])' for type 'IPType'.;cvc-type.3.1.3: The value '10.20.30.' of element 'ns1:OsgpIpAddress' is not valid.                                                                                                                                                                                                                                                                                               |
      | OSLP ELSTER |           |          |                     |                 |                                                                       |               |        0 | SOAP-ENV:Client | Validation error | cvc-minInclusive-valid: Value '0' is not facet-valid with respect to minInclusive '1' for type '#AnonType_OsgpPortNumberConfiguration'.;cvc-type.3.1.3: The value '0' of element 'ns1:OsgpPortNumber' is not valid.                                                                                                                                                                                                                                                                                                                                                    |
      | OSLP ELSTER |           |          |                     |                 |                                                                       |               |    65536 | SOAP-ENV:Client | Validation error | cvc-maxInclusive-valid: Value '65536' is not facet-valid with respect to maxInclusive '65535' for type '#AnonType_OsgpPortNumberConfiguration'.;cvc-type.3.1.3: The value '65536' of element 'ns1:OsgpPortNumber' is not valid.                                                                                                                                                                                                                                                                                                                                        |

  @OslpMockServer
  Scenario Outline: Set configuration data with invalid data
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    And the device returns a set configuration status over "<Protocol>"
      | Status | OK |
    When receiving a set configuration request
      | DeviceIdentification | TEST1024000000001 |
      | LightType            | <LightType>       |
      | DcLights             | <DcLights>        |
      | DcMap                | <DcMap>           |
      | RelayConf            | <RelayConf>       |
      | ShortInterval        | <ShortInterval>   |
      | LongInterval         | <LongInterval>    |
      | IntervalType         | <IntervalType>    |
    Then the set configuration async response contains soap fault
      | Code           |                                                           401 |
      | Message        | VALIDATION_ERROR                                              |
      | Component      | WS_CORE                                                       |
      | InnerException | com.alliander.osgp.domain.core.exceptions.ValidationException |
      | InnerMessage   | Validation Exception, violations: <InnerMessage>;             |

    Examples: 
      | Protocol    | LightType       | DcLights | DcMap | RelayConf           | ShortInterval | LongInterval | IntervalType | InnerMessage                                                                                                                                                                                                      |
      | OSLP ELSTER | RELAY           |        1 |   1,1 |                     |               |              |              | Light type (e.g. relay, dali) must match configuration type (e.g. relay, dali).                                                                                                                                   |
      | OSLP ELSTER | DALI            |          |       | 1,1,LIGHT           |               |              |              | Light type (e.g. relay, dali) must match configuration type (e.g. relay, dali).                                                                                                                                   |
      | OSLP ELSTER | ONE_TO_TEN_VOLT |          |       | 1,1,TARIFF          |               |              |              | Light type (e.g. relay, dali) must match configuration type (e.g. relay, dali).                                                                                                                                   |
      | OSLP ELSTER | ONE_TO_TEN_VOLT |          |       | 1,1,TARIFF_REVERSED |               |              |              | Light type (e.g. relay, dali) must match configuration type (e.g. relay, dali).                                                                                                                                   |
      | OSLP ELSTER |                 |          |       |                     |            12 |              |              | Permissable values for short term history interval minutes are: 15 - 30 - 60 and 240 minutes.                                                                                                                     |
      | OSLP ELSTER |                 |          |       |                     |               |           31 | DAYS         | LongTermInterval and LongTermIntervalType must both be omitted or both be present. Further the permitted range for LongTermIntervalType.DAYS is from 1 to 30 and for LongTermIntervalType.MONTHS is from 1 to 12. |
      | OSLP ELSTER |                 |          |       |                     |               |           13 | MONTHS       | LongTermInterval and LongTermIntervalType must both be omitted or both be present. Further the permitted range for LongTermIntervalType.DAYS is from 1 to 30 and for LongTermIntervalType.MONTHS is from 1 to 12. |

  @OslpMockServer
  Scenario Outline: Failed set configuration of a device
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    And the device returns a set configuration status over "<Protocol>"
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
    When receiving a set configuration request
      | DeviceIdentification | TEST1024000000001 |
    Then the set configuration async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set configuration "<Protocol>" message is sent to device "TEST1024000000001"
      | LightType         | LT_NOT_SET     |
      | DcLights          |                |
      | DcMap             |                |
      | RelayConf         | 1,1,LIGHT      |
      | PreferredLinkType | LINK_NOT_SET   |
      | MeterType         | MT_NOT_SET     |
      | ShortInterval     |             15 |
      | LongInterval      |             30 |
      | IntervalType      | LT_INT_NOT_SET |
    And the platform buffers a set configuration response message for device "TEST1024000000001" contains soap fault
      | Message | Device reports failure |

    Examples: 
      | Protocol    |
      | OSLP ELSTER |

  @OslpMockServer
  Scenario Outline: Rejected set configuration of a device
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    And the device returns a set configuration status over "<Protocol>"
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
    When receiving a set configuration request
      | DeviceIdentification | TEST1024000000001 |
    Then the set configuration async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set configuration "<Protocol>" message is sent to device "TEST1024000000001"
      | LightType         | LT_NOT_SET     |
      | DcLights          |                |
      | DcMap             |                |
      | RelayConf         | 1,1,LIGHT      |
      | PreferredLinkType | LINK_NOT_SET   |
      | MeterType         | MT_NOT_SET     |
      | ShortInterval     |             15 |
      | LongInterval      |             30 |
      | IntervalType      | LT_INT_NOT_SET |
    And the platform buffers a set configuration response message for device "TEST1024000000001" contains soap fault
      | Message | Device reports rejected |

    Examples: 
      | Protocol    |
      | OSLP ELSTER |
