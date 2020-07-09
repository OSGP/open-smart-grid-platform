@Common @Platform @AdminDeviceManagement
Feature: Filter device messages
  As an operator
  I want to be filter and sort the device messages 
  In order to analyse problems and check messages

  Background: 
    Given I have 6 device log items
      | DeviceIdentification | DEV-1   |
      | Organization         | Liander |
    And I have 5 device log items
      | DeviceIdentification | DEV-2   |
      | Organization         | Liander |
    And I have 4 device log items
      | DeviceIdentification | DEV-11 |
      | Organization         | TEST   |

  Scenario: No filters set so getting all the messages
    When receiving a message log request without a filter
    Then the messages response contains 15 correct messages with date filter or no filter

  @NightlyBuildOnly
  Scenario: Filter messages only on device identification
    When receiving a filter message log request
      | DeviceIdentification | DEV-1 |
    Then the messages response contains 6 correct messages
      | DeviceIdentification | DEV-1 |

  Scenario: Use wildcard filter for Device Identification
    When receiving a filter message log request
      | DeviceIdentification | DEV-1* |
    Then the messages response contains 10 correct messages for devices
      | DEV-1  |
      | DEV-11 |

  @NightlyBuildOnly
  Scenario: Use wildcard filter for Organisation Identification
    When receiving a filter message log request
      | OrganizationIdentification | Li?nder |
    Then the messages response contains 11 correct messages for devices
      | DEV-1 |
      | DEV-2 |

  @NightlyBuildOnly
  Scenario: Filter messages only on Organisation Identification
    When receiving a filter message log request
      | OrganizationIdentification | Liander |
    Then the messages response contains 11 correct messages
      | OrganizationIdentification | Liander |

  Scenario: Filter messages on Organisation Identification and Device Identification
    When receiving a filter message log request
      | DeviceIdentification       | DEV-2   |
      | OrganizationIdentification | Liander |
    Then the messages response contains 5 correct messages
      | DeviceIdentification       | DEV-2   |
      | OrganizationIdentification | Liander |

  Scenario: Sort messages by Device Identification
    When receiving a filter message log request
      | SortDir  | DESC                 |
      | SortedBy | deviceIdentification |
    Then the messages response contains 15 messages ordered descending by device identification

  Scenario Outline: Filter messages on Device Identification and within two dates
    When receiving a filter message log request
      | DeviceIdentification | DEV-1       |
      | StartTime            | <StartTime> |
      | EndTime              | <EndTime>   |
    Then the messages response contains <Amount> correct messages
      | DeviceIdentification | DEV-1 |

    Examples: 
      | StartTime            | EndTime              | Amount |
      | 2020-01-01T00:00:00Z | 2020-01-02T00:00:00Z |      0 |
      | 1970-01-01T00:00:00Z | 2025-01-01T00:00:00Z |      6 |

  @NightlyBuildOnly
  Scenario Outline: Filter messages on Organisation Identification and within two dates
    When receiving a filter message log request
      | OrganizationIdentification | Liander     |
      | StartTime                  | <StartTime> |
      | EndTime                    | <EndTime>   |
    Then the messages response contains <Amount> correct messages
      | OrganizationIdentification | Liander |

    Examples: 
      | StartTime            | EndTime              | Amount |
      | 2020-01-01T00:00:00Z | 2020-01-02T00:00:00Z |      0 |
      | 1970-01-01T00:00:00Z | 2025-01-01T00:00:00Z |     11 |

  Scenario Outline: Filter messages on Organisation Identification and Device Identification and within two dates
    When receiving a filter message log request
      | OrganizationIdentification | Liander     |
      | DeviceIdentification       | DEV-2       |
      | StartTime                  | <StartTime> |
      | EndTime                    | <EndTime>   |
    Then the messages response contains <Amount> correct messages
      | OrganizationIdentification | Liander |
      | DeviceIdentification       | DEV-2   |

    Examples: 
      | StartTime            | EndTime              | Amount |
      | 2020-01-01T00:00:00Z | 2020-01-02T00:00:00Z |      0 |
      | 1970-01-01T00:00:00Z | 2025-01-01T00:00:00Z |      5 |

  @NightlyBuildOnly
  Scenario Outline: Filter messages within two dates
    When receiving a filter message log request
      | StartTime | <StartTime> |
      | EndTime   | <EndTime>   |
    Then the messages response contains <Amount> correct messages with date filter or no filter

    Examples: 
      | StartTime            | EndTime              | Amount |
      | 2020-01-01T00:00:00Z | 2020-01-02T00:00:00Z |      0 |
      | 1970-01-01T00:00:00Z | 2025-01-01T00:00:00Z |     15 |

  @NightlyBuildOnly
  Scenario Outline: Filter messages on Start or End Date
    When receiving a filter message log request
      | <TimeFilter> | <Time> |
    Then the messages response contains <Amount> correct messages with date filter or no filter

    Examples: 
      | TimeFilter | Time                 | Amount |
      | StartTime  | 2025-01-01T00:00:00Z |      0 |
      | StartTime  | 1970-01-01T00:00:00Z |     15 |
      | EndTime    | 2025-01-01T00:00:00Z |     15 |
      | EndTime    | 1970-01-01T00:00:00Z |      0 |

  @NightlyBuildOnly
  Scenario Outline: Filter messages on Device Identification and Start Date or End Date
    When receiving a filter message log request
      | <TimeFilter>         | <Time> |
      | DeviceIdentification | DEV-1  |
    Then the messages response contains <Amount> correct messages
      | DeviceIdentification | DEV-1 |

    Examples: 
      | TimeFilter | Time                 | Amount |
      | StartTime  | 2025-01-01T00:00:00Z |      0 |
      | StartTime  | 1970-01-01T00:00:00Z |      6 |
      | EndTime    | 2025-01-01T00:00:00Z |      6 |
      | EndTime    | 1970-01-01T00:00:00Z |      0 |
