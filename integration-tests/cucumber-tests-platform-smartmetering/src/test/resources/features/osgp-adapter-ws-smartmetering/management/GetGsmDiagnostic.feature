@SmartMetering @Platform @GetGsmDiagnostic
Feature: SmartMetering Management - Get GSM Diagnostic
  As a grid operator
  I want to be able to get the GSM Diagnostic of a smart meter

  Scenario Outline: Get the gsm diagnostic of an E-Meter
    And a dlms device
      | DeviceIdentification | <DeviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | CommunicationMethod  | <CommunicationMethod>  |
      | Protocol             | <Protocol>             |
      | ProtocolVersion      | <ProtocolVersion>      |
      | Port                 | <Port>                 |
    When a get gsm diagnostic request is received
      | DeviceIdentification | <DeviceIdentification> |
    Then the get gsm diagnostic response is returned with values
      | operator                    | Utility Connect           |
      | modemRegistrationStatus     | REGISTERED_ROAMING        |
      | circuitSwitchedStatus       | INACTIVE                  |
      | packetSwitchedStatus        | GPRS                      |
      | cellId                      |                        77 |
      | locationId                  |                      2230 |
      | signalQuality               | MINUS_87_DBM              |
      | bitErrorRate                | RXQUAL_6                  |
      | mobileCountryCode           |                        66 |
      | mobileNetworkCode           |                       204 |
      | channelNumber               |                       107 |
      | adjacentCellIds             |                     93,94 |
      | adjacentCellSignalQualities | MINUS_91_DBM,MINUS_89_DBM |
# Reading of captureTime is disabled for now
#      | captureTime               | 2021-04-13T08:45:00.000Z |
  Examples:
    | DeviceIdentification  | CommunicationMethod | Protocol | ProtocolVersion | Port |
    | TEST1028000000001     | GPRS                | SMR      | 5.1             | 1028 |
    | TEST1025000000001     | CDMA                | DSMR     | 4.3             | 1025 |
