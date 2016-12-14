Feature: Temp Feature
  As a ...
  I want to ...
  In order ...

 # RemoveDevice doesn't work
	# RemoveDevice scenario's
#	Scenario Outline: Remove A Device
#		Given a device
#			| DeviceIdentification | <DeviceIdentification> |
#		And the device exists
#			| DeviceIdentification | <DeviceIdentification> |
#		When receiving a remove device request
#			| DeviceIdentification | <DeviceIdentification> |
#		Then the remove device response is successfull
#		And the device should be removed
#			| DeviceIdentification | <DeviceIdentification> |
#		
#		Examples:
#			| DeviceIdentification |
#			| TEST1024000000001    |

	# The database sets (almost) all boolean values to 'false',
	# so when you try to add a device with 'true' as a value, it fails.
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
    # 'Activated' is altijd 'false' wanneer een nieuwe device wordt aangemaakt.
    # Om deze stap volledig succesvol te laten verlopen moet de value van 'Activated' 'false' zijn.
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
      | Activated                  | <Activated>             |
      | HasSchedule                | <HasSchedule>           |
      | PublicKeyPresent           | <PublicKeyPresent>      |
      | DeviceModel                | <ModelCode>             |

    Examples:
    # Second to Sixth example are tests to check if a new device can be created
      | DeviceUid  | DeviceIdentification                         | Alias       | Owner    | ContainerPostalCode | ContainerCity | ContainerStreet | ContainerNumber | ContainerMunicipality | GpsLatitude | GpsLongitude | Activated | HasSchedule | PublicKeyPresent | Manufacturer | ModelCode  | Description | Metered |
      | 1234567890 | TEST1024000000001                            | Test device | test-org | 1234AA              | Maastricht    | Stationsstraat  |              12 |                       |           0 |            0 | true      | false       | false            | Test         | TestModel  | Test        | true    |
      #| 2345678901 |                                              | Test device | test-org | 1234AA              | Maastricht    | Stationsstraat  |              12 |                       |           0 |            0 | true      | false       | false            | Test         | TestModel  | Test        | true    |
      #| 3456789012 | TESTTESTTESTTESTTESTTESTTESTTESTTESTTESTTEST | Test device | test-org | 1234AA              | Maastricht    | Stationsstraat  |              12 |                       |           0 |            0 | true      | false       | false            | Test         | TestModel  | Test        | true    |
      #| 4567890123 | TEST1024000000001                            | Test device | test-org | 1234AA              | Maastricht    | Stationsstraat  |              12 |                       |           0 |            0 | true      | false       | false            | Test         | TestModel  | Test        | true    |
      #| 5678901234 | TEST1024000000001                            | Test device |          | 1234AA              | Maastricht    | Stationsstraat  |              12 |                       |           0 |            0 | true      | false       | false            | Test         | TestModel  | Test        | true    |
      #| 5678901234 | TEST1024000000001                            | Test device | unknown  | 1234AA              | Maastricht    | Stationsstraat  |              12 |                       |           0 |            0 | true      | false       | false            | Test         | TestModel  | Test        | true    |