/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.webdevicesimulator.web.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SendEventNotificationRequest {
    private Long deviceId;
    private Integer event;
    private String description;
    private Integer index;

    @JsonCreator
    public SendEventNotificationRequest(@JsonProperty("deviceId") final Long deviceId,
            @JsonProperty("event") final Integer event, @JsonProperty("description") final String description,
            @JsonProperty("index") final Integer index) {
        this.deviceId = deviceId;
        this.event = event;
        this.description = description;
        this.index = index;
    }

    public Long getDeviceId() {
        return this.deviceId;
    }

    public Integer getEvent() {
        return this.event;
    }

    public String getDescription() {
        return this.description;
    }

    public Integer getIndex() {
        return this.index;
    }
}
