@DistributionAutomation @Platform
Feature: Dummy
  As a grid operator
  I want to ..
  So I ...

  Scenario: Dummy feature for initial testing
    Given an rtu device
      | DeviceIdentification | TEST1024000000001 |
      | Status               | Active            |
    And I wait 2 seconds
    When I start the simulator
    Then I stop the simulator
