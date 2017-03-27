Feature: CoreDeviceInstallation Device Registration
  As a ...
  I want to be able to perform DeviceInstallation operations on a device
  In order to ...

  @OslpMockServer
  Scenario Outline: A device which is installed and performs first time registration.
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    When the device sends a register device request to the platform over "<Protocol>"
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    Then the register device response contains
      | Status | OK |

    Examples: 
      | Protocol    |
      | OSLP        |
      | OSLP ELSTER |

  @OslpMockServer
  Scenario Outline: A device which performs subsequent registration.
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    And the device sends a register device request to the platform over "<Protocol>"
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    When the device sends a register device request to the platform over "<Protocol>"
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
      | DeviceUid            | eHW0eEFzN0R2Okd5  |
      | IpAddress            | 127.0.0.2         |
      | DeviceType           | SSLD              |
    Then the register device response contains
      | Status | OK |

    Examples: 
      | Protocol    |
      | OSLP        |
      | OSLP ELSTER |

  @OslpMockServer
  Scenario Outline: Register device that already exists on the platform, without GPS metadata
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
      | gpsLatitude          |                   |
      | gpsLongitude         |                   |
    And the device sends a register device request to the platform over "<Protocol>"
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    When the device sends a register device request to the platform over "<Protocol>"
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
      | DeviceUid            | eHW0eEFzN0R2Okd5  |
      | IpAddress            | 127.0.0.2         |
      | DeviceType           | SSLD              |
    Then the register device response contains
      | Status | OK |

    Examples: 
      | Protocol    |
      | OSLP        |
      | OSLP ELSTER |

  @OslpMockServer
  Scenario Outline: Register device that does not yet exist on the platform
    When the device sends a register device request to the platform over "<Protocol>"
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    Then the device with id "TEST1024000000001" does not exist

    Examples: 
      | Protocol    |
      | OSLP        |
      | OSLP ELSTER |

  #Note: This test may sometimes fail on the Then case:
  #org.junit.ComparisonFailure: expected:<127.0.0.[3]> but was:<127.0.0.[2]>
  @OslpMockServer
  Scenario Outline: Register device with network address already in use by another device
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
      | NetworkAddress       | 127.0.0.2         |
      | DeviceType           | SSLD              |
    And an ssld oslp device
      | DeviceIdentification | TEST1024000000002 |
      | DeviceUid            | eHW0eEFzN0R2Okd5  |
      | Protocol             | <Protocol>        |
      | NetworkAddress       | 127.0.0.3         |
      | DeviceType           | SSLD              |
    When the device sends a register device request to the platform over "<Protocol>"
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
      | DeviceUid            | fIX1fFGaO1S3Ple6  |
      | IpAddress            | 127.0.0.3         |
      | DeviceType           | SSLD              |
    And the register device response contains
      | Status | OK |
    Then the IpAddress for the device "TEST1024000000001" should be "127.0.0.3"
    And the IpAddress for the device "TEST1024000000002" should be ""

    Examples: 
      | Protocol    |
      | OSLP        |
      | OSLP ELSTER |
