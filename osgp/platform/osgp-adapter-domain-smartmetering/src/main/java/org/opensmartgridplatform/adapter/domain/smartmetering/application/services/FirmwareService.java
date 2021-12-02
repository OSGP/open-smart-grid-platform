/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleType;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareVersion;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "domainSmartMeteringFirmwareService")
@Transactional(value = "transactionManager")
public class FirmwareService {

  private static final Logger LOGGER = LoggerFactory.getLogger(FirmwareService.class);

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

  public String determineFirmwareFileIdentifier(
      final SmartMeter smartMeter,
      final Map<FirmwareModuleType, String> firmwareVersionByModuleType)
      throws FunctionalException {

    final DeviceModel deviceModel = this.determineDeviceModel(smartMeter);
    return this.determineFirmwareFileIdentifier(deviceModel, firmwareVersionByModuleType);
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

  private String determineFirmwareFileIdentifier(
      final DeviceModel deviceModel,
      final Map<FirmwareModuleType, String> firmwareVersionByModuleType)
      throws FunctionalException {

    this.checkFirmwareModuleTypes(firmwareVersionByModuleType.keySet());

    final List<FirmwareFile> firmwareFileCandidates =
        this.determineCandidateFirmwareFiles(deviceModel, firmwareVersionByModuleType);

    final List<FirmwareFile> firmwareFiles =
        this.filterCandidateFirmwareFiles(firmwareFileCandidates, firmwareVersionByModuleType);

    if (firmwareFiles.isEmpty()) {
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_FIRMWARE,
          ComponentType.DOMAIN_SMART_METERING,
          new OsgpException(
              ComponentType.DOMAIN_SMART_METERING,
              "No FirmwareFile for DeviceModel "
                  + deviceModel.getModelCode()
                  + ", manufacturer "
                  + deviceModel.getManufacturer().getCode()
                  + " and firmware versions "
                  + firmwareVersionByModuleType));
    }
    if (firmwareFiles.size() > 1) {
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_FIRMWARE,
          ComponentType.DOMAIN_SMART_METERING,
          new OsgpException(
              ComponentType.DOMAIN_SMART_METERING,
              "No unique FirmwareFile (found "
                  + firmwareFiles.size()
                  + ") for DeviceModel "
                  + deviceModel.getModelCode()
                  + ", manufacturer "
                  + deviceModel.getManufacturer().getCode()
                  + " and firmware versions "
                  + firmwareVersionByModuleType));
    }

    return firmwareFiles.get(0).getIdentification();
  }

  private List<FirmwareFile> filterCandidateFirmwareFiles(
      final List<FirmwareFile> firmwareFileCandidates,
      final Map<FirmwareModuleType, String> firmwareVersionByModuleType)
      throws FunctionalException {

    final List<FirmwareFile> firmwareFilesFiltered = new ArrayList<>();
    for (final FirmwareFile firmwareFile : firmwareFileCandidates) {
      if (this.hasFirmwareModulesWithVersion(firmwareFile, firmwareVersionByModuleType)) {
        firmwareFilesFiltered.add(firmwareFile);
      }
    }
    return firmwareFilesFiltered;
  }

  private boolean hasFirmwareModulesWithVersion(
      final FirmwareFile firmwareFile,
      final Map<FirmwareModuleType, String> firmwareVersionByModuleType)
      throws FunctionalException {

    final Map<FirmwareModule, String> moduleVersionsWithFirmwareFile =
        firmwareFile.getModuleVersions();

    if (firmwareVersionByModuleType.size() != moduleVersionsWithFirmwareFile.size()) {
      return false;
    }

    for (final Entry<FirmwareModule, String> versionByModule :
        moduleVersionsWithFirmwareFile.entrySet()) {
      final String moduleDescription = versionByModule.getKey().getDescription();
      final String moduleVersion = versionByModule.getValue();
      final String filterVersion;
      switch (moduleDescription) {
        case MODULE_DESCRIPTION_ACTIVE_FIRMWARE:
          filterVersion = firmwareVersionByModuleType.get(FirmwareModuleType.ACTIVE_FIRMWARE);
          break;
        case MODULE_DESCRIPTION_COMMUNICATION_MODULE_ACTIVE_FIRMWARE:
          filterVersion = firmwareVersionByModuleType.get(FirmwareModuleType.COMMUNICATION);
          break;
        case MODULE_DESCRIPTION_MODULE_ACTIVE_FIRMWARE:
          filterVersion = firmwareVersionByModuleType.get(FirmwareModuleType.MODULE_ACTIVE);
          break;
        default:
          throw new FunctionalException(
              FunctionalExceptionType.UNKNOWN_FIRMWARE,
              ComponentType.DOMAIN_SMART_METERING,
              new OsgpException(
                  ComponentType.DOMAIN_SMART_METERING,
                  "Unable to match firmware file with module \""
                      + moduleDescription
                      + "\" with smart meters."));
      }

      if (!moduleVersion.equals(filterVersion)) {
        return false;
      }
    }

    return true;
  }

  private void checkFirmwareModuleTypes(final Set<FirmwareModuleType> firmwareModuleTypes)
      throws FunctionalException {

    for (final FirmwareModuleType firmwareModuleType : firmwareModuleTypes) {
      switch (firmwareModuleType) {
        case ACTIVE_FIRMWARE:
          // fall-through
        case COMMUNICATION:
          // fall-through
        case MODULE_ACTIVE:
          // Smart Meter related firmware: OK.
          break;
        default:
          throw new FunctionalException(
              FunctionalExceptionType.UNKNOWN_FIRMWARE,
              ComponentType.DOMAIN_SMART_METERING,
              new OsgpException(
                  ComponentType.DOMAIN_SMART_METERING,
                  "Unable to determine firmware file with module of type "
                      + firmwareModuleType
                      + " with smart meters."));
      }
    }
  }

  /**
   * Returns a list of firmware files matching only a single version from the provided map. This
   * needs to be filtered further to take all versions into account.
   *
   * @see
   *     FirmwareFileRepository#findFirmwareFilesForDeviceModelContainingModuleWithVersion(DeviceModel,
   *     String, String)
   */
  private List<FirmwareFile> determineCandidateFirmwareFiles(
      final DeviceModel deviceModel,
      final Map<FirmwareModuleType, String> firmwareVersionByModuleType)
      throws FunctionalException {

    if (firmwareVersionByModuleType.isEmpty()) {
      return Collections.emptyList();
    }

    final Entry<FirmwareModuleType, String> versionByModuleType =
        firmwareVersionByModuleType.entrySet().iterator().next();
    final String moduleVersion = versionByModuleType.getValue();
    final String moduleDescription;
    final FirmwareModuleType firmwareModuleType = versionByModuleType.getKey();
    switch (firmwareModuleType) {
      case ACTIVE_FIRMWARE:
        moduleDescription = MODULE_DESCRIPTION_ACTIVE_FIRMWARE;
        break;
      case COMMUNICATION:
        moduleDescription = MODULE_DESCRIPTION_COMMUNICATION_MODULE_ACTIVE_FIRMWARE;
        break;
      case MODULE_ACTIVE:
        moduleDescription = MODULE_DESCRIPTION_MODULE_ACTIVE_FIRMWARE;
        break;
      default:
        throw new FunctionalException(
            FunctionalExceptionType.UNKNOWN_FIRMWARE,
            ComponentType.DOMAIN_SMART_METERING,
            new OsgpException(
                ComponentType.DOMAIN_SMART_METERING,
                "Unable to determine firmware file with module of type "
                    + firmwareModuleType
                    + " with smart meters."));
    }

    return this.firmwareFileRepository.findFirmwareFilesForDeviceModelContainingModuleWithVersion(
        deviceModel, moduleDescription, moduleVersion);
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
        LOGGER.error(
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

  public Map<FirmwareModuleType, String> getFirmwareVersionByModuleType(
      final List<FirmwareVersion> firmwareVersions) throws FunctionalException {

    final Map<FirmwareModuleType, String> firmwareVersionByModuleType =
        new EnumMap<>(FirmwareModuleType.class);

    for (final FirmwareVersion firmwareVersion : firmwareVersions) {
      switch (firmwareVersion.getFirmwareModuleType()) {
        case COMMUNICATION:
          // fall-through
        case MODULE_ACTIVE:
          // fall-through
        case ACTIVE_FIRMWARE:
          firmwareVersionByModuleType.put(
              firmwareVersion.getFirmwareModuleType(), firmwareVersion.getVersion());
          break;
        default:
          LOGGER.error(
              "Cannot handle firmware version type: {}", firmwareVersion.getFirmwareModuleType());
          throw new FunctionalException(
              FunctionalExceptionType.UNKNOWN_FIRMWARE, ComponentType.DOMAIN_SMART_METERING);
      }
    }

    return firmwareVersionByModuleType;
  }
}
