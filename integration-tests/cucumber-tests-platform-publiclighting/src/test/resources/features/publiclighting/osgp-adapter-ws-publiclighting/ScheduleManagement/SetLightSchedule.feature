# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@PublicLighting @Platform @PublicLightingSetLightSchedule
Feature: PublicLightingScheduleManagement Set Light Schedule
  In order to ...
  As a platform
  I want to ...

  @OslpMockServer
  Scenario Outline: Set light schedule
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    And the device returns a set light schedule response "OK" over "<Protocol>"
    When receiving a set light schedule request
      | DeviceIdentification | TEST1024000000001 |
      | WeekDay              | <WeekDay>         |
      | StartDay             | <StartDay>        |
      | EndDay               | <EndDay>          |
      | ActionTime           | <ActionTime>      |
      | Time                 | <Time>            |
      | LightValues          | <LightValues>     |
      | TriggerType          | <TriggerType>     |
      | TriggerWindow        | <TriggerWindow>   |
    Then the set light schedule async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set light schedule "<Protocol>" message is sent to device "TEST1024000000001"
      | WeekDay       | <WeekDay>       |
      | StartDay      | <StartDay>      |
      | EndDay        | <EndDay>        |
      | ActionTime    | <ActionTime>    |
      | Time          | <Time>          |
      | LightValues   | <LightValues>   |
      | TriggerType   | <TriggerType>   |
      | TriggerWindow | <TriggerWindow> |
    And the platform buffers a set light schedule response message for device "TEST1024000000001"
      | Result | OK |
    Examples:
      | Protocol    | WeekDay     | StartDay   | EndDay     | ActionTime   | Time         | TriggerWindow | LightValues         | TriggerType   |
      | OSLP ELSTER | ALL         |            |            | ABSOLUTETIME | 18:00:00.000 |               | 0,true,             |               |
      | OSLP ELSTER | ALL         |            |            | ABSOLUTETIME | 08:00:00.000 |               | 0,false,            |               |
      | OSLP ELSTER | MONDAY      |            |            | ABSOLUTETIME | 18:00:00.000 |               | 0,true,             |               |
      | OSLP ELSTER | MONDAY      |            |            | ABSOLUTETIME | 08:00:00.000 |               | 0,false,            |               |
      | OSLP ELSTER | MONDAY      |            |            | ABSOLUTETIME | 18:00:00.000 |               | 2,true,;3,true,50   |               |
      | OSLP ELSTER | MONDAY      |            |            | ABSOLUTETIME | 08:00:00.000 |               | 2,false,;3,false,   |               |
      | OSLP ELSTER | ABSOLUTEDAY | 2013-03-01 |            | ABSOLUTETIME | 18:00:00.000 |               | 0,true,             |               |
      | OSLP ELSTER | ABSOLUTEDAY | 2013-03-01 |            | ABSOLUTETIME | 08:00:00.000 |               | 0,false,            |               |
      | OSLP ELSTER | ALL         |            | 2013-12-31 | ABSOLUTETIME | 18:00:00.000 |               | 0,true,             |               |
      | OSLP ELSTER | ALL         |            | 2013-12-31 | ABSOLUTETIME | 08:00:00.000 |               | 0,false,            |               |
      | OSLP ELSTER | MONDAY      |            | 2013-12-31 | ABSOLUTETIME | 18:00:00.000 |               | 0,true,             |               |
      | OSLP ELSTER | MONDAY      |            | 2013-12-31 | ABSOLUTETIME | 08:00:00.000 |               | 0,false,            |               |
      | OSLP ELSTER | MONDAY      | 2013-03-01 | 2013-12-31 | ABSOLUTETIME | 18:00:00.000 |               | 2,true,;3,true,50   |               |
      | OSLP ELSTER | MONDAY      | 2013-03-01 | 2013-12-31 | ABSOLUTETIME | 08:00:00.000 |               | 2,false,;3,false,   |               |
      | OSLP ELSTER | ABSOLUTEDAY | 2013-03-01 | 2013-12-31 | ABSOLUTETIME | 18:00:00.000 |               | 0,true,             |               |
      | OSLP ELSTER | ABSOLUTEDAY | 2013-03-01 | 2013-12-31 | ABSOLUTETIME | 08:00:00.000 |               | 0,false,            |               |
      | OSLP ELSTER | ALL         |            |            | SUNSET       |              |               | 0,true,             | ASTRONOMICAL  |
      | OSLP ELSTER | ALL         |            |            | SUNRISE      |              |               | 0,false,            | ASTRONOMICAL  |
      | OSLP ELSTER | ALL         |            |            | SUNSET       |              |         42,42 | 0,true,             | LIGHT_TRIGGER |
      | OSLP ELSTER | ALL         |            |            | SUNRISE      |              |       150,150 | 0,false,            | LIGHT_TRIGGER |
      | OSLP ELSTER | MONDAY      |            |            | SUNSET       |              |               | 2,true,;3,true,50   | ASTRONOMICAL  |
      | OSLP ELSTER | MONDAY      |            |            | SUNRISE      |              |               | 2,false,;3,false,   | ASTRONOMICAL  |
      | OSLP ELSTER | MONDAY      |            |            | SUNSET       |              |         30,30 | 2,true,50;3,true,75 | LIGHT_TRIGGER |
      | OSLP ELSTER | MONDAY      |            |            | SUNRISE      |              |         60,90 | 2,false,;3,false,   | LIGHT_TRIGGER |
      | OSLP ELSTER | ABSOLUTEDAY | 2013-03-01 |            | SUNSET       |              |               | 0,true,             | ASTRONOMICAL  |
      | OSLP ELSTER | ABSOLUTEDAY | 2013-03-01 |            | SUNRISE      |              |               | 0,false,            | ASTRONOMICAL  |
      | OSLP ELSTER | ABSOLUTEDAY | 2013-03-01 |            | SUNSET       |              |         30,30 | 0,true,             | LIGHT_TRIGGER |
      | OSLP ELSTER | ABSOLUTEDAY | 2013-03-01 |            | SUNRISE      |              |         60,90 | 0,false,            | LIGHT_TRIGGER |
      | OSLP ELSTER | ABSOLUTEDAY | 2013-03-01 | 2013-12-31 | SUNSET       |              |               | 0,true,             | ASTRONOMICAL  |
      | OSLP ELSTER | ABSOLUTEDAY | 2013-03-01 | 2013-12-31 | SUNRISE      |              |               | 0,false,            | ASTRONOMICAL  |
      | OSLP ELSTER | ABSOLUTEDAY | 2013-03-01 | 2013-12-31 | SUNSET       |              |         30,30 | 0,true,             | LIGHT_TRIGGER |
      | OSLP ELSTER | ABSOLUTEDAY | 2013-03-01 | 2013-12-31 | SUNRISE      |              |         60,90 | 0,false,            | LIGHT_TRIGGER |

  #Note: the astronomical offsets are part of the set schedule request in the web services,
  #      while in the oslp elster protocol adapter they are sent to the device using a set configuration message
  #      followed by a reboot sequence
  @OslpMockServer @AstronomicalSchedule
  Scenario: Set light schedule with astronomical offsets
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | OSLP ELSTER       |
    And the device returns a get configuration status over "OSLP_ELSTER"
      | Status            | OK          |
      | LightType         | RELAY       |
      | DcLights          |             |
      | DcMap             |             |
      | RelayConf         |             |
      | PreferredLinkType |             |
      | OsgpIpAddress     | 10.20.30.40 |
      | OsgpPort          |       12122 |
    And the device returns a set configuration status "OK" over "OSLP ELSTER"
    And the device returns a set light schedule response "OK" over "OSLP ELSTER"
    When receiving a set light schedule request with astronomical offsets
      | DeviceIdentification | TEST1024000000001 |
      | SunriseOffset        |               -15 |
      | SunsetOffset         |                45 |
    Then the set light schedule async response contains
      | DeviceIdentification | TEST1024000000001 |
    And the device returns a set reboot response "OK" over "OLSP ELSTER"
    And a get configuration "OSLP ELSTER" message is sent to device "TEST1024000000001"

    And a set reboot "OSLP ELSTER" message is sent to device "TEST1024000000001"
    And a set configuration "OSLP ELSTER" message is sent to device "TEST1024000000001"
      | SunriseOffset | -900 |
      | SunsetOffset  | 2700 |

    # Register.
    And the device sends a register device request to the platform over "OSLP ELSTER"
      | DeviceIdentification | TEST1024000000001 |
    And the device sends a confirm register device request to the platform over "OSLP ELSTER"
      | DeviceIdentification | TEST1024000000001 |

    And a set light schedule "OSLP ELSTER" message is sent to device "TEST1024000000001"
      | WeekDay     | ALL          |
      | ActionTime  | SUNRISE      |
      | LightValues | 0,false,     |
      | TriggerType | ASTRONOMICAL |
    And the platform buffers a set light schedule response message for device "TEST1024000000001"
      | Result | OK |

  @OslpMockServer @AstronomicalSchedule
  Scenario: Set light schedule with astronomical offsets is blocked when a previous request with astronomical offsets is in progress
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | OSLP ELSTER       |
    And a pending set schedule request that expires within "5" minutes
      | DeviceIdentification | TEST1024000000001 |
    When receiving a set light schedule request with astronomical offsets
      | DeviceIdentification | TEST1024000000001 |
      | SunriseOffset        |               -15 |
      | SunsetOffset         |                45 |
    Then the set light schedule async response contains
      | DeviceIdentification | TEST1024000000001 |
    And the platform buffers a set light schedule response message for device "TEST1024000000001"
      | Result      | NOT_OK                                             |
      | Description | SET_SCHEDULE_WITH_ASTRONOMICAL_OFFSETS_IN_PROGRESS |

  @OslpMockServer @AstronomicalSchedule
  Scenario: Set light schedule when a previous request with astronomical offsets is in progress
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | OSLP ELSTER       |
    And a pending set schedule request that expires within "5" minutes
      | DeviceIdentification | TEST1024000000001 |
    When receiving a set light schedule request
      | DeviceIdentification | TEST1024000000001 |
      | WeekDay              | ALL               |
      | ActionTime           | ABSOLUTETIME      |
      | Time                 | 18:00:00.000      |
      | LightValues          | 0,true,           |
      | TriggerType          |                   |
    Then the set light schedule async response contains
      | DeviceIdentification | TEST1024000000001 |
    And the platform buffers a set light schedule response message for device "TEST1024000000001"
      | Result      | NOT_OK                                             |
      | Description | SET_SCHEDULE_WITH_ASTRONOMICAL_OFFSETS_IN_PROGRESS |


  @OslpMockServer
  Scenario Outline: Failed set light schedule
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    And the device returns a set light schedule response "FAILURE" over "<Protocol>"
    When receiving a set light schedule request
      | DeviceIdentification | TEST1024000000001 |
      | WeekDay              | MONDAY            |
      | StartDay             |                   |
      | EndDay               |                   |
      | ActionTime           | ABSOLUTETIME      |
      | Time                 | 18:00:00.000      |
      | LightValues          | 0,true,           |
      | TriggerType          |                   |
      | TriggerWindow        |                   |
    Then the set light schedule async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set light schedule "<Protocol>" message is sent to device "TEST1024000000001"
      | WeekDay       | MONDAY       |
      | StartDay      |              |
      | EndDay        |              |
      | ActionTime    | ABSOLUTETIME |
      | Time          | 18:00:00.000 |
      | LightValues   | 0,true,      |
      | TriggerType   |              |
      | TriggerWindow |              |
    # Note: The platform throws a TechnicalException when the status is 'FAILURE'.
    And the platform buffers a set light schedule response message for device "TEST1024000000001"
      | Result      | NOT_OK                 |
      | Description | Device reports failure |

    Examples:
      | Protocol    |
      | OSLP ELSTER |

  @OslpMockServer
  Scenario Outline: Rejected set light schedule
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    And the device returns a set light schedule response "REJECTED" over "<Protocol>"
    When receiving a set light schedule request
      | DeviceIdentification | TEST1024000000001 |
      | WeekDay              | MONDAY            |
      | StartDay             |                   |
      | EndDay               |                   |
      | ActionTime           | ABSOLUTETIME      |
      | Time                 | 18:00:00.000      |
      | LightValues          | 0,true,           |
      | TriggerType          |                   |
      | TriggerWindow        |                   |
    Then the set light schedule async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set light schedule "<Protocol>" message is sent to device "TEST1024000000001"
      | WeekDay       | MONDAY       |
      | StartDay      |              |
      | EndDay        |              |
      | ActionTime    | ABSOLUTETIME |
      | Time          | 18:00:00.000 |
      | LightValues   | 0,true,      |
      | TriggerType   |              |
      | TriggerWindow |              |
    # Note: The platform throws a TechnicalException when the status is 'REJECTED'.
    And the platform buffers a set light schedule response message for device "TEST1024000000001"
      | Result      | NOT_OK                  |
      | Description | Device reports rejected |

    Examples:
      | Protocol    |
      | OSLP ELSTER |

  Scenario Outline: Set light schedule with invalid schedule
    Given an ssld device
      | DeviceIdentification | TEST1024000000001 |
    When receiving a set light schedule request
      | DeviceIdentification | TEST1024000000001 |
      | WeekDay              | <WeekDay>         |
      | StartDay             |                   |
      | EndDay               |                   |
      | ActionTime           | <ActionTime>      |
      | Time                 | <Time>            |
      | LightValues          | 0,true,           |
      | TriggerType          | <TriggerType>     |
      | TriggerWindow        |                   |
    Then the set light schedule response contains soap fault
      | FaultCode    | SOAP-ENV:Server  |
      | FaultString  | VALIDATION_ERROR |
      | InnerMessage | <Message>        |

    Examples:
      | WeekDay     | ActionTime   | Time         | TriggerType   | Message                                                                                                                                       |
      | ABSOLUTEDAY | ABSOLUTETIME | 18:00:00.000 |               | Validation Exception, violations: startDay may not be null when weekDay is set to ABSOLUTEDAY;                                                |
      | MONDAY      | SUNRISE      |              | LIGHT_TRIGGER | Validation Exception, violations: triggerWindow may not be null when actionTime is set to SUNRISE or SUNSET and triggerType is LIGHT_TRIGGER; |
      | MONDAY      | SUNSET       |              | LIGHT_TRIGGER | Validation Exception, violations: triggerWindow may not be null when actionTime is set to SUNRISE or SUNSET and triggerType is LIGHT_TRIGGER; |

  Scenario Outline: Set light schedule for inactive or unregistered device (device lifecycle state)
    Given an ssld device
      | DeviceIdentification  | TEST1024000000001       |
      | DeviceLifecycleStatus | <DeviceLifecycleStatus> |
    When receiving a set light schedule request
      | DeviceIdentification | TEST1024000000001 |
      | WeekDay              | MONDAY            |
      | StartDay             |                   |
      | EndDay               |                   |
      | ActionTime           | ABSOLUTETIME      |
      | Time                 | 18:00:00.000      |
      | LightValues          | 0,true,           |
      | TriggerType          |                   |
      | TriggerWindow        |                   |
    Then the set light schedule response contains soap fault
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

  Scenario: Set light schedule for inactive or unregistered device (is activated false)
    Given an ssld device
      | DeviceIdentification | TEST1024000000001 |
      | Activated            | false             |
    When receiving a set light schedule request
      | DeviceIdentification | TEST1024000000001 |
      | WeekDay              | MONDAY            |
      | StartDay             |                   |
      | EndDay               |                   |
      | ActionTime           | ABSOLUTETIME      |
      | Time                 | 18:00:00.000      |
      | LightValues          | 0,true,           |
      | TriggerType          |                   |
      | TriggerWindow        |                   |
    Then the set light schedule response contains soap fault
      | FaultCode    | SOAP-ENV:Server                                        |
      | FaultString  | INACTIVE_DEVICE                                        |
      | InnerMessage | Device TEST1024000000001 is not active in the platform |

  Scenario: Set light schedule for unregistered device (public key missing)
    Given an ssld device
      | DeviceIdentification | TEST1024000000001 |
      | PublicKeyPresent     | false             |
    When receiving a set light schedule request
      | DeviceIdentification | TEST1024000000001 |
      | WeekDay              | MONDAY            |
      | StartDay             |                   |
      | EndDay               |                   |
      | ActionTime           | ABSOLUTETIME      |
      | Time                 | 18:00:00.000      |
      | LightValues          | 0,true,           |
      | TriggerType          |                   |
      | TriggerWindow        |                   |
    Then the set light schedule response contains soap fault
      | FaultCode    | SOAP-ENV:Server                            |
      | FaultString  | UNREGISTERED_DEVICE                        |
      | InnerMessage | Device TEST1024000000001 is not registered |


  @OslpMockServer
  Scenario Outline: Set light schedule with 50 schedules # Success
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    And the device returns a set light schedule response "OK" over "<Protocol>"
    And the device returns a set light schedule response "OK" over "<Protocol>"
    And the device returns a set light schedule response "OK" over "<Protocol>"
    And the device returns a set light schedule response "OK" over "<Protocol>"
    And the device returns a set light schedule response "OK" over "<Protocol>"
    And the device returns a set light schedule response "OK" over "<Protocol>"
    And the device returns a set light schedule response "OK" over "<Protocol>"
    And the device returns a set light schedule response "OK" over "<Protocol>"
    And the device returns a set light schedule response "OK" over "<Protocol>"
    And the device returns a set light schedule response "OK" over "<Protocol>"
    When receiving a set light schedule request for 50 schedules
      | DeviceIdentification | TEST1024000000001 |
      | WeekDay              | <WeekDay>         |
      | StartDay             | <StartDay>        |
      | EndDay               | <EndDay>          |
      | ActionTime           | <ActionTime>      |
      | Time                 | <Time>            |
      | LightValues          | <LightValues>     |
      | TriggerType          | <TriggerType>     |
      | TriggerWindow        | <TriggerWindow>   |
    Then the set light schedule async response contains
      | DeviceIdentification | TEST1024000000001 |
    And I wait 7 seconds
    And a set light schedule "<Protocol>" message is sent to device "TEST1024000000001"
      | WeekDay       | <WeekDay>       |
      | StartDay      | <StartDay>      |
      | EndDay        | <EndDay>        |
      | ActionTime    | <ActionTime>    |
      | Time          | <Time>          |
      | LightValues   | <LightValues>   |
      | TriggerType   | <TriggerType>   |
      | TriggerWindow | <TriggerWindow> |
      | ScheduledTime | <ScheduledTime> |
    And the platform buffers a set light schedule response message for device "TEST1024000000001"
      | Result | OK |

    Examples:
      | Protocol    | WeekDay     | StartDay   | EndDay     | ScheduledTime | ActionTime   | Time         | TriggerWindow | LightValues | TriggerType   |
      | OSLP ELSTER | ABSOLUTEDAY | 2016-01-01 | 2016-12-31 | 2016-12-15    | ABSOLUTETIME | 18:00:00.000 |         30,30 | 0,true,     | LIGHT_TRIGGER |

  Scenario Outline: Set light schedule with 2 schedules containing illegal combinations of ActionTime, TriggerWindow and TriggerType # Fail
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    When receiving a set light schedule request for 2 schedules
      | DeviceIdentification | TEST1024000000001 |
      | WeekDay              | <WeekDay>         |
      | StartDay             | <StartDay>        |
      | EndDay               | <EndDay>          |
      | ActionTime           | <ActionTime>      |
      | Time                 | <Time>            |
      | LightValues          | <LightValues>     |
      | TriggerType          | <TriggerType>     |
      | TriggerWindow        | <TriggerWindow>   |
    And the platform buffers a set light schedule response message for device "TEST1024000000001" that contains a soap fault
      | FaultCode   | SOAP-ENV:Client  |
      | FaultString | Validation error |

    Examples:
      | Protocol    | WeekDay | StartDay | EndDay | ActionTime   | Time         | TriggerWindow | LightValues | TriggerType   |
      | OSLP ELSTER | ALL     |          |        | ABSOLUTETIME | 18:00:00.000 |               | 2,true,     | ASTRONOMICAL  |
      | OSLP ELSTER | ALL     |          |        | SUNRISE      |              |               | 2,true,     | LIGHT_TRIGGER |
      | OSLP ELSTER | ALL     |          |        | SUNSET       |              |               | 2,false,    | LIGHT_TRIGGER |
      | OSLP ELSTER | ALL     |          |        | SUNRISE      |              |         30,30 | 2,true,     | ASTRONOMICAL  |
      | OSLP ELSTER | ALL     |          |        | SUNSET       |              |         30,30 | 2,false,    | ASTRONOMICAL  |

  Scenario Outline: Set light schedule with 51 schedules # Fail
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
    When receiving a set light schedule request for 51 schedules
      | DeviceIdentification | TEST1024000000001 |
      | WeekDay              | <WeekDay>         |
      | StartDay             | <StartDay>        |
      | EndDay               | <EndDay>          |
      | ActionTime           | <ActionTime>      |
      | Time                 | <Time>            |
      | LightValues          | <LightValues>     |
      | TriggerType          | <TriggerType>     |
      | TriggerWindow        | <TriggerWindow>   |
      | ScheduledTime        | 2016-12-15        |
    Then the set light schedule response contains soap fault
      | FaultCode        | SOAP-ENV:Client                                                                                                                                                                                                                                                           |
      | FaultString      | Validation error                                                                                                                                                                                                                                                          |
      | ValidationErrors | cvc-complex-type.2.4.e: 'ns2:Schedules' can occur a maximum of '50' times in the current sequence. This limit was exceeded. At this point one of '{"http://www.opensmartgridplatform.org/schemas/publiclighting/schedulemanagement/2014/10":scheduled_time}' is expected. |

    Examples:
      | WeekDay     | StartDay   | EndDay     | ActionTime   | Time         | TriggerWindow | LightValues | TriggerType   |
      | ABSOLUTEDAY | 2016-01-01 | 2016-12-31 | ABSOLUTETIME | 18:00:00.000 |         30,30 | 0,true,     | LIGHT_TRIGGER |
