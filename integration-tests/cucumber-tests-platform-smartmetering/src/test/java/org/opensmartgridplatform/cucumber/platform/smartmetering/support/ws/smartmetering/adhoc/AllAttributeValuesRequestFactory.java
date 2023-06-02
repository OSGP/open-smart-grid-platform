//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetAllAttributeValuesAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetAllAttributeValuesRequest;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class AllAttributeValuesRequestFactory {

  private AllAttributeValuesRequestFactory() {
    // Private constructor for utility class
  }

  public static GetAllAttributeValuesRequest fromParameterMap(
      final Map<String, String> requestParameters) {
    final GetAllAttributeValuesRequest request = new GetAllAttributeValuesRequest();
    request.setDeviceIdentification(requestParameters.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
    return request;
  }

  public static GetAllAttributeValuesAsyncRequest fromScenarioContext() {
    final GetAllAttributeValuesAsyncRequest asyncRequest = new GetAllAttributeValuesAsyncRequest();
    asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    asyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return asyncRequest;
  }
}
