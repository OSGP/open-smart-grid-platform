/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.admin.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.exceptions.PlatformException;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.SsldRepository;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.validation.PublicKey;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "domainAdminDeviceManagementService")
@Transactional(value = "transactionManager")
public class DeviceManagementService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManagementService.class);

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SsldRepository ssldRepository;

    /**
     * Constructor
     */
    public DeviceManagementService() {
        // Parameterless constructor required for transactions...
    }

    // === UPDATE KEY ===

    public void updateKey(final String organisationIdentification, @Identification final String deviceIdentification,
            final String correlationUid, final String messageType, @PublicKey final String publicKey)
                    throws FunctionalException {

        LOGGER.info("MessageType: {}. Updating key for device [{}] on behalf of organisation [{}]",
                deviceIdentification, organisationIdentification, messageType);

        try {
            this.organisationDomainService.searchOrganisation(organisationIdentification);
        } catch (final UnknownEntityException e) {
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_ORGANISATION, ComponentType.DOMAIN_ADMIN, e);
        }

        this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, publicKey), messageType, null);
    }

    public void handleUpdateKeyResponse(final String deviceIdentification, final String organisationIdentification,
            final String correlationUid, final String messageType, final ResponseMessageResultType deviceResult,
            final OsgpException exception) {

        LOGGER.info("handleUpdateKeyResponse called for device: {} for organisation: {}", deviceIdentification,
                organisationIdentification);

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        OsgpException osgpException = exception;

        try {
            if (deviceResult == ResponseMessageResultType.NOT_OK || osgpException != null) {
                LOGGER.error("Device Response not ok.", osgpException);
                throw osgpException;
            }

            Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
            if (device == null) {
                // Device not found, create new device
                LOGGER.debug("Device [{}] does not exist, creating new device", deviceIdentification);
                device = new Device(deviceIdentification);
            }

            final Ssld ssld = this.ssldRepository.findByDeviceIdentification(deviceIdentification);

            ssld.setPublicKeyPresent(true);
            this.ssldRepository.save(ssld);

            LOGGER.info("publicKey has been set for device: {} for organisation: {}", deviceIdentification,
                    organisationIdentification);

        } catch (final Exception e) {
            LOGGER.error("Unexpected Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            osgpException = new TechnicalException(ComponentType.UNKNOWN,
                    "Unexpected exception while retrieving response message", e);
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, result, osgpException, null));
    }

    // === REVOKE KEY ===

    public void revokeKey(final String organisationIdentification, @Identification final String deviceIdentification,
            final String correlationUid, final String messageType) throws FunctionalException {

        LOGGER.info("MessageType: {}. Revoking key for device [{}] on behalf of organisation [{}]", messageType,
                deviceIdentification, organisationIdentification);

        try {
            this.organisationDomainService.searchOrganisation(organisationIdentification);
        } catch (final UnknownEntityException e) {
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_ORGANISATION, ComponentType.DOMAIN_ADMIN, e);
        }

        this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, null), messageType, null);

    }

    public void handleRevokeKeyResponse(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final String messageType, final ResponseMessageResultType deviceResult,
            final OsgpException exception) {

        LOGGER.info("handleRevokeKeyResponse called for device: {} for organisation: {}", deviceIdentification,
                organisationIdentification);

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        OsgpException osgpException = exception;

        try {
            if (deviceResult == ResponseMessageResultType.NOT_OK || osgpException != null) {
                LOGGER.error("Device Response not ok.", osgpException);
                throw osgpException;
            }

            final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
            if (device == null) {
                throw new PlatformException(String.format("Device not found: %s", deviceIdentification));
            }

            final Ssld ssld = this.ssldRepository.findByDeviceIdentification(deviceIdentification);

            ssld.setPublicKeyPresent(false);
            this.ssldRepository.save(ssld);

            LOGGER.info("publicKey has been revoked for device: {} for organisation: {}", deviceIdentification,
                    organisationIdentification);

        } catch (final Exception e) {
            LOGGER.error("Unexpected Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            osgpException = new TechnicalException(ComponentType.UNKNOWN,
                    "Unexpected exception while retrieving response message", e);
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, result, osgpException, null));
    }
}
