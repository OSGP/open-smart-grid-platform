Feature: BasiOsgpFunctions Authorizing Device Functions
  As a ...
  I want to ...
  In order to ...

  Scenario Outline: Call a device function and verify whether this is allowed
    Given a registered device
      | DeviceIdentification | TEST1024000000001 |
    And an organization
      | OrganizationIdentification | test-org |
      | FunctionGroup              |          |
    When receiving a device function request
      | DeviceFunction |  |
    Then the device function response is successful

    Examples: 
      | function                 | group             | allowed |  
      | START_SELF_TEST          | OWNER             | true    |  
      | START_SELF_TEST          | INSTALLATION      | true    |  
      | START_SELF_TEST          | AD_HOC            | false   |  
      | START_SELF_TEST          | MANAGEMENT        | false   |  
      | START_SELF_TEST          | FIRMWARE          | false   |  
      | START_SELF_TEST          | SCHEDULING        | false   |  
      | START_SELF_TEST          | TARIFF_SCHEDULING | false   |  
      | START_SELF_TEST          | CONFIGURATION     | false   |  
      | START_SELF_TEST          | MONITORING        | false   |  
      | STOP_SELF_TEST           | OWNER             | true    |  
      | STOP_SELF_TEST           | INSTALLATION      | true    |  
      | STOP_SELF_TEST           | AD_HOC            | false   |  
      | STOP_SELF_TEST           | MANAGEMENT        | false   |  
      | STOP_SELF_TEST           | FIRMWARE          | false   |  
      | STOP_SELF_TEST           | SCHEDULING        | false   |  
      | STOP_SELF_TEST           | TARIFF_SCHEDULING | false   |  
      | STOP_SELF_TEST           | CONFIGURATION     | false   |  
      | STOP_SELF_TEST           | MONITORING        | false   |  
      | SET_LIGHT                | OWNER             | true    |  
      | SET_LIGHT                | INSTALLATION      | false   |  
      | SET_LIGHT                | AD_HOC            | true    |  
      | SET_LIGHT                | MANAGEMENT        | false   |  
      | SET_LIGHT                | FIRMWARE          | false   |  
      | SET_LIGHT                | SCHEDULING        | false   |  
      | SET_LIGHT                | TARIFF_SCHEDULING | false   |  
      | SET_LIGHT                | CONFIGURATION     | false   |  
      | SET_LIGHT                | MONITORING        | false   |  
      | GET_STATUS               | OWNER             | true    |  
      | GET_STATUS               | INSTALLATION      | false   |  
      | GET_STATUS               | AD_HOC            | true    |  
      | GET_STATUS               | MANAGEMENT        | false   |  
      | GET_STATUS               | FIRMWARE          | false   |  
      | GET_STATUS               | SCHEDULING        | false   |  
      | GET_STATUS               | TARIFF_SCHEDULING | false   |  
      | GET_STATUS               | CONFIGURATION     | false   |  
      | GET_STATUS               | MONITORING        | false   |  
      | GET_DEVICE_AUTHORIZATION | OWNER             | true    |  
      | GET_DEVICE_AUTHORIZATION | INSTALLATION      | true    |  
      | GET_DEVICE_AUTHORIZATION | AD_HOC            | true    |  
      | GET_DEVICE_AUTHORIZATION | MANAGEMENT        | true    |  
      | GET_DEVICE_AUTHORIZATION | FIRMWARE          | true    |  
      | GET_DEVICE_AUTHORIZATION | SCHEDULING        | true    |  
      | GET_DEVICE_AUTHORIZATION | TARIFF_SCHEDULING | true    |  
      | GET_DEVICE_AUTHORIZATION | CONFIGURATION     | true    |  
      | GET_DEVICE_AUTHORIZATION | MONITORING        | true    |  
      | SET_EVENT_NOTIFICATIONS  | OWNER             | true    |  
      | SET_EVENT_NOTIFICATIONS  | INSTALLATION      | false   |  
      | SET_EVENT_NOTIFICATIONS  | AD_HOC            | false   |  
      | SET_EVENT_NOTIFICATIONS  | MANAGEMENT        | true    |  
      | SET_EVENT_NOTIFICATIONS  | FIRMWARE          | false   |  
      | SET_EVENT_NOTIFICATIONS  | SCHEDULING        | false   |  
      | SET_EVENT_NOTIFICATIONS  | TARIFF_SCHEDULING | false   |  
      | SET_EVENT_NOTIFICATIONS  | CONFIGURATION     | false   |  
      | SET_EVENT_NOTIFICATIONS  | MONITORING        | false   |  
      | GET_EVENT_NOTIFICATIONS  | OWNER             | true    |  
      | GET_EVENT_NOTIFICATIONS  | INSTALLATION      | false   |  
      | GET_EVENT_NOTIFICATIONS  | AD_HOC            | false   |  
      | GET_EVENT_NOTIFICATIONS  | MANAGEMENT        | true    |  
      | GET_EVENT_NOTIFICATIONS  | FIRMWARE          | false   |  
      | GET_EVENT_NOTIFICATIONS  | SCHEDULING        | false   |  
      | GET_EVENT_NOTIFICATIONS  | TARIFF_SCHEDULING | false   |  
      | GET_EVENT_NOTIFICATIONS  | CONFIGURATION     | false   |  
      | GET_EVENT_NOTIFICATIONS  | MONITORING        | false   |  
      | UPDATE_FIRMWARE          | OWNER             | true    |  
      | UPDATE_FIRMWARE          | INSTALLATION      | false   |  
      | UPDATE_FIRMWARE          | AD_HOC            | false   |  
      | UPDATE_FIRMWARE          | MANAGEMENT        | false   |  
      | UPDATE_FIRMWARE          | FIRMWARE          | true    |  
      | UPDATE_FIRMWARE          | SCHEDULING        | false   |  
      | UPDATE_FIRMWARE          | TARIFF_SCHEDULING | false   |  
      | UPDATE_FIRMWARE          | CONFIGURATION     | false   |  
      | UPDATE_FIRMWARE          | MONITORING        | false   |  
      | GET_FIRMWARE_VERSION     | OWNER             | true    |  
      | GET_FIRMWARE_VERSION     | INSTALLATION      | false   |  
      | GET_FIRMWARE_VERSION     | AD_HOC            | false   |  
      | GET_FIRMWARE_VERSION     | MANAGEMENT        | false   |  
      | GET_FIRMWARE_VERSION     | FIRMWARE          | true    |  
      | GET_FIRMWARE_VERSION     | SCHEDULING        | false   |  
      | GET_FIRMWARE_VERSION     | TARIFF_SCHEDULING | false   |  
      | GET_FIRMWARE_VERSION     | CONFIGURATION     | false   |  
      | GET_FIRMWARE_VERSION     | MONITORING        | false   |  
      | SET_SCHEDULE             | OWNER             | true    |  
      | SET_SCHEDULE             | INSTALLATION      | false   |  
      | SET_SCHEDULE             | AD_HOC            | false   |  
      | SET_SCHEDULE             | MANAGEMENT        | false   |  
      | SET_SCHEDULE             | FIRMWARE          | false   |  
      | SET_SCHEDULE             | SCHEDULING        | true    |  
      | SET_SCHEDULE             | TARIFF_SCHEDULING | false   |  
