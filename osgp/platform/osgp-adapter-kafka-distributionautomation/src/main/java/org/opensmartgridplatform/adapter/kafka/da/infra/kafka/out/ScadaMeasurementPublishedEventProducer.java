/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.infra.kafka.out;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.opensmartgridplatform.adapter.kafka.da.application.mapping.DistributionAutomationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.alliander.data.scadameasurementpublishedevent.Message;
import com.alliander.data.scadameasurementpublishedevent.ScadaMeasurementPublishedEvent;
import com.alliander.messaging.MessageId;

@Service
public class ScadaMeasurementPublishedEventProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScadaMeasurementPublishedEventProducer.class);

    private final KafkaTemplate<String, Message> kafkaTemplate;

    private final DistributionAutomationMapper mapper;

    @Autowired
    public ScadaMeasurementPublishedEventProducer(
            @Qualifier("distributionAutomationKafkaTemplate") final KafkaTemplate<String, Message> kafkaTemplate,
            final DistributionAutomationMapper mapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
    }

    public void send(final String measurement) {

        LOGGER.info("ScadaMeasurementPublishedEventProducer.send is called with measurement {}", measurement);

        final ScadaMeasurementPublishedEvent event = this.mapper.map(measurement, ScadaMeasurementPublishedEvent.class);

        LOGGER.debug("Trying to send ScadaMeasurementPublishedEventProducer {}", event);

        if (event != null) {
            final MessageId messageId = new MessageId(getBytesFromUUID(UUID.randomUUID()));
            final Message message = new Message(messageId, System.currentTimeMillis(), "GXF", null, event);
            /*
             * No need for callback functionality now; by default, the template
             * is configured with a LoggingProducerListener, which logs errors
             * and does nothing when the send is successful.
             */
            this.kafkaTemplate.sendDefault(message);
        }
    }

    private static byte[] getBytesFromUUID(final UUID uuid) {
        final ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

        return bb.array();
    }
}
