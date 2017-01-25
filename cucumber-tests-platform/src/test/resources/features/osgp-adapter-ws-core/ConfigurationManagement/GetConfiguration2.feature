Feature: ConfigurationManagement GetConfiguration
  As a ...
  I want to ...
  In order to ...

  #@OslpMockServer
  #Scenario: Failed get configuration of a device
    #Given an oslp device
      #| DeviceIdentification | TEST1024000000001 |
      #| DeviceType           | SSLD              |
    #And the device returns configuration status over OSLP
      #| Status            | FAILURE  |
      #| LightType         | RELAY    |
      #| DcLights          |          |
      #| DcMap             |          |
      #| RcType            | LIGHT    |
      #| RcMap             |      1,1 |
      #| PreferredLinkType | ETHERNET |
      #| MeterType         | P1       |
      #| ShortInterval     |       15 |
      #| LongInterval      |       30 |
      #| IntervalType      | DAYS     |
    #When receiving a get configuration request
      #| DeviceIdentification | TEST1024000000001 |
    #Then the get configuration async response contains
      #| DeviceIdentification | TEST1024000000001 |
    #And a get configuration OSLP message is sent to device "TEST1024000000001"
    #And the platform buffers a get configuration response message for device "TEST1024000000001" contains soap fault
      #| Message | Device reports failure1 |

  @OslpMockServer
  Scenario: Rejected get configuration of a device
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SSLD              |
    And the device returns configuration status over OSLP
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
    When receiving a get configuration request
      | DeviceIdentification | TEST1024000000001 |
    Then the get configuration async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a get configuration OSLP message is sent to device "TEST1024000000001"
    And the platform buffers a get configuration response message for device "TEST1024000000001" contains soap fault
      | Message | Device reports rejected1 |
