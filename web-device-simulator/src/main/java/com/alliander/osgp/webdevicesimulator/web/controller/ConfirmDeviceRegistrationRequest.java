package com.alliander.osgp.webdevicesimulator.web.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConfirmDeviceRegistrationRequest {
    private Long deviceId;

    public ConfirmDeviceRegistrationRequest(@JsonProperty("deviceId") final Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getDeviceId() {
        return this.deviceId;
    }
}
