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

import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.mqtt.application.messaging.OutboundOsgpCoreResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.mqtt.application.metrics.MqttMetricsService;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.shared.infra.jms.RetryHeader;
import org.opensmartgridplatform.shared.metrics.MetricsNameService;

@ExtendWith(MockitoExtension.class)
class ProtocolResponseMessageSendingHandlerTest {

  private static final String ORGANISATION_IDENTIFICATION = "test-org";
  private static final String DEVICE_IDENTIFICATION = "test-device";

  @Mock OutboundOsgpCoreResponseMessageSender outboundOsgpCoreResponseMessageSender;
  @Mock CorrelationIdProviderService correlationIdProviderService;
  @Captor ArgumentCaptor<ResponseMessage> responseMessageCaptor;

  private final PrometheusMeterRegistry meterRegistry;
  private final MetricsNameService metricsNameService = new MetricsNameService();

  ProtocolResponseMessageSendingHandlerTest() {
    this.meterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
  }

  @Test
  void sendsOkProtocolResponseMessageWithSamePayload() {
    final String topic = "test/topic";
    final byte[] payload = "payload-as-string".getBytes(StandardCharsets.UTF_8);
    final String correlationId = "correlation-id-from-provider";
    final ProtocolResponseMessageSendingHandler protocolResponseMessageSendingHandler =
        this.aProtocolResponseMessageSendingHandler();
    when(this.correlationIdProviderService.getCorrelationId(ORGANISATION_IDENTIFICATION, topic))
        .thenReturn(correlationId);

    protocolResponseMessageSendingHandler.handlePublishedMessage(topic, payload);

    verify(this.outboundOsgpCoreResponseMessageSender).send(this.responseMessageCaptor.capture());
    final ResponseMessage actualResponseMessage = this.responseMessageCaptor.getValue();
    assertThat(actualResponseMessage.getResult()).isEqualTo(ResponseMessageResultType.OK);
    assertThat(actualResponseMessage.getDataObject()).isEqualTo(payload);
    this.assertIncrementReceivedMessage();
  }

  @Test
  void sendsProtocolResponseMessageWithExpectedMetadata() {
    final String topic = "test/topic";
    final String correlationId = "correlation-id-from-provider";
    final ProtocolResponseMessageSendingHandler protocolResponseMessageSendingHandler =
        this.aProtocolResponseMessageSendingHandler();
    when(this.correlationIdProviderService.getCorrelationId(ORGANISATION_IDENTIFICATION, topic))
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
        .isEqualTo(ORGANISATION_IDENTIFICATION);
    assertThat(actualMessageMetadata.getDeviceIdentification()).isEqualTo(DEVICE_IDENTIFICATION);
    assertThat(actualMessageMetadata.getTopic()).isEqualTo(topic);
    assertThat(actualMessageMetadata.getCorrelationUid()).isEqualTo(correlationId);
    this.assertIncrementReceivedMessage();
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
    this.assertIncrementReceivedMessage();
  }

  private ProtocolResponseMessageSendingHandler aProtocolResponseMessageSendingHandler() {
    final MqttMetricsService metricsService =
        new MqttMetricsService(this.meterRegistry, this.metricsNameService);
    return new ProtocolResponseMessageSendingHandler(
        this.outboundOsgpCoreResponseMessageSender,
        this.correlationIdProviderService,
        metricsService,
        ProtocolResponseMessageSendingHandlerTest.ORGANISATION_IDENTIFICATION,
        ProtocolResponseMessageSendingHandlerTest.DEVICE_IDENTIFICATION);
  }

  private void assertIncrementReceivedMessage() {
    assertThat(this.meterRegistry.find(MqttMetricsService.MESSAGE_COUNTER).counter().count())
        .isEqualTo(1);
  }
}
