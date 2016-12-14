Feature: Organisation management
  As a ...
  I want to manage the Organisations in the platform
  In order ...

  Scenario Outline: Create a new Organisation
    When receiving a create organization request
      | OrganizationIdentification | <OrganizationIdentification> |
      | Name                       | <Name>                       |
      | Prefix                     | <Prefix>                     |
      | FunctionGroup              | <FunctionGroup>              |
      | Enabled                    | <Enabled>                    |
      | Domains                    | <Domains>                    |
    Then the create organization response is successfull
    And the entity organization exists
      | OrganizationIdentification | <OrganizationIdentification> |
      | Name                       | <Name>                       |
      | Prefix                     | <Prefix>                     |
      | FunctionGroup              | <FunctionGroup>              |
      | Enabled                    | <Enabled>                    |
      | Domains                    | <Domains>                    |
      
    Examples:
    	| OrganizationIdentification | Name                | Prefix | FunctionGroup | Enabled | Domains |
    	| ATestOrganization          | A Test Organization | MAA    | ADMIN         | true    | COMMON  |

  Scenario Outline: Create an already existing Organisation
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
      | FaultCode      | SOAP-ENV:Server                                                                                                               |
      | FaultString    | com.alliander.osgp.shared.exceptionhandling.TechnicalException                                                                |
      | FaultType      | TechnicalFault                                                                                                                |
      | Component      | WS_ADMIN                                                                                                                      |
      | InnerException | org.springframework.transaction.TransactionSystemException                                                                    |
      | InnerMessage   | Could not commit JPA transaction; nested exception is javax.persistence.RollbackException: Transaction marked as rollbackOnly |
    And the organization with name "Different Organisation" should not be created

		Examples:
    	| OrganizationIdentification | Original Name       | Prefix | FunctionGroup | Enabled | Domains | Different Name         |
    	| ATestOrganization          | A Test Organization | MAA    | ADMIN         | true    | COMMON  | Different Organisation |

  # For this a special test is created in the SoapUI project, which uses the Unknown Organisation as an OrganizationIdentification in the header.
  Scenario Outline: Create an Organisation as an unauthorized Organisation
    When receiving a create organization request as an unauthorized organization
      | OrganizationIdentification | <OrganizationIdentification> |
      | Name                       | <Name>                       |
      | Prefix                     | <Prefix>                     |
      | FunctionGroup              | <FunctionGroup>              |
      | Enabled                    | <Enabled>                    |
      | Domains                    | <Domains>                    |
    Then the create organization response contains
      | FaultCode      | SOAP-ENV:Server                                                  |
      | FaultString    | UNKNOWN_ORGANISATION                                             |
      | FaultType      | FunctionalFault                                                  |
      | Code           |                                                              101 |
      | Message        | UNKNOWN_ORGANISATION                                             |
      | Component      | WS_ADMIN                                                         |
      | InnerException | com.alliander.osgp.domain.core.exceptions.UnknownEntityException |
      | InnerMessage   | Organisation with id "unknown-organization" could not be found.  |
    And the organization with name "<Name>" should not be created
    
		Examples:
    	| OrganizationIdentification | Name            | Prefix | FunctionGroup | Enabled | Domains |
    	| ATestOrganization          | An Organisation | MAA    | ADMIN         | true    | COMMON  |

  Scenario Outline: Creating an Organisation with an invalid Organisation identification
    When receiving a create organization request
      | OrganizationIdentification | <Invalid Organisation Identification> |
      | Name                       | <Name>                                |
      | Prefix                     | <Prefix>                              |
      | FunctionGroup              | <FunctionGroup>                       |
      | Enabled                    | <Enabled>                             |
      | Domains                    | <Domains>                             |
    Then the create organization response contains
      | FaultCode        | <FaultCode>         |
      | FaultString      | <FaultString>       |
      | FaultType        | <FaultType>         |
      | ValidationErrors | <Validation Errors> |
    And the organization with name "<Name>" should not be created

    # Note: The validation errors are ; separated if there are multiple.
    Examples: 
      | Invalid Organisation Identification | Name                | Prefix | FunctionGroup | Enabled | Domains | FaultCode       | FaultString      | FaultType       | Validation Errors                                                                                                                                                                                                                           |
      | A Test Organization                 | A Test Organization | MAA    | ADMIN         | true    | COMMON  | SOAP-ENV:Client | Validation error | ValidationError | cvc-pattern-valid: Value 'A Test Organization' is not facet-valid with respect to pattern '[^ ]{0,40}' for type 'Identification'.;cvc-type.3.1.3: The value 'A Test Organization' of element 'ns1:OrganizationIdentification' is not valid. |
      |                                     | A Test Organization | MAA    | ADMIN         | true    | COMMON  | SOAP-ENV:Client | Validation error | ValidationError | cvc-minLength-valid: Value '' with length = '0' is not facet-valid with respect to minLength '1' for type 'Identification'.;cvc-type.3.1.3: The value '' of element 'ns1:OrganizationIdentification' is not valid.                          |

### Converted Fitnesse tests to Cucumber ###
	# ChangeOrganisationData scenario's
	Scenario Outline: Change Data Of Organisation
		Given an organization
			| OrganizationIdentification | <OrganizationIdentification> |
			| Name                       | <OldName>                    |
			| FunctionGroup              | <FunctionGroup>              |
			| Domains                    | <Domains>                    |
		When receiving an update organization request
			| OrganizationIdentification | <OrganizationIdentification> |
			| Name                       | <NewName>                    |
			| FunctionGroup              | <FunctionGroup>              |
			| Domains                    | <Domains>                    |
		Then the update organization response is successfull
		And the update organization response contains
			| OrganizationIdentification | <OrganizationIdentification> |
			| Name                       | <NewName>                    |
			| FunctionGroup              | <FunctionGroup>              |
			| Domains                    | <Domains>                    |

		Examples:
			| OrganizationIdentification | OldName             | NewName                       | FunctionGroup | Domains |
			| ATestOrganization          | A Test Organization | A Different Test Organisation | ADMIN         | COMMON  |

### Already defined tests
  Scenario Outline: Remove an existing Organisation
    Given an organization
      | OrganizationIdentification | <OrganizationIdentification>   |
      | Name                       | <Name>                         |
      | Prefix                     | <Prefix>                       |
      | FunctionGroup              | <FunctionGroup>                |
      | Enabled                    | <Enabled>                      |
      | Domains                    | <Domains>                      |
    When receiving a remove organization request
      | OrganizationIdentification | <OrganizationIdentification>   |
      | Name                       | <Name>                         |
      | FunctionGroup              | <FunctionGroup>                |
    Then the remove organization response is successfull
    And the organization with organization identification "<OrganizationIdentification>" should be disabled
    
    Examples:
    	| OrganizationIdentification | Name                | Prefix | FunctionGroup | Enabled | Domains |
    	| ATestOrganization          | A Test Organization | MAA    | ADMIN         | true    | COMMON  |

  Scenario Outline: Remove a non existing Organisation
    When receiving a remove organization request
      | OrganizationIdentification | <OrganizationIdentification> |
      | Name                       | <Name>                       |
      | FunctionGroup              | <FunctionGroup>              |
    Then the remove organization response contains
      | FaultCode      | SOAP-ENV:Server                                                  |
      | FaultString    | UNKNOWN_ORGANISATION                                             |
      | FaultType      | FunctionalFault                                                  |
      | Code           |                                                              101 |
      | Message        | UNKNOWN_ORGANISATION                                             |
      | Component      | WS_ADMIN                                                         |
      | InnerException | com.alliander.osgp.domain.core.exceptions.UnknownEntityException |
      | InnerMessage   | Organisation with id "<OrganizationIdentification>" could not be found.     |

    Examples:
     	| OrganizationIdentification | Name                | FunctionGroup |
     	| ATestOrganization          | A Test Organization | ADMIN         |
     	
### Converted Fitnesse tests to Cucumber ###    
  # SetEventNotification scenario's
	@OslpMockServer
	Scenario Outline: Set Event Notifications
		Given an oslp device
			| DeviceIdentification | <DeviceIdentification> |
		And the device returns a set event notification "<Result>" over OSLP
    When receiving a set event notification message request on OSGP
    	| Event                | <Event>                |
    	| DeviceIdentification | <DeviceIdentification> |
    Then the set event notification async response contains
      | DeviceIdentification | <DeviceIdentification> |
    And a set event notification OSLP message is sent to device "<DeviceIdentification>"
		And the platform buffers a set event notification response message for device "<DeviceIdentification>"
    
    Examples:
    	| DeviceIdentification | Result | Event                         |
      | TEST1024000000001    | OK     | LIGHT_EVENTS, SECURITY_EVENTS |
      | TEST1024000000001    | OK     | SECURITY_EVENTS               |