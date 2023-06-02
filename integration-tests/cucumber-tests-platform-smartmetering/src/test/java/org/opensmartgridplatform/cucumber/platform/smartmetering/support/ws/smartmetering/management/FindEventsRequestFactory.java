//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.FindEventsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.FindEventsRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class FindEventsRequestFactory {
  private FindEventsRequestFactory() {
    // Private constructor for utility class
  }

  public static FindEventsRequest fromParameterMap(final Map<String, String> requestParameters) {
    final FindEventsRequest findEventsRequest = new FindEventsRequest();
    findEventsRequest.setDeviceIdentification(
        requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));
    findEventsRequest
        .getFindEventsRequestData()
        .add(0, FindEventsRequestDataFactory.fromParameterMap(requestParameters));
    return findEventsRequest;
  }

  public static FindEventsAsyncRequest fromScenarioContext() {
    final FindEventsAsyncRequest findEventsAsyncRequest = new FindEventsAsyncRequest();
    findEventsAsyncRequest.setCorrelationUid(
        RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    findEventsAsyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return findEventsAsyncRequest;
  }
}
