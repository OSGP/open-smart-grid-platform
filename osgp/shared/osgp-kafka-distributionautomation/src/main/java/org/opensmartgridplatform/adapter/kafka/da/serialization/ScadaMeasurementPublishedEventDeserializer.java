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

import org.apache.avro.AvroRuntimeException;
import org.apache.kafka.common.serialization.Deserializer;

import com.alliander.data.scadameasurementpublishedevent.ScadaMeasurementPublishedEvent;

public class ScadaMeasurementPublishedEventDeserializer implements Deserializer<ScadaMeasurementPublishedEvent> {

    @Override
    public ScadaMeasurementPublishedEvent deserialize(final String topic, final byte[] data) {
        try {
            return ScadaMeasurementPublishedEvent.fromByteBuffer(ByteBuffer.wrap(data));
        } catch (final IOException e) {
            throw new AvroRuntimeException(e);
        }
    }

}
