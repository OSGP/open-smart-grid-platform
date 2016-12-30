Feature: Device installation
  As a grid operator
  I want to be able to perform DeviceInstallation operations on a device
  In order to ...

  Scenario Outline: Adding a device
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
      #| Activated                  | <Activated>             |
      | HasSchedule                | <HasSchedule>           |
      | PublicKeyPresent           | <PublicKeyPresent>      |
      | DeviceModel                | <ModelCode>             |

    Examples: 
      | DeviceUid  | DeviceIdentification                     | Alias | Owner    | ContainerPostalCode | ContainerCity | ContainerStreet | ContainerNumber | ContainerMunicipality | GpsLatitude | GpsLongitude | Activated | HasSchedule | PublicKeyPresent | Manufacturer | ModelCode  | Description | Metered |
      | 1234567890 | TEST1024000000001                        |       | test-org | 1234AA              | Maastricht    | Stationsstraat  |              12 |                       |           0 |            0 | true      | false       | false            | Test         | Test Model | Test        | true    |
      | 3456789012 | 0123456789012345678901234567890123456789 |       | test-org | 1234AA              | Maastricht    | Stationsstraat  |              12 |                       |           0 |            0 | true      | false       | false            | Test         | Test Model | Test        | true    |

  Scenario Outline: Add a device with an incorrect device identification
    Given a device model
      | ModelCode | <ModelCode> |
      | Metered   | <Metered>   |
    When receiving an add device request
      | DeviceUid               | <DeviceUid>             |
      | DeviceIdentification    | <DeviceIdentification>  |
      | alias                   | <Alias>                 |
      | owner                   | <Owner>                 |
      | containerPostalCode     | <ContainerPostalCode>   |
      | containerCity           | <ContainerCity>         |
      | containerStreet         | <ContainerStreet>       |
      | containerNumber         | <ContainerNumber>       |
      | containerMunicipality   | <ContainerMunicipality> |
      | gpsLatitude             | <GpsLatitude>           |
      | gpsLongitude            | <GpsLongitude>          |
      | Activated               | <Activated>             |
      | HasSchedule             | <HasSchedule>           |
      | PublicKeyPresent        | <PublicKeyPresent>      |
      | DeviceModelManufacturer | <Manufacturer>          |
      | DeviceModelModelCode    | <ModelCode>             |
      | DeviceModelDescription  | <Description>           |
      | DeviceModelMetered      | <Metered>               |
    Then the add device response contains soap fault
      | Message | Validation error |
      | FaultCode        | <FaultCode>         |
      | FaultString      | <FaultString>       |
      | FaultType        | <FaultType>         |
      | ValidationErrors | <Validation Errors> |

    # Note: The validation errors are ; separated if there are multiple.
    Examples: 
      | DeviceUid  | DeviceIdentification | Alias       | Owner    | ContainerPostalCode | ContainerCity | ContainerStreet | ContainerNumber | ContainerMunicipality | GpsLatitude | GpsLongitude | Activated | HasSchedule | PublicKeyPresent | Manufacturer | ModelCode  | Description | Metered | FaultCode       | FaultString      | FaultType       | Validation Errors                                                                                                                                                                                            |
      | 2345678901 |                      | Test device | test-org | 1234AA              | Maastricht    | Stationsstraat  |              12 |                       |           0 |            0 | true      | false       | false            | Test         | Test Model | Test        | true    | SOAP-ENV:Client | Validation error | ValidationError | cvc-minLength-valid: Value '' with length = '0' is not facet-valid with respect to minLength '1' for type 'Identification'.;cvc-type.3.1.3: The value '' of element 'ns1:DeviceIdentification' is not valid. |

  Scenario Outline: Add a device wih incorrect data
    Given a device model
      | ModelCode | <ModelCode> |
      | Metered   | <Metered>   |
    When receiving an add device request
      | DeviceUid               | <DeviceUid>             |
      | DeviceIdentification    | <DeviceIdentification>  |
      | alias                   | <Alias>                 |
      | owner                   | <Owner>                 |
      | containerPostalCode     | <ContainerPostalCode>   |
      | containerCity           | <ContainerCity>         |
      | containerStreet         | <ContainerStreet>       |
      | containerNumber         | <ContainerNumber>       |
      | containerMunicipality   | <ContainerMunicipality> |
      | gpsLatitude             | <GpsLatitude>           |
      | gpsLongitude            | <GpsLongitude>          |
      | Activated               | <Activated>             |
      | HasSchedule             | <HasSchedule>           |
      | PublicKeyPresent        | <PublicKeyPresent>      |
      | DeviceModelManufacturer | <Manufacturer>          |
      | DeviceModelModelCode    | <ModelCode>             |
      | DeviceModelDescription  | <Description>           |
      | DeviceModelMetered      | <Metered>               |
    Then the add device response contains soap fault
      | Message | Validation Errors |

    Examples: 
      # TODO, deviceidentification with 40 characters.
      # Empty owner, is defaulted
      # Unknown is also default as I am requesting with test-org in the headers.
      | DeviceUid | DeviceIdentification | Alias | Owner | ContainerPostalCode | ContainerCity | ContainerStreet | ContainerNumber | ContainerMunicipality | GpsLatitude | GpsLongitude | Activated | HasSchedule | PublicKeyPresent | Manufacturer | ModelCode | Description | Metered |

  #| 5678901234 | TEST1024000000001    | Test device |         | 1234AA              | Maastricht    | Stationsstraat  |              12 |                       |           0 |            0 | true      | false       | false            | Test         | Test Model | Test        | true    |
  #| 6789012345 | TEST1024000000001    | Test device | unknown | 1234AA              | Maastricht    | Stationsstraat  |              12 |                       |           0 |            0 | true      | false       | false            | Test         | Test Model | Test        | true    |
  Scenario: Adding a device which already exists
    Given a device
      | DeviceIdentification | TEST1024000000001 |
    When receiving an add device request
      | DeviceIdentification | TEST1024000000001 |
    Then the add device response contains soap fault
      | Message | EXISTING_DEVICE |

  Scenario: Updating a device
    Given a device
      | DeviceIdentification | TEST1024000000001 |
      | Alias                | BeforeTest        |
    When receiving an update device request
      | DeviceIdentification      | TEST1024000000001       |
      | Alias                     | AfterTest               |
      | NetworkAddress            | 127.0.0.1               |
      | Active                    | true                    |
      | internalId                |                       1 |
      | externalId                |                       2 |
      | relayType                 | LIGHT                   |
      | code                      |      100000000000000000 |
      | Index                     |                       1 |
      | LastKnownState            | false                   |
      | LastKnowSwitchingTime     | 2016-12-07T09:10:33.684 |
      | InMaintenance             | false                   |
      | TechnicalInstallationDate | 2016-12-07T09:10:33.684 |
      | UsePrefix                 | false                   |
      | Metered                   | false                   |
    Then the update device response is successfull
    And the device exists
      | DeviceIdentification | TEST1024000000001 |
  		#| Alias                | AfterTest         |
  
  Scenario: Updating a non existing device
    When receiving an update device request
      | DeviceIdentification      | TEST1024000000001       |
      | Alias                     | AfterTest               |
      | NetworkAddress            | 127.0.0.1               |
      | Active                    | true                    |
      | internalId                |                       1 |
      | externalId                |                       2 |
      | relayType                 | LIGHT                   |
      | code                      |      100000000000000000 |
      | Index                     |                       1 |
      | LastKnownState            | false                   |
      | LastKnowSwitchingTime     | 2016-12-07T09:10:33.684 |
      | InMaintenance             | false                   |
      | TechnicalInstallationDate | 2016-12-07T09:10:33.684 |
      | UsePrefix                 | false                   |
      | Metered                   | false                   |
    Then the update device response contains soap fault
      | Message | UNKNOWN_DEVICE |

  # This test doesn't work because the backend doesn't remove the device.
  #Scenario Outline: Remove A Device
  #		Given a device
  #			| DeviceIdentification | <DeviceIdentification> |
  #		When receiving a remove device request
  #			| DeviceIdentification | <DeviceIdentification> |
  #		Then the remove device response is successfull
  #		And the device with id "<DeviceIdentification>" does not exists
  #
  #		Examples:
  #			| DeviceIdentification |
  #			| TEST1024000000001    |
  #
  # Recent means today, yesterday and the day before yesterday (full days).
  # TODO Check response corretly.
  #	Scenario Outline: Find recent devices
  #		Given a device
  #| DeviceIdentification | <DeviceIdentification> |
  #When receiving a find recent devices request
  #	| DeviceIdentification       | <DeviceIdentification>       |
  #	| OrganizationIdentification | <OrganizationIdentification> |
  #Then the find recent devices response contains
  #	| DeviceIdentification       | <DeviceIdentification>       |
  #	| OrganizationIdentification | <OrganizationIdentification> |
  #
  #Examples:
  #	| DeviceIdentification | OrganizationIdentification |
  #	| TEST1024000000001    | test-org                   |
  #
  Scenario Outline: Find recent devices without owner
    When receiving a find recent devices request
      | DeviceIdentification | <DeviceIdentification> |
    Then the find recent devices response contains "0" devices

    Examples: 
      | DeviceIdentification |
      | TEST1024000000002    |

  # RegisterDevices scenario's
  # Nieuwe classe? Hoe kan een device geregistreerd worden?
  #	Scenario Outline: A Device Performs First Time Registration
  #		Given a not registered device
  #			| DeviceIdentification | <DeviceIdentification> |
  #		And the device returns a register device response over OSLP
  #		When receiving a register device request
  #		Then the register device response contains
  #			| DeviceUid            | <DeviceUid>            |
  #| DeviceIdentification | <DeviceIdentification> |
  #| DeviceType           | <DeviceType>           |
  #| GpsLatitude          | <GpsLatitude>          |
  #| GpsLongitude         | <GpsLongitude>         |
  #| CurrentTime          | <CurrentTime>          |
  #| TimeZone             | <TimeZone>             |
  #
  #Examples:
  #	| DeviceUid  | DeviceIdentification | DeviceType | GpsLatitude | GpsLongitude | CurrentTime | TimeZone |
  #	| 1234567890 | TEST1024000000001    |            |           0 |            0 |             |          |
  #
  #  # Nieuwe classe? Hoe kan een device geregistreerd worden?
  #	Scenario Outline: A Device Performs First Time Registration
  #		Given a not registered device
  #			| DeviceUid            | <DeviceUid>            |
  #| DeviceIdentification | <DeviceIdentification> |
  #| DeviceType           | <DeviceType>           |
  #| GpsLatitude          | <GpsLatitude>          |
  #| GpsLongitude         | <GpsLongitude>         |
  #| NetworkAddress       | <NetworkAddress>       |
  #| CurrentTime          | <CurrentTime>          |
  #| TimeZone             | <TimeZone>             |
  #		And the device returns a register device response over OSLP
  #			| Result | <Result> |
  #		When receiving a register device request
  #		Then the register device response contains
  #			| FaultCode      | SOAP-ENV:Server                                                   |
  #| FaultString    | NETWORK_IN_USE                                                    |
  #| FaultType      | FunctionalFault                                                   |
  #| Code           |                                                               204 |
  #| Message        | NETWORK_IN_USE                                                    |
  #| Component      | WS_CORE                                                           |
  #| InnerException | com.alliander.osgp.domain.core.exceptions.ExistingEntityException |
  #| InnerMessage   | Network address <NetworkAddress> already used by another device.  |
  #
  #Examples:
  #	| DeviceUid  | DeviceIdentification | DeviceType | GpsLatitude | GpsLongitude | NetworkAddress | CurrentTime | TimeZone | Result |
  #	| 1234567890 | TEST1024000000001    |            |           0 |            0 | 0.0.0.0        |             |          | OK     |
  @OslpMockServer
  Scenario: Start Device
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
    And the device returns a start device response "OK" over OSLP
    When receiving a start device test request
      | DeviceIdentification | TEST1024000000001 |
    Then the start device async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a start device OSLP message is sent to device "TEST1024000000001"
    And the platform buffers a start device response message for device "TEST1024000000001"
      | Result | OK |

  @OslpMockServer
  Scenario: Stop Device
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
    And the device returns a stop device response "OK" over OSLP
    When receiving a stop device test request
      | DeviceIdentification | TEST1024000000001 |
    Then the stop device async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a stop device OSLP message is sent to device "TEST1024000000001"
    And the platform buffers a stop device response message for device "TEST1024000000001"
      | Result | OK |
