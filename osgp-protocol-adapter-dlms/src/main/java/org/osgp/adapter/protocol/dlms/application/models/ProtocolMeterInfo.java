/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.models;

public class ProtocolMeterInfo {
    private int channel;
    private String deviceIdentification;
    private String encryptionKey;
    private String masterKey;

    public ProtocolMeterInfo(final int channel, final String deviceIdentification, final String encryptionKey,
            final String masterKey) {
        this.channel = channel;
        this.deviceIdentification = deviceIdentification;
        this.encryptionKey = encryptionKey;
        this.masterKey = masterKey;
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

    public String getMasterKey() {
        return this.masterKey;
    }

}
