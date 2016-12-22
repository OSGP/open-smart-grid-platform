Feature: Stop Device
  As an ...
  I want to ...
  In order to ...

  @OslpMockServer
  Scenario: Stop Device Successfully
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
    And the device returns a stop device response "OK" over OSLP
    When receiving a stop device request
      | DeviceIdentification | TEST1024000000001 |
    Then the stop device async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a stop device OSLP message is sent to device "TEST1024000000001"
    And the platform buffers a stop device response message for device "TEST1024000000001"
      | Result | OK |
