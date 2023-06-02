//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class FirmwareVersionRequestFactory {
  private FirmwareVersionRequestFactory() {
    // Private constructor for utility class
  }

  public static GetFirmwareVersionRequest fromParameterMap(
      final Map<String, String> requestParameters) {
    final GetFirmwareVersionRequest getFirmwareVersionRequest = new GetFirmwareVersionRequest();
    getFirmwareVersionRequest.setDeviceIdentification(
        requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));
    return getFirmwareVersionRequest;
  }

  public static GetFirmwareVersionAsyncRequest fromScenarioContext() {
    final GetFirmwareVersionAsyncRequest getFirmwareVersionAsyncRequest =
        new GetFirmwareVersionAsyncRequest();
    getFirmwareVersionAsyncRequest.setCorrelationUid(
        RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    getFirmwareVersionAsyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return getFirmwareVersionAsyncRequest;
  }
}
