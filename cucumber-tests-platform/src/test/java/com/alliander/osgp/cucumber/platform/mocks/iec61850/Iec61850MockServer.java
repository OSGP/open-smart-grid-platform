/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.mocks.iec61850;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.openmuc.openiec61850.SclParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alliander.osgp.simulator.protocol.iec61850.server.RtuSimulator;

@Component
public class Iec61850MockServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850MockServer.class);

    @Value("${iec61850.mock.icd.filename:Pampus_v0.4.5.icd}")
    private String icdFilename;

    @Value("${iec61850.mock.port:60102}")
    private int port;

    @Value("${iec61850.mock.serverName:WAGO61850Server}")
    private String serverName;

    private RtuSimulator rtuSimulator;

    private boolean simulatorIsListening = false;

    private boolean isInitialised() {
        return this.rtuSimulator != null;
    }

    public Iec61850MockServer() {
    }

    public Iec61850MockServer(final String serverName, final String icdFilename, final int port) {
        super();
        this.serverName = serverName;
        this.icdFilename = icdFilename;
        this.port = port;
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
            return new RtuSimulator(this.port, this.getIcdFile(), this.serverName);
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
        this.rtuSimulator.assertValue(logicalDeviceName, node, value);
    }

    /**
     * This method can be used to stop the running simulator, that was started
     * (in @Before) with all default parameters, and start it again with the
     * supplied parameters in the feature file. Only the provided feature
     * parameters will override the default parameter from the property file.
     * Currently the properties can be overwritten: ServerName, Port and/or
     * IcdFilename.
     *
     * @param settings
     *            the feature file parameters.
     *
     */
    public void restart(final Map<String, String> settings) {
        this.stop();
        this.rtuSimulator = null;
        this.start();
    }

}
