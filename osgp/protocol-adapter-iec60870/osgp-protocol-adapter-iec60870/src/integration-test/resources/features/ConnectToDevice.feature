@ProtocolAdapterIec60870
Feature: OSGP Protocol Adapter IEC60870 - ConnectToDevice
    In order to monitor and control devices
    As a protocol adapter
    I want to be able to connect to IEC60870 devices
 
Scenario: Connect to an IEC60870 device
    Given an IEC60870 device
    And the IEC60870 device is not connected
    When I receive a request for the IEC60870 device
    Then I should connect to the IEC60870 device
    And I should cache the connection with the IEC60870 device