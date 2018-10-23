/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.database.ws;

import java.util.Map;

import org.opensmartgridplatform.adapter.ws.smartmetering.domain.entities.ResponseUrlData;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;

public class ResponseUrlDataBuilder {

    private String correlationUid = "test-org|||TEST1024000000001|||20170101000000000";
    private String responseUrl = "http://localhost:8843/notifications";

    public ResponseUrlData build() {
        return new ResponseUrlData(this.correlationUid, this.responseUrl);
    }

    public ResponseUrlDataBuilder fromSettings(final Map<String, String> settings) {
        if (settings.containsKey(PlatformKeys.KEY_CORRELATION_UID)) {
            this.withCorrelationUid(settings.get(PlatformKeys.KEY_CORRELATION_UID));
        }
        if (settings.containsKey(PlatformKeys.KEY_RESPONSE_URL)) {
            this.withResponseUrl(settings.get(PlatformKeys.KEY_RESPONSE_URL));
        }
        return this;
    }

    public ResponseUrlDataBuilder withCorrelationUid(final String correlationUid) {
        this.correlationUid = correlationUid;
        return this;
    }

    public ResponseUrlDataBuilder withResponseUrl(final String responseUrl) {
        this.responseUrl = responseUrl;
        return this;
    }

}
