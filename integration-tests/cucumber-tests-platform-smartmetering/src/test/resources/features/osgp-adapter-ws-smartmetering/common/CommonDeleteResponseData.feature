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
    Given no response data record
    Then the delete response data request with correlation uid "notexist" should throw SoapFault

  Scenario: Bundled requests should not delete response data
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a get actual meter reads action
    When the bundle request is received
    Then the bundle response should contain a get actual meter reads response
    And the response data record should not be deleted

  Scenario: Single requests should not delete response data
    Given a dlms device
      | DeviceIdentification     | TEST1024000000001 |
      | DeviceType               | SMART_METER_E     |
      | SelectiveAccessSupported | true              |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | TEST1024000000001 |
      | Channel                     |                 1 |
    When the get actual meter reads gas request is received
      | DeviceIdentification | TESTG102400000001 |
    Then the actual meter reads gas result should be returned
      | DeviceIdentification | TESTG102400000001 |
    And the response data record should not be deleted
