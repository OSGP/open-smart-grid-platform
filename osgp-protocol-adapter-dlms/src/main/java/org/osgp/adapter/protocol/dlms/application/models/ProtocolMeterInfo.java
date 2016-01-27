package org.osgp.adapter.protocol.dlms.application.models;

public class ProtocolMeterInfo {
    private int channel;
    private String deviceIdentification;
    private String masterKey;
    private String encryptionKey;

    public ProtocolMeterInfo(final int channel, final String deviceIdentification, final String masterKey,
            final String encryptionKey) {
        this.channel = channel;
        this.deviceIdentification = deviceIdentification;
        this.masterKey = masterKey;
        this.encryptionKey = encryptionKey;
    }

    public int getChannel() {
        return this.channel;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public String getMasterKey() {
        return this.masterKey;
    }

    public String getEncryptionKey() {
        return this.encryptionKey;
    }

}
