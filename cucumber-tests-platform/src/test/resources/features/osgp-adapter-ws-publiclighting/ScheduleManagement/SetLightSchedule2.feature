Feature: SetSchedule
  In order to ... 
  As a platform 
  I want to ...

  @OslpMockServer
  Scenario Outline: Set light schedule
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
    And the device returns a set light schedule response over OSLP
      | Status | OK |
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
    And a set light schedule OSLP message is sent to device "TEST1024000000001"
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
      | WeekDay     | StartDay   | EndDay     | ActionTime   | Time         | TriggerWindow | LightValues | TriggerType |
      #| MONDAY      |            |            | ABSOLUTETIME | 18:00:00.000 |               | 0,true,           |               |
      #| MONDAY      |            |            | ABSOLUTETIME | 18:00:00.000 |               | 1,true,;2,true,50 |               |
      #| ABSOLUTEDAY | 2013-03-01 |            | ABSOLUTETIME | 18:00:00.000 |               | 0,true,           |               |
      #| MONDAY      |            |            | SUNSET       |              |         30,30 | 0,true,           | LIGHT_TRIGGER |
      | ABSOLUTEDAY | 2016-01-01 | 2016-12-31 | ABSOLUTETIME | 18:00:00.000 |               | 0,true,     |             |
  #
  #@OslpMockServer @Skip
  #Scenario: Failed set light schedule
  #Given an oslp device
  #| DeviceIdentification | TEST1024000000001 |
  #And the device returns a set light schedule response over OSLP
  #| Status | FAILURE |
  #When receiving a set light schedule request
  #| DeviceIdentification | TEST1024000000001 |
  #| WeekDay              | MONDAY            |
  #| StartDay             |                   |
  #| EndDay               |                   |
  #| ActionTime           | ABSOLUTETIME      |
  #| Time                 | 18:00:00.000      |
  #| LightValues          | 0,true,           |
  #| TriggerType          |                   |
  #| TriggerWindow        |                   |
  #Then the set light schedule async response contains
  #| DeviceIdentification | TEST1024000000001 |
  #And a set light schedule OSLP message is sent to device "TEST1024000000001"
  #| WeekDay       | MONDAY       |
  #| StartDay      |              |
  #| EndDay        |              |
  #| ActionTime    | ABSOLUTETIME |
  #| Time          | 18:00:00.000 |
  #| LightValues   | 0,true,      |
  #| TriggerType   |              |
  #| TriggerWindow |              |
  #And the platform buffers a set light schedule response message for device "TEST1024000000001"
  #| Result      | NOT OK                       |
  #| Description | DEVICEMESSAGEFAILEDEXCEPTION |
  #
  #@Skip
  #Scenario: Rejected set light schedule
  #Given an oslp device
  #| DeviceIdentification | TEST1024000000001 |
  #And the device returns a set light schedule response over OSLP
  #| Status | REJECTED |
  #When receiving a set light schedule request
  #| DeviceIdentification | TEST1024000000001 |
  #| WeekDay              | MONDAY            |
  #| StartDay             |                   |
  #| EndDay               |                   |
  #| ActionTime           | ABSOLUTETIME      |
  #| Time                 | 18:00:00.000      |
  #| LightValues          | 0,true,           |
  #| TriggerType          |                   |
  #| TriggerWindow        |                   |
  #Then the set light schedule async response contains
  #| DeviceIdentification | TEST1024000000001 |
  #And a set light schedule OSLP message is sent to device "TEST1024000000001"
  #| WeekDay       | MONDAY       |
  #| StartDay      |              |
  #| EndDay        |              |
  #| ActionTime    | ABSOLUTETIME |
  #| Time          | 18:00:00.000 |
  #| LightValues   | 0,true,      |
  #| TriggerType   |              |
  #| TriggerWindow |              |
  #And the platform buffers a set light schedule response message for device "TEST1024000000001"
  #| Result      | NOT OK                       |
  #| Description | DEVICEMESSAGEFAILEDEXCEPTION |
  #
  #@OslpMockServer
  #Scenario Outline: Set light schedule with 50 schedules # Success
    #Given an oslp device
      #| DeviceIdentification | TEST1024000000001 |
    #And the device returns a set light schedule response over OSLP
      #| Status | OK |
    #When receiving a set light schedule request for 50 schedules
      #| DeviceIdentification | TEST1024000000001 |
      #| WeekDay              | <WeekDay>         |
      #| StartDay             | <StartDay>        |
      #| EndDay               | <EndDay>          |
      #| ActionTime           | <ActionTime>      |
      #| Time                 | <Time>            |
      #| LightValues          | <LightValues>     |
      #| TriggerType          | <TriggerType>     |
      #| TriggerWindow        | <TriggerWindow>   |
    #Then the set light schedule async response contains
      #| DeviceIdentification | TEST1024000000001 |
    #And a set light schedule OSLP message is sent to device "TEST1024000000001"
      #| WeekDay       | <WeekDay>       |
      #| StartDay      | <StartDay>      |
      #| EndDay        | <EndDay>        |
      #| ActionTime    | <ActionTime>    |
      #| Time          | <Time>          |
      #| LightValues   | <LightValues>   |
      #| TriggerType   | <TriggerType>   |
      #| TriggerWindow | <TriggerWindow> |
      #| ScheduledTime | 2016-12-15      |
    #And the platform buffers a set light schedule response message for device "TEST1024000000001"
      #| Result | OK |
#
    #Examples: 
      #| WeekDay     | StartDay   | EndDay     | ActionTime   | Time         | TriggerWindow | LightValues | TriggerType   |
      #| ABSOLUTEDAY | 2016-01-01 | 2016-12-31 | ABSOLUTETIME | 18:00:00.000 |         30,30 | 0,true,     | LIGHT_TRIGGER |
