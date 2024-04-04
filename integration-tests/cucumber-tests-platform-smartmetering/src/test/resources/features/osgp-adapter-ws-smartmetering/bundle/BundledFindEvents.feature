# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @FindEvents
Feature: SmartMetering Bundle - FindEvents
  As a grid operator 
  I want to retrieve the events from a meter via a bundle request

  Scenario Outline: Retrieve event codes for meter with protocol <protocol> <version>
    And a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
    Given a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And the bundle request contains a find events action with parameters
      | DeviceIdentification | <deviceIdentification>        |
      | EventLogCategory     | STANDARD_EVENT_LOG            |
      | FromDate             | 2015-09-01T00:00:00.000+02:00 |
      | UntilDate            | 2015-09-05T00:00:00.000+02:00 |
    And the bundle request contains a find events action with parameters
      | DeviceIdentification | <deviceIdentification>        |
      | EventLogCategory     | FRAUD_DETECTION_LOG           |
      | FromDate             | 2015-09-01T00:00:00.000+02:00 |
      | UntilDate            | 2015-09-05T00:00:00.000+02:00 |
    And the bundle request contains a find events action with parameters
      | DeviceIdentification | <deviceIdentification>        |
      | EventLogCategory     | COMMUNICATION_SESSION_LOG     |
      | FromDate             | 2015-09-01T00:00:00.000+02:00 |
      | UntilDate            | 2016-09-05T00:00:00.000+02:00 |
    And the bundle request contains a find events action with parameters
      | DeviceIdentification | <deviceIdentification>        |
      | EventLogCategory     | M_BUS_EVENT_LOG               |
      | FromDate             | 2015-09-01T00:00:00.000+02:00 |
      | UntilDate            | 2015-09-05T00:00:00.000+02:00 |
    And the bundle request contains a find events action with parameters
      | DeviceIdentification | <deviceIdentification>        |
      | EventLogCategory     | POWER_QUALITY_EVENT_LOG       |
      | FromDate             | 2015-09-01T00:00:00.000+02:00 |
      | UntilDate            | 2015-09-05T00:00:00.000+02:00 |
    And the bundle request contains a find events action with parameters
      | DeviceIdentification | <deviceIdentification>        |
      | EventLogCategory     | AUXILIARY_EVENT_LOG           |
      | FromDate             | 2015-09-01T00:00:00.000+02:00 |
      | UntilDate            | 2016-09-05T00:00:00.000+02:00 |
    And the bundle request contains a find events action with parameters
      | DeviceIdentification | <deviceIdentification>           |
      | EventLogCategory     | POWER_QUALITY_EXTENDED_EVENT_LOG |
      | FromDate             | 2015-09-01T00:00:00.000+02:00    |
      | UntilDate            | 2015-09-05T00:00:00.000+02:00    |
    When the bundle request is received
    Then the bundle response should contain a find events response with <n1> events
    And the bundle response should contain a find events response with <n2> events
    And the bundle response should contain a find events response with <n3> events
    And the bundle response should contain a find events response with <n4> events
    And the bundle response should contain a find events response with <n5> events
    And the bundle response should contain a find events response with <n6> events
    And the bundle response should contain a find events response with <n7> events
    Examples:
      | deviceIdentification | protocol | version | n1 | n2 | n3 | n4 | n5 |  n6 | n7 |
      | TEST1026000000001    | DSMR     |     2.2 |  0 |  0 |  0 |  0 |  0 |   0 |  0 |
      | TEST1024000000001    | DSMR     |   4.2.2 | 21 |  9 |  7 | 30 |  0 |   0 |  0 |
      | TEST1031000000001    | SMR      |     4.3 | 21 |  9 |  7 | 30 |  0 |   0 |  0 |
      | TEST1027000000001    | SMR      |   5.0.0 | 21 |  9 |  7 | 30 | 19 |   0 |  0 |
      | TEST1028000000001    | SMR      |     5.1 | 21 |  9 |  7 | 30 | 19 | 169 |  0 |
      | TEST1029000000001    | SMR      |     5.2 | 21 |  9 |  7 | 30 | 19 | 169 |  6 |
      | TEST1030000000001    | SMR      |     5.5 | 21 |  9 |  7 | 30 | 19 | 169 |  6 |

  Scenario Outline: Retrieve event codes for meter with protocol <protocol> <version> throws an Exception
    And a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
    Given a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And the bundle request contains a find events action with parameters
      | DeviceIdentification | <deviceIdentification>        |
      | EventLogCategory     | STANDARD_EVENT_LOG            |
      | FromDate             | 2015-09-01T00:00:00.000+02:00 |
      | UntilDate            | 2015-09-05T00:00:00.000+02:00 |
    And the bundle request contains a find events action with parameters
      | DeviceIdentification | <deviceIdentification>        |
      | EventLogCategory     | FRAUD_DETECTION_LOG           |
      | FromDate             | 2015-09-01T00:00:00.000+02:00 |
      | UntilDate            | 2015-09-05T00:00:00.000+02:00 |
    And the bundle request contains a find events action with parameters
      | DeviceIdentification | <deviceIdentification>        |
      | EventLogCategory     | COMMUNICATION_SESSION_LOG     |
      | FromDate             | 2015-09-01T00:00:00.000+02:00 |
      | UntilDate            | 2016-09-05T00:00:00.000+02:00 |
    And the bundle request contains a find events action with parameters
      | DeviceIdentification | <deviceIdentification>        |
      | EventLogCategory     | M_BUS_EVENT_LOG               |
      | FromDate             | 2015-09-01T00:00:00.000+02:00 |
      | UntilDate            | 2015-09-05T00:00:00.000+02:00 |
    And the bundle request contains a find events action with parameters
      | DeviceIdentification | <deviceIdentification>        |
      | EventLogCategory     | POWER_QUALITY_EVENT_LOG       |
      | FromDate             | 2015-09-01T00:00:00.000+02:00 |
      | UntilDate            | 2015-09-05T00:00:00.000+02:00 |
    And the bundle request contains a find events action with parameters
      | DeviceIdentification | <deviceIdentification>        |
      | EventLogCategory     | AUXILIARY_EVENT_LOG           |
      | FromDate             | 2015-09-01T00:00:00.000+02:00 |
      | UntilDate            | 2016-09-05T00:00:00.000+02:00 |
    And the bundle request contains a find events action with parameters
      | DeviceIdentification | <deviceIdentification>           |
      | EventLogCategory     | POWER_QUALITY_EXTENDED_EVENT_LOG |
      | FromDate             | 2015-09-01T00:00:00.000+02:00    |
      | UntilDate            | 2015-09-05T00:00:00.000+02:00    |
    When the bundle request is received
    Then the bundle response should <be/notbe1> a FaultResponse with message containing
      | Message | "No address found for STANDARD_EVENT_LOG in protocol <protocol> <version>"
    And the bundle response should <be/notbe2> a FaultResponse with message containing
      | Message | "No address found for FRAUD_DETECTION_LOG in protocol <protocol> <version>"
    And the bundle response should <be/notbe3> a FaultResponse with message containing
      | Message | "No address found for COMMUNICATION_SESSION_LOG in protocol <protocol> <version>"
    And the bundle response should <be/notbe4> a FaultResponse with message containing
      | Message | "No address found for M_BUS_EVENT_LOG in protocol <protocol> <version>"
    And the bundle response should <be/notbe5> a FaultResponse with message containing
      | Message | "No address found for POWER_QUALITY_EVENT_LOG in protocol <protocol> <version>"
    And the bundle response should <be/notbe6> a FaultResponse with message containing
      | Message | "No address found for AUXILIARY_EVENT_LOG in protocol <protocol> <version>"
    And the bundle response should <be/notbe7> a FaultResponse with message containing
      | Message | "No address found for POWER_QUALITY_EXTENDED_EVENT_LOG in protocol <protocol> <version>"

    Examples:
      | deviceIdentification | protocol | version | be/notbe1 | be/notbe2 | be/notbe3 | be/notbe4 | be/notbe5 | be/notbe6 | be/notbe7 |
      | TEST1026000000001    | DSMR     |     2.2 | be        | be        | be        | be        | be        | be        | be        |
      | TEST1024000000001    | DSMR     |   4.2.2 | not be    | not be    | not be    | not be    | be        | be        | be        |
      | TEST1031000000001    | SMR      |     4.3 | not be    | not be    | not be    | not be    | be        | be        | be        |
      | TEST1027000000001    | SMR      |   5.0.0 | not be    | not be    | not be    | not be    | not be    | be        | be        |
      | TEST1028000000001    | SMR      |     5.1 | not be    | not be    | not be    | not be    | not be    | not be    | be        |
      | TEST1029000000001    | SMR      |     5.2 | not be    | not be    | not be    | not be    | not be    | not be    | not be    |
      | TEST1030000000001    | SMR      |     5.5 | not be    | not be    | not be    | not be    | not be    | not be    | not be    |

  Scenario: Retrieve an unknown event code
    And a dlms device
      | DeviceIdentification | TEST1029000000001 |
      | DeviceType           | SMART_METER_E     |
      | ManufacturerCode     | KAI               |
      | DeviceModelCode      | MA105             |
      | Protocol             | SMR               |
      | ProtocolVersion      | 5.5               |
      | Port                 | 1030              |
    Given a bundle request
      | DeviceIdentification | TEST1029000000001 |
    And the bundle request contains a find events action with parameters
      | DeviceIdentification | TEST1029000000001        |
      | EventLogCategory     | AUXILIARY_EVENT_LOG      |
      | FromDate             | 2013-09-01T00:00:00.000+02:00 |
      | UntilDate            | 2016-09-05T00:00:00.000+02:00 |
    When the bundle request is received
    Then the bundle response should contain a response with 170 events containing "UNKNOWN_EVENT_HEADEND"