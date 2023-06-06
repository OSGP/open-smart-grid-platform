// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.GetGsmDiagnosticAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.GetGsmDiagnosticRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.GetGsmDiagnosticRequestData;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class GetGsmDiagnosticRequestFactory {

  private GetGsmDiagnosticRequestFactory() {
    // Private constructor for utility class
  }

  public static GetGsmDiagnosticRequest fromParameterMap(final Map<String, String> settings) {
    final GetGsmDiagnosticRequest request = new GetGsmDiagnosticRequest();
    final GetGsmDiagnosticRequestData requestData = new GetGsmDiagnosticRequestData();
    request.setDeviceIdentification(settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
    request.setGetGsmDiagnosticRequestData(requestData);
    return request;
  }

  public static GetGsmDiagnosticAsyncRequest fromScenarioContext() {
    final GetGsmDiagnosticAsyncRequest asyncRequest = new GetGsmDiagnosticAsyncRequest();
    asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    asyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return asyncRequest;
  }
}
