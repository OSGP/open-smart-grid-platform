# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@Common @Platform @AdminDeviceManagement
Feature: AdminDeviceManagement Device Removal
  As a ...
  I want to be able to perform DeviceManagement operations on a device
  In order to ...

  Scenario Outline: Remove a device
    Given a device
      | DeviceIdentification | <DeviceIdentification> |
    When receiving a remove device request
      | DeviceIdentification | <DeviceIdentification> |
    Then the remove device response is successful
    And the device with id "<DeviceIdentification>" should be removed

    Examples: 
      | DeviceIdentification |
      | TEST1024000000001    |
      | TEST1024000000002    |
      | TEST1024000000003    |
      | TEST1024000000004    |
      | TEST1024000000005    |
      | TEST1024000000006    |

  Scenario: Remove a device when not authorized
    Given a device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceFunctionGroup  | INSTALLATION      |
    When receiving a remove device request
      | DeviceIdentification | TEST1024000000001 |
    Then the remove device response contains soap fault
      | FaultCode    | SOAP-ENV:Server                                      |
      | FaultString  | UNAUTHORIZED                                         |
      | InnerMessage | Organisation [test-org] is not authorized for action |

  Scenario: Remove a device with unknown device identification
    When receiving a remove device request with unknown device identification
      | DeviceIdentification | unknown |
    Then the remove device response contains soap fault
      | FaultCode    | SOAP-ENV:Server                              |
      | FaultString  | UNKNOWN_DEVICE                               |

  Scenario: Remove a device with empty device identification
    When receiving a remove device request with empty device identification
      | DeviceIdentification |  |
    Then the remove device response contains soap fault
      | FaultCode        | SOAP-ENV:Client                                                                                                                                                                                              |
      | FaultString      | Validation error                                                                                                                                                                                             |
      | ValidationErrors | cvc-minLength-valid: Value '' with length = '0' is not facet-valid with respect to minLength '1' for type 'Identification'.;cvc-type.3.1.3: The value '' of element 'ns2:DeviceIdentification' is not valid. |
