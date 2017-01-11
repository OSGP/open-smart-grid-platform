Feature: Adhoc Management
  As OSGP
  I want to asynchronously handle set reboot requests
  In order to reboot devices As a platform
  NOTE: Authorisation is tested in Basic OSGP Functions - PBI119

  @OslpMockServer
  Scenario Outline: Set Reboot
    Given an oslp device
      | DeviceIdentification | <DeviceIdentification> |
    And the device returns a set reboot response "<Result>" over OSLP
    When receiving a set reboot request
      | DeviceIdentification | <DeviceIdentification> |
    Then the set reboot async response contains
      | DeviceIdentification | <DeviceIdentification> |
    And a set reboot OSLP message is sent to device "<DeviceIdentification>"
    And the platform buffers a set reboot response message for device "<DeviceIdentification>"
      | Result | <Result> |

    Examples: 
      | DeviceIdentification | Result |
      | TEST1024000000001    | OK     |

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
