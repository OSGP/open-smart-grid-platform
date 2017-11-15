@SmartMetering @Platform
Feature: SmartMetering Bundle
  As a grid operator
  I want to be able to perform operations on a device via a bundle request
  In order to ...

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | TEST1024000000001 |
      | Channel                     |                 1 |      

  Scenario: Handle a bundle of operations
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a find events action
    And the bundle request contains a set special days action
    And the bundle request contains a get specific attribute value action
    And the bundle request contains a read alarm register action
    And the bundle request contains a set administrative status action
    And the bundle request contains a get actual meter reads action
    And the bundle request contains a get administrative status action
    And the bundle request contains a get periodic meter reads action
    And the bundle request contains a get M-Bus encryption key status action
    And the bundle request contains a set activity calendar action
    And the bundle request contains a get configuration object action
    And the bundle request contains a set alarm notifications action
    And the bundle request contains a set configuration object action
    And the bundle request contains a set push setup alarm action
    And the bundle request contains a synchronize time action
    When the bundle request is received
    Then the number of responses in the bundle response should match the number of actions in the bundle request
    And the order of the responses in the bundle response should match the order of actions in the bundle request
