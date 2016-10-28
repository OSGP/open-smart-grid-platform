Feature: SmartMetering Notification
  As a grid operator
  I want to be able to perform SmartMeteringNotification operations on a device

  Background: 
    Given a dlms device
      | DeviceIdentification | E9998000014123414 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification        | G00XX561204926013 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | E9998000014123414 |
