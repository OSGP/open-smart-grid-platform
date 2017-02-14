Feature: SmartMetering Installation
  As a grid operator
  I want to be able to perform SmartMeteringInstallation operations on a device

  Scenario: Add a new device
    When receiving a smartmetering add device request
      | DeviceIdentification  | TEST1024000000001                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
      | DeviceType            | SMART_METER_E                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
      | CommunicationMethod   | GPRS                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
      | CommunicationProvider | KPN                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
      | ICC_id                |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             1234 |
      | DSMR_version          | 4.2.2                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
      | Supplier              | Kaifa                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
      | HLS3_active           | false                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
      | HLS4_active           | false                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
      | HLS5_active           | true                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
      | Master_key            | 6fa7f5f19812391b2803a142f17c67aa0e3fc23b537ae6f9cd34a850d4fd5f4d60a3b2bdd6f8cb356e00e6c4e104fb5ea521eeabd8cb69d8f7a5cbe2b20e010c089ee346aaa13c9abdc5e0c9ba0fcafff53d2dcd3c1b7a8ee3c3f76e0d00fcd043940586f055c5e19a0fa7eeff6a7894e128029eaf11c1734565f3f5b614bfab9ea5ce24bf34d2e59878dc2401bd175333315ce197d4243dced9c4e28a23bc91dca432985debe81cf5912df7e99b28f596f335e80678d7b5d1edc93be8bf22d77b2e172ccd7c6907454a983999840bf540343d281e8f9871386f005fe40065fcbe218bdc605be4e759cb1b8d5760eab7b8ceb95cfae2224c15045834962f9b6b |
      | Authentication_key    | 9eab9df8169a9c22d694067435b584d573b1a57d62d491b58fd9058e994861666831fb9f5ddbf5aba9ef169256cffc8e540c34b3f92246d062889eca13639fe317e92beec86b48b14d5ef4b74682497eed7d8ea3ae6ea3dfa1877045653cb989146f826b2d97a3294a2aa22f804b1f389d0684482dde33e6cdfc51700156e3be94fc8d5b3a1302b3f3992564982e7cd7885c26fa96eeb7cab5a13d6d7fd341f665d61581dd71f652dc278823216ab75b5a430edc826021c4a2dc9de95fbdfb0e79421e2662743650690bc6b69b0b91035e96cb6396626aa1c252cddf87046dc53b9da0c8d74b517c2845b2e8eaaf72e97d41df1c4ce232e7bb082c82154e9ae5 |
      | Encryption_key        | 4e6fb5bd62d7a21f87438c04f518939cce7cfe8259ff40d9e3ff4a3a8c3befdad191eb066c8332d6d3066a2ed866774616c2b893da4543998eb57fcf35323cd2b41960e857c1a99f5cb59405081712ab23da97353014f500046756eab2620d13a269b83cbefbdfb5e275862b34dd407fd745a1bca18f1b66cb114641212579c6da03e86be2973f8dd6988b15bb6e9ef0f5637827829fc2241891c050a95ef5fc787f740a40aa2d528c69f99c76ad380bba3725929fcbe11ab72cf61e342ab95fc3b883372c110830f28144894aa2919a590822b1e594b807e86f49093982b871c658db0b6c08a90bae55c731efb3d40f245d8c0ad1478b55fa68cced3c1386a7 |
    Then the get add device response should be returned
      | DeviceIdentification  | TEST1024000000001 |
      | Result                | OK                |
    And the dlms device with identification "TEST1024000000001" exists
    And a request to the device can be performed after activation
    And the stored keys are not equal to the received keys

  Scenario: Add a new device with incorrectly encrypted keys
    When receiving a smartmetering add device request
      | DeviceIdentification  | TEST1024000000001                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
      | DeviceType            | SMART_METER_E                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
      | CommunicationMethod   | GPRS                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
      | CommunicationProvider | KPN                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
      | ICC_id                |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             1234 |
      | DSMR_version          | 4.2.2                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
      | Supplier              | Kaifa                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
      | HLS3_active           | false                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
      | HLS4_active           | false                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
      | HLS5_active           | true                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
      | Master_key            | abcdef0123456789 |
      | Authentication_key    | def0123456789abc |
      | Encryption_key        | abc0123456789def |
    Then the get add device response should be returned
      | DeviceIdentification  | TEST1024000000001 |
      | Result                | NOT_OK            |
    And the dlms device with identification "TEST1024000000001" does not exist

  Scenario: Couple G-meter "TESTG102400000001" to E-meter "TEST1024000000001" on free MBUS channel 1  
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
    When the Couple G-meter "TESTG102400000001" request on channel 1 is received
    Then the Couple response is "OK"
    And the mbus device "TESTG102400000001" is coupled to device "TEST1024000000001" on MBUS channel 1

  Scenario: Couple G-meter to an E-meter on occupied MBUS channel 1
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | TEST1024000000001 |
      | Channel                     | 1                 |
    And a dlms device
      | DeviceIdentification        | TESTG102400000002 |
      | DeviceType                  | SMART_METER_G     |
    When the Couple G-meter "TESTG102400000002" request on channel 1 is received
    Then the Couple response is "NOT_OK" and contains
      | There is already a device coupled on Mbus channel 1     |
    And the mbus device "TESTG102400000001" is coupled to device "TEST1024000000001" on MBUS channel 1
    And the mbus device "TESTG102400000002" is not coupled to the device "TEST1024000000001"

  Scenario: Couple unknown G-meter to an E-meter
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When the Couple G-meter "TESTG10240unknown" request on channel 1 is received
    Then the Couple response is "NOT_OK" and contains
      | SmartMeter with id "TESTG10240unknown" could not be found |

  Scenario: Couple G-meter to an unknown E-meter
    Given a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
    When the Couple G-meter "TESTG102400000001" to E-meter "TEST102400unknown" request on channel 1 is received for an unknown gateway
    Then a SOAP fault should have been returned
      | Message | UNKNOWN_DEVICE |

  Scenario: Couple inactive G-meter to an E-meter
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | Active                      | False             |
    When the Couple G-meter "TESTG102400000001" request on channel 1 is received
    Then the Couple response is "NOT_OK" and contains
      | Device TESTG102400000001 is not active in the platform |

  Scenario: Couple G-meter to an inactive E-meter
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | Active               | False             |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
    When the Couple G-meter "TESTG102400000001" request on channel 1 is received for an inactive device
    Then a SOAP fault should have been returned
      | Message | INACTIVE_DEVICE |

  Scenario: DeCouple G-meter from E-meter
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | TEST1024000000001 |
      | Channel                     | 1                 |
    When the DeCouple G-meter "TESTG102400000001" request is received 
    Then the DeCouple response is "OK"
    And the G-meter "TESTG102400000001" is DeCoupled from device "TEST1024000000001"
    And the channel of device "TESTG102400000001" is cleared

  Scenario: DeCouple unknown G-meter from E-meter
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When the DeCouple G-meter "TESTunknownDevice" request is received
    Then the Couple response is "NOT_OK" and contains
     | SmartMeter with id "TESTunknownDevice" could not be found |

@Skip
  Scenario: DeCouple G-meter from unknown E-meter
    Given a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
    When the DeCouple G-meter "TESTG102400000001" from E-meter "TEST102400unknown" request is received for an unknown gateway
    Then a SOAP fault should have been returned
      | Message | UNKNOWN_DEVICE |

  Scenario: DeCouple inactive G-meter from E-meter
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | TEST1024000000001 |
      | Channel                     | 1                 |
      | Active                      | False             |
    When the DeCouple G-meter "TESTG102400000001" request is received
    Then the DeCouple response is "NOT_OK" and contains
      | Device TESTG102400000001 is not active in the platform |

@Skip
  Scenario: DeCouple G-meter from an inactive E-meter
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | Active               | False             |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | TEST1024000000001 |
      | Channel                     | 1                 |
    When the DeCouple G-meter "TESTG102400000001" from E-meter "TEST1024000000001" request is received for an inactive gateway
    Then a SOAP fault should have been returned
      | Message | INACTIVE_DEVICE |
