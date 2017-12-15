@MicroGrids @Platform @TestThis
Feature: MicroGrids Housekeeping
  As a ...
  I want to clean up the rtu response data
  So that obsolete data is removed

  Scenario: Clean obsolete response data
    Given an rtu response data record
      | CreationTime   | 2017-01-01T00:00:00                          |
      | CorrelationUid | test-org-TEST1024000000001-20170101000000000 |
    When the rtu response data cleanup job runs
    Then the record with correlation uid "test-org-TEST1024000000001-20170101000000000" should be deleted

  Scenario: Do not clean non-obsolete response data
    Given an rtu response data record
      | CreationTime   | now                                          |
      | CorrelationUid | test-org-TEST1024000000001-NOW-000000000 |
    When the rtu response data cleanup job runs
    Then the record with correlation uid "test-org-TEST1024000000001-NOW-000000000" should not be deleted
