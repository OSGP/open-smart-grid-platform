@SmartMetering @Platform @SmartMeteringConfiguration @GetKeys
Feature: SmartMetering - Configuration - Get Keys
  As a product owner
  I want to be able to obtain the unencrypted keys of a device
  So I can support technical services

  Scenario: Get keys from a device
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | Master_key           | 17ec0e5f6a3314df6239cf9f1b902cbfc9f39e82c57a40ffd8a3e552cc720c92 |
      | Authentication_key   | 55ec0e5f6a3314df6239cf9f1b902cbfc9f39e82c57a40ffd8a3e552cc720caa |
    When a get keys request is received
      | DeviceIdentification | TEST1024000000001                             |
      | SecretTypes          | E_METER_MASTER_KEY,E_METER_AUTHENTICATION_KEY |
    Then the get keys response should return the requested keys
      | Master_key           | 17ec0e5f6a3314df6239cf9f1b902cbfc9f39e82c57a40ffd8a3e552cc720c92 |
      | Authentication_key   | 55ec0e5f6a3314df6239cf9f1b902cbfc9f39e82c57a40ffd8a3e552cc720caa |
