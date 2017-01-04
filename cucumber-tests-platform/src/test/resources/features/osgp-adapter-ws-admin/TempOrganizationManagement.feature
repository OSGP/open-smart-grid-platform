Feature: Temp Organization management
  As a ...
  I want to ...
  In order ...

  # This test doesn't work because the backend doesn't remove the device.
  Scenario Outline: Remove A Device
  		Given a device
  			| DeviceIdentification | TEST1024000000001 |
  		When receiving a remove device request
  			| DeviceIdentification | TEST1024000000001 |
  		Then the remove device response is successfull
  		And the device with id "TEST1024000000001" does not exists
