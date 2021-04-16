/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsRequest;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class ActualMeterReadsRequestFactory {

  private ActualMeterReadsRequestFactory() {
    // Private constructor for utility class
  }

  public static ActualMeterReadsRequest fromParameterMap(
      final Map<String, String> requestParameters) {
    final ActualMeterReadsRequest actualMeterReadsRequest = new ActualMeterReadsRequest();
    actualMeterReadsRequest.setDeviceIdentification(
        requestParameters.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
    return actualMeterReadsRequest;
  }

  public static ActualMeterReadsAsyncRequest fromScenarioContext() {
    final ActualMeterReadsAsyncRequest actualMeterReadsAsyncRequest =
        new ActualMeterReadsAsyncRequest();
    actualMeterReadsAsyncRequest.setCorrelationUid(
        RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    actualMeterReadsAsyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return actualMeterReadsAsyncRequest;
  }
}
