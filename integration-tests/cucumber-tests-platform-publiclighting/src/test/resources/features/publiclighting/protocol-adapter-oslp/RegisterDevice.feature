# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@PublicLighting @Platform @OslpAdapter
Feature: ProtocolAdapterOSLP Device Registration
  As a ...
  I want to be able to perform DeviceInstallation operations on a device
  In order to ...

  @OslpMockServer
  Scenario Outline: A device which is installed and performs first time registration.
    Given an ssld oslp device
      | DeviceIdentification | TESTDEVICE0000001 |
      | Protocol             | <Protocol>        |
    When the device sends a register device request to the platform over "<Protocol>"
      | DeviceIdentification | TESTDEVICE0000001 |
      | Protocol             | <Protocol>        |
      | DeviceUid            | eHW0eEFzN0R2Okd5  |
      | IpAddress            | 127.0.0.2         |
      | DeviceType           | SSLD              |
      | HasSchedule          | false             |
    Then the register device response contains
      | Status | OK |
    And the ssld oslp device contains
      | DeviceIdentification | TESTDEVICE0000001 |
      | DeviceType           | SSLD              |
      | DeviceUid            | eHW0eEFzN0R2Okd5  |
      | HasSchedule          | false             |
      | IpAddress            | 127.0.0.2         |

    Examples: 
      | Protocol    |
      | OSLP ELSTER |

  @OslpMockServer
  Scenario Outline: A device which performs subsequent registration.
    Given an ssld oslp device
      | DeviceIdentification | TESTDEVICE0000001 |
      | DeviceUid            | fIX1fFGaO1S3Ple6  |
      | Protocol             | <Protocol>        |
      | DeviceType           | SSLD              |
    And the device sends a register device request to the platform over "<Protocol>"
      | DeviceIdentification | TESTDEVICE0000001 |
      | DeviceUid            | fIX1fFGaO1S3Ple6  |
      | Protocol             | <Protocol>        |
      | DeviceType           | SSLD              |
    When the device sends a register device request to the platform over "<Protocol>"
      | DeviceIdentification | TESTDEVICE0000001 |
      | Protocol             | <Protocol>        |
      | DeviceUid            | eHW0eEFzN0R2Okd5  |
      | IpAddress            | 127.0.0.2         |
      | DeviceType           | SSLD              |
    Then the register device response contains
      | Status | OK |
    And the ssld oslp device contains
      | DeviceIdentification | TESTDEVICE0000001 |
      | DeviceType           | SSLD              |
      | DeviceUid            | eHW0eEFzN0R2Okd5  |
      | HasSchedule          | false             |
      | IpAddress            | 127.0.0.2         |

    Examples: 
      | Protocol    |
      | OSLP ELSTER |

  @OslpMockServer
  Scenario Outline: Register device that already exists on the platform, without GPS metadata
    Given an ssld oslp device
      | DeviceIdentification | TESTDEVICE0000001 |
      | DeviceUid            | fIX1fFGaO1S3Ple6  |
      | Protocol             | <Protocol>        |
      | gpsLatitude          |                   |
      | gpsLongitude         |                   |
    And the device sends a register device request to the platform over "<Protocol>"
      | DeviceIdentification | TESTDEVICE0000001 |
      | DeviceUid            | fIX1fFGaO1S3Ple6  |
      | Protocol             | <Protocol>        |
    When the device sends a register device request to the platform over "<Protocol>"
      | DeviceIdentification | TESTDEVICE0000001 |
      | DeviceUid            | eHW0eEFzN0R2Okd5  |
      | Protocol             | <Protocol>        |
      | IpAddress            | 127.0.0.2         |
      | DeviceType           | SSLD              |
    Then the register device response contains
      | Status | OK |
    And the ssld oslp device contains
      | DeviceIdentification | TESTDEVICE0000001 |
      | DeviceType           | SSLD              |
      | DeviceUid            | eHW0eEFzN0R2Okd5  |
      | HasSchedule          | false             |
      | IpAddress            | 127.0.0.2         |

    Examples: 
      | Protocol    |
      | OSLP ELSTER |

  @OslpMockServer
  Scenario Outline: Register device with IpAddress already in use by another device
    Given an ssld oslp device
      | DeviceIdentification | TESTDEVICE0000002 |
      | DeviceUid            | eHW0eEFzN0R2Okd5  |
      | Protocol             | <Protocol>        |
      | IpAddress            | 127.0.0.3         |
      | DeviceType           | SSLD              |
    And an ssld oslp device
      | DeviceIdentification | TESTDEVICE0000003 |
      | DeviceUid            | sdfhDFDFLS34FDLSd |
      | IpAddress            | 127.0.0.5         |
      | Protocol             | <Protocol>        |
      | DeviceType           | SSLD              |
    When the device sends a register device request to the platform over "<Protocol>"
      | DeviceIdentification | TESTDEVICE0000003 |
      | DeviceUid            | fIX1fFGaO1S3Ple6  |
      | Protocol             | <Protocol>        |
      | IpAddress            | 127.0.0.3         |
      | DeviceType           | SSLD              |
    Then the register device response contains
      | Status | OK |
    And the ssld oslp device contains
      | DeviceIdentification | TESTDEVICE0000003 |
      | DeviceUid            | fIX1fFGaO1S3Ple6  |
      | Protocol             | <Protocol>        |
      | IpAddress            | 127.0.0.3         |
      | DeviceType           | SSLD              |
    And the ssld oslp device contains
      | DeviceIdentification | TESTDEVICE0000002 |
      | DeviceUid            | eHW0eEFzN0R2Okd5  |
      | Protocol             | <Protocol>        |
      | IpAddress            | null              |
      | DeviceType           | SSLD              |

    Examples: 
      | Protocol    |
      | OSLP ELSTER |

  @OslpMockServer
  Scenario Outline: Register device with empty DeviceIdentification
    When the device sends a register device request to the platform over "<Protocol>"
      | DeviceIdentification |  |
    Then the register device response contains
      | Message | Failed to receive response within timelimit 20000 ms |

    Examples: 
      | Protocol    |
      | OSLP ELSTER |

  @OslpMockServer
  Scenario Outline: Register device that does not yet exist on the platform
    When the device sends a register device request to the platform over "<Protocol>"
      | DeviceIdentification | TESTDEVICE0000001 |
      | Protocol             | <Protocol>        |
    And the register device response contains
      | Message | Failed to receive response within timelimit 20000 ms |
    Then the device with id "TESTDEVICE0000001" does not exist

    Examples: 
      | Protocol    |
      | OSLP ELSTER |

  @OslpMockServer
  Scenario Outline: Register a device but register device request for another deviceUid
    Given an ssld oslp device
      | DeviceIdentification | TESTDEVICE0000001 |
      | Protocol             | <Protocol>        |
      | DeviceUid            | eHW0eEFzN0R2Okd5  |
    When the device sends a register device request to the platform over "<Protocol>"
      | DeviceUid | fIX1fFGaO1S3Ple6 |
    Then the register device response contains
      | Message | Failed to receive response within timelimit 20000 ms |

    Examples: 
      | Protocol    |
      | OSLP ELSTER |

  @OslpMockServer
  Scenario Outline: Register a device but register device request for an empty deviceUid
    Given an ssld oslp device
      | DeviceIdentification | TESTDEVICE0000001 |
      | Protocol             | <Protocol>        |
      | DeviceUid            | eHW0eEFzN0R2Okd5  |
    When the device sends a register device request to the platform over "<Protocol>"
      | DeviceUid |  |
    Then the register device response contains
      | Message | ManufacturerId + DeviceId is not of expected Length: 12 |

    Examples: 
      | Protocol    |
      | OSLP ELSTER |
