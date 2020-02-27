/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.application.services;

import java.util.Arrays;
import java.util.UUID;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import org.opensmartgridplatform.adapter.protocol.mqtt.application.messaging.OutboundOsgpCoreRequestMessageSender;
import org.opensmartgridplatform.adapter.protocol.mqtt.application.messaging.OutboundOsgpCoreResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.entities.MqttDevice;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.repositories.MqttDeviceRepository;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.shared.infra.jms.RetryHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service(value = "mqttSubcriptionService")
public class SubcriptionService {

    private static final Logger LOG = LoggerFactory.getLogger(SubcriptionService.class);

    private final OutboundOsgpCoreRequestMessageSender outboundOsgpCoreRequestMessageSender;
    private final OutboundOsgpCoreResponseMessageSender outboundOsgpCoreResponseMessageSender;
    private final MqttDeviceRepository mqttDeviceRepository;

    @Value("mqtt.broker.defaultPort")
    private int defaultPort;
    @Value("mqtt.broker.defaultTopics")
    private String defaultTopics;
    @Value("mqtt.broker.defaultQos")
    private String defaultQos;

    public SubcriptionService(final OutboundOsgpCoreRequestMessageSender outboundOsgpCoreRequestMessageSender,
            final OutboundOsgpCoreResponseMessageSender outboundOsgpCoreResponseMessageSender,
            final MqttDeviceRepository mqttDeviceRepository) {
        this.outboundOsgpCoreRequestMessageSender = outboundOsgpCoreRequestMessageSender;
        this.outboundOsgpCoreResponseMessageSender = outboundOsgpCoreResponseMessageSender;
        this.mqttDeviceRepository = mqttDeviceRepository;
    }

    public void subscribe(final MessageMetadata messageMetadata) {
        final MqttDevice device = this.getOrCreateDevice(messageMetadata);
        final Mqtt3AsyncClient client = this.createClient(device);
        client.connectWith()
                .send()
                .whenComplete((ack, throwable) -> this.onConnect(device, messageMetadata, client, ack, throwable));
    }

    private MqttDevice getOrCreateDevice(final MessageMetadata messageMetadata) {
        MqttDevice device = this.mqttDeviceRepository.findByDeviceIdentification(
                messageMetadata.getDeviceIdentification());
        if (device == null) {
            device = new MqttDevice();
            device.setHost(messageMetadata.getIpAddress());
            device.setPort(this.defaultPort);
            device.setTopics(this.defaultTopics);
            device.setQos(this.defaultQos);
            this.mqttDeviceRepository.save(device);
        }
        return device;
    }

    private Mqtt3AsyncClient createClient(final MqttDevice device) {
        return Mqtt3Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost(device.getHost())
                .serverPort(device.getPort())
                .buildAsync();
    }

    private void onConnect(final MqttDevice device, final MessageMetadata messageMetadata,
            final Mqtt3AsyncClient client, final Mqtt3ConnAck ack, final Throwable throwable) {
        if (throwable != null) {
            LOG.info(String.format("Client connect failed for device:%s message:%s",
                    messageMetadata.getDeviceIdentification(), throwable.getMessage()));
            this.handleConnectError(throwable, messageMetadata);
        } else {
            LOG.info(String.format("Client connected for device:%s ack:%s", messageMetadata.getDeviceIdentification(),
                    ack.getType()));
            this.subscribe(device, client, messageMetadata);
        }
    }

    private void handleConnectError(final Throwable throwable, final MessageMetadata messageMetadata) {
        final ProtocolResponseMessage responseMessage = this.getResponseBuilder(messageMetadata)
                .result(ResponseMessageResultType.NOT_OK)
                .osgpException(new OsgpException(ComponentType.PROTOCOL_MQTT, "Client connect failed", throwable))
                .build();
        this.outboundOsgpCoreResponseMessageSender.send(responseMessage);
    }

    private void subscribe(final MqttDevice device, final Mqtt3AsyncClient client,
            final MessageMetadata messageMetadata) {
        final String[] topics = device.getTopics().split(",");
        Arrays.stream(topics).forEach(topic -> {
            client.subscribeWith()
                    .topicFilter(topic)
                    .qos(this.getQosOrDefault(device))
                    .callback(publish -> this.onReceive(publish, messageMetadata))
                    .send()
                    .whenComplete((subAck, throwable) -> this.onSubscribe(throwable, messageMetadata));
        });
    }

    private MqttQos getQosOrDefault(final MqttDevice device) {
        MqttQos mqttQos;
        try {
            mqttQos = MqttQos.valueOf(device.getQos());
        } catch (final IllegalArgumentException | NullPointerException e) {
            device.setQos(this.defaultQos);
            mqttQos = MqttQos.valueOf(device.getQos());
        }
        return mqttQos;
    }

    private void onSubscribe(final Throwable throwable, final MessageMetadata messageMetadata) {
        final ProtocolResponseMessage.Builder responseBuilder = this.getResponseBuilder(messageMetadata);
        if (throwable != null) {
            LOG.info(String.format("Client subscription for device:%s failed: %s",
                    messageMetadata.getDeviceIdentification(), throwable.getMessage()));
            responseBuilder.osgpException(
                    new OsgpException(ComponentType.PROTOCOL_MQTT, "Client %s subscription failed", throwable))
                    .result(ResponseMessageResultType.NOT_OK);
        } else {
            LOG.info(String.format("Client subscribed for device:%s", messageMetadata.getDeviceIdentification()));
            responseBuilder.result(ResponseMessageResultType.OK);
        }
        this.outboundOsgpCoreResponseMessageSender.send(responseBuilder.build());
    }

    private void onReceive(final Mqtt3Publish publish, final MessageMetadata messageMetadata) {
        publish.getPayload().ifPresent(p -> {
            final String message = new String(publish.getPayloadAsBytes());
            LOG.info(String.format("Client for device:%s received payload:%s getPayloadAsBytes:%s",
                    messageMetadata.getDeviceIdentification(), p, message));
            final RequestMessage requestMessage = new RequestMessage(messageMetadata.getCorrelationUid(),
                    messageMetadata.getOrganisationIdentification(), messageMetadata.getDeviceIdentification(),
                    message);
            this.outboundOsgpCoreRequestMessageSender.send(requestMessage, MessageType.GET_DATA.name(),
                    messageMetadata);
        });
    }

    private ProtocolResponseMessage.Builder getResponseBuilder(final MessageMetadata messageMetadata) {
        final ProtocolResponseMessage.Builder builder = new ProtocolResponseMessage.Builder();
        builder.deviceMessageMetadata(new DeviceMessageMetadata(messageMetadata))
                .domain(messageMetadata.getDomain())
                .domainVersion(messageMetadata.getDomainVersion())
                .retryCount(messageMetadata.getRetryCount())
                .retryHeader(new RetryHeader())
                .scheduled(messageMetadata.isScheduled())
                .dataObject(messageMetadata.getDeviceIdentification());
        return builder;
    }

}
