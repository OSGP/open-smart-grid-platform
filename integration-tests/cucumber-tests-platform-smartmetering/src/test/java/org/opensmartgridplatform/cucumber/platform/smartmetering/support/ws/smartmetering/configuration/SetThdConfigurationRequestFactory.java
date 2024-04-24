// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getLong;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.THD_MIN_DURATION_NORMAL_TO_OVER;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.THD_MIN_DURATION_OVER_TO_NORMAL;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.THD_TIME_THRESHOLD;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.THD_VALUE_HYSTERESIS;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.THD_VALUE_THRESHOLD;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetThdConfigurationAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetThdConfigurationRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetThdConfigurationRequestData;
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

    final SetThdConfigurationRequestData requestData = new SetThdConfigurationRequestData();
    requestData.setMinDurationNormalToOver(getLongValue(settings, THD_MIN_DURATION_NORMAL_TO_OVER));
    requestData.setMinDurationOverToNormal(getLongValue(settings, THD_MIN_DURATION_OVER_TO_NORMAL));
    requestData.setTimeThreshold(getLongValue(settings, THD_TIME_THRESHOLD));
    requestData.setValueHysteresis(getIntValue(settings, THD_VALUE_HYSTERESIS));
    requestData.setValueThreshold(getIntValue(settings, THD_VALUE_THRESHOLD));

    return requestData;
  }

  private static long getLongValue(final Map<String, String> parameters, final String key) {
    return getLong(parameters, key);
  }

  private static int getIntValue(final Map<String, String> parameters, final String key) {
    return getInteger(parameters, key);
  }
}
