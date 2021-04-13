@SmartMetering @Platform @GetModemInfo
Feature: SmartMetering Management - Get Modem Info
  As a grid operator
  I want to be able to get the modem info of a smart meter

  Scenario: Get the modem info of a E-Meter
    And a dlms device
      | DeviceIdentification | TEST1027000000001 |
      | DeviceType           | SMART_METER_E     |
      | CommunicationMethod  | GPRS              |
      | Protocol             | SMR               |
      | ProtocolVersion      |               5.1 |
      | Port                 |              1027 |
    When a get modem info request is received
      | DeviceIdentification | TEST1027000000001 |
    Then the get modem info response is returned with values
      | operator                  | Utility Connect          |
      | modemRegistrationStatus   | REGISTERED_ROAMING       |
      | circuitSwitchedStatus     | INACTIVE                 |
      | packetSwitchedStatus      | GPRS                     |
      | cellId                    | cid1                     |
      | locationId                | lo                       |
      | signalQuality             | MINUS_87_DBM             |
      | bitErrorRate              | RXQUAL_6                 |
      | mobileCountryCode         |                       66 |
      | mobileNetworkCode         |                      204 |
      | channelNumber             |                      107 |
      | numberOfAdjacentCells     |                        1 |
      | adjacentCellId            | cid2                     |
      | adjacentCellSignalQuality | MINUS_91_DBM             |
      | captureTime               | 2021-04-13T08:45:00.000Z |
