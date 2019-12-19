@SmartMetering @Platform @SmartMeteringManagement @NightlyBuildOnly
Feature: SmartMetering Management - Set Device Lifecycle Status by Channel
  As a grid operator
  I want to be able to set the device lifecycle status by channel
  So the lifecycle status can be stored for M-Bus devices based on their channel

  Background:
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

  # NOTE: The database MbusIdentificationNumber: 12056731 corresponds with the device attributeID 6: 302343985
  # and likewise the database MbusManufacturerIdentification: LGB corresponds with the device attributeID 7: 12514
  Scenario: Set M-Bus device lifecycle status by channel
    And a dlms device
      | DeviceIdentification           | TESTG101205673101 |
      | DeviceType                     | SMART_METER_G     |
      | GatewayDeviceIdentification    | TEST1024000000001 |
      | Channel                        | 1                 |
      | MbusIdentificationNumber       | 12056731          |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    | 66                |
      | MbusDeviceTypeIdentification   | 3                 |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5  | unsigned             | 9        |
      | 6  | double-long-unsigned | 12056731 |
      | 7  | long-unsigned        | 12514    |
      | 8  | unsigned             | 66       |
      | 9  | unsigned             | 3        |
      | 14 | enumerate            | 4        |
    When a set device lifecycle status by channel request is received
      | DeviceIdentification  | TEST1024000000001 |
      | Channel               | 1                 |
      | DeviceLifecycleStatus | READY_FOR_USE     |
    Then the set device lifecycle status by channel response is returned
      | DeviceIdentification  | TEST1024000000001 |
      | Channel               | 1                 |
      | DeviceLifecycleStatus | READY_FOR_USE     |

  Scenario: Set M-Bus device lifecycle status by channel, no device on that channel
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5  | unsigned             | 0 |
      | 6  | double-long-unsigned | 0 |
      | 7  | long-unsigned        | 0 |
      | 8  | unsigned             | 0 |
      | 9  | unsigned             | 0 |
      | 14 | enumerate            | 0 |
    When a set device lifecycle status by channel request is received
      | DeviceIdentification  | TEST1024000000001 |
      | Channel               | 1                 |
      | DeviceLifecycleStatus | NEW_IN_INVENTORY  |
    Then set device lifecycle status by channel request should return an exception
    And a SOAP fault should have been returned
      | Code    | 219                        |
      | Message | NO_DEVICE_FOUND_ON_CHANNEL |

  Scenario: Set M-Bus device lifecycle status by channel, no matching device in database
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5  | unsigned             | 9         |
      | 6  | double-long-unsigned | 302343985 |
      | 7  | long-unsigned        | 12514     |
      | 8  | unsigned             | 66        |
      | 9  | unsigned             | 3         |
      | 14 | enumerate            | 4         |
    When a set device lifecycle status by channel request is received
      | DeviceIdentification  | TEST1024000000001 |
      | Channel               | 1                 |
      | DeviceLifecycleStatus | REGISTERED        |
    Then set device lifecycle status by channel request should return an exception
    And a SOAP fault should have been returned
      | Code    | 218                           |
      | Message | NO_MATCHING_MBUS_DEVICE_FOUND |
