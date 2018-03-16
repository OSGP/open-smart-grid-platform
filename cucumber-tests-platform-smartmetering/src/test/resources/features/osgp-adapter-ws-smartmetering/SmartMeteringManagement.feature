@SmartMetering @Platform
Feature: SmartMetering Management
  As a grid operator
  I want to be able to perform SmartMeteringManagement operations on a device
  In order to ...

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

  Scenario: find standard events from a device for a period without events
    When receiving a find standard events request
      | DeviceIdentification | TEST1024000000001        |
      | BeginDate            | 2015-08-15T00:00:00.000Z |
      | EndDate              | 2015-08-25T00:00:00.000Z |
    Then 0 standard events should be returned
      | DeviceIdentification | TEST1024000000001 |

  Scenario: find standard events from a device within a period
    When receiving a find standard events request
      | DeviceIdentification | TEST1024000000001        |
      | BeginDate            | 2015-09-01T00:00:00.000Z |
      | EndDate              | 2015-09-05T00:00:00.000Z |
    Then 21 standard events should be returned
      | DeviceIdentification | TEST1024000000001 |

  Scenario: find fraud events from a device
    When receiving a find fraud events request
      | DeviceIdentification | TEST1024000000001        |
      | BeginDate            | 2014-09-02T00:00:00.000Z |
      | EndDate              | 2015-09-03T00:00:00.000Z |
    Then 9 fraud events should be returned
      | DeviceIdentification | TEST1024000000001 |

  Scenario: find communication events from a device
    When receiving a find communication events request
      | DeviceIdentification | TEST1024000000001        |
      | BeginDate            | 2014-09-02T00:00:00.000Z |
      | EndDate              | 2015-09-03T00:00:00.000Z |
    Then 9 communication events should be returned
      | DeviceIdentification | TEST1024000000001 |

  Scenario: find mbus events from a device
    When receiving a find mbus events request
      | DeviceIdentification | TEST1024000000001        |
      | BeginDate            | 2015-09-01T00:00:00.000Z |
      | EndDate              | 2015-09-05T00:00:00.000Z |
    Then 29 mbus events should be returned
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Set device communication settings on a single meter
    When the set device communication settings request is received
      | DeviceIdentification     | TEST1024000000001 |
      | ChallengeLength          |                16 |
      | WithListSupported        | true              |
      | SelectiveAccessSupported | false             |
      | IpAddressIsStatic        | false             |
      | UseSn                    | false             |
      | UseHdlc                  | true              |
    Then the set device communication settings response should be "OK"
    And the device "TEST1024000000001" should be in the database with attributes
      | ChallengeLength          |    16 |
      | WithListSupported        | true  |
      | SelectiveAccessSupported | false |
      | IpAddressIsStatic        | false |
      | UseSn                    | false |
      | UseHdlc                  | true  |

  # NOTE: The database MbusIdentificationNumber: 12056731 corresponds with the device attributeID 6: 302343985
  # and likewise the database MbusManufacturerIdentification: LGB corresponds with the device attributeID 7: 12514
  Scenario Outline: Set M-Bus device lifecycle status by channel
    And a dlms device
      | DeviceIdentification           | TESTG101205673101 |
      | DeviceType                     | SMART_METER_G     |
      | GatewayDeviceIdentification    | TEST1024000000001 |
      | Channel                        |                 1 |
      | MbusIdentificationNumber       |          12056731 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      |  5 | unsigned             |         9 |
      |  6 | double-long-unsigned | 302343985 |
      |  7 | long-unsigned        |     12514 |
      |  8 | unsigned             |        66 |
      |  9 | unsigned             |         3 |
      | 14 | enumerate            |         4 |
    When a set device lifecycle status by channel request is received
      | DeviceIdentification  | TEST1024000000001 |
      | Channel               |                 1 |
      | DeviceLifecycleStatus | <Status>          |
    Then the set device lifecycle status by channel response is returned
      | DeviceIdentification  | TEST1024000000001 |
      | Channel               |                 1 |
      | DeviceLifecycleStatus | <Status>          |

    Examples: 
      | Status                |
      | NEW_IN_INVENTORY      |
      | READY_FOR_USE         |
      | REGISTERED            |
      | IN_USE                |
      | RETURNED_TO_INVENTORY |
      | UNDER_TEST            |
      | DESTROYED             |

  Scenario Outline: Set M-Bus device lifecycle status by channel, no device on that channel
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      |  5 | unsigned             | 0 |
      |  6 | double-long-unsigned | 0 |
      |  7 | long-unsigned        | 0 |
      |  8 | unsigned             | 0 |
      |  9 | unsigned             | 0 |
      | 14 | enumerate            | 0 |
    When a set device lifecycle status by channel request is received
      | DeviceIdentification  | TEST1024000000001 |
      | Channel               |                 1 |
      | DeviceLifecycleStatus | <Status>          |
    Then set device lifecycle status by channel request should return an exception
    And a SOAP fault should have been returned
      | Code    |                        219 |
      | Message | NO_DEVICE_FOUND_ON_CHANNEL |

    Examples: 
      | Status                |
      | NEW_IN_INVENTORY      |
      | READY_FOR_USE         |
      | REGISTERED            |
      | IN_USE                |
      | RETURNED_TO_INVENTORY |
      | UNDER_TEST            |
      | DESTROYED             |

  Scenario Outline: Set M-Bus device lifecycle status by channel, no matching device in database
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      |  5 | unsigned             |         9 |
      |  6 | double-long-unsigned | 302343985 |
      |  7 | long-unsigned        |     12514 |
      |  8 | unsigned             |        66 |
      |  9 | unsigned             |         3 |
      | 14 | enumerate            |         4 |
    When a set device lifecycle status by channel request is received
      | DeviceIdentification  | TEST1024000000001 |
      | Channel               |                 1 |
      | DeviceLifecycleStatus | <Status>          |
    Then set device lifecycle status by channel request should return an exception
    And a SOAP fault should have been returned
      | Code    |                           218 |
      | Message | NO_MATCHING_MBUS_DEVICE_FOUND |

    Examples: 
      | Status                |
      | NEW_IN_INVENTORY      |
      | READY_FOR_USE         |
      | REGISTERED            |
      | IN_USE                |
      | RETURNED_TO_INVENTORY |
      | UNDER_TEST            |
      | DESTROYED             |
