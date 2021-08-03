/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.application.tasks;

import java.sql.Timestamp;
import java.util.List;
import org.opensmartgridplatform.core.application.config.ScheduledTaskExecutorJobConfig;
import org.opensmartgridplatform.core.application.services.DeviceRequestMessageService;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.ScheduledTask;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.ScheduledTaskRepository;
import org.opensmartgridplatform.domain.core.valueobjects.ScheduledTaskStatusType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ProtocolRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTaskExecutorService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTaskExecutorService.class);

  @Autowired private DeviceRequestMessageService deviceRequestMessageService;

  @Autowired private ScheduledTaskRepository scheduledTaskRepository;

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private ScheduledTaskExecutorJobConfig scheduledTaskExecutorJobConfig;

  public void processScheduledTasks() {
    this.processScheduledTasks(ScheduledTaskStatusType.NEW);
    this.processScheduledTasks(ScheduledTaskStatusType.RETRY);
  }

  private void processScheduledTasks(final ScheduledTaskStatusType type) {
    List<ScheduledTask> scheduledTasks = this.getScheduledTasks(type);

    while (!scheduledTasks.isEmpty()) {
      for (ScheduledTask scheduledTask : scheduledTasks) {
        LOGGER.info(
            "Processing scheduled task for device [{}] to perform [{}]  ",
            scheduledTask.getDeviceIdentification(),
            scheduledTask.getMessageType());
        try {
          scheduledTask.setPending();
          scheduledTask = this.scheduledTaskRepository.save(scheduledTask);
          final ProtocolRequestMessage protocolRequestMessage =
              this.createProtocolRequestMessage(scheduledTask);
          this.deviceRequestMessageService.processMessage(protocolRequestMessage);
        } catch (final FunctionalException e) {
          LOGGER.error("Processing scheduled task failed.", e);
          this.scheduledTaskRepository.delete(scheduledTask);
        }
      }
      scheduledTasks = this.getScheduledTasks(type);
    }
  }

  /**
   * Fetch scheduled tasks for given scheduledTaskStatusTypes: NEW and RETRY. The processed tasks
   * are set to PENDING, so they will not be fetched by this method.
   *
   * @param type ScheduledTaskStatusType (NEW, PENDING, COMPLETE, FAILED, RETRY)
   * @return List of ScheduledTasks, paged
   */
  private List<ScheduledTask> getScheduledTasks(final ScheduledTaskStatusType type) {
    final Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    // configurable page size for scheduled tasks
    final Pageable pageable =
        PageRequest.of(0, this.scheduledTaskExecutorJobConfig.scheduledTaskPageSize());

    return this.scheduledTaskRepository.findByStatusAndScheduledTimeLessThan(
        type, timestamp, pageable);
  }

  private ProtocolRequestMessage createProtocolRequestMessage(final ScheduledTask scheduledTask) {
    final Device device =
        this.deviceRepository.findByDeviceIdentification(scheduledTask.getDeviceIdentification());

    final MessageMetadata messageMetadata =
        messageMetadataFromScheduledTaskForDevice(scheduledTask, device);

    return ProtocolRequestMessage.newBuilder()
        .messageMetadata(messageMetadata)
        .request(scheduledTask.getMessageData())
        .build();
  }

  private static MessageMetadata messageMetadataFromScheduledTaskForDevice(
      final ScheduledTask scheduledTask, final Device device) {

    return MessageMetadata.newBuilder()
        .withDeviceIdentification(scheduledTask.getDeviceIdentification())
        .withOrganisationIdentification(scheduledTask.getOrganisationIdentification())
        .withCorrelationUid(scheduledTask.getCorrelationId())
        .withMessageType(scheduledTask.getMessageType())
        .withDomain(scheduledTask.getDomain())
        .withDomainVersion(scheduledTask.getDomainVersion())
        .withIpAddress(getIpAddress(device))
        .withMessagePriority(scheduledTask.getMessagePriority())
        .withScheduled(true)
        .withMaxScheduleTime(
            scheduledTask.getMaxScheduleTime() == null
                ? null
                : scheduledTask.getMaxScheduleTime().getTime())
        .withRetryCount(scheduledTask.getRetry())
        .build();
  }

  private static String getIpAddress(final Device device) {
    if (device.getIpAddress() == null && device.getGatewayDevice() != null) {
      return device.getGatewayDevice().getIpAddress();
    }
    return device.getIpAddress();
  }
}
