@PublicLighting @Platform @AdminOrganizationManagement
Feature: AdminOrganizationManagement Organization Activation
  As a ...
  I want to manage the Organizations in the platform
  In order ...

  Scenario: Activate an organization
    Given an organization
      | OrganizationIdentification | AnOrganization |
      | Enabled                    | false          |
    When receiving an activate organization request
      | OrganizationIdentification | AnOrganization |
    Then the activate organization response is successful
    And the organization with organization identification "AnOrganization" should be enabled
