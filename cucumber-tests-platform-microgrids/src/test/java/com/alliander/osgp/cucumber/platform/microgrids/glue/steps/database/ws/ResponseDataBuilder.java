/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.microgrids.glue.steps.database.ws;

import java.io.Serializable;
import java.util.Map;

import com.alliander.osgp.adapter.ws.domain.entities.ResponseData;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

public class ResponseDataBuilder {

    private String organisationIdentification = "test-org";
    private String deviceIdentification = "test-rtu";
    private String correlationUid = "test-org|||test-rtu|||20170101000000000";
    private String messageType = "GET_DATA";
    private Serializable messageData = null;
    private ResponseMessageResultType resultType = ResponseMessageResultType.OK;

    public ResponseData build() {
        return new ResponseData(this.organisationIdentification, this.messageType, this.deviceIdentification,
                this.correlationUid, this.resultType, this.messageData);
    }

    public ResponseDataBuilder fromSettings(final Map<String, String> settings) {
        if (settings.containsKey(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION)) {
            this.withOrganisationIdentification(settings.get(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION));
        }
        if (settings.containsKey(PlatformKeys.KEY_DEVICE_IDENTIFICATION)) {
            this.withDeviceIdentification(settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
        }
        if (settings.containsKey(PlatformKeys.KEY_CORRELATION_UID)) {
            this.withCorrelationUid(settings.get(PlatformKeys.KEY_CORRELATION_UID));
        }
        if (settings.containsKey(PlatformKeys.KEY_MESSAGE_TYPE)) {
            this.withMessageType(settings.get(PlatformKeys.KEY_MESSAGE_TYPE));
        }
        if (settings.containsKey(PlatformKeys.KEY_MESSAGE_DATA)) {
            this.withMessageData(settings.get(PlatformKeys.KEY_MESSAGE_DATA));
        }
        if (settings.containsKey(PlatformKeys.KEY_RESULT_TYPE)) {
            this.withResultType(ResponseMessageResultType.valueOf(settings.get(PlatformKeys.KEY_RESULT_TYPE)));
        }
        return this;
    }

    public ResponseDataBuilder withOrganisationIdentification(final String organisationIdentification) {
        this.organisationIdentification = organisationIdentification;
        return this;
    }

    public ResponseDataBuilder withDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
        return this;
    }

    public ResponseDataBuilder withCorrelationUid(final String correlationUid) {
        this.correlationUid = correlationUid;
        return this;
    }

    public ResponseDataBuilder withMessageType(final String messageType) {
        this.messageType = messageType;
        return this;
    }

    public ResponseDataBuilder withMessageData(final Serializable messageData) {
        this.messageData = messageData;
        return this;
    }

    public ResponseDataBuilder withResultType(final ResponseMessageResultType resultType) {
        this.resultType = resultType;
        return this;
    }
}
