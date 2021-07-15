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
      | SeasonProfileName | SEASON01                 |
      | SeasonStart       | FFFF0101FFFFFFFFFF000000 |
      | WeekName          | WEEK0001                 |
    And the activity calendar contains a week profile
      | WeekProfileName | WEEK0001 |
      | MondayDayId     |        0 |
      | TuesdayDayId    |        0 |
      | WednesdayDayId  |        0 |
      | ThursdayDayId   |        0 |
      | FridayDayId     |        0 |
      | SaturdayDayId   |        0 |
      | SundayDayId     |        0 |
    And the activity calendar contains a week profile
      | WeekProfileName | WEEK0002 |
      | MondayDayId     |        1 |
      | TuesdayDayId    |        1 |
      | WednesdayDayId  |        1 |
      | ThursdayDayId   |        1 |
      | FridayDayId     |        1 |
      | SaturdayDayId   |        0 |
      | SundayDayId     |        0 |
    And the activity calendar contains a week profile
      | WeekProfileName | WEEK0003 |
      | MondayDayId     |        2 |
      | TuesdayDayId    |        2 |
      | WednesdayDayId  |        2 |
      | ThursdayDayId   |        2 |
      | FridayDayId     |        2 |
      | SaturdayDayId   |        0 |
      | SundayDayId     |        0 |
    And the activity calendar contains a day profile
      | DayId                 |        0 |
      | DayProfileActionCount |        1 |
      | StartTime_1           | 00000000 |
      | ScriptSelector_1      |        1 |
    And the activity calendar contains a day profile
      | DayId                 |        1 |
      | DayProfileActionCount |        3 |
      | StartTime_1           | 00000000 |
      | ScriptSelector_1      |        1 |
      | StartTime_2           | 07000000 |
      | ScriptSelector_2      |        2 |
      | StartTime_3           | 21000000 |
      | ScriptSelector_3      |        1 |
    And the activity calendar contains a day profile
      | DayId                 |        2 |
      | DayProfileActionCount |        3 |
      | StartTime_1           | 00000000 |
      | ScriptSelector_1      |        1 |
      | StartTime_2           | 07000000 |
      | ScriptSelector_2      |        2 |
      | StartTime_3           | 21000000 |
      | ScriptSelector_3      |        1 |
    And the bundle request contains a set activity calendar action
    When the bundle request is received
    Then the bundle response should contain a set special days response with values
      | Result | OK |
    And the response data record should not be deleted
