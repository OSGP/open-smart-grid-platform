package com.alliander.osgp.domain.core.valueobjects.smartmetering;

public abstract class MBusActionRequest implements ActionRequest {

    private static final long serialVersionUID = 2320272681064473304L;

    private String mBusDeviceIdentification;
    private Short channel;

    public MBusActionRequest(final String mBusDeviceIdentification) {
        this.mBusDeviceIdentification = mBusDeviceIdentification;
    }

    public String getMBusDeviceIdentification() {
        return this.mBusDeviceIdentification;
    }

    public Short getChannel() {
        return this.channel;
    }

    public void setChannel(final Short channel) {
        this.channel = channel;
    }

}
