Feature: PublicLightingAdhocManagement Set Reboot
  As OSGP
  I want to asynchronously handle set reboot requests
  In order to reboot devices As a platform

  @OslpMockServer
  Scenario: Set reboot
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
    And the device returns a set reboot response "OK" over OSLP
    When receiving a set reboot request
      | DeviceIdentification | TEST1024000000001 |
    Then the set reboot async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set reboot OSLP message is sent to device "TEST1024000000001"
    And the platform buffers a set reboot response message for device "TEST1024000000001"
      | Result | OK |

  Scenario: Set reboot as an unknown organization
    When receiving a set reboot request by an unknown organization
      | DeviceIdentification | TEST1024000000001 |
    Then the set reboot async response contains a soap fault
      | Message | UNKNOWN_ORGANISATION |

  Scenario: Set reboot for an unknown device
    When receiving a set reboot request
      | DeviceIdentification | TEST1024000000001 |
      | TransitionType       | DAY_NIGHT         |
    Then the set reboot async response contains a soap fault
      | Message | UNKNOWN_DEVICE |
