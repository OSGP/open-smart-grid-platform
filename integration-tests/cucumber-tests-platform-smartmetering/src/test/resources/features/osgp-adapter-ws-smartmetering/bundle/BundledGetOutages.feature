# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform
Feature: SmartMetering Bundle - GetOutages
  As a grid operator 
  I want to retrieve the power outages from a meter via a bundle request

  Scenario Outline: Retrieve power outages of a <protocol> <version> device in a bundle request
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
    And a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And the bundle request contains a get outages action
    When the bundle request is received
    Then the bundle response should contain a get outages response with 4 outages
      | 2015-09-01T00:00:00.000+02:00 | 180 |
      | 2015-08-31T00:00:00.000+02:00 | 360 |
      | 2015-08-30T00:00:00.000+02:00 | 540 |
      | 2015-08-29T00:00:00.000+02:00 | 720 |

    Examples:
      | deviceIdentification | protocol | version |
      | TEST1024000000001    | DSMR     | 4.2.2   |
    @NightlyBuildOnly
    Examples:
      | deviceIdentification | protocol | version |
      | TEST1024000000001    | DSMR     | 2.2     |
      | TEST1031000000001    | SMR      | 4.3     |
      | TEST1027000000001    | SMR      | 5.0.0   |
      | TEST1028000000001    | SMR      | 5.1     |
      | TEST1029000000001    | SMR      | 5.2     |
      | TEST1030000000001    | SMR      | 5.5     |

  @GetOutages
  Scenario Outline: Retrieve power outages of a <protocol> <version> device in a bundle request
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
    And a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And the bundle request contains a get outages action
    And device simulation of "<deviceIdentification>" with classid 7 obiscode "1-0:99.97.0" and attributes
      | 2 | array             | [] |
#      | 2 | array             | [["2015-09-01T00:00:00.000+02:00", 1800], ["2015-08-31T00:00:00.000+02:00", 3600], ["2015-08-30T00:00:00.000+02:00", 5400], ["2015-08-29T00:00:00.000+02:00", 7200]] |
    When the bundle request is received
    Then the bundle response should contain a get outages response without outages

    Examples:
      | deviceIdentification | protocol | version |
      | TEST1024000000001    | DSMR     | 4.2.2   |
