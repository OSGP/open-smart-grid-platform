@PublicLighting @Platform @BasicOsgpFunctions
Feature: BasicOsgpFunctions Protocol functions
  I want to check the protocol version and variant 

  Scenario Outline: Instances of one protocol with/without version and variant
    Given a protocol
      | Protocol             | <Protocol>        |
      | ProtocolVersion			 | <ProtocolVersion> |
      | ProtocolVariant			 | <ProtocolVariant> |
    Then I validate that there is only one protocol record
      | Protocol             | <Protocol>        |
      | ProtocolVersion			 | <ProtocolVersion> |
      | ProtocolVariant			 | <ProtocolVariant> |
    
    Examples:
    	| Protocol 		| ProtocolVersion | ProtocolVariant |
    	| OSLP ELSTER | 1.0					    |                 |
    	| OSLP ELSTER | 1.0					    | test            |