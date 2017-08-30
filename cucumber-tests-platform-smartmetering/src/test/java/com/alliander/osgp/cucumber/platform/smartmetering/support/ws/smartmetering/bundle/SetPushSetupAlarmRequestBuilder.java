/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.SetPushSetupAlarmRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.PushSetupAlarm;
import com.alliander.osgp.cucumber.core.Helpers;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class SetPushSetupAlarmRequestBuilder {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 9598;

    private String host;
    private BigInteger port;

    public SetPushSetupAlarmRequestBuilder withDefaults() {
        return this.fromParameterMap(Collections.emptyMap());
    }

    public SetPushSetupAlarmRequestBuilder fromParameterMap(final Map<String, String> parameters) {
        this.host = this.getHost(parameters);
        this.port = this.getPort(parameters);
        return this;
    }

    public SetPushSetupAlarmRequest build() {
        final SetPushSetupAlarmRequest request = new SetPushSetupAlarmRequest();
        final PushSetupAlarm pushSetupAlarm = new PushSetupAlarm();
        pushSetupAlarm.setHost(this.host);
        pushSetupAlarm.setPort(this.port);
        request.setPushSetupAlarm(pushSetupAlarm);
        return request;
    }

    private String getHost(final Map<String, String> parameters) {
        return Helpers.getString(parameters, PlatformSmartmeteringKeys.HOSTNAME, DEFAULT_HOST);
    }

    private BigInteger getPort(final Map<String, String> parameters) {
        return BigInteger.valueOf(Helpers.getInteger(parameters, PlatformSmartmeteringKeys.PORT, DEFAULT_PORT));
    }
}
