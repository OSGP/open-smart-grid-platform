/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDeviceBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class InvocationCounterManagerTest {
  private static final AttributeAddress ATTRIBUTE_ADDRESS_INVOCATION_COUNTER_VALUE =
      new AttributeAddress(1, new ObisCode(new byte[] {0, 0, 43, 1, 0, -1}), 2);

  private InvocationCounterManager manager;
  private MessageMetadata messageMetadata;
  private DlmsDevice device;
  private final long invocationCounterValueInDatabaseEntity = 7;
  private final long initialDeviceVersion = 2;

  @Mock private DlmsConnectionFactory connectionFactory;

  @Mock private DlmsHelper dlmsHelper;
  @Mock private DlmsDeviceRepository deviceRepository;

  @BeforeEach
  void setUp() {
    this.manager =
        new InvocationCounterManager(
            this.connectionFactory, this.dlmsHelper, this.deviceRepository);
    this.messageMetadata = MessageMetadata.newBuilder().withCorrelationUid("123456").build();
    this.device =
        new DlmsDeviceBuilder()
            .withInvocationCounter(this.invocationCounterValueInDatabaseEntity)
            .withVersion(this.initialDeviceVersion)
            .build();
  }

  @Test
  void initializeInvocationCounterForDeviceTaskExecuted() throws OsgpException {
    this.manager.initializeInvocationCounter(this.messageMetadata, this.device);

    verify(this.connectionFactory, times(1))
        .createAndHandlePublicClientConnection(
            any(MessageMetadata.class), eq(this.device), isNull(), isNull(), any());
  }

  @Test
  void initializesInvocationCounterForDevice() throws Exception {
    final long invocationCounterValueOnDevice = 123;
    final DlmsConnectionManager connectionManager = mock(DlmsConnectionManager.class);
    final DataObject dataObject = DataObject.newUInteger32Data(invocationCounterValueOnDevice);
    when(this.dlmsHelper.getAttributeValue(
            eq(connectionManager), refEq(ATTRIBUTE_ADDRESS_INVOCATION_COUNTER_VALUE)))
        .thenReturn(dataObject);
    when(this.deviceRepository.save(this.device))
        .thenReturn(
            new DlmsDeviceBuilder()
                .withDeviceIdentification(this.device.getDeviceIdentification())
                .withInvocationCounter(this.device.getInvocationCounter())
                .withVersion(this.device.getVersion() + 1)
                .build());

    this.manager.initializeWithInvocationCounterStoredOnDeviceTask(this.device, connectionManager);

    verify(this.deviceRepository).save(this.device);
    assertThat(this.device.getVersion()).isGreaterThan(this.initialDeviceVersion);
    assertThat(this.device.getInvocationCounter()).isEqualTo(invocationCounterValueOnDevice);
  }

  @Test
  void attemptToLowerInvocationCounterForDeviceLogsErrorAndThrowsException() throws Exception {
    final Logger invocationManagerLogger =
        (Logger) LoggerFactory.getLogger(InvocationCounterManager.class);
    final ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    final List<ILoggingEvent> logsList = listAppender.list;
    final long invocationCounterValueOnDevice = 1;
    final DlmsConnectionManager connectionManager = mock(DlmsConnectionManager.class);
    final DataObject dataObject = DataObject.newUInteger32Data(invocationCounterValueOnDevice);

    when(this.dlmsHelper.getAttributeValue(
            eq(connectionManager), refEq(ATTRIBUTE_ADDRESS_INVOCATION_COUNTER_VALUE)))
        .thenReturn(dataObject);

    listAppender.start();
    invocationManagerLogger.addAppender(listAppender);

    final FunctionalException thrownException =
        catchThrowableOfType(
            () ->
                this.manager.initializeWithInvocationCounterStoredOnDeviceTask(
                    this.device, connectionManager),
            FunctionalException.class);

    assertTrue(logsList.size() > 0);
    assertEquals(
        "[ERROR] Attempt to lower invocationCounter of device device-1",
        logsList.get(0).toString());

    assertThat(thrownException).isNotNull();
    assertThat(thrownException.getExceptionType())
        .isEqualTo(FunctionalExceptionType.ATTEMPT_TO_LOWER_INVOCATION_COUNTER);
  }
}
