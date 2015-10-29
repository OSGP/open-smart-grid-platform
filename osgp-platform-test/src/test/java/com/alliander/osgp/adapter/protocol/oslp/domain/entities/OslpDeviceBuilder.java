/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.domain.entities;

import java.net.InetAddress;

public class OslpDeviceBuilder {
    private String deviceUid;
    private String deviceIdentification;
    private String deviceType;
    private boolean activated;
    private boolean hasSchedule;
    private Integer sequenceNumber;
    private Integer randomDevice;
    private Integer randomPlatform;
    private String publicKey;

    public OslpDeviceBuilder() {
        this.deviceUid = null;
        this.deviceIdentification = null;
        this.deviceType = OslpDevice.PSLD_TYPE;
        this.activated = true;
        this.hasSchedule = true;
        this.sequenceNumber = 1;
        this.randomDevice = 1;
        this.randomPlatform = 1;
        this.publicKey = null;
    }

    public OslpDeviceBuilder withDeviceUid(final String deviceUid) {
        this.deviceUid = deviceUid;
        return this;
    }

    public OslpDeviceBuilder withDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
        return this;
    }

    public OslpDeviceBuilder withDeviceType(final String deviceType) {
        this.deviceType = deviceType;
        return this;
    }

    public OslpDeviceBuilder withSequenceNumber(final int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
        return this;
    }

    public OslpDeviceBuilder withRandomDevice(final int randomDevice) {
        this.randomDevice = randomDevice;
        return this;
    }

    public OslpDeviceBuilder withRandomPlatform(final int randomPlatform) {
        this.randomPlatform = randomPlatform;
        return this;
    }

    public OslpDeviceBuilder withPublicKey(final String publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public OslpDeviceBuilder withIsActivated(final boolean activated) {
        this.activated = activated;
        return this;
    }

    public OslpDeviceBuilder withHasSchedule(final boolean hasSchedule) {
        this.hasSchedule = hasSchedule;
        return this;
    }

    public OslpDevice build() {
        final OslpDevice device = new OslpDevice(this.deviceUid, this.deviceIdentification, this.deviceType,
                InetAddress.getLoopbackAddress(), this.activated, this.hasSchedule);
        device.setSequenceNumber(this.sequenceNumber);
        device.setRandomDevice(this.randomDevice);
        device.setRandomPlatform(this.randomPlatform);
        device.updatePublicKey(this.publicKey);
        return device;
    }
}
