# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@Common @Platform @BasicOsgpFunctions
Feature: BasicOsgpFunctions Authorizing Platform Functions
  As a ...
  I want to ...
  In order to ...

  Scenario Outline: Call a platform function and verify whether this is allowed
    Given an organization
      | OrganizationIdentification | test-org                |
      | PlatformFunctionGroup      | <PlatformFunctionGroup> |
    When receiving a platform function request
      | DeviceIdentification       | TEST1024000000001      |
      | OrganizationIdentification | test-org               |
      | PlatformFunction           | <PlatformFunction>     |
    Then the platform function response is "<Allowed>"

    Examples: 
      | PlatformFunction       | PlatformFunctionGroup | Allowed |
      | CREATE_ORGANISATION    | ADMIN                 | true    |
      | CREATE_ORGANISATION    | USER                  | false   |
      | REMOVE_ORGANISATION    | ADMIN                 | true    |
      | REMOVE_ORGANISATION    | USER                  | false   |
      | CHANGE_ORGANISATION    | ADMIN                 | true    |
      | CHANGE_ORGANISATION    | USER                  | false   |
      | GET_ORGANISATIONS      | ADMIN                 | true    |
      | GET_ORGANISATIONS      | USER                  | true    |
      | GET_MESSAGES           | ADMIN                 | true    |
      | GET_MESSAGES           | USER                  | true    |
      | GET_DEVICE_NO_OWNER    | ADMIN                 | true    |
      | GET_DEVICE_NO_OWNER    | USER                  | false   |
      | FIND_DEVICES           | ADMIN                 | true    |
      | FIND_DEVICES           | USER                  | true    |
      | SET_OWNER              | ADMIN                 | true    |
      | SET_OWNER              | USER                  | false   |
      | UPDATE_KEY             | ADMIN                 | true    |
      | UPDATE_KEY             | USER                  | false   |
      | REVOKE_KEY             | ADMIN                 | true    |
      | REVOKE_KEY             | USER                  | false   |
      | FIND_SCHEDULED_TASKS   | ADMIN                 | true    |
      | FIND_SCHEDULED_TASKS   | USER                  | false   |
      | CREATE_MANUFACTURER    | ADMIN                 | true    |
      | CREATE_MANUFACTURER    | USER                  | false   |
      | REMOVE_MANUFACTURER    | ADMIN                 | true    |
      | REMOVE_MANUFACTURER    | USER                  | false   |
      | CHANGE_MANUFACTURER    | ADMIN                 | true    |
      | CHANGE_MANUFACTURER    | USER                  | false   |
      | GET_MANUFACTURERS      | ADMIN                 | true    |
      | GET_MANUFACTURERS      | USER                  | false   |
      | GET_PROTOCOL_INFOS     | ADMIN                 | true    |
      | GET_PROTOCOL_INFOS     | USER                  | false   |
      | UPDATE_DEVICE_PROTOCOL | ADMIN                 | true    |
      | UPDATE_DEVICE_PROTOCOL | USER                  | false   |
      | GET_DEVICE_MODELS      | ADMIN                 | true    |
      | GET_DEVICE_MODELS      | USER                  | true    |
      | CREATE_DEVICE_MODEL    | ADMIN                 | true    |
      | CREATE_DEVICE_MODEL    | USER                  | false   |
      | REMOVE_DEVICE_MODEL    | ADMIN                 | true    |
      | REMOVE_DEVICE_MODEL    | USER                  | false   |
      | CHANGE_DEVICE_MODEL    | ADMIN                 | true    |
      | CHANGE_DEVICE_MODEL    | USER                  | false   |
      | GET_FIRMWARE           | ADMIN                 | true    |
      | GET_FIRMWARE           | USER                  | true    |
      | CREATE_FIRMWARE        | ADMIN                 | true    |
      | CREATE_FIRMWARE        | USER                  | false   |
      | CHANGE_FIRMWARE        | ADMIN                 | true    |
      | CHANGE_FIRMWARE        | USER                  | false   |
      | REMOVE_FIRMWARE        | ADMIN                 | true    |
      | REMOVE_FIRMWARE        | USER                  | false   |
