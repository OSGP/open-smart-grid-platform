Feature: Organization management
  As a ...
  I want to manage the Organizations in the platform
  In order ...

  Scenario: Change Data Of Non Existing Organization
    When receiving an update organization request
      | OrganizationIdentification | ATestOrganization |
      | Name                       | SomeData          |
    Then the update organization response contains
      | Message | UNKNOWN_ORGANISATION |
