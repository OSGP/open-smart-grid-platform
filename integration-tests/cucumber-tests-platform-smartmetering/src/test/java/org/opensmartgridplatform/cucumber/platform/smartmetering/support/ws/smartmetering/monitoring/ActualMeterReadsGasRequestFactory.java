/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasRequest;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class ActualMeterReadsGasRequestFactory {

  private ActualMeterReadsGasRequestFactory() {
    // Private constructor for utility class
  }

  public static ActualMeterReadsGasRequest fromParameterMap(final Map<String, String> settings) {
    final ActualMeterReadsGasRequest actualMeterReadsGasRequest = new ActualMeterReadsGasRequest();
    actualMeterReadsGasRequest.setDeviceIdentification(
        getString(
            settings,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformSmartmeteringDefaults.DEFAULT_SMART_METER_GAS_DEVICE_IDENTIFICATION));
    return actualMeterReadsGasRequest;
  }

  public static ActualMeterReadsGasAsyncRequest fromScenarioContext() {
    final ActualMeterReadsGasAsyncRequest actualMeterReadsGasAsyncRequest =
        new ActualMeterReadsGasAsyncRequest();
    actualMeterReadsGasAsyncRequest.setCorrelationUid(
        RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    actualMeterReadsGasAsyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return actualMeterReadsGasAsyncRequest;
  }
}
