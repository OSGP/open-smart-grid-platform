// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ClearAlarmRegisterAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ClearAlarmRegisterRequest;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class ClearAlarmRegisterRequestFactory {

  private ClearAlarmRegisterRequestFactory() {
    // Private constructor for utility class
  }

  public static ClearAlarmRegisterRequest fromParameterMap(
      final Map<String, String> requestParameters) {
    final ClearAlarmRegisterRequest clearAlarmRegisterRequest = new ClearAlarmRegisterRequest();
    clearAlarmRegisterRequest.setDeviceIdentification(
        requestParameters.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
    return clearAlarmRegisterRequest;
  }

  public static ClearAlarmRegisterAsyncRequest fromScenarioContext() {
    final ClearAlarmRegisterAsyncRequest clearAlarmRegisterAsyncRequest =
        new ClearAlarmRegisterAsyncRequest();
    clearAlarmRegisterAsyncRequest.setCorrelationUid(
        RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    clearAlarmRegisterAsyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return clearAlarmRegisterAsyncRequest;
  }
}
