/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasRequest;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class PeriodicMeterReadsGasRequestFactory {

    public static PeriodicMeterReadsGasRequest fromParameterMap(final Map<String, String> requestParameters) {

        final PeriodicMeterReadsGasRequest periodicMeterReadsGasRequest = new PeriodicMeterReadsGasRequest();

        periodicMeterReadsGasRequest
                .setDeviceIdentification(getString(requestParameters, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                        PlatformSmartmeteringDefaults.DEFAULT_SMART_METER_GAS_DEVICE_IDENTIFICATION));
        periodicMeterReadsGasRequest
                .setPeriodicReadsRequestData(PeriodicReadsRequestDataFactory.fromParameterMap(requestParameters));

        return periodicMeterReadsGasRequest;
    }

    public static PeriodicMeterReadsGasAsyncRequest fromScenarioContext() {
        final PeriodicMeterReadsGasAsyncRequest periodicMeterReadsGasAsyncRequest = new PeriodicMeterReadsGasAsyncRequest();
        periodicMeterReadsGasAsyncRequest
                .setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
        periodicMeterReadsGasAsyncRequest
                .setDeviceIdentification(RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
        return periodicMeterReadsGasAsyncRequest;
    }
}
