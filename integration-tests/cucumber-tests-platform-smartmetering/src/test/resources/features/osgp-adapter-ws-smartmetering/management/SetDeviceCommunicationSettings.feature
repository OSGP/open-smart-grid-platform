# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringManagement
Feature: SmartMetering Management - Set Device Communication Settings
  As a grid operator
  I want to be able to set device communication settings
  So OSGP will have proper values for setting up a connection with the device

  Scenario: Set device communication settings on a single meter
    Given a dlms device
      | DeviceIdentification     | TEST1024000000001 |
      | DeviceType               | SMART_METER_E     |
      | ChallengeLength          | 8                 |
      | WithListMax              | 32                |
      | SelectiveAccessSupported | true              |
      | IpAddressIsStatic        | true              |
      | UseSn                    | true              |
      | UseHdlc                  | true              |
      | Polyphase                | true              |
    When the set device communication settings request is received
      | DeviceIdentification     | TEST1024000000001 |
      | ChallengeLength          | 16                |
      | WithListMax              | 1                 |
      | SelectiveAccessSupported | false             |
      | IpAddressIsStatic        | false             |
      | UseSn                    | false             |
      | UseHdlc                  | false             |
      | Polyphase                | false             |
    Then the set device communication settings response should be "OK"
    And the device "TEST1024000000001" should be in the database with attributes
      | ChallengeLength          | 16    |
      | WithListMax              | 1     |
      | SelectiveAccessSupported | false |
      | IpAddressIsStatic        | false |
      | UseSn                    | false |
      | UseHdlc                  | false |
      | Polyphase                | false |
