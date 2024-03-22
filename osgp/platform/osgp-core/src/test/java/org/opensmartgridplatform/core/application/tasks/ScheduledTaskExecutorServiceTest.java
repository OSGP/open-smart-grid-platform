// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.application.tasks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.util.concurrent.MoreExecutors;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.core.application.config.ScheduledTaskExecutorJobConfig;
import org.opensmartgridplatform.core.application.services.DeviceRequestMessageService;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.ScheduledTask;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.ScheduledTaskRepository;
import org.opensmartgridplatform.domain.core.valueobjects.ScheduledTaskStatusType;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ProtocolRequestMessage;
import org.quartz.JobExecutionException;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

/** test class for ScheduledTaskExecutorService */
@ExtendWith(MockitoExtension.class)
class ScheduledTaskExecutorServiceTest {
  private static final long AWAIT_TERMINATION_IN_SEC = 5;

  private static final String DOMAIN = "Domain";

  private static final String DATA_OBJECT = "data object";

  private static final Timestamp INITIAL_SCHEDULED_TIME = new Timestamp(System.currentTimeMillis());

  private static final int MAX_RETRY_COUNT = 0;

  @Mock private DeviceRequestMessageService deviceRequestMessageService;

  @Mock private ScheduledTaskRepository scheduledTaskRepository;
  @Mock private DeviceRepository deviceRepository;
  private ScheduledTaskExecutorService scheduledTaskExecutorService;
  @Mock private ScheduledTaskExecutorJobConfig scheduledTaskExecutorJobConfig;

  @Captor private ArgumentCaptor<List<ScheduledTask>> scheduledTaskCaptor;

  @Captor private ArgumentCaptor<ProtocolRequestMessage> protocolRequestMessageCaptor;

  @BeforeEach
  void setUp() {
    this.scheduledTaskExecutorService =
        new ScheduledTaskExecutorService(
            this.deviceRequestMessageService,
            this.scheduledTaskRepository,
            this.deviceRepository,
            this.scheduledTaskExecutorJobConfig,
            MAX_RETRY_COUNT);
  }

  /**
   * Test the scheduled task runner for the case when the deviceRequestMessageService gives a
   * functional exception
   *
   * @throws FunctionalException
   * @throws UnknownHostException
   * @throws JobExecutionException
   */
  @Test
  void testRunFunctionalException() throws FunctionalException {
    final List<ScheduledTask> scheduledTasks = new ArrayList<>();
    final Timestamp scheduledTime = new Timestamp(System.currentTimeMillis());
    final ScheduledTask scheduledTask =
        new ScheduledTask(this.createMessageMetadata(), DOMAIN, DOMAIN, DATA_OBJECT, scheduledTime);
    scheduledTasks.add(scheduledTask);

    when(this.scheduledTaskRepository.findByStatusAndScheduledTimeLessThan(
            eq(ScheduledTaskStatusType.PENDING), any(Timestamp.class), any(Pageable.class)))
        .thenReturn(new ArrayList<>());
    when(this.scheduledTaskRepository.findByStatusAndScheduledTimeLessThan(
            eq(ScheduledTaskStatusType.NEW), any(Timestamp.class), any(Pageable.class)))
        .thenReturn(scheduledTasks)
        .thenReturn(new ArrayList<>());
    when(this.scheduledTaskRepository.findByStatusAndScheduledTimeLessThan(
            eq(ScheduledTaskStatusType.RETRY), any(Timestamp.class), any(Pageable.class)))
        .thenReturn(new ArrayList<>());
    when(this.scheduledTaskExecutorJobConfig.getScheduledTaskThreadPoolSize()).thenReturn(1);

    final Device device = new Device();
    device.updateRegistrationData("127.0.0.1", "deviceType");
    when(this.deviceRepository.findByDeviceIdentification(anyString())).thenReturn(device);
    when(this.scheduledTaskRepository.updateStatus(
            scheduledTask.getId(), ScheduledTaskStatusType.PENDING))
        .thenReturn(1);
    when(this.scheduledTaskExecutorJobConfig.scheduledTaskPageSize()).thenReturn(30);
    doThrow(new FunctionalException(FunctionalExceptionType.ARGUMENT_NULL, ComponentType.OSGP_CORE))
        .when(this.deviceRequestMessageService)
        .processMessage(any(ProtocolRequestMessage.class));

    this.scheduledTaskExecutorService.processScheduledTasks();

    // check if task is deleted
    verify(this.scheduledTaskRepository).delete(scheduledTask);
  }

  @Test
  void testRunNewAndRetryTasks() throws FunctionalException {
    final List<ScheduledTask> scheduledTasks = new ArrayList<>();
    for (int i = 0; i < 1000; i++) {
      final Timestamp scheduledTime = new Timestamp(System.currentTimeMillis());
      final ScheduledTask scheduledTask =
          new ScheduledTask(
              this.createMessageMetadata(), DOMAIN, DOMAIN, DATA_OBJECT, scheduledTime);
      ReflectionTestUtils.setField(scheduledTask, "id", Integer.valueOf(i).longValue());
      scheduledTasks.add(scheduledTask);
    }

    when(this.scheduledTaskRepository.findByStatusAndScheduledTimeLessThan(
            eq(ScheduledTaskStatusType.PENDING), any(Timestamp.class), any(Pageable.class)))
        .thenReturn(new ArrayList<>());
    when(this.scheduledTaskRepository.findByStatusAndScheduledTimeLessThan(
            eq(ScheduledTaskStatusType.NEW), any(Timestamp.class), any(Pageable.class)))
        .thenReturn(scheduledTasks)
        .thenReturn(new ArrayList<>());
    when(this.scheduledTaskRepository.findByStatusAndScheduledTimeLessThan(
            eq(ScheduledTaskStatusType.RETRY), any(Timestamp.class), any(Pageable.class)))
        .thenReturn(scheduledTasks)
        .thenReturn(new ArrayList<>());
    when(this.scheduledTaskExecutorJobConfig.getScheduledTaskThreadPoolSize()).thenReturn(1);

    final Device device = new Device();
    device.updateRegistrationData("127.0.0.1", "deviceType");
    when(this.deviceRepository.findByDeviceIdentification(anyString())).thenReturn(device);
    when(this.scheduledTaskExecutorJobConfig.scheduledTaskPageSize()).thenReturn(30);

    try (final MockedStatic<MoreExecutors> mockStatic = mockStatic(MoreExecutors.class)) {
      this.scheduledTaskExecutorService.processScheduledTasks();

      mockStatic.verify(
          () ->
              MoreExecutors.shutdownAndAwaitTermination(
                  any(ExecutorService.class), eq(Duration.ofSeconds(AWAIT_TERMINATION_IN_SEC))),
          times(2));
    }

    verify(this.deviceRequestMessageService, times(scheduledTasks.size() * 2))
        .processMessage(any(ProtocolRequestMessage.class));
    verify(this.scheduledTaskRepository, times(scheduledTasks.size() * 2))
        .updateStatus(any(Long.class), eq(ScheduledTaskStatusType.PENDING));
  }

  @Test
  void testRetryStrandedPendingTask() {

    // 0) expired (beyond max scheduled time but not exceeded max number of retries);
    // 1) retryable (before max scheduled time and not exceeded max number of retries)
    // 2) expired and exceeded (beyond max scheduled time and exceeded max number of retries);
    // 3) exceeded (before max scheduled time but exceeded max number of retries)
    final List<ScheduledTask> expiredPendingTasks = this.createPendingTasks();

    when(this.scheduledTaskExecutorJobConfig.scheduledTaskPendingDurationMaxSeconds())
        .thenReturn(-1L);
    when(this.scheduledTaskExecutorJobConfig.scheduledTaskPageSize()).thenReturn(30);
    this.whenFindByStatusAndScheduledTime(
        expiredPendingTasks, new ArrayList<>(), new ArrayList<>());

    this.scheduledTaskExecutorService.processScheduledTasks();

    verify(this.scheduledTaskRepository).saveAll(this.scheduledTaskCaptor.capture());
    final List<ScheduledTask> retryScheduledTasks = this.scheduledTaskCaptor.getValue();
    assertThat(retryScheduledTasks).hasSize(1);
    assertThat(retryScheduledTasks.get(0).getStatus()).isEqualTo(ScheduledTaskStatusType.RETRY);
    assertThat(retryScheduledTasks.get(0).getScheduledTime()).isAfter(INITIAL_SCHEDULED_TIME);

    verify(this.scheduledTaskRepository).deleteAll(this.scheduledTaskCaptor.capture());
    final List<ScheduledTask> deleteScheduledTasks = this.scheduledTaskCaptor.getValue();
    assertThat(deleteScheduledTasks).hasSize(3);
  }

  @Test
  void testMetadataOfScheduledTaskToRetryRequest() throws FunctionalException {
    final String deviceIdentification = "device-1";
    final String deviceModelCode = "E,M1,M2,M3,M4";
    final MessageMetadata messageMetadata =
        this.createMessageMetadata(deviceIdentification, deviceModelCode);
    final ScheduledTask scheduledTask =
        new ScheduledTask(messageMetadata, DOMAIN, DOMAIN, DATA_OBJECT, INITIAL_SCHEDULED_TIME);
    final Device device = new Device();

    when(this.scheduledTaskExecutorJobConfig.scheduledTaskPendingDurationMaxSeconds())
        .thenReturn(-1L);
    when(this.scheduledTaskExecutorJobConfig.scheduledTaskPageSize()).thenReturn(30);
    when(this.scheduledTaskRepository.updateStatus(
            scheduledTask.getId(), ScheduledTaskStatusType.PENDING))
        .thenReturn(1);
    when(this.deviceRepository.findByDeviceIdentification(deviceIdentification)).thenReturn(device);
    when(this.scheduledTaskRepository.findByStatusAndScheduledTimeLessThan(
            eq(ScheduledTaskStatusType.PENDING), any(Timestamp.class), any(Pageable.class)))
        .thenReturn(new ArrayList<>());
    when(this.scheduledTaskRepository.findByStatusAndScheduledTimeLessThan(
            eq(ScheduledTaskStatusType.NEW), any(Timestamp.class), any(Pageable.class)))
        .thenReturn(List.of(scheduledTask), Collections.emptyList());
    when(this.scheduledTaskRepository.findByStatusAndScheduledTimeLessThan(
            eq(ScheduledTaskStatusType.RETRY), any(Timestamp.class), any(Pageable.class)))
        .thenReturn(new ArrayList<>());
    when(this.scheduledTaskExecutorJobConfig.getScheduledTaskThreadPoolSize()).thenReturn(1);

    this.scheduledTaskExecutorService.processScheduledTasks();

    verify(this.deviceRequestMessageService)
        .processMessage(this.protocolRequestMessageCaptor.capture());
    final ProtocolRequestMessage protocolRequestMessage =
        this.protocolRequestMessageCaptor.getValue();
    assertThat(protocolRequestMessage).isNotNull();
    assertThat(protocolRequestMessage.getDeviceIdentification()).isEqualTo(deviceIdentification);
    assertThat(protocolRequestMessage.getDeviceModelCode()).isEqualTo(deviceModelCode);
  }

  private void whenFindByStatusAndScheduledTime(
      final List<ScheduledTask> pendingTasks,
      final List<ScheduledTask> newTasks,
      final List<ScheduledTask> retryTasks) {
    when(this.scheduledTaskRepository.findByStatusAndScheduledTimeLessThan(
            eq(ScheduledTaskStatusType.PENDING), any(Timestamp.class), any(Pageable.class)))
        .thenReturn(pendingTasks);
    when(this.scheduledTaskRepository.findByStatusAndScheduledTimeLessThan(
            eq(ScheduledTaskStatusType.NEW), any(Timestamp.class), any(Pageable.class)))
        .thenReturn(newTasks);
    when(this.scheduledTaskRepository.findByStatusAndScheduledTimeLessThan(
            eq(ScheduledTaskStatusType.RETRY), any(Timestamp.class), any(Pageable.class)))
        .thenReturn(retryTasks);
  }

  private List<ScheduledTask> createPendingTasks() {
    // Create a list of two scheduled tasks both in pending state.
    final List<ScheduledTask> scheduledTasks = new ArrayList<>();
    scheduledTasks.add(this.createScheduledTask(false, true));
    scheduledTasks.add(this.createScheduledTask(false, false));
    scheduledTasks.add(this.createScheduledTask(true, true));
    scheduledTasks.add(this.createScheduledTask(true, false));
    return scheduledTasks;
  }

  private ScheduledTask createScheduledTask(
      final boolean exceededMaxRetry, final boolean expiredTask) {
    final MessageMetadata messageMetadata;
    if (expiredTask) {
      messageMetadata = this.createExpiredMessageMetadata();
    } else {
      messageMetadata = this.createMessageMetadata();
    }
    final ScheduledTask expiredScheduledTask =
        new ScheduledTask(messageMetadata, DOMAIN, DOMAIN, DATA_OBJECT, INITIAL_SCHEDULED_TIME);
    // retryOn() will raise the number of retries. The retry time will not change since it is the
    // same as the retry time in the message metadata. State will be set RETRY and will be reset to
    // PENDING by the setPending method. This is the only way to raise the number of retry above the
    // maxRetries (0)
    if (exceededMaxRetry) {
      expiredScheduledTask.retryOn(INITIAL_SCHEDULED_TIME);
    }
    expiredScheduledTask.setPending();
    return expiredScheduledTask;
  }

  private MessageMetadata createExpiredMessageMetadata() {
    return this.createMessageMetadataBuilder()
        .withMaxScheduleTime(Instant.now().minus(100, ChronoUnit.SECONDS).toEpochMilli())
        .withDeviceIdentification("expired")
        .build();
  }

  private MessageMetadata createMessageMetadata() {
    return this.createMessageMetadata("retryable", null);
  }

  private MessageMetadata createMessageMetadata(
      final String deviceIdentification, final String deviceModelCode) {
    return this.createMessageMetadataBuilder()
        .withDeviceIdentification(deviceIdentification)
        .withDeviceModelCode(deviceModelCode)
        .build();
  }

  private MessageMetadata.Builder createMessageMetadataBuilder() {
    return new MessageMetadata.Builder()
        .withOrganisationIdentification("organisationId")
        .withCorrelationUid("correlationId")
        .withMessageType("messageType")
        .withMessagePriority(4);
  }
}
