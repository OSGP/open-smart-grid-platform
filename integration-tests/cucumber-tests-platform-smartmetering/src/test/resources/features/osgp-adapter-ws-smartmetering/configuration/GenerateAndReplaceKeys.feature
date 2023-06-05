# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringConfiguration @NightlyBuildOnly
Feature: SmartMetering Configuration - Generate And Replace Keys
  As a grid operator
  I want to be able to generate and replace the keys on a device
  So I can ensure secure device communication according to requirements

  @ResetKeysOnDevice 
  Scenario: Generate and Replace keys on a device
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When the generate and replace keys request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the generate and replace keys response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |
    And the newly generated keys are stored in the secret management database encrypted_secret table
    And the encrypted_secret table in the secret management database should contain "Authentication_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_A | EXPIRED |
    And the encrypted_secret table in the secret management database should contain "Encryption_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_E | EXPIRED |
    And the keyprocessing lock should be removed from off dlms device with identification "TEST1024000000001"

  @ResetKeysOnDevice
  Scenario: Generate and Replace keys on a device while NEW key already present in SecretManagement
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And new keys are registered in the secret management database 1440 minutes ago
      | DeviceIdentification | TEST1024000000001 |
      | Encryption_key       | SECURITY_KEY_1    |
      | Authentication_key   | SECURITY_KEY_2    |
    When the generate and replace keys request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the generate and replace keys response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |
    And the newly generated keys are stored in the secret management database encrypted_secret table
    And the encrypted_secret table in the secret management database should contain "Authentication_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_A | EXPIRED   |
      | SECURITY_KEY_2 | WITHDRAWN |
    And the encrypted_secret table in the secret management database should contain "Encryption_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_E | EXPIRED   |
      | SECURITY_KEY_1 | WITHDRAWN |
    And the keyprocessing lock should be removed from off dlms device with identification "TEST1024000000001"

  @ResetKeysOnDevice 
  Scenario: Generate and Replace keys on a device while multiple NEW keys already present in SecretManagement
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And new keys are registered in the secret management database 1440 minutes ago
      | DeviceIdentification | TEST1024000000001 |
      | Encryption_key       | SECURITY_KEY_1    |
      | Authentication_key   | SECURITY_KEY_2    |
    And new keys are registered in the secret management database 2880 minutes ago
      | DeviceIdentification | TEST1024000000001 |
      | Encryption_key       | SECURITY_KEY_3    |
      | Authentication_key   | SECURITY_KEY_4    |
    When the generate and replace keys request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the generate and replace keys response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |
    And the newly generated keys are stored in the secret management database encrypted_secret table
    And the encrypted_secret table in the secret management database should contain "Authentication_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_A | EXPIRED   |
      | SECURITY_KEY_2 | WITHDRAWN |
      | SECURITY_KEY_4 | WITHDRAWN |
    And the encrypted_secret table in the secret management database should contain "Encryption_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_E | EXPIRED   |
      | SECURITY_KEY_1 | WITHDRAWN |
      | SECURITY_KEY_3 | WITHDRAWN |
    And the keyprocessing lock should be removed from off dlms device with identification "TEST1024000000001"

  @ResetKeysOnDevice
  Scenario: Generate and Replace keys on a device (multiple concurrent single requests are executed after one other)
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | Master_key           | SECURITY_KEY_M    |
      | Encryption_key       | SECURITY_KEY_E    |
      | Authentication_key   | SECURITY_KEY_A    |
    When multiple generate and replace keys requests are received
      | DeviceIdentification | TEST1024000000001,TEST1024000000001,TEST1024000000001 |
    Then multiple generate and replace keys responses should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |
    And the encrypted_secret table in the secret management database should contain "Authentication_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_A | EXPIRED |
    And the encrypted_secret table in the secret management database should contain "Encryption_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_E | EXPIRED |
    And the encrypted_secret table in the secret management database should contain a specified number of EXPIRED and just one ACTIVE key for device "TEST1024000000001"
      | Authentication_key | 3 |
      | Encryption_key     | 3 |
    And the keyprocessing lock should be removed from off dlms device with identification "TEST1024000000001"

  @ResetKeysOnDevice
  Scenario: Generate and Replace keys on a device (multiple requests in one bundle are executed after one other)
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | Master_key           | SECURITY_KEY_M    |
      | Encryption_key       | SECURITY_KEY_E    |
      | Authentication_key   | SECURITY_KEY_A    |
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a generate and replace keys action
    And the bundle request contains a generate and replace keys action
    And the bundle request contains a generate and replace keys action
    When the bundle request is received
    Then the bundle response should contain a generate replace keys response with values
      | Result               | OK |
      | ResultString         | Replace keys for device TEST1024000000001 was successful |
    And the encrypted_secret table in the secret management database should contain "Authentication_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_A | EXPIRED |
    And the encrypted_secret table in the secret management database should contain "Encryption_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_E | EXPIRED |
    And the encrypted_secret table in the secret management database should contain a specified number of EXPIRED and just one ACTIVE key for device "TEST1024000000001"
      | Authentication_key | 3 |
      | Encryption_key     | 3 |
    And the keyprocessing lock should be removed from off dlms device with identification "TEST1024000000001"
