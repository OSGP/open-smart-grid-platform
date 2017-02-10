Feature: PublicLightingDeviceMonitoring Get Actual Power Usage
  In order to ...
  As a ...

  @OslpMockServer
  Scenario Outline: Get actual power usage
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SSLD              |
    And the device returns a get actual power usage response "OK" over OSLP
      | RecordTime          | <RecordTime>          |
      | MeterType           | <MeterType>           |
      | TotalConsumedEnergy | <TotalConsumedEnergy> |
      | ActualConsumedPower | <ActualConsumedPower> |
      | TotalLightingHours  | <TotalLightingHours>  |
      | ActualCurrent1      | <ActualCurrent1>      |
      | ActualCurrent2      | <ActualCurrent2>      |
      | ActualCurrent3      | <ActualCurrent3>      |
      | ActualPower1        | <ActualPower1>        |
      | ActualPower2        | <ActualPower2>        |
      | ActualPower3        | <ActualPower3>        |
      | AveragePowerFactor1 | <AveragePowerFactor1> |
      | AveragePowerFactor2 | <AveragePowerFactor2> |
      | AveragePowerFactor3 | <AveragePowerFactor3> |
      | RelayData           | <RelayData>           |
    When receiving a get actual power usage request
      | DeviceIdentification | TEST1024000000001 |
    Then the get actual power usage async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a get actual power usage OSLP message is sent to the device
    And the platform buffers a get actual power usage response message for device "TEST1024000000001"
      | Status              | OK                    |
      | Description         |                       |
      | RecordTime          | <RecordTime>          |
      | MeterType           | <MeterType>           |
      | TotalConsumedEnergy | <TotalConsumedEnergy> |
      | ActualConsumedPower | <ActualConsumedPower> |
      | TotalLightingHours  | <TotalLightingHours>  |
      | ActualCurrent1      | <ActualCurrent1>      |
      | ActualCurrent2      | <ActualCurrent2>      |
      | ActualCurrent3      | <ActualCurrent3>      |
      | ActualPower1        | <ActualPower1>        |
      | ActualPower2        | <ActualPower2>        |
      | ActualPower3        | <ActualPower3>        |
      | AveragePowerFactor1 | <AveragePowerFactor1> |
      | AveragePowerFactor2 | <AveragePowerFactor2> |
      | AveragePowerFactor3 | <AveragePowerFactor3> |
      | RelayData           | <RelayData>           |

    Examples: 
      | RecordTime          | MeterType | TotalConsumedEnergy | ActualConsumedPower | TotalLightingHours | ActualCurrent1 | ActualCurrent2 | ActualCurrent3 | ActualPower1 | ActualPower2 | ActualPower3 | AveragePowerFactor1 | AveragePowerFactor2 | AveragePowerFactor3 | RelayData   |
      | 2013-02-02T12:00:00 | AUX       |                   0 |                   0 |                  0 |              0 |              0 |              0 |            0 |            0 |            0 |                   0 |                   0 |                   0 |             |
      | 2014-01-01T12:00:00 | PULSE     |                 200 |                1000 |                100 |            200 |            200 |            300 |          100 |          100 |          100 |                 100 |                 100 |                 100 | 1,600;2,480 |
      | 2013-02-02T12:00:00 | AUX       |                  10 |                   1 |                  1 |              1 |              1 |              1 |            1 |            1 |            1 |                   1 |                   1 |                   1 | 1,600;2,480 |
      | 2013-02-02T12:00:00 | P1        |                  10 |                  10 |                 10 |             10 |             10 |             10 |           10 |           10 |           10 |                  10 |                  10 |                  10 | 1,600;2,480 |
      | 2013-02-02T12:00:00 | PULSE     |                   0 |                 200 |                 90 |             90 |              0 |             90 |           90 |            0 |            0 |                   0 |                   0 |                   0 |             |
      | 2014-01-01T12:00:00 | AUX       |                   0 |                  90 |                  0 |             90 |              0 |             90 |           90 |            0 |            0 |                   0 |                   0 |                   0 |             |

  Scenario: Get the actual power usage as an unauthorized organization
    When receiving a get actual power usage request as an unknown organization
      | OrganizationIdentification | TEST1024000000001 |
    Then the get actual power usage response contains soap fault
      | FaultCode | SOAP-ENV:Server      |
      | Message   | UNKNOWN_ORGANISATION |

  Scenario: Get the actual power usage for an unknown device
    When receiving a get actual power usage request
      | OrganizationIdentification | unknown |
    Then the get actual power usage response contains soap fault
      | FaultCode | SOAP-ENV:Server |
      | Message   | UNKNOWN_DEVICE  |
