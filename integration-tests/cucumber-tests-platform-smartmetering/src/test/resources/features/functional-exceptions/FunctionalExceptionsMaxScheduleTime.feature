# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform
Feature: SmartMetering functional exceptions max schedule time exceeded
  As a grid operator
  I want some bundle request to take into account a time after which the request is dropped
  So that no device communication is initiated when the results are no longer useful

  Background:
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

  Scenario: Handling a bundled request after its max schedule time results in an error response
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a get administrative status action
    When the bundle request generating an error is received with headers
      | MaxScheduleTime | now - 5 minutes |
    Then a SOAP fault should have been returned
      | Code    |                        417 |
      | Message | MAX_SCHEDULE_TIME_EXCEEDED |

  Scenario: Handling a bundled request before its max schedule time results in a regular response
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a get administrative status action
    When the bundle request is received with headers
      | MaxScheduleTime | tomorrow at noon |
    Then the bundle response should contain a get administrative status response
