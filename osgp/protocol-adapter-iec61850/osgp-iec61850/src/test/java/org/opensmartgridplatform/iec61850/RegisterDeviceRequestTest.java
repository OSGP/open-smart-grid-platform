package org.opensmartgridplatform.iec61850;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class RegisterDeviceRequestTest {

    private static final String TESTED_SERIAL_NUMBER = "1234567890";
    private static final String TESTED_IP_ADDRESS = "127.0.0.1";
    private static final byte[] TESTED_BYTES = (TESTED_SERIAL_NUMBER + "," + TESTED_IP_ADDRESS)
            .getBytes(StandardCharsets.US_ASCII);

    @Test
    public void testRegisterDeviceRequestFromBytes() throws Exception {

        final RegisterDeviceRequest request = new RegisterDeviceRequest(TESTED_BYTES);

        assertEquals(TESTED_SERIAL_NUMBER, request.getSerialNumber());
        assertEquals(TESTED_IP_ADDRESS, request.getIpAddress());

        assertEquals("Device identification should be serial number prefixed with 'KAI-'", "KAI-"
                + TESTED_SERIAL_NUMBER, request.getDeviceIdentification());
        assertArrayEquals(
                "Byte array should match the byte[] used to construct the "
                        + RegisterDeviceRequest.class.getSimpleName(), TESTED_BYTES, request.toByteArray());
        assertEquals("Size should be the length of the byte array representation", TESTED_BYTES.length,
                request.getSize());
    }

    @Test
    public void testRegisterDeviceRequestFromFieldValues() throws Exception {

        final RegisterDeviceRequest request = new RegisterDeviceRequest(TESTED_SERIAL_NUMBER, TESTED_IP_ADDRESS);

        assertEquals(TESTED_SERIAL_NUMBER, request.getSerialNumber());
        assertEquals(TESTED_IP_ADDRESS, request.getIpAddress());

        assertEquals("Device identification should be serial number prefixed with 'KAI-'", "KAI-"
                + TESTED_SERIAL_NUMBER, request.getDeviceIdentification());
        assertArrayEquals(
                "Byte array should match the ASCII bytes for the concatenation of serial number, separator '/' and the IP address"
                        + RegisterDeviceRequest.class.getSimpleName(), TESTED_BYTES, request.toByteArray());
        assertEquals("Size should be the length of the byte array representation", TESTED_BYTES.length,
                request.getSize());
    }
}
