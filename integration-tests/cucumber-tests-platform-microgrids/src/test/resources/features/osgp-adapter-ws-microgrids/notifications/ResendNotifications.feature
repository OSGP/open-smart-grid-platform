# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@Microgrids @Platform @ResendNotifications
Feature: Microgrids notifications - Resend notifications
  As an OSGP user
  I want the platform to resend missed notifications
  So the notification mechanism is more robust

  Scenario: Resend missed notifications
    Given a response data record
      | DeviceIdentification      | TEST1024000000001                                      |
      | CreationTime              | now - 2 minutes                                        |
      | MessageType               | GET_DATA                                               |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170101000000000 |
      | NumberOfNotificationsSent |                                                      0 |
    When OSGP checks for which response data a notification has to be resend
    Then a notification is sent
      | CorrelationUid | test-org\|\|\|TEST1024000000001\|\|\|20170101000000000 |
    And the response data has values
      | DeviceIdentification      | TEST1024000000001                                      |
      | MessageType               | GET_DATA                                               |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170101000000000 |
      | NumberOfNotificationsSent |                                                      1 |

  Scenario: Don't send notifications when the configurable time has not passed
    Given a response data record
      | CreationTime              | now + 5 minutes                                        |
      | MessageType               | GET_DATA                                               |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170201000000000 |
      | NumberOfNotificationsSent |                                                      0 |
    When OSGP checks for which response data a notification has to be resend
    Then no notification is sent
      | CorrelationUid | test-org\|\|\|TEST1024000000001\|\|\|20170201000000000 |
    And the response data has values
      | DeviceIdentification      | TEST1024000000001                                      |
      | MessageType               | GET_DATA                                               |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170201000000000 |
      | NumberOfNotificationsSent |                                                      0 |

  Scenario: Don't send notifications when the maximum of notifications sent has been reached
    Given a response data record
      | DeviceIdentification      | TEST1024000000001                                      |
      | CreationTime              | yesterday                                              |
      | MessageType               | GET_DATA                                               |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170301000000000 |
      | NumberOfNotificationsSent |                                                      3 |
    When OSGP checks for which response data a notification has to be resend
    Then no notification is sent
      | CorrelationUid | test-org\|\|\|TEST1024000000001\|\|\|20170301000000000 |
    And the response data has values
      | DeviceIdentification      | TEST1024000000001                                      |
      | MessageType               | GET_DATA                                               |
      | CorrelationUid            | test-org\|\|\|TEST1024000000001\|\|\|20170301000000000 |
      | NumberOfNotificationsSent |                                                      3 |
