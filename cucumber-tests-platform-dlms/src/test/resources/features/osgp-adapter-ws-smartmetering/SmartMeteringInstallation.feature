Feature: SmartMetering Installation
  As a grid operator
  I want to be able to perform SmartMeteringInstallation operations on a device
    
Scenario: Add a new device
     When receiving an add device request
        | DeviceIdentification   | E0026000059790003 |
        | DeviceType             | SMART_METER_E     |
     Then the add device response contains 
        | DeviceIdentification   | E0026000059790003 |
      And receiving an get add device response request
        | DeviceIdentification   | E0026000059790003 |
      And the get add device request response should be ok
      And the device with id "E0026000059790003" should be added in the core database
      And the device with id "E0026000059790003" should be added in the dlms database