Feature: Device installation
  As a grid operator
  I want to be able to perform DeviceInstallation operations on a device
  In order to ...

  Scenario Outline: Adding a device
    When receiving an add device request
      | DeviceUid               | <DeviceUid>             |
      | DeviceIdentification    | <DeviceIdentification>  |
      | Alias                   | <Alias>                 |
      | Owner                   | <Owner>                 |
      | ContainerPostalCode     | <ContainerPostalCode>   |
      | ContainerCity           | <ContainerCity>         |
      | ContainerStreet         | <ContainerStreet>       |
      | ContainerNumber         | <ContainerNumber>       |
      | ContainerMunicipality   | <ContainerMunicipality> |
      | GpsLatitude             | <GpsLatitude>           |
      | GpsLongitude            | <GpsLongitude>          |
      | Activated               | <Activated>             |
      | HasSchedule             | <HasSchedule>           |
      | PublicKeyPresent        | <PublicKeyPresent>      |
      | DeviceModelManufacturer | <Manufacturer>          |
      | DeviceModelModelCode    | <ModelCode>             |
      | DeviceModelDescription  | <Description>           |
      | DeviceModelMetered      | <Metered>               |
    Then the add device response is successfull
    And the device exists
      | DeviceIdentification       | <DeviceIdentification>  |
      | Alias                      | <Alias>                 |
      | OrganizationIdentification | <Owner>                 |
      | ContainerPostalCode        | <ContainerPostalCode>   |
      | ContainerCity              | <ContainerCity>         |
      | ContainerStreet            | <ContainerStreet>       |
      | ContainerNumber            | <ContainerNumber>       |
      | ContainerMunicipality      | <ContainerMunicipality> |
      | GpsLatitude                | <GpsLatitude>           |
      | GpsLongitude               | <GpsLongitude>          |
#      | Activated                  | <Activated>             |
      | HasSchedule                | <HasSchedule>           |
      | PublicKeyPresent           | <PublicKeyPresent>      |
#      | DeviceModel                | <ModelCode>             |

    Examples: 
      | DeviceUid  | DeviceIdentification | Alias       | Owner    | ContainerPostalCode | ContainerCity | ContainerStreet | ContainerNumber | ContainerMunicipality | GpsLatitude | GpsLongitude | Activated | HasSchedule | PublicKeyPresent | Manufacturer | ModelCode  | Description | Metered |
      | 1234567890 | TEST1024000000001    | Test device | test-org | 1234AA              | Maastricht    | Stationsstraat  |              12 |                       |           0 |            0 | true      | false       | false            | Test         | Test Model | Test        | true    |

  Scenario: Adding a device which already exists
    Given a device
      | DeviceIdentification | TEST1024000000001 |
    When receiving an add device request
      | DeviceIdentification | TEST1024000000001 |
    Then the add device response contains
      | FaultCode      | SOAP-ENV:Server                                                   |
      | FaultString    | EXISTING_DEVICE                                                   |
      | FaultType      | FunctionalFault                                                   |
      | Code           |                                                               204 |
      | Message        | EXISTING_DEVICE                                                   |
      | Component      | WS_CORE                                                           |
      | InnerException | com.alliander.osgp.domain.core.exceptions.ExistingEntityException |
      | InnerMessage   | Device with id TEST1024000000001 already exists.                  |

  #Scenario: Updating a device
  #Given a device
  #| DeviceIdentification | TEST1024000000001 |
  #| Alias                | BeforeTest        |
  #When receiving an update device request
  #| DeviceIdentification | TEST1024000000001 |
  #| Alias                | AfterTest         |
  #Then the update device response is successfull
  #And the device exists
  #| DeviceIdentification | TEST1024000000001 |
  #| Alias                | AfterTest         |
  Scenario: Updating a non existing device
    When receiving an update device request
      | DeviceIdentification | TEST1024000000001 |
    Then the update device response contains
      | FaultCode      | SOAP-ENV:Server                                                  |
      | FaultString    | UNKNOWN_DEVICE                                                   |
      | FaultType      | FunctionalFault                                                  |
      | Code           |                                                              201 |
      | Message        | UNKNOWN_DEVICE                                                   |
      | Component      | WS_CORE                                                          |
      | InnerException | com.alliander.osgp.domain.core.exceptions.UnknownEntityException |
      | InnerMessage   | Device with id "TEST1024000000001" could not be found.           |
