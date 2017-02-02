Feature: FirmwareManagement GetFirmware
  As OSGP 
  I want to manage the firmware of a device
  In order to ...

  # Note: All devices return multiple firmwares. How to solve this?
  @OslpMockServer
  Scenario Outline: Update firmware
    Given an oslp device
      | DeviceIdentification    | TEST1024000000001         |
      | Status                  | Active                    |
      | Organization            | TestOrganization          |
      | IsActivated             | true                      |
      | FirmwareIdentification  | F01                       |
      | FirmwareDomainConfig    | <FirmwareDomainConfig>    |
      | FirmwarePathConfig      | <FirmwarePathConfig>      |
      | FirmwareExtensionConfig | <FirmwareExtensionConfig> |
      | FirmwareName            | <FirmwareName>            |
      | FirmwareDomain          | <FirmwareDomain>          |
      | FirmwareUrl             | <FirmwareUrl>             |
    And the device returns update firmware response "OK" over OSLP
    When receiving an update firmware request
      | DeviceIdentification | TEST1024000000001 |
    Then the update firmware async response contains
      | DeviceIdentification | TEST1024000000001 |
    And an update firmware OSLP message is sent to device "TEST1024000000001"
    And the platform buffers an update firmware response message for device "TEST1024000000001"
      | Result                  | OK                        |
      | FirmwareDomainConfig    | <FirmwareDomainConfig>    |
      | FirmwarePathConfig      | <FirmwarePathConfig>      |
      | FirmwareExtensionConfig | <FirmwareExtensionConfig> |
      | FirmwareName            | <FirmwareName>            |
      | FirmwareDomain          | <FirmwareDomain>          |
      | FirmwareUrl             | <FirmwareUrl>             |
      | FirmwareModuleType      | FUNCTIONAL                |

    Examples: 
      | FirmwareDomainConfig            | FirmwarePathConfig | FirmwareExtensionConfig | FirmwareName | FirmwareDomain           | FirmwareUrl            |
      | flexovltest.cloudapp.net        | /firmware          | hex                     | AME-v0.1     | flexovltest.cloudapp.net | /firmware/AME-v0.1.hex |
      #| flexovltest.cloudapp.net        | firmware           | hex                     | AME-v0.1     | flexovltest.cloudapp.net | /firmware/AME-v0.1.hex |
      #| flexovltest.cloudapp.net        | firmware/          | hex                     | AME-v0.1     | flexovltest.cloudapp.net | /firmware/AME-v0.1.hex |
      #| flexovltest.cloudapp.net        | /firmware/         | hex                     | AME-v0.1     | flexovltest.cloudapp.net | /firmware/AME-v0.1.hex |
      #| flexovltest.cloudapp.net        | /firmware          | .hex                    | AME-v0.1     | flexovltest.cloudapp.net | /firmware/AME-v0.1.hex |
      #| flexovltest.cloudapp.net/       | /firmware          | hex                     | AME-v0.1     | flexovltest.cloudapp.net | /firmware/AME-v0.1.hex |
      #| http://flexovltest.cloudapp.net | /firmware          | hex                     | AME-v0.1     | flexovltest.cloudapp.net | /firmware/AME-v0.1.hex |
      #| ftp://flexovltest.cloudapp.net  | /firmware          | hex                     | AME-v0.1     | flexovltest.cloudapp.net | /firmware/AME-v0.1.hex |
      #| flexovltest.cloudapp.net        |                    | hex                     | AME-v0.1     | flexovltest.cloudapp.net | /AME-v0.1.hex          |
      #| flexovltest.cloudapp.net        | /firmware          |                         | AME-v0.1     | flexovltest.cloudapp.net | /firmware/AME-v0.1     |
  #
  #Scenario: Update the firmware for an unknown device
    #When receiving an update firmware request
      #| DeviceIdentification | TEST1024000000001 |
    #Then the update firmware response contains soap fault
      #| FaultCode | SOAP-ENV:Server |
      #| Message   | UNKNOWN_DEVICE  |
#
  #Scenario: Update the firmware for an unregistered device
    #Given an oslp device
      #| DeviceIdentification | TEST1024000000001 |
      #| PublicKeyPresent     | false             |
    #When receiving an update firmware request
      #| DeviceIdentification | TEST1024000000001 |
    #Then the update firmware response contains soap fault
      #| FaultCode | SOAP-ENV:Server     |
      #| Message   | UNREGISTERED_DEVICE |
