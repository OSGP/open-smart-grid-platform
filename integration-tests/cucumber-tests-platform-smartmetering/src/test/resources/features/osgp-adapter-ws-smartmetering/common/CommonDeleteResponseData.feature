@SmartMetering @Platform
Feature: SmartMetering Common - Delete Response Data
  As a grid operator
  I want to be able to delete the response data in the platform

  Scenario: Delete response data
    Given a response data record
      | CreationTime               | now                                      |
      | CorrelationUid             | test-org-TEST1024000000001-NOW-666666666 |
      | NumberOfNotificationsSent  |                                        5 |
      | OrganizationIdentification | test-org                                 |
      | DeviceIdentifcation        | TEST1024000000001                        |
      | MessageType                | REQUEST_ACTUAL_METER_DATA                |
      | ResultType                 | OK                                       |
    When the delete response data request with correlation uid "test-org-TEST1024000000001-NOW-666666666"
    Then the response data record with correlation uid "test-org-TEST1024000000001-NOW-666666666" should be deleted

  Scenario: Delete response data for data that does not exists
    When the delete response data request with correlation uid "notexist" should throw SoapFault
    Then the response data record with correlation uid "notexist" should be deleted
