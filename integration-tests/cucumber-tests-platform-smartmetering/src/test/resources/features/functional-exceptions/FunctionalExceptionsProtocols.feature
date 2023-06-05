# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@NightlyBuildOnly
Feature: SmartMetering functional exceptions DSMR versions

  Scenario: Add a device with an unknown DSMR version
    When receiving a smartmetering add device request with an invalid DSMR version
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | protocolName         | DSMR              |
      | protocolVersion      |                14 |
    Then a SOAP fault should have been returned
      | Code    |                                         206 |
      | Message | UNKNOWN_PROTOCOL_NAME_OR_VERSION_OR_VARIANT |
    And the dlms device with identification "TEST1024000000001" does not exist
