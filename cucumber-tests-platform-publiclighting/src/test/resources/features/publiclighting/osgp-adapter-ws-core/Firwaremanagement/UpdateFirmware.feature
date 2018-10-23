@Publiclighting @Platform @FirmwareManagement
Feature: FirmwareManagement update firmware
  As OSGP 
  I want to manage the firmware of a device
  In order to ...

  @OslpMockServer
  Scenario Outline: Update firmware
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Status               | Active            |
      | Organization         | TestOrganization  |
      | Protocol             | <Protocol>        |
    And the device returns a update firmware "OK" over "<Protocol>"
    When receiving an update firmware request
      | DeviceIdentification   | TEST1024000000001 |
      | FirmwareIdentification | F01               |
    Then the update firmware async response contains
      | DeviceIdentification | TEST1024000000001 |
    And an update firmware "<Protocol>" message is sent to device "TEST1024000000001"
      | FirmwareDomain | 127.0.0.1     |
      | FirmwareUrl    | /firmware/F01 |
    And the platform buffers a set reboot response message for device "TEST1024000000001"
      | Result | OK |

    Examples: 
      | Protocol    |
      | OSLP ELSTER |

  Scenario: Update the firmware for an unknown firmware
    When receiving an update firmware request
      | DeviceIdentification | unknown |
    Then the update firmware response contains soap fault
      | FaultCode | SOAP-ENV:Server |
      | Message   | UNKNOWN_DEVICE  |
