/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringinstallation;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.cucumber.platform.smartmetering.config.DlmsSimulatorConfig;
import com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.AbstractSmartMeteringSteps;

import cucumber.api.java.en.Given;

public class DeviceSimulatorSteps extends AbstractSmartMeteringSteps {

    @Autowired
    private DlmsSimulatorConfig dlmsSimulatorConfig;

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceSimulatorSteps.class);

    private static final String CLEANUP_PROPS_REQUEST = "http://%s/RESTfulExample/rest/CleanupProperties";
    private static final String ADD_PROPS_REQUEST = "http://%s/RESTfulExample/rest/AddProperties/%s/%s";

    @Given("^device simulate with classid (\\d+) obiscode \"([^\"]*)\" and attributes$")
    public void deviceSimulateWithClassidObiscodeAndAttributes(final int classId, final String obisCode,
            final Map<String, String> settings) throws Throwable {

        this.setRemoteProperties(classId, obisCode, settings);
    }

    public void removeAllTemporaryPropertiesFiles() {
        try {
            final CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            final String request = String.format(CLEANUP_PROPS_REQUEST, this.getUrl());
            final HttpGet httpGetRequest = new HttpGet(request);
            httpClient.execute(httpGetRequest);
        } catch (final IOException e) {
            LOGGER.error("error while calling CleanupProperties request", e);
        }
    }

    private void setRemoteProperties(final int classId, final String obisCode, final Map<String, String> settings) {

        try (final CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            final String request = this.makeRequest(classId, obisCode, settings);
            final HttpGet httpGetRequest = new HttpGet(request);
            final HttpResponse httpResponse = httpClient.execute(httpGetRequest);
            final String msg = String.format("add device-simulator properties %s, status=", request,
                    httpResponse.getStatusLine());
            LOGGER.debug(msg);
        } catch (final IOException e) {
            LOGGER.error("error while calling AddProperties request", e);
            Assert.fail("error while calling AddProperties request");
        }
    }

    private String makeRequest(final int classId, final String obisCode, final Map<String, String> settings) {
        final StringBuilder props = new StringBuilder();
        for (final String key : settings.keySet()) {
            props.append(key + "=" + settings.get(key) + ",");
        }

        final String filename = String.format("%d_%s", classId, obisCode);
        return String.format(ADD_PROPS_REQUEST, this.getUrl(), filename, props.toString());
    }

    private String getUrl() {
        return this.dlmsSimulatorConfig.getDynamicPropertiesHost() + ":"
                + this.dlmsSimulatorConfig.getDynamicPropertiesPort();
    }

}
