# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @Bundle @PushSetup @SetPushSetupSms
Feature: SmartMetering Bundle - SetPushSetupSms
  As a grid operator 
  I want to be able to set push setup sms on a meter via a bundle request

  Scenario Outline: Set push setup sms on a <protocol><version> device in a bundle request
    Given a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
    And the bundle request contains a set push setup sms action with parameters
      | Host | localhost |
      | Port | 9598      |
    When the bundle request is received
    Then the bundle response should contain a set push setup sms response with values
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


  Scenario Outline: Set push setup sms on a <protocol><version> GPRS device in a bundle request
    Given a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | CommunicationMethod  | GPRS                   |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
    And the bundle request contains a set push setup sms action with parameters
      | Host | localhost |
      | Port | 9598      |
    When the bundle request is received
    Then the bundle response should contain a set push setup sms response with values
      | Result | OK |

    Examples:
      | deviceIdentification | protocol | version |
      | TEST1029000000001    | SMR      | 5.2     |
    @NightlyBuildOnly
    Examples:
      | deviceIdentification | protocol | version |
      | TEST1024000000001    | DSMR     | 4.2.2   |
      | TEST1027000000001    | SMR      | 5.0.0   |
      | TEST1028000000001    | SMR      | 5.1     |
      | TEST1030000000001    | SMR      | 5.5     |

  Scenario: Set push setup sms on a DSMR2.2 device in a bundle request
    Given a bundle request
      | DeviceIdentification | TEST1031000000001 |
    And a dlms device
      | DeviceIdentification | TEST1031000000001 |
      | DeviceType           | SMART_METER_E     |
      | CommunicationMethod  | GPRS              |
      | Protocol             | DSMR              |
      | ProtocolVersion      |               2.2 |
    And the bundle request contains a set push setup sms action
    When the bundle request is received
    Then the bundle response should be a FaultResponse with message containing
      | Message | Error handling request with SetPushSetupSmsCommandExecutor: No address found for PUSH_SETUP_SMS in protocol DSMR 2.2 |
