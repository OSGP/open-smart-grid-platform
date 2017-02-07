Feature: OrganizationManagement Finding Organizations
  As a ...
  I want to ...
  In order ...

  Scenario: Get all organizations
    When receiving a get all organizations request
    Then the get all organizations response contains "1" organization
    And the get all organizations response contains at index "1"
      | OrganizationIdentification | test-org                                 |
      | OrganizationName           | Test Organization                        |
      | Domains                    | COMMON;PUBLIC_LIGHTING;TARIFF_SWITCHING; |
      | Prefix                     | MAA                                      |
