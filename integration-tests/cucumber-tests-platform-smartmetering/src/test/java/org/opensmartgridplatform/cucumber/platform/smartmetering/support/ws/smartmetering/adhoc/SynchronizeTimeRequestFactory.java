// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeRequestData;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public final class SynchronizeTimeRequestFactory {

  private static final String EUROPE_AMSTERDAM = "Europe/Amsterdam";

  private SynchronizeTimeRequestFactory() {
    // Private constructor for utility class
  }

  public static SynchronizeTimeRequest fromParameterMap(final Map<String, String> parameters) {
    final SynchronizeTimeRequest request = new SynchronizeTimeRequest();
    request.setDeviceIdentification(
        parameters.get(PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION));
    final SynchronizeTimeRequestData requestData = new SynchronizeTimeRequestData();
    requestData.setTimeZone(EUROPE_AMSTERDAM);
    request.setSynchronizeTimeRequestData(requestData);
    return request;
  }

  public static SynchronizeTimeAsyncRequest fromScenarioContext() {
    final SynchronizeTimeAsyncRequest asyncRequest = new SynchronizeTimeAsyncRequest();
    asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    asyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return asyncRequest;
  }
}
