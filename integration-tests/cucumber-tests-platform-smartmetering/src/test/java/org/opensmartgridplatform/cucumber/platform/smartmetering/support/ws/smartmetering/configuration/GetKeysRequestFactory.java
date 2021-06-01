/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetKeysAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetKeysRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetKeysRequestData;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SecretType;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class GetKeysRequestFactory {

  private GetKeysRequestFactory() {
    // Private constructor for utility class
  }

  public static GetKeysRequest fromParameterMap(final Map<String, String> settings) {
    final GetKeysRequest request = new GetKeysRequest();
    final GetKeysRequestData requestData = new GetKeysRequestData();
    final List<String> secretTypesAsString =
        Arrays.asList(getString(settings, "SecretTypes").split(","));
    requestData
        .getSecretTypes()
        .addAll(secretTypesAsString.stream().map(SecretType::valueOf).collect(Collectors.toList()));
    request.setDeviceIdentification(settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
    request.setGetKeysData(requestData);
    return request;
  }

  public static GetKeysAsyncRequest fromScenarioContext() {
    final GetKeysAsyncRequest asyncRequest = new GetKeysAsyncRequest();
    asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    asyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return asyncRequest;
  }
}
