/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.services.transition;

import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.services.AbstractService;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.CdmaBatch;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.CdmaBatchDevice;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.CdmaMastSegment;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.CdmaRun;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.TransitionType;
import org.opensmartgridplatform.dto.valueobjects.TransitionMessageDataContainerDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetTransitionService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetTransitionService.class);

    private int delayBetweenBatchSeconds;

    public SetTransitionService(final int delayBetweenBatchSeconds) {
        LOGGER.info("Delay between batches (in seconds) {}", this.delayBetweenBatchSeconds);
        this.delayBetweenBatchSeconds = delayBetweenBatchSeconds;
    }

    public void transitionDevice(final MessageMetadata metadata, @NotNull final TransitionType transitionType,
            final DateTime transitionTime) throws FunctionalException {

        final String organisationIdentification = metadata.getOrganisationIdentification();
        final String deviceIdentification = metadata.getDeviceIdentification();

        LOGGER.debug("Public setTransitionForDevice called for device {} with organisation {}", deviceIdentification,
                organisationIdentification);

        this.findOrganisation(organisationIdentification);
        final Device device = this.findActiveDevice(deviceIdentification);

        this.transitionDevice(metadata, device.getIpAddress(), transitionType, transitionTime);
    }

    private void transitionDevice(final MessageMetadata metadata, final String ipAddress,
            @NotNull final TransitionType transitionType, final DateTime transitionTime) throws FunctionalException {

        final String organisationIdentification = metadata.getOrganisationIdentification();
        final String deviceIdentification = metadata.getDeviceIdentification();

        LOGGER.info("Private setTransitionForDevice called for device {} with organisation {}", deviceIdentification,
                organisationIdentification);

        final TransitionMessageDataContainerDto transitionMessageDataContainerDto = new TransitionMessageDataContainerDto(
                this.domainCoreMapper.map(transitionType,
                        org.opensmartgridplatform.dto.valueobjects.TransitionTypeDto.class),
                transitionTime);

        this.osgpCoreRequestMessageSender.send(
                new RequestMessage(metadata.getCorrelationUid(), organisationIdentification, deviceIdentification,
                        transitionMessageDataContainerDto),
                metadata.getMessageType(), metadata.getMessagePriority(), ipAddress);
    }

    public void transitionCdmaRun(final CdmaRun cdmaRun, final String organisationIdentification,
            final String correlationUid, final TransitionType transitionType) {
        LOGGER.info("Perform transition run for " + cdmaRun);

        final ExecutorService executor = Executors.newSingleThreadExecutor();

        final Iterator<CdmaMastSegment> mastSegments = cdmaRun.getMastSegmentIterator();
        try {
            mastSegments.forEachRemaining(mastSegment -> executor.submit(
                    new MastSegmentRunnable(mastSegment, organisationIdentification, correlationUid, transitionType)));
        } finally {
            executor.shutdown();
        }

        LOGGER.info("Task creation and submission complete for the mast segments");
    }

    private class MastSegmentRunnable implements Runnable {

        private final CdmaMastSegment mastSegment;
        private final String organisationIdentification;
        private final String correlationUid;
        private final TransitionType transitionType;

        public MastSegmentRunnable(final CdmaMastSegment mastSegment, final String organisationIdentification,
                final String correlationUid, final TransitionType transitionType) {
            this.mastSegment = mastSegment;
            this.organisationIdentification = organisationIdentification;
            this.correlationUid = correlationUid;
            this.transitionType = transitionType;
        }

        @Override
        public void run() {
            final Instant start = Instant.now();
            final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

            final CdmaBatch firstCdmaBatch = this.mastSegment.popCdmaBatch();
            this.submitCdmaBatch(executor, firstCdmaBatch, this.organisationIdentification, this.correlationUid,
                    this.transitionType);

            // Create a scheduled task for the remaining batches of this
            // mastSegment
            if (!this.mastSegment.empty()) {
                try {
                    final Instant now = Instant.now();
                    final Duration currentTaskDuration = Duration.between(now, start);

                    final long delayInMillis = Duration.ofSeconds(SetTransitionService.this.delayBetweenBatchSeconds)
                            .minus(currentTaskDuration).toMillis();
                    executor.schedule(new MastSegmentRunnable(this.mastSegment, this.organisationIdentification,
                            this.correlationUid, this.transitionType), delayInMillis, TimeUnit.MILLISECONDS);
                } finally {
                    executor.shutdown();
                }
            }
        }

        private void submitCdmaBatch(final ExecutorService executor, final CdmaBatch cdmaBatch,
                final String organisationIdentification, final String correlationUid,
                final TransitionType transitionType) {
            executor.submit(() -> {
                LOGGER.info("Submit CdmaBatch {}", cdmaBatch.getBatchNumber());
                final List<CdmaBatchDevice> devices = cdmaBatch.getCdmaBatchDevices();

                // Send a message for each device
                devices.forEach(device -> MastSegmentRunnable.this.transitionDeviceRunnable(device,
                        organisationIdentification, correlationUid, transitionType));
            });
        }

        private void transitionDeviceRunnable(final CdmaBatchDevice device, final String organisationIdentification,
                final String correlationUid, final TransitionType transitionType) {
            final MessageMetadata metadata = new MessageMetadata.Builder(correlationUid, organisationIdentification,
                    device.getDeviceIdentification(), DeviceFunction.SET_TRANSITION.name()).build();

            final DateTime emptyTransitionTime = null;
            final InetAddress inetAddress = device.getInetAddress();

            try {
                if (inetAddress != null) {
                    SetTransitionService.this.transitionDevice(metadata, inetAddress.getHostAddress(), transitionType,
                            emptyTransitionTime);
                } else {
                    LOGGER.warn("setTransition not possible, because InetAddress is null for device {}",
                            metadata.getDeviceIdentification());
                }
            } catch (final Exception e) {
                LOGGER.warn("Set transition failed failed for " + metadata, e);
            }
        }
    }
}
