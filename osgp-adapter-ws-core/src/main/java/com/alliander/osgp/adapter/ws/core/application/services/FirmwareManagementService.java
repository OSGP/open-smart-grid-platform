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

import javax.annotation.Resource;
import javax.validation.Valid;

import org.hibernate.validator.constraints.NotBlank;
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
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceFirmwareRepository;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceModelRepository;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceRepository;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableFirmwareRepository;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableManufacturerRepository;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceFirmware;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Firmware;
import com.alliander.osgp.domain.core.entities.Manufacturer;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.ExistingEntityException;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.services.CorrelationIdProviderService;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.FirmwareModuleData;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunction;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
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
    private WritableFirmwareRepository firmwareRepository;

    @Autowired
    private WritableDeviceRepository deviceRepository;

    @Autowired
    private WritableDeviceFirmwareRepository deviceFirmwareRepository;

    @Resource
    @Qualifier("wsCoreFirmwareManagementFirmwareDirectory")
    private String firmwareDirectory;

    @Autowired
    private WritableDeviceFirmwareRepository writableDeviceFirmwareRepository;

    @Autowired
    private WritableDeviceRepository writableDeviceRepository;

    public String enqueueUpdateFirmwareRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, @NotBlank final String firmwareIdentification,
            final DateTime scheduledTime) throws FunctionalException {
        LOGGER.debug("Queue update firmware request");

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.UPDATE_FIRMWARE);
        this.domainHelperService.isInMaintenance(device);

        LOGGER.debug("enqueueUpdateFirmwareRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);
        final CommonRequestMessage message = new CommonRequestMessage(CommonRequestMessageType.UPDATE_FIRMWARE,
                correlationUid, organisationIdentification, deviceIdentification, firmwareIdentification, scheduledTime);
        this.commonRequestMessageSender.send(message);

        return correlationUid;
    }

    public ResponseMessage dequeueUpdateFirmwareResponse(final String correlationUid) throws OsgpException {
        return this.commonResponseMessageFinder.findMessage(correlationUid);
    }

    public String enqueueGetFirmwareRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification) throws FunctionalException {
        LOGGER.debug("Queue get firmware request");

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_FIRMWARE_VERSION);
        this.domainHelperService.isInMaintenance(device);

        LOGGER.debug("enqueueGetFirmwareRequest called with organisation {} and device {}", organisationIdentification,
                deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);
        final CommonRequestMessage message = new CommonRequestMessage(CommonRequestMessageType.GET_FIRMWARE_VERSION,
                correlationUid, organisationIdentification, deviceIdentification, null, null);
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

        final Manufacturer dataseManufacturer = this.manufacturerRepository.findByManufacturerId(manufacturer
                .getManufacturerId());

        if (dataseManufacturer != null) {
            LOGGER.info("Manufacturer already exixts.");
            throw new FunctionalException(FunctionalExceptionType.EXISTING_MANUFACTURER, ComponentType.WS_CORE,
                    new ExistingEntityException(Manufacturer.class, manufacturer.getManufacturerId()));
        } else {
            this.manufacturerRepository.save(manufacturer);
        }
    }

    /**
     * Updates a Manufacturer to the platform. Throws exception if
     * {@link Manufacturer} doesn't exists.
     */
    @Transactional(value = "writableTransactionManager")
    public void changeManufacturer(@Identification final String organisationIdentification,
            @Valid final Manufacturer manufacturer) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.CHANGE_MANUFACTURER);

        final Manufacturer databaseManufacturer = this.manufacturerRepository.findByManufacturerId(manufacturer
                .getManufacturerId());

        if (databaseManufacturer == null) {
            LOGGER.info("Manufacturer not found.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_MANUFACTURER, ComponentType.WS_CORE,
                    new ExistingEntityException(Manufacturer.class, manufacturer.getManufacturerId()));
        } else {
            databaseManufacturer.setManufacturerId(manufacturer.getManufacturerId());
            databaseManufacturer.setName(manufacturer.getName());
            databaseManufacturer.setUsePrefix(manufacturer.isUsePrefix());

            this.manufacturerRepository.save(databaseManufacturer);
        }
    }

    /**
     * Removes a Manufacturer from the platform. Throws exception if
     * {@link Manufacturer} doesn't exists
     */
    @Transactional(value = "writableTransactionManager")
    public void removeManufacturer(@Identification final String organisationIdentification,
            @Valid final String manufacturerId) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.REMOVE_MANUFACTURER);

        final Manufacturer dataseManufacturer = this.manufacturerRepository.findByManufacturerId(manufacturerId);
        final List<DeviceModel> deviceModels = this.deviceModelRepository.findByManufacturerId(dataseManufacturer);

        if (!deviceModels.isEmpty()) {
            LOGGER.info("Manufacturer is linked to a Model.");
            throw new FunctionalException(FunctionalExceptionType.EXISTING_DEVICEMODEL_MANUFACTURER,
                    ComponentType.WS_CORE, new ExistingEntityException(DeviceModel.class, deviceModels.get(0)
                            .getModelCode()));
        }

        if (dataseManufacturer == null) {
            LOGGER.info("Manufacturer not found.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_MANUFACTURER, ComponentType.WS_CORE,
                    new ExistingEntityException(Manufacturer.class, manufacturerId));
        } else {
            this.manufacturerRepository.delete(dataseManufacturer);
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
     * Returns a {@link DeviceModel}, it it exists
     */
    @Transactional(value = "writableTransactionManager")
    public DeviceModel findDeviceModel(final String organisationIdentification, final String deviceModelCode)
            throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.GET_DEVICE_MODELS);

        return this.deviceModelRepository.findByModelCode(deviceModelCode);
    }

    /**
     * Returns a devicemodel in the Platform
     */
    @Transactional(value = "writableTransactionManager")
    public DeviceModel findDeviceModel(final String deviceModelName) throws FunctionalException {
        return this.deviceModelRepository.findByModelCode(deviceModelName);
    }

    /**
     * Adds new deviceModel to the platform. Throws exception if
     * {@link DeviceModel} already exists
     */
    @Transactional(value = "writableTransactionManager")
    public void addDeviceModel(@Identification final String organisationIdentification, final String manufacturerId,
            final String modelCode, final String description) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.CREATE_DEVICE_MODEL);

        final Manufacturer manufacturer = this.manufacturerRepository.findByManufacturerId(manufacturerId);

        if (manufacturer == null) {
            LOGGER.info("Manufacturer doesn't exixts.");
            throw new FunctionalException(FunctionalExceptionType.EXISTING_MANUFACTURER, ComponentType.WS_CORE,
                    new UnknownEntityException(Manufacturer.class, manufacturerId));
        }

        final DeviceModel savedDeviceModel = this.deviceModelRepository.findByManufacturerIdAndModelCode(manufacturer,
                modelCode);

        if (savedDeviceModel != null) {
            LOGGER.info("DeviceModel already exixts.");
            throw new FunctionalException(FunctionalExceptionType.EXISTING_DEVICEMODEL, ComponentType.WS_CORE,
                    new ExistingEntityException(DeviceModel.class, manufacturerId));
        } else {
            final DeviceModel deviceModel = new DeviceModel(manufacturer, modelCode, description);
            this.deviceModelRepository.save(deviceModel);
        }
    }

    /**
     * Removes a DeviceModel from the platform. Throws exception if
     * {@link DeviceModel} doesn't exists
     */
    @Transactional(value = "writableTransactionManager")
    public void removeDeviceModel(@Identification final String organisationIdentification,
            @Valid final String manufacturer, final String modelCode) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.REMOVE_DEVICE_MODEL);

        final Manufacturer dataseManufacturer = this.manufacturerRepository.findByManufacturerId(manufacturer);
        final DeviceModel removedDeviceModel = this.deviceModelRepository.findByManufacturerIdAndModelCode(
                dataseManufacturer, modelCode);

        if (removedDeviceModel == null) {
            LOGGER.info("DeviceModel not found.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICEMODEL, ComponentType.WS_CORE,
                    new ExistingEntityException(Manufacturer.class, modelCode));
        } else {
            final List<Device> devices = this.deviceRepository.findByDeviceModel(removedDeviceModel);
            if (!devices.isEmpty()) {
                LOGGER.info("DeviceModel is linked to a device.");
                throw new FunctionalException(FunctionalExceptionType.EXISTING_DEVICE_DEVICEMODEL,
                        ComponentType.WS_CORE, new ExistingEntityException(Device.class, devices.get(0)
                                .getDeviceIdentification()));
            }
            final List<Firmware> firmwares = this.firmwareRepository.findByDeviceModel(removedDeviceModel);
            if (!firmwares.isEmpty()) {
                LOGGER.info("DeviceModel is linked to a firmware.");
                throw new FunctionalException(FunctionalExceptionType.EXISTING_DEVICEMODEL_FIRMWARE,
                        ComponentType.WS_CORE, new ExistingEntityException(Firmware.class, firmwares.get(0)
                                .getFilename()));
            }
            this.deviceModelRepository.delete(removedDeviceModel);
        }
    }

    /**
     * Updates a DeviceModel to the platform. Throws exception if
     * {@link DeviceModel} doesn't exists.
     */
    @Transactional(value = "writableTransactionManager")
    public void changeDeviceModel(@Identification final String organisationIdentification, final String manufacturer,
            final String modelCode, final String description) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.CHANGE_DEVICE_MODEL);

        final Manufacturer dataseManufacturer = this.manufacturerRepository.findByManufacturerId(manufacturer);
        final DeviceModel changedDeviceModel = this.deviceModelRepository.findByManufacturerIdAndModelCode(
                dataseManufacturer, modelCode);

        if (changedDeviceModel == null) {
            LOGGER.info("DeviceModel not found.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICEMODEL, ComponentType.WS_CORE,
                    new ExistingEntityException(Manufacturer.class, modelCode));
        } else {

            changedDeviceModel.setDescription(description);
            this.deviceModelRepository.save(changedDeviceModel);
        }
    }

    /**
     * Returns a list of all {@link Firmware} in the Platform
     */
    public List<Firmware> findAllFirmwares(final String organisationIdentification, final String manufacturer,
            final String modelCode) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.GET_FIRMWARE);

        List<Firmware> firmwares = new ArrayList<Firmware>();
        if (manufacturer != null) {
            final Manufacturer databaseManufacturer = this.manufacturerRepository.findByManufacturerId(manufacturer);
            final DeviceModel databaseDeviceModel = this.deviceModelRepository.findByManufacturerIdAndModelCode(
                    databaseManufacturer, modelCode);
            firmwares = this.firmwareRepository.findByDeviceModel(databaseDeviceModel);
        } else {
            final DeviceModel databaseDeviceModel = this.deviceModelRepository.findByModelCode(modelCode);
            firmwares = this.firmwareRepository.findByDeviceModel(databaseDeviceModel);
        }

        // performance issue, clean list with firmware files for front-end admin
        // app.
        for (final Firmware firmware : firmwares) {
            firmware.setFile(null);
        }

        return firmwares;
    }

    /**
     * Returns the {@link Firmware} of the given id, if it exists
     */
    public Firmware findFirmware(final String organisationIdentification, final int firmwareId)
            throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.GET_FIRMWARE);

        return this.firmwareRepository.findOne(Long.valueOf(firmwareId));
    }

    /**
     * Adds new {@link Firmware} to the platform. Throws exception if
     * {@link Firmware} already exists
     */
    @Transactional(value = "writableTransactionManager")
    public void addFirmware(@Identification final String organisationIdentification, final String description,
            final byte[] file, final String fileName, final String manufacturer, final String modelCode,
            final FirmwareModuleData firmwareModuleData, final boolean pushToNewDevices) throws Exception {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.CREATE_FIRMWARE);

        final Manufacturer dataseManufacturer = this.manufacturerRepository.findByManufacturerId(manufacturer);

        if (dataseManufacturer == null) {
            LOGGER.info("Manufacturer doesn't exixts.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_MANUFACTURER, ComponentType.WS_CORE,
                    new UnknownEntityException(Manufacturer.class, manufacturer));
        }

        final DeviceModel databaseDeviceModel = this.deviceModelRepository.findByManufacturerIdAndModelCode(
                dataseManufacturer, modelCode);

        if (databaseDeviceModel == null) {
            LOGGER.info("DeviceModel already exixts.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICEMODEL, ComponentType.WS_CORE,
                    new UnknownEntityException(DeviceModel.class, modelCode));
        }

        Firmware savedFirmware = null;

        // file == null, user selected an existing firmware file
        if (file == null) {
            final List<Firmware> databaseFirmwares = this.firmwareRepository.findByDeviceModelAndFilename(
                    databaseDeviceModel, fileName);

            if (databaseFirmwares.isEmpty()) {
                LOGGER.error("Firmware file doesn't exixts.");
                throw new FunctionalException(FunctionalExceptionType.UNKNOWN_FIRMWARE, ComponentType.WS_CORE,
                        new UnknownEntityException(DeviceModel.class, fileName));
            }

            if (databaseDeviceModel.isFileStorage()) {
                // The file is already in the directory, so nothing else has to
                // happen
                savedFirmware = new Firmware(databaseDeviceModel, fileName, description, pushToNewDevices,
                        firmwareModuleData);
            } else {
                // Storing the file in the database
                savedFirmware = new Firmware(databaseDeviceModel, fileName, description, pushToNewDevices,
                        firmwareModuleData, databaseFirmwares.get(0).getFile(), this.getMd5Hash(databaseFirmwares
                                .get(0).getFile()));
            }
        } else {
            if (databaseDeviceModel.isFileStorage()) {
                // Saving the file to the file system
                this.writeToFilesystem(file, fileName, databaseDeviceModel);
                savedFirmware = new Firmware(databaseDeviceModel, fileName, description, pushToNewDevices,
                        firmwareModuleData);
            } else {
                // Storing the file in the database
                savedFirmware = new Firmware(databaseDeviceModel, fileName, description, pushToNewDevices,
                        firmwareModuleData, file, this.getMd5Hash(file));
            }
        }

        if (pushToNewDevices) {
            final List<Firmware> firmwares = this.firmwareRepository.findByDeviceModel(databaseDeviceModel);
            for (final Firmware dbFirmware : firmwares) {
                if (dbFirmware.getPushToNewDevices()) {
                    dbFirmware.setPushToNewDevices(false);
                }
            }
            this.firmwareRepository.save(firmwares);
        }
        this.firmwareRepository.save(savedFirmware);
    }

    /**
     * Links a {@link DeviceFirmware} instance to a {@link Device}, and sets it
     * as the active one.
     */
    @Transactional(value = "writableTransactionManager")
    public void saveCurrentDeviceFirmware(final DeviceFirmware deviceFirmware) throws Exception {

        // Setting other devicefirmwares for this device on inactive
        this.deviceFirmwareRepository.updateDeviceFirmwareSetActiveFalseWhereDevice(deviceFirmware.getDevice());

        // Setting active to true, just to be sure and saving it.
        deviceFirmware.setActive(true);
        this.deviceFirmwareRepository.save(deviceFirmware);
    }

    /**
     * Updates a Firmware to the platform. Throws exception if {@link Firmware}
     * doesn't exists.
     */
    @Transactional(value = "writableTransactionManager")
    public void changeFirmware(@Identification final String organisationIdentification, final int id,
            final String description, final String filename, final String manufacturer, final String modelCode,
            final FirmwareModuleData firmwareModuleData, final boolean pushToNewDevices) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.CHANGE_FIRMWARE);

        final Manufacturer databaseManufacturer = this.manufacturerRepository.findByManufacturerId(manufacturer);

        if (databaseManufacturer == null) {
            LOGGER.info("Manufacturer doesn't exixts.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_MANUFACTURER, ComponentType.WS_CORE,
                    new UnknownEntityException(Manufacturer.class, manufacturer));
        }

        final DeviceModel databaseDeviceModel = this.deviceModelRepository.findByManufacturerIdAndModelCode(
                databaseManufacturer, modelCode);

        if (databaseDeviceModel == null) {
            LOGGER.info("DeviceModel already exixts.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICEMODEL, ComponentType.WS_CORE,
                    new UnknownEntityException(DeviceModel.class, modelCode));
        }

        final Firmware changedFirmware = this.firmwareRepository.findOne(Long.valueOf(id));

        if (changedFirmware == null) {
            LOGGER.info("Firmware not found.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_FIRMWARE, ComponentType.WS_CORE,
                    new UnknownEntityException(Firmware.class, filename));
        }

        changedFirmware.setDescription(description);
        changedFirmware.setDeviceModel(databaseDeviceModel);
        changedFirmware.setFilename(filename);
        changedFirmware.updateFirmwareModuleData(firmwareModuleData);
        changedFirmware.setPushToNewDevices(pushToNewDevices);

        // set all devicefirmwares.pushToNewDevices on false
        if (pushToNewDevices) {
            final List<Firmware> firmwares = this.firmwareRepository.findByDeviceModel(databaseDeviceModel);
            for (final Firmware firmware : firmwares) {
                if (firmware.getPushToNewDevices() && (firmware.getId() != Long.valueOf(id))) {
                    firmware.setPushToNewDevices(false);
                }
            }
            this.firmwareRepository.save(firmwares);
        }

        this.firmwareRepository.save(changedFirmware);
    }

    /**
     * Removes a {@link Firmware} from the platform. Throws exception if
     * {@link Firmware} doesn't exists
     */
    @Transactional(value = "writableTransactionManager")
    public void removeFirmware(@Identification final String organisationIdentification,
            @Valid final int firmwareIdentification) throws Exception {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.REMOVE_FIRMWARE);

        final Firmware removedFirmware = this.firmwareRepository.findOne(Long.valueOf(firmwareIdentification));

        if (removedFirmware == null) {
            LOGGER.info("Firmware not found.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_FIRMWARE, ComponentType.WS_CORE,
                    new UnknownEntityException(Firmware.class, String.valueOf(firmwareIdentification)));
        }

        final List<DeviceFirmware> deviceFirmwares = this.writableDeviceFirmwareRepository
                .findByFirmware(removedFirmware);
        if (!deviceFirmwares.isEmpty()) {
            LOGGER.info("Firmware is linked to firmware.");
            throw new FunctionalException(FunctionalExceptionType.EXISTING_FIRMWARE_DEVICEFIRMWARE,
                    ComponentType.WS_CORE, new ExistingEntityException(DeviceFirmware.class, deviceFirmwares.get(0)
                            .getFirmware().getDescription()));
        }

        // Only remove the file if no other firmware is using it.
        if (removedFirmware.getDeviceModel().isFileStorage()
                && this.firmwareRepository.findByDeviceModelAndFilename(removedFirmware.getDeviceModel(),
                        removedFirmware.getFilename()).size() == 1) {
            this.removeFirmwareFile(this.createFirmwarePath(removedFirmware.getDeviceModel(),
                    removedFirmware.getFilename()));

        }

        this.firmwareRepository.delete(removedFirmware);
    }

    /**
     * Returns a list of all {@link DeviceFirmware}s in the Platform
     */
    public List<DeviceFirmware> getDeviceFirmwares(final String organisationIdentification,
            final String deviceIdentification) throws FunctionalException {
        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.GET_FIRMWARE);

        final Device device = this.writableDeviceRepository.findByDeviceIdentification(deviceIdentification);

        return this.writableDeviceFirmwareRepository.findByDevice(device);
    }

    public ResponseMessage dequeueGetFirmwareResponse(final String correlationUid) throws OsgpException {
        return this.commonResponseMessageFinder.findMessage(correlationUid);
    }

    public String enqueueSwitchFirmwareRequest(final String organisationIdentification,
            final String deviceIdentification, final String version) throws FunctionalException {
        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SWITCH_FIRMWARE);
        this.domainHelperService.isInMaintenance(device);

        LOGGER.debug("enqueueSwitchFirmwareRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final CommonRequestMessage message = new CommonRequestMessage(CommonRequestMessageType.SWITCH_FIRMWARE,
                correlationUid, organisationIdentification, deviceIdentification, version, null);

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
            throw new RuntimeException(e);
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
            throw new TechnicalException(ComponentType.WS_CORE, "Could not write firmware file to system".concat(e
                    .getMessage()));
        }

        // Setting the file to readable to be downloadable
        path.setReadable(true, false);

    }

    private void removeFirmwareFile(final File file) throws TechnicalException {

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
                .concat(deviceModel.getManufacturerId().getManufacturerId().replaceAll(" ", SPACE_REPLACER))
                .concat(File.separator).concat(deviceModel.getModelCode().replaceAll(" ", SPACE_REPLACER))
                .concat(File.separator).concat(fileName));
    }

}
