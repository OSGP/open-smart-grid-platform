/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionService;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.springframework.jms.IllegalStateException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class DeviceResponseMessageSenderTest {

  @InjectMocks private DeviceResponseMessageSender messageSender;

  @Mock private JmsTemplate jmsTemplate;

  @Mock private ClientConnectionService clientConnectionService;

  @BeforeEach
  public void setup() {
    this.injectCloseConnectionsOnBrokerFailure(true);
  }

  @Test
  public void shouldSendResponse() {
    // Arrange
    final ProtocolResponseMessage responseMessage = this.createDefaultResponseMessage();

    // Act
    this.messageSender.send(responseMessage);

    // Assert
    verify(this.jmsTemplate).send(any(MessageCreator.class));
  }

  @Test
  public void shouldCloseAllConnectionsOnBrokerFailure() {
    // Arrange
    final ProtocolResponseMessage responseMessage = this.createDefaultResponseMessage();
    doThrow(IllegalStateException.class).when(this.jmsTemplate).send(any(MessageCreator.class));

    // Act
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(
            () -> {
              this.messageSender.send(responseMessage);
            });
    // Assert
    verify(this.clientConnectionService).closeAllConnections();
  }

  private void injectCloseConnectionsOnBrokerFailure(final boolean value) {
    final String name = "isCloseConnectionsOnBrokerFailure";
    ReflectionTestUtils.setField(this.messageSender, name, value);
  }

  private ProtocolResponseMessage createDefaultResponseMessage() {
    final DeviceMessageMetadata metadata =
        DeviceMessageMetadata.newBuilder()
            .withDeviceIdentification("TEST-DEVICE-1")
            .withCorrelationUid("TEST-CORR-1")
            .withOrganisationIdentification("TEST-ORG-1")
            .withMessageType("GET_MEASUREMENT_REPORT")
            .build();

    return new ProtocolResponseMessage.Builder()
        .deviceMessageMetadata(metadata)
        .domain("DistributionAutomation")
        .domainVersion("1.0")
        .result(ResponseMessageResultType.OK)
        .build();
  }
}
