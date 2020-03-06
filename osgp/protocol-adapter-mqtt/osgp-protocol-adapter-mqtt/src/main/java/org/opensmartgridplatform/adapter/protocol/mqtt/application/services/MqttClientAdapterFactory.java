package org.opensmartgridplatform.adapter.protocol.mqtt.application.services;

import org.opensmartgridplatform.adapter.protocol.mqtt.domain.entities.MqttDevice;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Component;

@Component
public class MqttClientAdapterFactory {

    public MqttClientAdapter create(final MqttDevice device, final MessageMetadata messageMetadata,
            final MqttClientEventHandler mqttClientEventHandler) {
        return new MqttClientAdapter(device, messageMetadata, mqttClientEventHandler);
    }
}
