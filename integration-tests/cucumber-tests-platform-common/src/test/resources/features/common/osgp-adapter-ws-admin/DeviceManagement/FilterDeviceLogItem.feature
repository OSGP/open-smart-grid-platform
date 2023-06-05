# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@Common @Platform @AdminDeviceManagement
Feature: Filter device messages
  As an operator
  I want to be filter and sort the device messages 
  In order to analyse problems and check messages

  Background: 
    Given I have 6 device log items
      | DeviceIdentification | DEV-1                |
      | Organization         | Liander              |
    And I have 5 device log items
      | DeviceIdentification | DEV-2                |
      | Organization         | Liander              |
    And I have 4 device log items
      | DeviceIdentification | DEV-11               |
      | Organization         | TEST                 |

  Scenario: No filters set so getting all the messages
    When receiving a message log request without a filter
    Then the messages response contains 15 messages

  Scenario: Use wildcard filter for Device Identification
    When receiving a filter message log request
      | DeviceIdentification | DEV-1* |
    Then the messages response contains 10 messages for devices
      | DEV-1  |
      | DEV-11 |

  Scenario: Use wildcard filter for Organisation Identification
    When receiving a filter message log request
      | OrganizationIdentification | Li?nder |
    Then the messages response contains 11 messages for devices
      | DEV-1 |
      | DEV-2 |

  Scenario: Filter messages only on Organisation Identification
    When receiving a filter message log request
      | OrganizationIdentification | Liander |
    Then the messages response contains 11 messages for
      | OrganizationIdentification | Liander |

  Scenario: Filter messages on Organisation Identification and Device Identification
    When receiving a filter message log request
      | DeviceIdentification       | DEV-2   |
      | OrganizationIdentification | Liander |
    Then the messages response contains 5 messages for
      | DeviceIdentification       | DEV-2   |
      | OrganizationIdentification | Liander |

  Scenario: Sort messages by Device Identification
    When receiving a filter message log request
      | SortDir  | DESC                 |
      | SortedBy | deviceIdentification |
    Then the messages response contains 15 messages ordered descending by device identification

  Scenario Outline: Filter messages on Organisation Identification and Device Identification and within two dates
    When receiving a filter message log request
      | OrganizationIdentification | Liander     |
      | DeviceIdentification       | DEV-2       |
      | StartTime                  | <StartTime> |
      | EndTime                    | <EndTime>   |
    Then the messages response contains <Amount> messages for
      | OrganizationIdentification | Liander |
      | DeviceIdentification       | DEV-2   |

    Examples: 
      | StartTime            | EndTime              | Amount |
      | 2020-01-01T00:00:00Z | 2020-05-01T00:00:00Z |      0 |
      | 2020-01-01T00:00:00Z | 2040-01-01T00:00:00Z |      5 |

  Scenario Outline: Filter messages within two dates
    When receiving a filter message log request
      | StartTime | <StartTime> |
      | EndTime   | <EndTime>   |
    Then the messages response contains <Amount> messages

    Examples: 
      | StartTime            | EndTime              | Amount |
      | 2020-01-01T00:00:00Z | 2020-05-01T00:00:00Z |      0 |
      | 2020-01-01T00:00:00Z | 2040-01-01T00:00:00Z |     15 |

  Scenario Outline: Filter messages on Start or End Date
    When receiving a filter message log request
      | <TimeFilter> | <Time> |
    Then the messages response contains <Amount> messages

    Examples: 
      | TimeFilter | Time                 | Amount |
      | StartTime  | 2040-01-01T00:00:00Z |      0 |
      | StartTime  | 2020-01-01T00:00:00Z |     15 |
      | EndTime    | 2040-01-01T00:00:00Z |     15 |
      | EndTime    | 2020-01-01T00:00:00Z |      0 |
