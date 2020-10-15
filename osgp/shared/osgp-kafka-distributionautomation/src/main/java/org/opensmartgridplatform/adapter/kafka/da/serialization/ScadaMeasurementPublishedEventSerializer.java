/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.serialization;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.data.scadameasurementpublishedevent.ScadaMeasurementPublishedEvent;

public class ScadaMeasurementPublishedEventSerializer implements Serializer<ScadaMeasurementPublishedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScadaMeasurementPublishedEventSerializer.class);

    @Override
    public byte[] serialize(final String topic, final ScadaMeasurementPublishedEvent data) {
        try {
            final ByteBuffer byteBuffer = data.toByteBuffer();
            final byte[] byteArray = new byte[byteBuffer.remaining()];
            byteBuffer.get(byteArray);
            return byteArray;
        } catch (final IOException e) {
            LOGGER.error("Error during serializing ScadaMeasurementPublishedEvent {} for topic {}.",
                    this.getDescription(data), topic, e);
            return new byte[0];
        }
    }

    private CharSequence getDescription(final ScadaMeasurementPublishedEvent data) {
        if (data == null || data.getDescription() == null) {
            return "unknown";
        }
        return data.getDescription();
    }

}
