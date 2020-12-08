/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.da.application.services;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.OptimisticLockException;

import org.opensmartgridplatform.adapter.domain.da.application.mapping.DomainDistributionAutomationMapper;
import org.opensmartgridplatform.adapter.domain.da.application.routing.ResponseMessageRouter;
import org.opensmartgridplatform.adapter.domain.da.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.entities.RtuDevice;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.repositories.RtuDeviceRepository;
import org.opensmartgridplatform.domain.core.services.DeviceDomainService;
import org.opensmartgridplatform.domain.core.services.OrganisationDomainService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class BaseService {

    private static final ComponentType COMPONENT_TYPE = ComponentType.DOMAIN_DISTRIBUTION_AUTOMATION;

    @Autowired
    protected DeviceDomainService deviceDomainService;

    @Autowired
    protected OrganisationDomainService organisationDomainService;

    @Autowired
    protected RtuDeviceRepository rtuDeviceRepository;

    @Autowired
    @Qualifier(value = "domainDistributionAutomationOutboundOsgpCoreRequestsMessageSender")
    protected OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

    @Autowired
    protected DomainDistributionAutomationMapper domainCoreMapper;

    @Autowired
    protected ResponseMessageRouter responseMessageRouter;

    @Autowired
    private Integer lastCommunicationUpdateInterval;

    protected Device findActiveDevice(final String deviceIdentification) throws FunctionalException {
        return this.deviceDomainService.searchActiveDevice(deviceIdentification,
                ComponentType.DOMAIN_DISTRIBUTION_AUTOMATION);
    }

    protected Organisation findOrganisation(final String organisationIdentification) throws FunctionalException {
        final Organisation organisation;
        try {
            organisation = this.organisationDomainService.searchOrganisation(organisationIdentification);
        } catch (final UnknownEntityException e) {
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_ORGANISATION,
                    ComponentType.DOMAIN_DISTRIBUTION_AUTOMATION, e);
        }
        return organisation;
    }

    protected Optional<RtuDevice> findRtuDeviceForDevice(final Device device) {
        return this.rtuDeviceRepository.findById(device.getId());
    }

    protected OsgpException ensureOsgpException(final Throwable t, final String defaultMessage) {
        if (t instanceof OsgpException) {
            return (OsgpException) t;
        }

        return new TechnicalException(ComponentType.DOMAIN_DISTRIBUTION_AUTOMATION, defaultMessage, t);
    }

    protected void handleResponseMessageReceived(final Logger logger, final String deviceIdentification)
            throws FunctionalException {
        try {
            final RtuDevice device = this.rtuDeviceRepository.findByDeviceIdentification(deviceIdentification)
                    .orElseThrow(() -> new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE, COMPONENT_TYPE,
                            new UnknownEntityException(RtuDevice.class, deviceIdentification)));
            if (this.shouldUpdateCommunicationTime(device, this.lastCommunicationUpdateInterval)) {
                device.messageReceived();
                this.rtuDeviceRepository.save(device);
            } else {
                logger.info("Last communication time within {} seconds. Skipping last communication date update.",
                        this.lastCommunicationUpdateInterval);
            }
        } catch (final OptimisticLockException ex) {
            logger.warn("Last communication time not updated due to optimistic lock exception", ex);
        }
    }

    protected boolean shouldUpdateCommunicationTime(final RtuDevice device, final int lastCommunicationUpdateInterval) {
        final Instant timeToCheck = Instant.now().minusSeconds(lastCommunicationUpdateInterval);
        final Instant timeOfLastCommunication = device.getLastCommunicationTime();
        return timeOfLastCommunication.isBefore(timeToCheck);
    }

    protected static String getCorrelationId(final String organisationIdentification,
            final String deviceIdentification) {

        return organisationIdentification + "|||" + deviceIdentification + "|||" + UUID.randomUUID().toString();
    }

}
