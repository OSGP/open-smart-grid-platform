/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_DEVICE_IDENTIFICATION;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_MESSAGE_TYPE;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.processors.GetHealthStatusRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.iec60870.testutils.matchers.ErrorResponseMessageMatcher;
import org.opensmartgridplatform.dto.da.GetHealthStatusRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.ObjectMessageBuilder;

@ExtendWith(MockitoExtension.class)
public class DeviceRequestMessageListenerTest {

    @InjectMocks
    private DeviceRequestMessageListener deviceRequestMessageListener;

    @Mock
    private MessageProcessorMap iec60870RequestMessageProcessorMap;

    @Mock
    private DeviceResponseMessageSender deviceResponseMessageSender;

    @Test
    public void shouldProcessMessageWhenMessageTypeIsSupported() throws JMSException {
        // Arrange
        final ObjectMessage message = new ObjectMessageBuilder().withDeviceIdentification(DEFAULT_DEVICE_IDENTIFICATION)
                .withMessageType(DEFAULT_MESSAGE_TYPE)
                .withObject(new GetHealthStatusRequestDto())
                .build();

        final MessageProcessor messageProcessor = mock(GetHealthStatusRequestMessageProcessor.class);
        when(this.iec60870RequestMessageProcessorMap.getMessageProcessor(message)).thenReturn(messageProcessor);

        // Act
        this.deviceRequestMessageListener.onMessage(message);

        // Assert
        verify(messageProcessor).processMessage(message);
    }

    @Test
    public void shouldSendErrorMessageWhenMessageTypeIsNotSupported() throws JMSException {
        // Arrange
        final ObjectMessage message = new ObjectMessageBuilder().withDeviceIdentification(DEFAULT_DEVICE_IDENTIFICATION)
                .withMessageType(DEFAULT_MESSAGE_TYPE)
                .withObject(new GetHealthStatusRequestDto())
                .build();

        when(this.iec60870RequestMessageProcessorMap.getMessageProcessor(message)).thenThrow(JMSException.class);

        // Act
        this.deviceRequestMessageListener.onMessage(message);

        // Assert
        verify(this.deviceResponseMessageSender).send(argThat(new ErrorResponseMessageMatcher()));
    }
}
