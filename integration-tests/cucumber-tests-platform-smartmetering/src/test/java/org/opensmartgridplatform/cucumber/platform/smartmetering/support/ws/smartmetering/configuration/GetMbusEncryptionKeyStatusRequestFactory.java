/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class GetMbusEncryptionKeyStatusRequestFactory {
  private GetMbusEncryptionKeyStatusRequestFactory() {
    // Private constructor for utility class
  }

  public static GetMbusEncryptionKeyStatusRequest fromParameterMap(
      final Map<String, String> requestParameters) {
    final GetMbusEncryptionKeyStatusRequest request = new GetMbusEncryptionKeyStatusRequest();
    request.setDeviceIdentification(
        requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));
    return request;
  }

  public static GetMbusEncryptionKeyStatusAsyncRequest fromScenarioContext() {
    final GetMbusEncryptionKeyStatusAsyncRequest asyncRequest =
        new GetMbusEncryptionKeyStatusAsyncRequest();
    asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    asyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return asyncRequest;
  }
}
