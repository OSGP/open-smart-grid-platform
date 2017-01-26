Feature: AdminDeviceManagement Create Organization
  As a ...
  I want to manage the Organizations in the platform
  In order ...

  Scenario Outline: Create A New Organization
    When receiving a create organization request
      | OrganizationIdentification | <OrganizationIdentification> |
      | Name                       | <Name>                       |
      | Prefix                     | <Prefix>                     |
      | FunctionGroup              | <FunctionGroup>              |
      | Enabled                    | <Enabled>                    |
      | Domains                    | <Domains>                    |
    Then the create organization response is successful
    And the entity organization exists
      | OrganizationIdentification | <OrganizationIdentification> |
      | Name                       | <Name>                       |
      | Prefix                     | <Prefix>                     |
      | FunctionGroup              | <FunctionGroup>              |
      | Enabled                    | <Enabled>                    |
      | Domains                    | <Domains>                    |

    Examples: 
      | OrganizationIdentification | Name                | Prefix | FunctionGroup | Enabled | Domains                                 |
      | ATestOrganization          | A Test Organization | MAA    | ADMIN         | true    | COMMON                                  |
      | ATestOrganization          | A Test Organization | MAA    | ADMIN         | true    | COMMON;PUBLIC_LIGHTING;TARIFF_SWITCHING |
      | Heerlen                    | Gemeente Heerlen    | MAA    | USER          |         | COMMON                                  |

  Scenario Outline: Create An Already Existing Organization
    Given an organization
      | OrganizationIdentification | <OrganizationIdentification> |
      | Name                       | <Original Name>              |
      | Prefix                     | <Prefix>                     |
      | FunctionGroup              | <FunctionGroup>              |
      | Enabled                    | <Enabled>                    |
      | Domains                    | <Domains>                    |
    When receiving a create organization request
      | OrganizationIdentification | <OrganizationIdentification> |
      | Name                       | <Different Name>             |
      | Prefix                     | <Prefix>                     |
      | FunctionGroup              | <FunctionGroup>              |
      | Enabled                    | <Enabled>                    |
      | Domains                    | <Domains>                    |
    Then the create organization response contains
      | COMPONENT | WS_ADMIN                                                       |
      | MESSAGE   | com.alliander.osgp.shared.exceptionhandling.TechnicalException |
    And the organization with name "Different Organisation" should not be created

    Examples: 
      | OrganizationIdentification | Original Name       | Prefix | FunctionGroup | Enabled | Domains | Different Name         |
      | ATestOrganization          | A Test Organization | MAA    | ADMIN         | true    | COMMON  | Different Organization |

  Scenario Outline: Create An Organization As An Unauthorized Organization
    When receiving a create organization request as an unauthorized organization
      | OrganizationIdentification | <OrganizationIdentification> |
      | Name                       | <Name>                       |
      | Prefix                     | <Prefix>                     |
      | FunctionGroup              | <FunctionGroup>              |
      | Enabled                    | <Enabled>                    |
      | Domains                    | <Domains>                    |
    Then the create organization response contains
      | Message | UNKNOWN_ORGANISATION |
    And the organization with name "<Name>" should not be created

    Examples: 
      | OrganizationIdentification | Name            | Prefix | FunctionGroup | Enabled | Domains |
      | ATestOrganization          | An Organization | MAA    | ADMIN         | true    | COMMON  |

  Scenario Outline: Creating An Organization With An Invalid Organization Identification
    When receiving a create organization request
      | OrganizationIdentification | <Invalid Organization Identification> |
      | Name                       | <Name>                                |
      | Prefix                     | <Prefix>                              |
      | FunctionGroup              | <FunctionGroup>                       |
      | Enabled                    | <Enabled>                             |
      | Domains                    | <Domains>                             |
    Then the create organization response contains
      | MESSAGE | Validation error |
    And the organization with name "<Name>" should not be created

    # Note: The validation errors are ; separated if there are multiple.
    Examples: 
      | Invalid Organization Identification | Name                | Prefix | FunctionGroup | Enabled | Domains | FaultCode       | FaultString      | FaultType       | Validation Errors                                                                                                                                                                                                                           |
      | A Test Organization                 | A Test Organization | MAA    | ADMIN         | true    | COMMON  | SOAP-ENV:Client | Validation error | ValidationError | cvc-pattern-valid: Value 'A Test Organization' is not facet-valid with respect to pattern '[^ ]{0,40}' for type 'Identification'.;cvc-type.3.1.3: The value 'A Test Organization' of element 'ns1:OrganizationIdentification' is not valid. |
      |                                     | A Test Organization | MAA    | ADMIN         | true    | COMMON  | SOAP-ENV:Client | Validation error | ValidationError | cvc-minLength-valid: Value '' with length = '0' is not facet-valid with respect to minLength '1' for type 'Identification'.;cvc-type.3.1.3: The value '' of element 'ns1:OrganizationIdentification' is not valid.                          |
