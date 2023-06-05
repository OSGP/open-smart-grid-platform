# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@Common @Platform @AdminDeviceManagement
Feature: AdminDeviceManagement Update Key
  As a ...
  I want to be able to perform DeviceManagement operations on a device
  In order to ...

  Scenario: Update Key For Device
    Given an ssld device
      | DeviceIdentification | TEST1024000000001 |
    When receiving an update key request
      | DeviceIdentification | TEST1024000000001 |
      | PublicKey            | abcdef123456      |
    Then the update key response contains
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Receive update key request containing invalid protocol info ID for unknown device
    When receiving an update key request
      | DeviceIdentification | TEST1024000000002 |
      | PublicKey            | abcdef123456      |
      | ProtocolInfoId       | -1                |
    Then the update key response contains soap fault
      | FaultCode    | SOAP-ENV:Server                                                       |
      | FaultString  | org.opensmartgridplatform.shared.exceptionhandling.TechnicalException |

  Scenario Outline: Update Key For Device With Invalid Public Key
    Given an ssld device
      | DeviceIdentification | TEST1024000000003 |
    When receiving an update key request
      | DeviceIdentification | TEST1024000000003 |
      | PublicKey            | <PublicKey>       |
    Then the update key response contains soap fault
      | FaultCode    | SOAP-ENV:Server                                       |
      | FaultString  | VALIDATION_ERROR                                      |
      | InnerMessage | Validation Exception, violations: Invalid public key; |

    Examples: 
      | PublicKey |
      |           |
      |        10 |

  Scenario: Disallow updating a device key if the requesting organisation is not enabled
    Given an organization
      | OrganizationIdentification | test-org |
      | Enabled                    | false    |
    When receiving an update key request
      | DeviceIdentification | TEST1024000000001 |
      | PUblicKey            |                   |
    Then the update device response contains soap fault
      | Message | DISABLED_ORGANISATION |
