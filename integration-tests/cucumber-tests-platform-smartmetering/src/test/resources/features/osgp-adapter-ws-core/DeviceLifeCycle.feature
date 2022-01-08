@SmartMetering @Platform @NightlyBuildOnly
Feature: Core Operations, DeviceLifeCycle
  As a grid operator
  I want to distinguish the various statuses of a device
  So I know what I can or cannot do with the device

  Scenario: Set dlms device status
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When the SetDeviceLifecycleStatus request is received
      | DeviceIdentification  | TEST1024000000001 |
      | DeviceLifecycleStatus | IN_USE          |
    And the notification is recieved
      | NotificationType | SET_DEVICE_LIFECYCLE_STATUS |
    Then the device lifecycle status in the database is
      | DeviceIdentification  | TEST1024000000001 |
      | DeviceLifecycleStatus | IN_USE          |
