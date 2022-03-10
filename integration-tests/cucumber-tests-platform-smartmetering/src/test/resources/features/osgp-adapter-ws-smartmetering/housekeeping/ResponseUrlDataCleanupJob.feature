@SmartMetering @Platform @NightlyBuildOnly @Housekeeping
Feature: SmartMetering Housekeeping - Response Url Data Cleanup Job
  As a grid operator
  I want to clean up the response url data
  So that obsolete data is removed

  Scenario: Clean obsolete response url data
    Given a response url data record in ws-smartmetering
      | CreationTime   | now - 1 months                               |
      | CorrelationUid | test-org-TEST1024000000001-NOW-1-MONTH-00000 |
      | ResponseUrl    | http://localhost:8189/notifications/         |
    When the response url data cleanup job runs
    Then the cleanup job should have removed the response url data with correlation uid "test-org-TEST1024000000001-NOW-1-MONTH-00000"

  Scenario: Do not clean non-obsolete response url data
    Given a response url data record in ws-smartmetering
      | CreationTime   | now                                      |
      | CorrelationUid | test-org-TEST1024000000001-NOW-000000000 |
      | ResponseUrl    | http://localhost:8189/notifications/     |
    When the response url data cleanup job runs
    Then the cleanup job should not have removed the response url data with correlation uid "test-org-TEST1024000000001-NOW-000000000"
