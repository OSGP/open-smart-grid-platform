Feature: Find Devices
  As a ...
  I want to ...
  So that ...

  Scenario Outline: Find devices parameterized
    Given a device
      | DeviceIdentification | <DeviceIdentification> |
    When receiving a find devices request
      | PageSize | <PageSize> |
      | Page     | <Page>     |
    Then the find devices response contains "<Number>" devices
    And the find devices response contains at index "1"
      | DeviceIdentification | <DeviceIdentification> |

    Examples: 
      | DeviceIdentification | PageSize | Page | Number |
      | TEST1024000000001    |       25 |    0 |      1 |
