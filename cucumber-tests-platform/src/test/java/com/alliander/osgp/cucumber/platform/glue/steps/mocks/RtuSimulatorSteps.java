/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.mocks;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.cucumber.platform.GlueBase;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.mocks.iec61850.Iec61850MockServer;
import com.alliander.osgp.cucumber.platform.mocks.iec61850.Iec61850MockServerMarkerWadden;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class RtuSimulatorSteps extends GlueBase {

    private static final int INDEX_LOGICAL_DEVICE_NAME = 0;
    private static final int INDEX_NODE_NAME = 1;
    private static final int INDEX_NODE_VALUE = 2;
    private static final int NUMBER_OF_INPUTS_FOR_MOCK_VALUE = 3;

    @Autowired
    private Iec61850MockServer mockServer;

    @Autowired
    private Iec61850MockServerMarkerWadden mockServerMarkerWadden;

    @Given("^an rtu simulator returning$")
    public void anRtuSimulatorReturning(final List<List<String>> mockValues) throws Throwable {
        for (final List<String> mockValue : mockValues) {
            if (NUMBER_OF_INPUTS_FOR_MOCK_VALUE != mockValue.size()) {
                throw new AssertionError("Mock value input rows from the Step DataTable must have "
                        + NUMBER_OF_INPUTS_FOR_MOCK_VALUE + " elements.");
            }
            final String logicalDeviceName = mockValue.get(INDEX_LOGICAL_DEVICE_NAME);
            final String node = mockValue.get(INDEX_NODE_NAME);
            final String value = mockValue.get(INDEX_NODE_VALUE);
            this.mockServer.mockValue(logicalDeviceName, node, value);
        }
    }

    /**
     * If this method is user, then (in order to correctly start a different
     * RtuSimulator), these setting must be supplied in the feature file:
     * ServerName, IcdFilename and Port In addition, the Port should be unique,
     * and the RTU device should unique as well because the platform caches the
     * server based on the RTU device name!
     *
     * @param settings
     * @throws Throwable
     */
    @Given("^an rtu simulator started with$")
    public void anRtuSimulatorStartedWithSettings(final Map<String, String> settings) throws Throwable {
        this.validateRtuSimulatorSettings(settings);
        final String givenServerName = getString(settings, Keys.KEY_IEC61850_SERVERNAME);
        final String givenIcdFilename = getString(settings, Keys.KEY_IEC61850_ICD_FILENAME);
        final String givenPort = getString(settings, Keys.KEY_IEC61850_PORT);

        this.mockServer = new Iec61850MockServer(givenServerName, givenIcdFilename, Integer.valueOf(givenPort));
        this.mockServer.restart(settings);
    }

    @Then("^the rtu simulator should contain$")
    public void theRtuSimulatorShouldContain(final List<List<String>> mockValues) throws Throwable {
        for (final List<String> mockValue : mockValues) {
            if (NUMBER_OF_INPUTS_FOR_MOCK_VALUE != mockValue.size()) {
                throw new AssertionError("Mock value input rows from the Step DataTable must have "
                        + NUMBER_OF_INPUTS_FOR_MOCK_VALUE + " elements.");
            }
            final String logicalDeviceName = mockValue.get(INDEX_LOGICAL_DEVICE_NAME);
            final String node = mockValue.get(INDEX_NODE_NAME);
            final String value = mockValue.get(INDEX_NODE_VALUE);
            this.mockServer.assertValue(logicalDeviceName, node, value);
        }
    }

    @Then("^the Marker Wadden RTU simulator should contain$")
    public void theMarkerWaddenRTUSimulatorShouldContain(final List<List<String>> mockValues) throws Throwable {
        for (final List<String> mockValue : mockValues) {
            if (NUMBER_OF_INPUTS_FOR_MOCK_VALUE != mockValue.size()) {
                throw new AssertionError("Mock value input rows from the Step DataTable must have "
                        + NUMBER_OF_INPUTS_FOR_MOCK_VALUE + " elements.");
            }
            final String logicalDeviceName = mockValue.get(INDEX_LOGICAL_DEVICE_NAME);
            final String node = mockValue.get(INDEX_NODE_NAME);
            final String value = mockValue.get(INDEX_NODE_VALUE);
            this.mockServerMarkerWadden.assertValue(logicalDeviceName, node, value);
        }
    }

    private void validateRtuSimulatorSettings(final Map<String, String> settings) {
        final String givenIcdFilename = getString(settings, Keys.KEY_IEC61850_ICD_FILENAME);
        final String givenServerName = getString(settings, Keys.KEY_IEC61850_SERVERNAME);
        final String givenPort = getString(settings, Keys.KEY_IEC61850_PORT);
        Assert.assertNotNull("For another RtuSimuler the ServerName must be given", givenServerName);
        Assert.assertNotNull("For another RtuSimuler the IcdFilename must be given", givenIcdFilename);
        Assert.assertNotNull("For another RtuSimuler the Port must be given", givenPort);
    }
}
