/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.protocol.iec60870.glue.hooks;

import org.opensmartgridplatform.cucumber.protocol.iec60870.database.Iec60870Database;
import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.Before;

public class ScenarioHooks {

    @Autowired
    private Iec60870Database database;

    @Before(order = 1000)
    public void beforeScenario() {
        this.database.prepareForScenario();
    }

}
