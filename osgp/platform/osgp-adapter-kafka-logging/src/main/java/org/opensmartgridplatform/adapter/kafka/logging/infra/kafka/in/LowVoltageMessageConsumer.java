/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.logging.infra.kafka.in;

import java.nio.ByteBuffer;

import org.apache.commons.codec.binary.Hex;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.opensmartgridplatform.adapter.kafka.da.signature.MessageSigner;
import org.opensmartgridplatform.adapter.kafka.logging.config.LowVoltageMessageLoggingEnabled;
import org.opensmartgridplatform.kafka.logging.KafkaLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.alliander.data.scadameasurementpublishedevent.Message;

@Component
@Conditional(LowVoltageMessageLoggingEnabled.class)
public class LowVoltageMessageConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(LowVoltageMessageConsumer.class);

    @Autowired
    private KafkaLogger kafkaLogger;

    @Autowired
    private MessageSigner messageSigner;

    @KafkaListener(containerFactory = "lowVoltageMessageKafkaListenerContainerFactory",
            topics = "${low.voltage.kafka.topic}")
    public void listen(final ConsumerRecord<String, Message> consumerRecord) {
        this.kafkaLogger.log(consumerRecord);

        final String topic = consumerRecord.topic();
        final Object key = consumerRecord.key();
        final Message message = consumerRecord.value();
        if (this.messageSigner.verify(message)) {
            LOGGER.debug("Consumer received message with verified signature on topic \"{}\" with key \"{}\"", topic,
                    key);
        } else {
            final String signature = this.asString(message.getSignature());
            LOGGER.warn(
                    "Consumer received message for which signature could not be verified on topic \"{}\" with key \"{}\" and signature {}",
                    topic, key, signature);
        }
    }

    private String asString(final ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            return null;
        }
        return Hex.encodeHexString(byteBuffer);
    }
}
