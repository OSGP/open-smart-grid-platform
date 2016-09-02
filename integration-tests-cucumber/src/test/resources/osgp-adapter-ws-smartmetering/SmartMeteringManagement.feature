Feature: SmartMetering Management
  As a grid operator
  I want to be able to perform SmartMeteringManagement operations on a device
    
Background:
    Given a device with DeviceID "TEST1024000000001" 
      And an organisation with OrganisationID "Test Organisation"
    
@SLIM-150 @SmartMeterManagement
Scenario: find standard events from a device
     When the find standard events request is received
     Then standard events should be returned

@SLIM-150 @SmartMeterManagement
Scenario: find fraud events from a device
     When the find fraud events request is received
     Then fraud events should be returned

@SLIM-150 @SmartMeterManagement
Scenario: find communication events from a device
     When the find communication events request is received
     Then communication events should be returned

@SLIM-150 @SmartMeterManagement
Scenario: find mbus events from a device
     When the find mbus events request is received
     Then mbus events should be returned        