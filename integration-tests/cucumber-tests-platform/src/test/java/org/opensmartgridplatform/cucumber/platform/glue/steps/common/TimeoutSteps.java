/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.glue.steps.common;

import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;

import io.cucumber.java.en.Given;

public class TimeoutSteps {

    @Given("^a timeout of \"([^\"]*)\" seconds$")
    public void aTimeoutOfSeconds(final String seconds) {
        ScenarioContext.current().put(PlatformKeys.TIMEOUT, seconds);
    }

}
