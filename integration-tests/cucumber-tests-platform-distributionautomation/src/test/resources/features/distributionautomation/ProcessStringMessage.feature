@DistributionAutomation @Platform @StringMessage
Feature: DistributionAutomation String message processing

  Scenario: Process a String message from MQTT device
    When an MQTT message is published
      | MqttTopic | TST-01/measurement                                              |
      | payload   | {"measurement-data": [0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0]} |
    Then a String message is published to Kafka
      | payload | {"measurement-data": [0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0]} |
