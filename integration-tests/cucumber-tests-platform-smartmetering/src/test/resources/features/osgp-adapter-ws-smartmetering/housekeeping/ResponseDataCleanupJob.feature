@SmartMetering @Platform @NightlyBuildOnly
Feature: SmartMetering Housekeeping - Response Data Cleanup Job
  As a ...
  I want to clean up the meter response data
  So that obsolete data is removed

  Scenario: Clean obsolete response data
    Given a response data record
      | CreationTime              | now - 1 months                               |
      | CorrelationUid            | test-org-TEST1024000000001-NOW-1-MONTH-00000 |
      | NumberOfNotificationsSent |                                            5 |
    When the response data cleanup job runs
    Then the cleanup job should have removed the response data with correlation uid "test-org-TEST1024000000001-NOW-1-MONTH-00000"

  Scenario: Do not clean non-obsolete response data
    Given a response data record
      | CreationTime              | now                                      |
      | CorrelationUid            | test-org-TEST1024000000001-NOW-000000000 |
      | NumberOfNotificationsSent |                                        5 |
    When the response data cleanup job runs
    Then the cleanup job should not have removed the response data with correlation uid "test-org-TEST1024000000001-NOW-000000000"
