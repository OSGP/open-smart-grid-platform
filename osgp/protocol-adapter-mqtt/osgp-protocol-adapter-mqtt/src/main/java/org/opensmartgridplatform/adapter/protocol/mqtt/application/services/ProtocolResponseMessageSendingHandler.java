//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.mqtt.application.services;

import org.opensmartgridplatform.adapter.protocol.mqtt.application.messaging.OutboundOsgpCoreResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.mqtt.application.metrics.MqttMetricsService;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * ProtocolResponseMessageSendingHandler is a {@link MessageHandler} implementation that treats the
 * payload of published MQTT messages as UTF-8 bytes to construct a {@link String}.<br>
 * The textual form of the payload will be sent as a protocol response message of type {@link
 * MessageType#GET_DATA GET_DATA} to the OSGP Core responses queue as if it were in reaction to a
 * request for the data of some device.
 *
 * <p>The device identification for the response message metadata will be set from a configured
 * value, so this identification could indicate an MQTT broker to which multiple field devices
 * publish content.
 *
 * <p>The message metadata has a correlation UID which is determined by an organization
 * identification (from configuration) and the topic of the published MQTT message.<br>
 * The topic is used as input to the {@link CorrelationIdProviderService} in place of where the
 * device identification would be provided in more typical request flows.
 */
@Service
public class ProtocolResponseMessageSendingHandler implements MessageHandler {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ProtocolResponseMessageSendingHandler.class);

  private final OutboundOsgpCoreResponseMessageSender outboundOsgpCoreResponseMessageSender;
  private final CorrelationIdProviderService correlationIdProviderService;
  private final MqttMetricsService metricsService;
  private final String organisationIdentification;
  private final String deviceIdentification;

  public ProtocolResponseMessageSendingHandler(
      final OutboundOsgpCoreResponseMessageSender outboundOsgpCoreResponseMessageSender,
      final CorrelationIdProviderService correlationIdProviderService,
      final MqttMetricsService metricsService,
      @Value("${mqtt.broker.organisation.identification}") final String organisationIdentification,
      @Value("${mqtt.broker.device.identification}") final String deviceIdentification) {

    this.outboundOsgpCoreResponseMessageSender = outboundOsgpCoreResponseMessageSender;
    this.correlationIdProviderService = correlationIdProviderService;
    this.metricsService = metricsService;
    this.organisationIdentification = organisationIdentification;
    this.deviceIdentification = deviceIdentification;
  }

  /**
   * Handles the {@code payload} as UTF-8 bytes for which the textual form is sent to the outbound
   * OSGP Core responses queue.
   */
  @Override
  public void handlePublishedMessage(final String topic, final byte[] payload) {
    final MessageMetadata messageMetadata =
        MessageMetadata.newBuilder()
            .withOrganisationIdentification(this.organisationIdentification)
            .withDeviceIdentification(this.deviceIdentification)
            .withTopic(topic)
            .withCorrelationUid(
                this.correlationIdProviderService.getCorrelationId(
                    this.organisationIdentification, topic))
            /*
             * Only handling in the platform for these messages at the time of introduction is as
             * GET_DATA responses for the domain DISTRIBUTION_AUTOMATION 1.0. Change hard coded
             * references to something more flexible when there are use cases.
             */
            .withMessageType(MessageType.GET_DATA.name())
            .withDomain("DISTRIBUTION_AUTOMATION")
            .withDomainVersion("1.0")
            /*
             * Retries should not be attempted or scheduled as the device may not actually be
             * present in the core database.
             */
            .withBypassRetry(true)
            .withScheduled(false)
            .build();
    LOGGER.info("Handling message published on topic {}", topic);
    // Increment counter
    this.metricsService.receivedMessage();
    final ResponseMessage responseMessage =
        ProtocolResponseMessage.newBuilder()
            .messageMetadata(messageMetadata)
            .dataObject(payload)
            .result(ResponseMessageResultType.OK)
            .build();

    this.outboundOsgpCoreResponseMessageSender.send(responseMessage);
  }
}
