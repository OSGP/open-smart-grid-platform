Feature: 
  As a grid operator
  I want to be able to perform SmartMeteringBundle operations on a device
    
Background:
    Given a device with DeviceID "E9998000014123414" 
    Given a gas device with DeviceID "G00XX561204926013"
    And an organisation with OrganisationID "LianderNetManagement"
    
@SLIM-484
  Scenario: Handle a bundle of requests
	When a bundled request message is received
	Then the requests in the bundled request message will be executed from top to bottom
	And a bundled response message will contain the response from all the requests
	
@SLIM-501	
  Scenario: Retrieve COSEM Logical Device Name
	When a retrieve configuration request for OBIS code 0.0.42.0.0.255 is received as part of a bundled request
	Then "bytes[75, 70, 77, 56, 48, 48, 48, 48, 49, 52, 49, 50, 51, 52, 49, 52]" is part of the response

@SLIM-501	
  Scenario: Retrieve Administrative in/out
	When a retrieve configuration request for OBIS code 0.1.94.31.0.255 is received as part of a bundled request
	Then "Choice=ENUMERATE, ResultData isNumber" is part of the response
	
@SLIM-501	
  Scenario:	Retrieve Currently Active Tariff
	When a retrieve configuration request for OBIS code 0.0.96.14.0.255 is received as part of a bundled request
	Then "bytes[0, 1]" is part of the response
	
@SLIM-526
  Scenario: Retrieve the association LN objectlist of a meter in a Bundle request
  	When the get associationLnObjects request is received as part of a bundled request
  	Then "AssociationLnListElement" is part of the response
  	