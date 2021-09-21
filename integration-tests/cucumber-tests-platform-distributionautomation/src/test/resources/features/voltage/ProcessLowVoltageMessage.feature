@DistributionAutomation @Platform @LowVoltageMessage
Feature: DistributionAutomation Low voltage message processing

  Scenario: Process a low voltage message from MQTT device version 2
    Given an MQTT device
      | DeviceIdentification | TST-01             |
      | IntegrationType      | Kafka              |
      | MqttTopic            | TST-01/measurement |
    And a location
      | substation identification | sub-1        |
      | substation name           | substation-1 |
    And a feeder
      | substation identification | sub-1 |
      | feeder number             |     1 |
      | field code                |    01 |
      | feeder name               | fdr-1 |
      | asset label               | lbl-1 |
    When MQTT device "TST-01" sends a measurement report
      | payload | [{"gisnr":"sub-1", "versie":"2", "feeder":"1", "D": "02/10/2020 16:03:38", "uts":"1601647418", "data": [0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0,1.1,1.2,1.3,1.4,1.5,1.6,1.7,1.8,1.9,2.0,2.1,2.2,2.3,2.4,2.5,2.6,2.7,2.8,2.9,3.0,3.1,3.2,3.3,3.4,3.5,3.6,3.7,3.8,3.9,4.0,4.1,4.2,4.3,4.4,4.5,4.6,4.7,4.8,4.9]}] |
    Then a LOW_VOLTAGE message is published to Kafka
      | substation identification    | sub-1        |
      | version                      |            2 |
      | substation name              | substation-1 |
      | field code                   | 01           |
      | bay identification           | fdr-1        |
      | asset label                  | lbl-1        |
      | numberOfElements             |           49 |
      | measurement1_description     | U-avg        |
      | measurement1_unitSymbol      | V            |
      | measurement1_value           |          0.1 |
      | measurement2_description     | I-L1         |
      | measurement2_unitSymbol      | A            |
      | measurement2_value           |          0.2 |
      | measurement3_description     | I-L2         |
      | measurement3_unitSymbol      | A            |
      | measurement3_value           |          0.3 |
      | measurement4_description     | I-L3         |
      | measurement4_unitSymbol      | A            |
      | measurement4_value           |          0.4 |
      | measurement5_description     | Tot-P        |
      | measurement5_unitSymbol      | W            |
      | measurement5_unitMultiplier  | k            |
      | measurement5_value           |          0.5 |
      | measurement6_description     | Tot-Q        |
      | measurement6_unitSymbol      | VAr          |
      | measurement6_unitMultiplier  | k            |
      | measurement6_value           |          0.6 |
      | measurement7_description     | P-L1         |
      | measurement7_unitSymbol      | W            |
      | measurement7_unitMultiplier  | k            |
      | measurement7_value           |          0.7 |
      | measurement8_description     | P-L2         |
      | measurement8_unitSymbol      | W            |
      | measurement8_unitMultiplier  | k            |
      | measurement8_value           |          0.8 |
      | measurement9_description     | P-L3         |
      | measurement9_unitSymbol      | W            |
      | measurement9_unitMultiplier  | k            |
      | measurement9_value           |          0.9 |
      | measurement10_description    | Q-L1         |
      | measurement10_unitSymbol     | VAr          |
      | measurement10_unitMultiplier | k            |
      | measurement10_value          |          1.0 |
      | measurement11_description    | Q-L2         |
      | measurement11_unitSymbol     | VAr          |
      | measurement11_unitMultiplier | k            |
      | measurement11_value          |          1.1 |
      | measurement12_description    | Q-L3         |
      | measurement12_unitSymbol     | VAr          |
      | measurement12_unitMultiplier | k            |
      | measurement12_value          |          1.2 |
      | measurement13_description    | PF-L1        |
      | measurement13_unitSymbol     | none         |
      | measurement13_value          |          1.3 |
      | measurement14_description    | PF-L2        |
      | measurement14_unitSymbol     | none         |
      | measurement14_value          |          1.4 |
      | measurement15_description    | PF-L3        |
      | measurement15_unitSymbol     | none         |
      | measurement15_value          |          1.5 |
      | measurement16_description    | THDi-L1      |
      | measurement16_unitSymbol     | PerCent      |
      | measurement16_value          |          1.6 |
      | measurement17_description    | THDi-L2      |
      | measurement17_unitSymbol     | PerCent      |
      | measurement17_value          |          1.7 |
      | measurement18_description    | THDi-L3      |
      | measurement18_unitSymbol     | PerCent      |
      | measurement18_value          |          1.8 |
      | measurement19_description    | H3-I1        |
      | measurement19_unitSymbol     | A            |
      | measurement19_value          |          1.9 |
      | measurement20_description    | H3-I2        |
      | measurement20_unitSymbol     | A            |
      | measurement20_value          |          2.0 |
      | measurement21_description    | H3-I3        |
      | measurement21_unitSymbol     | A            |
      | measurement21_value          |          2.1 |
      | measurement22_description    | H5-I1        |
      | measurement22_unitSymbol     | A            |
      | measurement22_value          |          2.2 |
      | measurement23_description    | H5-I2        |
      | measurement23_unitSymbol     | A            |
      | measurement23_value          |          2.3 |
      | measurement24_description    | H5-I3        |
      | measurement24_unitSymbol     | A            |
      | measurement24_value          |          2.4 |
      | measurement25_description    | H7-I1        |
      | measurement25_unitSymbol     | A            |
      | measurement25_value          |          2.5 |
      | measurement26_description    | H7-I2        |
      | measurement26_unitSymbol     | A            |
      | measurement26_value          |          2.6 |
      | measurement27_description    | H7-I3        |
      | measurement27_unitSymbol     | A            |
      | measurement27_value          |          2.7 |
      | measurement28_description    | H9-I1        |
      | measurement28_unitSymbol     | A            |
      | measurement28_value          |          2.8 |
      | measurement29_description    | H9-I2        |
      | measurement29_unitSymbol     | A            |
      | measurement29_value          |          2.9 |
      | measurement30_description    | H9-I3        |
      | measurement30_unitSymbol     | A            |
      | measurement30_value          |          3.0 |
      | measurement31_description    | H11-I1       |
      | measurement31_unitSymbol     | A            |
      | measurement31_value          |          3.1 |
      | measurement32_description    | H11-I2       |
      | measurement32_unitSymbol     | A            |
      | measurement32_value          |          3.2 |
      | measurement33_description    | H11-I3       |
      | measurement33_unitSymbol     | A            |
      | measurement33_value          |          3.3 |
      | measurement34_description    | H13-I1       |
      | measurement34_unitSymbol     | A            |
      | measurement34_value          |          3.4 |
      | measurement35_description    | H13-I2       |
      | measurement35_unitSymbol     | A            |
      | measurement35_value          |          3.5 |
      | measurement36_description    | H13-I3       |
      | measurement36_unitSymbol     | A            |
      | measurement36_value          |          3.6 |
      | measurement37_description    | H15-I1       |
      | measurement37_unitSymbol     | A            |
      | measurement37_value          |          3.7 |
      | measurement38_description    | H15-I2       |
      | measurement38_unitSymbol     | A            |
      | measurement38_value          |          3.8 |
      | measurement39_description    | H15-I3       |
      | measurement39_unitSymbol     | A            |
      | measurement39_value          |          3.9 |
      | measurement40_description    | IrmsN        |
      | measurement40_unitSymbol     | A            |
      | measurement40_value          |          4.0 |
      | measurement41_description    | Pp           |
      | measurement41_unitSymbol     | none         |
      | measurement41_value          |          4.1 |
      | measurement42_description    | Pm           |
      | measurement42_unitSymbol     | none         |
      | measurement42_value          |          4.2 |
      | measurement43_description    | Qp           |
      | measurement43_unitSymbol     | none         |
      | measurement43_value          |          4.3 |
      | measurement44_description    | Qm           |
      | measurement44_unitSymbol     | none         |
      | measurement44_value          |          4.4 |
      | measurement45_description    | U-L1         |
      | measurement45_unitSymbol     | V            |
      | measurement45_value          |          4.5 |
      | measurement46_description    | U-L2         |
      | measurement46_unitSymbol     | V            |
      | measurement46_value          |          4.6 |
      | measurement47_description    | U-L3         |
      | measurement47_unitSymbol     | V            |
      | measurement47_value          |          4.7 |
      | measurement48_description    | Temp         |
      | measurement48_unitSymbol     | C            |
      | measurement48_value          |          4.8 |
      | measurement49_description    | F            |
      | measurement49_unitSymbol     | Hz           |
      | measurement49_value          |          4.9 |

@Skip      
Scenario: Process a low voltage message from MQTT device version 1
    Given an MQTT device
      | DeviceIdentification | TST-02             |
      | IntegrationType      | Kafka              |
      | MqttTopic            | TST-02/measurement |
    And a location
      | substation identification | sub-2        |
      | substation name           | substation-2 |
    And a feeder
      | substation identification | sub-2 |
      | feeder number             |     2 |
      | field code                |    02 |
      | feeder name               | fdr-2 |
      | asset label               | lbl-2 |
    When MQTT device "TST-02" sends a measurement report
      | payload | [{"gisnr":"sub-2", "versie":"1", "feeder":"2", "D": "02/10/2020 16:03:38", "uts":"1601647418", "data": [0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0,1.1,1.2,1.3,1.4,1.5,1.6,1.7,1.8,1.9,2.0,2.1,2.2,2.3,2.4,2.5,2.6,2.7,2.8,2.9,3.0,3.1,3.2,3.3,3.4,3.5,3.6,3.7,3.8,3.9,4.0,4.1]}] |
    Then a LOW_VOLTAGE message is published to Kafka
      | substation identification    | sub-2        |
      | version                      |            1 |
      | substation name              | substation-2 |
      | field code                   | 02           |
      | bay identification           | fdr-2        |
      | asset label                  | lbl-2        |
      | numberOfElements             |           41 |
      | measurement1_description     | U-L1         |
      | measurement1_unitSymbol      | V            |
      | measurement1_value           |          0.1 |
      | measurement2_description     | U-L2         |
      | measurement2_unitSymbol      | V            |
      | measurement2_value           |          0.2 |
      | measurement3_description     | U-L3         |
      | measurement3_unitSymbol      | V            |
      | measurement3_value           |          0.3 |
      | measurement4_description     | I-L1         |
      | measurement4_unitSymbol      | A            |
      | measurement4_value           |          0.4 |
      | measurement5_description     | I-L2         |
      | measurement5_unitSymbol      | A            |
      | measurement5_value           |          0.5 |
      | measurement6_description     | I-L3         |
      | measurement6_unitSymbol      | A            |
      | measurement6_value           |          0.6 |
      | measurement7_description     | Tot-P        |
      | measurement7_unitSymbol      | W            |
      | measurement7_unitMultiplier  | k            |
      | measurement7_value           |          0.7 |
      | measurement8_description     | Tot-Q        |
      | measurement8_unitSymbol      | VAr          |
      | measurement8_unitMultiplier  | k            |
      | measurement8_value           |          0.8 |
      | measurement9_description     | P-L1         |
      | measurement9_unitSymbol      | W            |
      | measurement9_unitMultiplier  | k            |
      | measurement9_value           |          0.9 |
      | measurement10_description    | P-L2         |
      | measurement10_unitSymbol     | W            |
      | measurement10_unitMultiplier | k            |
      | measurement10_value          |          1.0 |
      | measurement11_description    | P-L3         |
      | measurement11_unitSymbol     | W            |
      | measurement11_unitMultiplier | k            |
      | measurement11_value          |          1.1 |
      | measurement12_description    | Q-L1         |
      | measurement12_unitSymbol     | VAr          |
      | measurement12_unitMultiplier | k            |
      | measurement12_value          |          1.2 |
      | measurement13_description    | Q-L2         |
      | measurement13_unitSymbol     | VAr          |
      | measurement13_unitMultiplier | k            |
      | measurement13_value          |          1.3 |
      | measurement14_description    | Q-L3         |
      | measurement14_unitSymbol     | VAr          |
      | measurement14_unitMultiplier | k            |
      | measurement14_value          |          1.4 |
      | measurement15_description    | PF-L1        |
      | measurement15_unitSymbol     | none         |
      | measurement15_value          |          1.5 |
      | measurement16_description    | PF-L2        |
      | measurement16_unitSymbol     | none         |
      | measurement16_value          |          1.6 |
      | measurement17_description    | PF-L3        |
      | measurement17_unitSymbol     | none         |
      | measurement17_value          |          1.7 |
      | measurement18_description    | THDi-L1      |
      | measurement18_unitSymbol     | PerCent      |
      | measurement18_value          |          1.8 |
      | measurement19_description    | THDi-L2      |
      | measurement19_unitSymbol     | PerCent      |
      | measurement19_value          |          1.9 |
      | measurement20_description    | THDi-L3      |
      | measurement20_unitSymbol     | PerCent      |
      | measurement20_value          |          2.0 |
      | measurement21_description    | H3-I1        |
      | measurement21_unitSymbol     | A            |
      | measurement21_value          |          2.1 |
      | measurement22_description    | H3-I2        |
      | measurement22_unitSymbol     | A            |
      | measurement22_value          |          2.2 |
      | measurement23_description    | H3-I3        |
      | measurement23_unitSymbol     | A            |
      | measurement23_value          |          2.3 |
      | measurement24_description    | H5-I1        |
      | measurement24_unitSymbol     | A            |
      | measurement24_value          |          2.4 |
      | measurement25_description    | H5-I2        |
      | measurement25_unitSymbol     | A            |
      | measurement25_value          |          2.5 |
      | measurement26_description    | H5-I3        |
      | measurement26_unitSymbol     | A            |
      | measurement26_value          |          2.6 |
      | measurement27_description    | H7-I1        |
      | measurement27_unitSymbol     | A            |
      | measurement27_value          |          2.7 |
      | measurement28_description    | H7-I2        |
      | measurement28_unitSymbol     | A            |
      | measurement28_value          |          2.8 |
      | measurement29_description    | H7-I3        |
      | measurement29_unitSymbol     | A            |
      | measurement29_value          |          2.9 |
      | measurement30_description    | H9-I1        |
      | measurement30_unitSymbol     | A            |
      | measurement30_value          |          3.0 |
      | measurement31_description    | H9-I2        |
      | measurement31_unitSymbol     | A            |
      | measurement31_value          |          3.1 |
      | measurement32_description    | H9-I3        |
      | measurement32_unitSymbol     | A            |
      | measurement32_value          |          3.2 |
      | measurement33_description    | H11-I1       |
      | measurement33_unitSymbol     | A            |
      | measurement33_value          |          3.3 |
      | measurement34_description    | H11-I2       |
      | measurement34_unitSymbol     | A            |
      | measurement34_value          |          3.4 |
      | measurement35_description    | H11-I3       |
      | measurement35_unitSymbol     | A            |
      | measurement35_value          |          3.5 |
      | measurement36_description    | H13-I1       |
      | measurement36_unitSymbol     | A            |
      | measurement36_value          |          3.6 |
      | measurement37_description    | H13-I2       |
      | measurement37_unitSymbol     | A            |
      | measurement37_value          |          3.7 |
      | measurement38_description    | H13-I3       |
      | measurement38_unitSymbol     | A            |
      | measurement38_value          |          3.8 |
      | measurement39_description    | H15-I1       |
      | measurement39_unitSymbol     | A            |
      | measurement39_value          |          3.9 |
      | measurement40_description    | H15-I2				|
      | measurement40_unitSymbol     | A            |
      | measurement40_value          |          4.0 |
      | measurement41_description    | H15-I3       |
      | measurement41_unitSymbol     | A            |
      | measurement41_value          |          4.1 |
