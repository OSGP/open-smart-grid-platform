@SmartMetering @Platform
Feature: SmartMetering Bundle - SetRandomisationSettings
  As a grid operator
  I want to be able to set randomisation settings on a meter via a bundle request

  Scenario: Set direct attach on a SMR5 device in a bundle request
    Given a bundle request
      | DeviceIdentification | TEST1027000000001 |
    And a dlms device
      | DeviceIdentification | TEST1027000000001 |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | SMR               |
      | ProtocolVersion      | 5.1               |
      | Port                 | 1027              |
    And the bundle request contains a set randomisation settings action with parameters
      | directAttach             | 1 |
      | randomisationStartWindow | 1 |
      | multiplicationFactor     | 1 |
      | numberOfRetries          | 1 |
    When the bundle request is received
    Then the bundle response should be OK
    And the response data record should not be deleted

  Scenario: Set randomisation settings on a SMR5 device in a bundle request
    Given a bundle request
      | DeviceIdentification | TEST1027000000001 |
    And a dlms device
      | DeviceIdentification | TEST1027000000001 |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | SMR               |
      | ProtocolVersion      | 5.1               |
      | Port                 | 1027              |
    And the bundle request contains a set randomisation settings action with parameters
      | directAttach             | 0  |
      | randomisationStartWindow | 10 |
      | multiplicationFactor     | 2  |
      | numberOfRetries          | 1  |
    When the bundle request is received
    Then the bundle response should be OK

  Scenario: Set direct attach on a DSMR4 device in a bundle request
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | ProtocolVersion      | 4.2.2             |
      | Port                 | 1024              |
    And the bundle request contains a set randomisation settings action with parameters
      | directAttach             | 1 |
      | randomisationStartWindow | 1 |
      | multiplicationFactor     | 1 |
      | numberOfRetries          | 1 |
    When the bundle request is received
    Then the bundle response should be a FaultResponse with message containing
      | Message | DIRECT_ATTACH_AT_POWER_ON not known for protocol |

  Scenario: Set randomisation settings on a DSMR4 device in a bundle request
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | ProtocolVersion      | 4.2.2             |
      | Port                 | 1024              |
    And the bundle request contains a set randomisation settings action with parameters
      | directAttach             | 0  |
      | randomisationStartWindow | 10 |
      | multiplicationFactor     | 2  |
      | numberOfRetries          | 1  |
    When the bundle request is received
    Then the bundle response should be OK
