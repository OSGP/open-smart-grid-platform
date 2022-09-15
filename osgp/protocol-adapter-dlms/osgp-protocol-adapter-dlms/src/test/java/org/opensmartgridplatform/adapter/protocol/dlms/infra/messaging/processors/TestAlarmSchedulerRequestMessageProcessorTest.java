/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SynchronizeTimeRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class TestAlarmSchedulerRequestMessageProcessorTest {

  @InjectMocks private TestAlarmSchedulerRequestMessageProcessor messageProcessor;

  @Mock private DlmsConnectionManager conn;

  @Mock private DlmsDevice device;

  @Mock private MessageMetadata messageMetadata;

  @Test
  void process_incorrectMessageTypeShouldFail() {

    final SynchronizeTimeRequestDto requestObject = Mockito.mock(SynchronizeTimeRequestDto.class);

    Assertions.assertThrows(
        ProtocolAdapterException.class,
        () -> {
          this.messageProcessor.handleMessage(
              this.conn, this.device, requestObject, this.messageMetadata);
        });
  }

  @Test
  void process_shouldBeASuccess() {

    final SynchronizeTimeRequestDto requestObject = Mockito.mock(SynchronizeTimeRequestDto.class);

    Assertions.assertThrows(
        ProtocolAdapterException.class,
        () -> {
          this.messageProcessor.handleMessage(
              this.conn, this.device, requestObject, this.messageMetadata);
        });
  }
}
