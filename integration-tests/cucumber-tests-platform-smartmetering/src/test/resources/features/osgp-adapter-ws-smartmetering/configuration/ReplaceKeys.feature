@SmartMetering @Platform @SmartMeteringConfiguration @NightlyBuildOnly
Feature: SmartMetering Configuration - Replace Keys
  As a grid operator
  I want to be able to replace the keys on a device
  So I can ensure secure device communication according to requirements

  Scenario: Replace keys on a device
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When the replace keys request is received
      | DeviceIdentification | TEST1024000000001                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
      | Authentication_key   | 9eab9df8169a9c22d694067435b584d573b1a57d62d491b58fd9058e994861666831fb9f5ddbf5aba9ef169256cffc8e540c34b3f92246d062889eca13639fe317e92beec86b48b14d5ef4b74682497eed7d8ea3ae6ea3dfa1877045653cb989146f826b2d97a3294a2aa22f804b1f389d0684482dde33e6cdfc51700156e3be94fc8d5b3a1302b3f3992564982e7cd7885c26fa96eeb7cab5a13d6d7fd341f665d61581dd71f652dc278823216ab75b5a430edc826021c4a2dc9de95fbdfb0e79421e2662743650690bc6b69b0b91035e96cb6396626aa1c252cddf87046dc53b9da0c8d74b517c2845b2e8eaaf72e97d41df1c4ce232e7bb082c82154e9ae5 |
      | Encryption_key       | 4e6fb5bd62d7a21f87438c04f518939cce7cfe8259ff40d9e3ff4a3a8c3befdad191eb066c8332d6d3066a2ed866774616c2b893da4543998eb57fcf35323cd2b41960e857c1a99f5cb59405081712ab23da97353014f500046756eab2620d13a269b83cbefbdfb5e275862b34dd407fd745a1bca18f1b66cb114641212579c6da03e86be2973f8dd6988b15bb6e9ef0f5637827829fc2241891c050a95ef5fc787f740a40aa2d528c69f99c76ad380bba3725929fcbe11ab72cf61e342ab95fc3b883372c110830f28144894aa2919a590822b1e594b807e86f49093982b871c658db0b6c08a90bae55c731efb3d40f245d8c0ad1478b55fa68cced3c1386a7 |
    Then the replace keys response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |
    And the new keys are stored in the secret management database encrypted_secret table
    And the stored keys are not equal to the received keys

  Scenario: Replace keys on a device that has previously created keys with status NEW older than the specified max time in the properties.
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And new keys are registered in the secret management database 1440 minutes ago
      | DeviceIdentification | TEST1024000000001 |
      | Encryption_key       | 867424ac75b6d53c89276d304608321f0a1f6e401f453f84adf3477c7ee1623c |
      | Authentication_key   | c19fe80a22a0f6c5cdaad0826c4d204f23694ded08d811b66e9b845d9f2157d2 |
    And new keys are registered in the secret management database 2880 minutes ago
      | DeviceIdentification | TEST1024000000001 |
      | Encryption_key       | 867424ac75b6d53c89276d304608321f0a1f6e401f453f84adf3477c7ee1623c |
      | Authentication_key   | c19fe80a22a0f6c5cdaad0826c4d204f23694ded08d811b66e9b845d9f2157d2 |
    When the replace keys request is received
      | DeviceIdentification | TEST1024000000001                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
      | Encryption_key       | 9eab9df8169a9c22d694067435b584d573b1a57d62d491b58fd9058e994861666831fb9f5ddbf5aba9ef169256cffc8e540c34b3f92246d062889eca13639fe317e92beec86b48b14d5ef4b74682497eed7d8ea3ae6ea3dfa1877045653cb989146f826b2d97a3294a2aa22f804b1f389d0684482dde33e6cdfc51700156e3be94fc8d5b3a1302b3f3992564982e7cd7885c26fa96eeb7cab5a13d6d7fd341f665d61581dd71f652dc278823216ab75b5a430edc826021c4a2dc9de95fbdfb0e79421e2662743650690bc6b69b0b91035e96cb6396626aa1c252cddf87046dc53b9da0c8d74b517c2845b2e8eaaf72e97d41df1c4ce232e7bb082c82154e9ae5 |
      | Authentication_key   | 4e6fb5bd62d7a21f87438c04f518939cce7cfe8259ff40d9e3ff4a3a8c3befdad191eb066c8332d6d3066a2ed866774616c2b893da4543998eb57fcf35323cd2b41960e857c1a99f5cb59405081712ab23da97353014f500046756eab2620d13a269b83cbefbdfb5e275862b34dd407fd745a1bca18f1b66cb114641212579c6da03e86be2973f8dd6988b15bb6e9ef0f5637827829fc2241891c050a95ef5fc787f740a40aa2d528c69f99c76ad380bba3725929fcbe11ab72cf61e342ab95fc3b883372c110830f28144894aa2919a590822b1e594b807e86f49093982b871c658db0b6c08a90bae55c731efb3d40f245d8c0ad1478b55fa68cced3c1386a7 |
    Then the replace keys response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |
    And the new keys are stored in the secret management database encrypted_secret table
    And the stored keys are not equal to the received keys
    And the encrypted_secret table in the secret management database should contain "ACTIVE" keys for device "TEST1024000000001"
      | Authentication_key   | 1 |
      | Encryption_key       | 1 |
    And the encrypted_secret table in the secret management database should contain "EXPIRED" keys for device "TEST1024000000001"
      | Authentication_key   | 2 |
      | Encryption_key       | 2 |
    And the encrypted_secret table in the secret management database should contain "NEW" keys for device "TEST1024000000001"
      | Authentication_key   | 0 |
      | Encryption_key       | 0 |
      
  @ResetKeysOnDevice
  Scenario: Replace keys with generated ones on a device
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When the generate and replace keys request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the generate and replace keys response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |
    And the new keys are stored in the secret management database encrypted_secret table

  @RecoverKeys
  Scenario: Recover keys after a (simulated) failed key change (incorrect E key)
    #Try to connect using incorrect E-key and then try to recover the correct new key
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | Authentication_key   | c19fe80a22a0f6c5cdaad0826c4d204f23694ded08d811b66e9b845d9f2157d2 |
      | Encryption_key       | c19fe80a22a0f6c5cdaad0826c4d204f23694ded08d811b66e9b845d9f2157d2 |
    And new keys are registered in the secret management database
      | DeviceIdentification | TEST1024000000001 |
      | Encryption_key       | 867424ac75b6d53c89276d304608321f0a1f6e401f453f84adf3477c7ee1623c |
    When the get actual meter reads request is received
      | DeviceIdentification | TEST1024000000001 |
    Then after 15 seconds, the new E_METER_ENCRYPTION_KEY_UNICAST key is recovered
      | DeviceIdentification | TEST1024000000001 |
      | Encryption_key       | 867424ac75b6d53c89276d304608321f0a1f6e401f453f84adf3477c7ee1623c |
