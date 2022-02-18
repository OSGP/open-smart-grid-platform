/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.GetResultImpl;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDeviceBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.requests.to.core.OsgpRequestMessageSender;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SystemEventDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SystemEventTypeDto;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata.Builder;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;

@ExtendWith(MockitoExtension.class)
class SystemEventServiceTest {
  private SystemEventService service;
  private DlmsDevice device;
  private MessageMetadata messageMetadata;

  @Mock private OsgpRequestMessageSender osgpRequestMessageSender;
  @Mock private CorrelationIdProviderService correlationIdProviderService;

  private final long invocationCounterEventThreshold = 10;

  @BeforeEach
  void setUp() {
    this.service =
        new SystemEventService(
            this.osgpRequestMessageSender,
            this.correlationIdProviderService,
            this.invocationCounterEventThreshold);
    this.device = this.getDevice();
    this.messageMetadata = this.getMessageMetaData();
  }

  @Test
  void verifyMaxValueNotReachedEvent() {
    final DlmsDevice device =
        new DlmsDeviceBuilder()
            .withInvocationCounter(this.invocationCounterEventThreshold - 1)
            .build();

    final MessageMetadata messageMetadata =
        new Builder().withOrganisationIdentification("org-id").build();

    this.service.verifySystemEventThresholdReachedEvent(device, messageMetadata);

    verifyNoInteractions(this.correlationIdProviderService);
    verify(this.osgpRequestMessageSender, never())
        .send(
            any(RequestMessage.class),
            eq(MessageType.SYSTEM_EVENT.name()),
            any(MessageMetadata.class));
  }

  @Test
  void verifyInvocationCounterIsNull() {
    final DlmsDevice device = new DlmsDeviceBuilder().withInvocationCounter(null).build();

    final MessageMetadata messageMetadata =
        new Builder().withOrganisationIdentification("org-id").build();

    this.service.verifySystemEventThresholdReachedEvent(device, messageMetadata);

    verifyNoInteractions(this.correlationIdProviderService);
    verify(this.osgpRequestMessageSender, never())
        .send(
            any(RequestMessage.class),
            eq(MessageType.SYSTEM_EVENT.name()),
            any(MessageMetadata.class));
  }

  @Test
  void verifyMaxValueReachedEvent() {
    when(this.correlationIdProviderService.getCorrelationId(
            this.messageMetadata.getOrganisationIdentification(),
            this.device.getDeviceIdentification()))
        .thenReturn("corr-id");

    this.service.verifySystemEventThresholdReachedEvent(this.device, this.messageMetadata);

    final ArgumentCaptor<MessageMetadata> messageMetadataCaptor =
        ArgumentCaptor.forClass(MessageMetadata.class);
    final ArgumentCaptor<RequestMessage> requestMessageCaptor =
        ArgumentCaptor.forClass(RequestMessage.class);

    verify(this.osgpRequestMessageSender)
        .send(
            requestMessageCaptor.capture(),
            eq(MessageType.SYSTEM_EVENT.name()),
            messageMetadataCaptor.capture());

    final RequestMessage requestMessage = requestMessageCaptor.getValue();
    assertThat(requestMessage.getDeviceIdentification())
        .isEqualTo(this.device.getDeviceIdentification());
    assertThat(requestMessage.getCorrelationUid()).isEqualTo("corr-id");
    assertThat(requestMessage.getOrganisationIdentification())
        .isEqualTo(this.messageMetadata.getOrganisationIdentification());
    assertThat(requestMessage.getIpAddress()).isEqualTo(this.messageMetadata.getIpAddress());
    assertThat(requestMessage.getRequest()).isInstanceOf(SystemEventDto.class);

    final SystemEventDto systemEventDto = (SystemEventDto) requestMessage.getRequest();
    assertThat(systemEventDto.getDeviceIdentification())
        .isEqualTo(this.device.getDeviceIdentification());
    assertThat(systemEventDto.getSystemEventType())
        .isEqualTo(SystemEventTypeDto.INVOCATION_COUNTER_THRESHOLD_REACHED);
    assertThat(systemEventDto.getTimestamp()).isNotNull();

    final MessageMetadata metadata = messageMetadataCaptor.getValue();
    assertThat(metadata.getDeviceIdentification()).isEqualTo(this.device.getDeviceIdentification());
    assertThat(metadata.getCorrelationUid()).isEqualTo("corr-id");
    assertThat(metadata.getOrganisationIdentification())
        .isEqualTo(this.messageMetadata.getOrganisationIdentification());
    assertThat(metadata.getIpAddress()).isEqualTo(this.messageMetadata.getIpAddress());
    assertThat(metadata.getMessagePriority()).isEqualTo(MessagePriorityEnum.HIGH.getPriority());
    assertThat(metadata.getMessageType()).isEqualTo(MessageType.SYSTEM_EVENT.name());
    assertThat(metadata.getDomain()).isEqualTo(this.messageMetadata.getDomain());
    assertThat(metadata.getDomainVersion()).isEqualTo(this.messageMetadata.getDomainVersion());
  }

  @Test
  void verifyLowerInvocationCounterReceived() throws IOException {
    final DlmsConnection dlmsConnection = mock(DlmsConnection.class);
    final DlmsConnectionManager connectionManager = mock(DlmsConnectionManager.class);
    final long receivedLowerThanActualInvocationCounter = 9;

    when(this.correlationIdProviderService.getCorrelationId(
            this.messageMetadata.getOrganisationIdentification(),
            this.device.getDeviceIdentification()))
        .thenReturn("corr-id");
    when(connectionManager.getConnection()).thenReturn(dlmsConnection);

    when(dlmsConnection.get(any(AttributeAddress.class)))
        .thenReturn(
            new GetResultImpl(
                DataObject.newUInteger32Data(receivedLowerThanActualInvocationCounter),
                AccessResultCode.SUCCESS));

    final boolean invocationCounterIsLowered =
        this.service.receivedInvocationCounterIsLowerThanCurrentValue(
            this.device, this.messageMetadata, connectionManager);

    final ArgumentCaptor<MessageMetadata> messageMetadataCaptor =
        ArgumentCaptor.forClass(MessageMetadata.class);
    final ArgumentCaptor<RequestMessage> requestMessageCaptor =
        ArgumentCaptor.forClass(RequestMessage.class);

    verify(this.osgpRequestMessageSender)
        .send(
            requestMessageCaptor.capture(),
            eq(MessageType.SYSTEM_EVENT.name()),
            messageMetadataCaptor.capture());

    assertThat(invocationCounterIsLowered).isTrue();

    final RequestMessage requestMessage = requestMessageCaptor.getValue();
    assertThat(requestMessage.getDeviceIdentification())
        .isEqualTo(this.device.getDeviceIdentification());
    assertThat(requestMessage.getCorrelationUid()).isEqualTo("corr-id");
    assertThat(requestMessage.getOrganisationIdentification())
        .isEqualTo(this.messageMetadata.getOrganisationIdentification());
    assertThat(requestMessage.getIpAddress()).isEqualTo(this.messageMetadata.getIpAddress());
    assertThat(requestMessage.getRequest()).isInstanceOf(SystemEventDto.class);

    final SystemEventDto systemEventDto = (SystemEventDto) requestMessage.getRequest();
    assertThat(systemEventDto.getDeviceIdentification())
        .isEqualTo(this.device.getDeviceIdentification());
    assertThat(systemEventDto.getSystemEventType())
        .isEqualTo(SystemEventTypeDto.INVOCATION_COUNTER_LOWERED);
    assertThat(systemEventDto.getTimestamp()).isNotNull();

    final MessageMetadata metadata = messageMetadataCaptor.getValue();
    assertThat(metadata.getDeviceIdentification()).isEqualTo(this.device.getDeviceIdentification());
    assertThat(metadata.getCorrelationUid()).isEqualTo("corr-id");
    assertThat(metadata.getOrganisationIdentification())
        .isEqualTo(this.messageMetadata.getOrganisationIdentification());
    assertThat(metadata.getIpAddress()).isEqualTo(this.messageMetadata.getIpAddress());
    assertThat(metadata.getMessagePriority()).isEqualTo(MessagePriorityEnum.HIGH.getPriority());
    assertThat(metadata.getMessageType()).isEqualTo(MessageType.SYSTEM_EVENT.name());
    assertThat(metadata.getDomain()).isEqualTo(this.messageMetadata.getDomain());
    assertThat(metadata.getDomainVersion()).isEqualTo(this.messageMetadata.getDomainVersion());
  }

  @Test
  void verifyHigherInvocationCounterReceived() throws IOException {
    final DlmsConnection dlmsConnection = mock(DlmsConnection.class);
    final DlmsConnectionManager connectionManager = mock(DlmsConnectionManager.class);
    final long receivedHigherThanActualInvocationCounter = 100;

    when(connectionManager.getConnection()).thenReturn(dlmsConnection);

    when(dlmsConnection.get(any(AttributeAddress.class)))
        .thenReturn(
            new GetResultImpl(
                DataObject.newUInteger32Data(receivedHigherThanActualInvocationCounter),
                AccessResultCode.SUCCESS));

    final boolean invocationCounterIsLowered =
        this.service.receivedInvocationCounterIsLowerThanCurrentValue(
            this.device, this.messageMetadata, connectionManager);

    assertThat(invocationCounterIsLowered).isFalse();

    verifyNoInteractions(this.correlationIdProviderService);
    verify(this.osgpRequestMessageSender, never())
        .send(
            any(RequestMessage.class),
            eq(MessageType.SYSTEM_EVENT.name()),
            any(MessageMetadata.class));
  }

  DlmsDevice getDevice() {
    return new DlmsDeviceBuilder()
        .withDeviceIdentification("device-1")
        .withInvocationCounter(this.invocationCounterEventThreshold)
        .build();
  }

  MessageMetadata getMessageMetaData() {
    return new Builder()
        .withIpAddress("127.0-.0.1")
        .withOrganisationIdentification("org-id")
        .withDomain("domain")
        .withDomainVersion("1.0")
        .build();
  }
}
