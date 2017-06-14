/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.common;

import com.alliander.osgp.cucumber.core.GlueBase;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformKeys;

import cucumber.api.java.en.Given;

public class TimeoutSteps extends GlueBase {

    @Given("^a timeout of \"([^\"]*)\" seconds$")
    public void aTimeoutOfSeconds(final String seconds) {
        ScenarioContext.current().put(PlatformKeys.TIMEOUT, seconds);
    }

}
