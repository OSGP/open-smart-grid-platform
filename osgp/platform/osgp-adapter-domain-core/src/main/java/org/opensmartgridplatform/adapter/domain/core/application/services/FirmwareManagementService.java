/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.application.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceFirmwareFile;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.FirmwareFile;
import org.opensmartgridplatform.domain.core.entities.FirmwareFileFirmwareModule;
import org.opensmartgridplatform.domain.core.entities.FirmwareModule;
import org.opensmartgridplatform.domain.core.entities.Manufacturer;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.entities.SsldPendingFirmwareUpdate;
import org.opensmartgridplatform.domain.core.repositories.DeviceFirmwareFileRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceModelRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.FirmwareFileFirmwareModuleRepository;
import org.opensmartgridplatform.domain.core.repositories.FirmwareFileRepository;
import org.opensmartgridplatform.domain.core.repositories.ManufacturerRepository;
import org.opensmartgridplatform.domain.core.repositories.SsldPendingFirmwareUpdateRepository;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleData;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleType;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareUpdateMessageDataContainer;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareVersion;
import org.opensmartgridplatform.dto.valueobjects.FirmwareVersionDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.shared.validation.Identification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service(value = "domainCoreFirmwareManagementService")
@Transactional(value = "transactionManager")
public class FirmwareManagementService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManagementService.class);

    private static final String INSTALLER = "Inserted to match the version reported by the device.";

    @Autowired
    private DeviceFirmwareFileRepository deviceFirmwareFileRepository;

    @Autowired
    private DeviceModelRepository deviceModelRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private FirmwareFileRepository firmwareFileRepository;

    @Autowired
    private FirmwareFileFirmwareModuleRepository firmwareFileFirmwareModuleRepository;

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Autowired
    private SsldPendingFirmwareUpdateRepository ssldPendingFirmwareUpdateRepository;

    @Autowired
    private SsldRepository ssldRepository;

    /**
     * Constructor
     */
    public FirmwareManagementService() {
        // Parameterless constructor required for transactions...
    }

    // === UPDATE FIRMWARE ===

    public void updateFirmware(final CorrelationIds ids,
            final FirmwareUpdateMessageDataContainer firmwareUpdateMessageDataContainer, final Long scheduleTime,
            final String messageType, final int messagePriority) throws FunctionalException {

        LOGGER.debug("Update firmware called with organisation [{}], device [{}], firmwareIdentification [{}].",
                ids.getOrganisationIdentification(), ids.getDeviceIdentification(),
                firmwareUpdateMessageDataContainer.getFirmwareUrl());

        this.findOrganisation(ids.getOrganisationIdentification());
        final Device device = this.findActiveDevice(ids.getDeviceIdentification());

        if (device instanceof Ssld) {
            final Ssld ssld = this.findSsldForDevice(device);
            this.createSsldPendingFirmwareUpdateRecord(ids, ssld,
                    firmwareUpdateMessageDataContainer.getFirmwareModuleData(),
                    firmwareUpdateMessageDataContainer.getFirmwareUrl());
        }

        this.osgpCoreRequestMessageSender.send(
                new RequestMessage(ids,
                        this.domainCoreMapper.map(firmwareUpdateMessageDataContainer,
                                org.opensmartgridplatform.dto.valueobjects.FirmwareUpdateMessageDataContainer.class)),
                messageType, messagePriority, device.getIpAddress(), scheduleTime);
    }

    private void createSsldPendingFirmwareUpdateRecord(final CorrelationIds ids, final Ssld ssld,
            final FirmwareModuleData firmwareModuleData, final String firmwareUrl) {
        try {
            final String[] split = firmwareUrl.split("/");
            Assert.isTrue(split.length >= 1, "Splitting URL on / failed!");
            final String firmwareFilename = split[split.length - 1];

            final List<FirmwareFile> firmwareFiles = this.firmwareFileRepository.findByFilename(firmwareFilename);
            Assert.isTrue(firmwareFiles.size() == 1, "Expected 1 firmware file for filename: " + firmwareFilename);
            final FirmwareFile firmwareFile = firmwareFiles.get(0);

            final FirmwareFileFirmwareModule firmwareFileFirmwareModule = this.firmwareFileFirmwareModuleRepository
                    .findByFirmwareFile(firmwareFile);
            final String firmwareVersion = firmwareFileFirmwareModule.getModuleVersion();

            final FirmwareModuleType firmwareModuleType = firmwareModuleData.getFirmwareModuleType();

            SsldPendingFirmwareUpdate ssldPendingFirmwareUpdate = new SsldPendingFirmwareUpdate(true,
                    firmwareModuleType, firmwareVersion, "CORE", "1.0", ids.getOrganisationIdentification());
            ssldPendingFirmwareUpdate = this.ssldPendingFirmwareUpdateRepository.save(ssldPendingFirmwareUpdate);

            ssld.setSsldPendingFirmwareUpdate(ssldPendingFirmwareUpdate);
            this.ssldRepository.save(ssld);

            LOGGER.info("Saved pending fimware update record for SSLD: {}, {}", ssld.getDeviceIdentification(),
                    ssldPendingFirmwareUpdate);
        } catch (final Exception e) {
            LOGGER.error("Caugth exception when creating pending firmware update record for SSLD: {}",
                    ssld.getDeviceIdentification(), e);
        }
    }

    public void handleSsldPendingFirmwareUpdate(final String organisationIdentification,
            final String deviceIdentification, final String correlationUid) {

        final Ssld ssld = this.ssldRepository.findByDeviceIdentification(deviceIdentification);
        final SsldPendingFirmwareUpdate ssldPendingFirmwareUpdate = ssld.getSsldPendingFirmwareUpdate();
        if (ssldPendingFirmwareUpdate.hasPendingFirmwareUpdate()) {
            LOGGER.info(
                    "Handling SSLD pending firmware update for device identification: {}, organisation identification: {} and correlation UID: {}.",
                    deviceIdentification, organisationIdentification, correlationUid);

            ssldPendingFirmwareUpdate.setCorrelationUid(correlationUid);
            this.ssldPendingFirmwareUpdateRepository.save(ssldPendingFirmwareUpdate);

            try {
                this.getFirmwareVersion(organisationIdentification, deviceIdentification, correlationUid,
                        DeviceFunction.GET_FIRMWARE_VERSION.name(), 4);
            } catch (final FunctionalException e) {
                LOGGER.error("Caught exception when calling get firmware version", e);
            }
        }
    }

    // === GET FIRMWARE VERSION ===

    public void getFirmwareVersion(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final String correlationUid, final String messageType,
            final int messagePriority) throws FunctionalException {

        LOGGER.debug("Get firmware version called with organisation [{}], device [{}].", organisationIdentification,
                deviceIdentification);

        this.findOrganisation(organisationIdentification);
        final Device device = this.findActiveDevice(deviceIdentification);

        this.osgpCoreRequestMessageSender.send(
                new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, null), messageType,
                messagePriority, device.getIpAddress());
    }

    public void handleGetFirmwareVersionResponse(final List<FirmwareVersionDto> firmwareVersionsDto,
            final CorrelationIds ids, final String messageType, final int messagePriority,
            final ResponseMessageResultType deviceResult, final OsgpException exception) {

        LOGGER.info("handleResponse for MessageType: {}", messageType);

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        OsgpException osgpException = exception;

        try {
            if (deviceResult == ResponseMessageResultType.NOT_OK || osgpException != null) {
                LOGGER.error("Device Response not ok.", osgpException);
                throw osgpException;
            }
        } catch (final Exception e) {
            LOGGER.error("Unexpected Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            osgpException = new TechnicalException(ComponentType.UNKNOWN,
                    "Exception occurred while getting device firmware version", e);
        }
        final List<FirmwareVersion> firmwareVersions = this.domainCoreMapper.mapAsList(firmwareVersionsDto,
                FirmwareVersion.class);

        this.checkFirmwareHistory(ids.getDeviceIdentification(), firmwareVersions);

        final boolean isPendingFirmwareUpdate = this.checkSsldPendingFirmwareUpdate(ids, firmwareVersions);

        if (!isPendingFirmwareUpdate) {
            final ResponseMessage responseMessage = ResponseMessage.newResponseMessageBuilder()
                    .withIds(ids)
                    .withResult(result)
                    .withOsgpException(osgpException)
                    .withDataObject((Serializable) firmwareVersions)
                    .withMessagePriority(messagePriority)
                    .build();
            this.webServiceResponseMessageSender.send(responseMessage);
        }
    }

    private boolean checkSsldPendingFirmwareUpdate(final CorrelationIds ids,
            final List<FirmwareVersion> firmwareVersions) {

        final String deviceIdentification = ids.getDeviceIdentification();
        final String organisationIdentification = ids.getOrganisationIdentification();
        final String correlationUid = ids.getCorrelationUid();

        final Ssld ssld = this.ssldRepository.findByDeviceIdentification(deviceIdentification);
        final SsldPendingFirmwareUpdate ssldPendingFirmwareUpdate = ssld.getSsldPendingFirmwareUpdate();
        if (ssldPendingFirmwareUpdate == null) {
            return false;
        }
        if (!organisationIdentification.equals(ssldPendingFirmwareUpdate.getOrganisationIdentification())) {
            return false;
        }
        if (!correlationUid.equals(ssldPendingFirmwareUpdate.getCorrelationUid())) {
            return false;
        }

        LOGGER.info("Found SSLD pending firmware update record for device identification: {}, {}.",
                deviceIdentification, ssldPendingFirmwareUpdate);

        final FirmwareModuleType expectedFirmwareModuleType = ssldPendingFirmwareUpdate.getFirmwareModuleType();
        final String expectedFirmwareVersion = ssldPendingFirmwareUpdate.getFirmwareVersion();
        boolean foundExpectedFirmwareVersion = false;

        for (final FirmwareVersion firmwareVersion : firmwareVersions) {
            if (expectedFirmwareModuleType.equals(firmwareVersion.getFirmwareModuleType())
                    && expectedFirmwareVersion.equals(firmwareVersion.getVersion())) {
                foundExpectedFirmwareVersion = true;
                break;
            }
        }

        if (foundExpectedFirmwareVersion) {
            LOGGER.info(
                    "Expected firmware version from SSLD pending firmware update record matches firmware version as retrieved from device identification: {}, firmware version: {}, firmware module type: {}.",
                    deviceIdentification, expectedFirmwareVersion, expectedFirmwareModuleType);
        } else {
            LOGGER.error(
                    "Expected firmware version from SSLD pending firmware update record does not match firmware version as retrieved from device identification: {}, expected firmware version: {}, expected firmware module type: {}, actual firmware version and module type list:",
                    deviceIdentification, expectedFirmwareVersion, expectedFirmwareModuleType);
            for (final FirmwareVersion firmwareVersion : firmwareVersions) {
                LOGGER.error(" - {} {}", firmwareVersion.getFirmwareModuleType().name(), firmwareVersion.getVersion());
            }
        }

        this.ssldPendingFirmwareUpdateRepository.delete(ssldPendingFirmwareUpdate);
        ssld.setSsldPendingFirmwareUpdate(null);
        this.ssldRepository.save(ssld);
        return true;
    }

    private void checkFirmwareHistory(final String deviceId,
            final List<org.opensmartgridplatform.domain.core.valueobjects.FirmwareVersion> firmwareVersions) {
        final List<org.opensmartgridplatform.domain.core.valueobjects.FirmwareVersion> versionsNotInHistory = this
                .checkFirmwareHistoryForVersion(deviceId, firmwareVersions);
        for (final org.opensmartgridplatform.domain.core.valueobjects.FirmwareVersion firmwareVersion : versionsNotInHistory) {
            LOGGER.info("Firmware version {} is not in history of device {}, we are trying to add it", firmwareVersion,
                    deviceId);
            this.tryToAddFirmwareVersionToHistory(deviceId, firmwareVersion);
        }
    }

    /**
     * @param organisationIdentification
     *            the organisation the device we want to check belongs to
     * @param deviceId
     *            the id of the device we are checking
     * @param firmwareVersions
     *            the list of firmware versions to check if they are in the
     *            history of the devices firmware history
     * @return a list of firmware versions not present in the the devices
     *         firmware history
     * @throws FunctionalException
     */
    public List<FirmwareVersion> checkFirmwareHistoryForVersion(final String deviceId,
            final List<FirmwareVersion> firmwareVersions) {

        if (firmwareVersions.isEmpty()) {
            return firmwareVersions;
        }
        // copy input parameter
        final List<FirmwareVersion> firmwareVersionsToCheck = new ArrayList<>();
        firmwareVersionsToCheck.addAll(firmwareVersions);

        // get history
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceId);
        final List<DeviceFirmwareFile> deviceFirmwareFiles = this.deviceFirmwareFileRepository
                .findByDeviceOrderByInstallationDateAsc(device);
        final List<FirmwareVersion> firmwareVersionsInHistory = deviceFirmwareFiles.stream()
                .map(d -> d.getFirmwareFile().getModuleVersions().entrySet())
                .flatMap(Collection::stream)
                .map(e -> new FirmwareVersion(FirmwareModuleType.forDescription(e.getKey().getDescription()),
                        e.getValue()))
                .collect(Collectors.toList());

        // remove the history versions
        firmwareVersionsToCheck.removeAll(firmwareVersionsInHistory);

        return firmwareVersionsToCheck;
    }

    public void tryToAddFirmwareVersionToHistory(final String deviceIdentification,
            final FirmwareVersion firmwareVersion) {

        final FirmwareModule module = createFirmwareModule(firmwareVersion);
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
        final List<FirmwareFile> firmwareFiles = this.getAvailableFirmwareFilesForDeviceModel(device.getDeviceModel());

        // check each file for the module and the version as returned by the
        // device
        boolean recordAdded = false;

        for (final FirmwareFile file : firmwareFiles) {
            final Map<FirmwareModule, String> moduleVersions = file.getModuleVersions();
            if (moduleVersions.containsKey(module) && moduleVersions.get(module).equals(firmwareVersion.getVersion())) {

                // file found, insert a record into the history
                final DeviceFirmwareFile deviceFirmwareFile = new DeviceFirmwareFile(device, file, new Date(),
                        INSTALLER);
                this.deviceFirmwareFileRepository.save(deviceFirmwareFile);
                LOGGER.info("Firmware version {} added to device {}", firmwareVersion.getVersion(),
                        deviceIdentification);

                // we only want to add one record in history
                recordAdded = true;
                break;
            }
        }

        if (!recordAdded) {
            LOGGER.warn("No firmware file record found for: {} for device: {}", firmwareVersion, deviceIdentification);
        }

    }

    private static FirmwareModule createFirmwareModule(final FirmwareVersion firmwareVersion) {
        final String description = firmwareVersion.getFirmwareModuleType().getDescription().toLowerCase(
                Locale.getDefault());
        return new FirmwareModule(description);
    }

    private List<FirmwareFile> getAvailableFirmwareFilesForDeviceModel(final DeviceModel deviceModel) {
        final Manufacturer manufacturer = deviceModel.getManufacturer();

        return this.findAllFirmwareFiles(manufacturer.getCode(), deviceModel.getModelCode());
    }

    private List<FirmwareFile> findAllFirmwareFiles(final String manufacturer, final String modelCode) {
        List<FirmwareFile> firmwareFiles = new ArrayList<>();
        if (manufacturer != null) {
            final Manufacturer databaseManufacturer = this.manufacturerRepository.findByCode(manufacturer);
            final DeviceModel databaseDeviceModel = this.deviceModelRepository
                    .findByManufacturerAndModelCode(databaseManufacturer, modelCode);
            if (databaseDeviceModel != null) {
                firmwareFiles = this.firmwareFileRepository.findByDeviceModel(databaseDeviceModel);
            }
        } else {
            final List<DeviceModel> deviceModels = this.deviceModelRepository.findByModelCode(modelCode);
            for (final DeviceModel deviceModel : deviceModels) {
                firmwareFiles.addAll(this.firmwareFileRepository.findByDeviceModel(deviceModel));
            }
        }

        return firmwareFiles;
    }

    // === SWITCH TO OTHER FIRMWARE VERSION ===

    public void switchFirmware(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final String messageType, final int messagePriority, final String version)
            throws FunctionalException {
        LOGGER.debug("switchFirmware called with organisation {} and device {}", organisationIdentification,
                deviceIdentification);

        this.findOrganisation(organisationIdentification);
        final Device device = this.findActiveDevice(deviceIdentification);

        this.osgpCoreRequestMessageSender.send(
                new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, version),
                messageType, messagePriority, device.getIpAddress());
    }
}
