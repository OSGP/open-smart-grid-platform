/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class GetAdministrativeStatusRequestFactory {
  private GetAdministrativeStatusRequestFactory() {
    // Private constructor for utility class
  }

  public static GetAdministrativeStatusRequest fromParameterMap(
      final Map<String, String> requestParameters) {
    final GetAdministrativeStatusRequest getAdministrativeStatusRequest =
        new GetAdministrativeStatusRequest();
    getAdministrativeStatusRequest.setDeviceIdentification(
        requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));
    return getAdministrativeStatusRequest;
  }

  public static GetAdministrativeStatusAsyncRequest fromScenarioContext() {
    final GetAdministrativeStatusAsyncRequest getAdministrativeStatusAsyncRequest =
        new GetAdministrativeStatusAsyncRequest();
    getAdministrativeStatusAsyncRequest.setCorrelationUid(
        RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    getAdministrativeStatusAsyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return getAdministrativeStatusAsyncRequest;
  }
}
