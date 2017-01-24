Feature: AdminDeviceManagement Update Key
  As a ...
  I want to be able to perform DeviceManagement operations on a device
  In order to ...

  Scenario: Update Key For Device
    Given a device
      | DeviceIdentification | TEST1024000000001 |
    When receiving an update key request
      | DeviceIdentification | TEST1024000000001 |
      | PublicKey            | abcdef123456      |
    Then the update key response contains
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Update Key For Not Existing Device
    When receiving an update key request
      | DeviceIdentification | TEST1024000000002 |
      | PublicKey            | abcdef123456      |
    Then the update key response contains
      | DeviceIdentification | TEST1024000000002 |

  Scenario: Update Key For Device With Invalid Public Key
    Given a device
      | DeviceIdentification | TEST1024000000001 |
    When receiving an update key request
      | DeviceIdentification | TEST1024000000001 |
      | PublicKey            |                10 |
    Then the update key response contains soap fault
      | Message | VALIDATION_ERROR |
