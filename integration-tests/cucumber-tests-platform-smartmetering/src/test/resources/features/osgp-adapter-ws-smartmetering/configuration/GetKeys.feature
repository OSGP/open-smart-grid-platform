# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringConfiguration @GetKeys
Feature: SmartMetering - Configuration - Get Keys
  As a product owner
  I want to be able to obtain the keys of a device
  So I can support technical services

  Scenario: Get keys from a device
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And an application key is configured
      | OrganizationIdentification | test-org |
    When a get keys request is received
      | DeviceIdentification | TEST1024000000001                             |
      | SecretTypes          | E_METER_MASTER_KEY,E_METER_AUTHENTICATION_KEY |
    Then the get keys response should return the requested keys
      | SecretTypes          | E_METER_MASTER_KEY,E_METER_AUTHENTICATION_KEY |
