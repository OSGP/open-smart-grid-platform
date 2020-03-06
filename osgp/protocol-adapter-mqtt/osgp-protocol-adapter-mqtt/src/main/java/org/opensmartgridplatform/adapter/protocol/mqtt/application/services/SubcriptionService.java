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

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import com.hivemq.client.mqtt.mqtt3.message.subscribe.suback.Mqtt3SubAck;
import org.opensmartgridplatform.adapter.protocol.mqtt.application.messaging.OutboundOsgpCoreRequestMessageSender;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.entities.MqttDevice;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.repositories.MqttDeviceRepository;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service(value = "mqttSubcriptionService")
public class SubcriptionService implements MqttClientEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SubcriptionService.class);

    private final MqttDeviceRepository mqttDeviceRepository;
    private final OutboundOsgpCoreRequestMessageSender outboundOsgpCoreRequestMessageSender;
    private final MqttClientAdapterFactory mqttClientAdapterFactory;

    private final int defaultPort;
    private final String defaultTopics;
    private final String defaultQos;

    public SubcriptionService(final MqttDeviceRepository mqttDeviceRepository,
            final MqttClientAdapterFactory mqttClientAdapterFactory,
            final OutboundOsgpCoreRequestMessageSender outboundOsgpCoreRequestMessageSender,
            @Value("#{new Integer('${mqtt.broker.defaultPort}')}") final int defaultPort,
            @Value("${mqtt.broker.defaultTopics}") final String defaultTopics,
            @Value("${mqtt.broker.defaultQos}") final String defaultQos) {
        this.mqttDeviceRepository = mqttDeviceRepository;
        this.mqttClientAdapterFactory = mqttClientAdapterFactory;
        this.outboundOsgpCoreRequestMessageSender = outboundOsgpCoreRequestMessageSender;
        this.defaultPort = defaultPort;
        this.defaultTopics = defaultTopics;
        this.defaultQos = defaultQos;
    }

    public void subscribe(final MessageMetadata messageMetadata) {
        final MqttDevice device = this.getOrCreateDevice(messageMetadata);
        final MqttClientAdapter mqttClientAdapter = this.mqttClientAdapterFactory.create(device, messageMetadata, this);
        mqttClientAdapter.connect();
    }

    private MqttDevice getOrCreateDevice(final MessageMetadata messageMetadata) {
        MqttDevice device = this.mqttDeviceRepository.findByDeviceIdentification(
                messageMetadata.getDeviceIdentification());
        if (device == null) {
            device = new MqttDevice(messageMetadata.getDeviceIdentification());
            device.setHost(messageMetadata.getIpAddress());
            device.setPort(this.defaultPort);
            device.setTopics(this.defaultTopics);
            device.setQos(this.defaultQos);
            this.mqttDeviceRepository.save(device);
        }
        return device;
    }

    @Override
    public void onConnect(final MqttClientAdapter mqttClientAdapter, final Mqtt3ConnAck ack,
            final Throwable throwable) {
        final MessageMetadata messageMetadata = mqttClientAdapter.getMessageMetadata();
        if (throwable != null) {
            LOG.info(String.format("Client connect failed for device:%s", messageMetadata.getDeviceIdentification()),
                    throwable);
        } else {
            LOG.info(String.format("Client connected for device:%s ack:%s", messageMetadata.getDeviceIdentification(),
                    ack.getType()));
            final MqttDevice device = mqttClientAdapter.getDevice();
            final MqttQos qos = this.getQosOrDefault(device);
            final String[] topics = device.getTopics().split(",");
            Arrays.stream(topics).forEach(topic -> mqttClientAdapter.subscribe(topic, qos));
        }
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

    @Override
    public void onSubscribe(final MqttClientAdapter mqttClientAdapter, final Mqtt3SubAck subAck,
            final Throwable throwable) {
        final MessageMetadata messageMetadata = mqttClientAdapter.getMessageMetadata();
        if (throwable != null) {
            LOG.info(String.format("Client subscription for device:%s failed",
                    messageMetadata.getDeviceIdentification()), throwable);
        } else {
            LOG.info(String.format("Client subscribed for device:%s suback:%s",
                    messageMetadata.getDeviceIdentification(), subAck.getType()));
        }
    }

    @Override
    public void onReceive(final MqttClientAdapter mqttClientAdapter, final byte[] payloadAsBytes) {
        final String payload = new String(payloadAsBytes);
        final MessageMetadata messageMetadata = mqttClientAdapter.getMessageMetadata();
        LOG.info(String.format("Client for device:%s received payload:%s", messageMetadata.getDeviceIdentification(),
                payload));
        final RequestMessage requestMessage = new RequestMessage(messageMetadata.getCorrelationUid(),
                messageMetadata.getOrganisationIdentification(), messageMetadata.getDeviceIdentification(), payload);
        this.outboundOsgpCoreRequestMessageSender.send(requestMessage, MessageType.GET_DATA.name(), messageMetadata);
    }

}
