@SmartMetering @Platform @SmartMeteringConfiguration @PushSetup @SetPushSetupUdp @NightlyBuildOnly
Feature: SmartMetering Configuration - Set Push Setup UDP
  As a grid operator
  I want to be able to set the Push setup UDP on a device
  So the device will push its related messages to the correct endpoint

  Scenario: Set push setup udp on a SMR5.5 device
    Given a dlms device
      | DeviceIdentification | TEST1030000000001 |
      | DeviceType           | SMART_METER_E     |
      | CommunicationMethod  | GPRS              |
      | Protocol             | SMR               |
      | ProtocolVersion      | 5.5               |
    When the set PushSetupUdp request is received
      | DeviceIdentification | TEST1030000000001 |
    Then the PushSetupUdp response should be returned
      | DeviceIdentification | TEST1030000000001 |
      | Result               | OK                |
    Then the PushSetupUdp should be set on the device
      | DeviceIdentification | TEST1030000000001 |

  Scenario Outline: Set push setup udp on a SMR5.2 device
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | CommunicationMethod  | GPRS                   |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
    When the set PushSetupUdp request is received
      | DeviceIdentification | <deviceIdentification> |
    Then the PushSetupUdp response should be returned
      | DeviceIdentification | <deviceIdentification> |
      | Result               | NOT_OK                 |

     Examples:
      | deviceIdentification | protocol | version |
      | TEST1029000000001    | SMR      | 5.2     |
    @NightlyBuildOnly
    Examples:
      | deviceIdentification | protocol | version |
      | TEST1024000000001    | DSMR     | 4.2.2   |
      | TEST1027000000001    | SMR      | 5.0.0   |
      | TEST1028000000001    | SMR      | 5.1     |
