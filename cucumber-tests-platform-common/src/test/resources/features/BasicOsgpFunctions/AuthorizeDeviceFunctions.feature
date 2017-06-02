@PublicLighting @Platform @BasicOsgpFunctions
Feature: BasicOsgpFunctions Common Authorizing Device Functions
  As a ...
  I want to ...
  In order to ...

  Scenario Outline: Call a device function and verify whether this is allowed
    Given a device
      | DeviceIdentification       | TEST1024000000001     |
      | OrganizationIdentification | test-org              |
      | DeviceFunctionGroup        | <DeviceFunctionGroup> |
    When receiving a device function request
      | DeviceIdentification       | TEST1024000000001 |
      | OrganizationIdentification | test-org          |
      | DeviceFunction             | <DeviceFunction>  |
    Then the device function response is "<Allowed>"

    Examples: 
      | DeviceFunction           | DeviceFunctionGroup | Allowed |
      | START_SELF_TEST          | OWNER               | true    |
      | START_SELF_TEST          | INSTALLATION        | true    |
      | START_SELF_TEST          | AD_HOC              | false   |
      | START_SELF_TEST          | MANAGEMENT          | false   |
      | START_SELF_TEST          | FIRMWARE            | false   |
      | START_SELF_TEST          | SCHEDULING          | false   |
      | START_SELF_TEST          | TARIFF_SCHEDULING   | false   |
      | START_SELF_TEST          | CONFIGURATION       | false   |
      | START_SELF_TEST          | MONITORING          | false   |
      | START_SELF_TEST          | METADATA_MANAGEMENT | false   |
      | STOP_SELF_TEST           | OWNER               | true    |
      | STOP_SELF_TEST           | INSTALLATION        | true    |
      | STOP_SELF_TEST           | AD_HOC              | false   |
      | STOP_SELF_TEST           | MANAGEMENT          | false   |
      | STOP_SELF_TEST           | FIRMWARE            | false   |
      | STOP_SELF_TEST           | SCHEDULING          | false   |
      | STOP_SELF_TEST           | TARIFF_SCHEDULING   | false   |
      | STOP_SELF_TEST           | CONFIGURATION       | false   |
      | STOP_SELF_TEST           | MONITORING          | false   |
      | STOP_SELF_TEST           | METADATA_MANAGEMENT | false   |
      | GET_STATUS               | OWNER               | true    |
      | GET_STATUS               | INSTALLATION        | false   |
      | GET_STATUS               | AD_HOC              | true    |
      | GET_STATUS               | MANAGEMENT          | false   |
      | GET_STATUS               | FIRMWARE            | false   |
      | GET_STATUS               | SCHEDULING          | false   |
      | GET_STATUS               | TARIFF_SCHEDULING   | false   |
      | GET_STATUS               | CONFIGURATION       | false   |
      | GET_STATUS               | MONITORING          | false   |
      | GET_STATUS               | METADATA_MANAGEMENT | false   |
      | SET_DEVICE_AUTHORIZATION | OWNER               | true    |
      | SET_DEVICE_AUTHORIZATION | INSTALLATION        | true    |
      | SET_DEVICE_AUTHORIZATION | AD_HOC              | true    |
      | SET_DEVICE_AUTHORIZATION | MANAGEMENT          | true    |
      | SET_DEVICE_AUTHORIZATION | FIRMWARE            | true    |
      | SET_DEVICE_AUTHORIZATION | SCHEDULING          | true    |
      | SET_DEVICE_AUTHORIZATION | TARIFF_SCHEDULING   | true    |
      | SET_DEVICE_AUTHORIZATION | CONFIGURATION       | true    |
      | SET_DEVICE_AUTHORIZATION | MONITORING          | true    |
      | SET_DEVICE_AUTHORIZATION | METADATA_MANAGEMENT | true    |
      | GET_DEVICE_AUTHORIZATION | OWNER               | true    |
      | GET_DEVICE_AUTHORIZATION | INSTALLATION        | true    |
      | GET_DEVICE_AUTHORIZATION | AD_HOC              | true    |
      | GET_DEVICE_AUTHORIZATION | MANAGEMENT          | true    |
      | GET_DEVICE_AUTHORIZATION | FIRMWARE            | true    |
      | GET_DEVICE_AUTHORIZATION | SCHEDULING          | true    |
      | GET_DEVICE_AUTHORIZATION | TARIFF_SCHEDULING   | true    |
      | GET_DEVICE_AUTHORIZATION | CONFIGURATION       | true    |
      | GET_DEVICE_AUTHORIZATION | MONITORING          | true    |
      | GET_DEVICE_AUTHORIZATION | METADATA_MANAGEMENT | true    |
      | SET_EVENT_NOTIFICATIONS  | OWNER               | true    |
      | SET_EVENT_NOTIFICATIONS  | INSTALLATION        | false   |
      | SET_EVENT_NOTIFICATIONS  | AD_HOC              | false   |
      | SET_EVENT_NOTIFICATIONS  | MANAGEMENT          | true    |
      | SET_EVENT_NOTIFICATIONS  | FIRMWARE            | false   |
      | SET_EVENT_NOTIFICATIONS  | SCHEDULING          | false   |
      | SET_EVENT_NOTIFICATIONS  | TARIFF_SCHEDULING   | false   |
      | SET_EVENT_NOTIFICATIONS  | CONFIGURATION       | false   |
      | SET_EVENT_NOTIFICATIONS  | MONITORING          | false   |
      | SET_EVENT_NOTIFICATIONS  | METADATA_MANAGEMENT | false   |
      | GET_EVENT_NOTIFICATIONS  | OWNER               | true    |
      | GET_EVENT_NOTIFICATIONS  | INSTALLATION        | false   |
      | GET_EVENT_NOTIFICATIONS  | AD_HOC              | false   |
      | GET_EVENT_NOTIFICATIONS  | MANAGEMENT          | true    |
      | GET_EVENT_NOTIFICATIONS  | FIRMWARE            | false   |
      | GET_EVENT_NOTIFICATIONS  | SCHEDULING          | false   |
      | GET_EVENT_NOTIFICATIONS  | TARIFF_SCHEDULING   | false   |
      | GET_EVENT_NOTIFICATIONS  | CONFIGURATION       | false   |
      | GET_EVENT_NOTIFICATIONS  | MONITORING          | false   |
      | GET_EVENT_NOTIFICATIONS  | METADATA_MANAGEMENT | false   |
      | UPDATE_FIRMWARE          | OWNER               | true    |
      | UPDATE_FIRMWARE          | INSTALLATION        | false   |
      | UPDATE_FIRMWARE          | AD_HOC              | false   |
      | UPDATE_FIRMWARE          | MANAGEMENT          | false   |
      | UPDATE_FIRMWARE          | FIRMWARE            | true    |
      | UPDATE_FIRMWARE          | SCHEDULING          | false   |
      | UPDATE_FIRMWARE          | TARIFF_SCHEDULING   | false   |
      | UPDATE_FIRMWARE          | CONFIGURATION       | false   |
      | UPDATE_FIRMWARE          | MONITORING          | false   |
      | UPDATE_FIRMWARE          | METADATA_MANAGEMENT | false   |
      | GET_FIRMWARE_VERSION     | OWNER               | true    |
      | GET_FIRMWARE_VERSION     | INSTALLATION        | false   |
      | GET_FIRMWARE_VERSION     | AD_HOC              | false   |
      | GET_FIRMWARE_VERSION     | MANAGEMENT          | false   |
      | GET_FIRMWARE_VERSION     | FIRMWARE            | true    |
      | GET_FIRMWARE_VERSION     | SCHEDULING          | false   |
      | GET_FIRMWARE_VERSION     | TARIFF_SCHEDULING   | false   |
      | GET_FIRMWARE_VERSION     | CONFIGURATION       | false   |
      | GET_FIRMWARE_VERSION     | MONITORING          | false   |
      | GET_FIRMWARE_VERSION     | METADATA_MANAGEMENT | false   |
      | SET_CONFIGURATION        | OWNER               | true    |
      | SET_CONFIGURATION        | INSTALLATION        | false   |
      | SET_CONFIGURATION        | AD_HOC              | false   |
      | SET_CONFIGURATION        | MANAGEMENT          | false   |
      | SET_CONFIGURATION        | FIRMWARE            | false   |
      | SET_CONFIGURATION        | SCHEDULING          | false   |
      | SET_CONFIGURATION        | TARIFF_SCHEDULING   | false   |
      | SET_CONFIGURATION        | CONFIGURATION       | true    |
      | SET_CONFIGURATION        | MONITORING          | false   |
      | SET_CONFIGURATION        | METADATA_MANAGEMENT | false   |
      | GET_CONFIGURATION        | OWNER               | true    |
      | GET_CONFIGURATION        | INSTALLATION        | false   |
      | GET_CONFIGURATION        | AD_HOC              | false   |
      | GET_CONFIGURATION        | MANAGEMENT          | false   |
      | GET_CONFIGURATION        | FIRMWARE            | false   |
      | GET_CONFIGURATION        | SCHEDULING          | false   |
      | GET_CONFIGURATION        | TARIFF_SCHEDULING   | false   |
      | GET_CONFIGURATION        | CONFIGURATION       | true    |
      | GET_CONFIGURATION        | MONITORING          | false   |
      | GET_CONFIGURATION        | METADATA_MANAGEMENT | false   |
      | REMOVE_DEVICE            | OWNER               | true    |
      | REMOVE_DEVICE            | INSTALLATION        | false   |
      | REMOVE_DEVICE            | AD_HOC              | false   |
      | REMOVE_DEVICE            | MANAGEMENT          | true    |
      | REMOVE_DEVICE            | FIRMWARE            | false   |
      | REMOVE_DEVICE            | SCHEDULING          | false   |
      | REMOVE_DEVICE            | TARIFF_SCHEDULING   | false   |
      | REMOVE_DEVICE            | CONFIGURATION       | false   |
      | REMOVE_DEVICE            | MONITORING          | false   |
      | REMOVE_DEVICE            | METADATA_MANAGEMENT | false   |
      | SET_REBOOT               | OWNER               | true    |
      | SET_REBOOT               | INSTALLATION        | false   |
      | SET_REBOOT               | AD_HOC              | true    |
      | SET_REBOOT               | MANAGEMENT          | false   |
      | SET_REBOOT               | FIRMWARE            | false   |
      | SET_REBOOT               | SCHEDULING          | false   |
      | SET_REBOOT               | TARIFF_SCHEDULING   | false   |
      | SET_REBOOT               | CONFIGURATION       | false   |
      | SET_REBOOT               | MONITORING          | false   |
      | SET_REBOOT               | METADATA_MANAGEMENT | false   |
      | DEACTIVATE_DEVICE        | OWNER               | true    |
      | DEACTIVATE_DEVICE        | INSTALLATION        | true    |
      | DEACTIVATE_DEVICE        | AD_HOC              | true    |
      | DEACTIVATE_DEVICE        | MANAGEMENT          | true    |
      | DEACTIVATE_DEVICE        | FIRMWARE            | true    |
      | DEACTIVATE_DEVICE        | SCHEDULING          | true    |
      | DEACTIVATE_DEVICE        | TARIFF_SCHEDULING   | true    |
      | DEACTIVATE_DEVICE        | CONFIGURATION       | true    |
      | DEACTIVATE_DEVICE        | MONITORING          | true    |
      | DEACTIVATE_DEVICE        | METADATA_MANAGEMENT | true    |

  Scenario Outline: Change device authorization
    Given a device
      | DeviceIdentification | TEST1024000000001     |
      | DeviceFunctionGroup  | <DeviceFunctionGroup> |
    When receiving a device function request
      | DeviceIdentification  | TEST1024000000001        |
      | DeviceFunction        | SET_DEVICE_AUTHORIZATION |
      | DelegateFunctionGroup | <DelegateFunctionGroup>  |
      | DeviceFunctionGroup   | <DeviceFunctionGroup>    |
    Then the device function response is "<Allowed>"

    Examples: 
      | DeviceFunctionGroup | DelegateFunctionGroup | Allowed |
      | OWNER               | OWNER                 | false   |
      | OWNER               | INSTALLATION          | true    |
      | OWNER               | AD_HOC                | true    |
      | OWNER               | MANAGEMENT            | true    |
      | OWNER               | FIRMWARE              | true    |
      | OWNER               | SCHEDULING            | true    |
      | OWNER               | TARIFF_SCHEDULING     | true    |
      | OWNER               | CONFIGURATION         | true    |
      | OWNER               | MONITORING            | true    |
      | OWNER               | METADATA_MANAGEMENT   | true    |
      | INSTALLATION        | OWNER                 | false   |
      | INSTALLATION        | INSTALLATION          | false   |
      | INSTALLATION        | AD_HOC                | false   |
      | INSTALLATION        | MANAGEMENT            | false   |
      | INSTALLATION        | FIRMWARE              | false   |
      | INSTALLATION        | SCHEDULING            | false   |
      | INSTALLATION        | TARIFF_SCHEDULING     | false   |
      | INSTALLATION        | CONFIGURATION         | false   |
      | INSTALLATION        | MONITORING            | false   |
      | INSTALLATION        | METADATA_MANAGMENT    | false   |
      | AD_HOC              | OWNER                 | false   |
      | AD_HOC              | INSTALLATION          | false   |
      | AD_HOC              | AD_HOC                | false   |
      | AD_HOC              | MANAGEMENT            | false   |
      | AD_HOC              | FIRMWARE              | false   |
      | AD_HOC              | SCHEDULING            | false   |
      | AD_HOC              | TARIFF_SCHEDULING     | false   |
      | AD_HOC              | CONFIGURATION         | false   |
      | AD_HOC              | MONITORING            | false   |
      | AD_HOC              | METADATA_MANAGEMENT   | false   |
      | MANAGEMENT          | OWNER                 | false   |
      | MANAGEMENT          | INSTALLATION          | false   |
      | MANAGEMENT          | AD_HOC                | false   |
      | MANAGEMENT          | MANAGEMENT            | false   |
      | MANAGEMENT          | FIRMWARE              | false   |
      | MANAGEMENT          | SCHEDULING            | false   |
      | MANAGEMENT          | TARIFF_SCHEDULING     | false   |
      | MANAGEMENT          | CONFIGURATION         | false   |
      | MANAGEMENT          | MONITORING            | false   |
      | MANAGEMENT          | METADATA_MANAGEMENT   | false   |
      | FIRMWARE            | OWNER                 | false   |
      | FIRMWARE            | INSTALLATION          | false   |
      | FIRMWARE            | AD_HOC                | false   |
      | FIRMWARE            | MANAGEMENT            | false   |
      | FIRMWARE            | FIRMWARE              | false   |
      | FIRMWARE            | SCHEDULING            | false   |
      | FIRMWARE            | TARIFF_SCHEDULING     | false   |
      | FIRMWARE            | CONFIGURATION         | false   |
      | FIRMWARE            | MONITORING            | false   |
      | FIRMWARE            | METADATA_MANAGEMENT   | false   |
      | SCHEDULING          | OWNER                 | false   |
      | SCHEDULING          | INSTALLATION          | false   |
      | SCHEDULING          | AD_HOC                | false   |
      | SCHEDULING          | MANAGEMENT            | false   |
      | SCHEDULING          | FIRMWARE              | false   |
      | SCHEDULING          | SCHEDULING            | false   |
      | SCHEDULING          | TARIFF_SCHEDULING     | false   |
      | SCHEDULING          | CONFIGURATION         | false   |
      | SCHEDULING          | MONITORING            | false   |
      | SCHEDULING          | METADATA_MANAGEMENT   | false   |
      | TARIFF_SCHEDULING   | OWNER                 | false   |
      | TARIFF_SCHEDULING   | INSTALLATION          | false   |
      | TARIFF_SCHEDULING   | AD_HOC                | false   |
      | TARIFF_SCHEDULING   | MANAGEMENT            | false   |
      | TARIFF_SCHEDULING   | FIRMWARE              | false   |
      | TARIFF_SCHEDULING   | SCHEDULING            | false   |
      | TARIFF_SCHEDULING   | TARIFF_SCHEDULING     | false   |
      | TARIFF_SCHEDULING   | CONFIGURATION         | false   |
      | TARIFF_SCHEDULING   | MONITORING            | false   |
      | TARIFF_SCHEDULING   | METDATA_MANAGEMENT    | false   |
      | CONFIGURATION       | OWNER                 | false   |
      | CONFIGURATION       | INSTALLATION          | false   |
      | CONFIGURATION       | AD_HOC                | false   |
      | CONFIGURATION       | MANAGEMENT            | false   |
      | CONFIGURATION       | FIRMWARE              | false   |
      | CONFIGURATION       | SCHEDULING            | false   |
      | CONFIGURATION       | TARIFF_SCHEDULING     | false   |
      | CONFIGURATION       | CONFIGURATION         | false   |
      | CONFIGURATION       | MONITORING            | false   |
      | CONFIGURATION       | METDATA_MANAGEMENT    | false   |
      | MONITORING          | OWNER                 | false   |
      | MONITORING          | INSTALLATION          | false   |
      | MONITORING          | AD_HOC                | false   |
      | MONITORING          | MANAGEMENT            | false   |
      | MONITORING          | FIRMWARE              | false   |
      | MONITORING          | SCHEDULING            | false   |
      | MONITORING          | TARIFF_SCHEDULING     | false   |
      | MONITORING          | CONFIGURATION         | false   |
      | MONITORING          | MONITORING            | false   |
      | MONITORING          | METADATA_MANAGEMENT   | false   |
      | METADATA_MANAGEMENT | OWNER                 | false   |
      | METADATA_MANAGEMENT | INSTALLATION          | false   |
      | METADATA_MANAGEMENT | AD_HOC                | false   |
      | METADATA_MANAGEMENT | MANAGEMENT            | false   |
      | METADATA_MANAGEMENT | FIRMWARE              | false   |
      | METADATA_MANAGEMENT | SCHEDULING            | false   |
      | METADATA_MANAGEMENT | TARIFF_SCHEDULING     | false   |
      | METADATA_MANAGEMENT | CONFIGURATION         | false   |
      | METADATA_MANAGEMENT | MONITORING            | false   |
      | METADATA_MANAGEMENT | METADATA_MANAGEMENT   | false   |
