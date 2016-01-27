package org.osgp.adapter.protocol.dlms.application.models;

public class ProtocolMeterInfo {
    private int channel;
    private String deviceIdentification;
    private String encryptionKey;

    public ProtocolMeterInfo(final int channel, final String deviceIdentification, final String encryptionKey) {
        this.channel = channel;
        this.deviceIdentification = deviceIdentification;
        this.encryptionKey = encryptionKey;
    }

    public int getChannel() {
        return this.channel;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public String getEncryptionKey() {
        return this.encryptionKey;
    }

}
