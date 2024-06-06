# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform
Feature: SmartMetering Bundle - GetPowerQualityProfile
  As a grid operator
  I want to be able to retrieve power quality profile data from a meter via a bundle request

  Scenario Outline: Retrieve power quality profile data as part of a bundled request for - public - single phase - (D)SMR - (<protocol> - <version>)
    Given a dlms device
      | DeviceIdentification      | <deviceIdentification> |
      | DeviceType                | SMART_METER_E          |
      | Protocol                  | <protocol>             |
      | ProtocolVersion           | <version>              |
      | Port                      | <port>                 |
      | Polyphase                 | false                  |
    And a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And the bundle request contains a get power quality profile request with parameters
      | ProfileType          |              PUBLIC |
      | BeginDate            | 2015-01-01 00:00:00 |
      | EndDate              | 2017-01-10 00:00:00 |
    When the bundle request is received
    Then the bundle response should contain a power quality profile response with 960 values for profile "0.1.94.31.6.255"
      | description                       | classId | logicalName     | attributeIndex | dataIndex | unit      | value_type     |
      | Clock  	  	                      |       8 | 0.0.1.0.0.255   |              2 |         0 | UNDEFINED | DATE_TIME      |
      | Voltage swells L1	              |       1 | 1.0.32.36.0.255 |              2 |         0 | UNDEFINED | NUMBER         |
      | Voltage sags L1  	              |       1 | 1.0.32.32.0.255 |              2 |         0 | UNDEFINED | NUMBER         |
      | Number of Powerfailures           |       1 | 0.0.96.7.21.255 |              2 |         0 | UNDEFINED | NUMBER         |
      | Instantaneous voltage L1          |       3 | 1.0.32.7.0.255  |              2 |         0 | V         | BIG_DECIMAL    |
      | Average voltage L1	              |       3 | 1.0.32.24.0.255 |              2 |         0 | V         | BIG_DECIMAL    |

    Examples:
      | deviceIdentification | port | protocol | version |
      | TEST1024000011100    | 1024 | DSMR     |   4.2.2 |

  Scenario Outline: Retrieve power quality profile data as part of a bundled request for - public - single phase - (D)SMR - (<protocol> - <version>)
    Given a dlms device
      | DeviceIdentification      | <deviceIdentification> |
      | DeviceType                | SMART_METER_E          |
      | Protocol                  | <protocol>             |
      | ProtocolVersion           | <version>              |
      | Port                      | <port>                 |
      | Polyphase                 | false                  |
    And a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And the bundle request contains a get power quality profile request with parameters
      | ProfileType          |              PUBLIC |
      | BeginDate            | 2015-01-01 00:00:00 |
      | EndDate              | 2017-01-10 00:00:00 |
    When the bundle request is received
    Then the bundle response should contain a power quality profile response with 960 values for profile "0.1.94.31.6.255"
      | description                       | classId | logicalName     | attributeIndex | dataIndex | unit      | value_type     |
      | Clock  	  	                      |       8 | 0.0.1.0.0.255   |              2 |         0 | UNDEFINED | DATE_TIME      |
      | Voltage swells L1	              |       1 | 1.0.32.36.0.255 |              2 |         0 | UNDEFINED | NUMBER         |
      | Voltage sags L1  	              |       1 | 1.0.32.32.0.255 |              2 |         0 | UNDEFINED | NUMBER         |
      | Number of Powerfailures           |       1 | 0.0.96.7.21.255 |              2 |         0 | UNDEFINED | NUMBER         |
      | Instantaneous voltage L1          |       3 | 1.0.32.7.0.255  |              2 |         0 | V         | BIG_DECIMAL    |
      | Average voltage L1	              |       3 | 1.0.32.24.0.255 |              2 |         0 | V         | BIG_DECIMAL    |
      | CDMA Diagnostics (Signal quality) |      47 | 0.1.25.6.0.255  |              6 |         3 | UNDEFINED | SIGNAL_QUALITY |

    Examples:
      | deviceIdentification | port | protocol | version |
      | TEST1031000011101    | 1031 | SMR      |     4.3 |

  Scenario Outline: Retrieve power quality profile data as part of a bundled request for - public - poly phase - (D)SMR - (<protocol> - <version>)
    Given a dlms device
      | DeviceIdentification      | <deviceIdentification> |
      | DeviceType                | SMART_METER_E          |
      | Protocol                  | <protocol>             |
      | ProtocolVersion           | <version>              |
      | Port                      | <port>                 |
      | Polyphase                 | true                  |
    And a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And the bundle request contains a get power quality profile request with parameters
      | ProfileType          |              PUBLIC |
      | BeginDate            | 2015-01-01 00:00:00 |
      | EndDate              | 2017-01-10 00:00:00 |
    When the bundle request is received
    Then the bundle response should contain a power quality profile response with 960 values for profile "0.1.94.31.6.255"
      | description              | classId | logicalName     | attributeIndex | dataIndex | unit      | value_type     |
      | Clock  	  	             |       8 | 0.0.1.0.0.255   |              2 |         0 | UNDEFINED | DATE_TIME      |
      | Instantaneous voltage L1 |       3 | 1.0.32.7.0.255  |              2 |         0 | V         | BIG_DECIMAL    |
      | Average voltage L1	     |       3 | 1.0.32.24.0.255 |              2 |         0 | V         | BIG_DECIMAL    |
      | Instantaneous voltage L2 |       3 | 1.0.52.7.0.255  |              2 |         0 | V         | BIG_DECIMAL    |
      | Average voltage L2	     |       3 | 1.0.52.24.0.255 |              2 |         0 | V         | BIG_DECIMAL    |
      | Instantaneous voltage L3 |       3 | 1.0.72.7.0.255  |              2 |         0 | V         | BIG_DECIMAL    |
      | Average voltage L3	     |       3 | 1.0.72.24.0.255 |              2 |         0 | V         | BIG_DECIMAL    |

    Examples:
      | deviceIdentification | port | protocol | version |
      | TEST1024000011100    | 1024 | DSMR     |   4.2.2 |

  Scenario Outline: Retrieve power quality profile data as part of a bundled request for - public - poly phase - (D)SMR - (<protocol> - <version>)
    Given a dlms device
      | DeviceIdentification      | <deviceIdentification> |
      | DeviceType                | SMART_METER_E          |
      | Protocol                  | <protocol>             |
      | ProtocolVersion           | <version>              |
      | Port                      | <port>                 |
      | Polyphase                 | true                  |
    And a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And the bundle request contains a get power quality profile request with parameters
      | ProfileType          |              PUBLIC |
      | BeginDate            | 2015-01-01 00:00:00 |
      | EndDate              | 2017-01-10 00:00:00 |
    When the bundle request is received
    Then the bundle response should contain a power quality profile response with 960 values for profile "0.1.94.31.6.255"
      | description                       | classId | logicalName     | attributeIndex | dataIndex | unit      | value_type     |
      | Clock  	  	                      |       8 | 0.0.1.0.0.255   |              2 |         0 | UNDEFINED | DATE_TIME      |
      | Instantaneous voltage L1          |       3 | 1.0.32.7.0.255  |              2 |         0 | V         | BIG_DECIMAL    |
      | Average voltage L1	              |       3 | 1.0.32.24.0.255 |              2 |         0 | V         | BIG_DECIMAL    |
      | Instantaneous voltage L2          |       3 | 1.0.52.7.0.255  |              2 |         0 | V         | BIG_DECIMAL    |
      | Average voltage L2	              |       3 | 1.0.52.24.0.255 |              2 |         0 | V         | BIG_DECIMAL    |
      | Instantaneous voltage L3          |       3 | 1.0.72.7.0.255  |              2 |         0 | V         | BIG_DECIMAL    |
      | Average voltage L3	              |       3 | 1.0.72.24.0.255 |              2 |         0 | V         | BIG_DECIMAL    |
      | CDMA Diagnostics (Signal quality) |      47 | 0.1.25.6.0.255  |              6 |         3 | UNDEFINED | SIGNAL_QUALITY |

    Examples:
      | deviceIdentification | port | protocol | version |
      | TEST1031000011101    | 1031 | SMR      |     4.3 |

  Scenario Outline: Retrieve power quality profile data as part of a bundled request for - private - (D)SMR - single phase (<protocol> - <version>)
    Given a dlms device
      | DeviceIdentification      | <deviceIdentification> |
      | DeviceType                | SMART_METER_E          |
      | Protocol                  | <protocol>             |
      | ProtocolVersion           | <version>              |
      | Port                      | <port>                 |
      | Polyphase                 | false                  |
    And a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And the bundle request contains a get power quality profile request with parameters
      | ProfileType          |             PRIVATE |
      | BeginDate            | 2015-01-01 00:00:00 |
      | EndDate              | 2017-01-10 00:00:00 |
    When the bundle request is received
    Then the bundle response should contain a power quality profile response with 960 values for profile "0.1.94.31.6.255"
      | description                      | classId | logicalName     | attributeIndex | dataIndex | unit      | value_type     |
      | Clock  	  	                     | 8       | 0.0.1.0.0.255   | 2              | 0         | UNDEFINED | DATE_TIME      |
      | Instantaneous current L1         | 3       | 1.0.31.7.0.255  | 2              | 0         | AMP       | BIG_DECIMAL    |
      | L1 Average Current               | 3       | 1.0.31.24.0.255 | 2              | 0         | AMP       | BIG_DECIMAL    |
      | L1 Average Active Power Import   | 3       | 1.0.21.24.0.255 | 2              | 0         | W         | BIG_DECIMAL    |
      | L1 Average Active Power Export   | 3       | 1.0.22.24.0.255 | 2              | 0         | W         | BIG_DECIMAL    |
      | L1 Average Reactive Power Import | 3       | 1.0.23.24.0.255 | 2              | 0         | VAR       | BIG_DECIMAL    |
      | L1 Average Reactive Power Export | 3       | 1.0.24.24.0.255 | 2              | 0         | VAR       | BIG_DECIMAL    |

    Examples:
      | deviceIdentification | port | protocol | version |
      | TEST1024000011100    | 1024 | DSMR     |   4.2.2 |
      | TEST1031000011101    | 1031 |  SMR     |     4.3 |

  Scenario Outline: Retrieve power quality profile data as part of a bundled request for - private - (D)SMR - poly phase (<protocol> - <version>)
    Given a dlms device
      | DeviceIdentification      | <deviceIdentification> |
      | DeviceType                | SMART_METER_E          |
      | Protocol                  | <protocol>             |
      | ProtocolVersion           | <version>              |
      | Port                      | <port>                 |
      | Polyphase                 | true                   |
    And a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And the bundle request contains a get power quality profile request with parameters
      | ProfileType          |             PRIVATE |
      | BeginDate            | 2015-01-01 00:00:00 |
      | EndDate              | 2017-01-10 00:00:00 |
    When the bundle request is received
    Then the bundle response should contain a power quality profile response with 960 values for profile "0.1.94.31.6.255"
      | description                      | classId | logicalName     | attributeIndex | dataIndex | unit      | value_type     |
      | Clock  	  	                     | 8       | 0.0.1.0.0.255   | 2              | 0         | UNDEFINED | DATE_TIME      |
      | Instantaneous current Total      | 3       | 1.0.90.7.0.255  | 2              | 0         | AMP       | BIG_DECIMAL    |
      | L1 Average Active Power Import   | 3       | 1.0.21.24.0.255 | 2              | 0         | W         | BIG_DECIMAL    |
      | L2 Average Active Power Import   | 3       | 1.0.41.24.0.255 | 2              | 0         | W         | BIG_DECIMAL    |
      | L3 Average Active Power Import   | 3       | 1.0.61.24.0.255 | 2              | 0         | W         | BIG_DECIMAL    |
      | L1 Average Active Power Export   | 3       | 1.0.22.24.0.255 | 2              | 0         | W         | BIG_DECIMAL    |
      | L2 Average Active Power Export   | 3       | 1.0.42.24.0.255 | 2              | 0         | W         | BIG_DECIMAL    |
      | L3 Average Active Power Export   | 3       | 1.0.62.24.0.255 | 2              | 0         | W         | BIG_DECIMAL    |
      | L1 Average Reactive Power Import | 3       | 1.0.23.24.0.255 | 2              | 0         | VAR       | BIG_DECIMAL    |
      | L2 Average Reactive Power Import | 3       | 1.0.43.24.0.255 | 2              | 0         | VAR       | BIG_DECIMAL    |
      | L3 Average Reactive Power Import | 3       | 1.0.63.24.0.255 | 2              | 0         | VAR       | BIG_DECIMAL    |
      | L1 Average Reactive Power Export | 3       | 1.0.24.24.0.255 | 2              | 0         | VAR       | BIG_DECIMAL    |
      | L2 Average Reactive Power Export | 3       | 1.0.44.24.0.255 | 2              | 0         | VAR       | BIG_DECIMAL    |
      | L4 Average Reactive Power Export | 3       | 1.0.64.24.0.255 | 2              | 0         | VAR       | BIG_DECIMAL    |

    Examples:
      | deviceIdentification | port | protocol | version |
      | TEST1024000011100    | 1024 | DSMR     |   4.2.2 |
      | TEST1031000011101    | 1031 |  SMR     |     4.3 |

  #
  # SMR
  #

  Scenario Outline: Retrieve power quality profile data as part of a bundled request for - public - single phase - SMR - (<protocol> - <version>)
    Given a dlms device
      | DeviceIdentification      | <deviceIdentification> |
      | DeviceType                | SMART_METER_E          |
      | Protocol                  | <protocol>             |
      | ProtocolVersion           | <version>              |
      | Port                      | <port>                 |
      | Polyphase                 | false                  |
    And a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And the bundle request contains a get power quality profile request with parameters
      | ProfileType          |              PUBLIC |
      | BeginDate            | 2015-01-01 00:00:00 |
      | EndDate              | 2017-01-10 00:00:00 |
    When the bundle request is received
    Then the bundle response should contain a power quality profile response with 960 values for profile "0.1.94.31.6.255"
      | description                           | classId | logicalName     | attributeIndex | dataIndex | unit      | value_type     |
      | Clock  	  	                          |       8 | 0.0.1.0.0.255   |              2 |         0 | UNDEFINED | DATE_TIME      |
      | Number of voltage sags in phase L1    |       1 | 1.0.32.32.0.255 |              2 |         0 | UNDEFINED | NUMBER         |
      | Number of voltage swells in phase L1  |       1 | 1.0.32.36.0.255 |              2 |         0 | UNDEFINED | NUMBER         |
      | Number of power failures in any phase |       1 | 0.0.96.7.21.255 |              2 |         0 | UNDEFINED | NUMBER         |
      | CDMA Diagnostics (Signal quality)     |      47 | 0.1.25.6.0.255  |              6 |         3 | UNDEFINED | SIGNAL_QUALITY |
      | GPRS Diagnostics (Signal quality)     |      47 | 0.0.25.6.0.255  |              6 |         3 | UNDEFINED | SIGNAL_QUALITY |
      | CDMA Diagnostics (BER)                |      47 | 0.1.25.6.0.255  |              6 |         4 | UNDEFINED | NUMBER         |
      | GPRS Diagnostics (BER)                |      47 | 0.0.25.6.0.255  |              6 |         4 | UNDEFINED | NUMBER         |
      | M-Bus Client Setup CHn1               |      72 | 0.1.24.1.0.255  |             11 |         0 | UNDEFINED | NUMBER         |
      | M-Bus Client Setup CHn2               |      72 | 0.2.24.1.0.255  |             11 |         0 | UNDEFINED | NUMBER         |
      | M-Bus diagnostics RSSi CHn1           |      77 | 0.1.24.9.0.255  |              2 |         0 | UNDEFINED | NUMBER         |
      | M-Bus diagnostics RSSi CHn2           |      77 | 0.2.24.9.0.255  |              2 |         0 | UNDEFINED | NUMBER         |
      | M-Bus diagnostics FCS-NOK CHn1        |      77 | 0.1.24.9.0.255  |              8 |         0 | UNDEFINED | NUMBER         |
      | M-Bus diagnostics FCS-NOK CHn2        |      77 | 0.2.24.9.0.255  |              8 |         0 | UNDEFINED | NUMBER         |
    Then the same bundle response should contain a power quality profile response with 1440 values for profile "1.0.99.1.2.255"
      | description              | classId | logicalName     | attributeIndex | dataIndex | unit      | value_type     |
      | Clock  	  	             |       8 | 0.0.1.0.0.255   |              2 |         0 | UNDEFINED | DATE_TIME      |
      | L1 Average Voltage       |       3 | 1.0.32.24.0.255 |              2 |         0 | V         | BIG_DECIMAL    |
      | L1 Instantaneous Voltage |       3 | 1.0.32.7.0.255  |              2 |         0 | V         | BIG_DECIMAL    |

    Examples:
      | deviceIdentification | port | protocol | version |
      | TEST1027000011101    | 1027 | SMR      |   5.0.0 |
      | TEST1028000011102    | 1028 | SMR      |     5.1 |
      | TEST1029000011103    | 1029 | SMR      |     5.2 |
      | TEST1030000011104    | 1030 | SMR      |     5.5 |

  Scenario Outline: Retrieve power quality profile data as part of a bundled request for - public - poly phase - SMR - (<protocol> - <version>)
    Given a dlms device
      | DeviceIdentification      | <deviceIdentification> |
      | DeviceType                | SMART_METER_E          |
      | Protocol                  | <protocol>             |
      | ProtocolVersion           | <version>              |
      | Port                      | <port>                 |
      | Polyphase                 | true                  |
    And a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And the bundle request contains a get power quality profile request with parameters
      | ProfileType          |              PUBLIC |
      | BeginDate            | 2015-01-01 00:00:00 |
      | EndDate              | 2017-01-10 00:00:00 |
    When the bundle request is received
    Then the bundle response should contain a power quality profile response with 960 values for profile "0.1.94.31.6.255"
      | description                           | classId | logicalName     | attributeIndex | dataIndex | unit      | value_type     |
      | Clock  	  	                          |       8 | 0.0.1.0.0.255   |              2 |         0 | UNDEFINED | DATE_TIME      |
      | Number of voltage sags in phase L1    |       1 | 1.0.32.32.0.255 |              2 |         0 | UNDEFINED | NUMBER         |
      | Number of voltage sags in phase L2    |       1 | 1.0.52.32.0.255 |              2 |         0 | UNDEFINED | NUMBER         |
      | Number of voltage sags in phase L3    |       1 | 1.0.72.32.0.255 |              2 |         0 | UNDEFINED | NUMBER         |
      | Number of voltage swells in phase L1  |       1 | 1.0.32.36.0.255 |              2 |         0 | UNDEFINED | NUMBER         |
      | Number of voltage swells in phase L2  |       1 | 1.0.52.36.0.255 |              2 |         0 | UNDEFINED | NUMBER         |
      | Number of voltage swells in phase L3  |       1 | 1.0.72.36.0.255 |              2 |         0 | UNDEFINED | NUMBER         |
      | Number of power failures in any phase |       1 | 0.0.96.7.21.255 |              2 |         0 | UNDEFINED | NUMBER         |
      | CDMA Diagnostics (Signal quality)     |      47 | 0.1.25.6.0.255  |              6 |         3 | UNDEFINED | SIGNAL_QUALITY |
      | GPRS Diagnostics (Signal quality)     |      47 | 0.0.25.6.0.255  |              6 |         3 | UNDEFINED | SIGNAL_QUALITY |
      | CDMA Diagnostics (BER)                |      47 | 0.1.25.6.0.255  |              6 |         4 | UNDEFINED | NUMBER         |
      | GPRS Diagnostics (BER)                |      47 | 0.0.25.6.0.255  |              6 |         4 | UNDEFINED | NUMBER         |
      | M-Bus Client Setup CHn1               |      72 | 0.1.24.1.0.255  |             11 |         0 | UNDEFINED | NUMBER         |
      | M-Bus Client Setup CHn2               |      72 | 0.2.24.1.0.255  |             11 |         0 | UNDEFINED | NUMBER         |
      | M-Bus diagnostics RSSi CHn1           |      77 | 0.1.24.9.0.255  |              2 |         0 | UNDEFINED | NUMBER         |
      | M-Bus diagnostics RSSi CHn2           |      77 | 0.2.24.9.0.255  |              2 |         0 | UNDEFINED | NUMBER         |
      | M-Bus diagnostics FCS-NOK CHn1        |      77 | 0.1.24.9.0.255  |              8 |         0 | UNDEFINED | NUMBER         |
      | M-Bus diagnostics FCS-NOK CHn2        |      77 | 0.2.24.9.0.255  |              8 |         0 | UNDEFINED | NUMBER         |
    Then the same bundle response should contain a power quality profile response with 1440 values for profile "1.0.99.1.2.255"
      | description              | classId | logicalName     | attributeIndex | dataIndex | unit      | value_type     |
      | Clock  	  	             |       8 | 0.0.1.0.0.255   |              2 |         0 | UNDEFINED | DATE_TIME      |
      | L1 Average Voltage       |       3 | 1.0.32.24.0.255 |              2 |         0 | V         | BIG_DECIMAL    |
      | L2 Average Voltage       |       3 | 1.0.52.24.0.255 |              2 |         0 | V         | BIG_DECIMAL    |
      | L3 Average Voltage       |       3 | 1.0.72.24.0.255 |              2 |         0 | V         | BIG_DECIMAL    |

    Examples:
      | deviceIdentification | port | protocol | version |
      | TEST1027000011101    | 1027 | SMR      |   5.0.0 |
      | TEST1028000011102    | 1028 | SMR      |     5.1 |
      | TEST1029000011103    | 1029 | SMR      |     5.2 |
      | TEST1030000011104    | 1030 | SMR      |     5.5 |

  Scenario Outline: Retrieve power quality profile data as part of a bundled request for - private - SMR - single phase (<protocol> - <version>)
    Given a dlms device
      | DeviceIdentification      | <deviceIdentification> |
      | DeviceType                | SMART_METER_E          |
      | Protocol                  | <protocol>             |
      | ProtocolVersion           | <version>              |
      | Port                      | <port>                 |
      | Polyphase                 | false                  |
    And a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And the bundle request contains a get power quality profile request with parameters
      | ProfileType          |             PRIVATE |
      | BeginDate            | 2015-01-01 00:00:00 |
      | EndDate              | 2017-01-10 00:00:00 |
    When the bundle request is received
    Then the bundle response should contain a power quality profile response with 960 values for profile "1.0.99.1.1.255"
      | description                      | classId | logicalName    | attributeIndex | dataIndex | unit      | value_type     |
      | Clock  	  	                     |       8 | 0.0.1.0.0.255  |              2 |         0 | UNDEFINED | DATE_TIME      |
      | L1 Average Active Power Import   |       3 | 1.0.21.4.0.255 |              2 |         0 |         W | BIG_DECIMAL    |
      | L1 Average Active Power Export   |       3 | 1.0.22.4.0.255 |              2 |         0 |         W | BIG_DECIMAL    |
      | L1 Average Reactive Power Import |       3 | 1.0.23.4.0.255 |              2 |         0 |       VAR | BIG_DECIMAL    |
      | L1 Average Reactive Power Export |       3 | 1.0.24.4.0.255 |              2 |         0 |       VAR | BIG_DECIMAL    |
    Then the same bundle response should contain a power quality profile response with 1440 values for profile "1.0.99.1.2.255"
      | description              | classId | logicalName     | attributeIndex | dataIndex | unit      | value_type     |
      | Clock  	  	             |       8 | 0.0.1.0.0.255   |              2 |         0 | UNDEFINED | DATE_TIME      |
      | L1 Average Current       |       3 | 1.0.31.24.0.255 |              2 |         0 | AMP       | BIG_DECIMAL    |
      | Instantaneous current L1 |       3 | 1.0.31.7.0.255  |              2 |         0 | AMP       | BIG_DECIMAL    |

    Examples:
      | deviceIdentification | port | protocol | version |
      | TEST1027000011101    | 1027 | SMR      |   5.0.0 |
      | TEST1028000011102    | 1028 | SMR      |     5.1 |
      | TEST1029000011103    | 1029 | SMR      |     5.2 |
      | TEST1030000011104    | 1030 | SMR      |     5.5 |

  Scenario Outline: Retrieve power quality profile data as part of a bundled request for - private - SMR - poly phase (<protocol> - <version>)
    Given a dlms device
      | DeviceIdentification      | <deviceIdentification> |
      | DeviceType                | SMART_METER_E          |
      | Protocol                  | <protocol>             |
      | ProtocolVersion           | <version>              |
      | Port                      | <port>                 |
      | Polyphase                 | true                   |
    And a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And the bundle request contains a get power quality profile request with parameters
      | ProfileType          |             PRIVATE |
      | BeginDate            | 2015-01-01 00:00:00 |
      | EndDate              | 2017-01-10 00:00:00 |
    When the bundle request is received
    Then the bundle response should contain a power quality profile response with 960 values for profile "1.0.99.1.1.255"
      | description                      | classId | logicalName    | attributeIndex | dataIndex | unit      | value_type     |
      | Clock  	  	                     |       8 | 0.0.1.0.0.255  |              2 |         0 | UNDEFINED | DATE_TIME      |
      | L1 Average Active Power Import   |       3 | 1.0.21.4.0.255 |              2 |         0 |         W | BIG_DECIMAL    |
      | L2 Average Active Power Import   |       3 | 1.0.41.4.0.255 |              2 |         0 |         W | BIG_DECIMAL    |
      | L3 Average Active Power Import   |       3 | 1.0.61.4.0.255 |              2 |         0 |         W | BIG_DECIMAL    |
      | L1 Average Active Power Export   |       3 | 1.0.22.4.0.255 |              2 |         0 |         W | BIG_DECIMAL    |
      | L2 Average Active Power Export   |       3 | 1.0.42.4.0.255 |              2 |         0 |         W | BIG_DECIMAL    |
      | L3 Average Active Power Export   |       3 | 1.0.62.4.0.255 |              2 |         0 |         W | BIG_DECIMAL    |
      | L1 Average Reactive Power Import |       3 | 1.0.23.4.0.255 |              2 |         0 |       VAR | BIG_DECIMAL    |
      | L2 Average Reactive Power Import |       3 | 1.0.43.4.0.255 |              2 |         0 |       VAR | BIG_DECIMAL    |
      | L3 Average Reactive Power Import |       3 | 1.0.63.4.0.255 |              2 |         0 |       VAR | BIG_DECIMAL    |
      | L1 Average Reactive Power Export |       3 | 1.0.24.4.0.255 |              2 |         0 |       VAR | BIG_DECIMAL    |
      | L2 Average Reactive Power Export |       3 | 1.0.44.4.0.255 |              2 |         0 |       VAR | BIG_DECIMAL    |
      | L3 Average Reactive Power Export |       3 | 1.0.64.4.0.255 |              2 |         0 |       VAR | BIG_DECIMAL    |
    Then the same bundle response should contain a power quality profile response with 1440 values for profile "1.0.99.1.2.255"
      | description              | classId | logicalName     | attributeIndex | dataIndex | unit      | value_type     |
      | Clock  	  	             |       8 | 0.0.1.0.0.255   |              2 |         0 | UNDEFINED | DATE_TIME      |
      | L1 Average Current       |       3 | 1.0.31.24.0.255 |              2 |         0 | AMP       | BIG_DECIMAL    |
      | L2 Average Current       |       3 | 1.0.51.24.0.255 |              2 |         0 | AMP       | BIG_DECIMAL    |
      | L3 Average Current       |       3 | 1.0.71.24.0.255 |              2 |         0 | AMP       | BIG_DECIMAL    |

    Examples:
      | deviceIdentification | port | protocol | version |
      | TEST1027000011101    | 1027 | SMR      |   5.0.0 |
      | TEST1028000011102    | 1028 | SMR      |     5.1 |
      | TEST1029000011103    | 1029 | SMR      |     5.2 |
      | TEST1030000011104    | 1030 | SMR      |     5.5 |

  Scenario Outline: Retrieve power quality profile data as part of a bundled request for - [profileType: <profileType> - polyphase: <polyphase>] (DSMR - 2.2)
    Given a dlms device
      | DeviceIdentification      | <deviceIdentification> |
      | DeviceType                | SMART_METER_E          |
      | Protocol                  | DSMR                   |
      | ProtocolVersion           | 2.2                    |
      | Port                      | <port>                 |
      | Polyphase                 | <polyphase>            |
      | Lls1active                | true                   |
      | Hls5active                | false                  |
    And a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And the bundle request contains a get power quality profile request with parameters
      | ProfileType          |       <profileType> |
      | BeginDate            | 2015-01-01 00:00:00 |
      | EndDate              | 2017-01-10 00:00:00 |
    When the bundle request is received
    Then the bundle response should be a FaultResponse with message containing
      | Message | No PQ profile found in profile DSMR version 2.2 |

    Examples:
      | deviceIdentification | port | polyphase | profileType |
      | TEST1026000011100    | 1026 | false      |     PUBLIC |
      | TEST1026000011101    | 1026 | true       |     PUBLIC |
      | TEST1026000011100    | 1026 | false      |    PRIVATE |
      | TEST1026000011101    | 1026 | true       |    PRIVATE |
