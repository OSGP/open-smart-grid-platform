// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupAlarmAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupAlarmRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.SetPushSetupAlarmRequestBuilder;

public class SetPushSetupAlarmRequestFactory {

  private SetPushSetupAlarmRequestFactory() {
    // Private constructor for utility class
  }

  public static SetPushSetupAlarmRequest fromParameterMap(final Map<String, String> settings) {
    final SetPushSetupAlarmRequest request =
        new SetPushSetupAlarmRequestBuilder().fromParameterMap(settings).buildSingle();
    request.setDeviceIdentification(
        settings.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));
    return request;
  }

  public static SetPushSetupAlarmAsyncRequest fromScenarioContext() {
    final SetPushSetupAlarmAsyncRequest asyncRequest = new SetPushSetupAlarmAsyncRequest();
    asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    asyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return asyncRequest;
  }
}
