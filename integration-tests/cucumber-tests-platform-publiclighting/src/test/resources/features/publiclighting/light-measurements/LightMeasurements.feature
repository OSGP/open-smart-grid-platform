# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@PublicLighting @Platform @LightMeasurement
Feature: Light Measurement using IEC-60870 device
  As a grid operator
  I want to receive light measurement events
  So that I can switch public lighting based on light measurements

  @OslpMockServer @Iec60870MockServerLightMeasurement
  Scenario Outline: Switch public lighting upon receiving a light measurement event
    Given a light measurement RTU using IEC60870 protocol
      | DeviceIdentification | <RTU> |
    And a light measurement device using IEC60870 protocol
      | DeviceIdentification        | <LMD> |
      | GatewayDeviceIdentification | <RTU> |
      | InformationObjectAddress    | <IOA> |
    And a process image on the IEC60870 server
      | InformationObjectAddress | InformationObjectType                 | InformationElementValue |
      |                        1 | SINGLE_POINT_INFORMATION_WITH_QUALITY | true                    |
      |                        2 | SINGLE_POINT_INFORMATION_WITH_QUALITY | false                   |
      |                        3 | SINGLE_POINT_INFORMATION_WITH_QUALITY | true                    |
      |                        4 | SINGLE_POINT_INFORMATION_WITH_QUALITY | false                   |
    And an ssld oslp device
      | DeviceIdentification                 | <SSLD>     |
      | LightMeasurementDeviceIdentification | <LMD>      |
      | TechnicalInstallationDate            | 2020-01-01 |
    And the device returns a set transition response "OK" over "OSLP ELSTER"
    And an existing connection with the RTU
      | DeviceIdentification | <RTU> |
    When the RTU sends a light measurement event
      | InformationObjectAddress | <IOA> |
      | SinglePointInformation   | <SPI> |
    Then the device message for the light measurement event should be logged
      | DeviceIdentification | <RTU> |
    And the light measurement event should be logged
      | DeviceIdentification  | <LMD>   |
      | LightMeasurementEvent | <EVENT> |
    And a set transition "OSLP ELSTER" message is sent to device "<SSLD>"
      | TransitionType | <TRANSITION> |

    Examples: 
      | LMD   | IOA | RTU   | SSLD   | SPI   | EVENT | TRANSITION |
      | LMD-1 |   1 | RTU-1 | SSLD-1 | false | LIGHT | NIGHT_DAY  |
      | LMD-2 |   2 | RTU-1 | SSLD-2 | true  | DARK  | DAY_NIGHT  |
