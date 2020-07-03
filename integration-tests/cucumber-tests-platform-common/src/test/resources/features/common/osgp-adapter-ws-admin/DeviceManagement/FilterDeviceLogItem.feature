@Common @Platform @AdminDeviceManagement
Feature: Filter DeviceLogItem
  As a ...
  I want to be filter the DeviceLogItem 
  In order to ...
  
	Scenario: Filter DeviceLogItem only on device identification
		Given I have 60 device log items
      | DeviceIdentification | DEV-1 |
    When receiving a filter message log on device identification request
    	| DeviceIdentification       | DEV-1      |
    Then the messages response contains 60 correct messages
    	| DeviceIdentification       | DEV-1      |
    
			