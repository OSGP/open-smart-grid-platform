@SmartMetering @Platform
Feature: SmartMetering functional exceptions regarding organisations

  Scenario: Get administrative status on a device of an unknown organisation
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When the get administrative status request for an invalid organisation is received
      | DeviceIdentification       | TEST1024000000001    |
      | OrganizationIdentification | LianderNetManagement |
    Then a SOAP fault should have been returned
      | Code    |                  101 |
      | Message | UNKNOWN_ORGANISATION |
