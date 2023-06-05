// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.EnableDebuggingAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.EnableDebuggingRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class EnableDebuggingRequestFactory {
  private EnableDebuggingRequestFactory() {
    // Private constructor for utility class
  }

  public static EnableDebuggingRequest fromParameterMap(
      final Map<String, String> requestParameters) {
    final EnableDebuggingRequest enableDebuggingRequest = new EnableDebuggingRequest();
    enableDebuggingRequest.setDeviceIdentification(
        requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));
    return enableDebuggingRequest;
  }

  public static EnableDebuggingAsyncRequest fromScenarioContext() {
    final EnableDebuggingAsyncRequest enableDebuggingAsyncRequest =
        new EnableDebuggingAsyncRequest();
    enableDebuggingAsyncRequest.setCorrelationUid(
        RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    enableDebuggingAsyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return enableDebuggingAsyncRequest;
  }
}
