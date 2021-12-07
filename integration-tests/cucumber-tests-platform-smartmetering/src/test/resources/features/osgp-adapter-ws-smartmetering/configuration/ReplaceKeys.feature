@SmartMetering @Platform @SmartMeteringConfiguration @NightlyBuildOnly @SMHE-228
Feature: SmartMetering Configuration - Replace Keys
  As a grid operator
  I want to be able to replace the keys on a device
  So I can ensure secure device communication according to requirements
  All scenarios start with
   - SECURITY_KEYPAIR_1 as Authentication key
   - SECURITY_KEYPAIR_2 as Encryption key
   - SECURITY_KEYPAIR_3 as Master key

  @ResetKeysOnDevice
  Scenario: Replace keys on a device
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When the replace keys request is received
      | DeviceIdentification | TEST1024000000001  |
      | Encryption_key       | SECURITY_KEYPAIR_1 |
      | Authentication_key   | SECURITY_KEYPAIR_2 |
    Then the replace keys response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |
    And the new keys are stored in the secret management database encrypted_secret table
    And the stored keys are not equal to the received keys
    And the encrypted_secret table in the secret management database should contain "Authentication_key" keys for device "TEST1024000000001"
      | SECURITY_KEYPAIR_1 | EXPIRED |
      | SECURITY_KEYPAIR_2 | ACTIVE  |
    And the encrypted_secret table in the secret management database should contain "Encryption_key" keys for device "TEST1024000000001"
      | SECURITY_KEYPAIR_2 | EXPIRED |
      | SECURITY_KEYPAIR_1 | ACTIVE  |

  @ResetKeysOnDevice
  Scenario: Replace keys on a device while NEW key already present in SecretManagement
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And new keys are registered in the secret management database 1440 minutes ago
      | DeviceIdentification | TEST1024000000001 |
      | Encryption_key       | SECURITY_KEYPAIR_1 |
      | Authentication_key   | SECURITY_KEYPAIR_2 |
    When the replace keys request is received
      | DeviceIdentification | TEST1024000000001  |
      | Encryption_key       | SECURITY_KEYPAIR_3 |
      | Authentication_key   | SECURITY_KEYPAIR_3 |
    Then the replace keys response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |
    And the new keys are stored in the secret management database encrypted_secret table
    And the stored keys are not equal to the received keys
    And the encrypted_secret table in the secret management database should contain "Authentication_key" keys for device "TEST1024000000001"
      | SECURITY_KEYPAIR_1 | EXPIRED |
      | SECURITY_KEYPAIR_2 | ACTIVE |
    And the encrypted_secret table in the secret management database should contain "Encryption_key" keys for device "TEST1024000000001"
      | SECURITY_KEYPAIR_2 | EXPIRED |
      | SECURITY_KEYPAIR_1 | ACTIVE |

  @ResetKeysOnDevice
  Scenario: Replace keys on a device while multiple NEW keys already present in SecretManagement
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And new keys are registered in the secret management database 1440 minutes ago
      | DeviceIdentification | TEST1024000000001 |
      | Encryption_key       | SECURITY_KEYPAIR_1 |
      | Authentication_key   | SECURITY_KEYPAIR_2 |
    And new keys are registered in the secret management database 2880 minutes ago
      | DeviceIdentification | TEST1024000000001  |
      | Encryption_key       | SECURITY_KEYPAIR_4 |
      | Authentication_key   | SECURITY_KEYPAIR_4 |
    When the replace keys request is received
      | DeviceIdentification | TEST1024000000001  |
      | Encryption_key       | SECURITY_KEYPAIR_3 |
      | Authentication_key   | SECURITY_KEYPAIR_3 |
    Then the replace keys response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |
    And the new keys are stored in the secret management database encrypted_secret table
    And the stored keys are not equal to the received keys
    And the encrypted_secret table in the secret management database should contain "Authentication_key" keys for device "TEST1024000000001"
      | SECURITY_KEYPAIR_1 | EXPIRED |
      | SECURITY_KEYPAIR_2 | ACTIVE  |
      | SECURITY_KEYPAIR_4 | EXPIRED |
    And the encrypted_secret table in the secret management database should contain "Encryption_key" keys for device "TEST1024000000001"
      | SECURITY_KEYPAIR_2 | EXPIRED |
      | SECURITY_KEYPAIR_1 | ACTIVE  |
      | SECURITY_KEYPAIR_4 | EXPIRED |

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

  @ResetKeysOnDevice 
  Scenario: Generate and Replace keys on a device 2
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
      | SECURITY_KEYPAIR_1 | EXPIRED |
    And the encrypted_secret table in the secret management database should contain "Encryption_key" keys for device "TEST1024000000001"
      | SECURITY_KEYPAIR_2 | EXPIRED |

  @ResetKeysOnDevice
  Scenario: Generate and Replace keys on a device while NEW key already present in SecretManagement
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And new keys are registered in the secret management database 1440 minutes ago
      | DeviceIdentification | TEST1024000000001 |
      | Encryption_key       | SECURITY_KEYPAIR_1 |
      | Authentication_key   | SECURITY_KEYPAIR_2 |
    When the generate and replace keys request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the generate and replace keys response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |
    And the new keys are stored in the secret management database encrypted_secret table
    And the encrypted_secret table in the secret management database should contain "Authentication_key" keys for device "TEST1024000000001"
      | SECURITY_KEYPAIR_1 | EXPIRED |
      | SECURITY_KEYPAIR_2 | ACTIVE |
    And the encrypted_secret table in the secret management database should contain "Encryption_key" keys for device "TEST1024000000001"
      | SECURITY_KEYPAIR_2 | EXPIRED |
      | SECURITY_KEYPAIR_1 | ACTIVE |

  @ResetKeysOnDevice 
  Scenario: Generate and Replace keys on a device while multiple NEW keys already present in SecretManagement
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And new keys are registered in the secret management database 1440 minutes ago
      | DeviceIdentification | TEST1024000000001 |
      | Encryption_key       | SECURITY_KEYPAIR_1 |
      | Authentication_key   | SECURITY_KEYPAIR_2 |
    And new keys are registered in the secret management database 2880 minutes ago
      | DeviceIdentification | TEST1024000000001  |
      | Encryption_key       | SECURITY_KEYPAIR_3 |
      | Authentication_key   | SECURITY_KEYPAIR_3 |
    When the generate and replace keys request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the generate and replace keys response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |
    And the new keys are stored in the secret management database encrypted_secret table
    And the encrypted_secret table in the secret management database should contain "Authentication_key" keys for device "TEST1024000000001"
      | SECURITY_KEYPAIR_1 | EXPIRED |
      | SECURITY_KEYPAIR_2 | ACTIVE  |
      | SECURITY_KEYPAIR_3 | EXPIRED |
    And the encrypted_secret table in the secret management database should contain "Encryption_key" keys for device "TEST1024000000001"
      | SECURITY_KEYPAIR_2 | EXPIRED |
      | SECURITY_KEYPAIR_1 | ACTIVE  |
      | SECURITY_KEYPAIR_3 | EXPIRED |

  @RecoverKeys
  Scenario: Recover keys after a (simulated) failed key change (incorrect E key)
    #Try to connect using incorrect E-key and then try to recover the correct new key
    Given a dlms device
      | DeviceIdentification | TEST1024000000001  |
      | DeviceType           | SMART_METER_E      |
      | Authentication_key   | SECURITY_KEYPAIR_1 |
      | Encryption_key       | SECURITY_KEYPAIR_1 |
    And new keys are registered in the secret management database
      | DeviceIdentification | TEST1024000000001 |
      | Encryption_key       | SECURITY_KEYPAIR_2 |
    When the get actual meter reads request is received
      | DeviceIdentification | TEST1024000000001 |
    Then after 15 seconds, the new E_METER_ENCRYPTION_KEY_UNICAST key is recovered
      | DeviceIdentification | TEST1024000000001 |
      | Encryption_key       | SECURITY_KEYPAIR_2 |
