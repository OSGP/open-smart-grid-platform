# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@Common @Platform @FirmwareManagement
Feature: FirmwareManagement add or change firmware
  As OSGP 
  I want to add a new or change an existing firmware
  In order to ...

  Scenario: Add firmware with File Contents
    Given a device model
      | ModelCode              | DeviceModelDBStorageModel_test |
      | DeviceModelFileStorage | false                          |
    When receiving an add or change firmware request
      | FirmwareFileIdentification | 1234567890ABCBEF                                     |
      | FirmwareFile               | 57696520646974206c656573742069732067656b             |
      | FirmwareFilename           | NewFirmware_test1                                    |
      | ManufacturerName           | Test                                                 |
      | ModelCode                  | DeviceModelDBStorageModel_test                       |
      | FirmwareDescription        | Add 1 - Firmware is newly created with File Contents |
      | FirmwareModuleVersionComm  | comm_1.2                                             |
    Then the add or change firmware response contains
      | Result | OK |
    And the firmware file '1234567890ABCBEF' exists
      | FirmwareFile               | 57696520646974206c656573742069732067656b             |
      | FirmwareFilename           | NewFirmware_test1                                    |
      | FirmwareDescription        | Add 1 - Firmware is newly created with File Contents |
      | FirmwareModuleVersionComm  | comm_1.2                                             |

  Scenario: Add firmware without File Content
    Given a device model
      | ModelCode              | DeviceModelDBStorageModel_test  |
      | DeviceModelFileStorage | false                           |
    When receiving an add or change firmware request
      | FirmwareFileIdentification | 1234567890ABCBEF                                        |
      | ManufacturerName           | Test                                                    |
      | ModelCode                  | DeviceModelDBStorageModel_test                          |
      | FirmwareDescription        | Add 2 - Firmware is newly created without File Contents |
      | FirmwareModuleVersionComm  | comm_1.2                                                |
    Then the add or change firmware response contains
      | Result | OK |
    And the firmware file '1234567890ABCBEF' exists
      | FirmwareDescription       | Add 2 - Firmware is newly created without File Contents |
      | FirmwareModuleVersionComm | comm_1.2                                                |

  Scenario: Add firmware with File Contents and with related FirmwareModules
    Given a device model
      | ModelCode              | DeviceModelDBStorageModel_test1 |
      | DeviceModelFileStorage | false                           |
    When receiving an add or change firmware request
      | FirmwareFileIdentification | 1234567890ABCBEF                                               |
      | FirmwareFile               | 57696520646974206c656573742069732067656b                       |
      | FirmwareFilename           | NewFirmware_test3                                              |
      | FirmwareDescription        | Add 3 - Firmware is newly created with related FirmwareModules |
      | FirmwareModuleVersionComm  | comm_1.2                                                       |
      | FirmwareModuleVersionFunc  | func_1.3                                                       |
      | FirmwareModuleVersionMa    | ma_1.4                                                         |
      | FirmwareModuleVersionMbus  | mbus_1.5                                                       |
      | FirmwareModuleVersionSec   | sec_1.6                                                        |
      | FirmwareModuleVersionMbda  | mbda_1.7                                                       |  
      | ModelCode                  | DeviceModelDBStorageModel_test1                                |
      | DeviceModelDescription     | DeviceModelDBStorageModel_desc1                                |
      | ManufacturerName           | Test                                                           |
    Then the add or change firmware response contains
      | Result | OK |
    And the firmware file '1234567890ABCBEF' exists
      | FirmwareFile               | 57696520646974206c656573742069732067656b                       |
      | FirmwareFilename           | NewFirmware_test3                                              |
      | FirmwareDescription        | Add 3 - Firmware is newly created with related FirmwareModules |
    And the firmware file '1234567890ABCBEF' has module versions
      | FirmwareModuleVersionComm  | comm_1.2                                                       |
      | FirmwareModuleVersionFunc  | func_1.3                                                       |
      | FirmwareModuleVersionMa    | ma_1.4                                                         |
      | FirmwareModuleVersionMbus  | mbus_1.5                                                       |
      | FirmwareModuleVersionSec   | sec_1.6                                                        |
      | FirmwareModuleVersionMbda  | mbda_1.7                                                       |  
      
  Scenario: Add firmware with File Contents and with multiple related DeviceModels
    Given a device model
      | ModelCode              | DeviceModelDBStorageModel_test1 |
      | DeviceModelFileStorage | false                           |
    And a device model
      | ModelCode              | DeviceModelDBStorageModel_test2 |
      | DeviceModelFileStorage | false                           |
    And a device model
      | ModelCode              | DeviceModelDBStorageModel_test3 |
      | DeviceModelFileStorage | false                           |
    When receiving an add or change firmware request
      | FirmwareFileIdentification | 1234567890ABCBEF                                                                                |
      | FirmwareFile               | 57696520646974206c656573742069732067656b                                                        |
      | FirmwareFilename           | NewFirmware_test4                                                                               |
      | FirmwareDescription        | Add 4 - Firmware is newly created with multiple related DeviceModels                            |
      | ModelCode                  | DeviceModelDBStorageModel_test1;DeviceModelDBStorageModel_test2;DeviceModelDBStorageModel_test3 |
      | DeviceModelDescription     | DeviceModelDBStorageModel_desc1;DeviceModelDBStorageModel_desc2;DeviceModelDBStorageModel_desc3 |
      | ManufacturerName           | Test;Test;Test                                                                                  |
      | FirmwareModuleVersionComm  | comm_1.2                                                                                        |
    Then the add or change firmware response contains
      | Result | OK |    
    And the firmware file '1234567890ABCBEF' exists
      | FirmwareFile              | 57696520646974206c656573742069732067656b                                                        |
      | FirmwareFilename          | NewFirmware_test4                                                                               |
      | FirmwareDescription       | Add 4 - Firmware is newly created with multiple related DeviceModels                            |
    And the firmware file '1234567890ABCBEF' has device models
      | ModelCode                 | DeviceModelDBStorageModel_test1;DeviceModelDBStorageModel_test2;DeviceModelDBStorageModel_test3 |
      
  Scenario: Add firmware with File Contents and without related DeviceModel
    Given a device model
      | ModelCode              | DeviceModelDBStorageModel_test |
      | DeviceModelFileStorage | false                          |
    When receiving an add or change firmware request
      | FirmwareFileIdentification | 1234567890ABCBEF                                                                     |
      | FirmwareFile              | 57696520646974206c656573742069732067656b                                              |
      | FirmwareFilename          | NewFirmware_test5                                                                     |
      | FirmwareDescription       | Add 5 - Firmware is newly created with File Contents and without related DeviceModels |
      | FirmwareModuleVersionComm | comm_1.2                                                                              |
    Then the add or change firmware response contains
      | Result | OK |    
    And the firmware file '1234567890ABCBEF' exists
      | FirmwareFile              | 57696520646974206c656573742069732067656b                                              |
      | FirmwareFilename          | NewFirmware_test5                                                                     |
      | FirmwareDescription       | Add 5 - Firmware is newly created with File Contents and without related DeviceModels |
     
  Scenario: Change firmware with File Content - Change Description
    Given a device model
      | ModelCode              | DeviceModelDBStorageModel_test  |
      | DeviceModelFileStorage | false                           |
    And a firmware
      | FirmwareFileIdentification | 1234567890ABCBEF                         |
      | FirmwareFile               | 57696520646974206c656573742069732067656b |
      | FirmwareFilename           | NewFirmware_test6                        |
      | FirmwarePushToNewDevices   | true                                     |
      | ManufacturerName           | Test                                     |
      | ModelCode                  | DeviceModelDBStorageModel_test           |
      | FirmwareDescription        | This is some description                 |
    When receiving an add or change firmware request
      | FirmwareFileIdentification | 1234567890ABCBEF                                      |
      | FirmwarePushToNewDevices   | true                                                  |
      | ManufacturerName           | Test                                                  |
      | ModelCode                  | DeviceModelDBStorageModel_test                        |
      | FirmwareDescription        | Change 1 - Firmware is changed  - Added Description   |
      | FirmwareModuleVersionComm  | comm_1.2                                              |
    Then the add or change firmware response contains
      | Result | OK |
    And the firmware file '1234567890ABCBEF' exists
      | FirmwareFile               | 57696520646974206c656573742069732067656b            |
      | FirmwareFilename           | NewFirmware_test6                                   |
      | FirmwarePushToNewDevices   | true                                                |
      | ManufacturerName           | Test                                                |
      | ModelCode                  | DeviceModelDBStorageModel_test                      |
      | FirmwareDescription        | Change 1 - Firmware is changed  - Added Description |

  Scenario: Change firmware without File Content - Add File Content
    Given a device model
      | ModelCode              | DeviceModelDBStorageModel_test  |
      | DeviceModelFileStorage | false                           |
    And a firmware
      | FirmwareFileIdentification | 1234567890ABCBEF                         |
      | FirmwarePushToNewDevices   | true                                     |
      | ManufacturerName           | Test                                     |
      | ModelCode                  | DeviceModelDBStorageModel_test           |
      | FirmwareDescription        | This is some description                 |
    When receiving an add or change firmware request
      | FirmwareFileIdentification | 1234567890ABCBEF                                      |
      | FirmwareFile               | 57696520646974206c656573742069732067656b              |
      | FirmwareFilename           | NewFirmware_test4                                     |
      | FirmwarePushToNewDevices   | true                                                  |
      | ManufacturerName           | Test                                                  |
      | ModelCode                  | DeviceModelDBStorageModel_test                        |
      | FirmwareDescription        | Change 2 - Firmware is changed  - Added File Content  |
      | FirmwareModuleVersionComm  | comm_1.2                                              |
    Then the add or change firmware response contains
      | Result | OK |
    And the firmware file '1234567890ABCBEF' exists
      | FirmwareFile               | 57696520646974206c656573742069732067656b              |
      | FirmwareFilename           | NewFirmware_test4                                     |
      | FirmwarePushToNewDevices   | true                                                  |
      | FirmwareDescription        | Change 2 - Firmware is changed  - Added File Content  |

  Scenario: Change firmware without related DeviceModels- Add related DeviceModels
    Given a device model
      | ModelCode              | DeviceModelDBStorageModel_test1 |
      | DeviceModelFileStorage | false                           |
    And a device model
      | ModelCode              | DeviceModelDBStorageModel_test2 |
      | DeviceModelFileStorage | false                           |
    And a device model
      | ModelCode              | DeviceModelDBStorageModel_test3 |
      | DeviceModelFileStorage | false                           |
    And a firmware
      | FirmwareFileIdentification | 1234567890ABCBEF                         |
      | FirmwarePushToNewDevices   | true                                     |
      | FirmwareDescription        | This is some description                 |
      | ModelCode                  | DeviceModelDBStorageModel_test1          |
    When receiving an add or change firmware request
      | FirmwareFileIdentification | 1234567890ABCBEF                                             |
      | FirmwarePushToNewDevices   | true                                                         |
      | FirmwareDescription        | Change 3 - Firmware is changed  - Added related DeviceModels |
      | ModelCode                  | DeviceModelDBStorageModel_test1;DeviceModelDBStorageModel_test2;DeviceModelDBStorageModel_test3 |
      | DeviceModelDescription     | DeviceModelDBStorageModel_desc1;DeviceModelDBStorageModel_desc2;DeviceModelDBStorageModel_desc3 |
      | ManufacturerName           | Test;Test;Test                                                                                  |
      | FirmwareModuleVersionComm  | comm_1.2                                                                                        |
    Then the add or change firmware response contains
      | Result | OK |
    And the firmware file '1234567890ABCBEF' exists
      | FirmwarePushToNewDevices   | true                                                         |
      | FirmwareDescription        | Change 3 - Firmware is changed  - Added related DeviceModels |
 
  Scenario: Change firmware without related DeviceModels- Add related DeviceModels
    Given a device model
      | ModelCode              | DeviceModelDBStorageModel_test1 |
      | DeviceModelFileStorage | false                           |
    And a firmware
      | FirmwareFileIdentification | 1234567890ABCBEF                         |
      | FirmwarePushToNewDevices   | true                                     |
      | FirmwareDescription        | This is some description                 |
      | ModelCode                  | DeviceModelDBStorageModel_test1          |
      | FirmwareModuleVersionComm  | comm_1.2                                 |
      | FirmwareModuleVersionFunc  | func_1.3                                 |
      | FirmwareModuleVersionMa    | ma_1.4                                   |
      | FirmwareModuleVersionMbus  | mbus_1.5                                 |
      | FirmwareModuleVersionSec   | sec_1.6                                  |
      | FirmwareModuleVersionMbda  | mbda_1.7                                 |  
    When receiving an add or change firmware request
      | FirmwareFileIdentification | 1234567890ABCBEF                                          |
      | FirmwarePushToNewDevices   | true                                                      |
      | FirmwareDescription        | Change 4 - Firmware is changed  - Changed Module Versions |
      | FirmwareModuleVersionComm  | comm_2.2                                                  |
      | FirmwareModuleVersionFunc  | func_2.3                                                  |
      | FirmwareModuleVersionMa    | ma_2.4                                                    |
      | FirmwareModuleVersionMbus  | mbus_2.5                                                  |
      | FirmwareModuleVersionSec   | sec_2.6                                                   |
      | FirmwareModuleVersionMbda  | mbda_2.7                                                  |     
    Then the add or change firmware response contains
      | Result | OK |
    And the firmware file '1234567890ABCBEF' exists
      | FirmwarePushToNewDevices   | true                                                      |
      | FirmwareDescription        | Change 4 - Firmware is changed  - Changed Module Versions |
    And the firmware file '1234567890ABCBEF' has module versions
      | FirmwareModuleVersionComm  | comm_2.2                                                  |
      | FirmwareModuleVersionFunc  | func_2.3                                                  |
      | FirmwareModuleVersionMa    | ma_2.4                                                    |
      | FirmwareModuleVersionMbus  | mbus_2.5                                                  |
      | FirmwareModuleVersionSec   | sec_2.6                                                   |
      | FirmwareModuleVersionMbda  | mbda_2.7                                                  |