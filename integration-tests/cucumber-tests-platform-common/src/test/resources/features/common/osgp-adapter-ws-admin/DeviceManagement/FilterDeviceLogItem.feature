@Common @Platform @AdminDeviceManagement
Feature: Filter DeviceLogItem
  As a ...
  I want to be filter the DeviceLogItem 
  In order to ...

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

  Scenario: Filter DeviceLogItem only on device identification
    When receiving a filter message log request
      | DeviceIdentification | DEV-1 |
    Then the messages response contains 6 correct messages
      | DeviceIdentification | DEV-1 |

  Scenario: wildcard filter Device Identification
    When receiving a filter message log request
      | DeviceIdentification | DEV-1* |
    Then the messages response contains 10 correct messages for devices
      | DEV-1  |
      | DEV-11 |

  Scenario: wildcard filter Organisation Identification
    When receiving a filter message log request
      | OrganizationIdentification | Li?nder |
    Then the messages response contains 11 correct messages for devices
      | DEV-1 |
      | DEV-2 |

  Scenario: Filter DeviceLogItem only on organisation identification
    When receiving a filter message log request
      | OrganizationIdentification | Liander |
    Then the messages response contains 11 correct messages
      | OrganizationIdentification | Liander |

  Scenario: Filter DeviceLogItem on organisation identification and device identification
    When receiving a filter message log request
      | DeviceIdentification       | DEV-2   |
      | OrganizationIdentification | Liander |
    Then the messages response contains 5 correct messages
      | DeviceIdentification       | DEV-2   |
      | OrganizationIdentification | Liander |

  Scenario Outline: Filter DeviceLogItem only on device identification within two dates
    When receiving a filter message log request
      | DeviceIdentification | DEV-1       |
      | SetPointStartTime    | <StartTime> |
      | SetPointEndTime      | <EndTime>   |
    Then the messages response contains <Amount> correct messages
      | DeviceIdentification | DEV-1 |

    Examples: 
      | StartTime            | EndTime              | Amount |
      | 2020-01-01T00:00:00Z | 2020-01-02T00:00:00Z |      0 |
      | 1970-01-01T00:00:00Z | 2025-01-01T00:00:00Z |      6 |


  Scenario Outline: Filter DeviceLogItem only on organisation Identification within two dates
    When receiving a filter message log request
      | OrganizationIdentification | Liander     |
      | SetPointStartTime          | <StartTime> |
      | SetPointEndTime            | <EndTime>   |
    Then the messages response contains <Amount> correct messages
      | OrganizationIdentification | Liander |

    Examples: 
      | StartTime            | EndTime              | Amount |
      | 2020-01-01T00:00:00Z | 2020-01-02T00:00:00Z |      0 |
      | 1970-01-01T00:00:00Z | 2025-01-01T00:00:00Z |     11 |

  Scenario Outline: Filter DeviceLogItem on organisation Identification and device identification within two dates
    When receiving a filter message log request
      | OrganizationIdentification | Liander     |
      | DeviceIdentification       | DEV-2       |
      | SetPointStartTime          | <StartTime> |
      | SetPointEndTime            | <EndTime>   |
    Then the messages response contains <Amount> correct messages
      | OrganizationIdentification | Liander |
      | DeviceIdentification       | DEV-2   |

    Examples: 
      | StartTime            | EndTime              | Amount |
      | 2020-01-01T00:00:00Z | 2020-01-02T00:00:00Z |      0 |
      | 1970-01-01T00:00:00Z | 2025-01-01T00:00:00Z |      5 |

  Scenario Outline: Filter DeviceLogItem within two dates
    When receiving a filter message log request
      | SetPointStartTime | <StartTime> |
      | SetPointEndTime   | <EndTime>   |
    Then the messages response contains <Amount> correct messages with date filter or no filter

    Examples: 
      | StartTime            | EndTime              | Amount |
      | 2020-01-01T00:00:00Z | 2020-01-02T00:00:00Z |      0 |
      | 1970-01-01T00:00:00Z | 2025-01-01T00:00:00Z |     15 |

  Scenario Outline: Filter DeviceLogItem on start or end date
    When receiving a filter message log request
      | <TimeFilter> | <Time> |
    Then the messages response contains <Amount> correct messages with date filter or no filter

    Examples: 
      | TimeFilter        | Time                 | Amount |
      | SetPointStartTime | 2025-01-01T00:00:00Z |      0 |
      | SetPointStartTime | 1970-01-01T00:00:00Z |     15 |
      | SetPointEndTime   | 2025-01-01T00:00:00Z |     15 |
      | SetPointEndTime   | 1970-01-01T00:00:00Z |      0 |

  Scenario Outline: Filter DeviceLogItem on DeviceIdentification and start date or end date
    When receiving a filter message log request
      | <TimeFilter>         | <Time> |
      | DeviceIdentification | DEV-1  |
    Then the messages response contains <Amount> correct messages
      | DeviceIdentification | DEV-1 |

    Examples: 
      | TimeFilter        | Time                 | Amount |
      | SetPointStartTime | 2025-01-01T00:00:00Z |      0 |
      | SetPointStartTime | 1970-01-01T00:00:00Z |      6 |
      | SetPointEndTime   | 2025-01-01T00:00:00Z |      6 |
      | SetPointEndTime   | 1970-01-01T00:00:00Z |      0 |
