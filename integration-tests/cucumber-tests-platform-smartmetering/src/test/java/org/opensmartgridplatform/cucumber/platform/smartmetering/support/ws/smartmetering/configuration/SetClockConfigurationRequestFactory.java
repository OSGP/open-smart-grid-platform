// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class SetClockConfigurationRequestFactory {

  public static SetClockConfigurationRequest fromParameterMap(
      final Map<String, String> parameters) {
    final SetClockConfigurationRequest setClockConfigurationRequest =
        new SetClockConfigurationRequest();

    setClockConfigurationRequest.setDeviceIdentification(
        getString(
            parameters,
            PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION,
            PlatformSmartmeteringDefaults.DEVICE_IDENTIFICATION));

    setClockConfigurationRequest.setSetClockConfigurationData(
        SetClockConfigurationRequestDataFactory.fromParameterMap(parameters));

    return setClockConfigurationRequest;
  }

  public static SetClockConfigurationAsyncRequest fromParameterMapAsync(
      final Map<String, String> requestParameters) {
    final String correlationUid = RequestFactoryHelper.getCorrelationUidFromScenarioContext();
    final String deviceIdentification =
        RequestFactoryHelper.getDeviceIdentificationFromStepData(requestParameters);

    final SetClockConfigurationAsyncRequest setClockConfigurationAsyncRequest =
        new SetClockConfigurationAsyncRequest();
    setClockConfigurationAsyncRequest.setCorrelationUid(correlationUid);
    setClockConfigurationAsyncRequest.setDeviceIdentification(deviceIdentification);
    return setClockConfigurationAsyncRequest;
  }
}
