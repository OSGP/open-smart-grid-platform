@PublicLighting @Platform @BasicOsgpFunctions @ProtocolFunctions
Feature: BasicOsgpFunctions Protocol functions
  I want to check the protocol version and variant

  Scenario Outline: Instances of one protocol with or without variant
    Given a protocol
      | Protocol        | <Protocol>        |
      | ProtocolVersion | <ProtocolVersion> |
      | ProtocolVariant | <ProtocolVariant> |
    Then I validate that there is only one protocol record
      | Protocol        | <Protocol>        |
      | ProtocolVersion | <ProtocolVersion> |
      | ProtocolVariant | <ProtocolVariant> |
    And I delete the protocol record
      | Protocol        | <Protocol>        |
      | ProtocolVersion | <ProtocolVersion> |
      | ProtocolVariant | <ProtocolVariant> |

    Examples: 
      | Protocol    | ProtocolVersion | ProtocolVariant |
      | test        |             1.0 |                 |
      | test        |             1.0 | test            |
