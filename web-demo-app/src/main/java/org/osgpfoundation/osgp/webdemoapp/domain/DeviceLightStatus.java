package org.osgpfoundation.osgp.webdemoapp.domain;

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
