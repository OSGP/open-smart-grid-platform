// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.THD_MIN_DURATION_NORMAL_TO_OVER;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.THD_MIN_DURATION_OVER_TO_NORMAL;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.THD_TIME_THRESHOLD;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.THD_VALUE_HYSTERESIS;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.THD_VALUE_THRESHOLD;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetThdConfigurationAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetThdConfigurationRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetThdConfigurationRequestData;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ThdConfiguration;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class SetThdConfigurationRequestFactory {

  private SetThdConfigurationRequestFactory() {
    // Private constructor for utility class
  }

  public static SetThdConfigurationRequest fromParameterMap(final Map<String, String> settings) {
    final SetThdConfigurationRequest request = new SetThdConfigurationRequest();
    request.setDeviceIdentification(
        settings.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));

    request.setSetThdConfigurationRequestData(createRequestData(settings));
    return request;
  }

  public static SetThdConfigurationAsyncRequest fromScenarioContext() {
    final SetThdConfigurationAsyncRequest asyncRequest = new SetThdConfigurationAsyncRequest();
    asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    asyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return asyncRequest;
  }

  private static SetThdConfigurationRequestData createRequestData(
      final Map<String, String> settings) {

    final ThdConfiguration thdConfiguration = new ThdConfiguration();

    thdConfiguration.setThdMinDurationNormalToOver(
        getLongValue(settings, THD_MIN_DURATION_NORMAL_TO_OVER));
    thdConfiguration.setThdMinDurationOverToNormal(
        getLongValue(settings, THD_MIN_DURATION_OVER_TO_NORMAL));
    thdConfiguration.setThdTimeThreshold(getLongValue(settings, THD_TIME_THRESHOLD));
    thdConfiguration.setThdValueHysteresis(getIntValue(settings, THD_VALUE_HYSTERESIS));
    thdConfiguration.setThdValueThreshold(getIntValue(settings, THD_VALUE_THRESHOLD));

    final SetThdConfigurationRequestData requestData = new SetThdConfigurationRequestData();
    requestData.setThdConfiguration(thdConfiguration);

    return requestData;
  }

  private static long getLongValue(final Map<String, String> parameters, final String key) {
    return getLongValue(parameters, key);
  }

  private static int getIntValue(final Map<String, String> parameters, final String key) {
    return getIntValue(parameters, key);
  }
}
