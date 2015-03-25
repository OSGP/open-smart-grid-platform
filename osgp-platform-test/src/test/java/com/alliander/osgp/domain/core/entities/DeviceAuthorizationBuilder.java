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
