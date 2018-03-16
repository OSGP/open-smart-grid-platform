/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.DeviceLifecycleStatus;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;

public class SetDeviceLifecycleStatusByChannelRequestData implements ActionRequest {

    private static final long serialVersionUID = 3636769765482239443L;

    private final DeviceLifecycleStatus deviceLifecycleStatus;
    private final short channel;

    public SetDeviceLifecycleStatusByChannelRequestData(final short channel,
            final DeviceLifecycleStatus deviceLifecycleStatus) {
        this.deviceLifecycleStatus = deviceLifecycleStatus;
        this.channel = channel;
    }

    public short getChannel() {
        return this.channel;
    }

    public DeviceLifecycleStatus getDeviceLifecycleStatus() {
        return this.deviceLifecycleStatus;
    }

    @Override
    public void validate() throws FunctionalException {
        // nothing to validate
    }

    @Override
    public DeviceFunction getDeviceFunction() {
        return DeviceFunction.SET_DEVICE_LIFECYCLE_STATUS_BY_CHANNEL;
    }

}
