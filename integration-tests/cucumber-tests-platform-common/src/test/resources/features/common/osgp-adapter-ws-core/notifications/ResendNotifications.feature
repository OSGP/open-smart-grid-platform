@Common @Platform @NightlyBuildOnly
Feature: GXF notifications (WS Core) - Resend notifications
  As an OSGP user
  I want the platform to resend missed notifications
  So the notification mechanism is more robust

  # When running separately this scenario succeeds,
  # but it still fails in the nightly build...
#  @Skip
  Scenario: Resend missed notifications
    Given a response data record in ws-core
      | DeviceIdentification      | TEST1024000000001                                      |
      | CreationTime              | now - 5 minutes                                        |
      | MessageType               | SET_DEVICE_LIFECYCLE_STATUS                            |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170101010000000 |
      | NumberOfNotificationsSent |                                                      0 |
    When OSGP checks for which response data a notification has to be resend
    Then a notification is sent in ws-core
    And the response data has values in ws-core
      | DeviceIdentification      | TEST1024000000001                                      |
      | MessageType               | SET_DEVICE_LIFECYCLE_STATUS                            |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170101010000000 |
      | NumberOfNotificationsSent |                                                      1 |

  # When running separately this scenario succeeds,
  # but it still fails in the nightly build...
#  @Skip
  Scenario: Resend missed notifications with response url
    Given a response data record in ws-core
      | DeviceIdentification      | TEST1024000000001                                      |
      | CreationTime              | now - 5 minutes                                        |
      | MessageType               | SET_DEVICE_LIFECYCLE_STATUS                            |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170101020000000 |
      | NumberOfNotificationsSent |                                                      0 |
    And a response url data record in ws-core
      | CorrelationUid | test-org\|\|\|TEST1024000000001\|\|\|20170101020000000 |
      | ResponseUrl    | http://localhost:8088/notifications/                   |
    When OSGP checks for which response data a notification has to be resend
    Then a notification is sent in ws-core
    And the response url data in ws-core has values
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170101020000000 |
      | ResponseUrl               | http://localhost:8088/notifications/                   |

  Scenario: Don't send notifications when the configurable time has not passed
    Given a response data record in ws-core
      | CreationTime              | now + 5 minutes                                        |
      | MessageType               | SET_DEVICE_LIFECYCLE_STATUS                            |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170101030000000 |
      | NumberOfNotificationsSent |                                                      0 |
    When OSGP checks for which response data a notification has to be resend
    Then no notification is sent in ws-core
    And the response data has values in ws-core
      | DeviceIdentification      | TEST1024000000001                                      |
      | MessageType               | SET_DEVICE_LIFECYCLE_STATUS                            |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170101030000000 |
      | NumberOfNotificationsSent |                                                      0 |

  Scenario: Don't send notifications when the maximum of notifications sent has been reached
    Given a response data record in ws-core
      | DeviceIdentification      | TEST1024000000001                                      |
      | CreationTime              | yesterday                                              |
      | MessageType               | SET_DEVICE_LIFECYCLE_STATUS                            |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170101040000000 |
      | NumberOfNotificationsSent |                                                      3 |
    When OSGP checks for which response data a notification has to be resend
    Then no notification is sent in ws-core
    And the response data has values in ws-core
      | DeviceIdentification      | TEST1024000000001                                      |
      | MessageType               | SET_DEVICE_LIFECYCLE_STATUS                            |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170101040000000 |
      | NumberOfNotificationsSent |                                                      3 |
