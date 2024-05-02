// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.GetThdFingerprintAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.GetThdFingerprintRequest;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class GetThdFingerprintRequestFactory {

  public static GetThdFingerprintRequest fromParameterMap(
      final Map<String, String> requestParameters) {

    final GetThdFingerprintRequest getThdFingerprintRequest = new GetThdFingerprintRequest();

    getThdFingerprintRequest.setDeviceIdentification(
        requestParameters.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));

    return getThdFingerprintRequest;
  }

  public static GetThdFingerprintAsyncRequest fromScenarioContext() {
    final GetThdFingerprintAsyncRequest getThdFingerprintAsyncRequest =
        new GetThdFingerprintAsyncRequest();
    getThdFingerprintAsyncRequest.setCorrelationUid(
        RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    getThdFingerprintAsyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return getThdFingerprintAsyncRequest;
  }
}
