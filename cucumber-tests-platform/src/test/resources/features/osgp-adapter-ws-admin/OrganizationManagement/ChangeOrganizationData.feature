@PublicLighting @Platform @AdminOrganizationManagement
Feature: AdminOrganizationManagement Organization Updating
  As a ...
  I want to manage the Organizations in the platform
  In order ...

  Scenario Outline: Change Data Of Organization
    Given an organization
      | OrganizationIdentification | <OrganizationIdentification> |
      | Name                       | <Name>                       |
      | PlatformFunctionGroup      | <PlatformFunctionGroup>      |
      | Domains                    | <Domains>                    |
    When receiving an update organization request
      | OrganizationIdentification | <OrganizationIdentification> |
      | Name                       | <NewName>                    |
      | NewPlatformFunctionGroup   | <NewPlatformFunctionGroup>   |
      | NewDomains                 | <NewDomains>                 |
    Then the update organization response is successful
    And the organization exists
      | OrganizationIdentification | <OrganizationIdentification> |
      | Name                       | <NewName>                    |
      | PlatformFunctionGroup      | <NewPlatformFunctionGroup>   |
      | Domains                    | <NewDomains>                 |

    Examples: 
      | OrganizationIdentification | Name                | PlatformFunctionGroup | Domains | NewName        | NewPlatformFunctionGroup | NewDomains |
      | ATestOrganization          | A Test Organization | ADMIN                 | COMMON  | Different Name | USER                     | COMMON     |
      | ATestOrganization          | A Test Organization | USER                  | COMMON  | Different Name | ADMIN                    | COMMON     |

  Scenario: Change data of an organization as an unauthorized organization
    When receiving an update organization request
      | OrganizationIdentification    | ATestOrganization            |
      | NewOrganizationIdentification | NewTestOrganization          |
      | Name                          | A new test organization name |
      | NewPlatformFunctionGroup      | ADMIN                        |
      | NewDomains                    | COMMON                       |
    Then the update organization response contains
      | Message | UNKNOWN_ORGANISATION |

  Scenario: Change data of an unknown organization
    When receiving an update organization request
      | OrganizationIdentification    | ATestOrganization            |
      | NewOrganizationIdentification | NewTestOrganization          |
      | Name                          | A new test organization name |
      | NewPlatformFunctionGroup      | ADMIN                        |
      | NewDomains                    | COMMON                       |
    Then the update organization response contains
      | Message | UNKNOWN_ORGANISATION |
