/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupUdpAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupUdpRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class SetPushSetupUdpRequestFactory {

  private SetPushSetupUdpRequestFactory() {
    // Private constructor for utility class
  }

  public static SetPushSetupUdpRequest fromParameterMap(final Map<String, String> settings) {
    final SetPushSetupUdpRequest request = new SetPushSetupUdpRequest();
    request.setDeviceIdentification(
        settings.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));

    return request;
  }

  public static SetPushSetupUdpAsyncRequest fromScenarioContext() {
    final SetPushSetupUdpAsyncRequest asyncRequest = new SetPushSetupUdpAsyncRequest();
    asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    asyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return asyncRequest;
  }
}
