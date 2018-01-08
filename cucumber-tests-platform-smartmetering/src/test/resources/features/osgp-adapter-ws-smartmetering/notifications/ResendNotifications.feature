@SmartMetering @Platform
Feature: SmartMetering Housekeeping - Response Data Cleanup Job
  As an OSGP user
  I want the platform to resend missed notifications
  So the notification mechanism is more robust

  @test
  Scenario: Resend missed notifications
    Given a response data record
      | DeviceIdentification      | TEST1024000000001                                      |
      | CreationTime              | now - 1 years                                          |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170101000000000 |
      | NumberOfNotificationsSend |                                                      0 |
    When OSGP checks for which response data a notification has to be resend
    Then a notification is sent
      | CorrelationUid | test-org\|\|\|TEST1024000000001\|\|\|20170101000000000 |
    And a record in the response_data table of the database has values
      | CreationTime                 | now - 1 years                                          |
      | CorrelationUid               | test-org\|\|\|TEST1024000000001\|\|\|20170101000000000 |
      | number_of_notifications_send |                                                      1 |
      | modification_time            | now                                                    |

  Scenario: Don't send notifications when the configurable time has not passed
    Given a response data record
      | CreationTime              | now                                                    |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170101000000000 |
      | NumberOfNotificationsSend |                                                      0 |
    When OSGP checks for which response data a notification has to be resend
    Then no notification is sent

  Scenario: Don't send notifications when the maximum of notifications sent has been reached
    Given a response data record
      | CreationTime              | now - 1 years                                          |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170101000000000 |
      | NumberOfNotificationsSend |                                                      3 |
    When OSGP checks for which response data a notification has to be resend
    Then no notification is sent
