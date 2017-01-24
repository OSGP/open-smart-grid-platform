Feature: CoreDeviceInstallation Get Status
  As a ...
  I want to be able to perform DeviceInstallation operations on a device
  In order to ...

  @Skip
  Scenario: Get status of a device
    Given a device
      | DeviceIdentification | TEST1024000000001 |
    And the device returns a get status response "OK" over OSLP
    When receiving a device installation get status request
      | DeviceIdentification | TEST1024000000001 |
    Then the device installation get status async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a get status OSLP message is sent to device "TEST1024000000001"
    And the platform buffers a device installation get status response message for device "TEST1024000000001"
      | Result | OK |
