@SmartMetering @Platform @SmartMeteringConfiguration @NightlyBuildOnly
Feature: SmartMetering Configuration - Replace Keys
  As a grid operator
  I want to be able to replace the keys on a device
  So I can ensure secure device communication according to requirements

  @ResetKeysOnDevice
  Scenario: Replace keys on a device
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | Master_key           | SECURITY_KEY_M    |
      | Encryption_key       | SECURITY_KEY_E    |
      | Authentication_key   | SECURITY_KEY_A    |
    When the replace keys request is received
      | DeviceIdentification | TEST1024000000001 |
      | Encryption_key       | SECURITY_KEY_1    |
      | Authentication_key   | SECURITY_KEY_2    |
    Then the replace keys response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |
    And the newly received keys are stored in the secret management database encrypted_secret table
    And the new keys are stored in the database in another encryption then the encryption of the keys received in the SOAP request
    And the encrypted_secret table in the secret management database should contain "Authentication_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_A | EXPIRED |
      | SECURITY_KEY_2 | ACTIVE  |
    And the encrypted_secret table in the secret management database should contain "Encryption_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_E | EXPIRED |
      | SECURITY_KEY_1 | ACTIVE  |
    And the keyprocessing lock should be removed from off dlms device with identification "TEST1024000000001"

  @ResetKeysOnDevice
  Scenario: Replace keys on a device while NEW key already present in SecretManagement
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And new keys are registered in the secret management database 1440 minutes ago
      | DeviceIdentification | TEST1024000000001 |
      | Encryption_key       | SECURITY_KEY_1    |
      | Authentication_key   | SECURITY_KEY_2    |
    When the replace keys request is received
      | DeviceIdentification | TEST1024000000001 |
      | Encryption_key       | SECURITY_KEY_3    |
      | Authentication_key   | SECURITY_KEY_4    |
    Then the replace keys response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |
    And the newly received keys are stored in the secret management database encrypted_secret table
    And the new keys are stored in the database in another encryption then the encryption of the keys received in the SOAP request
    And the encrypted_secret table in the secret management database should contain "Authentication_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_A | EXPIRED   |
      | SECURITY_KEY_4 | ACTIVE    |
      | SECURITY_KEY_2 | WITHDRAWN |
    And the encrypted_secret table in the secret management database should contain "Encryption_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_E | EXPIRED   |
      | SECURITY_KEY_3 | ACTIVE    |
      | SECURITY_KEY_1 | WITHDRAWN |
    And the keyprocessing lock should be removed from off dlms device with identification "TEST1024000000001"

  @ResetKeysOnDevice
  Scenario: Replace keys on a device while multiple NEW keys already present in SecretManagement
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
    When the replace keys request is received
      | DeviceIdentification | TEST1024000000001 |
      | Encryption_key       | SECURITY_KEY_5    |
      | Authentication_key   | SECURITY_KEY_6    |
    Then the replace keys response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |
    And the newly received keys are stored in the secret management database encrypted_secret table
    And the new keys are stored in the database in another encryption then the encryption of the keys received in the SOAP request
    And the encrypted_secret table in the secret management database should contain "Authentication_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_A | EXPIRED   |
      | SECURITY_KEY_6 | ACTIVE    |
      | SECURITY_KEY_4 | WITHDRAWN |
      | SECURITY_KEY_2 | WITHDRAWN |
    And the encrypted_secret table in the secret management database should contain "Encryption_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_E | EXPIRED   |
      | SECURITY_KEY_5 | ACTIVE    |
      | SECURITY_KEY_3 | WITHDRAWN |
      | SECURITY_KEY_1 | WITHDRAWN |
    And the keyprocessing lock should be removed from off dlms device with identification "TEST1024000000001"

  @ResetKeysOnDevice @Padding
  Scenario: Replace keys on a device (multiple concurrent single requests are executed after one other)
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | Master_key           | SECURITY_KEY_M    |
      | Encryption_key       | SECURITY_KEY_E    |
      | Authentication_key   | SECURITY_KEY_A    |
    When multiple replace keys requests are received
      | DeviceIdentification | TEST1024000000001,TEST1024000000001,TEST1024000000001 |
      | Encryption_key       | SECURITY_KEY_1,SECURITY_KEY_3,SECURITY_KEY_5          |
      | Authentication_key   | SECURITY_KEY_2,SECURITY_KEY_4,SECURITY_KEY_6          |
    Then multiple replace keys responses should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |
    And the encrypted_secret table in the secret management database should contain "Authentication_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_A | EXPIRED   |
    And the encrypted_secret table in the secret management database should contain "Encryption_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_E | EXPIRED   |OK
    And the encrypted_secret table in the secret management database should contain one or more EXPIRED and just one ACTIVE key for device "TEST1024000000001" in correct combination
      | Authentication_key | SECURITY_KEY_2,SECURITY_KEY_4,SECURITY_KEY_6 |
      | Encryption_key     | SECURITY_KEY_1,SECURITY_KEY_3,SECURITY_KEY_5 |
    And the keyprocessing lock should be removed from off dlms device with identification "TEST1024000000001"

  @ResetKeysOnDevice
  Scenario: Replace keys on a device (multiple requests in one bundle are executed after one other)
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | Master_key           | SECURITY_KEY_M    |
      | Encryption_key       | SECURITY_KEY_E    |
      | Authentication_key   | SECURITY_KEY_A    |
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a replace keys action
      | Encryption_key       | SECURITY_KEY_1    |
      | Authentication_key   | SECURITY_KEY_2    |
    And the bundle request contains a replace keys action
      | Encryption_key       | SECURITY_KEY_3    |
      | Authentication_key   | SECURITY_KEY_4    |
    And the bundle request contains a replace keys action
      | Encryption_key       | SECURITY_KEY_5    |
      | Authentication_key   | SECURITY_KEY_6    |
    When the bundle request is received
    Then the bundle response should contain a replace keys response with values
      | Result               | OK                                                       |
      | ResultString         | Replace keys for device TEST1024000000001 was successful |
    And the encrypted_secret table in the secret management database should contain "Authentication_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_A | EXPIRED   |
    And the encrypted_secret table in the secret management database should contain "Encryption_key" keys for device "TEST1024000000001"
      | SECURITY_KEY_E | EXPIRED   |
    And the encrypted_secret table in the secret management database should contain one or more EXPIRED and just one ACTIVE key for device "TEST1024000000001" in correct combination
      | Authentication_key | SECURITY_KEY_2,SECURITY_KEY_4,SECURITY_KEY_6 |
      | Encryption_key     | SECURITY_KEY_1,SECURITY_KEY_3,SECURITY_KEY_5 |
    And the keyprocessing lock should be removed from off dlms device with identification "TEST1024000000001"
