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
      | MbusUserKey                    | MBUS_USER_KEYPAIR |
    When the replace keys request is received
      | DeviceIdentification | TEST1024000000001              |
      | Authentication_key   | AN_INCORRECT_SECURITY_KEY      |
      | Encryption_key       | ANOTHER_INCORRECT_SECURITY_KEY |
    Then the replace keys response generating an error is received
      | DeviceIdentification | TEST1024000000001 |
    And a SOAP fault should have been returned
      | Code    |                  804 |
      | Message | DECRYPTION_EXCEPTION |
    And the keys are not changed in the secret management database encrypted_secret table

  Scenario: HLS5 connect without an encryption key
    Given a dlms device
      | DeviceIdentification  | TEST1024000000001  |
      | DeviceType            | SMART_METER_E      |
      | Hls3active            | false              |
      | Hls4active            | false              |
      | Hls5active            | true               |
      | Encryption_key        | EMPTY_SECURITY_KEY |
    When the get actual meter reads request generating an error is received
      | DeviceIdentification | TEST1024000000001 |
    And a SOAP fault should have been returned
      | Code    |             806 |
      | Message | KEY_NOT_PRESENT |

