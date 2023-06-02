//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.da.infra.jms.messageprocessors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.notification.NotificationType;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationService;
import org.opensmartgridplatform.adapter.ws.shared.services.ResponseDataService;
import org.opensmartgridplatform.shared.infra.jms.Constants;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DomainResponseMessageProcessorTest {

  @Mock private NotificationService notificationService;

  @Mock private ResponseDataService responseDataService;

  @InjectMocks
  private final DomainResponseMessageProcessor responseMessageProcessor =
      new DomainResponseMessageProcessor();

  @Test
  public void testProcessGetHealthStatusResponseNotOkMessage() throws JMSException {
    this.testProcessResponse("NOT_OK", "GET_HEALTH_STATUS");
  }

  @Test
  public void testProcessGetHealthStatusResponseOkMessage() throws JMSException {
    this.testProcessResponse("OK", "GET_HEALTH_STATUS");
  }

  @Test
  public void testProcessGetMeasurementReportResponseNotOkMessage() throws JMSException {
    this.testProcessResponse("NOT_OK", "GET_MEASUREMENT_REPORT");
  }

  @Test
  public void testProcessGetMeasurementReportResponseOkMessage() throws JMSException {
    this.testProcessResponse("OK", "GET_MEASUREMENT_REPORT");
  }

  @Test
  public void testProcessGetPowerQualityResponseNotOkMessage() throws JMSException {
    this.testProcessResponse("NOT_OK", "GET_POWER_QUALITY_VALUES");
  }

  @Test
  public void testProcessGetPowerQualityResponseOkMessage() throws JMSException {
    this.testProcessResponse("OK", "GET_POWER_QUALITY_VALUES");
  }

  private void testProcessResponse(final String result, final String notificationName)
      throws JMSException {
    // Arrange
    final ObjectMessage myMessage = Mockito.mock(ObjectMessage.class);

    when(myMessage.getJMSType()).thenReturn(notificationName);
    when(myMessage.getStringProperty(Constants.RESULT)).thenReturn(result);

    // Act
    this.responseMessageProcessor.processMessage(myMessage);

    // Assert
    // Verify a notification was sent
    verify(this.notificationService)
        .sendNotification(
            nullable(String.class),
            nullable(String.class),
            eq(result),
            nullable(String.class),
            nullable(String.class),
            eq(NotificationType.valueOf(notificationName)));

    // Verify a response was enqueued for storage
    verify(this.responseDataService).enqueue(any());
  }

  /**
   * Tests processing an incoming message, which has an unknown notification type. The system should
   * discard the message.
   *
   * @throws JMSException, which should never occur.
   */
  @Test
  public void testProcessUnknownMessageTypeResponseMessage() throws JMSException {
    // Arrange
    final ObjectMessage myMessage = Mockito.mock(ObjectMessage.class);

    when(myMessage.getJMSType()).thenReturn("FAKE_UNKNOWN_NOTIFICATION_TYPE");

    // Act
    this.responseMessageProcessor.processMessage(myMessage);

    // Assert
    // Verify no notification was sent
    verify(this.notificationService, times(0))
        .sendNotification(anyString(), anyString(), eq("OK"), anyString(), anyString(), any());

    // Verify no response was enqueued for storage
    verify(this.responseDataService, times(0)).enqueue(any());
  }
}
