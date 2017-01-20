Feature: Connection security
  As a grid operator
  I want to communicate with devices at different levels of security
  So the transferred data is as secure as possible

  Scenario: Communicate with HLS5 encryption
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | Hls3active           | false             |
      | Hls4active           | false             |
      | Hls5active           | true             |
    When the get actual meter reads request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the actual meter reads result should be returned
      | DeviceIdentification | TEST1024000000001 |
    
  # Needs a DlmsDevice simulator with security.enabled=false on port 1025  
@Skip
  Scenario: Communicate unencrypted
    Given a dlms device
      | DeviceIdentification | TEST1025000000001 |
      | DeviceType           | SMART_METER_E     |
      | Hls3active           | false             |
      | Hls4active           | false             |
      | Hls5active           | false             |
      | Port                 | 1025              |
    When the get actual meter reads request is received
      | DeviceIdentification | TEST1025000000001 |
    Then the actual meter reads result should be returned
      | DeviceIdentification | TEST1025000000001 |