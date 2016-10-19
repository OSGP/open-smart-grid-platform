package org.osgp.adapter.protocol.dlms.domain.factories;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.PostConstruct;

import org.osgp.adapter.protocol.dlms.exceptions.FirmwareImageFactoryException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FirwareImageFactory {

    private static final String EXCEPTION_MSG_INVALID_HTTP_RESPONSE_CODE = "Invalid HTTP response code: ";
    private static final String EXCEPTION_MSG_MALFORMED_URL = "Could not download firmware, Malformed URL:";
    private static final String EXCEPTION_MSG_FIRMWARE_NOT_RETRIEVED = "Firmware could not be retrieved.";

    @Value("${firmware.url}")
    private String url;

    @PostConstruct
    public void init() {
        // URL should always and in a slash
        if (this.url.substring(this.url.length() - 1) != "/") {
            this.url += "/";
        }
    }

    public byte[] getFirmwareImage(final String firmwareIdentification) throws FirmwareImageFactoryException {
        try {
            URL downloadUrl = new URL(this.url + firmwareIdentification);
            this.checkUrl(downloadUrl);
            return this.download(downloadUrl);
        } catch (final MalformedURLException e) {
            throw new FirmwareImageFactoryException(EXCEPTION_MSG_MALFORMED_URL + this.url + firmwareIdentification);
        } catch (final IOException e) {
            throw new FirmwareImageFactoryException(EXCEPTION_MSG_FIRMWARE_NOT_RETRIEVED, e);
        }
    }

    private void checkUrl(final URL url) throws FirmwareImageFactoryException {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new FirmwareImageFactoryException(EXCEPTION_MSG_INVALID_HTTP_RESPONSE_CODE
                        + connection.getResponseCode());
            }
        } catch (IOException e) {
            throw new FirmwareImageFactoryException(EXCEPTION_MSG_FIRMWARE_NOT_RETRIEVED, e);
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
