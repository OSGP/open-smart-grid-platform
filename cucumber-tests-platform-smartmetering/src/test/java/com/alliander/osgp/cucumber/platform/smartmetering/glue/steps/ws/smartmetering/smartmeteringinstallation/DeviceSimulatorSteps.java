/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringinstallation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.AbstractSmartMeteringSteps;

import cucumber.api.java.en.Given;

public class DeviceSimulatorSteps extends AbstractSmartMeteringSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceSimulatorSteps.class);
    private static final String DEVSIM_PROPERTIES_PATH = "/tmp/device-simulator";

    @Given("^device simulate with classid (\\d+) osiscode \"([^\"]*)\" and attributes$")
    public void deviceSimulateWithClassidOsiscodeAndAttributes(final int classId, final String obisCode,
            final Map<String, String> settings) throws Throwable {

        final Properties properties = new Properties();
        for (final String key : settings.keySet()) {
            properties.put(key, settings.get(key));
        }

        final File path = new File(DEVSIM_PROPERTIES_PATH);
        if (!path.exists()) {
            path.mkdir();
        }

        final String filename = String.format("%s/%d_%s.properties", DEVSIM_PROPERTIES_PATH, classId, obisCode);
        final File outputFile = new File(filename);
        try (final FileOutputStream os = new FileOutputStream(outputFile)) {
            properties.store(os, "temporary device simulator properties");
        }
    }

    public void removeAllTemporaryPropertiesFiles() {
        final File path = new File(DEVSIM_PROPERTIES_PATH);
        try {
            if (path.exists()) {
                FileUtils.cleanDirectory(path);
            }
        } catch (final IOException e) {
            LOGGER.error("error deleting temporary properties file from " + path, e);
        }
    }
}
