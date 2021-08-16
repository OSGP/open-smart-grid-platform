/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.GetSystemEventAsyncRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class SystemEventRequestFactory {
  public static GetSystemEventAsyncRequest fromScenarioContext() {
    final GetSystemEventAsyncRequest getSystemEventAsyncRequest = new GetSystemEventAsyncRequest();
    getSystemEventAsyncRequest.setCorrelationUid(
        RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    getSystemEventAsyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return getSystemEventAsyncRequest;
  }
}
