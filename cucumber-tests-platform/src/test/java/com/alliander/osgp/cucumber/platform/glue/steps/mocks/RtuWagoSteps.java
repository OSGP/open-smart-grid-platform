/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.mocks;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.cucumber.platform.GlueBase;
import com.alliander.osgp.cucumber.platform.mocks.iec61850.Iec61850MockServer;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class RtuWagoSteps extends GlueBase {

    private static final int INDEX_LOGICAL_DEVICE_NAME = 0;
    private static final int INDEX_NODE_NAME = 1;
    private static final int INDEX_NODE_VALUE = 2;
    private static final int NUMBER_OF_INPUTS_FOR_MOCK_VALUE = 3;

    @Autowired
    private Iec61850MockServer iec61850MockServerWago;

    @Given("^the WAGO RTU returning$")
    public void theWagoRtuReturning(final List<List<String>> mockValues) throws Throwable {
        for (final List<String> mockValue : mockValues) {
            if (NUMBER_OF_INPUTS_FOR_MOCK_VALUE != mockValue.size()) {
                throw new AssertionError("Mock value input rows from the Step DataTable must have "
                        + NUMBER_OF_INPUTS_FOR_MOCK_VALUE + " elements.");
            }
            final String logicalDeviceName = mockValue.get(INDEX_LOGICAL_DEVICE_NAME);
            final String node = mockValue.get(INDEX_NODE_NAME);
            final String value = mockValue.get(INDEX_NODE_VALUE);

            this.iec61850MockServerWago.mockValue(logicalDeviceName, node, value);
        }
    }

    @Then("^the WAGO RTU should contain$")
    public void theWagoRtuShouldContain(final List<List<String>> mockValues) throws Throwable {
        for (final List<String> mockValue : mockValues) {
            if (NUMBER_OF_INPUTS_FOR_MOCK_VALUE != mockValue.size()) {
                throw new AssertionError("Mock value input rows from the Step DataTable must have "
                        + NUMBER_OF_INPUTS_FOR_MOCK_VALUE + " elements.");
            }
            final String logicalDeviceName = mockValue.get(INDEX_LOGICAL_DEVICE_NAME);
            final String node = mockValue.get(INDEX_NODE_NAME);
            final String value = mockValue.get(INDEX_NODE_VALUE);
            this.iec61850MockServerWago.assertValue(logicalDeviceName, node, value);
        }
    }
}
