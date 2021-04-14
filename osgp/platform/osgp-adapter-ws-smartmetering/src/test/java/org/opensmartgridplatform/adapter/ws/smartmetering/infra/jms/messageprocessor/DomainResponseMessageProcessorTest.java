/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.messageprocessor;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationService;
import org.opensmartgridplatform.adapter.ws.shared.services.ResponseDataService;

@ExtendWith(MockitoExtension.class)
public class DomainResponseMessageProcessorTest {

  @Mock private NotificationService notificationService;

  @Mock private ResponseDataService responseDataService;

  @Mock private ObjectMessage message;

  @InjectMocks private DomainResponseMessageProcessor messageProcessor;

  @BeforeEach
  void init() throws JMSException {
    when(this.message.getJMSType()).thenReturn("HANDLE_BUNDLED_ACTIONS");
    when(this.message.getStringProperty("Result")).thenReturn("OK");
    when(this.message.getStringProperty("OrganisationIdentification"))
        .thenReturn("some-organisation");
    when(this.message.getJMSCorrelationID()).thenReturn(null);
    when(this.message.getStringProperty("DeviceIdentification")).thenReturn(null);
    when(this.message.getStringProperty("Description")).thenReturn(null);
    when(this.message.getObject()).thenReturn(null);
  }

  @Test
  void processMessageSuccessfully() throws JMSException {
    this.messageProcessor.processMessage(this.message);

    verify(this.responseDataService).enqueue(any());
    verify(this.notificationService).sendNotification(any(), any());
  }

  @Test
  void processMessageErrorInHandleMessage() throws JMSException {

    doThrow(new NullPointerException("Some runtime exception"))
        .when(this.responseDataService)
        .enqueue(any());

    assertThatThrownBy(() -> this.messageProcessor.processMessage(this.message))
        .isInstanceOf(RuntimeException.class);
    verifyNoInteractions(this.notificationService);
  }

  @Test
  void processMessageErrorInSendNotification() throws JMSException {

    doThrow(new NullPointerException("Some runtime exception while sending the notification"))
        .when(this.notificationService)
        .sendNotification(any(), any(), any(), any(), any(), any());

    this.messageProcessor.processMessage(this.message);

    verify(this.responseDataService).enqueue(any());
  }
}
