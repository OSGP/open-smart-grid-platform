# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringConfiguration @Keys @NightlyBuildOnly
Feature: SmartMetering Configuration - Recover Keys
  As a grid operator
  I want to be able to recover the keys on a device
  So I can ensure secure device communication according to requirements

  @RecoverKeys
  Scenario Outline: Recover keys after a (simulated) failed key change on <protocol> <version> device
    #Try to connect using incorrect A-key and then try to recover the correct new key
    Given a dlms device
      | DeviceIdentification | <identification>  |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | <protocol>        |
      | ProtocolVersion      | <version>         |
    And simulate failure of change from previous key of device "<identification>"
      | Authentication_key   | SECURITY_KEY_1    |
    When the get actual meter reads request is received
      | DeviceIdentification | <identification>  |
    Then after 15 seconds, the new E_METER_ENCRYPTION_KEY_UNICAST key is recovered
      | DeviceIdentification | <identification>  |
      | Authentication_key   | SECURITY_KEY_A    |
    And after 30 seconds, the encrypted_secret table in the secret management database should contain "Authentication_key" keys for device "<identification>"
      | SECURITY_KEY_1 | EXPIRED |
    And the keyprocessing lock should be removed from off dlms device with identification "<identification>"

    Examples:
      | identification    | protocol | version |
      | TEST1024000000001 | DSMR     | 4.2.2   |
    @NightlyBuildOnly
    Examples:
      | identification    | protocol | version |
      | TEST1024000000001 | DSMR     | 2.2     |
      | TEST1031000000001 | SMR      | 4.3     |
      | TEST1027000000001 | SMR      | 5.0.0   |
      | TEST1028000000001 | SMR      | 5.1     |
      | TEST1029000000001 | SMR      | 5.2     |
      | TEST1030000000001 | SMR      | 5.5     |

  @RecoverKeys @ResetKeysOnDevice
  Scenario: Replace keys triggers a recover keys and replaces keys on the retry
    #Replace keys request will fail at first time which triggers the RecoverKey process
    #Replace keys request will succeed in the retry returning an OK response
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And simulate failure of change from previous key of device "TEST1024000000001"
      | Encryption_key       | SECURITY_KEY_1    |
    When the replace keys request is received
      | DeviceIdentification | TEST1024000000001 |
      | Encryption_key       | SECURITY_KEY_2    |
      | Authentication_key   | SECURITY_KEY_3    |
    And the replace keys response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |
    And the encrypted_secret table in the secret management database should contain "Authentication_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_A | EXPIRED |
      | SECURITY_KEY_3 | ACTIVE  |
    And the encrypted_secret table in the secret management database should contain "Encryption_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_E | EXPIRED |
      | SECURITY_KEY_1 | EXPIRED |
      | SECURITY_KEY_2 | ACTIVE  |
    And the keyprocessing lock should be removed from off dlms device with identification "TEST1024000000001"

  @RecoverKeys @ResetKeysOnDevice
  Scenario: Generate and Replace keys triggers a recover keys and replaces keys on the retry
    #Generate and Replace keys request will fail at first time which triggers the RecoverKey process
    #Generate and Replace keys request will succeed in the retry returning an OK response
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And simulate failure of change from previous key of device "TEST1024000000001"
      | Encryption_key       | SECURITY_KEY_1    |
    When the generate and replace keys request is received
      | DeviceIdentification | TEST1024000000001 |
    And the generate and replace keys response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |
    And the encrypted_secret table in the secret management database should contain "Authentication_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_A | EXPIRED |
    And the encrypted_secret table in the secret management database should contain "Encryption_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_E | EXPIRED |
      | SECURITY_KEY_1 | EXPIRED |
    And the keyprocessing lock should be removed from off dlms device with identification "TEST1024000000001"
