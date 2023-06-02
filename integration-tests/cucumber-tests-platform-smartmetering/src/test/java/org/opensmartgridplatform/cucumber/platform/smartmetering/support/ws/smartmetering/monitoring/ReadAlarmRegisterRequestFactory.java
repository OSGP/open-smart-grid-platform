//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterRequest;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class ReadAlarmRegisterRequestFactory {

  private ReadAlarmRegisterRequestFactory() {
    // Private constructor for utility class
  }

  public static ReadAlarmRegisterRequest fromParameterMap(
      final Map<String, String> requestParameters) {
    final ReadAlarmRegisterRequest readAlarmRegisterRequest = new ReadAlarmRegisterRequest();
    readAlarmRegisterRequest.setDeviceIdentification(
        requestParameters.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
    return readAlarmRegisterRequest;
  }

  public static ReadAlarmRegisterAsyncRequest fromScenarioContext() {
    final ReadAlarmRegisterAsyncRequest readAlarmRegisterAsyncRequest =
        new ReadAlarmRegisterAsyncRequest();
    readAlarmRegisterAsyncRequest.setCorrelationUid(
        RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    readAlarmRegisterAsyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return readAlarmRegisterAsyncRequest;
  }
}
