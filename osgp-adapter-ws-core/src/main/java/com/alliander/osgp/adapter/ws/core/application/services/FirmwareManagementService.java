/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.services;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.ws.core.infra.jms.CommonRequestMessage;
import com.alliander.osgp.adapter.ws.core.infra.jms.CommonRequestMessageSender;
import com.alliander.osgp.adapter.ws.core.infra.jms.CommonRequestMessageType;
import com.alliander.osgp.adapter.ws.core.infra.jms.CommonResponseMessageFinder;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceModelFirmwareRepository;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceModelRepository;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableManufacturerRepository;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.DeviceModelFirmware;
import com.alliander.osgp.domain.core.entities.Manufacturer;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.ExistingEntityException;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.services.CorrelationIdProviderService;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunction;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

@Service(value = "wsCoreFirmwareManagementService")
@Transactional(value = "transactionManager")
@Validated
public class FirmwareManagementService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManagementService.class);

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
    private WritableDeviceModelFirmwareRepository deviceModelFirmwareRepository;

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

        final Manufacturer dataseManufacturer = this.manufacturerRepository.findByManufacturerId(manufacturer.getManufacturerId());

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

        final Manufacturer databaseManufacturer = this.manufacturerRepository.findByManufacturerId(manufacturer.getManufacturerId());

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
    public void removeManufacturer(@Identification final String organisationIdentification, @Valid final String manufacturerId)
            throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.REMOVE_MANUFACTURER);

        final Manufacturer dataseManufacturer = this.manufacturerRepository.findByManufacturerId(manufacturerId);
        final List<DeviceModel> deviceModels = this.deviceModelRepository.findByManufacturerId(dataseManufacturer);

        if (deviceModels.size() > 0) {
            LOGGER.info("Manufacturer is linked to a Model.");
            throw new FunctionalException(FunctionalExceptionType.EXISTING_DEVICEMODEL_MANUFACTURER, ComponentType.WS_CORE,
                    new ExistingEntityException(DeviceModel.class, deviceModels.get(0).getModelCode()));
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

        List<DeviceModel> deviceModels = new ArrayList<DeviceModel>();
        deviceModels = this.deviceModelRepository.findAll();

        return deviceModels;
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
    public void addDeviceModel(@Identification final String organisationIdentification,
            final String manufacturerId, final String modelCode, final String description) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.CREATE_DEVICE_MODEL);

        final Manufacturer manufacturer = this.manufacturerRepository.findByManufacturerId(manufacturerId);

        if (manufacturer == null) {
            LOGGER.info("Manufacturer doesn't exixts.");
            throw new FunctionalException(FunctionalExceptionType.EXISTING_MANUFACTURER, ComponentType.WS_CORE,
                    new UnknownEntityException(Manufacturer.class, manufacturerId));
        }

        final DeviceModel savedDeviceModel = this.deviceModelRepository.findByManufacturerIdAndModelCode(manufacturer, modelCode);

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
    public void removeDeviceModel(@Identification final String organisationIdentification, @Valid final String manufacturer, final String modelCode)
            throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.REMOVE_DEVICE_MODEL);

        final Manufacturer dataseManufacturer = this.manufacturerRepository.findByManufacturerId(manufacturer);
        final DeviceModel removedDeviceModel = this.deviceModelRepository.findByManufacturerIdAndModelCode(dataseManufacturer, modelCode);

        if (removedDeviceModel == null) {
            LOGGER.info("DeviceModel not found.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICEMODEL, ComponentType.WS_CORE,
                    new ExistingEntityException(Manufacturer.class, modelCode));
        } else {
            this.deviceModelRepository.delete(removedDeviceModel);
        }
    }

    /**
     * Updates a DeviceModel to the platform. Throws exception if
     * {@link DeviceModel} doesn't exists.
     */
    @Transactional(value = "writableTransactionManager")
    public void changeDeviceModel(@Identification final String organisationIdentification,
            final String manufacturer, final String modelCode, final String description) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.CHANGE_DEVICE_MODEL);

        final Manufacturer dataseManufacturer = this.manufacturerRepository.findByManufacturerId(manufacturer);
        final DeviceModel changedDeviceModel = this.deviceModelRepository.findByManufacturerIdAndModelCode(dataseManufacturer, modelCode);

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
     * Returns a list of all DeviceModelFirmwares in the Platform
     */
    public List<DeviceModelFirmware> findAllDeviceModelFirmwares(final String organisationIdentification, final String manufacturer, final String modelCode) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.GET_DEVICE_MODEL_FIRMWARE);

        final Manufacturer databaseManufacturer = this.manufacturerRepository.findByManufacturerId(manufacturer);
        final DeviceModel databaseDeviceModel = this.deviceModelRepository.findByManufacturerIdAndModelCode(databaseManufacturer, modelCode);

        final List<DeviceModelFirmware> deviceModelFirmwares = this.deviceModelFirmwareRepository.findByDeviceModel(databaseDeviceModel);

        // performance issue, clean list with firmware files for front-end admin app.
        for (final DeviceModelFirmware deviceModelFirmware : deviceModelFirmwares) {
            deviceModelFirmware.setFile(null);
        }

        return deviceModelFirmwares;
    }

    /**
     * Adds new deviceModelFirmware to the platform. Throws exception if
     * {@link DeviceModelFirmware} already exists
     */
    @Transactional(value = "writableTransactionManager")
    public void addDeviceModelFirmware(@Identification final String organisationIdentification,
            final String description,
            final byte[] file,
            final String fileName,
            final String manufacturer,
            final String modelCode,
            final String moduleVersionComm,
            final String moduleVersionFunc,
            final String moduleVersionMa,
            final String moduleVersionMbus,
            final String moduleVersionSec,
            final boolean pushToNewDevices) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.CREATE_DEVICE_MODEL_FIRMWARE);

        final Manufacturer dataseManufacturer = this.manufacturerRepository.findByManufacturerId(manufacturer);

        if (dataseManufacturer == null) {
            LOGGER.info("Manufacturer doesn't exixts.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_MANUFACTURER, ComponentType.WS_CORE,
                    new UnknownEntityException(Manufacturer.class, manufacturer));
        }

        final DeviceModel databaseDeviceModel = this.deviceModelRepository.findByManufacturerIdAndModelCode(dataseManufacturer, modelCode);

        if (databaseDeviceModel == null) {
            LOGGER.info("DeviceModel already exixts.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICEMODEL, ComponentType.WS_CORE,
                    new UnknownEntityException(DeviceModel.class, modelCode));
        }

        DeviceModelFirmware savedDeviceModelFirmware = null;

        // file == null, user selected an existing firmware file
        if (file == null) {
            final List<DeviceModelFirmware> databaseDeviceModelFirmwares = this.deviceModelFirmwareRepository.findByDeviceModelAndFilename(databaseDeviceModel, fileName);

            if (databaseDeviceModelFirmwares.size() > 0) {
                savedDeviceModelFirmware = new DeviceModelFirmware(databaseDeviceModel, fileName, modelCode,
                        description, pushToNewDevices, moduleVersionComm, moduleVersionFunc, moduleVersionMa, moduleVersionMbus,
                        moduleVersionSec, databaseDeviceModelFirmwares.get(0).getFile(), this.getMd5Hash(databaseDeviceModelFirmwares.get(0).getFile()));
            } else {
                LOGGER.error("DeviceModelFirmware file doesn't exixts.");
                throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICEMODEL_FIRMWARE, ComponentType.WS_CORE,
                        new UnknownEntityException(DeviceModel.class, fileName));
            }
        } else {
            savedDeviceModelFirmware = new DeviceModelFirmware(databaseDeviceModel, fileName, modelCode,
                    description, pushToNewDevices, moduleVersionComm, moduleVersionFunc, moduleVersionMa, moduleVersionMbus,
                    moduleVersionSec, file, this.getMd5Hash(file));
        }

        if (pushToNewDevices) {
            final List<DeviceModelFirmware> deviceModelFirmwares = this.deviceModelFirmwareRepository.findByDeviceModel(databaseDeviceModel);
            for (final DeviceModelFirmware dbDeviceModelFirmware : deviceModelFirmwares) {
                if (dbDeviceModelFirmware.getPushToNewDevices()) {
                    dbDeviceModelFirmware.setPushToNewDevices(false);
                }
            }
            this.deviceModelFirmwareRepository.save(deviceModelFirmwares);
        }
        this.deviceModelFirmwareRepository.save(savedDeviceModelFirmware);
    }

    /**
     * Updates a DeviceModelFirmware to the platform. Throws exception if
     * {@link DeviceModelFirmware} doesn't exists.
     */
    @Transactional(value = "writableTransactionManager")
    public void changeDeviceModelFirmware(@Identification final String organisationIdentification,
            final int id,
            final String description,
            final String filename,
            final String manufacturer,
            final String modelCode,
            final String moduleVersionComm,
            final String moduleVersionFunc,
            final String moduleVersionMa,
            final String moduleVersionMbus,
            final String moduleVersionSec,
            final boolean pushToNewDevices) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.CHANGE_DEVICE_MODEL_FIRMWARE);

        final Manufacturer databaseManufacturer = this.manufacturerRepository.findByManufacturerId(manufacturer);

        if (databaseManufacturer == null) {
            LOGGER.info("Manufacturer doesn't exixts.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_MANUFACTURER, ComponentType.WS_CORE,
                    new UnknownEntityException(Manufacturer.class, manufacturer));
        }

        final DeviceModel databaseDeviceModel = this.deviceModelRepository.findByManufacturerIdAndModelCode(databaseManufacturer, modelCode);

        if (databaseDeviceModel == null) {
            LOGGER.info("DeviceModel already exixts.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICEMODEL, ComponentType.WS_CORE,
                    new UnknownEntityException(DeviceModel.class, modelCode));
        }

        final DeviceModelFirmware changedDeviceModelFirmware = this.deviceModelFirmwareRepository.findById(Long.valueOf(id));

        if (changedDeviceModelFirmware == null) {
            LOGGER.info("DeviceModelFirmware not found.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICEMODEL_FIRMWARE, ComponentType.WS_CORE,
                    new UnknownEntityException(DeviceModelFirmware.class, filename));
        }

        changedDeviceModelFirmware.setDescription(description);
        changedDeviceModelFirmware.setDeviceModel(databaseDeviceModel);
        changedDeviceModelFirmware.setFilename(filename);
        changedDeviceModelFirmware.setModelCode(modelCode);
        changedDeviceModelFirmware.setModuleVersionComm(moduleVersionComm);
        changedDeviceModelFirmware.setModuleVersionFunc(moduleVersionFunc);
        changedDeviceModelFirmware.setModuleVersionMa(moduleVersionMa);
        changedDeviceModelFirmware.setModuleVersionMbus(moduleVersionMbus);
        changedDeviceModelFirmware.setModuleVersionSec(moduleVersionSec);
        changedDeviceModelFirmware.setPushToNewDevices(pushToNewDevices);

        // set all devicefirmwares.pushToNewDevices on false
        if (pushToNewDevices) {
            final List<DeviceModelFirmware> deviceModelFirmwares = this.deviceModelFirmwareRepository.findByDeviceModel(databaseDeviceModel);
            for (final DeviceModelFirmware deviceModelFirmware : deviceModelFirmwares) {
                if (deviceModelFirmware.getPushToNewDevices() && (deviceModelFirmware.getId() != Long.valueOf(id))) {
                    deviceModelFirmware.setPushToNewDevices(false);
                }
            }
            this.deviceModelFirmwareRepository.save(deviceModelFirmwares);
        }

        this.deviceModelFirmwareRepository.save(changedDeviceModelFirmware);
    }

    /**
     * Removes a DeviceModelFirmware from the platform. Throws exception if
     * {@link DeviceModelFirmware} doesn't exists
     */
    @Transactional(value = "writableTransactionManager")
    public void removeDeviceModelFirmware(@Identification final String organisationIdentification, @Valid final int firmwareIdentification)
            throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.REMOVE_DEVICE_MODEL_FIRMWARE);

        final DeviceModelFirmware removedDeviceModelFirmware = this.deviceModelFirmwareRepository.findById(Long.valueOf(firmwareIdentification));

        if (removedDeviceModelFirmware == null) {
            LOGGER.info("DeviceModelFirmware not found.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICEMODEL_FIRMWARE, ComponentType.WS_CORE,
                    new UnknownEntityException(DeviceModelFirmware.class, String.valueOf(firmwareIdentification)));
        }

        this.deviceModelFirmwareRepository.delete(removedDeviceModelFirmware);
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
            LOGGER.error("RuntimeException while creating MD5 hash for device model firmware.", e);
            throw new RuntimeException(e);
        }
        return md5Hash;
    }

}
