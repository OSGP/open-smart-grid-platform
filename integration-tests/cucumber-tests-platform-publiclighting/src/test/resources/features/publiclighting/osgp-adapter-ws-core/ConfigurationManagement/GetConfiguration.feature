# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@PublicLighting @Platform @CoreConfigurationManagement
Feature: CoreConfigurationManagement GetConfiguration
  As a ...
  I want to ...
  In order to ...

  @OslpMockServer
  Scenario Outline: Get configuration of a device
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    And the device returns a get configuration status over "<Protocol>"
      | Status            | OK                  |
      | LightType         | <LightType>         |
      | DcLights          | <DcLights>          |
      | DcMap             | <DcMap>             |
      | RelayConf         | <RelayConf>         |
      | PreferredLinkType | <PreferredLinkType> |
      | OsgpIpAddress     | <OsgpIpAddress>     |
      | OsgpPort          | <OsgpPort>          |
    When receiving a get configuration request
      | DeviceIdentification | TEST1024000000001 |
    Then the get configuration async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a get configuration "<Protocol>" message is sent to device "TEST1024000000001"
    And the platform buffers a get configuration response message for device "TEST1024000000001"
      | Result            | OK                  |
      | Description       |                     |
      | LightType         | <LightType>         |
      | DcLights          | <DcLights>          |
      | DcMap             | <DcMap>             |
      | RelayConf         | <RelayConf>         |
      | PreferredLinkType | <PreferredLinkType> |
      | OsgpIpAddress     | <OsgpIpAddress>     |
      | OsgpPort          | <OsgpPort>          |

    Examples:
      | Protocol    | LightType               | DcLights | DcMap   | RelayConf  | PreferredLinkType | OsgpIpAddress | OsgpPort |
      | OSLP ELSTER | RELAY                   |          |         |            |                   | 10.20.30.40   |    12122 |
      | OSLP ELSTER | RELAY                   |          |         | 1,1,LIGHT  |                   | 10.20.30.40   |    12122 |
      | OSLP ELSTER | ONE_TO_TEN_VOLT         |          |         |            |                   | 10.20.30.40   |    12122 |
      | OSLP ELSTER | ONE_TO_TEN_VOLT_REVERSE |          |         |            |                   | 10.20.30.40   |    12123 |
      | OSLP ELSTER | DALI                    |        2 | 1,2;2,1 |            |                   | 10.20.30.40   |    12123 |
      | OSLP ELSTER |                         |          |         |            |                   | 10.20.30.40   |    12123 |
      | OSLP ELSTER |                         |          |         |            | GPRS              | 10.20.30.50   |    12122 |
      | OSLP ELSTER | DALI                    |          |         |            |                   | 10.20.30.50   |    12122 |
      | OSLP ELSTER |                         |          |         |            |                   | 10.20.30.50   |    12122 |
      | OSLP ELSTER |                         |          |         |            |                   | 10.20.30.50   |    12123 |
      | OSLP ELSTER | RELAY                   |          |         | 1,1,LIGHT  | CDMA              | 10.20.30.50   |    12123 |
      | OSLP ELSTER | RELAY                   |          |         | 1,1,LIGHT  | ETHERNET          | 10.20.30.50   |    12123 |

  Scenario: Get configuration data with unknown device
    When receiving a get configuration request
      | DeviceIdentification | TEST1024000000001 |
    Then the get configuration async response contains soap fault
      | Message | UNKNOWN_DEVICE |

  @OslpMockServer @GetConfigurationFailed
  Scenario Outline: Failed get configuration of a device
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    And the device returns a get configuration status over "<Protocol>"
      | Status            | FAILURE   |
      | LightType         | RELAY     |
      | DcLights          |           |
      | DcMap             |           |
      | RelayConf         | 1,1,LIGHT |
      | PreferredLinkType | ETHERNET  |
    When receiving a get configuration request
      | DeviceIdentification | TEST1024000000001 |
    Then the get configuration async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a get configuration "<Protocol>" message is sent to device "TEST1024000000001"
    And the platform buffers a get configuration response message for device "TEST1024000000001" contains soap fault
      | Message | Device reports failure |

    Examples:
      | Protocol    |
      | OSLP ELSTER |

  @OslpMockServer
  Scenario Outline: Rejected get configuration of a device
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    And the device returns a get configuration status over "<Protocol>"
      | Status            | REJECTED  |
      | LightType         | RELAY     |
      | DcLights          |           |
      | DcMap             |           |
      | RelayConf         | 1,1,LIGHT |
      | PreferredLinkType | ETHERNET  |
    When receiving a get configuration request
      | DeviceIdentification | TEST1024000000001 |
    Then the get configuration async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a get configuration "<Protocol>" message is sent to device "TEST1024000000001"
    And the platform buffers a get configuration response message for device "TEST1024000000001" contains soap fault
      | Message | Device reports rejected |

    Examples:
      | Protocol    |
      | OSLP ELSTER |
