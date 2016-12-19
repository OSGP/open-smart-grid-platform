Feature: Adhoc Management
  In order to ... 
  As a platform 
  I want to asynchronously handle set light requests

  @OslpMockServer
  Scenario Outline: Get Status from a device
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Status               | Active            |
      | RelayType            | <RelayType>       |
    And the device returns a get status response over OSLP
      | PreferredLinkType  | <PreferredLinkType>      |
      | ActualLinkType     | <ActualLinkType>         |
      | LightType          | <LightType>              |
      | EventNotifications | <EventNotificationTypes> |
      | LightValues        | <LightValues>            |
    When receiving a get status request
      | DeviceIdentification | TEST1024000000001 |
    Then the get status async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a get status OSLP message is sent to device "TEST1024000000001"
    And the platform buffers a get status response message for device "TEST1024000000001"
      | Result            | <Result>                    |
      | PreferredLinkType | <ExpectedPreferredLinkType> |
      | ActualLinkType    | <ExpectedActualLinkType>    |
      | LightType         | <ExpectedLightType>         |

    Examples: 
      | RelayType | PreferredLinkType | ActualLinkType | LightType  | EventNotificationTypes | LightValues | Result | ExpectedPreferredLinkType | ExpectedActualLinkType | ExpectedLightType |
      | LIGHT     | LINK_NOT_SET      | LINK_NOT_SET   | LT_NOT_SET |                        |             | OK     |                           |                        |                   |
     # | LIGHT     | CDMA              | GPRS           | DALI       | DIAG_EVENTS            | 1;true;100  | OK     | CDMA                      | GPRS                   | DALI              |     
     # | LIGHT		CDMA		GPRS		DALI		COMM_EVENTS		CDMA		GPRS		DALI		0		true			true		OK		
     # | LIGHT		CDMA		CDMA		RELAY		HARDWARE_FAILURE		CDMA		CDMA		RELAY		1		true			true		OK		
     # | LIGHT		CDMA		CDMA		RELAY		LIGHT_EVENTS,TARIFF_EVENTS		CDMA		CDMA		RELAY		1		true			true		OK		
     # | LIGHT		CDMA		CDMA		DALI		DIAG_EVENTS,HARDWARE_FAILURE,LIGHT_EVENTS,TARIFF_EVENTS,MONITOR_EVENTS,FIRMWARE_EVENTS,COMM_EVENTS		CDMA		CDMA		DALI		1		false			true		OK		
     # | LIGHT		CDMA		ETHERNET		ONE_TO_TEN_VOLT			CDMA		ETHERNET		ONE_TO_TEN_VOLT		1		true		1		true		OK		
     # | LIGHT		CDMA		CDMA		ONE_TO_TEN_VOLT_REVERSE			CDMA		CDMA		ONE_TO_TEN_VOLT_REVERSE		1		true		75		true		OK		
     # | LIGHT		CDMA		CDMA		DALI			CDMA		CDMA		DALI		1		true		100		true		OK		
     # | TARIFF		LINK_NOT_SET		LINK_NOT_SET		LT_NOT_SET						0		true			true		OK		
     # | TARIFF		CDMA		GPRS		DALI		DIAG_EVENTS		CDMA		GPRS		DALI		0		true			true		OK		
     # | TARIFF		CDMA		GPRS		DALI		COMM_EVENTS		CDMA		GPRS		DALI		0		true			true		OK		
     # | TARIFF		CDMA		CDMA		RELAY		HARDWARE_FAILURE		CDMA		CDMA		RELAY		1		true			true		OK		
     # | TARIFF		CDMA		CDMA		RELAY		LIGHT_EVENTS,TARIFF_EVENTS		CDMA		CDMA		RELAY		1		true			true		OK		
     # | TARIFF		CDMA		CDMA		DALI		DIAG_EVENTS,HARDWARE_FAILURE,LIGHT_EVENTS,TARIFF_EVENTS,MONITOR_EVENTS,FIRMWARE_EVENTS,COMM_EVENTS		CDMA		CDMA		DALI		1		false			true		OK		
     # | TARIFF		CDMA		ETHERNET		ONE_TO_TEN_VOLT			CDMA		ETHERNET		ONE_TO_TEN_VOLT		1		true		1		true		OK		
     # | TARIFF		CDMA		CDMA		ONE_TO_TEN_VOLT_REVERSE			CDMA		CDMA		ONE_TO_TEN_VOLT_REVERSE		1		true		75		true		OK		
     # | TARIFF		CDMA		CDMA		DALI			CDMA		CDMA		DALI		1		true		100		true		OK		
     # | TARIFF_REVERSED		LINK_NOT_SET		LINK_NOT_SET		LT_NOT_SET						0		true			true		OK		
     # | TARIFF_REVERSED		CDMA		GPRS		DALI		DIAG_EVENTS		CDMA		GPRS		DALI		0		true			true		OK		
     # | TARIFF_REVERSED		CDMA		GPRS		DALI		COMM_EVENTS		CDMA		GPRS		DALI		0		true			true		OK		
     # | TARIFF_REVERSED		CDMA		CDMA		RELAY		HARDWARE_FAILURE		CDMA		CDMA		RELAY		1		true			true		OK		
     # | TARIFF_REVERSED		CDMA		CDMA		RELAY		LIGHT_EVENTS,TARIFF_EVENTS		CDMA		CDMA		RELAY		1		true			true		OK		
     # | TARIFF_REVERSED		CDMA		CDMA		DALI		DIAG_EVENTS,HARDWARE_FAILURE,LIGHT_EVENTS,TARIFF_EVENTS,MONITOR_EVENTS,FIRMWARE_EVENTS,COMM_EVENTS		CDMA		CDMA		DALI		1		false			true		OK		
     # | TARIFF_REVERSED		CDMA		ETHERNET		ONE_TO_TEN_VOLT			CDMA		ETHERNET		ONE_TO_TEN_VOLT		1		true		1		true		OK		
     # | TARIFF_REVERSED		CDMA		CDMA		ONE_TO_TEN_VOLT_REVERSE			CDMA		CDMA		ONE_TO_TEN_VOLT_REVERSE		1		true		75		true		OK		
     # | TARIFF_REVERSED		CDMA		CDMA		DALI			CDMA		CDMA		DALI		1		true		100		true		OK		
