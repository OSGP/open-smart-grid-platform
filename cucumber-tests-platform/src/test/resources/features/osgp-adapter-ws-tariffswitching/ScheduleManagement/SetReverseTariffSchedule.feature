Feature: TariffSwitchingScheduleManagement Set Reverse Tariff Schedule
  In order to ... 
  As a platform 
  I want to ...

  @OslpMockServer
  Scenario Outline: Set reverse tariff schedule
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | RelayType            | TARIFF_REVERSED   |
      | Protocol             | <Protocol>        |
    And the device returns a set reverse tariff schedule response "OK" over "<Protocol>"
    When receiving a set reverse tariff schedule request
      | DeviceIdentification | TEST1024000000001 |
      | WeekDay              | <WeekDay>         |
      | StartDay             | <StartDay>        |
      | EndDay               | <EndDay>          |
      | Time                 | <Time>            |
      | TariffValues         | <TariffValues>    |
    Then the set reverse tariff schedule async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set reverse tariff schedule "<Protocol>" message is sent to device "TEST1024000000001"
      | WeekDay      | <WeekDay>              |
      | StartDay     | <StartDay>             |
      | EndDay       | <EndDay>               |
      | Time         | <Time>                 |
      | TariffValues | <ReceivedTariffValues> |
    And the platform buffers a set reverse tariff schedule response message for device "TEST1024000000001"
      | Result | OK |

    Examples: 
      | Protocol    | WeekDay     | StartDay   | EndDay     | Time         | TariffValues | ReceivedTariffValues |
      | OSLP        | MONDAY      |            |            | 08:00:00.000 | 1,true       | 1,false              |
      | OSLP        | WEEKDAY     |            |            | 21:00:00.000 | 1,false      | 1,true               |
      | OSLP        | MONDAY      |            |            | 18:00:00.000 | 1,true       | 1,false              |
      | OSLP        | ABSOLUTEDAY | 2013-03-01 |            | 18:00:00.000 | 1,true       | 1,false              |
      | OSLP        | MONDAY      |            |            |              | 1,true       | 1,false              |
      | OSLP        | ABSOLUTEDAY | 2016-01-01 | 2016-12-31 | 18:00:00.000 | 0,true       | 0,false              |
      | OSLP ELSTER | MONDAY      |            |            | 08:00:00.000 | 1,true       | 1,false              |
      | OSLP ELSTER | WEEKDAY     |            |            | 21:00:00.000 | 1,false      | 1,true               |
      | OSLP ELSTER | MONDAY      |            |            | 18:00:00.000 | 1,true       | 1,false              |
      | OSLP ELSTER | ABSOLUTEDAY | 2013-03-01 |            | 18:00:00.000 | 1,true       | 1,false              |
      | OSLP ELSTER | MONDAY      |            |            |              | 1,true       | 1,false              |
      | OSLP ELSTER | ABSOLUTEDAY | 2016-01-01 | 2016-12-31 | 18:00:00.000 | 0,true       | 0,false              |

  @OslpMockServer
  Scenario Outline: Failed set reverse tariff schedule
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | RelayType            | TARIFF_REVERSED   |
      | Protocol             | <Protocol>        |
    And the device returns a set reverse tariff schedule response "FAILURE" over "<Protocol>"
    When receiving a set reverse tariff schedule request
      | DeviceIdentification | TEST1024000000001 |
      | WeekDay              | MONDAY            |
      | StartDay             |                   |
      | EndDay               |                   |
      | Time                 | 18:00:00.000      |
      | TariffValues         | 0,true            |
    Then the set reverse tariff schedule async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set reverse tariff schedule "<Protocol>" message is sent to device "TEST1024000000001"
      | WeekDay      | MONDAY       |
      | StartDay     |              |
      | EndDay       |              |
      | Time         | 18:00:00.000 |
      | TariffValues | 0,false      |
    # Note: The platform throws a TechnicalException when the status is 'FAILURE'.
    And the platform buffers a set reverse tariff schedule response message for device "TEST1024000000001" contains soap fault
      | Message | Device reports failure |

    Examples: 
      | Protocol    |
      | OSLP        |
      | OSLP ELSTER |

  @OslpMockServer
  Scenario Outline: Rejected set reverse tariff schedule
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | RelayType            | TARIFF_REVERSED   |
      | Protocol             | <Protocol>        |
    And the device returns a set reverse tariff schedule response "REJECTED" over "<Protocol>"
    When receiving a set reverse tariff schedule request
      | DeviceIdentification | TEST1024000000001 |
      | WeekDay              | MONDAY            |
      | StartDay             |                   |
      | EndDay               |                   |
      | Time                 | 18:00:00.000      |
      | TariffValues         | 0,true            |
    Then the set reverse tariff schedule async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set tariff schedule "<Protocol>" message is sent to device "TEST1024000000001"
      | WeekDay      | MONDAY       |
      | StartDay     |              |
      | EndDay       |              |
      | Time         | 18:00:00.000 |
      | TariffValues | 0,false      |
    # Note: The platform throws a TechnicalException when the status is 'REJECTED'.
    And the platform buffers a set reverse tariff schedule response message for device "TEST1024000000001" contains soap fault
      | Message | Device reports rejected |

    Examples: 
      | Protocol    |
      | OSLP        |
      | OSLP ELSTER |

  Scenario: Set reverse tariff schedule with invalid schedule
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | RelayType            | TARIFF_REVERSED   |
    When receiving a set reverse tariff schedule request
      | DeviceIdentification | TEST1024000000001 |
      | WeekDay              | ABSOLUTEDAY       |
      | StartDay             |                   |
      | EndDay               |                   |
      | Time                 | 18:00:00.000      |
      | TariffValues         | 0,true            |
    Then the set reverse tariff schedule response contains soap fault
      | FaultCode    | SOAP-ENV:Server                                                                                |
      | FaultString  | VALIDATION_ERROR                                                                               |
      | InnerMessage | Validation Exception, violations: startDay may not be null when weekDay is set to ABSOLUTEDAY; |

  # Note: Result is 'NOT_FOUND' because there isn't a record in the database with a CorrelationUID
  # Note: HasScheduled is set to 'false' because the response type is 'NOT_OK', but should be 'OK'
  @OslpMockServer
  Scenario Outline: Set reverse tariff schedule with 50 schedules # Success
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | RelayType            | TARIFF_REVERSED   |
      | Protocol             | <Protocol>        |
    And the device returns a set reverse tariff schedule response "OK" over "<Protocol>"
    When receiving a set reverse tariff schedule request for 50 schedules
      | DeviceIdentification | TEST1024000000001 |
      | WeekDay              | ABSOLUTEDAY       |
      | StartDay             | 2016-01-01        |
      | EndDay               | 2016-12-31        |
      | Time                 | 18:00:00.000      |
      | TariffValues         | 0,true            |
    Then the set reverse tariff schedule async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set reverse tariff schedule "<Protocol>" message is sent to device "TEST1024000000001"
      | WeekDay      | ABSOLUTEDAY  |
      | WeekDay      | ABSOLUTEDAY  |
      | StartDay     | 2016-01-01   |
      | EndDay       | 2016-12-31   |
      | Time         | 18:00:00.000 |
      | TariffValues | 0,false      |
    And the platform buffers a set reverse tariff schedule response message for device "TEST1024000000001"
      | Result | NOT_FOUND |

    Examples: 
      | Protocol    |
      | OSLP        |
      | OSLP ELSTER |

  Scenario: Set reverse tariff schedule with 51 schedules # Fail
    Given an ssld device
      | DeviceIdentification | TEST1024000000001 |
      | RelayType            | TARIFF_REVERSED   |
    When receiving a set reverse tariff schedule request for 51 schedules
      | DeviceIdentification | TEST1024000000001 |
      | WeekDay              | ABSOLUTEDAY       |
      | StartDay             | 2016-01-01        |
      | EndDay               | 2016-12-31        |
      | Time                 | 18:00:00.000      |
      | TariffValues         | 0,true            |
      | ScheduledTime        | 2016-12-15        |
    Then the set reverse tariff schedule response contains soap fault
      | FaultCode        | SOAP-ENV:Client                                                                                                                                                                                                                                                                                             |
      | FaultString      | Validation error                                                                                                                                                                                                                                                                                            |
      | ValidationErrors | cvc-complex-type.2.4.a: Invalid content was found starting with element 'ns2:Schedules'. One of '{"http://www.alliander.com/schemas/osgp/tariffswitching/schedulemanagement/2014/10":Page, "http://www.alliander.com/schemas/osgp/tariffswitching/schedulemanagement/2014/10":scheduled_time}' is expected. |
