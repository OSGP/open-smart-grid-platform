@DistributionAutomation @Platform @MeasurementReports
Feature: DistributionAutomation Receive measurement reports
  As a grid operator
  I want to receive measurement reports from an IEC 60870 device
  So I get updates about the current state of the device

  @Iec60870MockServerDefaultControlledStation
  Scenario: Connect to an IEC 60870 device
    Given an IEC 60870 RTU
      | DeviceIdentification | TEST1024000000001 |
      | Status               | Active            |
    When Organization test-org connects to device TEST1024000000001
    Then I receive a measurement report for device TEST1024000000001

  @Iec60870MockServerDefaultControlledStation
  Scenario: Update process image of default controlled station
    Given an IEC 60870 RTU
      | DeviceIdentification | TEST1024000000002 |
      | Status               | Active            |
    When Organization test-org connects to device TEST1024000000002
    And the process image on the IEC60870 server changes
      | InformationObjectAddress | InformationObjectType | InformationElementValue |
      |                        2 | SHORT_FLOAT           |                    10.0 |
    Then I get a measurement report for device TEST1024000000002 with values
      | InformationObjectAddress |            2 |
      | InformationObjectType    | IeShortFloat |
      | InformationElementValue  |         10.0 |
