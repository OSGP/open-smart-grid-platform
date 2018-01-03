/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.microgrids.glue.steps.database.ws;

import java.lang.reflect.Field;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.ws.domain.entities.ResponseData;
import com.alliander.osgp.adapter.ws.domain.repositories.ResponseDataRepository;
import com.alliander.osgp.cucumber.core.Helpers;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.glue.steps.database.core.BaseDeviceSteps;

import cucumber.api.java.en.Given;

public class ResponseDataSteps extends BaseDeviceSteps {

    @Autowired
    private ResponseDataRepository responseDataRespository;

    @Given("^a microgrids response data record$")
    @Transactional("txMgrWsMicrogrids")
    public ResponseData aResponseDataRecord(final Map<String, String> settings) throws Throwable {

        ResponseData responseData = new ResponseDataBuilder().fromSettings(settings).build();
        responseData = this.responseDataRespository.save(responseData);

        // set correct creation time for testing after inserting in the database
        // (as it will be overridden on first save)
        if (settings.containsKey(PlatformKeys.KEY_CREATION_TIME)) {
            final Field fld = responseData.getClass().getSuperclass().getDeclaredField("creationTime");
            fld.setAccessible(true);
            fld.set(responseData, Helpers.getDateTime(settings.get(PlatformKeys.KEY_CREATION_TIME)));
            this.responseDataRespository.save(responseData);
        }

        return responseData;
    }
}
