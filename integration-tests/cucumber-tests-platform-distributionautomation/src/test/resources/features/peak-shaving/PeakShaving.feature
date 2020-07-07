Feature: Peak shaving

  Scenario: Get data from MQTT device
    Given an MQTT device
      | DeviceIdentification | TST-01             |
      | IntegrationType      | Kafka              |
      | MqttTopic            | TST-01/measurement |
    When MQTT device "TST-01" sends a measurement report
      | payload | TST-01; 220.1; 220.2; 220.3; 5.1; 5.2; 5.3; 7.1; 7.2; 7.3; |
    Then a message is published to Kafka
      | message | TST-01; 220.1; 220.2; 220.3; 5.1; 5.2; 5.3; 7.1; 7.2; 7.3; |
