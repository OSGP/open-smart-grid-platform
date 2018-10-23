package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class QualityConverterTest {

    @Test
    public void testToShort() throws Exception {

        // arrange
        final byte[] ba = new byte[2];
        ba[0] = (byte) 193;
        ba[1] = (byte) 0;

        // act
        final short s = QualityConverter.toShort(ba);

        // assert
        assertEquals(131, s);
    }
}
