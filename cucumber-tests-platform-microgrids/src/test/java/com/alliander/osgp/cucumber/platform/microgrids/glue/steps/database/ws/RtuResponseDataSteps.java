/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.microgrids.glue.steps.database.ws;

import java.util.Date;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.ws.microgrids.domain.entities.RtuResponseData;
import com.alliander.osgp.adapter.ws.microgrids.domain.entities.RtuResponseDataBuilder;
import com.alliander.osgp.adapter.ws.microgrids.domain.repositories.RtuResponseDataRepository;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.glue.steps.database.core.BaseDeviceSteps;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

import cucumber.api.java.en.Given;

public class RtuResponseDataSteps extends BaseDeviceSteps {

    @Autowired
    private RtuResponseDataRepository rtuResponseDataRespository;

    @Given("^an rtu response data record$")
    @Transactional("txMgrWsMicrogrids")
    public RtuResponseData anRtuResponseDataRecord(final Map<String, String> settings) throws Throwable {

        RtuResponseData rtuResponseData = this.fromSettings(settings);
        rtuResponseData = this.rtuResponseDataRespository.save(rtuResponseData);

        // set correct creation time for testing after inserting in the database
        // (as it will be overridden on first save)
        if (settings.containsKey(PlatformKeys.KEY_CREATION_TIME)) {
            final RtuResponseDataBuilder builder = new RtuResponseDataBuilder();
            rtuResponseData = builder.updateCreationTime(rtuResponseData,
                    this.parseCreationTime(settings.get(PlatformKeys.KEY_CREATION_TIME)));
            this.rtuResponseDataRespository.save(rtuResponseData);
        }

        return rtuResponseData;
    }

    private RtuResponseData fromSettings(final Map<String, String> settings) {
        RtuResponseDataBuilder builder = new RtuResponseDataBuilder();
        if (settings.containsKey(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION)) {
            builder = builder
                    .withOrganisationIdentification(settings.get(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION));
        }
        if (settings.containsKey(PlatformKeys.KEY_DEVICE_IDENTIFICATION)) {
            builder = builder.withDeviceIdentification(settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
        }
        if (settings.containsKey(PlatformKeys.KEY_CORRELATION_UID)) {
            builder = builder.withCorrelationUid(settings.get(PlatformKeys.KEY_CORRELATION_UID));
        }
        if (settings.containsKey(PlatformKeys.KEY_MESSAGE_TYPE)) {
            builder = builder.withMessageType(settings.get(PlatformKeys.KEY_MESSAGE_TYPE));
        }
        if (settings.containsKey(PlatformKeys.KEY_MESSAGE_DATA)) {
            builder = builder.withMessageData(settings.get(PlatformKeys.KEY_MESSAGE_DATA));
        }
        if (settings.containsKey(PlatformKeys.KEY_RESULT_TYPE)) {
            builder = builder
                    .withResultType(ResponseMessageResultType.valueOf(settings.get(PlatformKeys.KEY_RESULT_TYPE)));
        }
        return builder.build();
    }

    private Date parseCreationTime(final String creationTime) {
        if ("NOW".equalsIgnoreCase(creationTime)) {
            return DateTime.now().toDate();
        } else {
            return DateTime.parse(creationTime).toDate();
        }
    }

}
