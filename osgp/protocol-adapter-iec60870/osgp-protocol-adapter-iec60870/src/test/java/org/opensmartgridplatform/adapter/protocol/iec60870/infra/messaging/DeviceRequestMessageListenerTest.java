// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_CORRELATION_UID;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_DEVICE_IDENTIFICATION;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_MESSAGE_TYPE;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.PendingRequestsQueue;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.processors.GetHealthStatusRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.iec60870.testutils.matchers.ErrorResponseMessageMatcher;
import org.opensmartgridplatform.dto.da.GetHealthStatusRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.ObjectMessageBuilder;

@ExtendWith(MockitoExtension.class)
class DeviceRequestMessageListenerTest {

  @InjectMocks private DeviceRequestMessageListener deviceRequestMessageListener;

  @Mock private MessageProcessorMap iec60870RequestMessageProcessorMap;

  @Mock private DeviceResponseMessageSender deviceResponseMessageSender;

  @Mock private PendingRequestsQueue pendingRequestsQueue;

  @Test
  void shouldProcessMessageWhenMessageTypeIsSupported() throws JMSException {

    // Arrange
    final String correlationUid = DEFAULT_CORRELATION_UID;
    final ObjectMessage message =
        new ObjectMessageBuilder()
            .withCorrelationUid(correlationUid)
            .withDeviceIdentification(DEFAULT_DEVICE_IDENTIFICATION)
            .withMessageType(DEFAULT_MESSAGE_TYPE)
            .withObject(new GetHealthStatusRequestDto())
            .build();

    final MessageProcessor messageProcessor = mock(GetHealthStatusRequestMessageProcessor.class);
    when(this.iec60870RequestMessageProcessorMap.getMessageProcessor(message))
        .thenReturn(messageProcessor);

    // Act
    this.deviceRequestMessageListener.onMessage(message);

    // Assert
    verify(this.pendingRequestsQueue).enqueue(DEFAULT_DEVICE_IDENTIFICATION, correlationUid);
    verify(messageProcessor).processMessage(message);
  }

  @Test
  void shouldSendErrorMessageWhenMessageTypeIsNotSupported() throws JMSException {

    // Arrange
    final String correlationUid = DEFAULT_CORRELATION_UID;
    final ObjectMessage message =
        new ObjectMessageBuilder()
            .withCorrelationUid(correlationUid)
            .withDeviceIdentification(DEFAULT_DEVICE_IDENTIFICATION)
            .withMessageType(DEFAULT_MESSAGE_TYPE)
            .withObject(new GetHealthStatusRequestDto())
            .build();

    when(this.iec60870RequestMessageProcessorMap.getMessageProcessor(message))
        .thenThrow(JMSException.class);

    // Act
    this.deviceRequestMessageListener.onMessage(message);

    // Assert
    verify(this.pendingRequestsQueue).enqueue(DEFAULT_DEVICE_IDENTIFICATION, correlationUid);
    verify(this.pendingRequestsQueue).remove(DEFAULT_DEVICE_IDENTIFICATION, correlationUid);
    verify(this.deviceResponseMessageSender).send(argThat(new ErrorResponseMessageMatcher()));
  }
}
