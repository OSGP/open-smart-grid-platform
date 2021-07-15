Feature: General Interrogation
  As controlling station
  I want to send general interrogation requests to controlled stations
  So that I am able to return the actual statuses of the controlled stations

  Background:
    Given IEC60870 devices
      | device_identification | device_type           | gateway_device_identification | device_address |
      | RTU_1                 | LIGHT_MEASUREMENT_RTU |                               |                |
      | LMD_1                 | LIGHT_SENSOR          | RTU_1                         |              1 |
      | LMD_2                 | LIGHT_SENSOR          | RTU_1                         |              2 |
    And a process image on the controlled station
      | information_object_address | information_object_type | information_element_value |
      |                          1 | SIQ                     | OFF                       |
      |                          2 | SIQ                     | ON                        |

  Scenario: send general interrogation command after connecting to a controlled station
    When I receive a connect request for IEC60870 device "RTU_1" from osgp core
    Then I should send a general interrogation command to device "RTU_1"
    And I should send a connect response message to osgp core
      | device_identification | RTU_1 |

  Scenario: Send general interrogation command after receiving a get status request message from osgp core
    Given an existing connection with IEC60870 device "RTU_1" of type LIGHT_MEASUREMENT_RTU
    When I receive a get light sensor status request message for IEC60870 device "LMD_2" from osgp core
    Then I should send a general interrogation command to device "RTU_1"
    And I should send get light sensor status response messages to osgp core
      | device_identification | status |
      | LMD_2                 | DARK   |
