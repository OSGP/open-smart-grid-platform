@DistributionAutomation @Platform @MeasurementReports
Feature: DistributionAutomation Receive measurement reports
  As a grid operator
  I want to receive measurement reports from an IEC 60870 device
  So I get updates about the current state of the device

  Scenario: Connect to an IEC 60870 device
    Given an IEC 60870 RTU
      | DeviceIdentification | TEST1024000000001 |
      | Status               | Active            |
      | Port                 |             62404 |
      | CommonAddress        |                75 |
    When Organization test-org connects to device TEST1024000000001
    Then I get a measurement report for device TEST1024000000001
      |  |  |

  Scenario: Update process image of an IEC 60870 device
    Given an IEC 60870 RTU
      | DeviceIdentification | TEST1024000000002 |
      | Status               | Active            |
      | Port                 |             62404 |
      | CommonAddress        |                75 |
    When Organization test-org connects to device TEST1024000000002
    And I update the information object
      | InformationObjectAddress |            2 |
      | InformationObjectType    | IeShortFloat |
      | InformationElementValue  |         10.0 |
    Then I get a measurement report for device TEST1024000000002
      | InformationObjectAddress |                       2 |
      | InformationObjectType    | FloatMeasurementElement |
      | InformationElementValue  |                    10.0 |
