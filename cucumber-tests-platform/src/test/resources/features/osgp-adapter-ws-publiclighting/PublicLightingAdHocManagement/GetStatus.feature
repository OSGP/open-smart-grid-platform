Feature: Adhoc Management
  In order to ... 
  As a platform 
  I want to asynchronously handle set light requests

  @OslpMockServer
  Scenario Outline: Get Status from a device
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Status               | Active            |
      | RelayType            | <RelayType>       |
    And the device returns a get status response over OSLP
      | PreferredLinkType  | <PreferredLinkType>  |
      | ActualLinkType     | <ActualLinkType>     |
      | LightType          | <LightType>          |
      | EventNotifications | <EventNotifications> |
      | Index              | <Index>              |
      | On                 | <On>                 |
      | DimValue           | <DimValue>           |
    When receiving a get status request
      | DeviceIdentification | TEST1024000000001 |
    Then the get status async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a get status OSLP message is sent to device "TEST1024000000001"
    And the platform buffers a get status response message for device "TEST1024000000001"
      | Result | <Result> |

    Examples: 
      | RelayType | PreferredLinkType | ActualLinkType | LightType  | EventNotifications | Index | On   | DimValue | Result |
      | LIGHT     | LINK_NOT_SET      | LINK_NOT_SET   | LT_NOT_SET |                    |     0 | true |          | OK     |

  Scenario Outline: Fail To Get Status Values
    Given a device
      | DeviceIdentification | TEST1024000000001 |
    When receiving a get status request
      | DeviceIdentification | <DeviceIdentification> |
    Then the get status response contains soap fault
      | Message | <Message> |

    Examples: 
      | DeviceIdentification | Message        |
      | unknown              | UNKNOWN_DEVICE |

  @OslpMockServer
  Scenario Outline: Get Status Values From A Device With Multiple Lights
    Given an oslp device
      | DeviceIdentification | <DeviceIdentification> |
    And the device returns a get status response over OSLP
      | Result | <OSLPResult> |
    When receiving a get status request
      | DeviceIdentification | <DeviceIdentification> |
    Then the get status async response contains
      | DeviceIdentification | <DeviceIdentification> |
    And a get status OSLP message is sent to device "<DeviceIdentification>"
    And the platform buffers a get status response message for device "<DeviceIdentification>"
      | Result | <Result> |

    Examples: 
      | DeviceIdentification | On   | DimValue | OSLPResult                     | Result |
      | TEST1024000000001    | true |        1 | 1,1,TARIFF;2,2,LIGHT;3,3,LIGHT | OK     |
