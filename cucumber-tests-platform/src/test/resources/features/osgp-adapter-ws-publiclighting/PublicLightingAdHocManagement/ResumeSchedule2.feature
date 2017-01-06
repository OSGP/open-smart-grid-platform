Feature: Adhoc Management
  In order to ... 
  As a platform 
  I want to asynchronously handle set light requests

  # Note: This test doesn't return a 'Validation error', so this test doesn't fail.
  @OslpMockServer
  Scenario Outline: Resume Schedule for a device with no has schedule
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
      | HasSchedule          | false             |
    When receiving a resume schedule request
      | DeviceIdentification | TEST1024000000001 |
      | Index                | <Index>           |
      | IsImmediate          | <IsImmediate>     |
    Then the resume schedule async response contains
      | DeviceIdentification | TEST1024000000001 |
    And the platform buffers a get resume schedule response message for device "TEST1024000000001"
      | FaultString | <FaultString> |

    Examples: 
      | Index | IsImmediate | FaultString        |
      |     1 | true        | UNSCHEDULED_DEVICE |
