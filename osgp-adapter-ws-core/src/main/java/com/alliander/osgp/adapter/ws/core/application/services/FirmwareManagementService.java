/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.services;

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
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceModelRepository;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableManufacturerRepository;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceModel;
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
     * Adds new deviceModel to the platform. Throws exception if
     * {@link DeviceModel} already exists
     */
    @Transactional(value = "writableTransactionManager")
    public void addDeviceModel(@Identification final String organisationIdentification,
            final String manufacturerId, final String modelCode, final String description) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.CREATE_DEVICEMODEL);

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
        this.domainHelperService.isAllowed(organisation, PlatformFunction.REMOVE_DEVICE_MODELS);

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
        this.domainHelperService.isAllowed(organisation, PlatformFunction.CHANGE_MANUFACTURER);

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

}
