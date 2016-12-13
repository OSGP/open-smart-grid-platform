Feature: SmartMetering Installation
  As a grid operator
  I want to be able to perform SmartMeteringInstallation operations on a device

  Scenario: Add a new device
    When receiving an smartmetering add device request
      | DeviceIdentification  | E0026000059790003 |
      | DeviceType            | SMART_METER_E     |
      | CommunicationMethod   | GPRS              |
      | CommunicationProvider | KPN               |
      | ICC_id                |              1234 |
      | DSMR_version          | 4.2.2             |
      | Supplier              | Kaifa             |
      | HLS3_active           | false             |
      | HLS4_active           | false             |
      | HLS5_active           | true              |
      | Master_key            | true              |
    Then the smartmetering add device response contains
      | DeviceIdentification | E0026000059790003 |
    And receiving an get add device response request
      | DeviceIdentification | E0026000059790003 |
    And the get add device request response should be ok
    And the dlms device with id "E0026000059790003" exists
