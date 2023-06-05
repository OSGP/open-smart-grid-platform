# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform
Feature: SmartMetering Bundle - SetConfigurationObject
  As a grid operator 
  I want to be able to set configuration object on a meter via a bundle request

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

  Scenario: Set configuration object on a device in a bundle request
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a set configuration object action with parameters
      | GprsOperationModeType      | ALWAYS_ON |
      | ConfigurationFlagCount     |         1 |
      | ConfigurationFlagType_1    | PO_ENABLE |
      | ConfigurationFlagEnabled_1 | TRUE      |
    When the bundle request is received
    Then the bundle response should contain a set configuration object response with values
      | Result | OK |
