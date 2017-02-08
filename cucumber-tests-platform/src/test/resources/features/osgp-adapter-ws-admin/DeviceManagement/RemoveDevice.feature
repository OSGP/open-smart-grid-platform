Feature: AdminDeviceManagement Device Removal
  As a ...
  I want to be able to perform DeviceManagement operations on a device
  In order to ...

  Scenario Outline: Remove A Device
    Given a device
      | DeviceIdentification | <DeviceIdentification> |
    When receiving a remove device request
      | DeviceIdentification | <DeviceIdentification> |
    Then the remove device response is successful
    And the device with id "<DeviceIdentification>" does not exists

    Examples: 
      | DeviceIdentification |
      | TEST1024000000001    |
      #
      # Note: Unauthorized test with a 'DeviceFunctionGroup' other than 'OWNER'
