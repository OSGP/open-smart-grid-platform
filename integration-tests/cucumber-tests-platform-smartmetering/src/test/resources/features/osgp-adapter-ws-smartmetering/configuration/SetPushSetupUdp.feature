@SmartMetering @Platform @SmartMeteringConfiguration @NightlyBuildOnly
Feature: SmartMetering Configuration - Set Push Setup UDP
  As a grid operator
  I want to be able to set the Push setup UDP on a device
  So the device will push its related messages to the correct endpoint

  Scenario: Set push setup udp on an SMR5.5 device
    Given a dlms device
      | DeviceIdentification | TEST1030000000001 |
      | DeviceType           | SMART_METER_E     |
      | CommunicationMethod  | GPRS              |
      | Protocol             | SMR               |
      | ProtocolVersion      | 5.5               |
      | Port                 |              1030 |
    When the set PushSetupUdp request is received
      | DeviceIdentification | TEST1030000000001 |
    Then the PushSetupUdp response should be returned
      | DeviceIdentification | TEST1030000000001 |
      | Result               | OK                |
    Then the PushSetupUdp should be set on the device
      | DeviceIdentification | TEST1030000000001 |

  Scenario: Set push setup udp on an SMR5.2 device
    Given a dlms device
      | DeviceIdentification | TEST1029000000001 |
      | DeviceType           | SMART_METER_E     |
      | CommunicationMethod  | GPRS              |
      | Protocol             | SMR               |
      | ProtocolVersion      |               5.2 |
      | Port                 |              1029 |
    When the set PushSetupUdp request is received
      | DeviceIdentification | TEST1029000000001 |
    Then the PushSetupUdp response should be returned
      | DeviceIdentification | TEST1029000000001 |
      | Result               | NOT_OK            |
