/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.da.application.services;

import java.time.Duration;
import java.time.Instant;

import javax.persistence.OptimisticLockException;

import org.opensmartgridplatform.domain.core.entities.RtuDevice;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.repositories.RtuDeviceRepository;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RtuResponseService {

    private static final ComponentType COMPONENT_TYPE = ComponentType.DOMAIN_DISTRIBUTION_AUTOMATION;

    @Autowired
    protected RtuDeviceRepository rtuDeviceRepository;

    @Value("#{T(java.time.Duration).parse('${communication.monitoring.minimum.duration.between.communication.time.updates:PT1M}')}")
    private Duration minimumDurationBetweenCommunicationTimeUpdates;

    @Transactional(value = "transactionManager")
    public void handleResponseMessageReceived(final Logger logger, final String deviceIdentification)
            throws FunctionalException {
        try {
            final RtuDevice device = this.rtuDeviceRepository.findByDeviceIdentification(deviceIdentification)
                    .orElseThrow(() -> new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE, COMPONENT_TYPE,
                            new UnknownEntityException(RtuDevice.class, deviceIdentification)));
            if (this.shouldUpdateCommunicationTime(device, this.minimumDurationBetweenCommunicationTimeUpdates)) {
                device.messageReceived();
                this.rtuDeviceRepository.save(device);
            } else {
                logger.info("Last communication time within duration: {}. Skipping last communication date update.",
                        this.minimumDurationBetweenCommunicationTimeUpdates);
            }
        } catch (final OptimisticLockException ex) {
            logger.warn("Last communication time not updated due to optimistic lock exception", ex);
        }
    }

    private boolean shouldUpdateCommunicationTime(final RtuDevice device,
            final Duration minimumDurationBetweenCommunicationUpdates) {
        final Instant timeToCheck = Instant.now().minus(minimumDurationBetweenCommunicationUpdates);
        final Instant timeOfLastCommunication = device.getLastCommunicationTime();
        return timeOfLastCommunication.isBefore(timeToCheck);
    }
}
