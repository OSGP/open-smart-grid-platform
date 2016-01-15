/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.entities;

import java.net.InetAddress;

public class DeviceBuilder {
    private Long deviceId;
    private String deviceIdentification;
    private String deviceType;
    private InetAddress networkAddress;
    private boolean activated;
    private boolean hasSchedule;
    private Float gpsLatitude;
    private Float gpsLongitude;
    private boolean publicKeyPresent;
    private ProtocolInfo protocolInfo;

    public DeviceBuilder() {
        this.deviceId = 1L;
        this.deviceType = "PSLD";
        this.hasSchedule = false;
        this.activated = false;
        this.gpsLatitude = null;
        this.gpsLongitude = null;
        this.publicKeyPresent = false;
        this.protocolInfo = null;
    }

    public DeviceBuilder withId(final Long id) {
        this.deviceId = id;
        return this;
    }

    public DeviceBuilder withDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
        return this;
    }

    public DeviceBuilder ofDeviceType(final String deviceType) {
        this.deviceType = deviceType;
        return this;
    }

    public DeviceBuilder withNetworkAddress(final InetAddress networkAddress) {
        this.networkAddress = networkAddress;
        return this;
    }

    public DeviceBuilder isActivated(final boolean activated) {
        this.activated = activated;
        return this;
    }

    public DeviceBuilder hasSchedule(final boolean hasSchedule) {
        this.hasSchedule = hasSchedule;
        return this;
    }

    public DeviceBuilder withGps(final Float latitude, final Float longitude) {
        this.gpsLatitude = latitude;
        this.gpsLongitude = longitude;
        return this;
    }

    public DeviceBuilder withPublicKeyPresent(final boolean publicKeyPresent) {
        this.publicKeyPresent = publicKeyPresent;
        return this;
    }

    public DeviceBuilder withProtocolInfo(final ProtocolInfo protocolInfo) {
        this.protocolInfo = protocolInfo;
        return this;
    }

    public Device build() {
        final Ssld ssld = new Ssld(this.deviceIdentification, this.deviceType, this.networkAddress, this.activated,
                this.hasSchedule);
        ssld.setId(this.deviceId);
        ssld.updateMetaData(null, null, null, null, null, null, this.gpsLatitude, this.gpsLongitude);
        ssld.updateProtocol(this.protocolInfo);
        ssld.setPublicKeyPresent(this.publicKeyPresent);
        return ssld;
    }
}