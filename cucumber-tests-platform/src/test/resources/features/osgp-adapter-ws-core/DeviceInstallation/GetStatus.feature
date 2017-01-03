Feature: Get Status
  As a ...
  I want to ...
  In order to ...

  Scenario: Get status of a device
    Given a device
      | DeviceIdentification | TEST1024000000001 |
    When receiving a device installation get status request
      | DeviceIdentification | TEST1024000000001 |
    Then the device installation get status async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a get status OSLP message is sent to device "TEST1024000000001"
    And the platform buffers a device installation get status response message for device "TEST1024000000001"
      | Result | OK |
