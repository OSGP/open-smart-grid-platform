# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @GetGsmDiagnostic
Feature: SmartMetering Management - Get GSM Diagnostic
  As a grid operator
  I want to be able to get the GSM Diagnostic of a smart meter

  Scenario Outline: Get the gsm diagnostic of an E-Meter for <CommunicationMethod>
    Given
    Given a manufacturer
      | ManufacturerCode | KAI   |
      | ManufacturerName | Kaifa |
    And a device model
      | ManufacturerName | Kaifa |
      | ModelCode        | <ModelCode> |
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
      | packetSwitchedStatus        | <CommunicationMethod>     |
      | cellId                      |                        77 |
      | locationId                  |                      2230 |
      | signalQuality               | MINUS_87_DBM              |
      | bitErrorRate                |            <BitErrorRate> |
      | mobileCountryCode           |                        66 |
      | mobileNetworkCode           |                       204 |
      | channelNumber               |                       107 |
      | adjacentCellIds             |                     93,94 |
      | adjacentCellSignalQualities | MINUS_91_DBM,MINUS_89_DBM |
# Reading of captureTime is disabled for now
#      | captureTime               | 2021-04-13T08:45:00.000Z |
  Examples:
    | DeviceIdentification | Port | Protocol | ProtocolVersion | CommunicationMethod | ModelCode | BitErrorRate |
    | TEST1031000000001    | 1031 | SMR      | 4.3             | CDMA                | MA105A    |            6 |
    | TEST1027000000001    | 1027 | SMR      | 5.0.0           | CDMA                | MA105A    |            6 |
    | TEST1027000000001    | 1027 | SMR      | 5.0.0           | GPRS                | MA105     |            6 |
    | TEST1029000000001    | 1029 | SMR      | 5.2             | CDMA                | MA105A    |            6 |
    | TEST1029000000001    | 1029 | SMR      | 5.2             | GPRS                | MA105     |            6 |
    | TEST1029000000001    | 1029 | SMR      | 5.2             | LTE                 | MA105A    |            6 |
