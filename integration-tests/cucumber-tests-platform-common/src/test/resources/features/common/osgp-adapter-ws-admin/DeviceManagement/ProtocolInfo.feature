@Common @Platform @AdminDeviceManagement @ProtocolInfo
Feature: Manage protocol info records

  Scenario Outline: Get protocol info
    Given a protocol
      | Protocol        | <Protocol>        |
      | ProtocolVersion | <ProtocolVersion> |
      | ProtocolVariant | <ProtocolVariant> |
    When receiving a get protocol info request
    Then the get protocol info response should be returned
      | Protocol        | <Protocol>        |
      | ProtocolVersion | <ProtocolVersion> |
      | ProtocolVariant | <ProtocolVariant> |
    And I delete the protocol record
      | Protocol        | <Protocol>        |
      | ProtocolVersion | <ProtocolVersion> |
      | ProtocolVariant | <ProtocolVariant> |

    Examples: 
      | Protocol | ProtocolVersion | ProtocolVariant |
      | test     |             1.0 | null            |
      | test     |             1.0 | test            |

  Scenario Outline: Update protocol for device
    Given a device
      | DeviceIdentification       | TEST1024000000001 |
      | OrganizationIdentification | test-org          |
    When receiving a update device protocol request
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
      | ProtocolVersion      | <ProtocolVersion> |
      | ProtocolVariant      | <ProtocolVariant> |
    Then the update device protocol response should be returned
    And the device is configured with the protocol
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
      | ProtocolVersion      | <ProtocolVersion> |
      | ProtocolVariant      | <ProtocolVariant> |

    Examples: 
      | Protocol    | ProtocolVersion | ProtocolVariant |
      | OSLP ELSTER |             1.0 | null            |
      | IEC61850    |             1.0 | null            |

  Scenario Outline: Update protocol for device fails
    Given a device
      | DeviceIdentification       | TEST1024000000001 |
      | OrganizationIdentification | test-org          |
    When receiving a update device protocol request
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
      | ProtocolVersion      | <ProtocolVersion> |
      | ProtocolVariant      | <ProtocolVariant> |
    Then the update device protocol response contains an error
      | FaultString | UNKNOWN_PROTOCOL_NAME_OR_VERSION_OR_VARIANT |

    Examples: 
      | Protocol    | ProtocolVersion | ProtocolVariant |
      | OSLP ELSTER |             1.0 | does-not-exist  |
      | IEC61850    |             1.0 | does-not-exist  |
