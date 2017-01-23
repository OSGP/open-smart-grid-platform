Feature: PublicLightingDeviceMonitoring get actual power usage
  In order to ...
  As a ...
  I want to ...

  @OslpMockServer
  Scenario Outline: Get power usage history
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
    And the device returns a get actual power usage response over OSLP
      | Status              | <Status>              |
      | RecordTime          | <RecordTime>          |
      | Index               | <Index>               |
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
    When receiving a get power usage history request
      | DeviceIdentification | TEST1024000000001 |
      | FromDate             | <FromDate>        |
      | UntilDate            | <UntilDate>       |
    Then the get power usage history async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a get power usage history OSLP message is sent to the device
      | FromDate  | <FromDate>  |
      | UntilDate | <UntilDate> |
    And the platform buffers a get actual power usage response message for device "TEST1024000000001"
      | Status              | <Status>              |
      | Description         | <Description>         |
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
      | fromDate       | untilDate      | recordTime     | index | meterType | totalConsumedEnergy | actualConsumedPower | TotalLightingHours | actualCurrent1 | actualCurrent2 | actualCurrent3 | actualPower1 | actualPower2 | actualPower3 | averagePowerFactor1 | averagePowerFactor2 | averagePowerFactor3 | RelayData   | Result | Description |
      | 20120202120000 | 20130202120000 | 20130202120000 |     1 | PULSE     |                1000 |                 200 |                100 |            240 |            360 |             12 |           15 |           15 |           15 |                  15 |                  15 |                  15 | 1,600;2,480 | OK     |             |
      | 20120202120000 | 20130202120000 | 20130202120000 |     1 | P1        |                  10 |                 400 |                400 |            140 |            160 |            112 |          115 |           15 |           15 |                  15 |                  15 |                  15 | 1,600;2,480 | OK     |             |
      | 20110202120000 | 20140202120000 | 20130202120000 |     1 | AUX       |                 200 |                 360 |                100 |            240 |            360 |             12 |           15 |           15 |           15 |                  15 |                  15 |                  15 | 1,600;2,480 | OK     |             |

  Scenario: Get the power usage history as an unauthorized organization
    When receiving a get power usage history request as an unknown organization
      | OrganizationIdentification | TEST1024000000001 |
    Then the get power usage history response contains soap fault
      | Message | UNKNOWN_ORGANISATION |

  Scenario: Get the power usage history for an unknown device
    When receiving a get power usage history request
      | OrganizationIdentification | unknown |
    Then the get power usage history response contains soap fault
      | Message | UNKNOWN_DEVICE |
