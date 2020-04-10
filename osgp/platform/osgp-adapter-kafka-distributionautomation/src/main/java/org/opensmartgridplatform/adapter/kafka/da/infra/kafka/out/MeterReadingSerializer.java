package org.opensmartgridplatform.adapter.kafka.da.infra.kafka.out;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.kafka.common.serialization.Serializer;
import org.opensmartgridplatform.adapter.kafka.MeterReading;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO - Update for correct Avro type.
public class MeterReadingSerializer implements Serializer<MeterReading> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeterReadingSerializer.class);

    @Override
    public byte[] serialize(final String topic, final MeterReading data) {
        try {
            final ByteBuffer byteBuffer = data.toByteBuffer();
            final byte[] byteArray = new byte[byteBuffer.remaining()];
            byteBuffer.get(byteArray);
            return byteArray;
        } catch (final IOException e) {
            LOGGER.error("Error during serializing MeterReading {} for topic {}.", this.getName(data), topic, e);
            return new byte[0];
        }
    }

    private CharSequence getName(final MeterReading data) {
        if (data == null || data.getName() == null) {
            return "unknown";
        }
        return data.getName();
    }

}
