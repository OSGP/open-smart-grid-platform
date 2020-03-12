/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.application.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import com.hivemq.client.mqtt.mqtt3.message.subscribe.suback.Mqtt3SubAck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.mqtt.application.messaging.OutboundOsgpCoreRequestMessageSender;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.entities.MqttDevice;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.repositories.MqttDeviceRepository;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    private static final int DEFAULT_PORT = 11111;
    private static final String DEFAULT_TOPICS = "test-default-topics";
    private static final MqttQos DEFAULT_QOS = MqttQos.AT_MOST_ONCE;

    private SubscriptionService instance;

    @Mock
    private OutboundOsgpCoreRequestMessageSender outboundOsgpCoreRequestMessageSender;
    @Mock
    private MqttDeviceRepository mqttDeviceRepository;
    @Mock
    private MqttClientAdapterFactory mqttClientAdapterFactory;

    @Mock
    private MessageMetadata messageMetadata;
    @Captor
    private ArgumentCaptor<MqttDevice> deviceCaptor;
    @Mock
    private MqttClientAdapter mqttClientAdapter;
    @Captor
    private ArgumentCaptor<RequestMessage> requestMessageCaptor;

    @BeforeEach
    public void setUp() {
        this.instance = new SubscriptionService(this.mqttDeviceRepository, this.mqttClientAdapterFactory,
                this.outboundOsgpCoreRequestMessageSender, DEFAULT_PORT, DEFAULT_TOPICS, DEFAULT_QOS.name());
        lenient().when(this.mqttClientAdapter.getMessageMetadata()).thenReturn(this.messageMetadata);
    }

    @Test
    void subscribeNewDevice() {
        // SETUP
        when(this.messageMetadata.getDeviceIdentification()).thenReturn("test-metadata-device-id");
        when(this.messageMetadata.getIpAddress()).thenReturn("test-metadata-host");
        when(this.mqttDeviceRepository.findByDeviceIdentification(
                this.messageMetadata.getDeviceIdentification())).thenReturn(null);
        when(this.mqttClientAdapterFactory.create(any(MqttDevice.class), eq(this.messageMetadata),
                eq(this.instance))).thenReturn(this.mqttClientAdapter);

        // CALL
        this.instance.subscribe(this.messageMetadata);

        // VERIFY
        verify(this.mqttDeviceRepository).save(this.deviceCaptor.capture());

        final MqttDevice savedDevice = this.deviceCaptor.getValue();
        assertEquals(this.messageMetadata.getDeviceIdentification(), savedDevice.getDeviceIdentification());
        assertEquals(DEFAULT_QOS.name(), savedDevice.getQos());
        assertEquals(this.messageMetadata.getIpAddress(), savedDevice.getHost());
        assertEquals(DEFAULT_TOPICS, savedDevice.getTopics());
        assertEquals(DEFAULT_PORT, savedDevice.getPort());

        verify(this.mqttClientAdapter).connect();
    }

    @Test
    void subscribeExistingDevice() {
        // SETUP
        when(this.messageMetadata.getDeviceIdentification()).thenReturn("test-metadata-device-id");
        final MqttDevice device = mock(MqttDevice.class);
        when(this.mqttDeviceRepository.findByDeviceIdentification(
                this.messageMetadata.getDeviceIdentification())).thenReturn(device);
        when(this.mqttClientAdapterFactory.create(eq(device), eq(this.messageMetadata), eq(this.instance))).thenReturn(
                this.mqttClientAdapter);
        // CALL
        this.instance.subscribe(this.messageMetadata);

        // VERIFY
        verify(this.mqttClientAdapter).connect();
    }

    @Test
    void onConnect() {
        // SETUP
        final MqttDevice device = new MqttDevice();
        final String topic1 = "topic1";
        final String topic2 = "topic2";
        device.setTopics(String.join(",", topic1, topic2));
        when(this.mqttClientAdapter.getDevice()).thenReturn(device);
        final Mqtt3ConnAck ack = mock(Mqtt3ConnAck.class);
        final Throwable throwable = null;

        // CALL
        this.instance.onConnect(this.mqttClientAdapter, ack, throwable);

        // VERIFY
        verify(this.mqttClientAdapter).subscribe(topic1, DEFAULT_QOS);
        verify(this.mqttClientAdapter).subscribe(topic2, DEFAULT_QOS);
    }

    @Test
    void onConnectDeviceWithQos() {
        // SETUP
        final MqttDevice device = new MqttDevice();
        final MqttQos deviceQos = MqttQos.EXACTLY_ONCE;
        device.setQos(deviceQos.name());
        final String topic1 = "topic1";
        device.setTopics(topic1);
        when(this.mqttClientAdapter.getDevice()).thenReturn(device);
        final Mqtt3ConnAck ack = mock(Mqtt3ConnAck.class);
        final Throwable throwable = null;

        // CALL
        this.instance.onConnect(this.mqttClientAdapter, ack, throwable);

        // VERIFY
        verify(this.mqttClientAdapter).subscribe(topic1, deviceQos);
    }

    @Test
    void onConnectError() {
        // SETUP
        final Mqtt3ConnAck ack = mock(Mqtt3ConnAck.class);
        final Throwable throwable = mock(Throwable.class);

        // CALL
        this.instance.onConnect(this.mqttClientAdapter, ack, throwable);

        // VERIFY
        verify(this.messageMetadata).getDeviceIdentification();
    }

    @Test
    void onSubscribe() {
        // SETUP
        final Mqtt3SubAck subAck = mock(Mqtt3SubAck.class);
        final Throwable throwable = null;

        // CALL
        this.instance.onSubscribe(this.mqttClientAdapter, subAck, throwable);

        // VERIFY
        verify(subAck).getType();
    }

    @Test
    void onSubscribeError() {
        // SETUP
        final Mqtt3SubAck subAck = mock(Mqtt3SubAck.class);
        final Throwable throwable = mock(Throwable.class);

        // CALL
        this.instance.onSubscribe(this.mqttClientAdapter, subAck, throwable);

        // VERIFY
        verifyNoMoreInteractions(subAck);
    }

    @Test
    void onReceive() {
        // SETUP
        final String correlationUid = "test-correlation-uuid";
        when(this.messageMetadata.getCorrelationUid()).thenReturn(correlationUid);
        final String organisationIdentification = "test-organisation-id";
        when(this.messageMetadata.getOrganisationIdentification()).thenReturn(organisationIdentification);
        final String deviceIdentification = "test-device-id";
        when(this.messageMetadata.getDeviceIdentification()).thenReturn(deviceIdentification);

        final byte[] bytes = "12345".getBytes();

        // CALL
        this.instance.onReceive(this.mqttClientAdapter, bytes);

        // VERIFY
        verify(this.mqttClientAdapter).getMessageMetadata();
        verify(this.outboundOsgpCoreRequestMessageSender).send(this.requestMessageCaptor.capture(),
                eq(MessageType.GET_DATA.name()), eq(this.messageMetadata));
        final RequestMessage requestMessage = this.requestMessageCaptor.getValue();
        assertEquals(correlationUid, requestMessage.getCorrelationUid());
        assertEquals(organisationIdentification, requestMessage.getOrganisationIdentification());
        assertEquals(deviceIdentification, requestMessage.getDeviceIdentification());

    }
}