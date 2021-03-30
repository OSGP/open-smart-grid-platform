@SmartMetering @Platform @GetModemInfo
Feature: SmartMetering Management - Get Modem Info
  As a grid operator
  I want to be able to get the modem info of a smart meter

  Scenario: Get the modem info of a E-Meter
    And a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5 | unsigned             |        9 |
      | 6 | double-long-unsigned | 12056731 |
      | 7 | long-unsigned        |    12514 |
      | 8 | unsigned             |       66 |
      | 9 | unsigned             |        3 |
    When a get modem info request is received
    Then the get modem info response is returned
      | Operator                  | 1 |
      | ModemRegistrationStatus   | 1 |
      | CircuitSwitchedStatus     | 1 |
      | PacketSwitchedStatus      | 1 |
      | CellId                    | 1 |
      | LocationId                | 1 |
      | SignalQuality             | 1 |
      | BitErrorRate              | 1 |
      | MobileCountryCode         | 1 |
      | MobileNetworkCode         | 1 |
      | ChannelNumber             | 1 |
      | NumberOfAdjacentCells     | 1 |
      | AdjacantCellId            | 1 |
      | AdjacantCellSignalQuality | 1 |
      | CaptureTime               | 1 |
