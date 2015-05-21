/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.core.db.api.domain.entities;

import com.alliander.osgp.core.db.api.entities.Device;

public class DeviceDataBuilder {
    private String deviceIdentification;
    private Float gpsLatitude;
    private Float gpsLongitude;

    public DeviceDataBuilder() {
        this.gpsLatitude = null;
        this.gpsLongitude = null;
    }

    public DeviceDataBuilder withDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
        return this;
    }

    public DeviceDataBuilder withGps(final Float latitude, final Float longitude) {
        this.gpsLatitude = latitude;
        this.gpsLongitude = longitude;

        return this;
    }

    public Device build() {
        final Device device = new Device(this.deviceIdentification, this.gpsLatitude, this.gpsLongitude);

        return device;
    }
}