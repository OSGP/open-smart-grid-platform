Feature: Firmware management
  As OSGP 
  I want to manage the firmware of a device
  In order to ...

Scenario Outline: Get firmware version
	Given a device
	    | DeviceIdentification | device-01          |
	    | Status               | Active             |
	    | Firmware Version     | <Firmware Version> |
	    | Organization         | Test Organization  |
	    | IsActivated          | True               |
	And an oslp device	    
	    | DeviceIdentification | device-01 |
	 When receiving a get firmware version request
	    | DeviceIdentification | device-01 |
    And the device returns firmware version "<Firmware Version>" over OSLP 
	 Then the get firmware version response contains
	    | FirmwareVersion | <Firmware Version> |
	  And a get firmware version OSLP message is sent to device "device-01"
	  And a get firmware version OSLP response message is received
	    | FirmwareVersion | <Firmware Version> |
	    | Result          | OK                 |
	  And the platform sends a get get firmware version response message
	    | FirmwareVersion | <Firmware Version> |
	    
Examples: 
      | Firmware Version |
      | 0123             |

# Scenario: Unsuccessfully get firmware version
# |given|a get firmware version request for device @device                                     |
# |and  |the get firmware version request refers to a device @device with status @status       |
# |and  |the get firmware version request refers to an organisation that is authorised         |
# |when |the get firmware version request is received                                          |
# |then |a get firmware version oslp message is sent to device @device should be @ismessagesent|

# -| get firmware version failure matrix |
# |device   |status      |oslpresponse|ismessagesent|
# |device-01|unknown     |N/A         |false        |
# |device-01|unregistered|N/A         |false        |

#!3 ''NOTE: Device Message Status not implemented in protocol??? Therefore the following tests cannot be performed...''

# * |device-01|active      |FAILURE     |true         |DEVICEMESSAGEFAILEDEXCEPTION  |
# * |device-01|active      |REJECTED    |true         |DEVICEMESSAGEREJECTEDEXCEPTION|

#!2 Parameters:
# * device: device identification
# * status: active, unknown, unregistered, ???
# * isgenerated: boolean (true/false) indicating whether a correlationID is generated
# * isqueued: boolean (true/false) indicating whether a message is added to the queue
# * result: OK (correlationID is returned) or exception
Scenario Outline: Failure get firmware version
  Given a get firmware version request for device testdevice
    And the get firmware version request refers to a device testdevice with status <Status>
    And the get firmware version request refers to an organisation that is authorised
   When the get firmware version request is received
   Then a get firmware version oslp message is sent to device testdevice should be <Is Message Sent>
	    
Examples:
			| Status       | Oslp Response | Is Message Sent |
			| unknown      | N/A           | false           |
			| unregistered | N/A           | false           |
			
# Because the device message status is not implemented in the protocol, the following tests cannot be performed:
#     | active       | FAILURE       | true            | DEVICEMESSAGEFAILEDEXCEPTION   |
#     | active       | REJECTED      | true            | DEVICEMESSAGEREJECTEDEXCEPTION |

#!2 Scenario: Receive A Get Firmware Response Request
#-| scenario | get firmware response input values | correlationid || deviceid || isfound || deviceid2 || qresult || qdescription || firmwareversion || result || description|
#|given|a get firmware version response request with correlationId @correlationid and deviceId @deviceid                                                                                                                |
#|and  |a get firmware version response message with correlationId @correlationid, deviceId @deviceid2, qresult @qresult, qdescription @qdescription and firmwareversion @firmwareversion is found in the queue @isfound|
#|when |the get firmware version response request is received                                                                                                                                                           |
#|then |the get firmware version response request should return a firmware response with result @result, description @description and firmwareversion @firmwareversion                                                  |

#-| get firmware response input values |
#|correlationid|deviceid|isfound|deviceid2|qresult|qdescription    |firmwareversion|result   |description     |
#|cid-01       |dvc-01  |true   |dvc-01   |OK     |                |R01            |OK       |                |
#|cid-02       |dvc-01  |false  |         |       |                |               |NOT_FOUND|                |
#|cid-03       |dvc-01  |true   |dvc-01   |NOT_OK |VALIDATION ERROR|               |NOT_OK   |VALIDATION ERROR|

#!2 Parameters:
# * correlationId: correlation Id returned from a get firmware request
# * content: message content
# * result: OK or exception
Scenario Outline: Receive a get firmware response request
	Given a get firmware version response request
	    | Field         | Value           |
	    | correlationId | <CorrelationId> | 
	    | deviceId      | <DeviceId>      |
	  And a get firmware version response message <ExistsInQueue> in the queue
      | Field           | Value             |
      | correlationId   | <CorrelationId>   |
      | deviceId        | <DeviceId2>       | 
      | qresult         | <qresult>         |
      | qdescription    | <qdescription>    |
      | firmwareversion | <Firmwareversion> |
   When receiving a get firmware version response request
	    | Field    | Value     |
	    | deviceId | device-01 |
   Then ensure the get firmware version response request contains
      | Field           | Value             | 
      | result          | <result>          |
      | description     | <description>     |
      | firmwareversion | <firmwareversion> |                                                  |
      
Examples:
      | CorrelationId | DeviceId | ExistsInQueue | DeviceId2 | qresult | qdescription     | Firmwareversion | Result    | Description      |
      | cid-01        | dvc-01   | true          | dvc-01    | OK      |                  | v0.1            | OK        |                  |
      | cid-02        | dvc-01   | false         |           |         |                  |                 | NOT_FOUND |                  |
      | cid-03        | dvc-01   | true          | dvc-01    | NOT_OK  | VALIDATION ERROR |                 | NOT_OK    | VALIDATION ERROR |

#!2 Scenario: Successfully send firmware update request

#-| scenario | firmware success matrix | firmwareDomainConfig | | firmwarePathConfig | | firmwareExtensionConfig | | organisation | | device | | firmwareName | | firmwareDomain | | firmwareUrl | | ismessagesent | | result ||
#|given|a firmware update request for device @device, firmwareName @firmwareName                                                                             |
#|and  |a firmware location configuration with @firmwareDomainConfig, @firmwarePathConfig, and @firmwareExtensionConfig                                      |
#|and  |an OSGP client @organisation                                                                                                                          |
#|and  |an authorized device @device                                                                                                                         |
#|and  |the update firmware oslp message from the device                                                                                                     |
#|when |the update firmware request is received                                                                                                              |
#|then |the update firmware request should return an async response with a correlationId and deviceId @device                                                |
#|and  |an update firmware oslp message is sent to device @device with firmwareName @firmwareName and firmwareDomain @firmwareDomain should be @ismessagesent|
#|and  |an ovl update firmware message with result @result should be sent to the ovl out queue                                                               |



#-| firmware success matrix |
#|firmwareDomainConfig               |firmwarePathConfig|firmwareExtensionConfig|organisation   |device |firmwareName|firmwareDomain          |firmwareUrl           |ismessagesent|result|
#|flexovltest.cloudapp.net           |/firmware         |hex                    |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/firmware/AME-v0.1.hex|true         |OK    |
#|flexovltest.cloudapp.net           |firmware          |hex                    |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/firmware/AME-v0.1.hex|true         |OK    |
#|flexovltest.cloudapp.net           |firmware/         |hex                    |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/firmware/AME-v0.1.hex|true         |OK    |
#|flexovltest.cloudapp.net           |/firmware/        |hex                    |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/firmware/AME-v0.1.hex|true         |OK    |
#|flexovltest.cloudapp.net           |/firmware         |.hex                   |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/firmware/AME-v0.1.hex|true         |OK    |
#|flexovltest.cloudapp.net/          |/firmware         |hex                    |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/firmware/AME-v0.1.hex|true         |OK    |
#|!-http://flexovltest.cloudapp.net-!|/firmware         |hex                    |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/firmware/AME-v0.1.hex|true         |OK    |
#|!-ftp://flexovltest.cloudapp.net-! |/firmware         |hex                    |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/firmware/AME-v0.1.hex|true         |OK    |
#|flexovltest.cloudapp.net           |                  |hex                    |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/AME-v0.1.hex         |true         |OK    |
#|flexovltest.cloudapp.net           |/firmware         |                       |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/firmware/AME-v0.1    |true         |OK    |
Scenario Outline: Update firmware version
  Given a device 
      | Field           | Value             |
      | DeviceIdentification        | testdevice        |
      | firmwareVersion | <FirmwareVersion> |
    And a firmware location configuration 
      | Field                   | Value                     |
		  | firmwareDomainConfig    | <firmwareDomainConfig>    |
		  | firmwarePathConfig      | <firmwarePathConfig>      |
		  | firmwareExtensionConfig | <firmwareExtensionConfig> |
    And an OSGP client <organisation>                                                                                                                          
    And an authorized device <device>                                                                                                                         
    And the update firmware oslp message from the device                                                                                                     
   When receiving an update firmware request
      | Field    | Value      |
      | deviceId | testdevice |                                                                                                          
   Then the update firmware request should return an async response with a correlationId and deviceId <device>                                                
    And an update firmware oslp message is sent to device <device> with firmwareName <firmwareName> and firmwareDomain <firmwareDomain> should be <ismessagesent>
    And an ovl update firmware message with result <result> should be sent to the ovl out queue                                                               

Examples:
      | firmwareDomainConfig               |firmwarePathConfig|firmwareExtensionConfig|organisation   |device |firmwareName|firmwareDomain          |firmwareUrl           |ismessagesent|result|
      | flexovltest.cloudapp.net           |/firmware         |hex                    |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/firmware/AME-v0.1.hex|true         |OK    |
      | flexovltest.cloudapp.net           |firmware          |hex                    |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/firmware/AME-v0.1.hex|true         |OK    |
      | flexovltest.cloudapp.net           |firmware/         |hex                    |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/firmware/AME-v0.1.hex|true         |OK    |
      | flexovltest.cloudapp.net           |/firmware/        |hex                    |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/firmware/AME-v0.1.hex|true         |OK    |
      | flexovltest.cloudapp.net           |/firmware         |.hex                   |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/firmware/AME-v0.1.hex|true         |OK    |
      | flexovltest.cloudapp.net/          |/firmware         |hex                    |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/firmware/AME-v0.1.hex|true         |OK    |
      | !-http://flexovltest.cloudapp.net-!|/firmware         |hex                    |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/firmware/AME-v0.1.hex|true         |OK    |
      | !-ftp://flexovltest.cloudapp.net-! |/firmware         |hex                    |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/firmware/AME-v0.1.hex|true         |OK    |
      | flexovltest.cloudapp.net           |                  |hex                    |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/AME-v0.1.hex         |true         |OK    |
      | flexovltest.cloudapp.net           |/firmware         |                       |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/firmware/AME-v0.1    |true         |OK    |

#!2 Scenario: Incorrect firmware configuration
#
#-| scenario | firmware error matrix | firmwareDomainConfig | | firmwarePathConfig | | firmwareExtensionConfig | | organisation | | device | | firmwareName | | error || ismessagesent | | result ||
#|given|a firmware update request for device @device, firmwareName @firmwareName                                                                             |
#|and  |a firmware location configuration with @firmwareDomainConfig, @firmwarePathConfig, and @firmwareExtensionConfig                                      |
#|and  |an OSGP client @organisation                                                                                                                          |
#|and  |an authorized device @device                                                                                                                         |
#|and  |the update firmware oslp message from the device                                                                                                     |
#|when |the update firmware request is received                                                                                                              |
#|then |the update firmware request should return an async response with a correlationId and deviceId @device                                                |
#|and  |an update firmware oslp message is sent to device @device with firmwareName @firmwareName and firmwareDomain @firmwareDomain should be @ismessagesent|
#|and  |an ovl update firmware message with result @result should be sent to the ovl out queue                                                               |
#
#
#-| firmware error matrix |
#|firmwareDomainConfig    |firmwarePathConfig|firmwareExtensionConfig|organisation   |device |firmwareName|error                     |ismessagesent|result|
#|                        |/firmware         |hex                    |ORGANISATION-01|device1|AME-v0.1    |!-ConfigurationException-!|false        |NOT OK|
Scenario Outline: Update firmware configuration with incorrect data
  Given a device 
      | Field           | Value             |
      | DeviceIdentification        | testdevice        |
      | firmwareVersion | <FirmwareVersion> |
    And a firmware location configuration 
      | Field                   | Value                     |
		  | firmwareDomainConfig    | <firmwareDomainConfig>    |
		  | firmwarePathConfig      | <firmwarePathConfig>      |
		  | firmwareExtensionConfig | <firmwareExtensionConfig> |    
		And an OSGP client <organisation>
    And an authorized device <device>
    And the update firmware oslp message from the device
   When the update firmware request is received
      | Field    | Value      |
      | deviceId | testdevice |
   Then the update firmware request should return an async response with a correlationId and deviceId <device>
    And an update firmware oslp message is sent to device <device> with firmwareName <firmwareName> and firmwareDomain <firmwareDomain> should be <ismessagesent>
    And an ovl update firmware message with result <result> should be sent to the ovl out queue

Examples:
      | firmwareDomainConfig |firmwarePathConfig|firmwareExtensionConfig|organisation   |device |firmwareName|error                     |ismessagesent|result|
      |                      |/firmware         |hex                    |ORGANISATION-01|device1|AME-v0.1    |!-ConfigurationException-!|false        |NOT OK|

#!2 Scenario: Invalid firmware update request
#
#-| scenario | firmware error matrix | firmwareDomainConfig | | firmwarePathConfig | | firmwareExtensionConfig | | organisation | | device | | firmwareName | | error || ismessagesent | | result ||
#|given|a firmware update request for device @device, firmwareName @firmwareName                                                                             |
#|and  |a firmware location configuration with @firmwareDomainConfig, @firmwarePathConfig, and @firmwareExtensionConfig                                      |
#|and  |an OSGP client @organisation                                                                                                                          |
#|and  |an authorized device @device                                                                                                                         |
#|and  |the update firmware oslp message from the device                                                                                                     |
#|when |the update firmware request is received                                                                                                              |
#|then |the update firmware request should return a validation error                                                |
#|and  |an update firmware oslp message is sent to device @device with firmwareName @firmwareName and firmwareDomain @firmwareDomain should be @ismessagesent|
#
#
#-| firmware error matrix |
#|firmwareDomainConfig    |firmwarePathConfig|firmwareExtensionConfig|organisation   |device |firmwareName|error                     |ismessagesent|result|
#|flexovltest.cloudapp.net|/firmware         |hex                    |ORGANISATION-01|device1|            |!-ValidationException-!   |false        |NOT OK|
Scenario Outline: Update firmware configuration with invalid data
  Given a device
      | Field        | Value          |
      | DeviceIdentification     | testdevice     |
      | firmwareName | <firmwareName> |                                                                             
    And a firmware location configuration
      | Field                | Value                     |
      | firmwareDomainConfig | <firmwareDomainConfig>    |
      | firmwarePathConfig   | <firmwareExtensionConfig> |
    And an OSGP client <organisation>
    And an authorized device <device>
    And the update firmware oslp message from the device
   When the update firmware request is received
      | Field    | Value      |
      | deviceId | testdevice |
   Then the update firmware request should return a validation error                                                |
    And an update firmware oslp message is sent to device <device> with firmwareName <firmwareName> and firmwareDomain <firmwareDomain> should be <ismessagesent>
 
Examples:
      | firmwareDomainConfig     | firmwarePathConfig | firmwareExtensionConfig|organisation   |device |firmwareName|error                     |ismessagesent|result|
      | flexovltest.cloudapp.net | /firmware          | hex                    |ORGANISATION-01|device1|            |!-ValidationException-!   |false        |NOT OK|



#!2 Scenario: Receive An Update Firmware Response Request
#-| scenario | update firmware response input values | correlationid || deviceid || isfound || deviceid2 || qresult || qdescription || result || description|
#|given|an update firmware response request with correlationId @correlationid and deviceId @deviceid                                                                              |
#|and  |an update firmware response message with correlationId @correlationid, deviceId @deviceid2, qresult @qresult and qdescription @qdescription is found in the queue @isfound|
#|when |the update firmware response request is received                                                                                                                          |
#|then |the update firmware response request should return a firmware response with result @result and description @description                                                   |
#
#-| update firmware response input values |
#|correlationid|deviceid|isfound|deviceid2|qresult|qdescription    |result   |description     |
#|cid-01       |dvc-01  |true   |dvc-01   |OK     |                |OK       |                |
#|cid-02       |dvc-01  |false  |         |       |                |NOT_FOUND|                |
#|cid-03       |dvc-01  |true   |dvc-01   |NOT_OK |VALIDATION ERROR|NOT_OK   |VALIDATION ERROR|
#
#!2 Parameters:
# * correlationId: correlation Id returned from a update firmware request
# * content: message content
# * result: OK or exception
Scenario Outline: Recieve an update firmware response request
  Given an update firmware response request with correlationId <correlationid> and deviceId <deviceid>
    And an update firmware response message with correlationId <correlationid>, deviceId <deviceid2>, qresult <qresult> and qdescription <qdescription> is found in the queue <isfound>
   When the update firmware response request is received
   Then the update firmware response request should return a firmware response with result <result> and description <description>
  
Examples:
      | Correlationid | DeviceId | IsFound | DeviceId2 | qresult | qdescription     | Result    | Description      |
      | cid-01        | dvc-01   | true    | dvc-01    | OK      |                  | OK        |                  |
      | cid-02        | dvc-01   | false   |           |         |                  | NOT_FOUND |                  |
      | cid-03        | dvc-01   | true    | dvc-01    | NOT_OK  | VALIDATION ERROR | NOT_OK    | VALIDATION ERROR |