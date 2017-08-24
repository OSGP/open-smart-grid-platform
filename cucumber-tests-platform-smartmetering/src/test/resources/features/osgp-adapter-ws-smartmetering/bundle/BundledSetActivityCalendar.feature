@SmartMetering @Platform
Feature: SmartMetering Bundle - SetActivityCalendar
  As a grid operator 
  I want to be able to set activity calendsar on a meter via a bundle request

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

  Scenario: Set activity calendar on a device in a bundle request
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a set activity calendar action with parameters
      | CalendarName                | testcal1                 |
      | ActivatePassiveCalendarTime | FFFFFFFEFFFFFFFFFF000000 |
    #    And the activity calendar contains a season profile
    #      |SeasonProfileName | season01 |
    #      |SeasonStart | FFFF0C03FFFFFFFFFF000000 |
    #    And the season profile season01 contains a week profile
    #      | WeekProfileName | week0016 |
    #    And the week profile week0016 contains a day profile for monday
    #      | DayId | 1 |
    #      | DayScheduleCount | 1|
    #      | StartTime_1 | 06050000 |
    #      | ScriptSelector_1 | 1|
    #    And the week profile week0016 contains a day profile for tuesday
    #      | DayId | 1 |
    #      | DayScheduleCount | 1|
    #      | StartTime_1 | 06000000 |
    #      | ScriptSelector_1 | 1|
    #    And the week profile week0016 contains a day profile for wednesday
    #      | DayId | 1 |
    #      | DayScheduleCount | 1|
    #      | StartTime_1 | 06000000 |
    #      | ScriptSelector_1 | 1|
    #    And the week profile week0016 contains a day profile for thursday
    #      | DayId | 1 |
    #      | DayScheduleCount | 1|
    #      | StartTime_1 | 06000000 |
    #      | ScriptSelector_1 | 1|
    #    And the week profile week0016 contains a day profile for friday
    #      | DayId | 1 |
    #      | DayScheduleCount | 1|
    #      | StartTime_1 | 06000000 |
    #      | ScriptSelector_1 | 1|
    #    And the week profile week0016 contains a day profile for saturday
    #      | DayId | 1 |
    #      | DayScheduleCount | 1|
    #      | StartTime_1 | 06050000 |
    #      | ScriptSelector_1 | 1|
    #    And the week profile week0016 contains a day profile for sunday
    #      | DayId | 1 |
    #      | DayScheduleCount | 1|
    #      | StartTime_1 | 06050000 |
    #      | ScriptSelector_1 | 1|
    When the bundle request is received
    Then the bundle response should contain a set special days response with values
      | Result | OK |
