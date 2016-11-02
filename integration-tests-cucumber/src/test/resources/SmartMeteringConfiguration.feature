Feature: Smartmetering Configuration
  As a grid operator
  I want to be able to perform SmartMeteringConfiguration operations on a device

  Background: 
    Given an active device with DeviceID "TEST1024000000001"
    Given an active mbus device with DeviceID "TESTG102400000001"
    And an organisation with OrganisationID "Infostroom"

  @SLIM-215 @SmartMeterConfiguration
  Scenario: Set special days on a device
    When the set special days request is received
    Then the special days should be set on the device

  @SLIM-216 @SmartMeterConfiguration
  Scenario: Set configuration object on a device
    When the set configuration object request is received
    Then the configuration object should be set on the device

  @SLIM-125
  Scenario: Handle a received alarm notification from a known device
    When an alarm notification is received from a known device
    Then the alarm should be pushed to OSGP
    And the alarm should be pushed to the osgp_logging database device_log_item table

  @SLIM-125
  Scenario: Handle a received alarm notification from an unknown device
    When an alarm notification is received from an unknown device
    Then the alarm should be pushed to the osgp_logging database device_log_item table

  @SLIM-266 @SmartMeterConfiguration
  Scenario: Set alarm notifications on a device
    When the set alarm notifications request is received
    Then the specified alarm notifications should be set on the device

  @SLIM-256
  Scenario: Exchange user key on a gas device
    When the exchange user key request is received
    Then the new user key should be set on the gas device

  @SLIM-414 @SmartMeterConfiguration
  Scenario: Use wildcards for set activity calendar
    When the set activity calendar request is received
    Then the activity calendar profiles are set on the device

  @SLIM-190 @SmartMeterConfiguration
  Scenario: Retrieve get administrative status from a device
    When the get administrative status request is received
    Then the administrative status should be returned

  @SLIM-189 @SmartMeterConfiguration
  Scenario: Set administrative status on a device
    When the set administrative status request is received
    Then the administrative status should be set on the device

  @SLIM-128 @SLIM-441 @SmartMeterConfiguration
  Scenario: Replace keys on a device
    When the replace keys request is received
    Then the new keys are set on the device
    And the new keys are stored in the osgp_adapter_protocol_dlms database security_key table

  @SLIM-261 @SmartMeterConfiguration
  Scenario: Get the firmware version from device
    When the get firmware version request is received
    Then the firmware version result should be returned

  Scenario: successful upgrade of firmware
    Given a request for a firmware upgrade for device "TEST1024000000001" from a client
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
