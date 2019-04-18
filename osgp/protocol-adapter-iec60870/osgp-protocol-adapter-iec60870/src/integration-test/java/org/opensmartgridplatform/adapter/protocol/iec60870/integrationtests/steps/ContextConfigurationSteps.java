/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.steps;

import org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.TestConfiguration;
import org.springframework.test.context.ContextConfiguration;

import cucumber.api.java.Before;

@ContextConfiguration(classes = { TestConfiguration.class })
public class ContextConfigurationSteps {

    @Before
    public void loadContext() {
        // dummy method with cucumber Before annotation to make sure the test
        // configuration as given in the ContextConfiguration annotation is
        // loaded
    }
}
