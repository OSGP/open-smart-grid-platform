Feature: Organisation management
  As a ...
  I want to manage the Organisations in the platform
  In order ...

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
#			| DeviceIdentification | Result |
#			| TEST1024000000001    | OK     |

# Start/Stop Device scenario's			
#	@OslpMockServer
#	Scenario Outline: Stop Device
#		Given an oslp device
#			| DeviceIdentification | <DeviceIdentification> |
#			| Status               | <Status>               |
#		And the device returns a stop device response "<Result>" over OSLP
#		When receiving a stop device request
#			| DeviceIdentification | <DeviceIdentification> |
#		Then the stop device async response contains
      #| DeviceIdentification | <DeviceIdentification> |
    #And a stop device OSLP message is sent to device "<DeviceIdentification>"
#		And the platform buffers a stop device response message for device "<DeviceIdentification>"
#		
#		Examples:
#			| DeviceIdentification | Status | Result |
#			| TEST1024000000001    | active | OK     |

#	@OslpMockServer
  #Scenario Outline: Succesfull Retrieval Of Status Values
    #Given an oslp device
      #| DeviceIdentification | <DeviceIdentification> |
      #| Status               | <Status>               |
    #And the device returns a get status response "<Result>" over OSLP
    #When receiving a get status request
      #| DeviceIdentification | <DeviceIdentification> |
    #Then the get status async response contains
      #| DeviceIdentification | <DeviceIdentification> |
    #And a get status OSLP message is sent to device "<DeviceIdentification>"
    #And the platform buffers a get status response message for device "<DeviceIdentification>"
#
    #Examples:
      #| DeviceIdentification | Status | Result |
      #| TEST1024000000001    | active | OK     |
      
  # SetEventNotification scenario's
	@OslpMockServer
	Scenario Outline: Set Event Notifications
		Given an oslp device
			| DeviceIdentification | <DeviceIdentification> |
		And the device returns an event notification "<Result>" over OSLP
    When receiving a set event notification message request on OSGP
    	| Event                | <Event>                |
    	| DeviceIdentification | <DeviceIdentification> |
    Then the set event notification async response contains
      | DeviceIdentification | <DeviceIdentification> |
    #And a set event notification OSLP message is sent to device "<DeviceIdentification>"
#		And the platform buffers a set event notification response message for device "<DeviceIdentification>"
    
    Examples:
    	| DeviceIdentification | Result | Event                         |
      | TEST1024000000001    | OK     | LIGHT_EVENTS, SECURITY_EVENTS |
      #| TEST1024000000001    | OK     | SECURITY_EVENTS               |