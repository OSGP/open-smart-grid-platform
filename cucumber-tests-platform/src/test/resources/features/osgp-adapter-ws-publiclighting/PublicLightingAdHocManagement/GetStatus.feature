Feature: Adhoc Management
  In order to ... 
  As a platform 
  I want to asynchronously handle set light requests
  
  @OslpMockServer
  Scenario Outline: Get Status Values
    Given an oslp device
      | DeviceIdentification | <DeviceIdentification> |
      | Status               | <Status>               |
    And the device returns a get status response "<Result>" over OSLP
    When receiving a get status request
      | DeviceIdentification | <DeviceIdentification> |
    Then the get status async response contains
      | DeviceIdentification | <DeviceIdentification> |
    And a get status OSLP message is sent to device "<DeviceIdentification>"
    And the platform buffers a get status response message for device "<DeviceIdentification>"
      | Result | <Result> |

    Examples: 
      | DeviceIdentification | Status | Result |
      | TEST1024000000001    | active | OK     |

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
