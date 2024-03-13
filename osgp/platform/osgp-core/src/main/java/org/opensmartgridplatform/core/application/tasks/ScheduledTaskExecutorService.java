// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.application.tasks;

import static com.google.common.util.concurrent.MoreExecutors.shutdownAndAwaitTermination;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTaskExecutorService {
  private static final long AWAIT_TERMINATION_IN_SEC = 5;

  private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTaskExecutorService.class);

  private final DeviceRequestMessageService deviceRequestMessageService;
  private final ScheduledTaskRepository scheduledTaskRepository;
  private final DeviceRepository deviceRepository;
  private final ScheduledTaskExecutorJobConfig scheduledTaskExecutorJobConfig;
  private final int getMaxRetryCount;

  public ScheduledTaskExecutorService(
      final DeviceRequestMessageService deviceRequestMessageService,
      final ScheduledTaskRepository scheduledTaskRepository,
      final DeviceRepository deviceRepository,
      final ScheduledTaskExecutorJobConfig scheduledTaskExecutorJobConfig,
      final int getMaxRetryCount) {
    this.deviceRequestMessageService = deviceRequestMessageService;
    this.scheduledTaskRepository = scheduledTaskRepository;
    this.deviceRepository = deviceRepository;
    this.scheduledTaskExecutorJobConfig = scheduledTaskExecutorJobConfig;
    this.getMaxRetryCount = getMaxRetryCount;
  }

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
      final ExecutorService executorService =
          Executors.newFixedThreadPool(
              this.scheduledTaskExecutorJobConfig.getScheduledTaskThreadPoolSize());
      try {
        final List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (final ScheduledTask scheduledTask : scheduledTasks) {
          final CompletableFuture<Void> future =
              CompletableFuture.runAsync(
                  () -> this.processScheduledTask(scheduledTask), executorService);
          futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
      } finally {
        shutdownAndAwaitTermination(executorService, Duration.ofSeconds(AWAIT_TERMINATION_IN_SEC));
      }

      scheduledTasks = this.getScheduledTasks(type);
    }
  }

  private void processScheduledTask(final ScheduledTask scheduledTask) {
    LOGGER.info(
        "Processing scheduled task for device [{}] to perform [{}]  ",
        scheduledTask.getDeviceIdentification(),
        scheduledTask.getMessageType());
    try {
      this.scheduledTaskRepository.updateStatus(
          scheduledTask.getId(), ScheduledTaskStatusType.PENDING);
      final ProtocolRequestMessage protocolRequestMessage =
          this.createProtocolRequestMessage(scheduledTask);
      this.deviceRequestMessageService.processMessage(protocolRequestMessage);
    } catch (final FunctionalException e) {
      LOGGER.error("Processing scheduled task failed.", e);
      this.scheduledTaskRepository.delete(scheduledTask);
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
        .withNetworkAddress(getIpAddress(device))
        .withNetworkSegmentIds(device.getBtsId(), device.getCellId())
        .withMessagePriority(scheduledTask.getMessagePriority())
        .withDeviceModelCode(scheduledTask.getDeviceModelCode())
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
