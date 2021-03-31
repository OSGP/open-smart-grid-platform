@SmartMetering @Platform @GetModemInfo
Feature: SmartMetering Bundle - Get Modem Info
  As a grid operator
  I want to be able to get the modem info of a smart meter

  Scenario: Get the modem info of a E-Meter
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a get modem info action
    And a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | CommunicationMethod  | GPRS              |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5 | unsigned             |        9 |
      | 6 | double-long-unsigned | 12056731 |
      | 7 | long-unsigned        |    12514 |
      | 8 | unsigned             |       66 |
      | 9 | unsigned             |        3 |
    When the bundle request is received
    Then the bundle response should contain a get modem info response with values
      | operator                  | operator                 |
      | modemRegistrationStatus   | REGISTERED_ROAMING       |
      | circuitSwitchedStatus     | ACTIVE                   |
      | packetSwitchedStatus      | CDMA                     |
      | cellId                    | cellId                   |
      | locationId                | locationId               |
      | signalQuality             | MINUS_61_DBM             |
      | bitErrorRate              | RXQUAL_2                 |
      | mobileCountryCode         |                       31 |
      | mobileNetworkCode         |                        0 |
      | channelNumber             |                        1 |
      | numberOfAdjacentCells     |                        3 |
      | adjacantCellId            | adjacantCellId           |
      | adjacantCellSignalQuality | MINUS_83_DBM             |
      | captureTime               | 2021-03-31T04:05:06.000Z |
