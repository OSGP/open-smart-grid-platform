Feature: 
  As a grid operator
  I want the platform to collect detailed DLMS communication logs for a device in debug mode

Background:
    Given an organisation with OrganisationID "Infostroom"

Scenario: DLMS device log information is collected for a device in debug mode.
    Given an active device with DeviceID "TEST1024000000001" in debug mode
    # Include any request involving device communication to trigger logging.
    When the get administrative status request is received
    Then the administrative status should be returned
    And the get administrative status communication for device "TEST1024000000001" should be in the device_log_item table

Scenario: DLMS device log information is not collected for a device not in debug mode.
    Given an active device with DeviceID "TEST1024000000001" not in debug mode
    # Include any request involving device communication that could trigger logging.
    When the get administrative status request is received
    Then the administrative status should be returned
    And the get administrative status communication for device "TEST1024000000001" should not be in the device_log_item table
