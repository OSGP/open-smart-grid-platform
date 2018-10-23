/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdemoapp.domain;

/**
 *
 * Domain class used in the web demo app to hold basic light data.
 *
 */
public class DeviceLightStatus {

    private String deviceId;

    private int lightValue;

    private boolean lightOn;

    public String getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
    }



    public int getLightValue() {
        return this.lightValue;
    }

    public void setLightValue(final int lightValue) {
        this.lightValue = lightValue;
    }

    public boolean isLightOn() {
        return this.lightOn;
    }

    public void setLightOn(final boolean lightOn) {
        this.lightOn = lightOn;
    }

}
