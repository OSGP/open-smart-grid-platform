Feature: SmartMetering Configuration
  As a grid operator
  I want to be able to perform SmartMeteringConfiguration operations on a device
  In order to ...

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | TEST1024000000001 |
      | Channel                     | 1                 |

  Scenario: Set special days on a device
    When the set special days request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the special days should be set on the device
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Set configuration object on a device
    When the set configuration object request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the configuration object should be set on the device
      | DeviceIdentification | TEST1024000000001 |
  @Skip
  Scenario: Handle a received alarm notification from a known device
    When an alarm notification is received from a known device
      | DeviceIdentification | TEST1024000000001 |
    Then the alarm should be pushed to OSGP
      | DeviceIdentification | TEST1024000000001 |
    And the alarm should be pushed to the osgp_logging database device_log_item table
      | DeviceIdentification | TEST1024000000001 |

  @Skip
  Scenario: Handle a received alarm notification from an unknown device
    When an alarm notification is received from an unknown device
      | DeviceIdentification | UNKNOWN0000000001 |
    Then the response contains
      | FaultCode      | SOAP-ENV:Server                                                  |
      | FaultString    | UNKNOWN_DEVICE                                                   |
      | FaultType      | FunctionalFault                                                  |
      | Component      | WS_SMART_METERING                                                |
      | InnerException | com.alliander.osgp.domain.core.exceptions.UnknownEntityException |
      | InnerMessage   | Device with id "UNKNOWN0000000001" could not be found.           |
    And the alarm should be pushed to the osgp_logging database device_log_item table
      | DeviceIdentification | UNKNOWN0000000001 |

  Scenario: Set alarm notifications on a device
    When the set alarm notifications request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the specified alarm notifications should be set on the device
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Exchange user key on a gas device
    When the exchange user key request is received
      | DeviceIdentification | TESTG102400000001 |
    Then the new user key should be set on the gas device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | TEST1024000000001 |

  Scenario: Use wildcards for set activity calendar
    When the set activity calendar request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the activity calendar profiles are set on the device
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Retrieve get administrative status from a device
    When the get administrative status request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the administrative status should be returned
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Set administrative status on a device
    When the set administrative status request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the administrative status should be set on the device
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Replace keys on a device
    When the replace keys request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the new keys are set on the device
      | DeviceIdentification | TEST1024000000001 |
    And the new keys are stored in the osgp_adapter_protocol_dlms database security_key table

  Scenario: Get the firmware version from device
    When the get firmware version request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the firmware version result should be returned
      | DeviceIdentification | TEST1024000000001 |

  @Skip
  Scenario: successful upgrade of firmware
  	Given a firmware
    	| FirmwareModuleVersionComm | Telit 10.00.154 |
    	| FirmwareModuleVersionMa		| BL_012 XMX_N42_GprsV09 |
    	|	FirmwareModuleVersionFunc | M57 4836 |
    	|	FirmwareFilename					| KFPP_V060100FF |
    And a request for a firmware upgrade for device "TEST1024000000001" from a client
    And the installation file of version "KFPP_V060100FF" is available
    When the request for a firmware upgrade is received
    Then firmware should be updated
    And the database should be updated so it indicates that device "TEST1024000000001" is using firmware version "KFPP_V060100FF"

  Scenario: upgrade of firmware, installation file not available
    Given a request for a firmware upgrade for device "TEST1024000000001" from a client
    And the installation file of version "KFPP_V060100FA" is not available
    When the request for a firmware upgrade is received
    Then the message "Installation file is not available" should be given

  Scenario: upgrade of firmware, corrupt installation file
    Given a request for a firmware upgrade for device "TEST1024000000001" from a client
    And the installation file of version "KFPP_V060100FF.corrupt" is available
    And the installation file is corrupt
    When the request for a firmware upgrade is received
    Then the message "Upgrade of firmware did not succeed" should be given

  Scenario: unsuccessful upgrade of firmware
    Given a request for a firmware upgrade for device "TEST1024000000001" from a client
    And the installation file of version "KFPP_V060100FF.corrupt" is available
    When the request for a firmware upgrade is received
    And the upgrade of firmware did not succeed
    Then the message "Upgrade of firmware did not succeed" should be given

      