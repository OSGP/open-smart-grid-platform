// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
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
    final MessageMetadata metadata =
        new MessageMetadata.Builder()
            .withDeviceIdentification("TEST-DEVICE-1")
            .withCorrelationUid("TEST-CORR-1")
            .withOrganisationIdentification("TEST-ORG-1")
            .withMessageType("GET_MEASUREMENT_REPORT")
            .withDomain("DistributionAutomation")
            .withDomainVersion("1.0")
            .build();

    return new ProtocolResponseMessage.Builder()
        .messageMetadata(metadata)
        .result(ResponseMessageResultType.OK)
        .build();
  }
}
