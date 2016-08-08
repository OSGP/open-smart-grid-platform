Feature: Firmware Management
  As a grid operator
  I want to be able to get the firmware version from a device
  So that I can see which firmware version is installed on the device

Scenario Outline: Get the firmware version from device
    Given a device 
        | Field            | Value              | 
        | DeviceID         | device-01          | 
        | Firmware Version | <Firmware Version> | 
      And an organisation 
        | Field          | Value      | 
        | OrganisationID | Infostroom |
     When the get firmware version request is received
        | Field          | Value      | 
        | DeviceID       | device-01  |
        | OrganisationID | Infostroom |
     Then the get firmware response is
        | Field            | Value  |
        | Firmware Version | <Firmware Version> |
        
Examples:
        | Firmware Version |
        | v.0.0.1          |
        | v.0.0.2          |
        |                  |

