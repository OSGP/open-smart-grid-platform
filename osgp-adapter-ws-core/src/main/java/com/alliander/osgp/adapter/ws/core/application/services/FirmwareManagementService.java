/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.services;

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
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableManufacturerRepository;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Manufacturer;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.ExistingEntityException;
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

        final Manufacturer dataseManufacturer = this.manufacturerRepository.findByCode(manufacturer.getCode());

        if (dataseManufacturer != null) {
            LOGGER.info("Manufacturer already exixts.");
            throw new FunctionalException(FunctionalExceptionType.EXISTING_MANUFACTURER, ComponentType.WS_CORE,
                    new ExistingEntityException(Manufacturer.class, manufacturer.getCode()));
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

        final Manufacturer databaseManufacturer = this.manufacturerRepository.findByCode(manufacturer.getCode());

        if (databaseManufacturer == null) {
            LOGGER.info("Manufacturer not found.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_MANUFACTURER, ComponentType.WS_CORE,
                    new ExistingEntityException(Manufacturer.class, manufacturer.getCode()));
        } else {
            databaseManufacturer.setCode(manufacturer.getCode());
            databaseManufacturer.setName(manufacturer.getName());

            this.manufacturerRepository.save(databaseManufacturer);
        }
    }

    /**
     * Removes a Manufacturer from the platform. Throws exception if
     * {@link Manufacturer} doesn't exists
     */
    @Transactional(value = "writableTransactionManager")
    public void removeManufacturer(@Identification final String organisationIdentification, @Valid final String code)
            throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.REMOVE_MANUFACTURER);

        final Manufacturer dataseManufacturer = this.manufacturerRepository.findByCode(code);

        if (dataseManufacturer == null) {
            LOGGER.info("Manufacturer not found.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_MANUFACTURER, ComponentType.WS_CORE,
                    new ExistingEntityException(Manufacturer.class, code));
        } else {
            this.manufacturerRepository.delete(dataseManufacturer);
        }
    }

    public ResponseMessage dequeueGetFirmwareResponse(final String correlationUid) throws OsgpException {
        return this.commonResponseMessageFinder.findMessage(correlationUid);
    }
}
