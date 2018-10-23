@Common @Platform @CoreOrganizationManagement
Feature: CoreOrganizationManagement Finding Organizations
  As a ...
  I want to ...
  In order ...

  Scenario: Get an organization by net management organization
    Given an organization
      | OrganizationIdentification | test-org                                |
      | Name                       | Test Organization                       |
      | Domains                    | COMMON;PUBLIC_LIGHTING;TARIFF_SWITCHING |
      | Prefix                     | MAA                                     |
    And an organization
      | OrganizationIdentification | LianderNetManagement |
      | Name                       | An Organization      |
      | Domains                    | COMMON               |
      | Prefix                     | LIA                  |
    When receiving a get organization request
      | OrganizationIdentification       | LianderNetManagement |
      | OrganizationIdentificationToFind | test-org             |
    Then the get organization response contains 1 organization
    And the get organization response contains
      | OrganizationIdentification | test-org                                |
      | Name                       | Test Organization                       |
      | Domains                    | COMMON;PUBLIC_LIGHTING;TARIFF_SWITCHING |
      | Prefix                     | MAA                                     |

  Scenario: Get an organization by municipality organization
    Given an organization
      | OrganizationIdentification | test-org                                |
      | Name                       | Test Organization                       |
      | Domains                    | COMMON;PUBLIC_LIGHTING;TARIFF_SWITCHING |
      | Prefix                     | MAA                                     |
    And an organization
      | OrganizationIdentification | LianderNetManagement |
      | Name                       | An Organization      |
      | Domains                    | COMMON               |
      | Prefix                     | LIA                  |
    When receiving a get organization request
      | OrganizationIdentification       | test-org |
      | OrganizationIdentificationToFind | test-org |
    Then the get organization response contains 1 organizations
    And the get organization response contains
      | OrganizationIdentification | test-org                                |
      | Name                       | Test Organization                       |
      | Domains                    | COMMON;PUBLIC_LIGHTING;TARIFF_SWITCHING |
      | Prefix                     | MAA                                     |

  Scenario: Try to get a non existent organization
    Given an organization
      | OrganizationIdentification | LianderNetManagement |
      | Name                       | An Organization      |
      | Domains                    | COMMON               |
      | Prefix                     | LIA                  |
    When receiving a get organization request
      | OrganizationIdentification       | LianderNetManagement |
      | OrganizationIdentificationToFind | non-existent-org     |
    Then the get organization response contains 0 organizations

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
    Then the get all organizations response contains 2 organizations
    And the get all organizations response contains
      | OrganizationIdentification | test-org                                |
      | Name                       | Test Organization                       |
      | Domains                    | COMMON;PUBLIC_LIGHTING;TARIFF_SWITCHING |
      | Prefix                     | MAA                                     |
    And the get all organizations response contains
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
    Then the get all organizations response contains 1 organization
    And the get all organizations response contains
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
