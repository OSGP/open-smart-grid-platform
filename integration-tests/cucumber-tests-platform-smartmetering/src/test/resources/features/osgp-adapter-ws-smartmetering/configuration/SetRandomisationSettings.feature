@SmartMetering @Platform @SmartMeteringConfiguration @NightlyBuildOnly
Feature: SmartMetering Configuration - SetRandomisationSettings
  As a grid operator
  I want to be able to set randomisation settings on a device

  Scenario: Set direct attach on a SMR5 device
    Given a dlms device
      | DeviceIdentification | TEST1028000000001 |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | SMR               |
      | ProtocolVersion      | 5.1               |
      | Port                 | 1028              |
    When the set randomisation settings request is received
      | DeviceIdentification     | TEST1028000000001 |
      | directAttach             | 1                 |
      | randomisationStartWindow | 1                 |
      | multiplicationFactor     | 1                 |
      | numberOfRetries          | 1                 |
    Then the randomisation settings should be set on the device

  Scenario: Set randomisation settings on a SMR5 device
    Given a dlms device
      | DeviceIdentification | TEST1028000000001 |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | SMR               |
      | ProtocolVersion      | 5.1               |
      | Port                 | 1028              |
    When the set randomisation settings request is received
      | DeviceIdentification     | TEST1028000000001 |
      | directAttach             | 0                 |
      | randomisationStartWindow | 5                 |
      | multiplicationFactor     | 3                 |
      | numberOfRetries          | 1                 |
    Then the randomisation settings should be set on the device

  Scenario: Set randomisation settings on a DSMR4 device
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | DSMR              |
      | ProtocolVersion      | 4.2.2             |
      | Port                 | 1024              |
    When the set randomisation settings request is received
      | DeviceIdentification     | TEST1024000000001 |
      | directAttach             | 0                 |
      | randomisationStartWindow | 10                |
      | multiplicationFactor     | 4                 |
      | numberOfRetries          | 1                 |
    Then the randomisation settings should be set on the device
