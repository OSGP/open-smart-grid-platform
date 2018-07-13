/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.ws.core.infra.jms.CommonRequestMessage;
import com.alliander.osgp.adapter.ws.core.infra.jms.CommonRequestMessageSender;
import com.alliander.osgp.adapter.ws.core.infra.jms.CommonRequestMessageType;
import com.alliander.osgp.adapter.ws.core.infra.jms.CommonResponseMessageFinder;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceFirmwareFileRepository;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceModelRepository;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceRepository;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableFirmwareFileRepository;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableManufacturerRepository;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceFirmwareFile;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.FirmwareFile;
import com.alliander.osgp.domain.core.entities.FirmwareModule;
import com.alliander.osgp.domain.core.entities.Manufacturer;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.ExistingEntityException;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.repositories.FirmwareModuleRepository;
import com.alliander.osgp.domain.core.services.CorrelationIdProviderService;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.FirmwareModuleData;
import com.alliander.osgp.domain.core.valueobjects.FirmwareUpdateMessageDataContainer;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunction;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

@Service(value = "wsCoreFirmwareManagementService")
@Transactional(value = "transactionManager")
@Validated
public class FirmwareManagementService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManagementService.class);

    private static final String SPACE_REPLACER = "_";

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    private CommonRequestMessageSender commonRequestMessageSender;

    @Autowired
    private CommonResponseMessageFinder commonResponseMessageFinder;

    @Autowired
    private WritableManufacturerRepository manufacturerRepository;

    @Autowired
    private WritableDeviceModelRepository deviceModelRepository;

    @Autowired
    private WritableFirmwareFileRepository firmwareFileRepository;

    @Autowired
    private FirmwareModuleRepository firmwareModuleRepository;

    @Autowired
    private WritableDeviceRepository deviceRepository;

    @Autowired
    private WritableDeviceFirmwareFileRepository deviceFirmwareFileRepository;

    @Resource
    @Qualifier("wsCoreFirmwareManagementFirmwareDirectory")
    private String firmwareDirectory;

    @Autowired
    private WritableDeviceFirmwareFileRepository writableDeviceFirmwareRepository;

    @Autowired
    private WritableDeviceRepository writableDeviceRepository;

    @Autowired
    private boolean firmwareFileStorage;

    public String enqueueUpdateFirmwareRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification,
            final FirmwareUpdateMessageDataContainer firmwareUpdateMessageDataContainer, final DateTime scheduledTime,
            final int messagePriority) throws FunctionalException {
        LOGGER.debug("Queue update firmware request");

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.UPDATE_FIRMWARE);
        this.domainHelperService.isInMaintenance(device);

        LOGGER.debug("enqueueUpdateFirmwareRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, CommonRequestMessageType.UPDATE_FIRMWARE.name(),
                messagePriority, scheduledTime == null ? null : scheduledTime.getMillis());

        final CommonRequestMessage message = new CommonRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(firmwareUpdateMessageDataContainer).build();

        this.commonRequestMessageSender.send(message);

        return correlationUid;
    }

    public ResponseMessage dequeueUpdateFirmwareResponse(final String correlationUid) throws OsgpException {
        return this.commonResponseMessageFinder.findMessage(correlationUid);
    }

    public String enqueueGetFirmwareRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final int messagePriority) throws FunctionalException {
        LOGGER.debug("Queue get firmware request");

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_FIRMWARE_VERSION);
        this.domainHelperService.isInMaintenance(device);

        LOGGER.debug("enqueueGetFirmwareRequest called with organisation {} and device {}", organisationIdentification,
                deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, CommonRequestMessageType.GET_FIRMWARE_VERSION.name(),
                messagePriority);

        final CommonRequestMessage message = new CommonRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).build();

        this.commonRequestMessageSender.send(message);

        return correlationUid;
    }

    /**
     * Returns a list of all Manufacturers in the Platform
     */
    @Transactional(value = "writableTransactionManager")
    public List<Manufacturer> findAllManufacturers(final String organisationIdentification) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.GET_MANUFACTURERS);

        return this.manufacturerRepository.findAll();
    }

    /**
     * Returns a manufacturers in the Platform
     */
    @Transactional(value = "writableTransactionManager")
    public Manufacturer findManufacturer(final String manufacturerName) throws FunctionalException {
        return this.manufacturerRepository.findByName(manufacturerName);
    }

    /**
     * Adds new Manufacturer to the platform. Throws exception if
     * {@link Manufacturer} already exists
     */
    @Transactional(value = "writableTransactionManager")
    public void addManufacturer(@Identification final String organisationIdentification,
            @Valid final Manufacturer manufacturer) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.CREATE_MANUFACTURER);

        final Manufacturer databaseManufacturer = this.manufacturerRepository.findByCode(manufacturer.getCode());

        if (databaseManufacturer != null) {
            LOGGER.info("Manufacturer already exists.");
            throw new FunctionalException(FunctionalExceptionType.EXISTING_MANUFACTURER, ComponentType.WS_CORE,
                    new ExistingEntityException(Manufacturer.class, manufacturer.getCode()));
        } else {
            this.manufacturerRepository.save(manufacturer);
        }
    }

    /**
     * Updates a Manufacturer to the platform. Throws exception if
     * {@link Manufacturer} doesn't exist.
     */
    @Transactional(value = "writableTransactionManager")
    public void changeManufacturer(@Identification final String organisationIdentification,
            @Valid final Manufacturer manufacturer) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.CHANGE_MANUFACTURER);

        final Manufacturer databaseManufacturer = this.manufacturerRepository.findByCode(manufacturer.getCode());

        if (databaseManufacturer == null) {
            LOGGER.info("Manufacturer not found.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_MANUFACTURER, ComponentType.WS_CORE,
                    new ExistingEntityException(Manufacturer.class, manufacturer.getCode()));
        } else {
            databaseManufacturer.setCode(manufacturer.getCode());
            databaseManufacturer.setName(manufacturer.getName());
            databaseManufacturer.setUsePrefix(manufacturer.isUsePrefix());

            this.manufacturerRepository.save(databaseManufacturer);
        }
    }

    /**
     * Removes a Manufacturer from the platform. Throws exception if
     * {@link Manufacturer} doesn't exist
     */
    @Transactional(value = "writableTransactionManager")
    public void removeManufacturer(@Identification final String organisationIdentification,
            @Valid final String manufacturerCode) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.REMOVE_MANUFACTURER);

        final Manufacturer databaseManufacturer = this.manufacturerRepository.findByCode(manufacturerCode);
        final List<DeviceModel> deviceModels = this.deviceModelRepository.findByManufacturer(databaseManufacturer);

        if (!deviceModels.isEmpty()) {
            LOGGER.info("Manufacturer is linked to a Model.");
            throw new FunctionalException(FunctionalExceptionType.EXISTING_DEVICEMODEL_MANUFACTURER,
                    ComponentType.WS_CORE,
                    new ExistingEntityException(DeviceModel.class, deviceModels.get(0).getModelCode()));
        }

        if (databaseManufacturer == null) {
            LOGGER.info("Manufacturer not found.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_MANUFACTURER, ComponentType.WS_CORE,
                    new ExistingEntityException(Manufacturer.class, manufacturerCode));
        } else {
            this.manufacturerRepository.delete(databaseManufacturer);
        }
    }

    /**
     * Returns a list of all DeviceModels in the Platform
     */
    @Transactional(value = "writableTransactionManager")
    public List<DeviceModel> findAllDeviceModels(final String organisationIdentification) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.GET_DEVICE_MODELS);

        return this.deviceModelRepository.findAll();
    }

    /**
     * Returns a {@link DeviceModel}, if it exists
     */
    @Transactional(value = "writableTransactionManager")
    public DeviceModel findDeviceModel(final String organisationIdentification, final String deviceModelCode)
            throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.GET_DEVICE_MODELS);

        /*
         * Model code does not uniquely identify a device model, which is why
         * deviceModelRepository is changed to return a list of device models.
         *
         * A better solution would be to return a list of device models or to
         * determine the manufacturer and do a lookup by manufacturer and model
         * code, which should uniquely define the device model.
         */
        final List<DeviceModel> deviceModels = this.deviceModelRepository.findByModelCode(deviceModelCode);

        if (deviceModels.isEmpty()) {
            return null;
        }
        if (deviceModels.size() > 1) {
            throw new AssertionError(
                    "Model code \"" + deviceModelCode + "\" does not uniquely identify a device model.");
        }

        return deviceModels.get(0);
    }

    /**
     * Returns a devicemodel in the Platform
     */
    @Transactional(value = "writableTransactionManager")
    public DeviceModel findDeviceModel(final String modelCode) {
        /*
         * Model code does not uniquely identify a device model, which is why
         * deviceModelRepository is changed to return a list of device models.
         *
         * A better solution would be to return a list of device models or to
         * determine the manufacturer and do a lookup by manufacturer and model
         * code, which should uniquely define the device model.
         */
        final List<DeviceModel> deviceModels = this.deviceModelRepository.findByModelCode(modelCode);

        if (deviceModels.isEmpty()) {
            return null;
        }
        if (deviceModels.size() > 1) {
            throw new AssertionError("Model code \"" + modelCode + "\" does not uniquely identify a device model.");
        }

        return deviceModels.get(0);
    }

    /**
     * Adds new deviceModel to the platform. Throws exception if
     * {@link DeviceModel} already exists
     */
    @Transactional(value = "writableTransactionManager")
    public void addDeviceModel(@Identification final String organisationIdentification, final String manufacturerCode,
            final String modelCode, final String description, final boolean metered) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.CREATE_DEVICE_MODEL);

        final Manufacturer manufacturer = this.manufacturerRepository.findByCode(manufacturerCode);

        if (manufacturer == null) {
            LOGGER.info("Manufacturer doesn't exist.");
            throw new FunctionalException(FunctionalExceptionType.EXISTING_MANUFACTURER, ComponentType.WS_CORE,
                    new UnknownEntityException(Manufacturer.class, manufacturerCode));
        }

        final DeviceModel savedDeviceModel = this.deviceModelRepository.findByManufacturerAndModelCode(manufacturer,
                modelCode);

        if (savedDeviceModel != null) {
            LOGGER.info("DeviceModel already exists.");
            throw new FunctionalException(FunctionalExceptionType.EXISTING_DEVICEMODEL, ComponentType.WS_CORE,
                    new ExistingEntityException(DeviceModel.class, manufacturerCode));
        } else {
            final DeviceModel deviceModel = new DeviceModel(manufacturer, modelCode, description,
                    this.firmwareFileStorage, metered);
            this.deviceModelRepository.save(deviceModel);
        }
    }

    /**
     * Removes a DeviceModel from the platform. Throws exception if
     * {@link DeviceModel} doesn't exist
     */
    @Transactional(value = "writableTransactionManager")
    public void removeDeviceModel(@Identification final String organisationIdentification,
            @Valid final String manufacturer, final String modelCode) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.REMOVE_DEVICE_MODEL);

        final Manufacturer databaseManufacturer = this.manufacturerRepository.findByCode(manufacturer);
        final DeviceModel removedDeviceModel = this.deviceModelRepository
                .findByManufacturerAndModelCode(databaseManufacturer, modelCode);

        if (removedDeviceModel == null) {
            LOGGER.info("DeviceModel not found.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICEMODEL, ComponentType.WS_CORE,
                    new ExistingEntityException(Manufacturer.class, modelCode));
        } else {
            final List<Device> devices = this.deviceRepository.findByDeviceModel(removedDeviceModel);
            if (!devices.isEmpty()) {
                LOGGER.info("DeviceModel is linked to a device.");
                throw new FunctionalException(FunctionalExceptionType.EXISTING_DEVICE_DEVICEMODEL,
                        ComponentType.WS_CORE,
                        new ExistingEntityException(Device.class, devices.get(0).getDeviceIdentification()));
            }
            final List<FirmwareFile> firmwareFiles = this.firmwareFileRepository.findByDeviceModel(removedDeviceModel);
            if (!firmwareFiles.isEmpty()) {
                LOGGER.info("DeviceModel is linked to a firmware file.");
                throw new FunctionalException(FunctionalExceptionType.EXISTING_DEVICEMODEL_FIRMWARE,
                        ComponentType.WS_CORE,
                        new ExistingEntityException(FirmwareFile.class, firmwareFiles.get(0).getFilename()));
            }
            this.deviceModelRepository.delete(removedDeviceModel);
        }
    }

    /**
     * Updates a DeviceModel to the platform. Throws exception if
     * {@link DeviceModel} doesn't exist.
     */
    @Transactional(value = "writableTransactionManager")
    public void changeDeviceModel(@Identification final String organisationIdentification, final String manufacturer,
            final String modelCode, final String description, final boolean metered) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.CHANGE_DEVICE_MODEL);

        final Manufacturer databaseManufacturer = this.manufacturerRepository.findByCode(manufacturer);
        final DeviceModel changedDeviceModel = this.deviceModelRepository
                .findByManufacturerAndModelCode(databaseManufacturer, modelCode);

        if (changedDeviceModel == null) {
            LOGGER.info("DeviceModel not found.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICEMODEL, ComponentType.WS_CORE,
                    new ExistingEntityException(Manufacturer.class, modelCode));
        } else {

            changedDeviceModel.updateData(description, metered);
            this.deviceModelRepository.save(changedDeviceModel);
        }
    }

    /**
     * Returns a list of all {@link FirmwareFile} in the Platform
     */
    public List<FirmwareFile> findAllFirmwareFiles(final String organisationIdentification, final String manufacturer,
            final String modelCode) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.GET_FIRMWARE);

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

        // performance issue, clean list with firmware files for front-end admin
        // app.
        for (final FirmwareFile firmwareFile : firmwareFiles) {
            firmwareFile.setFile(null);
        }

        return firmwareFiles;
    }

    /**
     * Returns the {@link FirmwareFile} of the given id, if it exists
     */
    public FirmwareFile findFirmwareFile(final String organisationIdentification, final int firmwareFileId)
            throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.GET_FIRMWARE);

        return this.firmwareFileRepository.findOne(Long.valueOf(firmwareFileId));
    }

    /**
     * Adds new {@link FirmwareFile} to the platform. Throws exception if
     * {@link FirmwareFile} already exists
     */
    @Transactional(value = "writableTransactionManager")
    public void addFirmware(@Identification final String organisationIdentification, final String description,
            final byte[] file, final String fileName, final String manufacturer, final String modelCode,
            final FirmwareModuleData firmwareModuleData, final boolean pushToNewDevices) throws OsgpException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.CREATE_FIRMWARE);

        final Manufacturer databaseManufacturer = this.manufacturerRepository.findByCode(manufacturer);

        if (databaseManufacturer == null) {
            LOGGER.info("Manufacturer doesn't exist.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_MANUFACTURER, ComponentType.WS_CORE,
                    new UnknownEntityException(Manufacturer.class, manufacturer));
        }

        final DeviceModel databaseDeviceModel = this.deviceModelRepository
                .findByManufacturerAndModelCode(databaseManufacturer, modelCode);

        if (databaseDeviceModel == null) {
            LOGGER.info("DeviceModel doesn't exist.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICEMODEL, ComponentType.WS_CORE,
                    new UnknownEntityException(DeviceModel.class, modelCode));
        }

        final Map<FirmwareModule, String> firmwareVersionsByModule = firmwareModuleData
                .getVersionsByModule(this.firmwareModuleRepository, false);

        FirmwareFile savedFirmwareFile = null;

        // file == null, user selected an existing firmware file
        if (file == null) {
            final List<FirmwareFile> databaseFirmwareFiles = this.firmwareFileRepository
                    .findByDeviceModelAndFilename(databaseDeviceModel, fileName);

            if (databaseFirmwareFiles.isEmpty()) {
                LOGGER.error("Firmware file doesn't exist.");
                throw new FunctionalException(FunctionalExceptionType.UNKNOWN_FIRMWARE, ComponentType.WS_CORE,
                        new UnknownEntityException(DeviceModel.class, fileName));
            }

            if (databaseDeviceModel.isFileStorage()) {
                // The file is already in the directory, so nothing else has to
                // happen
                savedFirmwareFile = new FirmwareFile(fileName, description, pushToNewDevices);
            } else {
                // Storing the file in the database
                savedFirmwareFile = new FirmwareFile(fileName, description, pushToNewDevices,
                        databaseFirmwareFiles.get(0).getFile(),
                        this.getMd5Hash(databaseFirmwareFiles.get(0).getFile()));
            }
        } else {
            if (databaseDeviceModel.isFileStorage()) {
                // Saving the file to the file system
                this.writeToFilesystem(file, fileName, databaseDeviceModel);
                savedFirmwareFile = new FirmwareFile(fileName, description, pushToNewDevices);
            } else {
                // Storing the file in the database
                savedFirmwareFile = new FirmwareFile(fileName, description, pushToNewDevices, file,
                        this.getMd5Hash(file));
            }
        }

        if (pushToNewDevices) {
            final List<FirmwareFile> firmwareFiles = this.firmwareFileRepository.findByDeviceModel(databaseDeviceModel);
            this.setPushToNewDevicesToFalse(firmwareFiles);
        }
        /*
         * Save the firmware file before adding the device model and updating
         * the firmware module data. Trying to save a new firmware file with the
         * related entities that were persisted earlier causes Hibernate
         * exceptions referring to persistent entities in detached state.
         */
        savedFirmwareFile = this.firmwareFileRepository.save(savedFirmwareFile);
        savedFirmwareFile.addDeviceModel(databaseDeviceModel);
        savedFirmwareFile.updateFirmwareModuleData(firmwareVersionsByModule);
        this.firmwareFileRepository.save(savedFirmwareFile);
    }

    /**
     * Saves a {@link DeviceFirmwareFile} instance.
     */
    @Transactional(value = "writableTransactionManager")
    public void saveCurrentDeviceFirmwareFile(final DeviceFirmwareFile deviceFirmwareFile) {
        this.deviceFirmwareFileRepository.save(deviceFirmwareFile);
    }

    /**
     * Updates a FirmwareFile to the platform. Throws exception if
     * {@link FirmwareFile} doesn't exist.
     */
    @Transactional(value = "writableTransactionManager")
    public void changeFirmware(@Identification final String organisationIdentification, final int id,
            final String description, final String filename, final String manufacturer, final String modelCode,
            final FirmwareModuleData firmwareModuleData, final boolean pushToNewDevices) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.CHANGE_FIRMWARE);

        final Manufacturer databaseManufacturer = this.manufacturerRepository.findByCode(manufacturer);

        if (databaseManufacturer == null) {
            LOGGER.info("Manufacturer {} doesn't exist.", manufacturer);
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_MANUFACTURER, ComponentType.WS_CORE,
                    new UnknownEntityException(Manufacturer.class, manufacturer));
        }

        final DeviceModel databaseDeviceModel = this.deviceModelRepository
                .findByManufacturerAndModelCode(databaseManufacturer, modelCode);

        if (databaseDeviceModel == null) {
            LOGGER.info("DeviceModel unknown for manufacturer {} and model code {}.", manufacturer, modelCode);
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICEMODEL, ComponentType.WS_CORE,
                    new UnknownEntityException(DeviceModel.class, modelCode));
        }

        FirmwareFile changedFirmwareFile = this.firmwareFileRepository.findOne(Long.valueOf(id));

        if (changedFirmwareFile == null) {
            LOGGER.info("FirmwareFile not found for id {}.", id);
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_FIRMWARE, ComponentType.WS_CORE,
                    new UnknownEntityException(FirmwareFile.class, filename));
        }

        changedFirmwareFile.setDescription(description);
        /*
         * A firmware file has been changed to be related to (possibly) multiple
         * device models to be usable across different value streams for all
         * kinds of devices.
         *
         * This code mimics the earlier behavior with a single device model
         * linked to the firmware file, where the device model is changed.
         *
         * If multiple device models are related, it is not clear what to do,
         * and which if the device models (if any) should be removed. In such
         * case the device model will be added for now.
         */
        final Set<DeviceModel> existingDeviceModels = changedFirmwareFile.getDeviceModels();
        if (existingDeviceModels.size() > 1) {
            LOGGER.warn("Change Firmware (FirmwareFile id={}) with {} existing DeviceModels ({}), adding {}",
                    changedFirmwareFile.getId(), existingDeviceModels.size(), databaseDeviceModel);
        } else {
            LOGGER.warn("Change Firmware (FirmwareFile id={}) with existing DeviceModel ({}), replacing by {}",
                    changedFirmwareFile.getId(), existingDeviceModels.size(), databaseDeviceModel);
            existingDeviceModels.clear();
        }
        changedFirmwareFile.addDeviceModel(databaseDeviceModel);
        changedFirmwareFile.setFilename(filename);
        changedFirmwareFile
                .updateFirmwareModuleData(firmwareModuleData.getVersionsByModule(this.firmwareModuleRepository, false));
        changedFirmwareFile.setPushToNewDevices(pushToNewDevices);

        // Save the changed firmware entity
        changedFirmwareFile = this.firmwareFileRepository.save(changedFirmwareFile);

        // Set all devicefirmwares.pushToNewDevices on false
        if (pushToNewDevices) {
            final List<FirmwareFile> firmwareFiles = this.firmwareFileRepository.findByDeviceModel(databaseDeviceModel);
            firmwareFiles.remove(changedFirmwareFile);
            this.setPushToNewDevicesToFalse(firmwareFiles);
        }

        this.firmwareFileRepository.save(changedFirmwareFile);
    }

    /**
     * Removes a {@link FirmwareFile} from the platform. Throws exception if
     * {@link FirmwareFile} doesn't exist
     */
    @Transactional(value = "writableTransactionManager")
    public void removeFirmware(@Identification final String organisationIdentification,
            @Valid final int firmwareIdentification) throws OsgpException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.REMOVE_FIRMWARE);

        final FirmwareFile removedFirmwareFile = this.firmwareFileRepository
                .findOne(Long.valueOf(firmwareIdentification));

        if (removedFirmwareFile == null) {
            LOGGER.info("FirmwareFile not found for id {}.", firmwareIdentification);
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_FIRMWARE, ComponentType.WS_CORE,
                    new UnknownEntityException(FirmwareFile.class, String.valueOf(firmwareIdentification)));
        }

        final List<DeviceFirmwareFile> deviceFirmwares = this.writableDeviceFirmwareRepository
                .findByFirmwareFile(removedFirmwareFile);
        if (!deviceFirmwares.isEmpty()) {
            LOGGER.info("FirmwareFile is linked to device.");
            throw new FunctionalException(FunctionalExceptionType.EXISTING_FIRMWARE_DEVICEFIRMWARE,
                    ComponentType.WS_CORE, new ExistingEntityException(DeviceFirmwareFile.class,
                            deviceFirmwares.get(0).getFirmwareFile().getDescription()));
        }

        /*
         * A firmware file has been changed to be related to (possibly) multiple
         * device models to be usable across different value streams for all
         * kinds of devices.
         *
         * If this code gets used in a scenario where multiple device models are
         * actually related to the firmware file it may need to be updated to
         * deal with this.
         */
        final Set<DeviceModel> deviceModels = removedFirmwareFile.getDeviceModels();
        if (deviceModels.size() != 1) {
            LOGGER.warn("Remove Firmware assumes a single DeviceModel, FirmwareFile (id={}) has {}: {}",
                    removedFirmwareFile.getId(), deviceModels.size(), deviceModels);
        }
        final DeviceModel deviceModel = deviceModels.iterator().next();

        // Only remove the file if no other firmware is using it.
        if (deviceModel.isFileStorage() && this.firmwareFileRepository
                .findByDeviceModelAndFilename(deviceModel, removedFirmwareFile.getFilename()).size() == 1) {
            this.removePhysicalFirmwareFile(this.createFirmwarePath(deviceModel, removedFirmwareFile.getFilename()));

        }

        this.firmwareFileRepository.delete(removedFirmwareFile);
    }

    /**
     * Returns a list of all {@link DeviceFirmwareFile}s in the Platform
     */
    public List<DeviceFirmwareFile> getDeviceFirmwareFiles(final String organisationIdentification,
            final String deviceIdentification) throws FunctionalException {
        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.GET_FIRMWARE);

        final Device device = this.writableDeviceRepository.findByDeviceIdentification(deviceIdentification);

        return this.writableDeviceFirmwareRepository.findByDeviceOrderByInstallationDateAsc(device);
    }

    public ResponseMessage dequeueGetFirmwareResponse(final String correlationUid) throws OsgpException {
        return this.commonResponseMessageFinder.findMessage(correlationUid);
    }

    public String enqueueSwitchFirmwareRequest(final String organisationIdentification,
            final String deviceIdentification, final String version, final int messagePriority)
            throws FunctionalException {
        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SWITCH_FIRMWARE);
        this.domainHelperService.isInMaintenance(device);

        LOGGER.debug("enqueueSwitchFirmwareRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, CommonRequestMessageType.SWITCH_FIRMWARE.name(),
                messagePriority);

        final CommonRequestMessage message = new CommonRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(version).build();

        this.commonRequestMessageSender.send(message);

        return correlationUid;
    }

    public ResponseMessage dequeueSwitchFirmwareResponse(final String correlationUid) throws OsgpException {
        return this.commonResponseMessageFinder.findMessage(correlationUid);
    }

    // HELPER METHODS

    private String getMd5Hash(final byte[] file) {
        String md5Hash;
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            final byte[] messageDigest = md.digest(file);
            final BigInteger number = new BigInteger(1, messageDigest);
            md5Hash = number.toString(16);
            while (md5Hash.length() < 32) {
                md5Hash = "0" + md5Hash;
            }
        } catch (final NoSuchAlgorithmException e) {
            LOGGER.error("RuntimeException while creating MD5 hash for firmware file.", e);
            throw new AssertionError("Expected MD5 to be present as algorithm", e);
        }
        return md5Hash;
    }

    private void writeToFilesystem(final byte[] file, final String fileName, final DeviceModel deviceModel)
            throws TechnicalException {

        final File path = this.createFirmwarePath(deviceModel, fileName);

        // Creating the dir, if needed
        this.createModelDirectory(path.getParentFile(), deviceModel.getModelCode());

        // Replacing spaces by SPACE_REPLACER
        fileName.replaceAll(" ", SPACE_REPLACER);

        try (final FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(file);
        } catch (final IOException e) {
            LOGGER.error("Could not write firmware to system", e);
            throw new TechnicalException(ComponentType.WS_CORE,
                    "Could not write firmware file to system".concat(e.getMessage()));
        }

        // Setting the file to readable to be downloadable
        path.setReadable(true, false);

    }

    private void removePhysicalFirmwareFile(final File file) throws TechnicalException {

        try {
            // Delete file
            Files.deleteIfExists(file.toPath());

            // Delete directorty if it was the last file
            if (file.getParentFile().list().length == 0) {
                Files.deleteIfExists(file.toPath().getParent());
            }

        } catch (final IOException e) {
            LOGGER.error("Could not remove firmware file from directory", e);
            throw new TechnicalException(ComponentType.WS_CORE,
                    "Could not remove firmware file from directory: ".concat(e.getMessage()));
        }
    }

    /*
     * Creates a directory for the given modelCode and manufacturer, if it
     * doesn't exist yet.
     */
    private void createModelDirectory(final File file, final String modelCode) throws TechnicalException {
        if (!file.isDirectory()) {
            LOGGER.info("Creating directory for devicemodel {}", modelCode);
            if (!file.mkdirs()) {
                throw new TechnicalException(ComponentType.WS_CORE,
                        "Could not create directory for devicemodel ".concat(modelCode));
            }
            // Setting the correct permissions so that the directory can be read
            // and displayed
            file.setReadable(true, false);
            file.setExecutable(true, false);
            file.getParentFile().setReadable(true, false);
            file.getParentFile().setExecutable(true, false);
        }
    }

    private File createFirmwarePath(final DeviceModel deviceModel, final String fileName) {
        return new File(this.firmwareDirectory.concat(File.separator)
                .concat(deviceModel.getManufacturer().getCode().replaceAll(" ", SPACE_REPLACER)).concat(File.separator)
                .concat(deviceModel.getModelCode().replaceAll(" ", SPACE_REPLACER)).concat(File.separator)
                .concat(fileName));
    }

    private void setPushToNewDevicesToFalse(final List<FirmwareFile> firmwareFiles) {
        for (final FirmwareFile firmwareFile : firmwareFiles) {
            if (firmwareFile.getPushToNewDevices()) {
                firmwareFile.setPushToNewDevices(false);
            }
        }
        this.firmwareFileRepository.save(firmwareFiles);
    }
}
