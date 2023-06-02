//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.DisableDebuggingAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.DisableDebuggingRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class DisableDebuggingRequestFactory {
  private DisableDebuggingRequestFactory() {
    // Private constructor for utility class
  }

  public static DisableDebuggingRequest fromParameterMap(
      final Map<String, String> requestParameters) {
    final DisableDebuggingRequest disableDebuggingRequest = new DisableDebuggingRequest();
    disableDebuggingRequest.setDeviceIdentification(
        requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));
    return disableDebuggingRequest;
  }

  public static DisableDebuggingAsyncRequest fromScenarioContext() {
    final DisableDebuggingAsyncRequest disableDebuggingAsyncRequest =
        new DisableDebuggingAsyncRequest();
    disableDebuggingAsyncRequest.setCorrelationUid(
        RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    disableDebuggingAsyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return disableDebuggingAsyncRequest;
  }
}
