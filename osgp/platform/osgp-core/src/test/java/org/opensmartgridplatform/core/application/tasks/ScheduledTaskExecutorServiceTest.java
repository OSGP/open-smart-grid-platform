//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.core.application.tasks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

/** test class for ScheduledTaskExecutorService */
@ExtendWith(MockitoExtension.class)
public class ScheduledTaskExecutorServiceTest {

  private static final String DOMAIN = "Domain";

  private static final String DATA_OBJECT = "data object";

  private static final Timestamp SCHEDULED_TIME = new Timestamp(System.currentTimeMillis());

  @Mock private DeviceRequestMessageService deviceRequestMessageService;

  @Mock private ScheduledTaskRepository scheduledTaskRepository;
  @Mock private DeviceRepository deviceRepository;
  @InjectMocks private ScheduledTaskExecutorService scheduledTaskExecutorService;
  @Mock private ScheduledTaskExecutorJobConfig scheduledTaskExecutorJobConfig;

  @Captor private ArgumentCaptor<ScheduledTask> scheduledTaskCaptor;

  /**
   * Test the scheduled task runner for the case when the deviceRequestMessageService gives a
   * functional exception
   *
   * @throws FunctionalException
   * @throws UnknownHostException
   * @throws JobExecutionException
   */
  @Test
  void testRunFunctionalException()
      throws FunctionalException, UnknownHostException, JobExecutionException {
    final List<ScheduledTask> scheduledTasks = new ArrayList<>();
    final Timestamp scheduledTime = new Timestamp(System.currentTimeMillis());
    final ScheduledTask scheduledTask =
        new ScheduledTask(this.createMessageMetadata(), DOMAIN, DOMAIN, DATA_OBJECT, scheduledTime);
    scheduledTasks.add(scheduledTask);

    when(this.scheduledTaskRepository.findByStatusAndScheduledTimeLessThan(
            any(ScheduledTaskStatusType.class), any(Timestamp.class), any(Pageable.class)))
        .thenReturn(new ArrayList<ScheduledTask>())
        .thenReturn(scheduledTasks)
        .thenReturn(new ArrayList<ScheduledTask>());

    final Device device = new Device();
    device.updateRegistrationData(InetAddress.getByName("127.0.0.1"), "deviceType");
    when(this.deviceRepository.findByDeviceIdentification(anyString())).thenReturn(device);
    when(this.scheduledTaskRepository.save(any(ScheduledTask.class))).thenReturn(scheduledTask);
    when(this.scheduledTaskExecutorJobConfig.scheduledTaskPageSize()).thenReturn(30);
    doThrow(new FunctionalException(FunctionalExceptionType.ARGUMENT_NULL, ComponentType.OSGP_CORE))
        .when(this.deviceRequestMessageService)
        .processMessage(any(ProtocolRequestMessage.class));

    this.scheduledTaskExecutorService.processScheduledTasks();

    // check if task is deleted
    verify(this.scheduledTaskRepository).delete(scheduledTask);
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
        expiredPendingTasks, new ArrayList<ScheduledTask>(), new ArrayList<ScheduledTask>());

    this.scheduledTaskExecutorService.processScheduledTasks();

    verify(this.scheduledTaskRepository, times(4)).save(this.scheduledTaskCaptor.capture());
    final List<ScheduledTask> savedScheduledTasks = this.scheduledTaskCaptor.getAllValues();

    assertThat(savedScheduledTasks.get(0).getStatus()).isEqualTo(ScheduledTaskStatusType.FAILED);
    assertThat(savedScheduledTasks.get(1).getStatus()).isEqualTo(ScheduledTaskStatusType.RETRY);
    assertThat(savedScheduledTasks.get(2).getStatus()).isEqualTo(ScheduledTaskStatusType.FAILED);
    assertThat(savedScheduledTasks.get(3).getStatus()).isEqualTo(ScheduledTaskStatusType.FAILED);
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
    MessageMetadata messageMetadata;
    if (expiredTask) {
      messageMetadata = this.createExpiredMessageMetadata();
    } else {
      messageMetadata = this.createMessageMetadata();
    }
    final ScheduledTask expiredScheduledTask =
        new ScheduledTask(messageMetadata, DOMAIN, DOMAIN, DATA_OBJECT, SCHEDULED_TIME);
    // retryOn() will raise the number of retries. The retry time will not change since it is the
    // same as the retry time in the message metadata. State will be set RETRY and will be reset to
    // PENDING by the setPending method. This is the only way to raise the number of retry above the
    // maxRetries (0)
    if (exceededMaxRetry) {
      expiredScheduledTask.retryOn(SCHEDULED_TIME);
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
    return this.createMessageMetadataBuilder().withDeviceIdentification("retryable").build();
  }

  private MessageMetadata.Builder createMessageMetadataBuilder() {
    return new MessageMetadata.Builder()
        .withOrganisationIdentification("organisationId")
        .withCorrelationUid("correlationId")
        .withMessageType("messageType")
        .withMessagePriority(4);
  }
}
