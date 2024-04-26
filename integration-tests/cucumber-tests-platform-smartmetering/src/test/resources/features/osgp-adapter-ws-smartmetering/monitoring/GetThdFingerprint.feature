# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringMonitoring
Feature: SmartMetering Monitoring - Get Thd Fingerprint
  As a grid operator
  I want to be able to get the THD fingerprint and counters from a device

  Scenario Outline: Get the THD fingerprint from a <Protocol> <ProtocolVersion> E-meter
    Given a dlms device
      | DeviceIdentification     | <DeviceId>        |
      | DeviceType               | SMART_METER_E     |
      | Protocol                 | <Protocol>        |
      | ProtocolVersion          | <ProtocolVersion> |
    When the get THD fingerprint request is received
      | DeviceIdentification | <DeviceId>   |
    Then the THD fingerprint result should be returned
      | DeviceIdentification | <DeviceId> |

    Examples:
      | DeviceId             | Protocol | ProtocolVersion |
      | TEST1029000000001    | SMR      | 5.2             |
      | TEST1030000000001    | SMR      | 5.5             |

  Scenario Outline: Get THD fingerprint is not supported on a <Protocol> <ProtocolVersion> E-meter
    Given a dlms device
      | DeviceIdentification     | <DeviceId>        |
      | DeviceType               | SMART_METER_E     |
      | Protocol                 | <Protocol>        |
      | ProtocolVersion          | <ProtocolVersion> |
    When the get THD fingerprint request is received
      | DeviceIdentification | <DeviceId>   |
    Then the THD fingerprint result should not be returned
      | DeviceIdentification | <DeviceId> |

    Examples:
      | DeviceId             | Protocol | ProtocolVersion |
      | TEST1024000000002     | DSMR     | 2.2     |
      | TEST1024000000002     | DSMR     | 4.2.2   |
      | TEST1031000000002     | SMR      | 4.3     |
      | TEST1027000000002     | SMR      | 5.0.0   |
      | TEST1028000000002     | SMR      | 5.1     |
