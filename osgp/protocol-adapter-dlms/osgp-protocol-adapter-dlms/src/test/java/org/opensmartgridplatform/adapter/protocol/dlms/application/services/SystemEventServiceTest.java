//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDeviceBuilder;
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
    final DlmsDevice device =
        new DlmsDeviceBuilder()
            .withDeviceIdentification("device-1")
            .withInvocationCounter(this.invocationCounterEventThreshold)
            .build();

    final MessageMetadata messageMetadata =
        new Builder()
            .withIpAddress("127.0-.0.1")
            .withOrganisationIdentification("org-id")
            .withDomain("domain")
            .withDomainVersion("1.0")
            .build();

    when(this.correlationIdProviderService.getCorrelationId(
            messageMetadata.getOrganisationIdentification(), device.getDeviceIdentification()))
        .thenReturn("corr-id");

    this.service.verifySystemEventThresholdReachedEvent(device, messageMetadata);

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
        .isEqualTo(device.getDeviceIdentification());
    assertThat(requestMessage.getCorrelationUid()).isEqualTo("corr-id");
    assertThat(requestMessage.getOrganisationIdentification())
        .isEqualTo(messageMetadata.getOrganisationIdentification());
    assertThat(requestMessage.getIpAddress()).isEqualTo(messageMetadata.getIpAddress());
    assertThat(requestMessage.getRequest()).isInstanceOf(SystemEventDto.class);

    final SystemEventDto systemEventDto = (SystemEventDto) requestMessage.getRequest();
    assertThat(systemEventDto.getDeviceIdentification())
        .isEqualTo(device.getDeviceIdentification());
    assertThat(systemEventDto.getSystemEventType())
        .isEqualTo(SystemEventTypeDto.INVOCATION_COUNTER_THRESHOLD_REACHED);
    assertThat(systemEventDto.getTimestamp()).isNotNull();

    final MessageMetadata metadata = messageMetadataCaptor.getValue();
    assertThat(metadata.getDeviceIdentification()).isEqualTo(device.getDeviceIdentification());
    assertThat(metadata.getCorrelationUid()).isEqualTo("corr-id");
    assertThat(metadata.getOrganisationIdentification())
        .isEqualTo(messageMetadata.getOrganisationIdentification());
    assertThat(metadata.getIpAddress()).isEqualTo(messageMetadata.getIpAddress());
    assertThat(metadata.getMessagePriority()).isEqualTo(MessagePriorityEnum.HIGH.getPriority());
    assertThat(metadata.getMessageType()).isEqualTo(MessageType.SYSTEM_EVENT.name());
    assertThat(metadata.getDomain()).isEqualTo(messageMetadata.getDomain());
    assertThat(metadata.getDomainVersion()).isEqualTo(messageMetadata.getDomainVersion());
  }
}
