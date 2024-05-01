# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@PublicLighting @Platform @PublicLightingAdhocManagement @SetLight
Feature: PublicLightingAdhocManagement Set Light
  As a platform 
  I want to asynchronously handle set light requests
  In order to ...

  Background:
    Given an organization
      | OrganizationIdentification | LianderNetManagement |
      | Name                       | LianderNetManagement |
      | PlatformFunctionGroup      | ADMIN                |
      | Domains                    | COMMON               |

  @OslpMockServer @Jelle
  Scenario Outline: Receive A Set Light Request With A Single Light Value
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
    And the device returns a set light response "OK" over "<Protocol>"
    And the device returns a resume schedule response "OK" over "<Protocol>"
    When receiving a set light request
      | DeviceIdentification | TEST1024000000001 |
      | Index                | <Index>           |
      | On                   | <On>              |
      | DimValue             | <DimValue>        |
    Then the set light async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set light "<Protocol>" message with one light value is sent to the device
      | Protocol | <Protocol> |
      | Index    | <Index>    |
      | On       | <On>       |
      | DimValue | <DimValue> |
    And a resume schedule "<Protocol>" message is sent to device "TEST1024000000001"
      | Protocol  | <Protocol> |
      | Index     |          0 |
      | Immediate | false      |
    And the platform buffers a set light response message for device "TEST1024000000001"
      | Result | OK |
    And the event is stored
      | DeviceIdentification | TEST1024000000001 |
      | Event                | <Event>           |
      | Index                | <Index>           |

    Examples: 
      | Protocol    | Index | On    | DimValue | Event                  |
      | OSLP ELSTER |     0 | true  |          | LIGHT_EVENTS_LIGHT_ON  |
#      | OSLP ELSTER |     2 | true  |          | LIGHT_EVENTS_LIGHT_ON  |
#      | OSLP ELSTER |     6 | true  |          | LIGHT_EVENTS_LIGHT_ON  |
#      | OSLP ELSTER |     2 | false |          | LIGHT_EVENTS_LIGHT_OFF |
#      | OSLP ELSTER |     2 | true  |        1 | LIGHT_EVENTS_LIGHT_ON  |
#      | OSLP ELSTER |     2 | true  |       75 | LIGHT_EVENTS_LIGHT_ON  |
#      | OSLP ELSTER |     2 | true  |      100 | LIGHT_EVENTS_LIGHT_ON  |

  Scenario Outline: Receive A Set Light Request With An Invalid Single Light Value
    Given a device
      | DeviceIdentification | TEST1024000000001 |
    When receiving a set light request
      | DeviceIdentification | TEST1024000000001 |
      | Index                | <Index>           |
      | On                   | true              |
      | DimValue             | <DimValue>        |
    Then the set light response contains soap fault
      | MESSAGE | Validation error |

    Examples: 
      | Index | DimValue |
      |     1 |        0 |
      |    -1 |        1 |
      |     7 |        1 |
      |     1 |       -1 |
      |     1 |      101 |

  Scenario: Receive A Set Light Request With An Invalid Single Light Value due to the On value
    Given a device
      | DeviceIdentification | TEST1024000000001 |
    When receiving a set light request
      | DeviceIdentification | TEST1024000000001 |
      | Index                |                 1 |
      | On                   | false             |
      | DimValue             |                75 |
    Then the set light response contains soap fault
      | Message | VALIDATION_ERROR |

  @OslpMockServer
  Scenario Outline: Receive A Set Light Request With Multiple Light Values
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | relayType            | LIGHT             |
    And the device returns a set light response "OK" over "<Protocol>"
    And the device returns a resume schedule response "OK" over "<Protocol>"
    When receiving a set light request with "<nofLightValues>" light values
      | DeviceIdentification | TEST1024000000001 |
    Then the set light async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set light "<Protocol>" message with "<nofLightValues>" lightvalues is sent to the device
    And a resume schedule "<Protocol>" message is sent to device "TEST1024000000001"
      | Protocol  | <Protocol> |
      | Index     |          0 |
      | Immediate | false      |
    And the platform buffers a set light response message for device "TEST1024000000001"
      | Result | OK |
    And the events are stored
      | DeviceIdentification | TEST1024000000001       |
      | Events               | <EventTypes>            |
      | Indexes              | <Indexes>               |
      | NumberOfEvents       | <NumberOfEvents>        |
      | NumberOfStatuses     | <NumberOfRelayStatuses> |

    Examples: 
      | Protocol    | nofLightValues | EventTypes                                                          | Indexes | NumberOfEvents | NumberOfRelayStatuses |
      | OSLP ELSTER |              2 | LIGHT_EVENTS_LIGHT_ON, LIGHT_EVENTS_LIGHT_ON                        |     2,3 |              2 |                     2 |
      | OSLP ELSTER |              3 | LIGHT_EVENTS_LIGHT_ON, LIGHT_EVENTS_LIGHT_ON, LIGHT_EVENTS_LIGHT_ON |   2,3,4 |              3 |                     3 |

  Scenario Outline: Receive A Set Light Request With Invalid Multiple Light Values
    Given a device
      | DeviceIdentification | TEST1024000000001 |
    When receiving a set light request with "<NofValidLightValues>" valid lightvalues and "<NofInvalidLightValues>" invalid lightvalues
      | DeviceIdentification | TEST1024000000001 |
    Then the set light response contains soap fault
      | MESSAGE | <Message> |

    Examples: 
      | NofValidLightValues | NofInvalidLightValues | Message          |
      |                   0 |                     0 | Validation error |
      |                   7 |                     0 | Validation error |
      |                   2 |                     1 | VALIDATION_ERROR |
