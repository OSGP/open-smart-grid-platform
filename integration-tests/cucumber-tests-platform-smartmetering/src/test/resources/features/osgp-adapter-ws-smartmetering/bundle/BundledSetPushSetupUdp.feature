# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @Bundle @PushSetup @SetPushSetupUdp
Feature: SmartMetering Bundle - Set Push Setup UDP
  As a grid operator 
  I want to be able to set push setup udp on a meter via a bundle request

  Scenario: Set push setup udp on a SMR5.5 device in a bundle request
    Given a bundle request
      | DeviceIdentification | TEST1030000000001 |
    And a dlms device
      | DeviceIdentification | TEST1030000000001 |
      | DeviceType           | SMART_METER_E     |
      | CommunicationMethod  | GPRS              |
      | Protocol             | SMR               |
      | ProtocolVersion      | 5.5               |
    And the bundle request contains a set push setup udp action
    When the bundle request is received
    Then the bundle response should contain a set push setup udp response
    And the PushSetupUdp should be set on the device
      | DeviceIdentification | TEST1030000000001 |

  Scenario: Set push setup udp on a SMR5.2 device in a bundle request
    Given a bundle request
      | DeviceIdentification | TEST1029000000001 |
    And a dlms device
      | DeviceIdentification | TEST1029000000001 |
      | DeviceType           | SMART_METER_E     |
      | CommunicationMethod  | GPRS              |
      | Protocol             | SMR               |
      | ProtocolVersion      |               5.2 |
    And the bundle request contains a set push setup udp action
    When the bundle request is received
    Then the bundle response should be a FaultResponse with message containing
      | Message | Error handling request with SetPushSetupUdpCommandExecutor: No address found for PUSH_SETUP_UDP in protocol SMR 5.2 |

