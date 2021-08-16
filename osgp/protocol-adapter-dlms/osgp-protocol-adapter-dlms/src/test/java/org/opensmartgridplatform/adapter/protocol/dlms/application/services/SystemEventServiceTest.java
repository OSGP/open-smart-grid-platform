/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDeviceBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.requests.to.core.OsgpRequestMessageSender;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

@ExtendWith(MockitoExtension.class)
class SystemEventServiceTest {
  private SystemEventService service;

  ExecutorService systemEventExecutorService = MoreExecutors.newDirectExecutorService();
  @Mock private OsgpRequestMessageSender osgpRequestMessageSender;
  private final long invocationCounterEventThreshold = 10;

  @BeforeEach
  void setUp() {
    this.service =
        new SystemEventService(
            this.systemEventExecutorService,
            this.osgpRequestMessageSender,
            this.invocationCounterEventThreshold);
  }

  @Test
  void verifyMaxValueNotReachedEvent() {
    final DlmsDevice device =
        new DlmsDeviceBuilder().withInvocationCounter(this.invocationCounterEventThreshold).build();

    this.service.verifyMaxValueReachedEvent(device);

    verify(this.osgpRequestMessageSender, never())
        .send(
            any(RequestMessage.class),
            eq(MessageType.SYSTEM_EVENT.name()),
            any(MessageMetadata.class));
  }

  @Test
  void verifyMaxValueReachedEvent() {
    final DlmsDevice device =
        new DlmsDeviceBuilder()
            .withInvocationCounter(this.invocationCounterEventThreshold + 1)
            .build();

    this.service.verifyMaxValueReachedEvent(device);

    verify(this.osgpRequestMessageSender)
        .send(
            any(RequestMessage.class),
            eq(MessageType.SYSTEM_EVENT.name()),
            any(MessageMetadata.class));
  }
}
