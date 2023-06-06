// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionGasAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionGasRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public final class GetFirmwareVersionGasRequestFactory {
  private GetFirmwareVersionGasRequestFactory() {
    // Private constructor for utility class
  }

  public static GetFirmwareVersionGasRequest fromParameterMap(
      final Map<String, String> requestParameters) {
    final GetFirmwareVersionGasRequest gasRequest = new GetFirmwareVersionGasRequest();
    gasRequest.setDeviceIdentification(
        requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));
    return gasRequest;
  }

  public static GetFirmwareVersionGasAsyncRequest fromScenarioContext() {
    final GetFirmwareVersionGasAsyncRequest gasAsyncRequest =
        new GetFirmwareVersionGasAsyncRequest();
    gasAsyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    gasAsyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return gasAsyncRequest;
  }
}
