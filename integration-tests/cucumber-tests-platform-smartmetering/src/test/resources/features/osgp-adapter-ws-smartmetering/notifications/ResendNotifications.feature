@SmartMetering @Platform @NightlyBuildOnly
Feature: SmartMetering notifications - Resend notifications
  As an OSGP user
  I want the platform to resend missed notifications
  So the notification mechanism is more robust

  Scenario: Resend missed notifications
    Given a response data record
      | DeviceIdentification      | TEST1024000000001                                      |
      | CreationTime              | now - 2 minutes                                        |
      | MessageType               | REQUEST_PERIODIC_METER_DATA                            |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170101000000000 |
      | NumberOfNotificationsSent |                                                      0 |
    When OSGP checks for which response data a notification has to be resend
    Then a notification is sent
    And the response data has values
      | DeviceIdentification      | TEST1024000000001                                      |
      | MessageType               | REQUEST_PERIODIC_METER_DATA                            |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170101000000000 |
      | NumberOfNotificationsSent |                                                      1 |

  Scenario: Resend missed notifications with response url
    Given a response data record
      | DeviceIdentification      | TEST1024000000001                                      |
      | CreationTime              | now - 2 minutes                                        |
      | MessageType               | REQUEST_PERIODIC_METER_DATA                            |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170101000000000 |
      | NumberOfNotificationsSent |                                                      0 |
    And a response url data record in ws-smartmetering
      | CorrelationUid | test-org\|\|\|TEST1024000000001\|\|\|20170101000000000 |
      | ResponseUrl    | http://localhost:8089/notifications/                   |
    When OSGP checks for which response data a notification has to be resend
    Then a notification is sent
    And the response url data in ws-smartmetering has values
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170101000000000 |
      | ResponseUrl               | http://localhost:8089/notifications/                   |

  Scenario: Don't send notifications when the configurable time has not passed
    Given a response data record
      | CreationTime              | now + 5 minutes                                        |
      | MessageType               | REQUEST_PERIODIC_METER_DATA                            |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170101000000000 |
      | NumberOfNotificationsSent |                                                      0 |
    When OSGP checks for which response data a notification has to be resend
    Then no notification is sent
    And the response data has values
      | DeviceIdentification      | TEST1024000000001                                      |
      | MessageType               | REQUEST_PERIODIC_METER_DATA                            |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170101000000000 |
      | NumberOfNotificationsSent |                                                      0 |

  Scenario: Don't send notifications when the maximum of notifications sent has been reached
    Given a response data record
      | DeviceIdentification      | TEST1024000000001                                      |
      | CreationTime              | yesterday                                              |
      | MessageType               | REQUEST_PERIODIC_METER_DATA                            |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170101000000000 |
      | NumberOfNotificationsSent |                                                      3 |
    When OSGP checks for which response data a notification has to be resend
    Then no notification is sent
    And the response data has values
      | DeviceIdentification      | TEST1024000000001                                      |
      | MessageType               | REQUEST_PERIODIC_METER_DATA                            |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170101000000000 |
      | NumberOfNotificationsSent |                                                      3 |
