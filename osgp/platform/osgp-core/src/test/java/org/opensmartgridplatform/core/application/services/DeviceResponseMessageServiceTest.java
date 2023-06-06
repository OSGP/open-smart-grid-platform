// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.core.domain.model.domain.DomainResponseService;
import org.opensmartgridplatform.domain.core.entities.ScheduledTask;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.shared.infra.jms.RetryHeader;

/** test class for DeviceResponseMessageService */
@ExtendWith(MockitoExtension.class)
public class DeviceResponseMessageServiceTest {

  private static final String DOMAIN = "Domain";
  private static final String DOMAIN_VERSION = "1.0";
  private static final MessageMetadata MESSAGE_METADATA =
      new MessageMetadata.Builder()
          .withDeviceIdentification("deviceId")
          .withOrganisationIdentification("organisationId")
          .withCorrelationUid("correlationId")
          .withMessageType("messageType")
          .withDomain(DOMAIN)
          .withDomainVersion(DOMAIN_VERSION)
          .withMessagePriority(4)
          .build();
  private static final String DATA_OBJECT = "data object";
  private static final Timestamp SCHEDULED_TIME =
      new Timestamp(Calendar.getInstance().getTime().getTime());

  @Mock private DeviceService deviceService;

  @Mock private DomainResponseService domainResponseMessageSender;

  @Mock private ScheduledTaskService scheduledTaskService;

  @Mock private DeviceCommunicationInformationService deviceCommunicationInformationService;

  @InjectMocks private DeviceResponseMessageService deviceResponseMessageService;

  /** test processMessage with a scheduled task that failed */
  @Test
  public void testProcessScheduledMessageFailed() {
    final ResponseMessageResultType result = ResponseMessageResultType.NOT_OK;
    final Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DATE, 1);
    final Date scheduledRetryTime = calendar.getTime();

    // since the retryCount equals the maxRetries, the message should fail
    final RetryHeader retryHeader = new RetryHeader(1, 1, scheduledRetryTime);

    final ProtocolResponseMessage message =
        new ProtocolResponseMessage.Builder()
            .messageMetadata(MESSAGE_METADATA.builder().withScheduled(true).build())
            .result(result)
            .dataObject(DATA_OBJECT)
            .retryHeader(retryHeader)
            .build();
    final ScheduledTask scheduledTask =
        new ScheduledTask(MESSAGE_METADATA, DOMAIN, DOMAIN_VERSION, DATA_OBJECT, SCHEDULED_TIME);

    when(this.scheduledTaskService.findByCorrelationUid(anyString())).thenReturn(scheduledTask);
    this.deviceResponseMessageService.processMessage(message);

    // check if message is sent and task is deleted
    verify(this.domainResponseMessageSender).send(message);
    verify(this.scheduledTaskService).deleteScheduledTask(scheduledTask);
  }

  /** test processMessage with a scheduled task that must be retried */
  @Test
  public void testProcessScheduledMessageRetry() {
    final String exceptionMessage = "message";
    this.testProcessScheduledMessageRetry(exceptionMessage, exceptionMessage);
  }

  private void testProcessScheduledMessageRetry(
      final String exceptionMessage, final String expectedMessage) {
    final ResponseMessageResultType result = ResponseMessageResultType.NOT_OK;
    final Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DATE, 1);
    final Date scheduledRetryTime = calendar.getTime();

    // since the retryCount is smaller than the maxRetries, the message
    // should be retried
    final RetryHeader retryHeader = new RetryHeader(0, 1, scheduledRetryTime);

    final OsgpException exception = new OsgpException(ComponentType.OSGP_CORE, exceptionMessage);

    final ProtocolResponseMessage message =
        new ProtocolResponseMessage.Builder()
            .messageMetadata(MESSAGE_METADATA.builder().withScheduled(true).build())
            .result(result)
            .dataObject(DATA_OBJECT)
            .retryHeader(retryHeader)
            .osgpException(exception)
            .build();
    final ScheduledTask scheduledTask =
        new ScheduledTask(MESSAGE_METADATA, DOMAIN, DOMAIN_VERSION, DATA_OBJECT, SCHEDULED_TIME);

    when(this.scheduledTaskService.findByCorrelationUid(anyString())).thenReturn(scheduledTask);
    this.deviceResponseMessageService.processMessage(message);

    // check if the message is not send and the task is not deleted
    verify(this.domainResponseMessageSender, never()).send(message);
    verify(this.scheduledTaskService, never()).deleteScheduledTask(scheduledTask);
    verify(this.scheduledTaskService).saveScheduledTask(scheduledTask);

    // check if the scheduled time is updated to the message retry time
    assertThat(scheduledTask.getScheduledTime())
        .isEqualTo(new Timestamp(scheduledRetryTime.getTime()));
    assertThat(scheduledTask.getErrorLog()).contains(expectedMessage);
  }

  /**
   * test processMessage with a scheduled task that must be retried with an error message longer
   * than 255 characters
   */
  @Test
  public void testProcessScheduledMessageRetryWithTruncatedError() {
    final String exceptionMessageWith255Characters = StringUtils.repeat('x', 255);
    final String tooLongExceptionMessage = exceptionMessageWith255Characters + "extra";
    this.testProcessScheduledMessageRetry(
        tooLongExceptionMessage, exceptionMessageWith255Characters);
  }

  /** test processMessage with a scheduled task that has been successful */
  @Test
  public void testProcessScheduledMessageSuccess() {
    final ProtocolResponseMessage message =
        new ProtocolResponseMessage.Builder()
            .messageMetadata(MESSAGE_METADATA.builder().withScheduled(true).build())
            .result(ResponseMessageResultType.OK)
            .dataObject(DATA_OBJECT)
            .build();
    final ScheduledTask scheduledTask =
        new ScheduledTask(MESSAGE_METADATA, DOMAIN, DOMAIN_VERSION, DATA_OBJECT, SCHEDULED_TIME);
    scheduledTask.setPending();
    when(this.scheduledTaskService.findByCorrelationUid(anyString())).thenReturn(scheduledTask);
    this.deviceResponseMessageService.processMessage(message);

    // check if message is send and task is deleted
    verify(this.domainResponseMessageSender).send(message);
    verify(this.scheduledTaskService).deleteScheduledTask(scheduledTask);
  }
}
