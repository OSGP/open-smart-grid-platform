Feature: AdminDeviceManagement Device Removal
  As a ...
  I want to be able to perform DeviceManagement operations on a device
  In order to ...

  # Note: This scenario doesn't work yet
  #Scenario Outline: Remove a device
  #Given a device
  #| DeviceIdentification | <DeviceIdentification> |
  #When receiving a remove device request
  #| DeviceIdentification | <DeviceIdentification> |
  #Then the remove device response is successful
  #And the device with id "<DeviceIdentification>" does not exists
  #
  #Examples:
  #| DeviceIdentification |
  #| TEST1024000000001    |
  #
  #Scenario: Remove a device when not authorized
  #Given a device
  #| DeviceIdentification | TEST1024000000001 |
  #| DeviceFunctionGroup  | INSTALLATION      |
  #When receiving a remove device request
  #| DeviceIdentification | TEST1024000000001 |
  #Then the remove device response contains soap fault
  #| FaultCode    | SOAP-ENV:Server                                      |
  #| FaultString  | UNAUTHORIZED                                         |
  #| InnerMessage | Organisation [test-org] is not authorized for action |
  #
  Scenario: Remove a device with unknown device identification
    When receiving a remove device request with unknown device identification
      | DeviceIdentification | unknown |
    Then the remove device response contains soap fault
      | FaultCode    | SOAP-ENV:Server |
      | FaultString  | UNKNOWN_DEVICE  |
      | InnerMessage |                 |

  Scenario: Remove a device with empty device identification
    When receiving a remove device request with empty device identification
      | DeviceIdentification |  |
    Then the remove device response contains soap fault
      | FaultCode    | SOAP-ENV:Server                                      |
      | FaultString  | UNAUTHORIZED                                         |
      | InnerMessage | Organisation [test-org] is not authorized for action |
