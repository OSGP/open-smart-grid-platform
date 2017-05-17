/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.ws;

import java.util.Map;

import com.alliander.osgp.cucumber.core.GlueBase;

import cucumber.api.java.en.Then;

public class FaultSteps extends GlueBase {

    @Then("^a SOAP fault should have been returned$")
    public void aSoapFaultShouldHaveBeenReturned(final Map<String, String> responseParameters) throws Throwable {

        GenericResponseSteps.verifySoapFault(responseParameters);
    }
}
