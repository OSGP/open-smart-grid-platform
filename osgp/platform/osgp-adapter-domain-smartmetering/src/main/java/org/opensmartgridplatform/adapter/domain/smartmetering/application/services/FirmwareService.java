/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceFirmwareModule;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.FirmwareFile;
import org.opensmartgridplatform.domain.core.entities.FirmwareModule;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.repositories.DeviceFirmwareModuleRepository;
import org.opensmartgridplatform.domain.core.repositories.FirmwareFileRepository;
import org.opensmartgridplatform.domain.core.repositories.FirmwareModuleRepository;
import org.opensmartgridplatform.domain.core.repositories.SmartMeterRepository;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareVersion;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service(value = "domainSmartMeteringFirmwareService")
@Transactional(value = "transactionManager")
public class FirmwareService {

  public static final String MODULE_DESCRIPTION_COMMUNICATION_MODULE_ACTIVE_FIRMWARE =
      "communication_module_active_firmware";
  public static final String MODULE_DESCRIPTION_ACTIVE_FIRMWARE = "active_firmware";
  public static final String MODULE_DESCRIPTION_MODULE_ACTIVE_FIRMWARE = "module_active_firmware";

  @Autowired private FirmwareFileRepository firmwareFileRepository;

  @Autowired private FirmwareModuleRepository firmwareModuleRepository;

  @Autowired private DeviceFirmwareModuleRepository deviceFirmwareModuleRepository;

  @Autowired private SmartMeterRepository smartMeterRepository;

  public FirmwareService() {
    // Parameterless constructor required for transactions...
  }

  public FirmwareFile getFirmwareFile(final String firmwareIdentification)
      throws FunctionalException {
    final FirmwareFile firmware =
        this.firmwareFileRepository.findByIdentification(firmwareIdentification);
    if (firmware == null) {
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_FIRMWARE,
          ComponentType.DOMAIN_SMART_METERING,
          new OsgpException(
              ComponentType.DOMAIN_SMART_METERING,
              String.format(
                  "No firmware file found with Identification %s", firmwareIdentification)));
    }
    return firmware;
  }

  public void checkFirmwareFileSupportsDeviceModel(
      final SmartMeter smartMeter, final FirmwareFile firmware) throws FunctionalException {

    final DeviceModel deviceModel = this.determineDeviceModel(smartMeter);
    final List<String> deviceModelCodes =
        firmware.getDeviceModels().stream()
            .map(dm -> dm.getModelCode())
            .collect(Collectors.toList());
    if (!deviceModelCodes.contains(deviceModel.getModelCode())) {
      throw new FunctionalException(
          FunctionalExceptionType.FIRMWARE_DOES_NOT_SUPPORT_DEVICE_MODEL,
          ComponentType.DOMAIN_SMART_METERING,
          new OsgpException(
              ComponentType.DOMAIN_SMART_METERING,
              String.format(
                  "DeviceModel %s of smartmeter %s is not in list of devicemodels supported by firmware file %s : %s",
                  smartMeter.getDeviceModel().getModelCode(),
                  smartMeter.getDeviceIdentification(),
                  firmware.getIdentification(),
                  deviceModelCodes)));
    }
  }

  private DeviceModel determineDeviceModel(final SmartMeter smartMeter) throws FunctionalException {
    final DeviceModel deviceModel = smartMeter.getDeviceModel();
    if (deviceModel == null) {
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_DEVICEMODEL,
          ComponentType.DOMAIN_SMART_METERING,
          new OsgpException(
              ComponentType.DOMAIN_SMART_METERING,
              "No deviceModel for SmartMeter " + smartMeter.getDeviceIdentification()));
    }
    return deviceModel;
  }

  public void saveFirmwareVersionsReturnedFromDevice(
      final String deviceIdentification, final List<FirmwareVersion> firmwareVersions) {

    this.saveFirmwareVersionsReturnedFromDevice(
        this.smartMeterRepository.findByDeviceIdentification(deviceIdentification),
        firmwareVersions);
  }

  private void saveFirmwareVersionsReturnedFromDevice(
      final Device device, final List<FirmwareVersion> firmwareVersions) {

    for (final FirmwareVersion firmwareVersion : firmwareVersions) {
      final FirmwareModule firmwareModule =
          this.firmwareModuleRepository.findByDescriptionIgnoreCase(
              firmwareVersion.getFirmwareModuleType().getDescription());
      if (firmwareModule == null) {
        log.error(
            "Unable to store firmware version {} for device {}, no firmware module found for description \"{}\"",
            firmwareVersion,
            device.getDeviceIdentification(),
            firmwareVersion.getFirmwareModuleType().getDescription());
        continue;
      }

      /*
       * The DeviceFirmwareModule has a id, that prevents duplicates in the database
       * The id of DeviceFirmwareModule is made with a combination of deviceId and firmwareModuleId
       * There for we can just add a new object to the database here
       */
      final DeviceFirmwareModule deviceFirmwareModule =
          new DeviceFirmwareModule(device, firmwareModule, firmwareVersion.getVersion());
      this.deviceFirmwareModuleRepository.save(deviceFirmwareModule);
    }
  }
}
