/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.steps;

import static org.opensmartgridplatform.cucumber.platform.distributionautomation.PlatformDistributionAutomationKeys.INFORMATION_ELEMENT_VALUE;
import static org.opensmartgridplatform.cucumber.platform.distributionautomation.PlatformDistributionAutomationKeys.INFORMATION_OBJECT_ADDRESS;
import static org.opensmartgridplatform.cucumber.platform.distributionautomation.PlatformDistributionAutomationKeys.INFORMATION_OBJECT_TYPE;

import java.util.Map;

import org.opensmartgridplatform.cucumber.platform.distributionautomation.mocks.iec60870.Iec60870MockServer;
import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.When;

public class ProcessImageSteps {

    @Autowired
    private Iec60870MockServer mockServer;

    @When("I update the information object")
    public void iUpdateTheInformationObject(final Map<String, String> parameters) {
        final Integer informationObjectAddress = Integer.valueOf(parameters.get(INFORMATION_OBJECT_ADDRESS));
        final String informationObjectType = parameters.get(INFORMATION_OBJECT_TYPE);
        final Float value = Float.valueOf(parameters.get(INFORMATION_ELEMENT_VALUE));
        this.mockServer.getRtuSimulator()
                .updateInformationObject(informationObjectAddress, informationObjectType, value);
    }

}
