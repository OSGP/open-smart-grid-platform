Feature: Start Device
  As a grid operator
  I want to ...
  In order to ...

  @OslpMockServer
  Scenario: Start Device Successfully
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
    And the device returns a start device response "OK" over OSLP
    When receiving a start device test request
      | DeviceIdentification | TEST1024000000001 |
    Then the start device async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a start device OSLP message is sent to device "TEST1024000000001"
    And the platform buffers a start device response message for device "TEST1024000000001"
      | Result | OK |