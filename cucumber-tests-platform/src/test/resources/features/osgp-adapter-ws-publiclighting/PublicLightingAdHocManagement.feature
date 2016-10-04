Feature: Adhoc Management
  In order to ... 
  As a platform 
  I want to asynchronously handle set light requests

# TODO: The index parameter is not yet implemented.
  @OslpMockServer
  Scenario Outline: Receive A Set Light Request With A Single Light Value
    Given an oslp device
      | DeviceIdentification | D01      |
      | Status               | <Status> |
    And the device returns a set light "<Result>" over OSLP
    When receiving a set light request
      | DeviceIdentification | D01        |
      | Index                | <Index>    |
      | On                   | <On>       |
    Then the set light async response contains
      | DeviceIdentification | D01 |
    And a set light OSLP message is sent to device "D01"
    And the platform buffers a set light response message for device "D01"

    Examples: 
      | Status | Index | On    | Result | Description |
      | active |       | true  | OK     |             |
