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
    And the new keys are stored in the secret management database encrypted_secret table
    And the stored keys are not equal to the received keys
    And the encrypted_secret table in the secret management database should contain "Authentication_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_A | EXPIRED |
    And the encrypted_secret table in the secret management database should contain "Encryption_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_E | EXPIRED |

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
    And the new keys are stored in the secret management database encrypted_secret table
    And the encrypted_secret table in the secret management database should contain "Authentication_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_A | EXPIRED   |
      | SECURITY_KEY_2 | WITHDRAWN |
    And the encrypted_secret table in the secret management database should contain "Encryption_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_E | EXPIRED   |
      | SECURITY_KEY_1 | WITHDRAWN |

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
    And the new keys are stored in the secret management database encrypted_secret table
    And the encrypted_secret table in the secret management database should contain "Authentication_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_A | EXPIRED   |
      | SECURITY_KEY_2 | WITHDRAWN |
      | SECURITY_KEY_4 | WITHDRAWN |
    And the encrypted_secret table in the secret management database should contain "Encryption_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_E | EXPIRED   |
      | SECURITY_KEY_1 | WITHDRAWN |
      | SECURITY_KEY_3 | WITHDRAWN |
