package org.osgp.adapter.protocol.dlms.domain.factories;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FirwareImageFactory {

    private static final String EXCEPTION_MSG_MALFORMED_URL = "Could not download firmware, Malformed URL:";
    private static final String EXCEPTION_MSG_FIRMWARE_NOT_RETRIEVED = "Firmware could not be retrieved.";

    @Value("${firmware.url}")
    private String url;

    public byte[] getFirmwareImage(final String firmwareIdentification) throws ProtocolAdapterException {
        try {
            return this.download(new URL(this.url + firmwareIdentification));
        } catch (final MalformedURLException e) {
            throw new ProtocolAdapterException(EXCEPTION_MSG_MALFORMED_URL + this.url + firmwareIdentification);
        } catch (final IOException e) {
            throw new ProtocolAdapterException(EXCEPTION_MSG_FIRMWARE_NOT_RETRIEVED, e);
        }
    }

    private byte[] download(final URL url) throws IOException {
        try (final InputStream is = url.openStream(); final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            final byte[] byteChunk = new byte[4096];
            int bytesRead;

            while ((bytesRead = is.read(byteChunk)) > 0) {
                baos.write(byteChunk, 0, bytesRead);
            }

            return baos.toByteArray();
        }
    }
}
