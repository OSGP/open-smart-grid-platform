Feature: Remove organization
  As a ...
  I want to manage the Organizations in the platform
  In order ...

  # Note: Does only work if all the words 'organisation' are changed to 'organization', or it has to remain the same as it is.
  Scenario Outline: Remove An Existing Organization
    Given an organization
      | OrganizationIdentification | <OrganizationIdentification> |
      | Name                       | <Name>                       |
      | Prefix                     | <Prefix>                     |
      | PlatformFunctionGroup      | <FunctionGroup>              |
      | Enabled                    | <Enabled>                    |
      | Domains                    | <Domains>                    |
    When receiving a remove organization request
      | OrganizationIdentification | <OrganizationIdentification> |
      | Name                       | <Name>                       |
      | PlatformFunctionGroup      | <FunctionGroup>              |
    Then the remove organization response is successful
    And the organization with organization identification "<OrganizationIdentification>" should be disabled

    Examples: 
      | OrganizationIdentification | Name                | Prefix | FunctionGroup | Enabled | Domains |
      | ATestOrganization          | A Test Organization | MAA    | ADMIN         | true    | COMMON  |
      | ATestOrganization          | A Test Organization | MAA    | USER          | true    | COMMON  |

  Scenario Outline: Remove A Non Existing Organization
    When receiving a remove organization request
      | OrganizationIdentification | <OrganizationIdentification> |
      | Name                       | <Name>                       |
      | PlatformFunctionGroup      | <FunctionGroup>              |
    Then the remove organization response contains
      | Message | UNKNOWN_ORGANISATION |

    Examples: 
      | OrganizationIdentification | Name                | FunctionGroup |
      | ATestOrganization          | A Test Organization | ADMIN         |
