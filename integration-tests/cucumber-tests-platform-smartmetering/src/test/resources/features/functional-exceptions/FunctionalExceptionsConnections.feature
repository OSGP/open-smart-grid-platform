# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @NightlyBuildOnly
Feature: SmartMetering functional exceptions regarding connections

  Scenario: Get administrative status on a non-responsive device
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | Port                 |              9999 |
    When the get administrative status request generating an error is received
      | DeviceIdentification | TEST1024000000001 |
    Then a SOAP fault should have been returned
      | Code    |              211 |
      | Message | CONNECTION_ERROR |

  Scenario: Connect to a smart meter with an invalid ip address
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | NetworkAddress       | 192.168.1.1       |
    When the get administrative status request generating an error is received
      | DeviceIdentification | TEST1024000000001 |
    Then a SOAP fault should have been returned
      | Code    |              211 |
      | Message | CONNECTION_ERROR |

  Scenario: Connect to a smart meter without an ip address
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | NetworkAddress       |                   |
    When the get administrative status request generating an error is received
      | DeviceIdentification | TEST1024000000001 |
    Then a SOAP fault should have been returned
      | Code    |                212 |
      | Message | INVALID_IP_ADDRESS |

  Scenario: Connect to a smart meter with an invalid challenge length
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | Hls5active           | true              |
      | ChallengeLength      |                65 |
    When the get administrative status request generating an error is received
      | DeviceIdentification | TEST1024000000001 |
    Then a SOAP fault should have been returned
      | Code    |                           213 |
      | Message | CHALLENGE_LENGTH_OUT_OF_RANGE |

  Scenario: Connect to a HLS 3 smart meter
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | Hls3active           | true              |
      | Hls5active           | false             |
    When the get administrative status request generating an error is received
      | DeviceIdentification | TEST1024000000001 |
    Then a SOAP fault should have been returned
      | Code    |                               413 |
      | Message | UNSUPPORTED_COMMUNICATION_SETTING |
