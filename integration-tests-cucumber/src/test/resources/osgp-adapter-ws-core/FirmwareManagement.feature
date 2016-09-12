Feature: Firmware management
  As OSGP 
  I want to manage the firmware of a device
  In order to ...

  Scenario Outline: Get firmware version
    Given an oslp device
      | DeviceIdentification | D01               |
      | Status               | Active            |
      | Organization         | Test Organization |
      | IsActivated          | True              |
    And the device returns firmware version "<Firmware Version>" over OSLP
    When receiving a get firmware version request
      | DeviceIdentification | D01 |
    Then the get firmware version async response contains
      | DeviceIdentification | D01 |
    And a get firmware version OSLP message is sent to device "D01"
    And the platform buffers a get firmware version response message for device "D01"
      | FirmwareVersion    | <Firmware Version>     |
      | FirmwareModuleType | <Firmware Module Type> |

    Examples: 
      | Firmware Version | Firmware Module Type |
      | 0123             | FUNCTIONAL           |

  Scenario Outline: Get firmware version from inactive device
    Given an oslp device
      | DeviceIdentification | D01               |
      | Status               | <Status>          |
      | Organization         | Test Organization |
      | IsActivated          | True              |
    When receiving a get firmware version request
      | DeviceIdentification | D01 |
    Then no get firmware version oslp message is sent to device with deviceidentification "D01"

    Examples: 
      | Status       | 
      | unknown      | 
      | unregistered |

  # Because the device message status is not implemented in the protocol, the following tests cannot be performed:
  #     | Status       | Message Status | Reason                         |
  #     | active       | FAILURE        | DEVICEMESSAGEFAILEDEXCEPTION   |
  #     | active       | REJECTED       | DEVICEMESSAGEREJECTEDEXCEPTION |
  
  #!2 Scenario: Receive A Get Firmware Response Request
  #-| scenario | get firmware response input values | correlationid || deviceid || isfound || deviceid2 || qresult || qdescription || firmwareversion || result || description|
  #|given|a get firmware version response request with correlationId @correlationid and deviceId @deviceid                                                                                                                |
  #|and  |a get firmware version response message with correlationId @correlationid, deviceId @deviceid2, qresult @qresult, qdescription @qdescription and firmwareversion @firmwareversion is found in the queue @isfound|
  #|when |the get firmware version response request is received                                                                                                                                                           |
  #|then |the get firmware version response request should return a firmware response with result @result, description @description and firmwareversion @firmwareversion                                                  |
  #-| get firmware response input values |
  #|correlationid|deviceid|isfound|deviceid2|qresult|qdescription    |firmwareversion|result   |description     |
  #|cid-01       |dvc-01  |true   |dvc-01   |OK     |                |R01            |OK       |                |
  #|cid-02       |dvc-01  |false  |         |       |                |               |NOT_FOUND|                |
  #|cid-03       |dvc-01  |true   |dvc-01   |NOT_OK |VALIDATION ERROR|               |NOT_OK   |VALIDATION ERROR|
  #!2 Parameters:
  # * correlationId: correlation Id returned from a get firmware request
  # * content: message content
  # * result: OK or exception
  Scenario Outline: Receive a get firmware response request
    Given a get firmware version response request
      | Field         | Value           |
      | correlationId | <CorrelationId> |
      | deviceId      | <DeviceId>      |
    And a get firmware version response message <ExistsInQueue> in the queue
      | Field           | Value             |
      | correlationId   | <CorrelationId>   |
      | deviceId        | <DeviceId2>       |
      | qresult         | <qresult>         |
      | qdescription    | <qdescription>    |
      | firmwareversion | <Firmwareversion> |
    When receiving a get firmware version response request
      | Field    | Value     |
      | deviceId | device-01 |
    Then ensure the get firmware version response request contains
      | Field           | Value             |  |
      | result          | <result>          |  |
      | description     | <description>     |  |
      | firmwareversion | <firmwareversion> |  |

    Examples: 
      | CorrelationId | DeviceId | ExistsInQueue | DeviceId2 | qresult | qdescription     | Firmwareversion | Result    | Description      |
      | cid-01        | dvc-01   | true          | dvc-01    | OK      |                  | v0.1            | OK        |                  |
      | cid-02        | dvc-01   | false         |           |         |                  |                 | NOT_FOUND |                  |
      | cid-03        | dvc-01   | true          | dvc-01    | NOT_OK  | VALIDATION ERROR |                 | NOT_OK    | VALIDATION ERROR |

  #!2 Scenario: Successfully send firmware update request
  #-| scenario | firmware success matrix | firmwareDomainConfig | | firmwarePathConfig | | firmwareExtensionConfig | | organisation | | device | | firmwareName | | firmwareDomain | | firmwareUrl | | ismessagesent | | result ||
  #|given|a firmware update request for device @device, firmwareName @firmwareName                                                                             |
  #|and  |a firmware location configuration with @firmwareDomainConfig, @firmwarePathConfig, and @firmwareExtensionConfig                                      |
  #|and  |an OSGP client @organisation                                                                                                                          |
  #|and  |an authorized device @device                                                                                                                         |
  #|and  |the update firmware oslp message from the device                                                                                                     |
  #|when |the update firmware request is received                                                                                                              |
  #|then |the update firmware request should return an async response with a correlationId and deviceId @device                                                |
  #|and  |an update firmware oslp message is sent to device @device with firmwareName @firmwareName and firmwareDomain @firmwareDomain should be @ismessagesent|
  #|and  |an ovl update firmware message with result @result should be sent to the ovl out queue                                                               |
  #-| firmware success matrix |
  #|firmwareDomainConfig               |firmwarePathConfig|firmwareExtensionConfig|organisation   |device |firmwareName|firmwareDomain          |firmwareUrl           |ismessagesent|result|
  #|flexovltest.cloudapp.net           |/firmware         |hex                    |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/firmware/AME-v0.1.hex|true         |OK    |
  #|flexovltest.cloudapp.net           |firmware          |hex                    |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/firmware/AME-v0.1.hex|true         |OK    |
  #|flexovltest.cloudapp.net           |firmware/         |hex                    |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/firmware/AME-v0.1.hex|true         |OK    |
  #|flexovltest.cloudapp.net           |/firmware/        |hex                    |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/firmware/AME-v0.1.hex|true         |OK    |
  #|flexovltest.cloudapp.net           |/firmware         |.hex                   |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/firmware/AME-v0.1.hex|true         |OK    |
  #|flexovltest.cloudapp.net/          |/firmware         |hex                    |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/firmware/AME-v0.1.hex|true         |OK    |
  #|!-http://flexovltest.cloudapp.net-!|/firmware         |hex                    |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/firmware/AME-v0.1.hex|true         |OK    |
  #|!-ftp://flexovltest.cloudapp.net-! |/firmware         |hex                    |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/firmware/AME-v0.1.hex|true         |OK    |
  #|flexovltest.cloudapp.net           |                  |hex                    |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/AME-v0.1.hex         |true         |OK    |
  #|flexovltest.cloudapp.net           |/firmware         |                       |ORGANISATION-01|device1|AME-v0.1    |flexovltest.cloudapp.net|/firmware/AME-v0.1    |true         |OK    |
  Scenario Outline: Update firmware version
    Given a device
      | Field                | Value             |
      | DeviceIdentification | testdevice        |
      | firmwareVersion      | <FirmwareVersion> |
    And a firmware location configuration
      | Field                   | Value                     |
      | firmwareDomainConfig    | <firmwareDomainConfig>    |
      | firmwarePathConfig      | <firmwarePathConfig>      |
      | firmwareExtensionConfig | <firmwareExtensionConfig> |
    And an OSGP client <organisation>
    And an authorized device <device>
    And the update firmware oslp message from the device
    When receiving an update firmware request
      | Field    | Value      |
      | deviceId | testdevice |
    Then the update firmware request should return an async response with a correlationId and deviceId <device>
    And an update firmware oslp message is sent to device <device> with firmwareName <firmwareName> and firmwareDomain <firmwareDomain> should be <ismessagesent>
    And an ovl update firmware message with result <result> should be sent to the ovl out queue

    Examples: 
      | firmwareDomainConfig                | firmwarePathConfig | firmwareExtensionConfig | organisation    | device  | firmwareName | firmwareDomain           | firmwareUrl            | ismessagesent | result |
      | flexovltest.cloudapp.net            | /firmware          | hex                     | ORGANISATION-01 | device1 | AME-v0.1     | flexovltest.cloudapp.net | /firmware/AME-v0.1.hex | true          | OK     |
      | flexovltest.cloudapp.net            | firmware           | hex                     | ORGANISATION-01 | device1 | AME-v0.1     | flexovltest.cloudapp.net | /firmware/AME-v0.1.hex | true          | OK     |
      | flexovltest.cloudapp.net            | firmware/          | hex                     | ORGANISATION-01 | device1 | AME-v0.1     | flexovltest.cloudapp.net | /firmware/AME-v0.1.hex | true          | OK     |
      | flexovltest.cloudapp.net            | /firmware/         | hex                     | ORGANISATION-01 | device1 | AME-v0.1     | flexovltest.cloudapp.net | /firmware/AME-v0.1.hex | true          | OK     |
      | flexovltest.cloudapp.net            | /firmware          | .hex                    | ORGANISATION-01 | device1 | AME-v0.1     | flexovltest.cloudapp.net | /firmware/AME-v0.1.hex | true          | OK     |
      | flexovltest.cloudapp.net/           | /firmware          | hex                     | ORGANISATION-01 | device1 | AME-v0.1     | flexovltest.cloudapp.net | /firmware/AME-v0.1.hex | true          | OK     |
      | !-http://flexovltest.cloudapp.net-! | /firmware          | hex                     | ORGANISATION-01 | device1 | AME-v0.1     | flexovltest.cloudapp.net | /firmware/AME-v0.1.hex | true          | OK     |
      | !-ftp://flexovltest.cloudapp.net-!  | /firmware          | hex                     | ORGANISATION-01 | device1 | AME-v0.1     | flexovltest.cloudapp.net | /firmware/AME-v0.1.hex | true          | OK     |
      | flexovltest.cloudapp.net            |                    | hex                     | ORGANISATION-01 | device1 | AME-v0.1     | flexovltest.cloudapp.net | /AME-v0.1.hex          | true          | OK     |
      | flexovltest.cloudapp.net            | /firmware          |                         | ORGANISATION-01 | device1 | AME-v0.1     | flexovltest.cloudapp.net | /firmware/AME-v0.1     | true          | OK     |

  #!2 Scenario: Incorrect firmware configuration
  #
  #-| scenario | firmware error matrix | firmwareDomainConfig | | firmwarePathConfig | | firmwareExtensionConfig | | organisation | | device | | firmwareName | | error || ismessagesent | | result ||
  #|given|a firmware update request for device @device, firmwareName @firmwareName                                                                             |
  #|and  |a firmware location configuration with @firmwareDomainConfig, @firmwarePathConfig, and @firmwareExtensionConfig                                      |
  #|and  |an OSGP client @organisation                                                                                                                          |
  #|and  |an authorized device @device                                                                                                                         |
  #|and  |the update firmware oslp message from the device                                                                                                     |
  #|when |the update firmware request is received                                                                                                              |
  #|then |the update firmware request should return an async response with a correlationId and deviceId @device                                                |
  #|and  |an update firmware oslp message is sent to device @device with firmwareName @firmwareName and firmwareDomain @firmwareDomain should be @ismessagesent|
  #|and  |an ovl update firmware message with result @result should be sent to the ovl out queue                                                               |
  #
  #
  #-| firmware error matrix |
  #|firmwareDomainConfig    |firmwarePathConfig|firmwareExtensionConfig|organisation   |device |firmwareName|error                     |ismessagesent|result|
  #|                        |/firmware         |hex                    |ORGANISATION-01|device1|AME-v0.1    |!-ConfigurationException-!|false        |NOT OK|
  Scenario Outline: Update firmware configuration with incorrect data
    Given a device
      | Field                | Value             |
      | DeviceIdentification | testdevice        |
      | firmwareVersion      | <FirmwareVersion> |
    And a firmware location configuration
      | Field                   | Value                     |
      | firmwareDomainConfig    | <firmwareDomainConfig>    |
      | firmwarePathConfig      | <firmwarePathConfig>      |
      | firmwareExtensionConfig | <firmwareExtensionConfig> |
    And an OSGP client <organisation>
    And an authorized device <device>
    And the update firmware oslp message from the device
    When the update firmware request is received
      | Field    | Value      |
      | deviceId | testdevice |
    Then the update firmware request should return an async response with a correlationId and deviceId <device>
    And an update firmware oslp message is sent to device <device> with firmwareName <firmwareName> and firmwareDomain <firmwareDomain> should be <ismessagesent>
    And an ovl update firmware message with result <result> should be sent to the ovl out queue

    Examples: 
      | firmwareDomainConfig | firmwarePathConfig | firmwareExtensionConfig | organisation    | device  | firmwareName | error                      | ismessagesent | result |
      |                      | /firmware          | hex                     | ORGANISATION-01 | device1 | AME-v0.1     | !-ConfigurationException-! | false         | NOT OK |

  #!2 Scenario: Invalid firmware update request
  #
  #-| scenario | firmware error matrix | firmwareDomainConfig | | firmwarePathConfig | | firmwareExtensionConfig | | organisation | | device | | firmwareName | | error || ismessagesent | | result ||
  #|given|a firmware update request for device @device, firmwareName @firmwareName                                                                             |
  #|and  |a firmware location configuration with @firmwareDomainConfig, @firmwarePathConfig, and @firmwareExtensionConfig                                      |
  #|and  |an OSGP client @organisation                                                                                                                          |
  #|and  |an authorized device @device                                                                                                                         |
  #|and  |the update firmware oslp message from the device                                                                                                     |
  #|when |the update firmware request is received                                                                                                              |
  #|then |the update firmware request should return a validation error                                                |
  #|and  |an update firmware oslp message is sent to device @device with firmwareName @firmwareName and firmwareDomain @firmwareDomain should be @ismessagesent|
  #
  #
  #-| firmware error matrix |
  #|firmwareDomainConfig    |firmwarePathConfig|firmwareExtensionConfig|organisation   |device |firmwareName|error                     |ismessagesent|result|
  #|flexovltest.cloudapp.net|/firmware         |hex                    |ORGANISATION-01|device1|            |!-ValidationException-!   |false        |NOT OK|
  Scenario Outline: Update firmware configuration with invalid data
    Given a device
      | Field                | Value          |
      | DeviceIdentification | testdevice     |
      | firmwareName         | <firmwareName> |
    And a firmware location configuration
      | Field                | Value                     |
      | firmwareDomainConfig | <firmwareDomainConfig>    |
      | firmwarePathConfig   | <firmwareExtensionConfig> |
    And an OSGP client <organisation>
    And an authorized device <device>
    And the update firmware oslp message from the device
    When the update firmware request is received
      | Field    | Value      |
      | deviceId | testdevice |
    Then the update firmware request should return a validation error                                                |
    And an update firmware oslp message is sent to device <device> with firmwareName <firmwareName> and firmwareDomain <firmwareDomain> should be <ismessagesent>

    Examples: 
      | firmwareDomainConfig     | firmwarePathConfig | firmwareExtensionConfig | organisation    | device  | firmwareName | error                   | ismessagesent | result |
      | flexovltest.cloudapp.net | /firmware          | hex                     | ORGANISATION-01 | device1 |              | !-ValidationException-! | false         | NOT OK |

  #!2 Scenario: Receive An Update Firmware Response Request
  #-| scenario | update firmware response input values | correlationid || deviceid || isfound || deviceid2 || qresult || qdescription || result || description|
  #|given|an update firmware response request with correlationId @correlationid and deviceId @deviceid                                                                              |
  #|and  |an update firmware response message with correlationId @correlationid, deviceId @deviceid2, qresult @qresult and qdescription @qdescription is found in the queue @isfound|
  #|when |the update firmware response request is received                                                                                                                          |
  #|then |the update firmware response request should return a firmware response with result @result and description @description                                                   |
  #
  #-| update firmware response input values |
  #|correlationid|deviceid|isfound|deviceid2|qresult|qdescription    |result   |description     |
  #|cid-01       |dvc-01  |true   |dvc-01   |OK     |                |OK       |                |
  #|cid-02       |dvc-01  |false  |         |       |                |NOT_FOUND|                |
  #|cid-03       |dvc-01  |true   |dvc-01   |NOT_OK |VALIDATION ERROR|NOT_OK   |VALIDATION ERROR|
  #
  #!2 Parameters:
  # * correlationId: correlation Id returned from a update firmware request
  # * content: message content
  # * result: OK or exception
  Scenario Outline: Recieve an update firmware response request
    Given an update firmware response request with correlationId <correlationid> and deviceId <deviceid>
    And an update firmware response message with correlationId <correlationid>, deviceId <deviceid2>, qresult <qresult> and qdescription <qdescription> is found in the queue <isfound>
    When the update firmware response request is received
    Then the update firmware response request should return a firmware response with result <result> and description <description>

    Examples: 
      | Correlationid | DeviceId | IsFound | DeviceId2 | qresult | qdescription     | Result    | Description      |
      | cid-01        | dvc-01   | true    | dvc-01    | OK      |                  | OK        |                  |
      | cid-02        | dvc-01   | false   |           |         |                  | NOT_FOUND |                  |
      | cid-03        | dvc-01   | true    | dvc-01    | NOT_OK  | VALIDATION ERROR | NOT_OK    | VALIDATION ERROR |
