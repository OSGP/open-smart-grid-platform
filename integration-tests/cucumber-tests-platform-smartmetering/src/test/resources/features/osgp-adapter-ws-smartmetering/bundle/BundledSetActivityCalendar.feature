@SmartMetering @Platform
Feature: SmartMetering Bundle - SetActivityCalendar
  As a grid operator 
  I want to be able to set activity calendar on a meter via a bundle request

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

  Scenario: Set activity calendar on a device in a bundle request
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And an activity calendar
      | ActivityCalendarName        | CALENDAR                 |
      | ActivatePassiveCalendarTime | FFFFFFFEFFFFFFFFFF000000 |
    And the activity calendar contains a season profile
      | SeasonProfileName | 1                        |
      | SeasonStart       | FFFF0101FF00000000FFC400 |
      | WeekName          | 1                        |
    And the activity calendar contains a season profile
      | SeasonProfileName | 2                        |
      | SeasonStart       | FFFF0701FF00000000FFC400 |
      | WeekName          | 2                        |
    And the activity calendar contains a week profile
      | WeekProfileName | 1 |
      | MondayDayId     | 0 |
      | TuesdayDayId    | 0 |
      | WednesdayDayId  | 0 |
      | ThursdayDayId   | 0 |
      | FridayDayId     | 0 |
      | SaturdayDayId   | 1 |
      | SundayDayId     | 1 |
    And the activity calendar contains a week profile
      | WeekProfileName | 2 |
      | MondayDayId     | 0 |
      | TuesdayDayId    | 0 |
      | WednesdayDayId  | 0 |
      | ThursdayDayId   | 0 |
      | FridayDayId     | 0 |
      | SaturdayDayId   | 2 |
      | SundayDayId     | 2 |
    And the activity calendar contains a day profile
      | DayId                 |        0 |
      | DayProfileActionCount |        3 |
      | StartTime_1           | 00000000 |
      | ScriptSelector_1      |        1 |
      | StartTime_2           | 07000000 |
      | ScriptSelector_2      |        2 |
      | StartTime_3           | 17000000 |
      | ScriptSelector_3      |        1 |
    And the activity calendar contains a day profile
      | DayId                 |        1 |
      | DayProfileActionCount |        1 |
      | StartTime_1           | 00000000 |
      | ScriptSelector_1      |        1 |
    And the activity calendar contains a day profile
      | DayId                 |        2 |
      | DayProfileActionCount |        3 |
      | StartTime_1           | 00000000 |
      | ScriptSelector_1      |        1 |
      | StartTime_2           | 07000000 |
      | ScriptSelector_2      |        2 |
      | StartTime_3           | 16000000 |
      | ScriptSelector_3      |        3 |
    And the bundle request contains a set activity calendar action
    When the bundle request is received
    Then the bundle response should contain a set special days response with values
      | Result | OK |
