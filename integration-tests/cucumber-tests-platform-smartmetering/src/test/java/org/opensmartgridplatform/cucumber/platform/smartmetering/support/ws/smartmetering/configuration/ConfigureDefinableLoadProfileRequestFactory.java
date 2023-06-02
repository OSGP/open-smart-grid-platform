//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigureDefinableLoadProfileAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigureDefinableLoadProfileRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class ConfigureDefinableLoadProfileRequestFactory {

  public static ConfigureDefinableLoadProfileRequest fromParameterMap(
      final Map<String, String> parameters) {
    final ConfigureDefinableLoadProfileRequest configureDefinableLoadProfileRequest =
        new ConfigureDefinableLoadProfileRequest();

    configureDefinableLoadProfileRequest.setDeviceIdentification(
        getString(
            parameters,
            PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION,
            PlatformSmartmeteringDefaults.DEVICE_IDENTIFICATION));

    configureDefinableLoadProfileRequest.setDefinableLoadProfileConfigurationData(
        DefinableLoadProfileConfigurationDataFactory.fromParameterMap(parameters));

    return configureDefinableLoadProfileRequest;
  }

  public static ConfigureDefinableLoadProfileAsyncRequest fromParameterMapAsync(
      final Map<String, String> requestParameters) {
    final String correlationUid = RequestFactoryHelper.getCorrelationUidFromScenarioContext();
    final String deviceIdentification =
        RequestFactoryHelper.getDeviceIdentificationFromStepData(requestParameters);

    final ConfigureDefinableLoadProfileAsyncRequest configureDefinableLoadProfileAsyncRequest =
        new ConfigureDefinableLoadProfileAsyncRequest();
    configureDefinableLoadProfileAsyncRequest.setCorrelationUid(correlationUid);
    configureDefinableLoadProfileAsyncRequest.setDeviceIdentification(deviceIdentification);
    return configureDefinableLoadProfileAsyncRequest;
  }
}
