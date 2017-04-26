@PublicLighting @Platform @PublicLightingDeviceMonitoring
Feature: PublicLightingDeviceMonitoring Get Power Usage History
  In order to ...
  As a ...
  I want to ...

  @OslpMockServer
  Scenario Outline: Get power usage history
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    And the device returns a get power usage history response "OK" over "<Protocol>"
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
    And a get power usage history "<Protocol>" message is sent to the device
      | FromDate  | <FromDate>  |
      | UntilDate | <UntilDate> |
    And the platform buffers a get power usage history response message for device "TEST1024000000001"
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
      | Protocol    | FromDate            | UntilDate           | RecordTime          | Index | MeterType | TotalConsumedEnergy | ActualConsumedPower | TotalLightingHours | ActualCurrent1 | ActualCurrent2 | ActualCurrent3 | ActualPower1 | ActualPower2 | ActualPower3 | AveragePowerFactor1 | AveragePowerFactor2 | AveragePowerFactor3 | RelayData                       |
      | OSLP        | 2013-02-02T12:00:00 | 2014-02-02T12:00:00 | 2013-02-02T12:00:00 |     1 | AUX       |                   0 |                   0 |                  0 |              0 |              0 |              0 |            0 |            0 |            0 |                   0 |                   0 |                   0 |                                 |
      | OSLP        | 2012-02-02T12:00:00 | 2013-02-02T12:00:00 | 2013-02-02T12:00:00 |     1 | PULSE     |                1000 |                 200 |                100 |            240 |            360 |             12 |           15 |           15 |           15 |                  15 |                  15 |                  15 | 1,600;2,480                     |
      | OSLP        | 2012-02-02T12:00:00 | 2013-02-02T12:00:00 | 2013-02-02T12:00:00 |     1 | P1        |                  10 |                 400 |                400 |            140 |            160 |            112 |          115 |           15 |           15 |                  15 |                  15 |                  15 | 1,600;2,480                     |
      | OSLP        | 2013-02-02T12:00:00 | 2014-02-02T12:00:00 | 2013-02-02T12:00:00 |     1 | PULSE     |                1000 |                 200 |                100 |            240 |            360 |             12 |           15 |           15 |           15 |                  15 |                  15 |                  15 | 1,600;2,480                     |
      | OSLP        | 2011-02-02T12:00:00 | 2014-02-02T12:00:00 | 2013-02-02T12:00:00 |     1 | AUX       |                 200 |                 360 |                 12 |             15 |             15 |             15 |           15 |           15 |           15 |                   1 |                 600 |                   2 | 0,480;                          |
      | OSLP        | 2015-02-02T12:00:00 | 2017-02-02T12:00:00 | 2016-01-04T22:00:00 |     1 | P1        |               55229 |                2370 |               7110 |             10 |             20 |             30 |           10 |           20 |           30 |                  10 |                  20 |                  30 | 1,99897;2,99897;3,99897;4,99897 |
      | OSLP ELSTER | 2013-02-02T12:00:00 | 2014-02-02T12:00:00 | 2013-02-02T12:00:00 |     1 | AUX       |                   0 |                   0 |                  0 |              0 |              0 |              0 |            0 |            0 |            0 |                   0 |                   0 |                   0 |                                 |
      | OSLP ELSTER | 2012-02-02T12:00:00 | 2013-02-02T12:00:00 | 2013-02-02T12:00:00 |     1 | PULSE     |                1000 |                 200 |                100 |            240 |            360 |             12 |           15 |           15 |           15 |                  15 |                  15 |                  15 | 1,600;2,480                     |
      | OSLP ELSTER | 2012-02-02T12:00:00 | 2013-02-02T12:00:00 | 2013-02-02T12:00:00 |     1 | P1        |                  10 |                 400 |                400 |            140 |            160 |            112 |          115 |           15 |           15 |                  15 |                  15 |                  15 | 1,600;2,480                     |
      | OSLP ELSTER | 2013-02-02T12:00:00 | 2014-02-02T12:00:00 | 2013-02-02T12:00:00 |     1 | PULSE     |                1000 |                 200 |                100 |            240 |            360 |             12 |           15 |           15 |           15 |                  15 |                  15 |                  15 | 1,600;2,480                     |
      | OSLP ELSTER | 2011-02-02T12:00:00 | 2014-02-02T12:00:00 | 2013-02-02T12:00:00 |     1 | AUX       |                 200 |                 360 |                 12 |             15 |             15 |             15 |           15 |           15 |           15 |                   1 |                 600 |                   2 | 0,480;                          |
      | OSLP ELSTER | 2015-02-02T12:00:00 | 2017-02-02T12:00:00 | 2016-01-04T22:00:00 |     1 | P1        |               55229 |                2370 |               7110 |             10 |             20 |             30 |           10 |           20 |           30 |                  10 |                  20 |                  30 | 1,99897;2,99897;3,99897;4,99897 |

  Scenario: Get the power usage history as an unauthorized organization
    When receiving a get power usage history request as an unknown organization
      | DeviceIdentification | TEST1024000000001   |
      | FromDate             | 2012-02-02T12:00:00 |
      | UntilDate            | 2013-02-02T12:00:00 |
    Then the get power usage history response contains soap fault
      | FaultCode | SOAP-ENV:Server      |
      | Message   | UNKNOWN_ORGANISATION |

  Scenario: Get the power usage history for an unknown device
    When receiving a get power usage history request
      | DeviceIdentification | unknown             |
      | FromDate             | 2012-02-02T12:00:00 |
      | UntilDate            | 2013-02-02T12:00:00 |
    Then the get power usage history response contains soap fault
      | FaultCode | SOAP-ENV:Server |
      | Message   | UNKNOWN_DEVICE  |
