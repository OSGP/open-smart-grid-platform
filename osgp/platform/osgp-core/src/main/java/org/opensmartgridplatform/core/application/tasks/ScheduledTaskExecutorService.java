// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.application.tasks;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
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

  @Autowired private int getMaxRetryCount;

  public void processScheduledTasks() {
    this.processStrandedScheduledTasks();
    this.processScheduledTasks(ScheduledTaskStatusType.NEW);
    this.processScheduledTasks(ScheduledTaskStatusType.RETRY);
  }

  private void processStrandedScheduledTasks() {

    final List<ScheduledTask> scheduledTasks =
        this.getScheduledTasks(ScheduledTaskStatusType.PENDING);

    final long maxDurationPending =
        this.scheduledTaskExecutorJobConfig.scheduledTaskPendingDurationMaxSeconds();

    final Instant ultimatePendingTime = Instant.now().minus(maxDurationPending, ChronoUnit.SECONDS);

    final Predicate<? super ScheduledTask> pendingExceeded =
        st -> st.getModificationTimeInstant().isBefore(ultimatePendingTime);

    final List<ScheduledTask> strandedScheduledTasks =
        scheduledTasks.stream().filter(pendingExceeded).toList();

    final List<ScheduledTask> retryScheduledTasks = new ArrayList<>();
    final List<ScheduledTask> deleteScheduledTasks = new ArrayList<>();

    strandedScheduledTasks.forEach(
        strandedScheduledTask -> {
          if (this.shouldBeRetried(strandedScheduledTask)) {
            strandedScheduledTask.retryOn(new Date());
            retryScheduledTasks.add(strandedScheduledTask);
            LOGGER.info(
                "Scheduled task for device {} with correlationUid {} will be retried",
                strandedScheduledTask.getDeviceIdentification(),
                strandedScheduledTask.getCorrelationId());
          } else {
            LOGGER.info(
                "Scheduled task for device {} with correlationUid {} will be removed",
                strandedScheduledTask.getDeviceIdentification(),
                strandedScheduledTask.getCorrelationId());
            deleteScheduledTasks.add(strandedScheduledTask);
          }
        });

    this.scheduledTaskRepository.saveAll(retryScheduledTasks);
    this.scheduledTaskRepository.deleteAll(deleteScheduledTasks);
  }

  private boolean shouldBeRetried(final ScheduledTask scheduledTask) {
    return !this.maxScheduledTimeExceeded(scheduledTask) && !this.maxRetriesExceeded(scheduledTask);
  }

  private boolean maxRetriesExceeded(final ScheduledTask scheduledTask) {
    return scheduledTask.getRetry() > this.getMaxRetryCount;
  }

  private boolean maxScheduledTimeExceeded(final ScheduledTask scheduledTask) {
    return scheduledTask.getMaxScheduleTime() != null
        && scheduledTask.getMaxScheduleTime().getTime() <= System.currentTimeMillis();
  }

  private void processScheduledTasks(final ScheduledTaskStatusType type) {
    /*
     * Fetch scheduled tasks for given scheduledTaskStatusTypes: NEW and RETRY. The processed tasks
     * are set to PENDING, so they will not be fetched by this method.
     */
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
        .withNetworkSegmentIds(device.getBtsId(), device.getCellId())
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
    if (device.getNetworkAddress() == null && device.getGatewayDevice() != null) {
      return device.getGatewayDevice().getNetworkAddress();
    }
    return device.getNetworkAddress();
  }
}
