@ProtocolAdapterIec60870 @TestThis
Feature: OSGP Protocol Adapter IEC60870 - Receive Measurements
    In order to provide measurement data to osgp clients
    As a protocol adapter
    I want to be able to process measurement data from IEC60870 devices
    

Scenario Outline: Receive an ASDU
    Given an existing connection with an IEC60870 device
    When I receive an ASDU of type "<type_id>" from the IEC60870 device
    Then I should send a measurement report of type "<type_id>" to the platform
    And I should send a log item
    
    Examples: 
        |type_id  |
        |M_ME_TF_1|
