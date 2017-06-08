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

    private static final String CLEANUP_PROPS_REQUEST = "http://%s/CleanupProperties";
    private static final String ADD_PROPS_REQUEST = "http://%s/AddProperties/%s/%s";

    public void removeAllTemporaryPropertiesFiles() {
        if (this.isBaseUrlEnabled()) {
            try {
                final CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                final String request = String.format(CLEANUP_PROPS_REQUEST, this.getBaseUrl());
                final HttpGet httpGetRequest = new HttpGet(request);
                httpClient.execute(httpGetRequest);
            } catch (final IOException e) {
                LOGGER.error("error while calling CleanupProperties request", e);
            }
        }
    }

    /**
     * Currently the first argument: deviceIdentification, is not used yet,
     * because in all scenario created so far that make use of dymamic device
     * simulator properties, only one meter was read. In a future scenario's it
     * may be possible that in one request two (or more) meters should be read,
     * and that both meters should read their own set of dynamic properties. In
     * that case the deviceIdentification parameter can be used to make this
     * distinction.
     *
     * @param deviceIdentification
     * @param classId
     * @param obisCode
     * @param settings
     * @throws Throwable
     */
    @Given("^device simulation of \"([^\"]*)\" with classid (\\d+) obiscode \"([^\"]*)\" and attributes$")
    public void deviceSimulateWithClassidObiscodeAndAttributes(final String deviceIdentification, final int classId,
            final String obisCode, final Map<String, String> settings) throws Throwable {

        if (this.isBaseUrlEnabled()) {
            this.makeSetRemotePropertiesRequestUri(classId, obisCode, settings);
        }
    }

    /**
     * This method is added, so that all PR builds, still work, although the
     * module that should handle this trigger is not deployed. Once slim-975 is
     * deployed, this check can be removed. Note that in thar case the property
     * dynamic.properties.base.url should be enabled, in the properties file,
     * and a corresponding -D tag is added to the Jenkins jobs which run the
     * dlms cucumber tests.
     * 
     * @return
     */
    private boolean isBaseUrlEnabled() {
        return this.getBaseUrl() != null;
    }

    private void makeSetRemotePropertiesRequestUri(final int classId, final String obisCode,
            final Map<String, String> settings) {

        try (final CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            final String restGetUrl = this.makeRequest(classId, obisCode, settings);
            final HttpGet httpGetRequest = new HttpGet(restGetUrl);
            final HttpResponse httpResponse = httpClient.execute(httpGetRequest);
            final String msg = String.format("add device-simulator properties %s, status=", restGetUrl,
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
        return String.format(ADD_PROPS_REQUEST, this.getBaseUrl(), filename, props.toString());
    }

    private String getBaseUrl() {
        return this.dlmsSimulatorConfig.getDynamicPropertiesBaseUrl();
    }

}
