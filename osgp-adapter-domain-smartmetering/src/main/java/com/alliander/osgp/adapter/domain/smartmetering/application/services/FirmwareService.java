/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.services;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Firmware;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.repositories.FirmwareRepository;
import com.alliander.osgp.domain.core.repositories.SmartMeterRepository;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.FirmwareModuleType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.FirmwareVersion;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;

@Service(value = "domainSmartMeteringFirmwareService")
@Transactional(value = "transactionManager")
public class FirmwareService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirmwareService.class);

    @Autowired
    private FirmwareRepository firmwareRepository;

    @Autowired
    private SmartMeterRepository smartMeterRepository;

    public FirmwareService() {
        // Parameterless constructor required for transactions...
    }

    public String determineFirmwareIdentifier(final SmartMeter smartMeter,
            final Map<FirmwareModuleType, String> firmwareVersionByModuleType) throws FunctionalException {

        final DeviceModel deviceModel = this.determineDeviceModel(smartMeter);
        return this.determineFirmwareIdentifier(deviceModel, firmwareVersionByModuleType);
    }

    private DeviceModel determineDeviceModel(final SmartMeter smartMeter) throws FunctionalException {
        final DeviceModel deviceModel = smartMeter.getDeviceModel();
        if (deviceModel == null) {
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICEMODEL,
                    ComponentType.DOMAIN_SMART_METERING, new OsgpException(ComponentType.DOMAIN_SMART_METERING,
                            "No deviceModel for SmartMeter " + smartMeter.getDeviceIdentification()));
        }
        return deviceModel;
    }

    private String determineFirmwareIdentifier(final DeviceModel deviceModel,
            final Map<FirmwareModuleType, String> firmwareVersionByModuleType) throws FunctionalException {

        final String moduleVersionComm = firmwareVersionByModuleType.get(FirmwareModuleType.COMMUNICATION);
        final String moduleVersionMa = firmwareVersionByModuleType.get(FirmwareModuleType.MODULE_ACTIVE);
        final String moduleVersionFunc = firmwareVersionByModuleType.get(FirmwareModuleType.ACTIVE_FIRMWARE);

        final Firmware firmware = this.firmwareRepository
                .findByDeviceModelAndModuleVersionCommAndModuleVersionMaAndModuleVersionFunc(deviceModel,
                        moduleVersionComm, moduleVersionMa, moduleVersionFunc);

        if (firmware == null) {
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_FIRMWARE, ComponentType.DOMAIN_SMART_METERING,
                    new OsgpException(ComponentType.DOMAIN_SMART_METERING,
                            "No firmware for DeviceModel " + deviceModel.getModelCode() + ", manufacturer "
                                    + deviceModel.getManufacturerId().getManufacturerId() + " and firmware versions "
                                    + firmwareVersionByModuleType));
        }
        return firmware.getIdentification();
    }

    public SmartMeter storeFirmware(final SmartMeter smartMeter, final String firmwareIdentification,
            final List<FirmwareVersion> firmwareVersions, final String organisationIdentification)
            throws FunctionalException {

        final Map<FirmwareModuleType, String> firmwareVersionByModuleType = this
                .getFirmwareVersionByModuleType(firmwareVersions);

        final Firmware firmware = this.firmwareRepository.findByIdentification(firmwareIdentification);

        this.checkFirmwareIsUpdated(FirmwareModuleType.COMMUNICATION, firmware.getModuleVersionComm(),
                firmwareVersionByModuleType, smartMeter.getDeviceIdentification());
        this.checkFirmwareIsUpdated(FirmwareModuleType.MODULE_ACTIVE, firmware.getModuleVersionMa(),
                firmwareVersionByModuleType, smartMeter.getDeviceIdentification());
        this.checkFirmwareIsUpdated(FirmwareModuleType.ACTIVE_FIRMWARE, firmware.getModuleVersionFunc(),
                firmwareVersionByModuleType, smartMeter.getDeviceIdentification());

        smartMeter.setFirmware(firmware, organisationIdentification);
        return this.smartMeterRepository.save(smartMeter);
    }

    public Map<FirmwareModuleType, String> getFirmwareVersionByModuleType(final List<FirmwareVersion> firmwareVersions)
            throws FunctionalException {

        final Map<FirmwareModuleType, String> firmwareVersionByModuleType = new EnumMap<>(FirmwareModuleType.class);

        for (final FirmwareVersion firmwareVersion : firmwareVersions) {
            switch (firmwareVersion.getType()) {
            case COMMUNICATION:
                // fall-through
            case MODULE_ACTIVE:
                // fall-through
            case ACTIVE_FIRMWARE:
                firmwareVersionByModuleType.put(firmwareVersion.getType(), firmwareVersion.getVersion());
                break;
            default:
                LOGGER.error("Cannot handle firmware version type: {}", firmwareVersion.getType().name());
                throw new FunctionalException(FunctionalExceptionType.UNKNOWN_FIRMWARE,
                        ComponentType.DOMAIN_SMART_METERING);
            }
        }

        return firmwareVersionByModuleType;
    }

    private void checkFirmwareIsUpdated(final FirmwareModuleType firmwareModuleType, final String versionToBeInstalled,
            final Map<FirmwareModuleType, String> firmwareVersionsByModuleType, final String deviceIdentification)
            throws FunctionalException {

        if (StringUtils.isBlank(versionToBeInstalled)) {
            /*
             * Firmware for this module type was not in the firmware file. Any
             * version reported by the device is OK.
             */
            return;
        }

        final String versionReportedByDevice = firmwareVersionsByModuleType.get(firmwareModuleType);
        if (!versionToBeInstalled.equals(versionReportedByDevice)) {
            LOGGER.error(
                    "Firmware version \"{}\" of type {} not installed on device {}, "
                            + "which reported \"{}\" (all versions returned: {})",
                    versionToBeInstalled, firmwareModuleType, deviceIdentification, versionReportedByDevice,
                    firmwareVersionsByModuleType);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.DOMAIN_SMART_METERING,
                    new OsgpException(ComponentType.DOMAIN_SMART_METERING,
                            "Expected " + firmwareModuleType.getDescription() + " version \"" + versionToBeInstalled
                                    + "\", device has \"" + versionReportedByDevice + "\""));
        }
    }
}
