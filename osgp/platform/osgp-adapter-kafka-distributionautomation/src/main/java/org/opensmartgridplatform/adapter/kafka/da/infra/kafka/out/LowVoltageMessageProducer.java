/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.infra.kafka.out;

import org.opensmartgridplatform.adapter.kafka.da.application.config.LocationConfig;
import org.opensmartgridplatform.adapter.kafka.da.application.mapping.DistributionAutomationMapper;
import org.opensmartgridplatform.adapter.kafka.da.infra.mqtt.in.ScadaMeasurementPayload;
import org.opensmartgridplatform.shared.utils.UuidUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.alliander.data.scadameasurementpublishedevent.Message;
import com.alliander.data.scadameasurementpublishedevent.ScadaMeasurementPublishedEvent;
import com.alliander.messaging.MessageId;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LowVoltageMessageProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(LowVoltageMessageProducer.class);

    private static final int META_MEASUREMENT_FEEDER = 100;

    private final KafkaTemplate<String, Message> kafkaTemplate;

    private final DistributionAutomationMapper mapper;

    private final LocationConfig locationConfig;

    @Autowired
    public LowVoltageMessageProducer(
            @Qualifier("distributionAutomationKafkaTemplate") final KafkaTemplate<String, Message> kafkaTemplate,
            final DistributionAutomationMapper mapper, final LocationConfig locationConfig) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
        this.locationConfig = locationConfig;
    }

    public void send(final String measurement) {

        LOGGER.info("LowVoltageMessageProducer.send is called with measurement {}", measurement);

        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {

            // we expect a list with one payload from the rtu.
            final ScadaMeasurementPayload[] payloads = objectMapper.readValue(measurement,
                    ScadaMeasurementPayload[].class);
            if (payloads.length == 0 || payloads[0] == null) {
                LOGGER.error("Source does not include the correct data fields. Source {}", measurement);
                return;
            } else if (payloads.length > 1) {
                LOGGER.info(
                        "Source has more than one payload, we are only processing the first and ignoring the others");
            }

            final ScadaMeasurementPayload payload = this.addLocationData(payloads);

            final ScadaMeasurementPublishedEvent event = this.mapper.map(payload, ScadaMeasurementPublishedEvent.class);

            LOGGER.debug("Trying to send ScadaMeasurementPublishedEventProducer {}", event);

            if (event != null) {
                final MessageId messageId = new MessageId(UuidUtil.getBytesFromRandomUuid());
                final Message message = new Message(messageId, System.currentTimeMillis(), "GXF", null, event);
                /*
                 * No need for callback functionality now; by default, the
                 * template is configured with a LoggingProducerListener, which
                 * logs errors and does nothing when the send is successful.
                 */
                this.kafkaTemplate.sendDefault(message);
            }
        } catch (final JsonProcessingException e) {
            LOGGER.error("Error while converting measurement to Json", e);
        }
    }

    private ScadaMeasurementPayload addLocationData(final ScadaMeasurementPayload[] payloads) {
        final ScadaMeasurementPayload payload = payloads[0];
        final String substationIdentification = payload.getSubstationIdentification();
        payload.setSubstationName(this.locationConfig.getSubstationLocation(substationIdentification));
        final String feeder = payload.getFeeder();
        try {
            if (Integer.valueOf(feeder) != META_MEASUREMENT_FEEDER) {
                payload.setBayIdentification(
                        this.locationConfig.getBayIdentification(substationIdentification, feeder));
            }
        } catch (final NumberFormatException e) {
            LOGGER.error("Payload contains a non-numeric value for feeder", e);
        }
        return payload;
    }
}
