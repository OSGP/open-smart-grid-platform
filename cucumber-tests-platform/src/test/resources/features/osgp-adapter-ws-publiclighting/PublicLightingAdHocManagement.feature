Feature: Adhoc Management
  In order to ... 
  As a platform 
  I want to asynchronously handle set light requests

# SetLight scenario's
  @OslpMockServer
  Scenario Outline: Receive A Set Light Request With A Single Light Value
    Given an oslp device
      | DeviceIdentification | <DeviceIdentification> |
      | Status               | <Status>               |
    And the device returns a set light response "<Result>" over OSLP
    When receiving a set light request
      | DeviceIdentification | <DeviceIdentification> |
      | Index                | <Index>                |
      | On                   | <On>                   |
    Then the set light async response contains
      | DeviceIdentification | <DeviceIdentification> |
    And a set light OSLP message is sent to device "<DeviceIdentification>"
    And the platform buffers a set light response message for device "<DeviceIdentification>"

    Examples: 
      | DeviceIdentification | Status | Index | On    | Result |
      | TEST1024000000001    | active | 0     | true  | OK     |

### Converted Fitnesse tests to Cucumber ###
	Scenario Outline: Receive A Set Light Request With An Invalid Single Light Value
		Given a device
			| DeviceIdentification | TEST1024000000001 |
		When receiving a set light request
			| DeviceIdentification | <DeviceIdentification> |
      | Index                | <Index>                |
      | On                   | <On>                   |
		Then the set light async response contains
			| FaultCode      | SOAP-ENV:Server                                                  |
      | FaultString    | UNKNOWN_DEVICE                                                   |
      | Code           | 201                                                              |
      | Message        | UNKNOWN_DEVICE                                                   |
      | Component      | WS_PUBLIC_LIGHTING                                               |
      | InnerException | com.alliander.osgp.domain.core.exceptions.UnknownEntityException |
      | InnerMessage   | Device with id "<DeviceIdentification>" could not be found.      |
    
    Examples:
    	| DeviceIdentification | Index | On   |
    	| TEST1024000000002    | 0     | true |

	# Note: The program can't execute the param 'On' with the value 'false', because of SoapUI
	# Note: DimValue has to be at least '1' when the device is on
	@OslpMockServer
	Scenario Outline: Receive A Set Light Request With Multiple Light Values
		Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Status               | active            |
	  	| internalId           | <internalId>      |
	  	| externalId           | <externalId>      |
	  	| relayType            | LIGHT             |
    And the device returns a set light response "<Result>" over OSLP
    When receiving a set light request
    	| DeviceIdentification | TEST1024000000001 |
      | Index                | <Index>           |
      | On                   | <On>              |
      | DimValue             | <DimValue>        |
    Then the set light async response contains
			| DeviceIdentification | TEST1024000000001 |
		And a set light OSLP message is sent to device "TEST1024000000001"
		And the platform buffers a set light response message for device "TEST1024000000001"
	
		Examples:
    	| internalId | externalId | Index | On    | DimValue |
    	| 2          | 2          | 2     | true  | 1        |
    	| 3          | 3          | 3     | true  | 3        |
    #	| 4          | 4          | 3     | false | 5        |
	
	Scenario Outline: Receive A Set Light Request With Invalid Multiple Light Values
		Given a device
			| DeviceIdentification | TEST1024000000001 |
		When receiving a set light request
    	| DeviceIdentification | TEST1024000000001 |
      | Index                | <Index>           |
      | On                   | <On>              |
      | DimValue             | <DimValue>        |
		Then the set light async response contains
			| FaultCode      | SOAP-ENV:Server                                                                        |
      | FaultString    | VALIDATION_ERROR                                                                       |
      | Code           | 401                                                                                    |
      | Message        | VALIDATION_ERROR                                                                       |
      | Component      | WS_PUBLIC_LIGHTING                                                                     |
      | InnerException | com.alliander.osgp.domain.core.exceptions.ValidationException                          |
      | InnerMessage   | Validation Exception, violations: Dim value may not be set when light is switched off; |
	
		Examples:
    	| internalId | externalId | Index | On    | DimValue |
      | 4          | 4          | 3     | false | 5        |
			
# GetStatus scenario's
	# Succesfull: Status values
  @OslpMockServer
  Scenario Outline: Get Status Values
    Given an oslp device
      | DeviceIdentification | <DeviceIdentification> |
      | Status               | <Status>               |
    And the device returns a get status response "<Result>" over OSLP
    When receiving a get status request
      | DeviceIdentification | <DeviceIdentification> |
    Then the get status async response contains
      | DeviceIdentification | <DeviceIdentification> |
    And a get status OSLP message is sent to device "<DeviceIdentification>"
    And the platform buffers a get status response message for device "<DeviceIdentification>"

    Examples:
      | DeviceIdentification | Status | Result |
      | TEST1024000000001    | active | OK     |
	
	# Failure: Status values
	Scenario Outline: Fail To Get Status Values
		Given a device
			| DeviceIdentification | TEST1024000000001 |
		When receiving a get status request
			| DeviceIdentification | <DeviceIdentification> |
		Then the get status async response contains
			| FaultCode      | SOAP-ENV:Server                                                  |
      | FaultString    | UNKNOWN_DEVICE                                                   |
      | Code           | 201                                                              |
      | Message        | UNKNOWN_DEVICE                                                   |
      | Component      | WS_PUBLIC_LIGHTING                                               |
      | InnerException | com.alliander.osgp.domain.core.exceptions.UnknownEntityException |
      | InnerMessage   | Device with id "<DeviceIdentification>" could not be found.      |
		Examples:
			| DeviceIdentification | Status       |
			| unknown              | unknown      |
	
	# Multiple lights
	@OslpMockServer
	Scenario Outline: Get Status Values From A Device With Multiple Lights
		Given an oslp device
			| DeviceIdentification | <DeviceIdentification> |
			| Status               | active                 |
		And the device returns a get status response over OSLP
			| Result | <Result> |
		When receiving a get status request
      | DeviceIdentification | <DeviceIdentification> |
      | Index                | <Index>                |
			| On                   | <On>                   |
			| DimValue             | <DimValue>             |
		Then the get status async response contains
      | DeviceIdentification | <DeviceIdentification> |
    And a get status OSLP message is sent to device "<DeviceIdentification>"
    And the platform buffers a get status response message for device
      | DeviceIdentification | <DeviceIdentification> |
			| Result               | <Result>               |
    
    Examples:
    	| DeviceIdentification | On   | DimValue | Result                         |
    	| TEST1024000000001    | true | 1        | 1,1,TARIFF;2,2,LIGHT;3,3,LIGHT |

# ResumeSchedule scenario's
	@OslpMockServer
	Scenario Outline: Resume Schedule
		Given an oslp device
			| DeviceIdentification | <DeviceIdentification> |
			| HasSchedule          | <HasSchedule>          |
		And the device returns a resume schedule response "<Result>" over OSLP
		When receiving a resume schedule request
			| DeviceIdentification | <DeviceIdentification> |
			| Index                | <Index>                |
			| IsImmediate          | <IsImmediate>          |
		Then the resume schedule async response contains
      | DeviceIdentification | <DeviceIdentification> |
		And a resume schedule OSLP message is sent to device "<DeviceIdentification>"
		And the platform buffers a resume schedule response message for device "<DeviceIdentification>"
		
		Examples:
			| DeviceIdentification | HasSchedule | Index | IsImmediate | Result |
			| TEST1024000000001    | true        | 0     | true        | OK     |

	Scenario Outline: Resume Schedule With Invalid Index
		Given a device
			| DeviceIdentification | <DeviceIdentification> |
			| HasSchedule          | <HasSchedule>          |
		When receiving a resume schedule request
			| DeviceIdentification | <DeviceIdentification> |
			| Index                | <Index>                |
			| IsImmediate          | <IsImmediate>          |
		Then the resume schedule async response contains
      | FaultCode          | SOAP-ENV:Client                                                                                                                                   |
      | FaultString        | Validation error                                                                                                                                  |
      | FaultType          | ValidationError                                                                                                                                   |
      | ValidationErrors   | cvc-datatype-valid.1.2.1: '<Index>' is not a valid value for 'integer'.; cvc-type.3.1.3: The value '<Index>' of element 'ns1:Index' is not valid. |
		
		Examples:
			| DeviceIdentification | HasSchedule | Index | IsImmediate |
			| TEST1024000000001    | true        |       | true        |
	
# SetReboot scenario's
	@OslpMockServer
	Scenario Outline: Set Reboot
		Given an oslp device
			| DeviceIdentification | <DeviceIdentification> |
		And the device returns a set reboot response "<Result>" over OSLP
		When receiving a set reboot request
			| DeviceIdentification | <DeviceIdentification> |
		Then the set reboot async response contains
      | DeviceIdentification | <DeviceIdentification> |
		And a set reboot OSLP message is sent to device "<DeviceIdentification>"
		And the platform buffers a set reboot response message for device "<DeviceIdentification>"
		
		Examples:
			| DeviceIdentification | Result |
			| TEST1024000000001    | OK     |
	
# SetTransition scenario's
	@OslpMockServer
	Scenario Outline: Set Transition
		Given an oslp device
			| DeviceIdentification | <DeviceIdentification> |
		And the device returns a set transition response "<Result>" over OSLP
		When receiving a set transition request
			| DeviceIdentification | <DeviceIdentification> |
		Then the set transition async response contains
      | DeviceIdentification | <DeviceIdentification> |
    And a set transition OSLP message is sent to device "<DeviceIdentification>"
		And the platform buffers a set transition response message for device "<DeviceIdentification>"
		
		Examples:
			| DeviceIdentification | Result |
			| TEST1024000000001    | OK     |