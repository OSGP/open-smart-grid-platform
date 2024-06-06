# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @Bundle @PushSetup @SetPushSetupAlarm
Feature: SmartMetering Bundle - SetPushSetupAlarm
  As a grid operator 
  I want to be able to set push setup alarm on a meter via a bundle request

  Scenario Outline: Set push setup alarm (3 objects) on a <protocol><version> device in a bundle request
    Given a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
    And the bundle request contains a set push setup alarm action with parameters
      | Host                   | localhost                                     |
      | Port                   | 9598                                          |
      | PushObjectClassIds     | 1,40,1                                        |
      | PushObjectObisCodes    | 0.0.96.1.1.255,0.1.25.9.0.255,0.0.97.98.0.255 |
      | PushObjectAttributeIds | 2,1,2                                         |
      | PushObjectDataIndexes  | 0,0,0                                         |
    When the bundle request is received
    Then the bundle response should contain a set push setup alarm response with values
      | Result | OK |

    Examples:
      | deviceIdentification | protocol | version |
      | TEST1024000000001    | DSMR     | 4.2.2   |
    @NightlyBuildOnly
    Examples:
      | deviceIdentification | protocol | version |
      | TEST1027000000001    | SMR      | 5.0.0   |
      | TEST1028000000001    | SMR      | 5.1     |
      | TEST1029000000001    | SMR      | 5.2     |
      | TEST1030000000001    | SMR      | 5.5     |


  Scenario Outline: Set push setup alarm (4 objects) on an <protocol><version> device in a bundle request
    Given a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | CommunicationMethod  | GPRS                   |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
    And the bundle request contains a set push setup alarm action with parameters
      | Host                   | localhost                                                     |
      | Port                   | 9598                                                          |
      | PushObjectClassIds     | 1,40,1,1                                                      |
      | PushObjectObisCodes    | 0.0.96.1.1.255,0.1.25.9.0.255,0.0.97.98.0.255,0.0.97.98.1.255 |
      | PushObjectAttributeIds | 2,1,2,2                                                       |
      | PushObjectDataIndexes  | 0,0,0,0                                                       |
    When the bundle request is received
    Then the bundle response should contain a set push setup alarm response with values
      | Result | OK |

    Examples:
      | deviceIdentification | protocol | version |
      | TEST1029000000001    | SMR      | 5.2     |
    @NightlyBuildOnly
    Examples:
      | deviceIdentification | protocol | version |
      | TEST1030000000001    | SMR      | 5.5     |


  Scenario: Set push setup alarm on a DSMR2.2 device in a bundle request
    Given a bundle request
      | DeviceIdentification | TEST1031000000001 |
    And a dlms device
      | DeviceIdentification | TEST1031000000001 |
      | DeviceType           | SMART_METER_E     |
      | CommunicationMethod  | GPRS              |
      | Protocol             | DSMR              |
      | ProtocolVersion      |               2.2 |
    And the bundle request contains a set push setup alarm action
    When the bundle request is received
    Then the bundle response should be a FaultResponse with message containing
      | Message | Error handling request with SetPushSetupAlarmCommandExecutor: No address found for PUSH_SETUP_ALARM in protocol DSMR 2.2 |
