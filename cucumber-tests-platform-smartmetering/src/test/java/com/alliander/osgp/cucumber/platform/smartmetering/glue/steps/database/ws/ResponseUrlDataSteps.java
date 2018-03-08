/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.database.ws;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.smartmetering.domain.entities.ResponseUrlData;
import com.alliander.osgp.adapter.ws.smartmetering.domain.repositories.ResponseUrlDataRepository;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformKeys;

import cucumber.api.java.en.Given;

public class ResponseUrlDataSteps {

    @Autowired
    private ResponseUrlDataRepository responseUrlDataRespository;

    @Given("^a response url data record$")
    public void aResponseUrlDataRecord(final Map<String, String> settings) throws Throwable {
        final ResponseUrlData responseUrlData = this.responseUrlDataRespository
                .save(new ResponseUrlDataBuilder().fromSettings(settings).build());

        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, responseUrlData.getCorrelationUid());
    }

}
