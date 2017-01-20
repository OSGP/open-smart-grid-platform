Feature: OrganizationManagement Organization Removal
  As a ...
  I want to manage the Organizations in the platform
  In order ...

  # Note: Does only work if all the words 'organisation' are changed to 'organization', or it has to remain the same as it is.
  Scenario Outline: Remove An Existing Organization
    Given an organization
      | OrganizationIdentification | Test1024000000001 |
      | Name                       | A Test Organization                       |
      | Prefix                     | MAA                     |
      | PlatformFunctionGroup      | <FunctionGroup>              |
      | Enabled                    | true                    |
      | Domains                    | COMMON                    |
    When receiving a remove organization request
      | OrganizationIdentification | Test1024000000001 |
      | Name                       | A Test Organization                       |
      | PlatformFunctionGroup      | <FunctionGroup>              |
    Then the remove organization response is successful
    And the organization with organization identification "TEST1024000000001" should be disabled

    Examples:
      | FunctionGroup |
      | ADMIN         |
      | USER          |

  Scenario: Remove A Non Existing Organization
    When receiving a remove organization request
      | OrganizationIdentification | TEST1024000000001 |
      | Name                       | A Test Organization                       |
      | PlatformFunctionGroup      | ADMIN              |
    Then the remove organization response contains
      | Message | UNKNOWN_ORGANISATION |
