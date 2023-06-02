//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsRequest;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class ActualMeterReadsRequestFactory {

  private ActualMeterReadsRequestFactory() {
    // Private constructor for utility class
  }

  public static ActualMeterReadsRequest fromParameterMap(
      final Map<String, String> requestParameters) {
    final ActualMeterReadsRequest actualMeterReadsRequest = new ActualMeterReadsRequest();
    actualMeterReadsRequest.setDeviceIdentification(
        requestParameters.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
    return actualMeterReadsRequest;
  }

  public static ActualMeterReadsAsyncRequest fromScenarioContext() {
    final ActualMeterReadsAsyncRequest actualMeterReadsAsyncRequest =
        new ActualMeterReadsAsyncRequest();
    actualMeterReadsAsyncRequest.setCorrelationUid(
        RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    actualMeterReadsAsyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return actualMeterReadsAsyncRequest;
  }
}
