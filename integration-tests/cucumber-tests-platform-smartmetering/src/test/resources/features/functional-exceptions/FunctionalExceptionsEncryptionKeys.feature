@SmartMetering @Platform @NightlyBuildOnly
Feature: SmartMetering functional exceptions regarding encryption keys
  Scenario: Replace keys on a device with incorrectly encrypted keys
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | HLS5_active          | true              |
    And a dlms device
      | DeviceIdentification           | TESTG102400000001                                                |
      | DeviceType                     | SMART_METER_G                                                    |
      | GatewayDeviceIdentification    | TEST1024000000001                                                |
      | Channel                        |                                                                1 |
      | MbusIdentificationNumber       |                                                         24000000 |
      | MbusManufacturerIdentification | LGB                                                              |
      | MbusUserKey                    | 17ec0e5f6a3314df6239cf9f1b902cbfc9f39e82c57a40ffd8a3e552cc720c92 |
    When the replace keys request is received
      | DeviceIdentification | TEST1024000000001 |
      | Master_key           | abcdef0123456789  |
      | Authentication_key   | def0123456789abc  |
      | Encryption_key       | abc0123456789def  |
    Then the replace keys response generating an error is received
      | DeviceIdentification | TEST1024000000001 |
    And a SOAP fault should have been returned
      | Code    |                  804 |
      | Message | DECRYPTION_EXCEPTION |
    And the keys are not changed in the secret management database encrypted_secret table

  Scenario: HLS5 connect without an encryption key
    Given a dlms device
      | DeviceIdentification  | TEST1024000000001 |
      | DeviceType            | SMART_METER_E     |
      | Hls3active            | false             |
      | Hls4active            | false             |
      | Hls5active            | true              |
      | Encryption_key        |                   |
    When the get actual meter reads request generating an error is received
      | DeviceIdentification | TEST1024000000001 |
    And a SOAP fault should have been returned
      | Code    |             806 |
      | Message | KEY_NOT_PRESENT |

