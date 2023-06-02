//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class GetMbusEncryptionKeyStatusRequestFactory {
  private GetMbusEncryptionKeyStatusRequestFactory() {
    // Private constructor for utility class
  }

  public static GetMbusEncryptionKeyStatusRequest fromParameterMap(
      final Map<String, String> requestParameters) {
    final GetMbusEncryptionKeyStatusRequest request = new GetMbusEncryptionKeyStatusRequest();
    request.setDeviceIdentification(
        requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));
    return request;
  }

  public static GetMbusEncryptionKeyStatusAsyncRequest fromScenarioContext() {
    final GetMbusEncryptionKeyStatusAsyncRequest asyncRequest =
        new GetMbusEncryptionKeyStatusAsyncRequest();
    asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    asyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return asyncRequest;
  }
}
