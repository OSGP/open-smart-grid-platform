@SmartMetering @Platform @SmartMeteringConfiguration @NightlyBuildOnly
Feature: SmartMetering Configuration - Recover Keys
  As a grid operator
  I want to be able to recover the keys on a device
  So I can ensure secure device communication according to requirements

  @RecoverKeys
  Scenario: Recover keys after a (simulated) failed key change (incorrect E key)
    #Try to connect using incorrect E-key and then try to recover the correct new key
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | Authentication_key   | SECURITY_KEY_A    |
      | Encryption_key       | SECURITY_KEY_A    |
    And new keys are registered in the secret management database
      | DeviceIdentification | TEST1024000000001 |
      | Encryption_key       | SECURITY_KEY_E    |      
    When the get actual meter reads request is received
      | DeviceIdentification | TEST1024000000001 |
    Then after 15 seconds, the new E_METER_ENCRYPTION_KEY_UNICAST key is recovered
      | DeviceIdentification | TEST1024000000001 |
      | Encryption_key       | SECURITY_KEY_E    |
