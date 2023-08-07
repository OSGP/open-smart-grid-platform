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
    Then the bundle response should contain a power quality profile response with 960 values
      | description              | classId | logicalName     | attributeIndex | dataIndex | unit      |
      | Clock  	  	             |       8 | 0.0.1.0.0.255   |              2 |         0 | UNDEFINED |
      | Voltage swells L1	     |       1 | 1.0.32.36.0.255 |              2 |         0 | UNDEFINED |
      | Voltage sags L1  	     |       1 | 1.0.32.32.0.255 |              2 |         0 | UNDEFINED |
      | Number of Powerfailures  |       1 | 0.0.96.7.21.255 |              2 |         0 | UNDEFINED |
      | Instantaneous voltage L1 |       3 | 1.0.32.7.0.255  |              2 |         0 | V         |
      | Average voltage L1	     |       3 | 1.0.32.24.0.255 |              2 |         0 | V         |
  # 4.3 CDMA Diagnostics

    Examples:
      | deviceIdentification | port | protocol | version |
      | TEST1024000011100    | 1024 | DSMR     |   4.2.2 |
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
    Then the bundle response should contain a power quality profile response with 960 values
      | description              | classId | logicalName     | attributeIndex | dataIndex | unit      |
      | Clock  	  	             |       8 | 0.0.1.0.0.255   |              2 |         0 | UNDEFINED |
      | Instantaneous voltage L1 |       3 | 1.0.32.7.0.255  |              2 |         0 | V         |
      | Average voltage L1	     |       3 | 1.0.32.24.0.255 |              2 |         0 | V         |
      | Instantaneous voltage L2 |       3 | 1.0.52.7.0.255  |              2 |         0 | V         |
      | Average voltage L2	     |       3 | 1.0.52.24.0.255 |              2 |         0 | V         |
      | Instantaneous voltage L3 |       3 | 1.0.72.7.0.255  |              2 |         0 | V         |
      | Average voltage L3	     |       3 | 1.0.72.24.0.255 |              2 |         0 | V         |

    Examples:
      | deviceIdentification | port | protocol | version |
      | TEST1024000011100    | 1024 | DSMR     |   4.2.2 |
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
    Then the bundle response should contain a power quality profile response with 960 values
      | description                      | classId | logicalName     | attributeIndex | dataIndex | unit      |
      | Clock  	  	                     | 8       | 0.0.1.0.0.255   | 2              | 0         | UNDEFINED |
      | Instantaneous current L1         | 3       | 1.0.31.7.0.255  | 2              | 0         | AMP       |
      | L1 Average Current               | 3       | 1.0.31.24.0.255 | 2              | 0         | AMP       |
      | L1 Average Active Power Import   | 3       | 1.0.21.24.0.255 | 2              | 0         | W         |
      | L1 Average Active Power Export   | 3       | 1.0.22.24.0.255 | 2              | 0         | W         |
      | L1 Average Reactive Power Import | 3       | 1.0.23.24.0.255 | 2              | 0         | VAR       |
      | L1 Average Reactive Power Export | 3       | 1.0.24.24.0.255 | 2              | 0         | VAR       |

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
    Then the bundle response should contain a power quality profile response with 960 values
      | description                      | classId | logicalName     | attributeIndex | dataIndex | unit      |
      | Clock  	  	                     | 8       | 0.0.1.0.0.255   | 2              | 0         | UNDEFINED |
      | Instantaneous current Total      | 3       | 1.0.90.7.0.255  | 2              | 0         | AMP       |
      | L1 Average Active Power Import   | 3       | 1.0.21.24.0.255 | 2              | 0         | W         |
      | L2 Average Active Power Import   | 3       | 1.0.41.24.0.255 | 2              | 0         | W         |
      | L3 Average Active Power Import   | 3       | 1.0.61.24.0.255 | 2              | 0         | W         |
      | L1 Average Active Power Export   | 3       | 1.0.22.24.0.255 | 2              | 0         | W         |
      | L2 Average Active Power Export   | 3       | 1.0.42.24.0.255 | 2              | 0         | W         |
      | L3 Average Active Power Export   | 3       | 1.0.62.24.0.255 | 2              | 0         | W         |
      | L1 Average Reactive Power Import | 3       | 1.0.23.24.0.255 | 2              | 0         | VAR       |
      | L2 Average Reactive Power Import | 3       | 1.0.43.24.0.255 | 2              | 0         | VAR       |
      | L3 Average Reactive Power Import | 3       | 1.0.63.24.0.255 | 2              | 0         | VAR       |
      | L1 Average Reactive Power Export | 3       | 1.0.24.24.0.255 | 2              | 0         | VAR       |
      | L2 Average Reactive Power Export | 3       | 1.0.44.24.0.255 | 2              | 0         | VAR       |
      | L4 Average Reactive Power Export | 3       | 1.0.64.24.0.255 | 2              | 0         | VAR       |

    Examples:
      | deviceIdentification | port | protocol | version |
      | TEST1024000011100    | 1024 | DSMR     |   4.2.2 |
      | TEST1031000011101    | 1031 |  SMR     |     4.3 |
#
#  Scenario Outline: Retrieve power quality profile data as part of a bundled request for - public - single phase - SMR - (<protocol> - <version>)
#    Given a dlms device
#      | DeviceIdentification      | <deviceIdentification> |
#      | DeviceType                | SMART_METER_E          |
#      | Protocol                  | <protocol>             |
#      | ProtocolVersion           | <version>              |
#      | Port                      | <port>                 |
#      | Polyphase                 | false                  |
#    And a bundle request
#      | DeviceIdentification | <deviceIdentification> |
#    And the bundle request contains a get power quality profile request with parameters
#      | ProfileType          |              PUBLIC |
#      | BeginDate            | 2015-01-01 00:00:00 |
#      | EndDate              | 2017-01-10 00:00:00 |
#    When the bundle request is received
#    Then the bundle response should contain a power quality profile response with 960 values
#      | description              | classId | logicalName     | attributeIndex | dataIndex | unit      |
#      | Clock  	  	             |       8 | 0.0.1.0.0.255   |              2 |         0 | UNDEFINED |
##      | Voltage swells L1	     |       1 | 1.0.32.36.0.255 |              2 |         0 | UNDEFINED |
##      | Voltage sags L1  	     |       1 | 1.0.32.32.0.255 |              2 |         0 | UNDEFINED |
##      | Number of Powerfailures  |       1 | 0.0.96.7.21.255 |              2 |         0 | UNDEFINED |
##      | Instantaneous voltage L1 |       3 | 1.0.32.7.0.255  |              2 |         0 | V         |
##      | Average voltage L1	     |       3 | 1.0.32.24.0.255 |              2 |         0 | V         |
## missing:
#  # 4.3 CDMA Diagnostics
#
#    Examples:
#      | deviceIdentification | port | protocol | version |
#      | TEST1027000011101    | 1027 | SMR      |   5.0.0 |
##      | TEST1028000011102    | 1028 | SMR      |     5.1 |
##      | TEST1029000011103    | 1029 | SMR      |     5.2 |
##      | TEST1030000011104    | 1030 | SMR      |     5.5 |
