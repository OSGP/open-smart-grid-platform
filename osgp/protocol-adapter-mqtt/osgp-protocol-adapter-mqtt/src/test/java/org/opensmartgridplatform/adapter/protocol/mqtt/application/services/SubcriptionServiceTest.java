package org.opensmartgridplatform.adapter.protocol.mqtt.application.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
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

@ExtendWith(MockitoExtension.class)
class SubcriptionServiceTest {

    private static final int DEFAULT_PORT = 11111;
    private static final String DEFAULT_TOPICS = "test-default-topics";
    private static final MqttQos DEFAULT_QOS = MqttQos.AT_MOST_ONCE;

    private SubcriptionService instance;

    @Mock
    private OutboundOsgpCoreRequestMessageSender outboundOsgpCoreRequestMessageSender;
    @Mock
    private MqttDeviceRepository mqttDeviceRepository;
    @Mock
    private MqttClientAdapterFactory mqttClientAdapterFactory;

    @Mock
    private MessageMetadata messageMetaData;
    @Captor
    private ArgumentCaptor<MqttDevice> deviceCaptor;
    @Mock
    private MqttClientAdapter mqttClientAdapter;

    @BeforeEach
    public void setUp() {
        this.instance = new SubcriptionService(this.mqttDeviceRepository, this.mqttClientAdapterFactory,
                this.outboundOsgpCoreRequestMessageSender, DEFAULT_PORT, DEFAULT_TOPICS, DEFAULT_QOS.name());
    }

    @Test
    void subscribeNewDevice() {
        // SETUP
        when(this.messageMetaData.getDeviceIdentification()).thenReturn("test-metadata-device-id");
        when(this.messageMetaData.getIpAddress()).thenReturn("test-metadata-host");
        when(this.mqttDeviceRepository.findByDeviceIdentification(
                this.messageMetaData.getDeviceIdentification())).thenReturn(null);
        when(this.mqttClientAdapterFactory.create(any(MqttDevice.class), eq(this.messageMetaData),
                eq(this.instance))).thenReturn(this.mqttClientAdapter);

        // CALL
        this.instance.subscribe(this.messageMetaData);

        // VERIFY
        verify(this.mqttDeviceRepository).save(this.deviceCaptor.capture());

        final MqttDevice savedDevice = this.deviceCaptor.getValue();
        assertEquals(this.messageMetaData.getDeviceIdentification(), savedDevice.getDeviceIdentification());
        assertEquals(DEFAULT_QOS.name(), savedDevice.getQos());
        assertEquals(this.messageMetaData.getIpAddress(), savedDevice.getHost());
        assertEquals(DEFAULT_TOPICS, savedDevice.getTopics());
        assertEquals(DEFAULT_PORT, savedDevice.getPort());

        verify(this.mqttClientAdapter).connect();
    }

    @Test
    void subscribeExistingDevice() {
        // SETUP
        when(this.messageMetaData.getDeviceIdentification()).thenReturn("test-metadata-device-id");
        final MqttDevice device = mock(MqttDevice.class);
        when(this.mqttDeviceRepository.findByDeviceIdentification(
                this.messageMetaData.getDeviceIdentification())).thenReturn(device);
        when(this.mqttClientAdapterFactory.create(eq(device), eq(this.messageMetaData), eq(this.instance))).thenReturn(
                this.mqttClientAdapter);
        // CALL
        this.instance.subscribe(this.messageMetaData);

        // VERIFY
        verify(this.mqttClientAdapter).connect();
    }

    @Test
    void onConnect() {
        // SETUP
        when(this.mqttClientAdapter.getMessageMetadata()).thenReturn(this.messageMetaData);
        final MqttDevice device = new MqttDevice();
        device.setTopics("topic1");
        when(this.mqttClientAdapter.getDevice()).thenReturn(device);
        final Mqtt3ConnAck ack = mock(Mqtt3ConnAck.class);
        final Throwable throwable = null;

        // CALL
        this.instance.onConnect(this.mqttClientAdapter, ack, throwable);

        // VERIFY
        verify(this.mqttClientAdapter).subscribe(device.getTopics(), DEFAULT_QOS);
    }

    @Test
    void onConnectError() {
        // SETUP
        when(this.mqttClientAdapter.getMessageMetadata()).thenReturn(this.messageMetaData);
        final Mqtt3ConnAck ack = mock(Mqtt3ConnAck.class);
        final Throwable throwable = mock(Throwable.class);

        // CALL
        this.instance.onConnect(this.mqttClientAdapter, ack, throwable);

        // VERIFY
        verifyNoMoreInteractions(this.mqttClientAdapter);
    }
}