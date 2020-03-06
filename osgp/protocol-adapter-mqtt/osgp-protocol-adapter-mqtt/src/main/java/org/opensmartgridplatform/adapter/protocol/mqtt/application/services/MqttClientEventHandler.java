package org.opensmartgridplatform.adapter.protocol.mqtt.application.services;

import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import com.hivemq.client.mqtt.mqtt3.message.subscribe.suback.Mqtt3SubAck;

public interface MqttClientEventHandler {

    void onConnect(MqttClientAdapter mqttClientAdapter, final Mqtt3ConnAck ack, final Throwable throwable);

    void onSubscribe(MqttClientAdapter mqttClientAdapter, final Mqtt3SubAck subAck, final Throwable throwable);

    void onReceive(MqttClientAdapter mqttClientAdapter, final byte[] publish);
}
