Feature: SmartMetering Installation
  As a grid operator
  I want to be able to perform SmartMeteringInstallation operations on a device

  Scenario: Add a new device
    When receiving an smartmetering add device request
      | DeviceIdentification  | E0026000059790003 |
      | DeviceType            | SMART_METER_E     |
      | CommunicationMethod   | GPRS              |
      | CommunicationProvider | KPN               |
      | ICC_id                |              1234 |
      | DSMR_version          | 4.2.2             |
      | Supplier              | Kaifa             |
      | HLS3_active           | false             |
      | HLS4_active           | false             |
      | HLS5_active           | true              |
      | Master_key            | true              |
    Then the smartmetering add device response contains
      | DeviceIdentification | E0026000059790003 |
    And receiving an get add device response request
      | DeviceIdentification | E0026000059790003 |
    And the get add device request response should be ok
    And the dlms device with id "E0026000059790003" exists

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
    Then the Couple response is "NOT OK" and contains
      | There is already a device coupled on Mbus channel 1     |
    And the mbus device "TESTG102400000001" is coupled to device "TEST1024000000001" on MBUS channel 1
    And the mbus device "TESTG102400000002" is not coupled to the device "TEST1024000000001" 

  Scenario: Couple unknown G-meter to an E-meter
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When the Couple G-meter "TESTG10240unknown" request on channel 1 is received
    Then the Couple response is "NOT OK" and contains
      | SmartMeter with id "TESTG10240unknown" could not be found |

  Scenario: Couple G-meter to an unknown E-meter
    Given a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
    When the Couple G-meter "TESTG102400000001" to E-meter "TEST102400unknown" request on channel 1 is send a SoapException with message "UNKNOWN_DEVICE" is received

  Scenario: Couple inactive G-meter to an E-meter
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | Active                      | False             |
    When the Couple G-meter "TESTG102400000001" request on channel 1 is received
    Then the Couple response is "NOT OK" and contains
      | Device TESTG102400000001 is not active in the platform |

  Scenario: Couple G-meter to an inactive E-meter
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | Active               | False             |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
    When the Couple G-meter "TESTG102400000001" request on channel 1 is send a SoapException with message "INACTIVE_DEVICE" is received

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
    Then the Couple response is "NOT OK" and contains
     | SmartMeter with id "TESTunknownDevice" could not be found |

@Skip
  Scenario: DeCouple G-meter from unknown E-meter
    Given a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
    When the DeCouple G-meter "TESTG102400000001" from E-meter "TEST102400unknown" request is send a SoapException with message "UNKNOWN_DEVICE" is received

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
    Then the DeCouple response is "NOT OK" and contains
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
    When the DeCouple G-meter "TESTG102400000001" from E-meter "TEST1024000000001" request is send a SoapException with message "INACTIVE_DEVICE" is received

