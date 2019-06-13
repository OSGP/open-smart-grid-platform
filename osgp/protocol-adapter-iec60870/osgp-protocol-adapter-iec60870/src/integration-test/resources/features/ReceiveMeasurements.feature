@ProtocolAdapterIec60870
Feature: OSGP Protocol Adapter IEC60870 - Receive Measurements
    In order to provide measurement data to OSGP clients
    As a protocol adapter
    I want to be able to process incoming measurement data from IEC60870 devices
    

Scenario Outline: Receive an ASDU
    Given an existing connection with an IEC60870 device
    When I receive an ASDU of type "<type_id>" from the IEC60870 device
    Then I should send a measurement report of type "<type_id>" to the platform
    And I should send a log item with a message containing type "<type_id>"
    
    Examples: 
        |type_id  |
        |M_ME_TF_1|
