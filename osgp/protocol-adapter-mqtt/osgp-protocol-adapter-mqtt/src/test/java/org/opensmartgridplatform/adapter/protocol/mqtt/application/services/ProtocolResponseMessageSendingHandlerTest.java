/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.mqtt.application.messaging.OutboundOsgpCoreResponseMessageSender;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.shared.infra.jms.RetryHeader;

@ExtendWith(MockitoExtension.class)
class ProtocolResponseMessageSendingHandlerTest {

  private static final String ORGANISATION_IDENTIFICATION = "test-org";
  private static final String DEVICE_IDENTIFICATION = "test-device";

  @Mock OutboundOsgpCoreResponseMessageSender outboundOsgpCoreResponseMessageSender;
  @Mock CorrelationIdProviderService correlationIdProviderService;
  @Captor ArgumentCaptor<ResponseMessage> responseMessageCaptor;

  @Test
  void sendsOkProtocolResponseMessageWithPayloadAsStringDataObject() {
    final String organisationIdentification = ORGANISATION_IDENTIFICATION;
    final String deviceIdentification = DEVICE_IDENTIFICATION;
    final String topic = "test/topic";
    final String expectedDataObject = "payload-as-string";
    final byte[] payload = expectedDataObject.getBytes(StandardCharsets.UTF_8);
    final String correlationId = "correlation-id-from-provider";
    final ProtocolResponseMessageSendingHandler protocolResponseMessageSendingHandler =
        this.aProtocolResponseMessageSendingHandler(
            organisationIdentification, deviceIdentification);
    when(this.correlationIdProviderService.getCorrelationId(organisationIdentification, topic))
        .thenReturn(correlationId);

    protocolResponseMessageSendingHandler.handlePublishedMessage(topic, payload);

    verify(this.outboundOsgpCoreResponseMessageSender).send(this.responseMessageCaptor.capture());
    final ResponseMessage actualResponseMessage = this.responseMessageCaptor.getValue();
    assertThat(actualResponseMessage.getResult()).isEqualTo(ResponseMessageResultType.OK);
    assertThat(actualResponseMessage.getDataObject()).isEqualTo(expectedDataObject);
  }

  @Test
  void sendsProtocolResponseMessageWithExpectedMetadata() {
    final String organisationIdentification = ORGANISATION_IDENTIFICATION;
    final String deviceIdentification = DEVICE_IDENTIFICATION;
    final String topic = "test/topic";
    final String correlationId = "correlation-id-from-provider";
    final ProtocolResponseMessageSendingHandler protocolResponseMessageSendingHandler =
        this.aProtocolResponseMessageSendingHandler(
            organisationIdentification, deviceIdentification);
    when(this.correlationIdProviderService.getCorrelationId(organisationIdentification, topic))
        .thenReturn(correlationId);

    protocolResponseMessageSendingHandler.handlePublishedMessage(
        topic, "some-payload".getBytes(StandardCharsets.UTF_8));

    verify(this.outboundOsgpCoreResponseMessageSender).send(this.responseMessageCaptor.capture());
    final ResponseMessage actualResponseMessage = this.responseMessageCaptor.getValue();
    final MessageMetadata actualMessageMetadata = actualResponseMessage.messageMetadata();
    assertThat(actualMessageMetadata.getDomain()).isEqualTo("DISTRIBUTION_AUTOMATION");
    assertThat(actualMessageMetadata.getDomainVersion()).isEqualTo("1.0");
    assertThat(actualMessageMetadata.getMessageType()).isEqualTo(MessageType.GET_DATA.name());
    assertThat(actualMessageMetadata.getOrganisationIdentification())
        .isEqualTo(organisationIdentification);
    assertThat(actualMessageMetadata.getDeviceIdentification()).isEqualTo(deviceIdentification);
    assertThat(actualMessageMetadata.getCorrelationUid()).isEqualTo(correlationId);
  }

  @Test
  void sendsProtocolResponseMessageThatAvoidsRetries() {
    final ProtocolResponseMessageSendingHandler protocolResponseMessageSendingHandler =
        this.aProtocolResponseMessageSendingHandler();

    protocolResponseMessageSendingHandler.handlePublishedMessage(
        "any/topic", "any-payload".getBytes(StandardCharsets.UTF_8));

    verify(this.outboundOsgpCoreResponseMessageSender).send(this.responseMessageCaptor.capture());
    final ResponseMessage actualResponseMessage = this.responseMessageCaptor.getValue();

    final MessageMetadata actualMessageMetadata = actualResponseMessage.messageMetadata();
    assertThat(actualMessageMetadata.isScheduled()).isFalse();
    assertThat(actualMessageMetadata.isBypassRetry()).isTrue();

    final RetryHeader retryHeader = actualResponseMessage.getRetryHeader();
    if (retryHeader != null) {
      assertThat(retryHeader.getScheduledRetryTime()).isNull();
      assertThat(retryHeader.getMaxRetries()).isZero();
    }
  }

  private ProtocolResponseMessageSendingHandler aProtocolResponseMessageSendingHandler() {
    return this.aProtocolResponseMessageSendingHandler(
        ORGANISATION_IDENTIFICATION, DEVICE_IDENTIFICATION);
  }

  private ProtocolResponseMessageSendingHandler aProtocolResponseMessageSendingHandler(
      final String organisationIdentification, final String deviceIdentification) {

    return new ProtocolResponseMessageSendingHandler(
        this.outboundOsgpCoreResponseMessageSender,
        this.correlationIdProviderService,
        organisationIdentification,
        deviceIdentification);
  }
}
