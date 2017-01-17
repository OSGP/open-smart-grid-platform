package com.alliander.osgp.platform.cucumber.mocks.iec61850;

import java.io.IOException;
import java.io.InputStream;

import org.openmuc.openiec61850.SclParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alliander.osgp.simulator.protocol.iec61850.server.RtuSimulator;

@Component
public class Iec61850MockServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850MockServer.class);

    @Value("${iec61850.mock.icd.filename}")
    private String icdFilename;

    @Value("${iec61850.mock.port}")
    private int port;

    private RtuSimulator rtuSimulator;

    private boolean simulatorIsListening = false;

    private boolean isInitialised() {
        return this.rtuSimulator != null;
    }

    private InputStream getIcdFile() {
        final InputStream icdFile = ClassLoader.getSystemResourceAsStream(this.icdFilename);
        if (icdFile == null) {
            throw new AssertionError("Expected IEC61850 Mock ICD file: " + this.icdFilename);
        }
        return icdFile;
    }

    private RtuSimulator initialiseSimulator() {
        try {
            return new RtuSimulator(this.port, this.getIcdFile());
        } catch (final SclParseException e) {
            throw new AssertionError("Expected IEC61850 Mock configuration allowing simulator startup.", e);
        }
    }

    public void start() {

        if (this.isInitialised()) {
            if (this.simulatorIsListening) {
                throw new IllegalStateException("RtuSimulator was already started.");
            }
        } else {
            this.rtuSimulator = this.initialiseSimulator();
        }

        try {
            this.rtuSimulator.start();
            this.simulatorIsListening = true;
            LOGGER.info("Started IEC61850 Mock server on port {}", this.port);
        } catch (final IOException e) {
            throw new AssertionError("Expected IEC61850 Mock configuration allowing simulator startup.", e);
        }
    }

    public void stop() {

        if (this.rtuSimulator == null || !this.simulatorIsListening) {
            LOGGER.warn("Not stopping IEC61850 Mock server, because it was not running.");
            return;
        }

        this.rtuSimulator.stop();
        this.simulatorIsListening = false;
        LOGGER.info("Stopped IEC61850 Mock server");
    }

    public void mockValue(final String logicalDeviceName, final String node, final String value) {
        if (!this.isInitialised()) {
            throw new AssertionError("RtuSimulator has not yet been initialised.");
        }
        this.rtuSimulator.mockValue(logicalDeviceName, node, value);
    }

    public void assertValue(final String logicalDeviceName, final String node, final String value) {
        if (!this.isInitialised()) {
            throw new AssertionError("RtuSimulator has not yet been initialised.");
        }
        // TODO: assertValue method is missing in the RtuSimulator class
        // this.rtuSimulator.assertValue(logicalDeviceName, node, value);
    }
}
