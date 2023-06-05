// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class SetConfigurationObjectRequestFactory {

  private SetConfigurationObjectRequestFactory() {
    // Private constructor for utility class
  }

  public static SetConfigurationObjectRequest fromParameterMap(
      final Map<String, String> requestParameters) {
    final SetConfigurationObjectRequest setConfigurationObjectRequest =
        new SetConfigurationObjectRequest();
    setConfigurationObjectRequest.setDeviceIdentification(
        requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));

    setConfigurationObjectRequest.setSetConfigurationObjectRequestData(
        SetConfigurationObjectRequestDataFactory.fromParameterMap(requestParameters));

    return setConfigurationObjectRequest;
  }

  public static SetConfigurationObjectAsyncRequest fromScenarioContext() {
    final SetConfigurationObjectAsyncRequest setConfigurationObjectAsyncRequest =
        new SetConfigurationObjectAsyncRequest();
    setConfigurationObjectAsyncRequest.setCorrelationUid(
        RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    setConfigurationObjectAsyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return setConfigurationObjectAsyncRequest;
  }
}
