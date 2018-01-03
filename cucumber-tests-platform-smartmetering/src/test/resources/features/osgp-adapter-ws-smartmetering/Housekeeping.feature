@SmartMetering @Platform
Feature: SmartMetering Housekeeping
  As a ...
  I want to clean up the meter response data
  So that obsolete data is removed

  Scenario: Clean obsolete response data
    Given a smartmetering response data record
      | CreationTime   | now - 1 month                                |
      | CorrelationUid | test-org-TEST1024000000001-NOW-1-MONTH-00000 |
    When the smart metering response data cleanup job runs
    Then the response data with correlation uid "test-org-TEST1024000000001-NOW-1-MONTH-00000" should be deleted

  Scenario: Do not clean non-obsolete response data
    Given a smartmetering response data record
      | CreationTime   | now                                      |
      | CorrelationUid | test-org-TEST1024000000001-NOW-000000000 |
    When the smart metering response data cleanup job runs
    Then the response data with correlation uid "test-org-TEST1024000000001-NOW-000000000" should not be deleted
