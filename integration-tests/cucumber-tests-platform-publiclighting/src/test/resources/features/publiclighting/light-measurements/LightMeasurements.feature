##
# Copyright 2021 Alliander N.V.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
##
@PublicLighting @Platform @LightMeasurement
Feature: Light Measurement using IEC-60870 device
  As a grid operator
  I want to receive light measurement events
  So that I can switch public lighting based on light measurements

  #  Scenario: View events for a IEC 60870 light measurement device
  #    Given a light measurement gateway using IEC60870 protocol
  #    And a light measurement device using IEC60870 protocol
  #    And a light measurement event
  #    When I request the events for the light measurement device
  #    Then the light measurement event should be returned
  #
  #  Scenario: View device log messages for IEC 60870 light measurement gateway
  #    Given a light measurement gateway using IEC60870 protocol
  #    And a light measurement device using IEC60870 protocol
  #    And an existing connection with the light measurment gateway
  #    When I request the device log message for the light measurement gateway
  #    Then the device log message should be returned
  #
  ##  Scenario: View device log messages for IEC 60870 light measurement device
  ##    Given a light measurement gateway using IEC60870 protocol
  ##    And a light measurement device using IEC60870 protocol
  ##    And a device log message for the light measurement device
  ##    When I request the device log message for the light measurement device
  ##    Then the device log message should be returned
  #
  #  Scenario: View status for a IEC 60870 light measurement device
  #    Given a light measurement gateway using IEC60870 protocol
  #    And a light measurement device using IEC60870 protocol
  #    And the light measurement device has status
  #    When I request the status for the light measurement device
  #    Then the light measurement device status should be returned
 
  @OslpMockServer @Iec60870MockServerLightMeasurement
  Scenario Outline: Switch public lighting upon receiving a light measurement event from IEC light measurement device
    Given a light measurement gateway using IEC60870 protocol
      | DeviceIdentification | <LMG> |
    And a light measurement device using IEC60870 protocol
      | DeviceIdentification        | <LMD_1> |
      | GatewayDeviceIdentification | <LMG>   |
      | InformationObjectAddress    | <IOA_1> |
    And a light measurement device using IEC60870 protocol
      | DeviceIdentification        | <LMD_2> |
      | GatewayDeviceIdentification | <LMG>   |
      | InformationObjectAddress    | <IOA_2> |
    And an ssld oslp device
      | DeviceIdentification                 | <SSLD>     |
      | LightMeasurementDeviceIdentification | <LMD_1>    |
      | TechnicalInstallationDate            | 2020-01-01 |
    And an existing connection with the light measurement gateway
      | DeviceIdentification | <LMG> |
    When the light measurement gateway sends a light measurement event for the light measurement device
      | DeviceIdentification  | <LMD_1> |
      | LightMeasurementEvent | <EVENT> |
    Then the device message for the light measurement event should be logged
      | DeviceIdentification | <LMG> |
    And the light measurement event should be logged
      | DeviceIdentification  | <LMD_1> |
      | LightMeasurementEvent | <EVENT> |
    And a set transition message should be sent to the OSLP SSLD
      | TransitionType | <TRANSITION> |

    Examples: 
      | LMD_1 | IOA_1 | LMD_2 | IOA_2 | LMG   | SSLD   | EVENT | TRANSITION |
     # | LMD-1 |     1 | LMD-2 |     2 | LMG-1 | SSLD-1 | DARK  | DAY_NIGHT  |
      | LMD-1 |     1 | LMD-2 |     2 | LMG-1 | SSLD-1 | LIGHT | NIGHT_DAY  |
