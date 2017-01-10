Feature: Add Device
  As a ...
  I want to ...
  In order to ...

  Scenario Outline: Add New Device
    Given a device model
      | ModelCode | <ModelCode> |
      | Metered   | <Metered>   |
    When receiving an add device request
      | DeviceUid             | <DeviceUid>             |
      | DeviceIdentification  | <DeviceIdentification>  |
      | alias                 | <Alias>                 |
      | Owner                 | <Owner>                 |
      | containerPostalCode   | <ContainerPostalCode>   |
      | containerCity         | <ContainerCity>         |
      | containerStreet       | <ContainerStreet>       |
      | containerNumber       | <ContainerNumber>       |
      | containerMunicipality | <ContainerMunicipality> |
      | gpsLatitude           | <GpsLatitude>           |
      | gpsLongitude          | <GpsLongitude>          |
      | Activated             | <Activated>             |
      | HasSchedule           | <HasSchedule>           |
      | PublicKeyPresent      | <PublicKeyPresent>      |
      | Manufacturer          | <Manufacturer>          |
      | ModelCode             | <ModelCode>             |
      | Description           | <Description>           |
      | Metered               | <Metered>               |
    Then the add device response is successfull
    # 'Activated' is altijd 'false' wanneer een nieuwe device wordt aangemaakt.
    # Om deze stap volledig succesvol te laten verlopen moet de value van 'Activated' 'false' zijn.
    And the device exists
      | DeviceIdentification       | <DeviceIdentification>  |
      | alias                      | <Alias>                 |
      | OrganizationIdentification | <Owner>                 |
      | containerPostalCode        | <ContainerPostalCode>   |
      | containerCity              | <ContainerCity>         |
      | containerStreet            | <ContainerStreet>       |
      | containerNumber            | <ContainerNumber>       |
      | containerMunicipality      | <ContainerMunicipality> |
      | gpsLatitude                | <GpsLatitude>           |
      | gpsLongitude               | <GpsLongitude>          |
      | Activated                  | <Activated>             |
      | HasSchedule                | <HasSchedule>           |
      | PublicKeyPresent           | <PublicKeyPresent>      |
      | DeviceModel                | <ModelCode>             |

    Examples: 
      | DeviceUid  | DeviceIdentification | Alias | Owner    | ContainerPostalCode | ContainerCity | ContainerStreet | ContainerNumber | ContainerMunicipality | GpsLatitude | GpsLongitude | Activated | HasSchedule | PublicKeyPresent | Manufacturer | ModelCode  | Description | Metered |
      | 1234567890 | TEST1024000000001    |       | test-org | 1234AA              | Maastricht    | Stationsstraat  |              12 |                       |           0 |            0 | true      | false       | false            | Test         | Test Model | Test        | true    |
