@SmartMetering @Platform 
Feature: SmartMetering Housekeeping - Response Data Cleanup Job
  As an OSGP user
  I want the platform to resend missed notifications
  So the notification mechanism is more robust
@test
  Scenario: Resend missed notifications
    Given a response data record
      | DeviceIdentification      | TEST1024000000001                                      |
      | CreationTime              | yesterday                                              |
      | MessageType               | GET_DATA                                               |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170101000000000 |
      | NumberOfNotificationsSend |                                                      0 |
    When the missed notification is resend
      | CorrelationUid | test-org\|\|\|TEST1024000000001\|\|\|20170101000000000 |
    Then a record in the response_data table of the database has values
      | DeviceIdentification      | TEST1024000000001                                      |
      | MessageType               | REQUEST_PERIODIC_METER_DATA                            |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170101000000000 |
      | NumberOfNotificationsSend |                                                      1 |

  Scenario: Don't send notifications when the configurable time has not passed
    Given a response data record
      | CreationTime              | now                                                    |
      | MessageType               | REQUEST_PERIODIC_METER_DATA                            |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170101000000000 |
      | NumberOfNotificationsSend |                                                      0 |
    When no notification is resend
    Then a record in the response_data table of the database has values
      | DeviceIdentification      | TEST1024000000001                                      |
      | MessageType               | GET_DATA                                               |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170101000000000 |
      | NumberOfNotificationsSend |                                                      0 |

  Scenario: Don't send notifications when the maximum of notifications sent has been reached
    Given a response data record
      | DeviceIdentification      | TEST1024000000001                                      |
      | CreationTime              | yesterday                                              |
      | MessageType               | GET_DATA                                               |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170101000000000 |
      | NumberOfNotificationsSend |                                                      3 |
    When no notification is resend
    Then a record in the response_data table of the database has values
      | DeviceIdentification      | TEST1024000000001                                      |
      | MessageType               | REQUEST_PERIODIC_METER_DATA                            |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170101000000000 |
      | NumberOfNotificationsSend |                                                      3 |
