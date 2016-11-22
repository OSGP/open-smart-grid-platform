/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.steps.database.device;

import static com.alliander.osgp.platform.cucumber.steps.Defaults.SMART_METER_E;
import static com.alliander.osgp.platform.cucumber.steps.Defaults.SMART_METER_G;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.core.Helpers;
import com.alliander.osgp.platform.cucumber.helpers.Protocol;
import com.alliander.osgp.platform.cucumber.helpers.ProtocolHelper;
import com.alliander.osgp.platform.cucumber.steps.database.core.SmartMeterSteps;
import com.alliander.osgp.platform.dlms.cucumber.steps.Keys;
import com.alliander.osgp.platform.dlms.cucumber.steps.ws.smartmetering.smartmeteringbundle.Bundle;

import cucumber.api.java.en.Given;

/**
 * DLMS device specific steps.
 */
public abstract class DlmsDeviceStepsImpl extends DlmsDeviceSteps {

    @Autowired
    private SmartMeterSteps smartMeterSteps;

    @Autowired
    private com.alliander.osgp.platform.cucumber.steps.database.adapterprotocoldlms.DlmsDeviceSteps repoHelper;

    private static final Logger LOGGER = LoggerFactory.getLogger(Bundle.class);

    @Given("^a dlms device$")
    public void aDlmsDevice(final Map<String, String> inputSettings) throws Throwable {

        if (this.isSmartMeter(inputSettings)) {
            this.createDlmsDevice(inputSettings);
            }
        else {
            LOGGER.error("The following DLMS device input parameters are not present: ", Keys.KEY_DEVICE_IDENTIFICATION,
                    Keys.KEY_DEVICE_TYPE);
        }
    }

    private boolean isSmartMeter(final Map<String, String> settings) {
        final String deviceId = settings.get(Keys.KEY_DEVICE_IDENTIFICATION);
        final String deviceType = settings.get(Keys.KEY_DEVICE_TYPE);
        return (SMART_METER_E.equals(deviceType) || SMART_METER_G.equals(deviceType)) && deviceId != null;
    }

}
