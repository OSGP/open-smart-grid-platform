Feature: ConfigurationManagement GetConfiguration
  As a ...
  I want to ...
  In order to ...

  @OslpMockServer
  Scenario: Get configuration of a device
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Active               | true              |
    And the device returns configuration status "OK" over OSLP
    When receiving a get configuration request
      | DeviceIdentification | TEST1024000000001 |
    Then the get configuration async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a get configuration OSLP message is sent to device "TEST1024000000001"
    And the platform buffers a get configuration response message for device "TEST1024000000001"
      | Result | OK |
