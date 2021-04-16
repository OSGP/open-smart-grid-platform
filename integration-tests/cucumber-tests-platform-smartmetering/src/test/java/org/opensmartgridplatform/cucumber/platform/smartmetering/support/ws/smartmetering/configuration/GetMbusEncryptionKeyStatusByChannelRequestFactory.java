/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusByChannelAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusByChannelRequestData;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class GetMbusEncryptionKeyStatusByChannelRequestFactory {

  private GetMbusEncryptionKeyStatusByChannelRequestFactory() {
    // Private constructor for utility class
  }

  public static GetMbusEncryptionKeyStatusByChannelRequest fromParameterMap(
      final Map<String, String> requestParameters) {
    final GetMbusEncryptionKeyStatusByChannelRequest request =
        new GetMbusEncryptionKeyStatusByChannelRequest();
    final GetMbusEncryptionKeyStatusByChannelRequestData requestData =
        new GetMbusEncryptionKeyStatusByChannelRequestData();
    request.setGatewayDeviceIdentification(
        requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));
    requestData.setChannel(
        Short.parseShort(requestParameters.get(PlatformSmartmeteringKeys.KEY_CHANNEL)));
    request.setGetMbusEncryptionKeyStatusByChannelRequestData(requestData);
    return request;
  }

  public static GetMbusEncryptionKeyStatusByChannelAsyncRequest fromScenarioContext() {
    final GetMbusEncryptionKeyStatusByChannelAsyncRequest asyncRequest =
        new GetMbusEncryptionKeyStatusByChannelAsyncRequest();
    asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    asyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return asyncRequest;
  }
}
