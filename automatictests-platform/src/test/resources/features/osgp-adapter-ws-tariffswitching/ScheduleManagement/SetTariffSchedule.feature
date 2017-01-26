Feature: TariffSwitchingScheduleManagement Set Tariff Schedule
  In order to ... 
  As a platform 
  I want to ...

  @OslpMockServer
  Scenario Outline: Set tariff schedule
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
      | RelayType            | TARIFF            |
    And the device returns a set tariff schedule response "OK" over OSLP
    When receiving a set tariff schedule request
      | DeviceIdentification | TEST1024000000001 |
      | WeekDay              | <WeekDay>         |
      | StartDay             | <StartDay>        |
      | EndDay               | <EndDay>          |
      | Time                 | <Time>            |
      | TariffValues         | <TariffValues>    |
    Then the set tariff schedule async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set tariff schedule OSLP message is sent to device "TEST1024000000001"
      | WeekDay      | <WeekDay>      |
      | StartDay     | <StartDay>     |
      | EndDay       | <EndDay>       |
      | Time         | <Time>         |
      | TariffValues | <TariffValues> |
    And the platform buffers a set tariff schedule response message for device "TEST1024000000001"
      | Result | OK |

    Examples: 
      | WeekDay     | StartDay   | EndDay     | Time         | TariffValues |
      | MONDAY      |            |            | 08:00:00.000 | 1,true       |
      | WEEKDAY     |            |            | 21:00:00.000 | 1,false      |
      | MONDAY      |            |            | 18:00:00.000 | 1,true       |
      | ABSOLUTEDAY | 2013-03-01 |            | 18:00:00.000 | 1,true       |
      | MONDAY      |            |            |              | 1,true       |
      | ABSOLUTEDAY | 2016-01-01 | 2016-12-31 | 18:00:00.000 | 0,true       |

  @OslpMockServer
  Scenario: Failed set tariff schedule
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
      | RelayType            | TARIFF            |
    And the device returns a set tariff schedule response "FAILURE" over OSLP
    When receiving a set tariff schedule request
      | DeviceIdentification | TEST1024000000001 |
      | WeekDay              | MONDAY            |
      | StartDay             |                   |
      | EndDay               |                   |
      | Time                 | 18:00:00.000      |
      | TariffValues         | 0,true            |
    Then the set tariff schedule async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set tariff schedule OSLP message is sent to device "TEST1024000000001"
      | WeekDay      | MONDAY       |
      | StartDay     |              |
      | EndDay       |              |
      | Time         | 18:00:00.000 |
      | TariffValues | 0,true       |
    # Note: The platform throws a TechnicalException when the status is 'FAILURE'.
    And the platform buffers a set tariff schedule response message for device "TEST1024000000001" contains soap fault
      | Message | Device reports failure |

  @OslpMockServer
  Scenario: Rejected set tariff schedule
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
      | RelayType            | TARIFF            |
    And the device returns a set tariff schedule response "REJECTED" over OSLP
    When receiving a set tariff schedule request
      | DeviceIdentification | TEST1024000000001 |
      | WeekDay              | MONDAY            |
      | StartDay             |                   |
      | EndDay               |                   |
      | Time                 | 18:00:00.000      |
      | TariffValues         | 0,true            |
    Then the set tariff schedule async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set tariff schedule OSLP message is sent to device "TEST1024000000001"
      | WeekDay      | MONDAY       |
      | StartDay     |              |
      | EndDay       |              |
      | Time         | 18:00:00.000 |
      | TariffValues | 0,true       |
    # Note: The platform throws a TechnicalException when the status is 'REJECTED'.
    And the platform buffers a set tariff schedule response message for device "TEST1024000000001" contains soap fault
      | Message | Device reports rejected |

  Scenario: Set tariff schedule with invalid schedule
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
      | RelayType            | TARIFF            |
    When receiving a set tariff schedule request
      | DeviceIdentification | TEST1024000000001 |
      | WeekDay              | ABSOLUTEDAY       |
      | StartDay             |                   |
      | EndDay               |                   |
      | Time                 | 18:00:00.000      |
      | TariffValues         | 0,true            |
    Then the set tariff schedule response contains soap fault
      | FaultCode    | SOAP-ENV:Server                                                                                |
      | FaultString  | VALIDATION_ERROR                                                                               |
      | InnerMessage | Validation Exception, violations: startDay may not be null when weekDay is set to ABSOLUTEDAY; |

  # Note: Result is 'NOT_FOUND' because there isn't a record in the database with a CorrelationUID
  # Note: HasScheduled is set to 'false' because the response type is 'NOT_OK', but should be 'OK'
  @OslpMockServer
  Scenario Outline: Set tariff schedule with 50 schedules # Success
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
      | RelayType            | TARIFF            |
    And the device returns a set tariff schedule response "OK" over OSLP
    When receiving a set tariff schedule request for 50 schedules
      | DeviceIdentification | TEST1024000000001 |
      | WeekDay              | <WeekDay>         |
      | StartDay             | <StartDay>        |
      | EndDay               | <EndDay>          |
      | Time                 | <Time>            |
      | TariffValues         | <TariffValues>    |
    Then the set tariff schedule async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set tariff schedule OSLP message is sent to device "TEST1024000000001"
      | WeekDay       | <WeekDay>       |
      | StartDay      | <StartDay>      |
      | EndDay        | <EndDay>        |
      | Time          | <Time>          |
      | TariffValues  | <TariffValues>  |
      | ScheduledTime | <ScheduledTime> |
    And the platform buffers a set tariff schedule response message for device "TEST1024000000001"
      | Result | NOT_FOUND |

    Examples: 
      | WeekDay     | StartDay   | EndDay     | Time         | TariffValues | ScheduledTime |
      | ABSOLUTEDAY | 2016-01-01 | 2016-12-31 | 18:00:00.000 | 0,true       | 2016-12-15    |

  Scenario Outline: Set tariff schedule with 51 schedules # Fail
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
      | RelayType            | TARIFF            |
    When receiving a set tariff schedule request for 51 schedules
      | DeviceIdentification | TEST1024000000001 |
      | WeekDay              | <WeekDay>         |
      | StartDay             | <StartDay>        |
      | EndDay               | <EndDay>          |
      | Time                 | <Time>            |
      | TariffValues         | <TariffValues>    |
      | ScheduledTime        | 2016-12-15        |
    Then the set tariff schedule response contains soap fault
      | FaultCode        | SOAP-ENV:Client                                                                                                                                                                                                                                                                                             |
      | FaultString      | Validation error                                                                                                                                                                                                                                                                                            |
      | ValidationErrors | cvc-complex-type.2.4.a: Invalid content was found starting with element 'ns2:Schedules'. One of '{"http://www.alliander.com/schemas/osgp/tariffswitching/schedulemanagement/2014/10":Page, "http://www.alliander.com/schemas/osgp/tariffswitching/schedulemanagement/2014/10":scheduled_time}' is expected. |

    Examples: 
      | WeekDay     | StartDay   | EndDay     | Time         | TariffValues |
      | ABSOLUTEDAY | 2016-01-01 | 2016-12-31 | 18:00:00.000 | 0,true       |
