package org.opensmartgridplatform.adapter.kafka.da.infra.kafka.out;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.kafka.common.serialization.Deserializer;
import org.opensmartgridplatform.adapter.kafka.da.avro.MeterReading;

public class MeterReadingDeserializer implements Deserializer<MeterReading> {

    @Override
    public MeterReading deserialize(final String topic, final byte[] data) {
        try {
            return MeterReading.fromByteBuffer(ByteBuffer.wrap(data));
        } catch (final IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
