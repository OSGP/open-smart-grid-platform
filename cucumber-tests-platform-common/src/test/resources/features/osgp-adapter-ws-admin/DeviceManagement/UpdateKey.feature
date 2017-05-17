@PublicLighting @Platform @AdminDeviceManagement
Feature: AdminDeviceManagement Update Key
  As a ...
  I want to be able to perform DeviceManagement operations on a device
  In order to ...

  Scenario: Update Key For Device
    Given an ssld device
      | DeviceIdentification | TEST1024000000001 |
    When receiving an update key request
      | DeviceIdentification | TEST1024000000001 |
      | PublicKey            | abcdef123456      |
    Then the update key response contains
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Update Key with unknown device identification
    When receiving an update key request
      | DeviceIdentification | TEST1024000000002 |
      | PublicKey            | abcdef123456      |
    Then the update key response contains
      | DeviceIdentification | TEST1024000000002 |

  Scenario Outline: Update Key For Device With Invalid Public Key
    Given an ssld device
      | DeviceIdentification | TEST1024000000001 |
    When receiving an update key request
      | DeviceIdentification | TEST1024000000001 |
      | PublicKey            | <PublicKey>       |
    Then the update key response contains soap fault
      | FaultCode    | SOAP-ENV:Server                                       |
      | FaultString  | VALIDATION_ERROR                                      |
      | InnerMessage | Validation Exception, violations: Invalid public key; |

    Examples: 
      | PublicKey |
      |           |
      |        10 |
