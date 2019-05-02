@ProtocolAdapterIec60870
Feature: OSGP Protocol Adapter IEC60870 - Receive Interrogation Command Responses
    In order to correctly handle device connections
    As a protocol adapter
    I want to be able to process interrogation command responses from IEC60870 devices
    

Scenario Outline: Receive an interrogation command response ASDU
    Given an existing connection with an IEC60870 device
    When I receive an ASDU of type "<type_id>" from the IEC60870 device
    Then I should send a log item with a message containing type "<type_id>"
    
    Examples: 
        |type_id  |
        |C_IC_NA_1|
