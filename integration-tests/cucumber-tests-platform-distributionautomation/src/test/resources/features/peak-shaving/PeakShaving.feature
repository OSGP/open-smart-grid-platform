Feature: Peak shaving
  
  Scenario: Get data from MQTT device
    When the mqtt device sends a measurement report
    | payload | TST-01; 220.1; 220.2; 220.3; 5.1; 5.2; 5.3; 7.1; 7.2; 7.3;|

