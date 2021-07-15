# Needs a DlmsDevice simulator with e650 profile on port 1026

# This test is broken, needs fixing later when support for L+G E650 devices is needed:
@SmartMetering @Platform @SN @Skip
Feature: SmartMetering short names - Dedicated event logs

  Scenario Outline: Get dedicated event log capture objects from L+G E650
    Given a dlms device
      | DeviceIdentification | TEST1024000000005 |
      | DeviceType           | SMART_METER_E     |
      | UseSn                | true              |
      | UseHdlc              | true              |
      | Port                 |              1026 |
      | Hls5active           | false             |
      | Lls1active           | true              |
    When the get specific attribute value request is received
      | DeviceIdentification | TEST1024000000005 |
      | ClassId              |                 7 |
      | ObisCodeA            |                 1 |
      | ObisCodeB            |                 1 |
      | ObisCodeC            |                99 |
      | ObisCodeD            |                98 |
      | ObisCodeE            | <obiscodeE>       |
      | ObisCodeF            |               255 |
      | Attribute            |                 3 |
    Then a get specific attribute value response should be returned
      | DeviceIdentification | TEST1024000000005 |
      | Result               | OK                |
      | ResponsePart         | <responsePart>    |

    Examples: 
      | log            | obiscodeE | responsePart      |
      | UnderVoltageL1 |       152 | 1-1:32.23.0.255   |
      | UnderVoltageL2 |       153 | 1-1:52.23.0.255   |
      | UnderVoltageL3 |       154 | 1-1:72.23.0.255   |
      | OverVoltageL1  |       155 | 1-1:32.26.0.255   |
      | OverVoltageL2  |       156 | 1-1:52.26.0.255   |
      | OverVoltageL3  |       157 | 1-1:72.26.0.255   |
      | PhaseFailureL1 |       158 | 1-1:99.98.158.255 |
      | PhaseFailureL2 |       159 | 1-1:99.98.159.255 |
      | PhaseFailureL3 |       160 | 1-1:99.98.160.255 |
