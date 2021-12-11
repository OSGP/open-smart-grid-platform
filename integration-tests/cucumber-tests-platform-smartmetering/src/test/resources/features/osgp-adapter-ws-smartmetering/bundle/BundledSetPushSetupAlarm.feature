@SmartMetering @Platform
Feature: SmartMetering Bundle - SetPushSetupAlarm
  As a grid operator 
  I want to be able to set push setup alarm on a meter via a bundle request

  Scenario: Set push setup alarm on a device in a bundle request
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
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

  Scenario: Set push setup alarm on an SMR5.2 device in a bundle request
    Given a bundle request
      | DeviceIdentification | TEST1029000000001 |
    And a dlms device
      | DeviceIdentification | TEST1029000000001 |
      | DeviceType           | SMART_METER_E     |
      | CommunicationMethod  | GPRS              |
      | Protocol             | SMR               |
      | ProtocolVersion      |               5.2 |
      | Port                 |              1029 |
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
