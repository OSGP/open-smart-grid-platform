# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform
Feature: SmartMetering Bundle - SetAdministrativeStatus
  As a grid operator 
  I want to be able to set administrative status on a meter via a bundle request

  Scenario Outline: Set administrative status on a <protocol> <version> device in a bundle request
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
    And a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And the bundle request contains a set administrative status action with parameters
      | AdministrativeStatusType  | ON |
    When the bundle request is received
    Then the bundle response should contain a set administrative status response with values
      | Result | OK |
    Examples:
      | deviceIdentification  | protocol | version |
      | TEST1024000000001     | DSMR     | 4.2.2   |
      | TEST1031000000001     | SMR      | 4.3     |
      | TEST1027000000001     | SMR      | 5.0.0   |
      | TEST1028000000001     | SMR      | 5.1     |
      | TEST1029000000001     | SMR      | 5.2     |
      | TEST1030000000001     | SMR      | 5.5     |

  Scenario: Set administrative status on a DSMR 2.2 device in a bundle request
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | DSMR              |
      | ProtocolVersion      | 2.2               |
    And a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a set administrative status action with parameters
      | AdministrativeStatusType  | ON |
    When the bundle request is received
    Then the bundle response should be a FaultResponse with message containing
      | Message | No object found of type ADMINISTRATIVE_IN_OUT |
