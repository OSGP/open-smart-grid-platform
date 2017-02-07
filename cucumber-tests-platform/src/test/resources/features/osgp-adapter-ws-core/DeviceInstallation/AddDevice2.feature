Feature: CoreDeviceInstallation Device Creating
  As a ...
  I want to be able to perform DeviceInstallation operations on a device
  In order to ...

  Scenario: Add New Device With Empty Owner Organization
    Given a device model
      | ModelCode | Test Model |
      | Metered   | true       |
    When receiving an add device request with an empty organization
      | DeviceIdentification       | TEST1024000000001 |
      | Owner                      |                   |
    Then the add device response is successful
    #Then the add device response contains soap fault
      #| FaultCode      | SOAP-ENV:Server |
      #| FaultString    |  |
      #| InnerException |  |
      #| InnerMessage   |  |

    