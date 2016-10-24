/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;

public class SetPushSetupSmsRequestData implements ActionRequest {

    private static final long serialVersionUID = 6093319027662713873L;

    private final PushSetupSms pushSetupSms;

    public SetPushSetupSmsRequestData(final PushSetupSms pushSetupSms) {
        this.pushSetupSms = pushSetupSms;
    }

    public PushSetupSms getPushSetupSms() {
        return this.pushSetupSms;
    }

    @Override
    public void validate() throws FunctionalException {
        // No validation needed

    }

    @Override
    public DeviceFunction getDeviceFunction() {
        return DeviceFunction.SET_PUSH_SETUP_SMS;
    }
}