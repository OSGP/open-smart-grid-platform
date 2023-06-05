# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@PublicLighting @Platform @TariffSwitchingScheduleManagement
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
    And the platform buffers a set reverse tariff schedule response message for device "TEST1024000000001" that contains a soap fault
      | Message | Device reports failure |

    Examples: 
      | Protocol    |
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
    And the platform buffers a set reverse tariff schedule response message for device "TEST1024000000001" that contains a soap fault
      | Message | Device reports rejected |

    Examples: 
      | Protocol    |
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

  Scenario Outline: Set reverse tariff schedule for inactive or unregistered device (device lifecycle state)
    Given an ssld device
      | DeviceIdentification  | TEST1024000000001       |
      | DeviceLifecycleStatus | <DeviceLifecycleStatus> |
    When receiving a set reverse tariff schedule request
      | DeviceIdentification | TEST1024000000001 |
      | WeekDay              | MONDAY            |
      | StartDay             |                   |
      | EndDay               |                   |
      | Time                 | 18:00:00.000      |
      | TariffValues         | 0,true            |
    Then the set reverse tariff schedule response contains soap fault
      | FaultCode    | SOAP-ENV:Server                                        |
      | FaultString  | INACTIVE_DEVICE                                        |
      | InnerMessage | Device TEST1024000000001 is not active in the platform |

    Examples: 
      | DeviceLifecycleStatus |
      | NEW_IN_INVENTORY      |
      | READY_FOR_USE         |
      | REGISTERED            |
      | RETURNED_TO_INVENTORY |
      | UNDER_TEST            |
      | DESTROYED             |

  Scenario: Set reverse tariff schedule for inactive or unregistered device (is activated false)
    Given an ssld device
      | DeviceIdentification | TEST1024000000001 |
      | Activated            | false             |
    When receiving a set reverse tariff schedule request
      | DeviceIdentification | TEST1024000000001 |
      | WeekDay              | MONDAY            |
      | StartDay             |                   |
      | EndDay               |                   |
      | Time                 | 18:00:00.000      |
      | TariffValues         | 0,true            |
    Then the set reverse tariff schedule response contains soap fault
      | FaultCode    | SOAP-ENV:Server                                        |
      | FaultString  | INACTIVE_DEVICE                                        |
      | InnerMessage | Device TEST1024000000001 is not active in the platform |

  Scenario: Set reverse tariff schedule for unregistered device (public key missing)
    Given an ssld device
      | DeviceIdentification | TEST1024000000001 |
      | PublicKeyPresent     | false             |
    When receiving a set reverse tariff schedule request
      | DeviceIdentification | TEST1024000000001 |
      | WeekDay              | MONDAY            |
      | StartDay             |                   |
      | EndDay               |                   |
      | Time                 | 18:00:00.000      |
      | TariffValues         | 0,true            |
    Then the set reverse tariff schedule response contains soap fault
      | FaultCode    | SOAP-ENV:Server                            |
      | FaultString  | UNREGISTERED_DEVICE                        |
      | InnerMessage | Device TEST1024000000001 is not registered |

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
      | StartDay     | 2016-01-01   |
      | EndDay       | 2016-12-31   |
      | Time         | 18:00:00.000 |
      | TariffValues | 0,false      |
    And the platform buffers a set reverse tariff schedule response message for device "TEST1024000000001" that contains a soap fault
      | FaultCode   | SOAP-ENV:Server            |
      | FaultString | CorrelationUid is unknown. |

    Examples: 
      | Protocol    |
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
      | FaultCode        | SOAP-ENV:Client                                                                                                                                                                                                                                                                                                                                                            |
      | FaultString      | Validation error                                                                                                                                                                                                                                                                                                                                                           |
      | ValidationErrors | cvc-complex-type.2.4.e: 'ns2:Schedules' can occur a maximum of '50' times in the current sequence. This limit was exceeded. At this point one of '{"http://www.opensmartgridplatform.org/schemas/tariffswitching/schedulemanagement/2014/10":Page, "http://www.opensmartgridplatform.org/schemas/tariffswitching/schedulemanagement/2014/10":scheduled_time}' is expected. |
