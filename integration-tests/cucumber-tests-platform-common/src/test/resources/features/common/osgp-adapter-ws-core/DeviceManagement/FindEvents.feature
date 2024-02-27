# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@Common @Platform @CoreDeviceManagement
Feature: CoreDeviceManagement Find Events
  As a Grid Operator, my smart devices send events to OSGP which will be stored in the database
  I want to be able to retrieve those stored events sent by smart devices to OSGP
  So that I can monitor the behavior of the smart devices

  Scenario Outline: Find an event for a device
    Given a device
      | OrganizationIdentification | test-org               |
      | DeviceIdentification       | <DeviceIdentification> |
    And an event
      | OrganizationIdentification | test-org               |
      | DeviceIdentification       | <DeviceIdentification> |
      | EventType                  | <EventType>            |
      | Description                | Event description      |
    When a retrieve event notification request is sent
      | DeviceIdentification | <DeviceIdentification> |
      | PageSize             |                     25 |
      | RequestedPage        |                      0 |
    Then the retrieve event notification response contains
      | DeviceIdentification | <DeviceIdentification> |
      | EventType            | <EventType>            |
      | Description          | Event description      |

    Examples: 
      | EventType                                  | DeviceIdentification |
      | DIAG_EVENTS_GENERAL                        | TEST-1024000000001   |
      | DIAG_EVENTS_UNKNOWN_MESSAGE_TYPE           | TEST-1024000000001   |
      | HARDWARE_FAILURE_RELAY                     | TEST-1024000000001   |
      | HARDWARE_FAILURE_FLASH_WRITE_ERROR         | TEST-1024000000001   |
      | HARDWARE_FAILURE_FLASH_MEMORY_CORRUPT      | TEST-1024000000001   |
      | HARDWARE_FAILURE_RTC_NOT_SET               | TEST-1024000000001   |
      | LIGHT_FAILURE_DALI_COMMUNICATION           | TEST-1024000000001   |
      | LIGHT_FAILURE_BALLAST                      | TEST-1024000000001   |
      | LIGHT_EVENTS_LIGHT_ON                      | TEST-1024000000001   |
      | LIGHT_EVENTS_LIGHT_OFF                     | TEST-1024000000001   |
      | MONITOR_EVENTS_LONG_BUFFER_FULL            | TEST-1024000000001   |
      | FIRMWARE_EVENTS_ACTIVATING                 | TEST-1024000000001   |
      | FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND          | TEST-1024000000001   |
      | FIRMWARE_EVENTS_DOWNLOAD_FAILED            | TEST-1024000000001   |
      | LIGHT_FAILURE_TARIFF_SWITCH_ATTEMPT        | TEST-1024000000001   |
      | TARIFF_EVENTS_TARIFF_ON                    | TEST-1024000000001   |
      | TARIFF_EVENTS_TARIFF_OFF                   | TEST-1024000000001   |
      | MONITOR_FAILURE_P1_COMMUNICATION           | TEST-1024000000001   |
      | COMM_EVENTS_ALTERNATIVE_CHANNEL            | TEST-1024000000001   |
      | COMM_EVENTS_RECOVERED_CHANNEL              | TEST-1024000000001   |
      | SECURITY_EVENTS_OUT_OF_SEQUENCE            | TEST-1024000000001   |
      | SECURITY_EVENTS_OSLP_VERIFICATION_FAILED   | TEST-1024000000001   |
      | SECURITY_EVENTS_INVALID_CERTIFICATE        | TEST-1024000000001   |
      | MONITOR_SHORT_DETECTED                     | TEST-1024000000001   |
      | MONITOR_SHORT_RESOLVED                     | TEST-1024000000001   |
      | MONITOR_DOOR_OPENED                        | TEST-1024000000001   |
      | MONITOR_DOOR_CLOSED                        | TEST-1024000000001   |
      | ALARM_NOTIFICATION                         | TEST-1024000000001   |
      | SMS_NOTIFICATION                           | TEST-1024000000001   |
      | MONITOR_EVENTS_TEST_RELAY_ON               | TEST-1024000000001   |
      | MONITOR_EVENTS_TEST_RELAY_OFF              | TEST-1024000000001   |
      | MONITOR_EVENTS_LOSS_OF_POWER               | TEST-1024000000001   |
      | MONITOR_EVENTS_LOCAL_MODE                  | TEST-1024000000001   |
      | MONITOR_EVENTS_REMOTE_MODE                 | TEST-1024000000001   |
      | FIRMWARE_EVENTS_CONFIGURATION_CHANGED      | TEST-1024000000001   |
      | FIRMWARE_EVENTS_DOWNLOAD_SUCCESS           | TEST-1024000000001   |
      | CA_FILE_EVENTS_ACTIVATING                  | TEST-1024000000001   |
      | CA_FILE_FIRMWARE_EVENTS_DOWNLOAD_NOT_FOUND | TEST-1024000000001   |
      | CA_FILE_EVENTS_DOWNLOAD_FAILED             | TEST-1024000000001   |
      | CA_FILE_EVENTS_DOWNLOAD_SUCCESS            | TEST-1024000000001   |
      | NTP_SERVER_NOT_REACH                       | TEST-1024000000002   |
      | NTP_SYNC_ALARM_OFFSET                      | TEST-1024000000002   |
      | NTP_SYNC_MAX_OFFSET                        | TEST-1024000000002   |
      | NTP_SYNC_SUCCESS                           | TEST-1024000000002   |
      | AUTHENTICATION_FAIL                        | TEST-1024000000002   |
      | LIGHT_SENSOR_REPORTS_DARK                  | LMD-00000000000001   |
      | LIGHT_SENSOR_REPORTS_LIGHT                 | LMD-00000000000001   |

  Scenario Outline: Find page of events for a device
    Given a device
      | OrganizationIdentification | test-org               |
      | DeviceIdentification       | <DeviceIdentification> |
    And all events are present for device
      | DeviceIdentification | <DeviceIdentification> |
    When a retrieve event notification request is sent
      | DeviceIdentification | <DeviceIdentification> |
      | PageSize             | <PageSize>             |
      | RequestedPage        | <RequestedPage>        |
    Then the stored events are filtered and retrieved
      | DeviceIdentification | <DeviceIdentification> |
      | Result               | <ActualNumberOfEvents> |
    And the retrieve event notifications response should contain <numberOfEventsInPage> events on the current page and a total of <NumberOfPages> pages
      | DeviceIdentification | <DeviceIdentification> |

    Examples: 
      | DeviceIdentification | PageSize | RequestedPage | ActualNumberOfEvents | NumberOfPages | numberOfEventsInPage |
      | TEST-1024000000001   |       25 |             0 |                   48 |             2 |                   25 |
      | TEST-1024000000002   |       25 |             1 |                   48 |             2 |                   23 |
      | TEST-1024000000003   |        2 |             0 |                   48 |            24 |                    2 |
      | TEST-1024000000004   |        2 |            23 |                   48 |            24 |                    2 |
      | TEST-1024000000005   |        2 |            24 |                   48 |            24 |                    0 |

  Scenario: Find events for a device by filtering the description
    Given a device
      | OrganizationIdentification | test-org           |
      | DeviceIdentification       | TEST-1024000000001 |
    And an event
      | OrganizationIdentification | test-org              |
      | DeviceIdentification       | TEST-1024000000001    |
      | EventType                  | LIGHT_EVENTS_LIGHT_ON |
      | Description                | ad-hoc trigger        |
    And an event
      | OrganizationIdentification | test-org              |
      | DeviceIdentification       | TEST-1024000000001    |
      | EventType                  | LIGHT_EVENTS_LIGHT_ON |
      | Description                | another description   |
    When a retrieve event notification request is sent
      | DeviceIdentification | TEST-1024000000001 |
      | Description          | ad-hoc trigger     |
      | PageSize             |                 25 |
      | RequestedPage        |                  0 |
    Then the retrieve event notification response contains
      | DeviceIdentification | TEST-1024000000001    |
      | EventType            | LIGHT_EVENTS_LIGHT_ON |
      | Description          | ad-hoc trigger        |

  Scenario: Find events for a device by filtering the start of a description
    Given a device
      | OrganizationIdentification | test-org           |
      | DeviceIdentification       | TEST-1024000000001 |
    And an event
      | OrganizationIdentification | test-org                  |
      | DeviceIdentification       | TEST-1024000000001        |
      | EventType                  | LIGHT_EVENTS_LIGHT_ON     |
      | Description                | setLightRequest something |
    And an event
      | OrganizationIdentification | test-org                            |
      | DeviceIdentification       | TEST-1024000000001                  |
      | EventType                  | LIGHT_EVENTS_LIGHT_ON               |
      | Description                | setLightRequest something different |
    And an event
      | OrganizationIdentification | test-org              |
      | DeviceIdentification       | TEST-1024000000001    |
      | EventType                  | LIGHT_EVENTS_LIGHT_ON |
      | Description                | another description   |
    When a retrieve event notification request is sent
      | DeviceIdentification    | TEST-1024000000001 |
      | Description starts with | setLightRequest    |
      | PageSize                |                 25 |
      | RequestedPage           |                  0 |
    Then the retrieve event notification response contains
      | DeviceIdentification | TEST-1024000000001        |
      | EventType            | LIGHT_EVENTS_LIGHT_ON     |
      | Description          | setLightRequest something |
    And the retrieve event notification response contains
      | DeviceIdentification | TEST-1024000000001                  |
      | EventType            | LIGHT_EVENTS_LIGHT_ON               |
      | Description          | setLightRequest something different |

  Scenario: Find events for a device by combining filtering the description and filtering the start of a description
    Given a device
      | OrganizationIdentification | test-org           |
      | DeviceIdentification       | TEST-1024000000001 |
    And an event
      | OrganizationIdentification | test-org                  |
      | DeviceIdentification       | TEST-1024000000001        |
      | EventType                  | LIGHT_EVENTS_LIGHT_ON     |
      | Description                | setLightRequest something |
    And an event
      | OrganizationIdentification | test-org              |
      | DeviceIdentification       | TEST-1024000000001    |
      | EventType                  | LIGHT_EVENTS_LIGHT_ON |
      | Description                | ad-hoc trigger        |
    And an event
      | OrganizationIdentification | test-org              |
      | DeviceIdentification       | TEST-1024000000001    |
      | EventType                  | LIGHT_EVENTS_LIGHT_ON |
      | Description                | another description   |
    When a retrieve event notification request is sent
      | DeviceIdentification    | TEST-1024000000001 |
      | Description             | ad-hoc trigger     |
      | Description starts with | setLightRequest    |
      | PageSize                |                 25 |
      | RequestedPage           |                  0 |
    Then the retrieve event notification response contains
      | DeviceIdentification | TEST-1024000000001        |
      | EventType            | LIGHT_EVENTS_LIGHT_ON     |
      | Description          | setLightRequest something |
    And the retrieve event notification response contains
      | DeviceIdentification | TEST-1024000000001                  |
      | EventType            | LIGHT_EVENTS_LIGHT_ON               |
      | Description          | ad-hoc trigger |
