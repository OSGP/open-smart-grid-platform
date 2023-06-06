// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetAssociationLnObjectsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetAssociationLnObjectsRequest;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class AssociationLnObjectsRequestFactory {

  private AssociationLnObjectsRequestFactory() {
    // Private constructor for utility class
  }

  public static GetAssociationLnObjectsRequest fromParameterMap(
      final Map<String, String> requestParameters) {
    final GetAssociationLnObjectsRequest request = new GetAssociationLnObjectsRequest();
    request.setDeviceIdentification(requestParameters.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
    return request;
  }

  public static GetAssociationLnObjectsAsyncRequest fromScenarioContext() {
    final GetAssociationLnObjectsAsyncRequest asyncRequest =
        new GetAssociationLnObjectsAsyncRequest();
    asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    asyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return asyncRequest;
  }
}
