/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.services;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.joda.time.DateMidnight;
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
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceAuthorizationRepository;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceModelRepository;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceRepository;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableSsldRepository;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.exceptions.ExistingEntityException;
import com.alliander.osgp.domain.core.exceptions.NotAuthorizedException;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.ProtocolInfoRepository;
import com.alliander.osgp.domain.core.services.CorrelationIdProviderService;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunction;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

@SuppressWarnings("deprecation")
@Service(value = "wsCoreDeviceInstallationService")
@Validated
public class DeviceInstallationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceInstallationService.class);

    @Autowired
    private Integer recentDevicesPeriod;

    @Autowired
    private WritableDeviceAuthorizationRepository writableAuthorizationRepository;

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private WritableDeviceRepository writableDeviceRepository;

    @Autowired
    private WritableSsldRepository writableSsldRepository;

    @Autowired
    private WritableDeviceModelRepository deviceModelRepository;

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    private CommonRequestMessageSender commonRequestMessageSender;

    @Autowired
    private CommonResponseMessageFinder commonResponseMessageFinder;

    @Autowired
    private String defaultProtocol;

    @Autowired
    private String defaultProtocolVersion;

    @Autowired
    private ProtocolInfoRepository protocolRepository;

    DeviceInstallationService() {
        // Parameterless constructor required for transactions
    }

    @Transactional(value = "writableTransactionManager")
    public void addDevice(@Identification final String organisationIdentification, @Valid final Device newDevice)
            throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.GET_ORGANISATIONS);

        final Device existingDevice = this.writableDeviceRepository.findByDeviceIdentification(newDevice
                .getDeviceIdentification());

        if (existingDevice == null) {
            final Ssld ssld = new Ssld(newDevice.getDeviceIdentification(), newDevice.getAlias(),
                    newDevice.getContainerCity(), newDevice.getContainerPostalCode(), newDevice.getContainerStreet(),
                    newDevice.getContainerNumber(), newDevice.getContainerMunicipality(), newDevice.getGpsLatitude(),
                    newDevice.getGpsLongitude());
            ssld.setHasSchedule(false);
            ssld.setDeviceModel(newDevice.getDeviceModel());
            // device not created yet, add new device
            final DeviceAuthorization authorization = ssld.addAuthorization(organisation, DeviceFunctionGroup.OWNER);

            // Since the column device in device authorizations is cascaded,
            // this will also save the SSLD and device entities.
            this.writableAuthorizationRepository.save(authorization);

            LOGGER.info("Created new device {} with owner {}", newDevice.getDeviceIdentification(),
                    organisation.getOrganisationIdentification());
        } else {
            final List<DeviceAuthorization> owners = this.writableAuthorizationRepository.findByDeviceAndFunctionGroup(
                    existingDevice, DeviceFunctionGroup.OWNER);
            if (!owners.isEmpty()) {
                // device is already registered to a different owner
                throw new FunctionalException(FunctionalExceptionType.EXISTING_DEVICE, ComponentType.WS_CORE,
                        new ExistingEntityException(Device.class, newDevice.getDeviceIdentification()));
            }

            final Ssld ssld = this.writableSsldRepository.findByDeviceIdentification(existingDevice
                    .getDeviceIdentification());

            // device is orphan, register for current owner
            final DeviceAuthorization authorization = ssld.addAuthorization(organisation, DeviceFunctionGroup.OWNER);
            // add metadata to the device
            ssld.updateMetaData(null, newDevice.getContainerCity(), newDevice.getContainerPostalCode(),
                    newDevice.getContainerStreet(), newDevice.getContainerNumber(), null, newDevice.getGpsLatitude(),
                    newDevice.getGpsLongitude());

            // Since the column device in device authorizations is cascaded,
            // this will also save the SSLD and device entities.
            this.writableAuthorizationRepository.save(authorization);

            LOGGER.info("Registered orphan device {} to owner {}", newDevice.getDeviceIdentification(),
                    organisation.getOrganisationIdentification());
        }
    }

    @Transactional(value = "writableTransactionManager")
    public void updateDevice(@Identification final String organisationIdentification, @Valid final Device updateDevice)
            throws FunctionalException {

        final Ssld existingDevice = this.writableSsldRepository.findByDeviceIdentification(updateDevice
                .getDeviceIdentification());
        if (existingDevice == null) {
            // device does not exist
            LOGGER.info("Device does not exist, nothing to update.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE, ComponentType.WS_CORE,
                    new UnknownEntityException(Device.class, updateDevice.getDeviceIdentification()));
        }

        final List<DeviceAuthorization> owners = this.writableAuthorizationRepository.findByDeviceAndFunctionGroup(
                existingDevice, DeviceFunctionGroup.OWNER);

        // Check organisation against owner of device
        boolean isOwner = false;
        for (final DeviceAuthorization owner : owners) {
            if (owner.getOrganisation().getOrganisationIdentification().equalsIgnoreCase(organisationIdentification)) {
                isOwner = true;
            }
        }

        if (!isOwner) {
            LOGGER.info("Device has no owner yet, or organisation is not the owner.");
            throw new FunctionalException(FunctionalExceptionType.UNAUTHORIZED, ComponentType.WS_CORE,
                    new NotAuthorizedException(organisationIdentification));
        }

        // Update the device
        existingDevice.updateMetaData(updateDevice.getAlias(), updateDevice.getContainerCity(),
                updateDevice.getContainerPostalCode(), updateDevice.getContainerStreet(),
                updateDevice.getContainerNumber(), updateDevice.getContainerMunicipality(),
                updateDevice.getGpsLatitude(), updateDevice.getGpsLongitude());
        this.writableSsldRepository.save(existingDevice);
    }

    @Transactional(value = "transactionManager")
    public List<Device> findRecentDevices(@Identification final String organisationIdentification)
            throws FunctionalException {
        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);

        final Date fromDate = new DateMidnight().minusDays(this.recentDevicesPeriod).toDate();
        return this.deviceRepository.findRecentDevices(organisation, fromDate);
    }

    // === GET STATUS ===

    @Transactional(value = "transactionManager")
    public String enqueueGetStatusRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_STATUS);

        LOGGER.debug("enqueueGetStatusRequest called with organisation {} and device {}", organisationIdentification,
                deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final CommonRequestMessage message = new CommonRequestMessage(CommonRequestMessageType.GET_STATUS,
                correlationUid, organisationIdentification, deviceIdentification, null, null);

        this.commonRequestMessageSender.send(message);

        return correlationUid;
    }

    @Transactional(value = "transactionManager")
    public ResponseMessage dequeueGetStatusResponse(final String correlationUid) throws OsgpException {
        return this.commonResponseMessageFinder.findMessage(correlationUid);
    }

    // === START DEVICE TEST ===

    @Transactional(value = "transactionManager")
    public String enqueueStartDeviceTestRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification) throws FunctionalException {

        LOGGER.debug("Queue start device test request");

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.START_SELF_TEST);

        LOGGER.debug("enqueueStartDeviceTestRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);
        final CommonRequestMessage message = new CommonRequestMessage(CommonRequestMessageType.START_SELF_TEST,
                correlationUid, organisationIdentification, deviceIdentification, null, null);
        this.commonRequestMessageSender.send(message);

        return correlationUid;
    }

    @Transactional(value = "transactionManager")
    public ResponseMessage dequeueStartDeviceTestResponse(final String correlationUid) throws OsgpException {
        LOGGER.debug("Dequeue Start Device Test response");

        return this.commonResponseMessageFinder.findMessage(correlationUid);
    }

    // === STOP DEVICE TEST ===

    @Transactional(value = "transactionManager")
    public String enqueueStopDeviceTestRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification) throws FunctionalException {

        LOGGER.debug("Queue stop device test request");

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.STOP_SELF_TEST);

        LOGGER.debug("enqueueStopDeviceTestRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);
        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);
        final CommonRequestMessage message = new CommonRequestMessage(CommonRequestMessageType.STOP_SELF_TEST,
                correlationUid, organisationIdentification, deviceIdentification, null, null);
        this.commonRequestMessageSender.send(message);

        return correlationUid;
    }

    @Transactional(value = "transactionManager")
    public ResponseMessage dequeueStopDeviceTestResponse(final String correlationUid) throws OsgpException {
        LOGGER.debug("Dequeue Stop Device Test response");

        return this.commonResponseMessageFinder.findMessage(correlationUid);
    }

}