# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform
Feature: SmartMetering Bundle - Bundle retry mechanism
  As a grid operator
  I want bundle request to be retried

  Scenario: Retrying a bundled request after a connection exception
    Given a bundle request
      | DeviceIdentification | TEST1024000000002 |
    And a dlms device
      | DeviceIdentification | TEST1024000000002 |
      | DeviceType           | SMART_METER_E     |
      | ProtocolVersion      |             4.2.2 |
      | Port                 |              9999 |
    And the bundle request contains a set randomisation settings action with parameters
      | directAttach             |  0 |
      | randomisationStartWindow | 10 |
      | multiplicationFactor     |  2 |
      | numberOfRetries          |  1 |
    When the bundle request is received
    Then the bundled request should be rescheduled
      | DeviceIdentification | TEST1024000000002 |

  Scenario: Do not retry a bundle request when a non-retryable error occurs
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | ProtocolVersion      |             4.2.2 |
      | Port                 |              1024 |
    And the bundle request contains a set randomisation settings action with parameters
      | directAttach             | 1 |
      | randomisationStartWindow | 1 |
      | multiplicationFactor     | 1 |
      | numberOfRetries          | 1 |
    When the bundle request is received
    Then the bundle response should be a FaultResponse with message containing
      | Message | DIRECT_ATTACH_AT_POWER_ON not known for protocol |
