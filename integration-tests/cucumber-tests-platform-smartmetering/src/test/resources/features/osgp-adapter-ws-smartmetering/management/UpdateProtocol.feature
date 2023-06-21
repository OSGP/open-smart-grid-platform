# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringManagement @NightlyBuildOnly
Feature: Update protocol

  Scenario Outline: Update protocol for device to <Protocol> <ProtocolVersion> <ProtocolVariant>
    Given a dlms device
      | DeviceIdentification | TEST1027000000001 |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | SMR               |
      | ProtocolVersion      | 5.0.0             |
    And a protocol exists in the database
      | Protocol             | <Protocol>        |
      | ProtocolVersion      | <ProtocolVersion> |
      | ProtocolVariant      | <ProtocolVariant> |
    When an update protocol request is received
      | DeviceIdentification | TEST1027000000001 |
      | Protocol             | <Protocol>        |
      | ProtocolVersion      | <ProtocolVersion> |
      | ProtocolVariant      | <ProtocolVariant> |
    Then the update protocol response should be returned
    And the core device is configured with the protocol
      | DeviceIdentification | TEST1027000000001 |
      | Protocol             | <Protocol>        |
      | ProtocolVersion      | <ProtocolVersion> |
      | ProtocolVariant      | <ProtocolVariant> |
    And the dlms device is configured with the protocol
      | DeviceIdentification | TEST1027000000001 |
      | Protocol             | <Protocol>        |
      | ProtocolVersion      | <ProtocolVersion> |

    Examples:
      | Protocol | ProtocolVersion | ProtocolVariant |
      | SMR      |             5.1 | CDMA            |
      | SMR      |             5.2 | GPRS            |
      | SMR      |             5.5 | CDMA            |
      | SMR      |             5.1 | LTE             |
      | SMR      |           5.0.0 | null            |
