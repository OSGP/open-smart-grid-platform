Feature: Adhoc Management
  In order to ... 
  As a platform 
  I want to asynchronously handle set light requests

# SetLight scenario's
	# TODO: The index parameter is not yet implemented.
  @OslpMockServer
  Scenario Outline: Receive A Set Light Request With A Single Light Value
    Given an oslp device
      | DeviceIdentification | <DeviceIdentification> |
      | Status               | <Status>               |
    And the device returns a set light over OSLP
    	| Result | <Result> |
    When receiving a set light request
      | DeviceIdentification | <DeviceIdentification> |
      | Index                | <Index>                |
      | On                   | <On>                   |
    Then the set light async response contains
      | DeviceIdentification | <DeviceIdentification> |
    And a set light OSLP message is sent to device "<DeviceIdentification>"
    And the platform buffers a set light response message for device "<DeviceIdentification>"

    Examples: 
      | DeviceIdentification | Status | Index | On    | Result | Description |
      | D01                  | active |       | true  | OK     |             |

### Converted Fitnesse tests to Cucumber ###
	@OslpMockServer
	Scenario Outline: Receive A Set Light Request With An Invalid Single Light Value
		Given an oslp device
			| DeviceIdentification | <DeviceIdentification> |
			| Status               | <Status>               |
		When receiving a set light request
			| DeviceIdentification | <DeviceIdentification> |
      | Index                | <Index>                |
      | On                   | <On>                   |
		Then the set light async response contains
			| ValidationError | <ValidationError> |
    And a set light OSLP message is sent to device "<DeviceIdentification>"
    
    Examples:
    	| DeviceIdentification | Status | Index | On    | ValidationError  |
    	| D01                  | active |       | true  | VALIDATION ERROR  |

	@OslpMockServer
	Scenario Outline: Receive A Get Set Light Response Request
		Given an oslp device
			| DeviceIdentification | <DeviceIdentification> |
		And the device returns a set light over OSLP
			| Result | <Result> |
		When receiving a set light response request
			| DeviceIdentification | <DeviceIdentification> |
		Then the set light async response contains
      | DeviceIdentification | <DeviceIdentification> |
    And a set light OSLP message is sent to device "<DeviceIdentification>"
		And the platform buffers a set light response message for device "<DeviceIdentification>"

		Examples:
			| DeviceIdentification | Result |
			| D01                  | OK     |

	@OslpMockServer
	Scenario Outline: Receive A Set Light Request With Multiple Light Values
		Given an oslp device
      | DeviceIdentification | <DeviceIdentification> |
      | Status               | <Status>               |
    When receiving a set light request
    	| DeviceIdentification | <DeviceIdentification> |
      | Index                | <Index>                |
      | On                   | <On>                   |
    Then the set light async response contains
			| DeviceIdentification | <DeviceIdentification> |
		And a set light OSLP message is sent to device "<DeviceIdentification>"
		And the platform buffers a set light response message for device "<DeviceIdentification>"
	
		Examples:
    	| DeviceIdentification | Status | Index | On    |
    	| D01                  | active |       | true  |
	
	@OslpMockServer
	Scenario Outline: Receive A Set Light Request With Invalid Multiple Light Values
		Given an oslp device
			| DeviceIdentification | <DeviceIdentification> |
			| Status               | <Status>               |
		When receiving a set light request
    	| DeviceIdentification | <DeviceIdentification> |
      | Index                | <Index>                |
      | On                   | <On>                   |
		Then the set light async response contains
			| ValidationError | <ValidationError> |
		And a set light OSLP message is sent to device "<DeviceIdentification>"
	
		Examples:
			| DeviceIdentification | Status | Index | On   | ValidationError  |
			| D01                  | active |       | true | VALIDATION ERROR |
			
# GetStatus scenario's
	# Succesfull: Status values
  @OslpMockServer
  Scenario Outline: Succesfull Retrieval Of Status Values
    Given an oslp device
      | DeviceIdentification | <DeviceIdentification> |
      | Status               | <Status>               |
    And the device returns a get status response over OSLP
    	| Result | <Result> |
    When receiving a get status request
      | DeviceIdentification | <DeviceIdentification> |
    Then the get status async response contains
      | DeviceIdentification | <DeviceIdentification> |
    And a get status OSLP message is sent to device "<DeviceIdentification>"
    And the platform buffers a get status response message for device "<DeviceIdentification>"

    Examples:
      | DeviceIdentification | Status | Result |
      | D01                  | active | OK     |
	
	# Failure: Status values
	@OslpMockServer
	Scenario Outline: Fail To Get Status Values
		Given an oslp device
			| DeviceIdentification | <DeviceIdentification> |
			| Status               | <Status>               |
		When receiving a get status request
			| DeviceIdentification | <DeviceIdentification> |
		Then a get status OSLP message is not sent to device "<DeviceIdentification>"
			
		Examples:
			| DeviceIdentification | Status       |
			| unknown              | unknown      |
			| unregistered         | unregistered |
	
	# Multiple lights
	@OslpMockServer
	Scenario Outline: Receive Status From A Device With Multiple Lights
		Given an oslp device
			| DeviceIdentification | <DeviceIdentification> |
			| Status               | <Status>               |
		When receiving a get status request
      | DeviceIdentification | <DeviceIdentification> |
			| NumberOfLights       | <NumberOfLights>       |
		Then the get status async response contains
      | DeviceIdentification | <DeviceIdentification> |
    And a get status OSLP message is sent to device "<DeviceIdentification>"
		And the platform buffers a get status response message for device "<DeviceIdentification>"
    
    Examples:
    	| DeviceIdentification | Status | NumberOfLights |
    	| D01                  | active | 1              |
    	| D01                  | active | 6              |
    	
	# Get status response request
	@OslpMockServer
	Scenario Outline: Receive A Get Status Response
   	Given an oslp device
      | DeviceIdentification | <DeviceIdentification> |
    And the device returns a get status response over OSLP
    	| Result | <Result> |
    When receiving a get status request
      | DeviceIdentification | <DeviceIdentification> |
    Then the get status response contains 
      | DeviceIdentification | <DeviceIdentification> |
    And a get status OSLP message is sent to device "<DeviceIdentification>"
		And the platform buffers a get status response message for device "<DeviceIdentification>"

    Examples: 
      | DeviceIdentification | Result |
      | D01                  | OK     |

# ResumeSchedule scenario's
	@OslpMockServer
	Scenario Outline: Resume Schedule
		Given an oslp device
			| DeviceIdentification | <DeviceIdentification> |
			| Index                | <Index>                |
		And the device returns a resume schedule response over OSLP
			| Result | <Result> |
		When receiving the resume schedule request
			| DeviceIdentification | <DeviceIdentification> |
		Then the resume schedule async response contains
      | DeviceIdentification | <DeviceIdentification> |
		And a resume schedule OSLP message is sent to device "<DeviceIdentification>"
		And the platform buffers a resume schedule response message for device "<DeviceIdentification>"
		
		Examples:
			| DeviceIdentification | Index | Result |
			| D01                  |       | OK     |
			
	@OslpMockServer
	Scenario Outline: Resume Schedule With Invalid Index
		Given an oslp device
			| DeviceIdentification | <DeviceIdentification> |
			| Index                | <Index>                |
		When receiving the resume schedule request
			| DeviceIdentification | <DeviceIdentification> |
		Then the resume schedule async response contains
			| ValidationError | <ValidationError> |
		And a set light OSLP message is sent to device "<DeviceIdentification>"
		
		Examples:
			| DeviceIdentification | Index | ValidationError  |
			| D01                  |       | VALIDATION ERROR |
		
	@OslpMockServer
	Scenario Outline: Receive A Resume Schedule Response Request
		Given an oslp device
			| DeviceIdentification | <DeviceIdentification> |
		And the device returns a resume schedule over OSLP
			| Result | <Result> |
		When receiving the resume schedule response
			| DeviceIdentification | <DeviceIdentification> |
		Then the resume schedule async response contains
      | DeviceIdentification | <DeviceIdentification> |
    And a resume schedule OSLP message is sent to device "<DeviceIdentification>"
		And the platform buffers a resume schedule response message for device "<DeviceIdentification>"
		
		Examples:
			| DeviceIdentification | Result |
			| D01                  | OK     |
	
# SetReboot scenario's
	@OslpMockServer
	Scenario Outline: Set Reboot
		Given an oslp device
			| DeviceIdentification | <DeviceIdentification> |
			| Index                | <Status>               |
		When receiving the set reboot request
			| DeviceIdentification | <DeviceIdentification> |
		Then the resume schedule async response contains
      | DeviceIdentification | <DeviceIdentification> |
		And a resume schedule OSLP message is sent to device "<DeviceIdentification>"
		And the platform buffers a resume schedule response message for device "<DeviceIdentification>"
		
		Examples:
			| DeviceIdentification | Status |
			| D01                  | OK     |
		
	@OslpMockServer
	Scenario Outline: Receive A Get Set Reboot Response Request
		Given an oslp device
			| DeviceIdentification | <DeviceIdentification> |
		And the device returns a set reboot over OSLP
			| Result | <Result> |
		When receiving a set reboot response request
			| DeviceIdentification | <DeviceIdentification> |
		Then the set reboot async response contains
      | DeviceIdentification | <DeviceIdentification> |
    And a set reboot OSLP message is sent to device "<DeviceIdentification>"
		And the platform buffers a set reboot response message for device "<DeviceIdentification>"

		Examples:
			| DeviceIdentification | Result |
			| D01                  | OK     |
	
# SetTransition scenario's
	@OslpMockServer
	Scenario Outline: Set Transition
		Given an oslp device
			| DeviceIdentification | <DeviceIdentification> |
			| Index                | <Status>               |
		When receiving a set transition request
			| DeviceIdentification | <DeviceIdentification> |
		Then the set transition async response contains
      | DeviceIdentification | <DeviceIdentification> |
    And a set transition OSLP message is sent to device "<DeviceIdentification>"
		And the platform buffers a set transition response message for device "<DeviceIdentification>"
		
		Examples:
			| DeviceIdentification | Status |
			| D01                  | active |
	
	@OslpMockServer
	Scenario Outline: Receive A Get Set Transition Response Request
		Given an oslp device
      | DeviceIdentification | <DeviceIdentification> |
    And the device returns a set transition response over OSLP
    	| Result | <Result> |
    When receiving a set transition response request
      | DeviceIdentification | <DeviceIdentification> |
    Then the set transition response contains 
      | DeviceIdentification | <DeviceIdentification> |
    And a set transition OSLP message is sent to device "<DeviceIdentification>"
		And the platform buffers a set transition response message for device "<DeviceIdentification>"

		Examples:
			| DeviceIdentification | Result |
			| D01                  | OK     |