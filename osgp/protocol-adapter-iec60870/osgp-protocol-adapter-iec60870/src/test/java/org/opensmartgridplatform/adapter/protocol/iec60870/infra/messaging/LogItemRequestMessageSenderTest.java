/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.LogItem;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

@ExtendWith(MockitoExtension.class)
public class LogItemRequestMessageSenderTest {

  @Mock private JmsTemplate logItemRequestsJmsTemplate;

  @InjectMocks private LogItemRequestMessageSender messageSender;

  @Test
  public void shouldSendLogItem() {
    // Arrange
    final LogItem logItem = new LogItem("TEST-DEVICE-1", "TEST-ORG-1", true, "TEST-MESSAGE");

    // Act
    this.messageSender.send(logItem);

    // Assert
    verify(this.logItemRequestsJmsTemplate).send(any(MessageCreator.class));
  }
}
