Feature: OrganizationManagement Finding Organizations
  As a ...  
  I want to ...  
  In order ...

  Scenario: Get all organizations
    Given an organization
      | OrganizationIdentification | test-org                                |
      | Name                       | Test Organization                       |
      | Domains                    | COMMON;PUBLIC_LIGHTING;TARIFF_SWITCHING |
      | Prefix                     | MAA                                     |
    And an organization
      | OrganizationIdentification | LianderNetManagement |
      | Name                       | An Organization      |
      | Domains                    | COMMON               |
      | Prefix                     | Tes                  |
    When receiving a get all organizations request
      | OrganizationIdentification | LianderNetManagement |
    Then the get all organizations response contains "2" organization
    And the get all organizations response contains at index "1"
      | OrganizationIdentification | test-org                                |
      | Name                       | Test Organization                       |
      | Domains                    | COMMON;PUBLIC_LIGHTING;TARIFF_SWITCHING |
      | Prefix                     | MAA                                     |
    And the get all organizations response contains at index "2"
      | OrganizationIdentification | LianderNetManagement |
      | Name                       | An Organization      |
      | Domains                    | COMMON               |
      | Prefix                     | Tes                  |

  Scenario: Get own organization
    Given an organization
      | OrganizationIdentification | test-org                                |
      | Name                       | Test Organization                       |
      | Domains                    | COMMON;PUBLIC_LIGHTING;TARIFF_SWITCHING |
      | Prefix                     | MAA                                     |
    And an organization
      | OrganizationIdentification | TestOrganization |
      | Name                       | An Organization  |
      | Domains                    | COMMON           |
      | Prefix                     | Tes              |
    When receiving a get all organizations request
      | OrganizationIdentification | test-org |
    Then the get all organizations response contains "1" organization
    And the get all organizations response contains at index "1"
      | OrganizationIdentification | test-org                                |
      | Name                       | Test Organization                       |
      | Domains                    | COMMON;PUBLIC_LIGHTING;TARIFF_SWITCHING |
      | Prefix                     | MAA                                     |

  Scenario: Get own unknown organization
    When receiving an own unknown organization request
      | OrganizationIdentification | unknown-organization |
    Then the get own unknown organization response contains soap fault
      | FaultCode    | SOAP-ENV:Server                                                 |
      | FaultString  | UNKNOWN_ORGANISATION                                            |
      | InnerMessage | Organisation with id "unknown-organization" could not be found. |
