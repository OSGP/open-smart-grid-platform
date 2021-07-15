/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
