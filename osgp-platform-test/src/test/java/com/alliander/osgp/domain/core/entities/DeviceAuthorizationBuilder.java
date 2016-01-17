/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.entities;

import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;

public class DeviceAuthorizationBuilder {
    private Device device;
    private Organisation organisation;
    private DeviceFunctionGroup functionGroup;

    public DeviceAuthorizationBuilder() {

    }

    public DeviceAuthorizationBuilder withDevice(final Device device) {
        this.device = device;
        return this;
    }

    public DeviceAuthorizationBuilder withOrganisation(final Organisation organisation) {
        this.organisation = organisation;
        return this;
    }

    public DeviceAuthorizationBuilder withFunctionGroup(final DeviceFunctionGroup functionGroup) {
        this.functionGroup = functionGroup;
        return this;
    }

    public DeviceAuthorization build() {
        return new DeviceAuthorization(this.device, this.organisation, this.functionGroup);
    }
}
