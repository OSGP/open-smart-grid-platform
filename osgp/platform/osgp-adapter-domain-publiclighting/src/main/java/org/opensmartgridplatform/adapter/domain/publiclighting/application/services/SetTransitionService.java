/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.services;

import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import javax.validation.constraints.NotNull;
import org.joda.time.DateTime;
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

  private final Integer delayBetweenBatchSeconds;
  private final ScheduledExecutorService executor;

  public SetTransitionService(
      final ScheduledExecutorService executor, final int delayBetweenBatchSeconds) {
    this.executor = executor;
    this.delayBetweenBatchSeconds = delayBetweenBatchSeconds;
  }

  public void setTransitionForDevice(
      final MessageMetadata metadata,
      @NotNull final TransitionType transitionType,
      final DateTime transitionTime)
      throws FunctionalException {

    final String organisationIdentification = metadata.getOrganisationIdentification();
    final String deviceIdentification = metadata.getDeviceIdentification();

    LOGGER.debug(
        "Public setTransitionForDevice called for device {} with organisation {}",
        deviceIdentification,
        organisationIdentification);

    this.findOrganisation(organisationIdentification);
    final Device device = this.findActiveDevice(deviceIdentification);

    this.setTransitionForDevice(metadata, device.getIpAddress(), transitionType, transitionTime);
  }

  private void setTransitionForDevice(
      final MessageMetadata metadata,
      final String ipAddress,
      @NotNull final TransitionType transitionType,
      final DateTime transitionTime) {

    final String organisationIdentification = metadata.getOrganisationIdentification();
    final String deviceIdentification = metadata.getDeviceIdentification();

    LOGGER.debug(
        "Private setTransitionForDevice called for device {} with organisation {}",
        deviceIdentification,
        organisationIdentification);

    final TransitionMessageDataContainerDto transitionMessageDataContainerDto =
        new TransitionMessageDataContainerDto(
            this.domainCoreMapper.map(
                transitionType, org.opensmartgridplatform.dto.valueobjects.TransitionTypeDto.class),
            transitionTime);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            metadata.getCorrelationUid(),
            organisationIdentification,
            deviceIdentification,
            transitionMessageDataContainerDto),
        metadata.getMessageType(),
        metadata.getMessagePriority(),
        ipAddress);
  }

  public synchronized void setTransitionForCdmaRun(
      final CdmaRun cdmaRun,
      final String organisationIdentification,
      final String correlationUid,
      final TransitionType transitionType) {
    if (this.executor.isShutdown()) {
      throw new IllegalStateException(
          "Executor is already shutdown. Starting a new CdmaRun is not allowed.");
    }

    LOGGER.info("Perform transition run for " + cdmaRun);

    final Iterator<CdmaMastSegment> mastSegments = cdmaRun.getMastSegmentIterator();
    mastSegments.forEachRemaining(
        mastSegment ->
            this.executor.submit(
                new MastSegmentRunnable(
                    mastSegment, organisationIdentification, correlationUid, transitionType)));

    LOGGER.info("Task creation and submission complete for the mast segments");
  }

  @PreDestroy
  public void destroy() {
    LOGGER.info("Shutting down executor, cancelling running tasks");
    this.executor.shutdownNow();

    // Wait briefly for running tasks being cancelled
    try {
      if (!this.executor.awaitTermination(5, TimeUnit.SECONDS)) {
        LOGGER.warn("Executor pool for SetTransition did not shutdown");
      }
    } catch (final InterruptedException e) {
      LOGGER.error("Shutdown executor pool interrupted", e);
      // Preserve interrupt status
      Thread.currentThread().interrupt();
    }
  }

  private class MastSegmentRunnable implements Runnable {

    private final CdmaMastSegment mastSegment;
    private final String organisationIdentification;
    private final String correlationUid;
    private final TransitionType transitionType;

    public MastSegmentRunnable(
        final CdmaMastSegment mastSegment,
        final String organisationIdentification,
        final String correlationUid,
        final TransitionType transitionType) {
      this.mastSegment = mastSegment;
      this.organisationIdentification = organisationIdentification;
      this.correlationUid = correlationUid;
      this.transitionType = transitionType;
    }

    @Override
    public void run() {
      final Instant start = Instant.now();

      final CdmaBatch firstCdmaBatch = this.mastSegment.popCdmaBatch();
      this.submitCdmaBatch(
          firstCdmaBatch,
          this.organisationIdentification,
          this.correlationUid,
          this.transitionType);

      // Create a scheduled task for the remaining batches of this
      // mastSegment
      if (!this.mastSegment.empty()) {
        final Instant now = Instant.now();
        final Duration currentTaskDuration = Duration.between(now, start);

        final long delayInMillis =
            Duration.ofSeconds(SetTransitionService.this.delayBetweenBatchSeconds)
                .minus(currentTaskDuration)
                .toMillis();
        SetTransitionService.this.executor.schedule(
            new MastSegmentRunnable(
                this.mastSegment,
                this.organisationIdentification,
                this.correlationUid,
                this.transitionType),
            delayInMillis,
            TimeUnit.MILLISECONDS);
      }
    }

    private void submitCdmaBatch(
        final CdmaBatch cdmaBatch,
        final String organisationIdentification,
        final String correlationUid,
        final TransitionType transitionType) {
      SetTransitionService.this.executor.submit(
          () -> {
            LOGGER.info("Send messages for {}", cdmaBatch);
            final Set<CdmaBatchDevice> devices = cdmaBatch.getCdmaBatchDevices();

            // Send a message for each device
            devices.forEach(
                device ->
                    MastSegmentRunnable.this.setTransitionForDeviceRunnable(
                        device, organisationIdentification, correlationUid, transitionType));
          });
    }

    private void setTransitionForDeviceRunnable(
        final CdmaBatchDevice device,
        final String organisationIdentification,
        final String correlationUid,
        final TransitionType transitionType) {
      final MessageMetadata metadata =
          new MessageMetadata.Builder(
                  correlationUid,
                  organisationIdentification,
                  device.getDeviceIdentification(),
                  DeviceFunction.SET_TRANSITION.name())
              .build();

      final DateTime emptyTransitionTime = null;
      final InetAddress inetAddress = device.getInetAddress();

      try {
        if (inetAddress != null) {
          SetTransitionService.this.setTransitionForDevice(
              metadata, inetAddress.getHostAddress(), transitionType, emptyTransitionTime);
        } else {
          LOGGER.warn(
              "setTransition not possible, because InetAddress is null for device {}",
              metadata.getDeviceIdentification());
        }
      } catch (final Exception e) {
        LOGGER.warn("Set transition failed for " + metadata, e);
      }
    }
  }
}
