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
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SecretType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterRequestData;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class SetEncryptionKeyExchangeOnGMeterRequestFactory {
  private SetEncryptionKeyExchangeOnGMeterRequestFactory() {
    // Private constructor for utility class
  }

  public static SetEncryptionKeyExchangeOnGMeterRequest fromParameterMap(
      final Map<String, String> requestParameters) {
    final SetEncryptionKeyExchangeOnGMeterRequest setEncryptionKeyExchangeOnGMeterRequest =
        new SetEncryptionKeyExchangeOnGMeterRequest();
    setEncryptionKeyExchangeOnGMeterRequest.setDeviceIdentification(
        requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));

    final SetEncryptionKeyExchangeOnGMeterRequestData data =
        new SetEncryptionKeyExchangeOnGMeterRequestData();

    data.setMbusDeviceIdentification(
        requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));
    data.setSecretType(
        SecretType.valueOf(requestParameters.get(PlatformSmartmeteringKeys.SECRET_TYPE)));
    data.setCloseOpticalPort(
        Boolean.valueOf(
            requestParameters.getOrDefault(PlatformSmartmeteringKeys.CLOSE_OPTICAL_PORT, "false")));

    setEncryptionKeyExchangeOnGMeterRequest.setSetEncryptionKeyExchangeOnGMeterRequestData(data);

    return setEncryptionKeyExchangeOnGMeterRequest;
  }

  public static SetEncryptionKeyExchangeOnGMeterAsyncRequest fromScenarioContext() {
    final SetEncryptionKeyExchangeOnGMeterAsyncRequest
        setEncryptionKeyExchangeOnGMeterAsyncRequest =
            new SetEncryptionKeyExchangeOnGMeterAsyncRequest();
    setEncryptionKeyExchangeOnGMeterAsyncRequest.setCorrelationUid(
        RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    setEncryptionKeyExchangeOnGMeterAsyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return setEncryptionKeyExchangeOnGMeterAsyncRequest;
  }
}
