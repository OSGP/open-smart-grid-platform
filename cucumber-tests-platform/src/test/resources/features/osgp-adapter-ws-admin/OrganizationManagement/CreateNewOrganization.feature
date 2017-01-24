Feature: OrganizationManagement Organization Creating
  As a ...
  I want to manage the Organizations in the platform
  In order ...

  Scenario Outline: Create A New Organization
    When receiving a create organization request
      | OrganizationIdentification | <OrganizationIdentification> |
      | Name                       | <Name>                       |
      | Prefix                     | MAA                          |
      | FunctionGroup              | <FunctionGroup>              |
      | Enabled                    | <Enabled>                    |
      | Domains                    | <Domains>                    |
    Then the create organization response is successful
    And the entity organization exists
      | OrganizationIdentification | <OrganizationIdentification> |
      | Name                       | <Name>                       |
      | Prefix                     | MAA                          |
      | FunctionGroup              | <FunctionGroup>              |
      | Enabled                    | <Enabled>                    |
      | Domains                    | <Domains>                    |

    Examples: 
      | OrganizationIdentification | Name                | FunctionGroup | Enabled | Domains                                 |
      | TEST1024000000001          | A Test Organization | ADMIN         | true    | COMMON                                  |
      | TEST1024000000001          | A Test Organization | ADMIN         | true    | COMMON;PUBLIC_LIGHTING;TARIFF_SWITCHING |
      | Heerlen                    | Gemeente Heerlen    | USER          |         | COMMON                                  |

  Scenario: Create An Already Existing Organization
    Given an organization
      | OrganizationIdentification | TEST1024000000001   |
      | Name                       | A Test Organization |
      | Prefix                     | MAA                 |
      | FunctionGroup              | ADMIN               |
      | Enabled                    | true                |
      | Domains                    | COMMON              |
    When receiving a create organization request
      | OrganizationIdentification | TEST1024000000001      |
      | Name                       | Different Organization |
      | Prefix                     | MAA                    |
      | FunctionGroup              | ADMIN                  |
      | Enabled                    | true                   |
      | Domains                    | COMMON                 |
    Then the create organization response contains soap fault
      | COMPONENT | WS_ADMIN                                                       |
      | MESSAGE   | com.alliander.osgp.shared.exceptionhandling.TechnicalException |
    And the organization with name "Different Organization" should not be created

  Scenario: Create An Organization As An Unauthorized Organization
    When receiving a create organization request as an unauthorized organization
      | OrganizationIdentification | TEST1024000000001 |
      | Name                       | An Organization   |
      | Prefix                     | MAA               |
      | FunctionGroup              | ADMIN             |
      | Enabled                    | true              |
      | Domains                    | COMMON            |
    Then the create organization response contains soap fault
      | Message | UNKNOWN_ORGANISATION |
    And the organization with name "An Organization" should not be created

  Scenario Outline: Creating An Organization With An Invalid Organization Identification
    When receiving a create organization request
      | OrganizationIdentification | <Invalid Organization Identification> |
      | Name                       | A Test Organization                   |
      | Prefix                     | MAA                                   |
      | FunctionGroup              | ADMIN                                 |
      | Enabled                    | true                                  |
      | Domains                    | COMMON                                |
    Then the create organization response contains soap fault
      | FaultCode        | SOAP-ENV:Client    |
      | FaultString      | Validation error   |
      | ValidationErrors | <ValidationErrors> |
    And the organization with name "A Test Organization" should not be created

    # Note: The validation errors are ; separated if there are multiple.
    Examples: 
      | Invalid Organization Identification | ValidationErrors                                                                                                                                                                                                                            |
      | A Test Organization                 | cvc-pattern-valid: Value 'A Test Organization' is not facet-valid with respect to pattern '[^ ]{0,40}' for type 'Identification'.;cvc-type.3.1.3: The value 'A Test Organization' of element 'ns2:OrganisationIdentification' is not valid. |
      |                                     | cvc-minLength-valid: Value '' with length = '0' is not facet-valid with respect to minLength '1' for type 'Identification'.;cvc-type.3.1.3: The value '' of element 'ns2:OrganisationIdentification' is not valid.                          |
