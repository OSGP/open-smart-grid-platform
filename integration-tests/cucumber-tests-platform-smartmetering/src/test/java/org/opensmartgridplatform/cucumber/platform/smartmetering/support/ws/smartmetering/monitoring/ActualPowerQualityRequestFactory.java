/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualPowerQualityAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualPowerQualityRequest;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class ActualPowerQualityRequestFactory {

  private ActualPowerQualityRequestFactory() {
    // Private constructor for utility class
  }

  public static ActualPowerQualityRequest fromParameterMap(
      final Map<String, String> requestParameters) {
    final String profileType =
        getString(requestParameters, PlatformKeys.KEY_POWER_QUALITY_PROFILE_TYPE);

    final ActualPowerQualityRequest actualPowerQualityRequest = new ActualPowerQualityRequest();
    actualPowerQualityRequest.setDeviceIdentification(
        requestParameters.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
    actualPowerQualityRequest.setProfileType(profileType);
    return actualPowerQualityRequest;
  }

  public static ActualPowerQualityAsyncRequest fromScenarioContext() {
    final ActualPowerQualityAsyncRequest actualPowerQualityAsyncRequest =
        new ActualPowerQualityAsyncRequest();
    actualPowerQualityAsyncRequest.setCorrelationUid(
        RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    actualPowerQualityAsyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return actualPowerQualityAsyncRequest;
  }
}
