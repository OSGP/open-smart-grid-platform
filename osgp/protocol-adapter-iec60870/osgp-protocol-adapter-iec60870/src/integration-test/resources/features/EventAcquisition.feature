@ProtocolAdapterIec60870
Feature: OSGP Protocol Adapter IEC60870 - Event Acquisition
    As OSGP controlling station
    I want to receive events of a controlled station
    So that I am able to monitor the controlled station

  Scenario: Receive light measurement event
    Given a controlled station "LMD_RTU_1"
    And a light sensor
      | device_identification         | LMD_1     |
      | gateway_device_identification | LMD_RTU_1 |
      | device_address                |         1 |
    And an active connection with the light measurement rtu "LMD_RTU_1"
    When a light measurement event occurs for address 1 with value "ON"
    Then the protocol adapter should send a light measurement event to osgp core
      | event_type            | LIGHT_SENSOR_REPORTS_DARK |
      | device_identification | LMD_1                     |
