@SmartMetering @Platform @NightlyBuildOnly @DSMR22
Feature: SmartMetering Connection security
  As a grid operator
  I want to communicate with devices at different levels of security
  So the transferred data is as secure as possible

  # Needs a DlmsDevice simulator with security level 1 on port 1026
  Scenario: Communicate with LLS1 without sn and hdlc with simulator supporting encrypted communication
    Given a dlms device
      | DeviceIdentification | TEST1022000000001 |
      | DeviceType           | SMART_METER_E     |
      | Hls3active           | false             |
      | Hls4active           | false             |
      | Hls5active           | false             |
      | UseSn                | false             |
      | UseHdlc              | false             |
      | Lls1active           | true              |
      | Port                 |              1026 |
    When the get actual meter reads request is received
      | DeviceIdentification | TEST1022000000001 |
    Then the actual meter reads result should be returned
      | DeviceIdentification | TEST1022000000001 |

  # Needs a DlmsDevice simulator with security level 0 on port 1025
  Scenario: Communicate with LLS0 with simulator supporting unencrypted communication
    Given a dlms device
      | DeviceIdentification | TEST1025000000001 |
      | DeviceType           | SMART_METER_E     |
      | Hls3active           | false             |
      | Hls4active           | false             |
      | Hls5active           | false             |
      | Lls1active           | false             |
      | Port                 |              1025 |
    When the get actual meter reads request is received
      | DeviceIdentification | TEST1025000000001 |
    Then the actual meter reads result should be returned
      | DeviceIdentification | TEST1025000000001 |
