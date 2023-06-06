// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
