@Common @Platform @AdminDeviceManagement
Feature: Filter DeviceLogItem
  As a ...
  I want to be filter the DeviceLogItem 
  In order to ...

  Background: 
    Given I have 60 device log items
      | DeviceIdentification       | DEV-1   |
      | OrganisationIdentification | Liander |
    And I have 50 device log items
      | DeviceIdentification       | DEV-2   |
      | OrganisationIdentification | Liander |

  Scenario: Filter DeviceLogItem only on device identification
    When receiving a filter message log request
      | DeviceIdentification | DEV-1 |
    Then the messages response contains 60 correct messages
      | DeviceIdentification | DEV-1 |

  Scenario: Filter DeviceLogItem only on organisation identification
    When receiving a filter message log request
      | OrganisationIdentification | Liander |
    Then the messages response contains 110 correct messages
      | OrganisationIdentification | Liander |

  Scenario: Filter DeviceLogItem on organisation identification and device identification
    When receiving a filter message log request
      | DeviceIdentification       | DEV-2   |
      | OrganisationIdentification | Liander |
    Then the messages response contains 50 correct messages
      | DeviceIdentification       | DEV-2   |
      | OrganisationIdentification | Liander |

  Scenario Outline: Filter DeviceLogItem only on device identification within two dates
    When receiving a filter message log request
      | DeviceIdentification | DEV-1       |
      | SetpointStartTime    | <StartTime> |
      | SetpointEndTime      | <EndTime>   |
    Then the messages response contains <Amount> correct messages
      | DeviceIdentification | DEV-1 |

    Examples: 
      | StartTime    | EndTime      | Amount |
      | "2020-01-01" | "2020-01-02" |      0 |
      | "1970-01-01" | "2025-01-01" |     60 |

  Scenario Outline: Filter DeviceLogItem only on organisation Identification within two dates
    When receiving a filter message log request
      | OrganisationIdentification | Liander     |
      | SetpointStartTime          | <StartTime> |
      | SetpointEndTime            | <EndTime>   |
    Then the messages response contains <Amount> correct messages
      | OrganisationIdentification | Liander |

    Examples: 
      | StartTime    | EndTime      | Amount |
      | "2020-01-01" | "2020-01-02" |      0 |
      | "1970-01-01" | "2025-01-01" |    110 |

  Scenario Outline: Filter DeviceLogItem on organisation Identification and device identification within two dates
    When receiving a filter message log request
      | OrganisationIdentification | Liander     |
      | DeviceIdentification       | DEV-2       |
      | SetpointStartTime          | <StartTime> |
      | SetpointEndTime            | <EndTime>   |
    Then the messages response contains <Amount> correct messages
      | OrganisationIdentification | Liander |
      | DeviceIdentification       | DEV-2   |

    Examples: 
      | StartTime    | EndTime      | Amount |
      | "2020-01-01" | "2020-01-02" |      0 |
      | "1970-01-01" | "2025-01-01" |     50 |

  Scenario Outline: Filter DeviceLogItem on start or end date
    When receiving a filter message log request
      | <TimeFilter> | <Time> |
    Then the messages response contains <Amount> correct messages with date filter

    Examples: 
      | TimeFilter        | Time         | Amount |
      | SetpointStartTime | "2025-01-01" |      0 |
      | SetpointStartTime | "1970-01-01" |    110 |
      | SetpointEndTime   | "2025-01-01" |    110 |
      | SetpointEndTime   | "1970-01-01" |      0 |

  Scenario Outline: Filter DeviceLogItem on DeviceIdentification and start date or end date
    When receiving a filter message log request
      | <TimeFilter>         | <Time> |
      | DeviceIdentification | DEV-1  |
    Then the messages response contains <Amount> correct messages
      | DeviceIdentification | DEV-1 |

    Examples: 
      | TimeFilter        | Time         | Amount |
      | SetpointStartTime | "2025-01-01" |      0 |
      | SetpointStartTime | "1970-01-01" |     60 |
      | SetpointEndTime   | "2025-01-01" |     60 |
      | SetpointEndTime   | "1970-01-01" |      0 |
