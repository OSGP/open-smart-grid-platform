Feature: Organisation management
  As a ...
  I want to manage the Organisations in the platform
  In order ...

  # SetEventNotification scenario's
	@OslpMockServer
	Scenario Outline: Set Event Notifications
		Given an oslp device
			| DeviceIdentification | <DeviceIdentification> |
		And the device returns a set event notification "<Result>" over OSLP
    When receiving a set event notification message request on OSGP
    	| Event                | <Event>                |
    	| DeviceIdentification | <DeviceIdentification> |
    Then the set event notification async response contains
      | DeviceIdentification | <DeviceIdentification> |
    And a set event notification OSLP message is sent to device "<DeviceIdentification>"
		And the platform buffers a set event notification response message for device "<DeviceIdentification>"
    
    Examples:
    	| DeviceIdentification | Result | Event                         |
      | TEST1024000000001    | OK     | LIGHT_EVENTS, SECURITY_EVENTS |
      #| TEST1024000000001    | OK     | SECURITY_EVENTS               |

#	Scenario: Test adding a device
#		When receiving an add device request
#			| DeviceIdentification | D01 |
#		Then the add device response is successfull
    #And the device exists
    #	| DeviceIdentification | D01 |

  #Scenario Outline: Adding a device
    #When receiving an add device request
      #| DeviceUid               | <DeviceUid>             |
      #| DeviceIdentification    | <DeviceIdentification>  |
      #| Alias                   | <Alias>                 |
      #| Owner                   | <Owner>                 |
      #| ContainerPostalCode     | <ContainerPostalCode>   |
      #| ContainerCity           | <ContainerCity>         |
      #| ContainerStreet         | <ContainerStreet>       |
      #| ContainerNumber         | <ContainerNumber>       |
      #| ContainerMunicipality   | <ContainerMunicipality> |
      #| GpsLatitude             | <GpsLatitude>           |
      #| GpsLongitude            | <GpsLongitude>          |
      #| Activated               | <Activated>             |
      #| HasSchedule             | <HasSchedule>           |
      #| PublicKeyPresent        | <PublicKeyPresent>      |
      #| DeviceModelManufacturer | <Manufacturer>          |
      #| DeviceModelModelCode    | <ModelCode>             |
      #| DeviceModelDescription  | <Description>           |
      #| DeviceModelMetered      | <Metered>               |
    #Then the add device response is successfull
#    # Activated gives true, but the device returns false as Activated
    #And the device exists
      #| DeviceIdentification       | <DeviceIdentification>  |
      #| Alias                      | <Alias>                 |
      #| OrganizationIdentification | <Owner>                 |
      #| ContainerPostalCode        | <ContainerPostalCode>   |
      #| ContainerCity              | <ContainerCity>         |
      #| ContainerStreet            | <ContainerStreet>       |
      #| ContainerNumber            | <ContainerNumber>       |
      #| ContainerMunicipality      | <ContainerMunicipality> |
      #| GpsLatitude                | <GpsLatitude>           |
      #| GpsLongitude               | <GpsLongitude>          |
      #| Activated                  | <Activated>             |
      #| HasSchedule                | <HasSchedule>           |
      #| PublicKeyPresent           | <PublicKeyPresent>      |
      #| DeviceModel                | <ModelCode>             |

    #Examples: 
      #| DeviceUid  | DeviceIdentification | Alias       | Owner    | ContainerPostalCode | ContainerCity | ContainerStreet | ContainerNumber | ContainerMunicipality | GpsLatitude | GpsLongitude | Activated | HasSchedule | PublicKeyPresent | Manufacturer | ModelCode  | Description | Metered |
      #| 1234567890 | TEST1024000000001    | Test device | test-org | 1234AA              | Maastricht    | Stationsstraat  |              12 |                       |           0 |            0 | true      | false       | false            | Test         | TestModel  | Test        | true    |
      #
#	Scenario Outline: Updating a device
 #	  Given a device
#	  	| DeviceUid               | <DeviceUid>             |
      #| DeviceIdentification    | <DeviceIdentification>  |
      #| Alias                   | <OldAlias>              |
      #| Owner                   | <Owner>                 |
      #| ContainerPostalCode     | <ContainerPostalCode>   |
      #| ContainerCity           | <ContainerCity>         |
      #| ContainerStreet         | <ContainerStreet>       |
      #| ContainerNumber         | <ContainerNumber>       |
      #| ContainerMunicipality   | <ContainerMunicipality> |
      #| GpsLatitude             | <GpsLatitude>           |
      #| GpsLongitude            | <GpsLongitude>          |
      #| Activated               | <Activated>             |
      #| HasSchedule             | <HasSchedule>           |
      #| PublicKeyPresent        | <PublicKeyPresent>      |
      #| DeviceModelManufacturer | <Manufacturer>          |
      #| DeviceModelModelCode    | <ModelCode>             |
      #| DeviceModelDescription  | <Description>           |
      #| DeviceModelMetered      | <Metered>               |
#	  When receiving an update device request
#	  	| DeviceUid               | <DeviceUid>             |
      #| DeviceIdentification    | <DeviceIdentification>  |
      #| Alias                   | <NewAlias>              |
      #| Owner                   | <Owner>                 |
      #| ContainerPostalCode     | <ContainerPostalCode>   |
      #| ContainerCity           | <ContainerCity>         |
      #| ContainerStreet         | <ContainerStreet>       |
      #| ContainerNumber         | <ContainerNumber>       |
      #| ContainerMunicipality   | <ContainerMunicipality> |
      #| GpsLatitude             | <GpsLatitude>           |
      #| GpsLongitude            | <GpsLongitude>          |
      #| Activated               | <Activated>             |
      #| HasSchedule             | <HasSchedule>           |
      #| PublicKeyPresent        | <PublicKeyPresent>      |
      #| DeviceModelManufacturer | <Manufacturer>          |
      #| DeviceModelModelCode    | <ModelCode>             |
      #| DeviceModelDescription  | <Description>           |
      #| DeviceModelMetered      | <Metered>               |
#	  Then the update device response is successfull
#	  And the device exists
#	  	| DeviceUid               | <DeviceUid>             |
      #| DeviceIdentification    | <DeviceIdentification>  |
      #| Alias                   | <NewAlias>              |
      #| Owner                   | <Owner>                 |
      #| ContainerPostalCode     | <ContainerPostalCode>   |
      #| ContainerCity           | <ContainerCity>         |
      #| ContainerStreet         | <ContainerStreet>       |
      #| ContainerNumber         | <ContainerNumber>       |
      #| ContainerMunicipality   | <ContainerMunicipality> |
      #| GpsLatitude             | <GpsLatitude>           |
      #| GpsLongitude            | <GpsLongitude>          |
      #| Activated               | <Activated>             |
      #| HasSchedule             | <HasSchedule>           |
      #| PublicKeyPresent        | <PublicKeyPresent>      |
      #| DeviceModelManufacturer | <Manufacturer>          |
      #| DeviceModelModelCode    | <ModelCode>             |
      #| DeviceModelDescription  | <Description>           |
      #| DeviceModelMetered      | <Metered>               |
#	  	
    #Examples: 
      #| DeviceUid  | DeviceIdentification | OldAlias   | NewAlias  | Owner    | ContainerPostalCode | ContainerCity | ContainerStreet | ContainerNumber | ContainerMunicipality | GpsLatitude | GpsLongitude | Activated | HasSchedule | PublicKeyPresent | Manufacturer | ModelCode  | Description | Metered |
      #| 1234567890 | TEST1024000000001    | BeforeTest | AfterTest | test-org | 1234AA              | Maastricht    | Stationsstraat  |              12 | TEST                  |           0 |            0 | true      | false       | false            | Test         | TestModel  | Test        | true    |
      
  # Verwijderd de device niet (dit geldt ook voor de organisatie)
#	Scenario: Remove A Device
#		Given a device
#			| DeviceIdentification | TEST1024000000001 |
#		When receiving a remove device request
#			| DeviceIdentification | TEST1024000000001 |
#		And the device exists
#			| DeviceIdentification | TEST1024000000001 |
#		Then the remove device response is successfull
#		And the device should be removed
#			| DeviceIdentification | TEST1024000000001 |

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
    
# ResumeSchedule scenario's
#	@OslpMockServer
#	Scenario Outline: Resume Schedule
#		Given an oslp device
#			| DeviceIdentification | <DeviceIdentification> |
#		And the device returns a resume schedule response "<Result>" over OSLP
#		When receiving a resume schedule request
#			| DeviceIdentification | <DeviceIdentification> |
#			| Index                | <Index>                |
#			| IsImmediate          | <IsImmediate>          |
#		Then the resume schedule async response contains
      #| DeviceIdentification | <DeviceIdentification> |
#		And a resume schedule OSLP message is sent to device "<DeviceIdentification>"
#		And the platform buffers a resume schedule response message for device "<DeviceIdentification>"
#		
#		Examples:
#			| DeviceIdentification | Index | IsImmediate | Result |
#			| TEST1024000000001    | 0     | true        | OK     |



# REMOVE

  #Scenario Outline: Remove an existing Organisation
    #Given an organization
      #| OrganizationIdentification | <OrganizationIdentification>   |
      #| Name                       | <Name>                         |
      #| Prefix                     | <Prefix>                       |
      #| FunctionGroup              | <FunctionGroup>                |
      #| Enabled                    | <Enabled>                      |
      #| Domains                    | <Domains>                      |
    #When receiving a remove organization request
      #| OrganizationIdentification | <OrganizationIdentification>   |
      #| Name                       | <Name>                         |
      #| FunctionGroup              | <FunctionGroup>                |
    #Then the remove organization response is successfull
    #And the organization with organization identification "<OrganizationIdentification>" should be disabled
    #
    #Examples:
    #	| OrganizationIdentification | Name                | Prefix | FunctionGroup | Enabled | Domains |
    #	| ATestOrganization          | A Test Organization | MAA    | ADMIN         | true    | COMMON  |
#
  #Scenario Outline: Remove a non existing Organisation
    #When receiving a remove organization request
      #| OrganizationIdentification | <OrganizationIdentification> |
      #| Name                       | <Name>                       |
      #| FunctionGroup              | <FunctionGroup>              |
    #Then the remove organization response contains
      #| FaultCode      | SOAP-ENV:Server                                                  |
      #| FaultString    | UNKNOWN_ORGANISATION                                             |
      #| FaultType      | FunctionalFault                                                  |
      #| Code           |                                                              101 |
      #| Message        | UNKNOWN_ORGANISATION                                             |
      #| Component      | WS_ADMIN                                                         |
      #| InnerException | com.alliander.osgp.domain.core.exceptions.UnknownEntityException |
      #| InnerMessage   | Organisation with id "<OrganizationIdentification>" could not be found.     |