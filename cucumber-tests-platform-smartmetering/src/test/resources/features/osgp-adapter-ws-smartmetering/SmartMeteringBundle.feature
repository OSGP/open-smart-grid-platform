@SmartMetering @Platform 
Feature: SmartMetering Bundle
  As a grid operator
  I want to be able to perform SmartMeteringBundle operations on a device
  In order to ...

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

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
    # And the bundle request contains a set activity calendar action
    And the bundle request contains a get configuration object action
    And the bundle request contains a set alarm notifications action
    And the bundle request contains a set configuration object action
    And the bundle request contains a set push setup alarm action
    And the bundle request contains a synchronize time action
    When the bundle request is received
    # Then the bundle response should contain the responses for all operations in the same order as in the request
    And the bundle response should contain a find events response
    And the bundle response should contain a set special days response
    And the bundle response should contain a get specific attribute value response
    And the bundle response should contain a read alarm register response
    And the bundle response should contain a set administrative status response
    And the bundle response should contain a get actual meter reads response
    And the bundle response should contain a get administrative status response
    And the bundle response should contain a get periodic meter reads response
    # And the bundle response should contain a set activity calendar response
    And the bundle response should contain a get configuration object response
    And the bundle response should contain a set alarm notifications response
    And the bundle response should contain a set configuration object response
    And the bundle response should contain a set push setup alarm response
    And the bundle response should contain a synchronize time response
    
 
