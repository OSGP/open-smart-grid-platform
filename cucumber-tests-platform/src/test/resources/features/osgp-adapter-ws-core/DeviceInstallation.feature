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
      | 1234567890 | TEST1024000000001                            | Test device | test-org | 1234AA              | Maastricht    | Stationsstraat  |              12 |                       |           0 |            0 | true      | false       | false            | Test         | Test Model | Test        | true    |
      #| 2345678901 |                                              | Test device | test-org | 1234AA              | Maastricht    | Stationsstraat  |              12 |                       |           0 |            0 | true      | false       | false            | Test         | Test Model | Test        | true    |
      #| 3456789012 | TESTTESTTESTTESTTESTTESTTESTTESTTESTTESTTEST | Test device | test-org | 1234AA              | Maastricht    | Stationsstraat  |              12 |                       |           0 |            0 | true      | false       | false            | Test         | Test Model | Test        | true    |
      #| 4567890123 | TEST1024000000001                            | Test device | test-org | 1234AA              | Maastricht    | Stationsstraat  |              12 |                       |           0 |            0 | true      | false       | false            | Test         | Test Model | Test        | true    |
      #| 5678901234 | TEST1024000000001                            | Test device |          | 1234AA              | Maastricht    | Stationsstraat  |              12 |                       |           0 |            0 | true      | false       | false            | Test         | Test Model | Test        | true    |
      #| 5678901234 | TEST1024000000001                            | Test device | unknown  | 1234AA              | Maastricht    | Stationsstraat  |              12 |                       |           0 |            0 | true      | false       | false            | Test         | Test Model | Test        | true    |

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

	Scenario: Updating a device
	  Given a device
	  	| DeviceIdentification | TEST1024000000001 |
	  	| Alias                | BeforeTest        |
	  When receiving an update device request
	  	| DeviceIdentification      | TEST1024000000001       |
	  	| Alias                     | AfterTest               |
	  	| NetworkAddress            | 127.0.0.1               |
	  	| Active                    | true                    |
	  	| internalId                | 1                       |
	  	| externalId                | 2                       |
	  	| relayType                 | LIGHT                   |
	  	| code                      | 100000000000000000      |
	  	| Index                     | 1                       |
	  	| LastKnownState            | false                   |
	  	| LastKnowSwitchingTime     | 2016-12-07T09:10:33.684 |
	  	| InMaintenance             | false                   |
	  	| TechnicalInstallationDate | 2016-12-07T09:10:33.684 |
	  	| UsePrefix                 | false                   |
	  	| Metered                   | false                   |
	  Then the update device response is successfull
	  And the device exists
	  	| DeviceIdentification | TEST1024000000001 |
	  	| Alias                | AfterTest         |

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
			
### Converted Fitnesse tests to Cucumber ###
 # RemoveDevice doesn't work
#	# RemoveDevice scenario's
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
			
	# FindRecentDevices scenario's
	Scenario Outline: Find recent devices # Recent means today, yesterday and the day before yesterday (full days).
		Given a device
      | DeviceIdentification | <DeviceIdentification> |
    When receiving a find recent devices request
    	| DeviceIdentification       | <DeviceIdentification>       |
    	| OrganizationIdentification | <OrganizationIdentification> |
    # Geeft geen response
    Then the find recent devices response contains
    	| DeviceIdentification       | <DeviceIdentification>       |
    	| OrganizationIdentification | <OrganizationIdentification> |
    
    Examples:
    	| DeviceIdentification | OrganizationIdentification |
    	| TEST1024000000001    | test-org                   |
    	
  Scenario Outline: Find recent devices without owner
		Given a device
      | DeviceIdentification | TEST1024000000001 |
    When receiving a find recent devices request
    	| DeviceIdentification | <DeviceIdentification> |
    # Geeft geen response
    Then the find recent devices response contains
    	| FaultCode      | SOAP-ENV:Server                                                  |
      | FaultString    | <FaultString>                                                    |
      | FaultType      | FunctionalFault                                                  |
      | Code           |                                                              201 |
      | Message        | <Message>                                                        |
      | Component      | WS_CORE                                                          |
      | InnerException | com.alliander.osgp.domain.core.exceptions.UnknownEntityException |
      | InnerMessage   | <InnerMessage>                                                   |
    
    Examples:
    	| DeviceIdentification | FaultString    | Message        | InnerMessage                                         |
    	| TEST1024000000001    | EMPTY_OWNER    | EMPTY_OWNER    | Can not find devices without 'Owner' parameter.      |
    	| TEST1024000000002    | UNKNOWN_DEVICE | UNKNOWN_DEVICE | Can not find devices with unknown 'Owner' parameter. |
  
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
    	
	# Start/Stop Device scenario's
	@OslpMockServer
	Scenario Outline: Start Device
		Given an oslp device
			| DeviceIdentification | TEST1024000000001 |
		And the device returns a start device response "<Result>" over OSLP
		When receiving a start device request
			| DeviceIdentification | <DeviceIdentification> |
		Then the start device async response contains
      | DeviceIdentification | <DeviceIdentification> |
    And a start device OSLP message is sent to device "<DeviceIdentification>"
		And the platform buffers a start device response message for device "<DeviceIdentification>"
		
		Examples:
			| DeviceIdentification | Result |
			| TEST1024000000001    | OK     |
			| TEST1024000000002    | OK     |
			
	@OslpMockServer
	Scenario Outline: Stop Device
		Given an oslp device
			| DeviceIdentification | TEST1024000000001 |
		And the device returns a stop device response "<Result>" over OSLP
		When receiving a stop device request
			| DeviceIdentification | <DeviceIdentification> |
		Then the stop device async response contains
      | DeviceIdentification | <DeviceIdentification> |
    And a stop device OSLP message is sent to device "<DeviceIdentification>"
		And the platform buffers a stop device response message for device "<DeviceIdentification>"
		
		Examples:
			| DeviceIdentification | Result |
			| TEST1024000000001    | OK     |
			| TEST1024000000002    | OK     |