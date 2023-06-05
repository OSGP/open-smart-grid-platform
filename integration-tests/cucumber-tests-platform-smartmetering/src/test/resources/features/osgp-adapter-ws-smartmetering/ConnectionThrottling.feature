# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@Skip @SmartMetering @Platform @NightlyBuildOnly
Feature: SmartMetering Connection throttling
  As a grid operator
  I want to communicate with devices at constrained networks without exceeding those constraints
  So the load on the network does not rise above what is permitted

  # Skipped, because throttling tests need the ThrottlingServiceApplication to be available, and
  # throttling.client.enabled=true with appropriate settings on the Protocol Adapter DLMS.
  Background:
    Given a dlms device
      | DeviceIdentification     | TEST1024000000001 |
      | DeviceType               | SMART_METER_E     |
      | SelectiveAccessSupported | true              |
      | BtsId                    |                 1 |
      | CellId                   |                 1 |
    And a dlms device
      | DeviceIdentification     | TEST1024000000002 |
      | DeviceType               | SMART_METER_E     |
      | SelectiveAccessSupported | true              |
      | BtsId                    |                 1 |
      | CellId                   |                 1 |
    And a dlms device
      | DeviceIdentification     | TEST1024000000003 |
      | DeviceType               | SMART_METER_E     |
      | SelectiveAccessSupported | true              |
      | BtsId                    |                 1 |
      | CellId                   |                 1 |
    And a dlms device
      | DeviceIdentification     | TEST1024000000004 |
      | DeviceType               | SMART_METER_E     |
      | SelectiveAccessSupported | true              |
      | BtsId                    |                 1 |
      | CellId                   |                 1 |
    And a dlms device
      | DeviceIdentification     | TEST1024000000005 |
      | DeviceType               | SMART_METER_E     |
      | SelectiveAccessSupported | true              |
      | BtsId                    |                 1 |
      | CellId                   |                 2 |
    And a dlms device
      | DeviceIdentification     | TEST1024000000006 |
      | DeviceType               | SMART_METER_E     |
      | SelectiveAccessSupported | true              |
      | BtsId                    |                 2 |
      | CellId                   |                 3 |
    And a dlms device
      | DeviceIdentification     | TEST1024000000007 |
      | DeviceType               | SMART_METER_E     |
      | SelectiveAccessSupported | true              |
    And a dlms device
      | DeviceIdentification     | TEST1024000000008 |
      | DeviceType               | SMART_METER_E     |
      | SelectiveAccessSupported | true              |
    And a dlms device
      | DeviceIdentification     | TEST1024000000009 |
      | DeviceType               | SMART_METER_E     |
      | SelectiveAccessSupported | true              |
    And a dlms device
      | DeviceIdentification     | TEST1024000000010 |
      | DeviceType               | SMART_METER_E     |
      | SelectiveAccessSupported | true              |
    And a dlms device
      | DeviceIdentification     | TEST1024000000011 |
      | DeviceType               | SMART_METER_E     |
      | SelectiveAccessSupported | true              |
    And a dlms device
      | DeviceIdentification     | TEST1024000000012 |
      | DeviceType               | SMART_METER_E     |
      | SelectiveAccessSupported | true              |

  Scenario: Requests for multiple devices are handled.
    When the get administrative status request is received for devices
      | TEST1024000000001 |
      | TEST1024000000002 |
      | TEST1024000000003 |
      | TEST1024000000004 |
      | TEST1024000000005 |
      | TEST1024000000006 |
      | TEST1024000000007 |
      | TEST1024000000008 |
      | TEST1024000000009 |
      | TEST1024000000010 |
      | TEST1024000000011 |
      | TEST1024000000012 |
    Then the administrative status should be returned for devices
      | TEST1024000000001 |
      | TEST1024000000002 |
      | TEST1024000000003 |
      | TEST1024000000004 |
      | TEST1024000000005 |
      | TEST1024000000006 |
      | TEST1024000000007 |
      | TEST1024000000008 |
      | TEST1024000000009 |
      | TEST1024000000010 |
      | TEST1024000000011 |
      | TEST1024000000012 |

  Scenario: A single request in a bundle is handled
    Given a bundle request
      | DeviceIdentification | TEST1024000000006 |
    And the bundle request contains a get administrative status action
    When the bundle request is received
    Then the bundle response should contain a get administrative status response
