Feature: BasicOsgpFunctions Authorizing Platform Functions
  As a ...
  I want to ...
  In order to ...

  Scenario Outline: Call a platform function and verify whether this is allowed
    Given an organization
      | OrganizationIdentification | org-test                |
      | PlatformFunctionGroup      | <PlatformFunctionGroup> |
    When receiving a platform function request
      | DeviceIdentification       | TEST1024000000001      |
      | OrganizationIdentification | org-test               |
      | OrganisationFunction       | <OrganisationFunction> |
    Then the platform function response is "<Allowed>"

    Examples: 
      | OrganisationFunction | PlatformFunctionGroup | Allowed |
      | CREATE_ORGANISATION  | ADMIN                 | true    |
      | CREATE_ORGANISATION  | USER                  | false   |
      | GET_ORGANISATIONS    | ADMIN                 | true    |
      | GET_ORGANISATIONS    | USER                  | true    |
      | GET_MESSAGES         | ADMIN                 | true    |
      | GET_MESSAGES         | USER                  | false   |
      | GET_DEVICE_NO_OWNER  | ADMIN                 | true    |
      | GET_DEVICE_NO_OWNER  | USER                  | false   |
      | SET_OWNER            | ADMIN                 | true    |
      | SET_OWNER            | USER                  | false   |
      | UPDATE_KEY           | ADMIN                 | true    |
      | UPDATE_KEY           | USER                  | false   |
      | REVOKE_KEY           | ADMIN                 | true    |
      | REVOKE_KEY           | USER                  | false   |
