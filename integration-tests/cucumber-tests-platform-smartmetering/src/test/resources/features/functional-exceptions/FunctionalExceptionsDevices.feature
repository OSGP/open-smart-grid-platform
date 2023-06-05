# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @NightlyBuildOnly
Feature: SmartMetering functional exceptions regarding devices

  Scenario: Get administrative status on an unknown device
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When the get administrative status request for an invalid device is received
      | DeviceIdentification | TEST1024000000011 |
    Then a SOAP fault should have been returned
      | Code    |            201 |
      | Message | UNKNOWN_DEVICE |

  Scenario: Add an already existing device to OSGP
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When receiving a smartmetering add device request
      | DeviceIdentification  | TEST1024000000001 |
      | DeviceType            | SMART_METER_E     |
      | CommunicationMethod   | GPRS              |
      | CommunicationProvider | KPN               |
      | ICC_id                |              1234 |
      | protocolName          | DSMR              |
      | protocolVersion       | 4.2.2             |
      | Supplier              | Kaifa             |
      | HLS3_active           | false             |
      | HLS4_active           | false             |
      | HLS5_active           | true              |
      | Master_key            | SECURITY_KEY_M    |
      | Authentication_key    | SECURITY_KEY_A    |
      | Encryption_key        | SECURITY_KEY_E    |
    Then the add device response for an existing device is received
      | DeviceIdentification | TEST1024G00000001 |
    And a SOAP fault should have been returned
      | Code    |             204 |
      | Message | EXISTING_DEVICE |

  Scenario: Read actual meter reads of a gas meter without channel information
    Given a dlms device
      | DeviceIdentification     | TEST1024000000001 |
      | DeviceType               | SMART_METER_E     |
      | SelectiveAccessSupported | true              |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | TEST1024000000001 |
    When the get actual meter reads gas request generating an error is received
      | DeviceIdentification | TESTG102400000001 |
    Then a SOAP fault should have been returned
      | Code    |              401 |
      | Message | VALIDATION_ERROR |

  Scenario: Retrieve a non-existing DLMS attribute
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When the get specific attribute value request generating an error is received
      | DeviceIdentification | TEST1024000000001 |
      | ClassId              |               999 |
      | ObisCodeA            |                 9 |
      | ObisCodeB            |                 9 |
      | ObisCodeC            |                 9 |
      | ObisCodeD            |                 9 |
      | ObisCodeE            |                 9 |
      | ObisCodeF            |                 9 |
      | Attribute            |                 9 |
    Then a SOAP fault should have been returned
      | Code    |                              412 |
      | Message | ERROR_RETRIEVING_ATTRIBUTE_VALUE |
