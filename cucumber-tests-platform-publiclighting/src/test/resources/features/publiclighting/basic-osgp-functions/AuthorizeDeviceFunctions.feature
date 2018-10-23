@PublicLighting @Platform @BasicOsgpFunctions
Feature: BasicOsgpFunctions PublicLighting Authorizing Device Functions
  As a ...
  I want to ...
  In order to ...

  Scenario Outline: Call a device function and verify whether this is allowed
    Given a device
      | DeviceIdentification | TEST1024000000001     |
      | DeviceFunctionGroup  | <DeviceFunctionGroup> |
    When receiving a publiclighting device function request
      | DeviceIdentification | TEST1024000000001 |
      | DeviceFunction       | <DeviceFunction>  |
    Then the publiclighting device function response is "<Allowed>"

    Examples: 
      | DeviceFunction           | DeviceFunctionGroup | Allowed |
      | SET_LIGHT                | OWNER               | true    |
      | SET_LIGHT                | INSTALLATION        | false   |
      | SET_LIGHT                | AD_HOC              | true    |
      | SET_LIGHT                | MANAGEMENT          | false   |
      | SET_LIGHT                | FIRMWARE            | false   |
      | SET_LIGHT                | SCHEDULING          | false   |
      | SET_LIGHT                | TARIFF_SCHEDULING   | false   |
      | SET_LIGHT                | CONFIGURATION       | false   |
      | SET_LIGHT                | MONITORING          | false   |
      | SET_LIGHT                | METADATA_MANAGEMENT | false   |
      | GET_LIGHT_STATUS         | OWNER               | true    |
      | GET_LIGHT_STATUS         | INSTALLATION        | false   |
      | GET_LIGHT_STATUS         | AD_HOC              | true    |
      | GET_LIGHT_STATUS         | MANAGEMENT          | false   |
      | GET_LIGHT_STATUS         | FIRMWARE            | false   |
      | GET_LIGHT_STATUS         | SCHEDULING          | false   |
      | GET_LIGHT_STATUS         | TARIFF_SCHEDULING   | false   |
      | GET_LIGHT_STATUS         | CONFIGURATION       | false   |
      | GET_LIGHT_STATUS         | MONITORING          | false   |
      | GET_LIGHT_STATUS         | METADATA_MANAGEMENT | false   |
      | GET_TARIFF_STATUS        | OWNER               | true    |
      | GET_TARIFF_STATUS        | INSTALLATION        | false   |
      | GET_TARIFF_STATUS        | AD_HOC              | true    |
      | GET_TARIFF_STATUS        | MANAGEMENT          | false   |
      | GET_TARIFF_STATUS        | FIRMWARE            | false   |
      | GET_TARIFF_STATUS        | SCHEDULING          | false   |
      | GET_TARIFF_STATUS        | TARIFF_SCHEDULING   | false   |
      | GET_TARIFF_STATUS        | CONFIGURATION       | false   |
      | GET_TARIFF_STATUS        | MONITORING          | false   |
      | GET_TARIFF_STATUS        | METADATA_MANAGEMENT | false   |
      | SET_LIGHT_SCHEDULE       | OWNER               | true    |
      | SET_LIGHT_SCHEDULE       | INSTALLATION        | false   |
      | SET_LIGHT_SCHEDULE       | AD_HOC              | false   |
      | SET_LIGHT_SCHEDULE       | MANAGEMENT          | false   |
      | SET_LIGHT_SCHEDULE       | FIRMWARE            | false   |
      | SET_LIGHT_SCHEDULE       | SCHEDULING          | true    |
      | SET_LIGHT_SCHEDULE       | TARIFF_SCHEDULING   | false   |
      | SET_LIGHT_SCHEDULE       | CONFIGURATION       | false   |
      | SET_LIGHT_SCHEDULE       | MONITORING          | false   |
      | SET_LIGHT_SCHEDULE       | METADATA_MANAGEMENT | false   |
      | SET_TARIFF_SCHEDULE      | OWNER               | true    |
      | SET_TARIFF_SCHEDULE      | INSTALLATION        | false   |
      | SET_TARIFF_SCHEDULE      | AD_HOC              | false   |
      | SET_TARIFF_SCHEDULE      | MANAGEMENT          | false   |
      | SET_TARIFF_SCHEDULE      | FIRMWARE            | false   |
      | SET_TARIFF_SCHEDULE      | SCHEDULING          | false   |
      | SET_TARIFF_SCHEDULE      | TARIFF_SCHEDULING   | true    |
      | SET_TARIFF_SCHEDULE      | CONFIGURATION       | false   |
      | SET_TARIFF_SCHEDULE      | MONITORING          | false   |
      | SET_TARIFF_SCHEDULE      | METADATA_MANAGEMENT | false   |
      | GET_POWER_USAGE_HISTORY  | OWNER               | true    |
      | GET_POWER_USAGE_HISTORY  | INSTALLATION        | false   |
      | GET_POWER_USAGE_HISTORY  | AD_HOC              | false   |
      | GET_POWER_USAGE_HISTORY  | MANAGEMENT          | false   |
      | GET_POWER_USAGE_HISTORY  | FIRMWARE            | false   |
      | GET_POWER_USAGE_HISTORY  | SCHEDULING          | false   |
      | GET_POWER_USAGE_HISTORY  | TARIFF_SCHEDULING   | false   |
      | GET_POWER_USAGE_HISTORY  | CONFIGURATION       | false   |
      | GET_POWER_USAGE_HISTORY  | MONITORING          | true    |
      | GET_POWER_USAGE_HISTORY  | METADATA_MANAGEMENT | false   |
      | RESUME_SCHEDULE          | OWNER               | true    |
      | RESUME_SCHEDULE          | INSTALLATION        | false   |
      | RESUME_SCHEDULE          | AD_HOC              | true    |
      | RESUME_SCHEDULE          | MANAGEMENT          | false   |
      | RESUME_SCHEDULE          | FIRMWARE            | false   |
      | RESUME_SCHEDULE          | SCHEDULING          | false   |
      | RESUME_SCHEDULE          | TARIFF_SCHEDULING   | false   |
      | RESUME_SCHEDULE          | CONFIGURATION       | false   |
      | RESUME_SCHEDULE          | MONITORING          | false   |
      | RESUME_SCHEDULE          | METADATA_MANAGEMENT | false   |
      | SET_TRANSITION           | OWNER               | true    |
      | SET_TRANSITION           | INSTALLATION        | false   |
      | SET_TRANSITION           | AD_HOC              | true    |
      | SET_TRANSITION           | MANAGEMENT          | false   |
      | SET_TRANSITION           | FIRMWARE            | false   |
      | SET_TRANSITION           | SCHEDULING          | false   |
      | SET_TRANSITION           | TARIFF_SCHEDULING   | false   |
      | SET_TRANSITION           | CONFIGURATION       | false   |
      | SET_TRANSITION           | MONITORING          | false   |
      | SET_TRANSITION           | METADATA_MANAGEMENT | false   |