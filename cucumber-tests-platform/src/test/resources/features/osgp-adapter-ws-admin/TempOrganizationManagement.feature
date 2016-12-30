Feature: Temp Organization management
  As a ...
  I want to ...
  In order ...

  # This test doesn't work because the backend doesn't remove the device.
  Scenario Outline: Remove A Device
  		Given a device
  			| DeviceIdentification | <DeviceIdentification> |
  		When receiving a remove device request
  			| DeviceIdentification | <DeviceIdentification> |
  		Then the remove device response is successfull
  		And the device with id "<DeviceIdentification>" does not exists
  
  		Examples:
  			| DeviceIdentification |
  			| TEST1024000000001    |
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