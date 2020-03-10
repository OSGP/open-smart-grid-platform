@Skip @TariffSwitching @Platform @NightlyBuildOnly @Housekeeping
Feature: TariffSwitching Housekeeping - Response Data Cleanup Job
  As open smart grid platform
  I want to clean up the response data
  So that obsolete data is removed
#
  #Scenario: Clean obsolete response data
    #Given a tariff switching response data record
      #| CreationTime               | now - 1 months                               |
      #| CorrelationUid             | test-org-TEST1024000000001-20170101000000000 |
      #| NumberOfNotificationsSent  |                                            5 |
      #| OrganizationIdentification | test-org                                     |
      #| DeviceIdentifcation        | TEST1024000000001                            |
      #| MessageType                | SET_TARIFF_SCHEDULE                           |
      #| ResultType                 | OK                                           |
    #When the response data cleanup job runs
    #Then the tariff switching cleanup job should have removed the response data with correlation uid "test-org-TEST1024000000001-20170101000000000"
#
  #Scenario: Do not clean non-obsolete response data
    #Given a tariff switching response data record
      #| CreationTime               | now                                      |
      #| CorrelationUid             | test-org-TEST1024000000001-NOW-000000000 |
      #| NumberOfNotificationsSent  |                                        5 |
      #| OrganizationIdentification | test-org                                 |
      #| DeviceIdentifcation        | TEST1024000000001                        |
      #| MessageType                | SET_TARIFF_SCHEDULE                       |
      #| ResultType                 | OK                                       |
    #When the response data cleanup job runs
    #Then the tariff switching cleanup job should not have removed the response data with correlation uid "test-org-TEST1024000000001-NOW-000000000"
