Feature: Device management 
  As a grid operator
  I want to be able to perform DeviceManagement operations on a device
  In order to ...
    
	Scenario Outline: Activate a device
  	Given a device 
        | DeviceIdentification | <DeviceIdentification> |
        | Active               | <Active>               | 
     When receiving an activate device request
        | DeviceIdentification | <DeviceIdentification> |
	   Then the activate device response contains
	      | Result | <Result> |
	    And the device with device identification "<DeviceIdentification>" should be active

			Examples:
				| DeviceIdentification | Active | Result |
				| TEST1024000000001    | false  | OK     |
				
	Scenario Outline: Deactivate a device
    Given a device 
        | DeviceIdentification | <DeviceIdentification> |
        | Active               | <Active>               |
     When receiving a deactivate device request
        | DeviceIdentification | <DeviceIdentification> |
     Then the deactivate device response contains
        | Result | <Result> |
      And the device with device identification "<DeviceIdentification>" should be inactive
      
      Examples:
				| DeviceIdentification | Active | Result |
				| TEST1024000000001    | true   | OK     |
				
	Scenario: Revoke Key For Device
    Given a device
      | DeviceIdentification | TEST1024000000001 |
    When receiving a revoke key request
    Then the revoke key response contains
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Revoke Key Request For Non-Existing Device
    When receiving a revoke key request
      | DeviceIdentification | TEST1024000000001 |
    Then the revoke key response contains soap fault
      | Message | UNKNOWN_DEVICE |

  Scenario Outline: Set Owner
    Given a device
      | DeviceIdentification       | <DeviceIdentification>          |
      | OrganizationIdentification | <OldOrganizationIdentification> |
    When receiving a set owner request over OSGP
      | DeviceIdentification       | <DeviceIdentification>          |
      | OrganizationIdentification | <NewOrganizationIdentification> |
    Then the set owner async response contains
      | DeviceIdentification       | <DeviceIdentification>          |
      | OrganizationIdentification | <NewOrganizationIdentification> |
    And the owner of device "<DeviceIdentification>" has been changed
      | OrganizationIdentification | <NewOrganizationIdentification> |

    Examples: 
      | DeviceIdentification | OldOrganizationIdentification | NewOrganizationIdentification |
      | TEST1024000000001    |                               | test-org                      |

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

	Scenario: Show Devices Which Are Not Linked To An Organization
    Given a device
      | DeviceIdentification       | TEST1024000000001 |
      | OrganizationIdentification | test-org          |
      | DeviceFunctionGroup        | MANAGEMENT        |
    When receiving a find devices without organization request
    Then the find devices without organization response contains "1" devices
    And the find devices without organization response contains at index "1"
      | DeviceIdentification | TEST1024000000001 |
      
  Scenario: Show Devices Which Are Not Linked To An Organization While All Devices Are Linked
    Given a device
      | DeviceIdentification       | TEST1024000000001 |
      | OrganizationIdentification | test-org          |
    When receiving a find devices without organization request
    Then the find devices without organization response contains "0" devices