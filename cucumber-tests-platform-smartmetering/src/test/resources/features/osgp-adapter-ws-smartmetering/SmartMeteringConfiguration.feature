@SmartMetering @Platform @SmartMeteringConfiguration
Feature: SmartMetering Configuration
  As a grid operator
  I want to be able to perform SmartMeteringConfiguration operations on a device
  In order to ...

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification           | TESTG102400000001                                                |
      | DeviceType                     | SMART_METER_G                                                    |
      | GatewayDeviceIdentification    | TEST1024000000001                                                |
      | Channel                        |                                                                1 |
      | MbusIdentificationNumber       |                                                         24000000 |
      | MbusManufacturerIdentification | LGB                                                              |
      | MbusUserKey                    | 17ec0e5f6a3314df6239cf9f1b902cbfc9f39e82c57a40ffd8a3e552cc720c92 |

  Scenario: Set special days on a device
    When the set special days request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the special days should be set on the device
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Set configuration object on a device
    When the set configuration object request is received
      | DeviceIdentification     | TEST1024000000001 |
      | ConfigurationFlagType    | PO_ENABLE         |
      | ConfigurationFlagEnabled | TRUE              |
      | GprsOperationModeType    | ALWAYS_ON         |
    Then the configuration object should be set on the device
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Handle a received alarm notification from a known device
    When an alarm notification is received from a known device
      | DeviceIdentification | TEST1024000000001 |
    Then the alarm should be pushed to the osgp_logging database device_log_item table
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Handle a received alarm notification from an unknown device
    When an alarm notification is received from an unknown device
      | DeviceIdentification | UNKNOWN0000000001 |
    Then the alarm should be pushed to the osgp_logging database device_log_item table
      | DeviceIdentification | UNKNOWN0000000001 |

  Scenario: Set alarm notifications on a device
    When the set alarm notifications request is received
      | DeviceIdentification | TEST1024000000001 |
      | AlarmType            | CLOCK_INVALID     |
      | AlarmTypeEnabled     | TRUE              |
    Then the specified alarm notifications should be set on the device
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Set all alarm notifications disabled on a device
    When the set alarm notifications request is received
      | DeviceIdentification | TEST1024000000001                     |
      | AlarmType_1          | CLOCK_INVALID                         |
      | AlarmTypeEnabled1    | false                                 |
      | AlarmType_2          | REPLACE_BATTERY                       |
      | AlarmTypeEnabled2    | false                                 |
      | AlarmType_3          | POWER_UP                              |
      | AlarmTypeEnabled3    | false                                 |
      | AlarmType_4          | PROGRAM_MEMORY_ERROR                  |
      | AlarmTypeEnabled4    | false                                 |
      | AlarmType_5          | RAM_ERROR                             |
      | AlarmTypeEnabled5    | false                                 |
      | AlarmType_6          | NV_MEMORY_ERROR                       |
      | AlarmTypeEnabled6    | false                                 |
      | AlarmType_7          | MEASUREMENT_SYSTEM_ERROR              |
      | AlarmTypeEnabled7    | false                                 |
      | AlarmType_8          | WATCHDOG_ERROR                        |
      | AlarmTypeEnabled8    | false                                 |
      | AlarmType_9          | FRAUD_ATTEMPT                         |
      | AlarmTypeEnabled9    | false                                 |
      | AlarmType_10         | COMMUNICATION_ERROR_M_BUS_CHANNEL_1   |
      | AlarmTypeEnabled10   | false                                 |
      | AlarmType_11         | COMMUNICATION_ERROR_M_BUS_CHANNEL_2   |
      | AlarmTypeEnabled11   | false                                 |
      | AlarmType_12         | COMMUNICATION_ERROR_M_BUS_CHANNEL_3   |
      | AlarmTypeEnabled12   | false                                 |
      | AlarmType_13         | COMMUNICATION_ERROR_M_BUS_CHANNEL_4   |
      | AlarmTypeEnabled13   | false                                 |
      | AlarmType_14         | FRAUD_ATTEMPT_M_BUS_CHANNEL_1         |
      | AlarmTypeEnabled14   | false                                 |
      | AlarmType_15         | FRAUD_ATTEMPT_M_BUS_CHANNEL_2         |
      | AlarmTypeEnabled15   | false                                 |
      | AlarmType_16         | FRAUD_ATTEMPT_M_BUS_CHANNEL_3         |
      | AlarmTypeEnabled16   | false                                 |
      | AlarmType_17         | FRAUD_ATTEMPT_M_BUS_CHANNEL_4         |
      | AlarmTypeEnabled17   | false                                 |
      | AlarmType_18         | NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1 |
      | AlarmTypeEnabled18   | false                                 |
      | AlarmType_19         | NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_2 |
      | AlarmTypeEnabled19   | false                                 |
      | AlarmType_20         | NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_3 |
      | AlarmTypeEnabled20   | false                                 |
      | AlarmType_21         | NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_4 |
      | AlarmTypeEnabled_21  | false                                 |
    Then the specified alarm notifications should be set on the device
      | DeviceIdentification | TEST1024000000001 |

  # This test runs mostly OK in isolation. However, when run with other tests it fails.
  # Somehow the M-Bus User key is stored in the database, but is not seen in the device
  # as it is inspected in Then-step: "a valid m-bus user key is stored".
  @Skip
  Scenario: Exchange user key on a gas device with no existing user key
    Given a dlms device
      | DeviceIdentification | TEST2560000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification           | TESTG102411111111 |
      | DeviceType                     | SMART_METER_G     |
      | GatewayDeviceIdentification    | TEST2560000000001 |
      | Channel                        |                 1 |
      | MbusIdentificationNumber       |          24111111 |
      | MbusManufacturerIdentification | LGB               |
    When the exchange user key request is received
      | DeviceIdentification | TESTG102411111111 |
    Then a valid m-bus user key is stored
      | DeviceIdentification | TESTG102411111111 |

  Scenario: Exchange user key on a gas device with existing user key
    When the exchange user key request is received
      | DeviceIdentification | TESTG102400000001 |
    Then the exchange user key response should be returned
      | DeviceIdentification | TESTG102400000001 |
      | Result               | OK                |
    And a valid m-bus user key is stored
      | DeviceIdentification | TESTG102400000001 |

  # NOTE: The database MbusIdentificationNumber: 12056731 corresponds with the device attributeID 6: 302343985
  # and likewise the database MbusManufacturerIdentification: LGB corresponds with the device attributeID 7: 12514
  Scenario: Exchange user key on an m-bus device identified by channel
    Given a dlms device
      | DeviceIdentification           | TESTG101205673117 |
      | DeviceType                     | SMART_METER_G     |
      | MbusIdentificationNumber       |          12056731 |
      | MbusManufacturerIdentification | LGB               |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-2:24.1.0" and attributes
      | 5 |         1 |
      | 6 | 302343985 |
      | 7 |     12514 |
      | 8 |        66 |
      | 9 |         3 |
    When the set m-bus user key by channel request is received
      | DeviceIdentification | TEST1024000000001 |
      | Channel              |                 2 |
    Then the set m-bus user key by channel response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |
    And a valid m-bus user key is stored
      | DeviceIdentification | TESTG101205673117 |

  Scenario: Use wildcards for set activity calendar
    When the set activity calendar request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the activity calendar profiles are set on the device
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Retrieve get administrative status from a device
    When the get administrative status request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the administrative status should be returned
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Set administrative status on a device
    When the set administrative status request is received
      | DeviceIdentification     | TEST1024000000001 |
      | AdministrativeStatusType | ON                |
    Then the administrative status should be set on the device
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Replace keys on a device
    When the replace keys request is received
      | DeviceIdentification | TEST1024000000001                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
      | Master_key           | 6fa7f5f19812391b2803a142f17c67aa0e3fc23b537ae6f9cd34a850d4fd5f4d60a3b2bdd6f8cb356e00e6c4e104fb5ea521eeabd8cb69d8f7a5cbe2b20e010c089ee346aaa13c9abdc5e0c9ba0fcafff53d2dcd3c1b7a8ee3c3f76e0d00fcd043940586f055c5e19a0fa7eeff6a7894e128029eaf11c1734565f3f5b614bfab9ea5ce24bf34d2e59878dc2401bd175333315ce197d4243dced9c4e28a23bc91dca432985debe81cf5912df7e99b28f596f335e80678d7b5d1edc93be8bf22d77b2e172ccd7c6907454a983999840bf540343d281e8f9871386f005fe40065fcbe218bdc605be4e759cb1b8d5760eab7b8ceb95cfae2224c15045834962f9b6b |
      | Authentication_key   | 9eab9df8169a9c22d694067435b584d573b1a57d62d491b58fd9058e994861666831fb9f5ddbf5aba9ef169256cffc8e540c34b3f92246d062889eca13639fe317e92beec86b48b14d5ef4b74682497eed7d8ea3ae6ea3dfa1877045653cb989146f826b2d97a3294a2aa22f804b1f389d0684482dde33e6cdfc51700156e3be94fc8d5b3a1302b3f3992564982e7cd7885c26fa96eeb7cab5a13d6d7fd341f665d61581dd71f652dc278823216ab75b5a430edc826021c4a2dc9de95fbdfb0e79421e2662743650690bc6b69b0b91035e96cb6396626aa1c252cddf87046dc53b9da0c8d74b517c2845b2e8eaaf72e97d41df1c4ce232e7bb082c82154e9ae5 |
      | Encryption_key       | 4e6fb5bd62d7a21f87438c04f518939cce7cfe8259ff40d9e3ff4a3a8c3befdad191eb066c8332d6d3066a2ed866774616c2b893da4543998eb57fcf35323cd2b41960e857c1a99f5cb59405081712ab23da97353014f500046756eab2620d13a269b83cbefbdfb5e275862b34dd407fd745a1bca18f1b66cb114641212579c6da03e86be2973f8dd6988b15bb6e9ef0f5637827829fc2241891c050a95ef5fc787f740a40aa2d528c69f99c76ad380bba3725929fcbe11ab72cf61e342ab95fc3b883372c110830f28144894aa2919a590822b1e594b807e86f49093982b871c658db0b6c08a90bae55c731efb3d40f245d8c0ad1478b55fa68cced3c1386a7 |
    Then the replace keys response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |
    And the new keys are stored in the osgp_adapter_protocol_dlms database security_key table
    And the stored keys are not equal to the received keys

  Scenario: Replace keys on a device with incorrectly encrypted keys
    When the replace keys request is received
      | DeviceIdentification | TEST1024000000001 |
      | Master_key           | abcdef0123456789  |
      | Authentication_key   | def0123456789abc  |
      | Encryption_key       | abc0123456789def  |
    Then the replace keys response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | NOT_OK            |
    And the keys are not changed in the osgp_adapter_protocol_dlms database security_key table

  @ResetKeysOnDevice
  Scenario: Replace keys with generated ones on a device
    When the generate and replace keys request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the generate and replace keys response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |
    And the new keys are stored in the osgp_adapter_protocol_dlms database security_key table

  Scenario: Set push setup sms on a device
    When the set PushSetupSms request is received
      | DeviceIdentification | TEST1024000000001 |
      | Hostname             | localhost         |
      | Port                 |              9598 |
    Then the PushSetupSms should be set on the device
      | DeviceIdentification | TEST1024000000001 |
