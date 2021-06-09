@Common @Platform @FirmwareManagement
Feature: FirmwareManagement add firmware
  As OSGP 
  I want to add the firmware of a device
  In order to ...

  Scenario: Add firmware with File Contents but without related FirmwareModules
    Given a device model
      | ModelCode              | DeviceModelDBStorageModel_test |
      | DeviceModelFileStorage | false                          |
    When receiving an add firmware request
      | FirmwareFile             | 57696520646974206c656573742069732067656b                                         |
      | FirmwareFilename         | NewFirmware_test1                                                                |
      | ManufacturerName         | Test                                                                             |
      | ModelCode                | DeviceModelDBStorageModel_test                                                   |
      | FirmwareDescription      | Firmware is newly created with File Contents but without related FirmwareModules |
    Then the add firmware response contains
      | Result | OK |

  Scenario: Add firmware without File Content and without related DeviceModel and FirmwareModules
    Given a device model
      | ModelCode              | DeviceModelDBStorageModel_test  |
      | DeviceModelFileStorage | false                           |
    When receiving an add firmware request
      | FirmwareFilename          | NewFirmware_test2                                                                         |
      | ManufacturerName          | Test                                                                                      |
      | ModelCode                 | DeviceModelDBStorageModel_test                                                            |
      | FirmwareDescription       | Firmware is newly created without File Contents and without related FirmwareModules       |
    Then the add firmware response contains soap fault
      | FaultCode    | SOAP-ENV:Server                                             |
      | FaultString  | UNKNOWN_FIRMWARE                                            |
      | InnerMessage | DeviceModel with id "NewFirmware_test2" could not be found. |

  Scenario: Add firmware without File Content (but Firmware File in DB) and without related FirmwareModules
    Given a device model
      | ModelCode              | DeviceModelDBStorageModel_test  |
      | DeviceModelFileStorage | false                           |
    And a firmware
      | DeviceIdentification     | TEST1024000000001              |
      | FirmwareFilename         | NewFirmware_test3              |
      | FirmwarePushToNewDevices | true                           |
      | ManufacturerName         | Test                           |
      | ModelCode                | DeviceModelDBStorageModel_test |
      | Description              |                                |
    When receiving an add firmware request
      | FirmwareFilename          | NewFirmware_test3                                                                                                 |
      | ManufacturerName          | Test                                                                                                              |
      | ModelCode                 | DeviceModelDBStorageModel_test                                                                                    |
      | FirmwareDescription       | Firmware is newly created without File Content (but Firmware File in DB) and without related FirmwareModules      |
    Then the add firmware response contains
      | Result | OK |

  Scenario: Add firmware with File Contents and with related FirmwareModules
    Given a device model
      | ModelCode              | DeviceModelDBStorageModel_test |
      | DeviceModelFileStorage | false                          |
    When receiving an add firmware request
      | FirmwareFile              | 57696520646974206c656573742069732067656b                                      |
      | FirmwareFilename          | NewFirmware_test4                                                             |
      | ManufacturerName          | Test                                                                          |
      | ModelCode                 | DeviceModelDBStorageModel_test                                                |
      | FirmwareDescription       | Firmware is newly created with File Contents and with related FirmwareModules |
      | FirmwareModuleVersionComm | comm_1.2                                                                      |
      | FirmwareModuleVersionFunc | func_1.3                                                                      |
      | FirmwareModuleVersionMa   | ma_1.4                                                                        |
      | FirmwareModuleVersionMbus | mbus_1.5                                                                      |
      | FirmwareModuleVersionSec  | sec_1.6                                                                       |
      | FirmwareModuleVersionMbda | mbda_1.7                                                                      |
    Then the add firmware response contains
      | Result | OK |

